package org.iplantc.de.client.sysmsgs.view;

import java.util.Date;

import org.iplantc.de.client.I18N;
import org.iplantc.de.client.sysmsgs.model.Message;
import org.iplantc.de.client.sysmsgs.view.DefaultMessagesViewResources.Style;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.sencha.gxt.cell.core.client.AbstractEventCell;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.event.XEvent;

/**
 * This is the cell used to render message summaries.
 */
public final class MessageSummaryCell extends AbstractEventCell<Message> {
	
	interface Templates extends XTemplates {
        @XTemplate(source = "MessageSummary.html")
        SafeHtml make(Message msg, String activationTimeMsg, Style style);
	}
	
	private static final Style CSS;
    private static final Templates FACTORY;

    static {
        FACTORY = GWT.create(Templates.class);
        CSS = GWT.<DefaultMessagesViewResources> create(DefaultMessagesViewResources.class).style();
    	CSS.ensureInjected();
    }

    private static boolean withinPreviousWeek(final Date successor, final Date predecessor) {
        if (predecessor.after(successor)) {
            return false;
        }
        return CalendarUtil.getDaysBetween(predecessor, successor) < 7;
    }

    /**
     * the constructor
     */
    public MessageSummaryCell() {
        super(BrowserEvents.CLICK);
	}

    /**
     * @see AbstractEventCell<T>#onBrowserEvent(Context, Element, T,
     *      NativeEvent, ValueUpdater)
     */
	@Override
    public void onBrowserEvent(final Context context, final Element parent, final Message message, final NativeEvent nativeEvent, final ValueUpdater<Message> updater) {
		final XEvent event = nativeEvent.<XEvent>cast();
        if (event.getTypeInt() == Event.ONCLICK) {
            if (event.getEventTargetEl().hasClassName(CSS.dismiss())) {
                fireEvent(new DismissMessageEvent(message.getId()));
            }
        }
    }
	
    /**
     * @see AbstractEventCell<T>#render(com.google.gwt.cell.client.Cell.Context, T, SafeHtmlBuilder)
     */
	@Override
	public void render(final Context context, final Message message, 
			final SafeHtmlBuilder builder) {
	    final Date actTime = message.getActivationTime();
        final Date now = new Date();
        String actMsg = "";
        if (CalendarUtil.isSameDate(now, actTime)) {
            actMsg = I18N.DISPLAY.today();
        } else if (withinPreviousWeek(now, actTime)) {
            actMsg = DateTimeFormat.getFormat("cccc").format(actTime);
        } else {
            actMsg = DateTimeFormat.getFormat("dd MMMM yyyy").format(actTime);
        }
        builder.append(FACTORY.make(message, actMsg, CSS));
    }

}
