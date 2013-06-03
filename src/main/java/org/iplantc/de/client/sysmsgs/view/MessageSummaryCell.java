package org.iplantc.de.client.sysmsgs.view;

import java.util.Date;

import org.iplantc.de.client.sysmsgs.events.DismissEvent;
import org.iplantc.de.client.sysmsgs.view.DefaultMessagesViewResources.Style;
import org.iplantc.de.client.sysmsgs.view.MessagesView.MessageProperties;
import org.iplantc.de.client.sysmsgs.view.MessagesView.Presenter;

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
 * 
 * @param <M> the type of message
 */
public final class MessageSummaryCell<M> extends AbstractEventCell<M> {
	
	interface Templates extends XTemplates {
        @XTemplate(source = "MessageSummary.html")
        SafeHtml make(String type, boolean seen, boolean dismissible, String activationTimeMsg, Style style);
	}
	
	private static final Style CSS;
    private static final Templates FACTORY;

    static {
        FACTORY = GWT.create(Templates.class);
        CSS = GWT.<DefaultMessagesViewResources> create(DefaultMessagesViewResources.class).style();
    	CSS.ensureInjected();
    }

    private final Presenter<M> presenter;
    private final MessageProperties<M> messageProperties;

    /**
     * the constructor
     * 
     * @param presenter the presenter of the parent view
     * @param messageProperties the properties provider for a message
     */
    public MessageSummaryCell(final Presenter<M> presenter, final MessageProperties<M> messageProperties) {
        super(BrowserEvents.CLICK);
        this.presenter = presenter;
        this.messageProperties = messageProperties;
	}

    /**
     * @see AbstractEventCell<T>#onBrowserEvent(Context, Element, T, NativeEvent, ValueUpdater)
     */
    @Override
    public void onBrowserEvent(final Context context, final Element parent, final M message, final NativeEvent nativeEvent, final ValueUpdater<M> updater) {
		final XEvent event = nativeEvent.<XEvent>cast();
        if (event.getTypeInt() == Event.ONCLICK) {
            if (event.getEventTargetEl().hasClassName(CSS.dismiss())) {
                fireEvent(new DismissEvent<M>(message));
            }
        }
    }
	
    /**
     * @see AbstractEventCell<T>#render(com.google.gwt.cell.client.Cell.Context, T, SafeHtmlBuilder)
     */
	@Override
    public void render(final Context context, final M message, final SafeHtmlBuilder builder) {
        final String type = messageProperties.type().getValue(message);
        final boolean seen = messageProperties.seen().getValue(message);
        final boolean dissmissible = messageProperties.dismissible().getValue(message);
        final Date actTime = messageProperties.activationTime().getValue(message);
        final String actMsg = presenter.formatActivationTime(actTime);
        builder.append(FACTORY.make(type, seen, dissmissible, actMsg, CSS));
    }

}
