package org.iplantc.de.client.desktop.widget;

import java.util.LinkedList;
import java.util.Queue;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.HandlerRegistration;

import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.Popup;
import com.sencha.gxt.widget.core.client.button.IconButton.IconConfig;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer.CssFloatData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import org.iplantc.core.resources.client.AnnouncerStyle;
import org.iplantc.core.resources.client.IplantResources;

/**
 * Objects of this class display a message in the top center of the view port. The message may be a 
 * widget, allowing a user to interact with the message to obtain more information. By default, the 
 * message is closable by the user and will close automatically after 10 seconds.
 * 
 * Only one message can be displayed at time. If a second message is scheduled, it will be shown 
 * once the first one times out.
 */
public final class Announcer {
	
	private static final AnnouncerStyle STYLE;
	private static final IconConfig CLOSER_CFG;
	private static final int DEFAULT_TIMEOUT_ms;
	private static final Queue<Announcer> announcers;

	static {
		STYLE = IplantResources.RESOURCES.getAnnouncerStyle();
		STYLE.ensureInjected();
		CLOSER_CFG = new IconConfig(STYLE.closeButton(), STYLE.closeButtonOver());
		DEFAULT_TIMEOUT_ms = 10000;
		announcers = new LinkedList<Announcer>();
	}
	
	private static void removeAnnouncer(final Announcer oldAnnouncer) {
		if (announcers.contains(oldAnnouncer)) {
			announcers.remove(oldAnnouncer);
		}
		showNextAnnouncer();
	}

	private static void scheduleAnnouncer(final Announcer newAnnouncer) {
		if (announcers.contains(newAnnouncer)) {
			return;
		}
		announcers.add(newAnnouncer);
		showNextAnnouncer();
	}
	
	private static void showNextAnnouncer() {
		if (announcers.isEmpty()) {
			return;
		}
		announcers.peek().show();
		if (announcers.size() > 1) {
			announcers.peek().indicateMore();
		} else {
			announcers.peek().indicateNoMore();
		}
	}

	private final class CloseHandler implements SelectHandler {
		@Override
		public void onSelect(final SelectEvent event) {
			remove();
		}
	}
	
	private final class CloseTimer extends Timer {
		@Override
		public void run() {
			remove();
		};
	}

	private final Popup panel;
	private final Timer timer;
	private final int timeout_ms;
	
	private boolean showing;
	private HandlerRegistration positionerRegistration;
	
	/**
	 * Constructs a user closable announcer that will close automatically after 10 seconds.
	 * 
	 * @param content the message widget
	 */
	public Announcer(final IsWidget content) {
		this(content, true, DEFAULT_TIMEOUT_ms);
	}
	
	/**
	 * Constructs an announcer that will close automatically after 10 seconds.
	 * 
	 * @param content the message widget
	 * @param closable a flag indicating whether or not the message is user closable.
	 */
	public Announcer(final IsWidget content, final boolean closable) {
		this(content, closable, DEFAULT_TIMEOUT_ms);
	}
	
	/**
	 * Constructs an announcer. Setting a timeout of 0 or less will cause the message to not close 
	 * automatically. 
	 * 
	 * If the closable flag is set to false, the message must close automatically. In this case, if
	 * the provided timeout is 0 or less, the message will close automatically after 10 seconds.
	 * 
	 * @param content the message widget
	 * @param closable a flag indicating whether or not the message is closable.
	 * @param timeout_ms the amount of time in milliseconds to wait before automatically closing the
	 * message.
	 */
	public Announcer(final IsWidget content, final boolean closable, final int timeout_ms) {
		this.panel = new Popup();
		this.timer = new CloseTimer();
		this.timeout_ms = (!closable && timeout_ms <= 0) ? DEFAULT_TIMEOUT_ms : timeout_ms;
		this.showing = false;
		this.positionerRegistration = null;
		initPanel(content, closable);
	}
	
	private void initPanel(final IsWidget content, final boolean closable) {
		final SimpleContainer contentContainer = new SimpleContainer();
		contentContainer.setWidget(content);
		contentContainer.addStyleName(STYLE.content());

		final CssFloatLayoutContainer layout = new CssFloatLayoutContainer();
		layout.add(contentContainer, new CssFloatData(-1));

		if (closable) {
			final ToolButton closeButton = new ToolButton(CLOSER_CFG, new CloseHandler());
			layout.add(closeButton, new CssFloatData(-1));
			closeButton.getElement().getStyle().setFloat(Float.RIGHT);
		}

		panel.setWidget(layout);
		panel.setAutoHide(false);
		panel.addStyleName(STYLE.panel());
		panel.setShadow(true);
	}

	/**
	 * closes the message
	 */
	public void remove() {
		showing = false;
		unregisterPositioner();
		timer.cancel();
		panel.hide();
		removeAnnouncer(this);
	}
	
	/**
	 * Schedules a message to be shown.
	 */
	public void schedule() {
		scheduleAnnouncer(this);
	}

	private void indicateMore() {
		panel.addStyleName(STYLE.panelMultiple());
	}

	private void indicateNoMore() {
		panel.removeStyleName(STYLE.panelMultiple());
	}

	private void show() {
		if (!showing) {
			panel.show();
			positionPanel();
			registerPositioner();
			if (timeout_ms > 0) {
				timer.schedule(timeout_ms);
			}
			showing = true;
		}
	}

	private void registerPositioner() {
		unregisterPositioner();
		positionerRegistration = Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				positionPanel();
			}});
	}
	
	private void unregisterPositioner() {
		if (positionerRegistration != null) {
			positionerRegistration.removeHandler();
			positionerRegistration = null;
		}
	}
	
	private void positionPanel() {
		final XElement panElmt = panel.getElement();
		final int panelWid = panElmt.getMargins(Side.LEFT, Side.RIGHT) + panElmt.getOffsetWidth();
		panel.getElement().setX((Window.getClientWidth() - panelWid)/2);
		panel.getElement().setY(0);		
	}
	
}
