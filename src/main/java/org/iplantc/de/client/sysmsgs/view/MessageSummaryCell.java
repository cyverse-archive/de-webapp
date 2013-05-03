package org.iplantc.de.client.sysmsgs.view;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.client.Event;

import com.sencha.gxt.cell.core.client.AbstractEventCell;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.event.XEvent;

import org.iplantc.de.client.sysmsgs.model.Message;
import org.iplantc.de.client.sysmsgs.view.Resources.MessageCellStyle;


/**
 * TODO document
 */
final class MessageSummaryCell extends AbstractEventCell<Message> {
	
	interface Templates extends XTemplates {
		@XTemplate("<span style='white-space:nowrap;'>"
				 + "  <div>{msg.type}</div>"
				 + "  <div>{msg.creationTime:date(\"dd MMMM yyyy\")}</div>"
			     + "</span>")
		SafeHtml makeSummary(Message msg);
		
		@XTemplate("<span>"
				 + "  <span class='{dismissClass}'></span>"
				 + "  {summaryHTML}"
				 + "</span>")
		SafeHtml makeDismissable(SafeHtml summaryHTML, String dismissClass);
	}
	
	private static final class Renderer implements SafeHtmlRenderer<Message> {
		private static final Templates FACTORY = GWT.create(Templates.class);
			
		@Override
		public SafeHtml render(final Message msg) {
			final SafeHtmlBuilder builder = new SafeHtmlBuilder();
			render(msg, builder);
			return builder.toSafeHtml();
		}

		@Override
		public void render(final Message msg, final SafeHtmlBuilder builder) {
			builder.append(msg.isDismissible() 
					? FACTORY.makeDismissable(FACTORY.makeSummary(msg), CSS.dismiss())
					: FACTORY.makeSummary(msg));
		}
	}

	private static final MessageCellStyle CSS;

    static {
    	CSS = Resources.INSTANCE.messageCellCSS();
    	CSS.ensureInjected();
    }
 
    private final Renderer renderer = new Renderer();
    
	MessageSummaryCell() {
		super(BrowserEvents.CLICK, BrowserEvents.MOUSEOVER, BrowserEvents.MOUSEOUT);
	}

	@Override
	public void onBrowserEvent(final Context context, final Element parent, final Message message, 
			final NativeEvent nativeEvent, final ValueUpdater<Message> updater) {
		if (message == null) {
			return;
		}
		
		final XEvent event = nativeEvent.<XEvent>cast();
		final XElement target = event.getEventTargetEl();
		if (parent.isOrHasChild(target)) {
			switch (Event.as(event).getTypeInt()) {
			case Event.ONCLICK:
				onClick(target, message);
				break;
			case Event.ONMOUSEOUT:
				onMouseOut(target);
				break;
			case Event.ONMOUSEOVER:
				onMouseOver(target);
				break;
			default:
				break;
			}
		}
	}
	
	@Override
	public void render(final Context context, final Message message, 
			final SafeHtmlBuilder builder) {
		renderer.render(message, builder);
	}	

	private void onClick(final XElement target, final Message message) {
		if (isDismisser(target)) {
			fireEvent(new DismissMessageEvent(message));
		} 
	}

	private void onMouseOut(final XElement target) {
		if (isDismisser(target)) {
			target.removeClassName(CSS.dismissOnHover());
		}
	}

	private void onMouseOver(final XElement target) {
		if (isDismisser(target)) {
			target.addClassName(CSS.dismissOnHover());
		}
	}
	
	private boolean isDismisser(final XElement element) {
		return element.hasClassName(CSS.dismiss());
	}
	
}