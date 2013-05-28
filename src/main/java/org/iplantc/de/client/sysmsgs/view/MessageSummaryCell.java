package org.iplantc.de.client.sysmsgs.view;

import org.iplantc.de.client.sysmsgs.model.Message;
import org.iplantc.de.client.sysmsgs.view.DefaultMessagesViewResources.Style;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.cell.core.client.AbstractEventCell;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.event.XEvent;


/**
 * This is the cell used to render message summaries.
 */
final class MessageSummaryCell extends AbstractEventCell<Message> {
	
    /**
     * The XTemplates interface for rendering the HTML.
     */
	interface Templates extends XTemplates {

        /**
         * Creates the HTML.
         * 
         * @param msg the message displayed
         * @param style the style used
         * @return the HTML
         */
        @XTemplate(source = "MessageSummary.html")
        SafeHtml make(Message msg, Style style);
	}
	
	private static final Style CSS;
    private static final Templates FACTORY;

    static {
        FACTORY = GWT.create(Templates.class);
        CSS = GWT.<DefaultMessagesViewResources> create(DefaultMessagesViewResources.class).style();
    	CSS.ensureInjected();
    }
 
    /**
     * the constructor
     */
    MessageSummaryCell() {
        super(BrowserEvents.CLICK);
	}

    /**
     * @see AbstractEventCell<T>#onBrowserEvent(com.google.gwt.cell.client.Cell.Context, Element, T,
     *      NativeEvent, ValueUpdater)
     */
	@Override
	public void onBrowserEvent(final Context context, final Element parent, final Message message, 
			final NativeEvent nativeEvent, final ValueUpdater<Message> updater) {
		final XEvent event = nativeEvent.<XEvent>cast();
        if (event.getTypeInt() == Event.ONCLICK) {
            if (event.getEventTargetEl().hasClassName(CSS.dismiss())) {
                fireEvent(new DismissMessageEvent(message));
            }
        }
    }
	
    /**
     * @see AbstractEventCell<T>#render(com.google.gwt.cell.client.Cell.Context, T, SafeHtmlBuilder)
     */
	@Override
	public void render(final Context context, final Message message, 
			final SafeHtmlBuilder builder) {
        builder.append(FACTORY.make(message, CSS));
	}	
	
}
