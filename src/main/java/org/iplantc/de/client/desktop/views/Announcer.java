package org.iplantc.de.client.desktop.views;

import com.extjs.gxt.ui.client.core.XDOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * Objects of this class display a message in the top center of the view port. The message may be a
 * widget, allowing a user to interact with the message to obtain more information. By default, the
 * message is closable by the user and will close automatically after 10 seconds. 
 * 
 * Only one message can be displayed at time. If a second message is displayed, the first one will
 * be replaced.
 * 
 * FIXME Due to time pressure, Announcer itself guarantees that there is only one Announcer
 * displayed at a time. This is not very MVP design.
 * 
 * TODO handle styling
 */
final class Announcer {

	private static final int DEFAULT_TIMEOUT_ms = 10000;
	private static final int TOP_INSET = 16;

	private static Announcer currentAnnouncer = null;
	
	private static void setCurrentAnnouncer(final Announcer current) {
		if (currentAnnouncer != null) {
			currentAnnouncer.hide();
		}
		
		currentAnnouncer = current;		
	}
	
	private static int computeWidgetHeight(final Widget widget) {
		final XElement elmt = XElement.as(widget.getElement());
		return elmt.getOffsetHeight() + elmt.getMargins(Side.TOP, Side.BOTTOM);
	}
	
	private static int computeWidgetWidth(final Widget widget) {
		final XElement elmt = XElement.as(widget.getElement());
		return elmt.getOffsetWidth() + elmt.getMargins(Side.LEFT, Side.RIGHT);
	}
	
	private final class CloseHandler implements SelectHandler {
		
		@Override
		public void onSelect(final SelectEvent event) {
			hide();
		}
		
	}
	
	private final class CloseTimer extends Timer {

		@Override
		public void run() {
			hide();
		};
		
	}
	
	private final BorderLayoutContainer panel;
	private final Widget content;
	private final BorderLayoutData contentLayout;
	private final ToolButton closeButton;
	private final BorderLayoutData closeButtonLayout;
	private final boolean closable;
	private final Timer timer;
	private final int timeout_ms;
	
	/**
	 * Constructs a user closable announcer that will close automatically after 10 seconds.
	 * 
	 * @param content the message widget
	 */
	Announcer(final IsWidget content) {
		this(content, true, DEFAULT_TIMEOUT_ms);
	}
	
	/**
	 * Constructs an announcer that will close automatically after 10 seconds.
	 * 
	 * @param content the message widget
	 * @param closable a flag indicating whether or not the message is user closable.
	 */
	Announcer(final IsWidget content, final boolean closable) {
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
	Announcer(final IsWidget content, final boolean closable, final int timeout_ms) {
		this.panel = new BorderLayoutContainer();
		this.content = content.asWidget();
		this.contentLayout = new BorderLayoutData(-1);
		this.closeButton = new ToolButton(ToolButton.CLOSE, new CloseHandler());
		this.closeButtonLayout = new BorderLayoutData(-1);
		this.closable = closable;
		this.timer = new CloseTimer();
		this.timeout_ms = (!closable && timeout_ms <= 0) ? DEFAULT_TIMEOUT_ms : timeout_ms;	

		panel.setWestWidget(content, contentLayout);
		
		if (closable) {
			panel.setEastWidget(closeButton, closeButtonLayout);
		}
	}
	
	/**
	 * closes the message
	 */
	void hide() {
		timer.cancel();
		panel.hide();
	}
	
	/**
	 * shows the message
	 */
	void show() {
		setCurrentAnnouncer(this);
		final XElement elmt = panel.getElement();
		elmt.updateZIndex(XDOM.getTopZIndex());
		RootPanel.get().add(panel);
		panel.setSize(computeWidth(), computeHeight());
		contentLayout.setSize(computeWidgetWidth(content));
		closeButtonLayout.setMinSize(closeButton.getOffsetWidth());
		closeButtonLayout.setMaxSize(closeButton.getOffsetWidth());
		closeButtonLayout.setSize(computeWidgetWidth(closeButton));
		elmt.center();
		elmt.setY(TOP_INSET);
		
		if (timeout_ms > 0) {
			timer.schedule(timeout_ms);
		}
	}
	
	private String computeHeight() {
		final int contentHeight = computeWidgetHeight(content);
		final int innerHeight = closable
				? Math.max(contentHeight, computeWidgetHeight(closeButton)) 
				: contentHeight;
		return Integer.toString(innerHeight);
	}

	private String computeWidth() {
		final int contentWidth = computeWidgetWidth(content);
		final int innerWidth = closable
				? contentWidth + computeWidgetWidth(closeButton)
				: contentWidth;
		return Integer.toString(innerWidth);
	}
}
