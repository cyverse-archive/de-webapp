package org.iplantc.de.client.sysmsgs.view;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This represents the user dismissing a system message.
 */
public final class DismissMessageEvent extends GwtEvent<DismissMessageEvent.Handler> {
	
    /**
     * Any class that can handle this event needs to extend this interface.
     */
    public interface Handler extends EventHandler {
        /**
         * This method is called when the event is fired.
         * 
         * @param event the event
         */
        void handleDismiss(DismissMessageEvent event);
	}

    /**
     * the type object of this event
     */
    public static final Type<Handler> TYPE = new Type<Handler>();
	
    private final String messageId;
	
    /**
     * the constructor
     * 
     * @param messageId the id of the message being dismissed
     */
    public DismissMessageEvent(final String messageId) {
        this.messageId = messageId;
	}
	
    /**
     * @see GwtEvent<T>#getAssociatedType()
     */
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

    /**
     * Retrieves the message being dismissed
     * 
     * @return the id of the message being dismissed
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @see GwtEvent<T>#dispatch(T)
     */
    @Override
	protected void dispatch(final Handler handler) {
		handler.handleDismiss(this);
	}
	
}
