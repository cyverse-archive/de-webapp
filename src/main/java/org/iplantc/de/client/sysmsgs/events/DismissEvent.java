package org.iplantc.de.client.sysmsgs.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This represents the user dismissing something.
 * 
 * @param <T> type of thing being dismissed
 */
public final class DismissEvent<T> extends GwtEvent<DismissEvent.Handler<T>> {
	
    /**
     * Any class that can handle this event needs to extend this interface.
     */
    public interface Handler<T> extends EventHandler {
        /**
         * This method is called when the event is fired.
         * 
         * @param event the event
         */
        void handleDismiss(DismissEvent<T> event);
	}

    /**
     * the type object of this event
     */
    public static final Type<Handler<?>> TYPE = new Type<Handler<?>>();
	
    private final T dismissed;
	
    /**
     * the constructor
     * 
     * @param dismissed the thing being dismissed
     */
    public DismissEvent(final T dismissed) {
        this.dismissed = dismissed;
	}
	
    /**
     * @see GwtEvent<T>#getAssociatedType()
     */
	@Override
	public Type<Handler<T>> getAssociatedType() {
        return new Type<Handler<T>>();
	}

    /**
     * Retrieves the thing being dismissed
     * 
     * @return the thing being dismissed
     */
    public T getDismissed() {
        return dismissed;
    }

    /**
     * @see GwtEvent<T>#dispatch(T)
     */
    @Override
    protected void dispatch(final Handler<T> handler) {
		handler.handleDismiss(this);
	}
	
}
