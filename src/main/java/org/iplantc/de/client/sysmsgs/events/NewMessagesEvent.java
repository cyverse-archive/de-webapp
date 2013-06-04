package org.iplantc.de.client.sysmsgs.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This event indicates that new messages have been received.
 */
public final class NewMessagesEvent extends GwtEvent<NewMessagesEvent.Handler> {

	/**
	 * Classes that implement this interface are able to receive MessagesUpdateEvent objects.
	 */
	public interface Handler extends EventHandler {

		/**
		 * This method is called when an MessagesUpdatedEvent is dispatched.
		 * 
		 * @param event The event being dispatched.
		 */
		void onUpdate(NewMessagesEvent event);
		
	}

	/**
	 * The type object associated with MessagesUpdatedEvents objects.
	 */
	public static final Type<Handler> TYPE = new Type<Handler>();
	
	/**
	 * @see GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	/**
	 * @see GwtEvent#dispatch(Handler)
	 */
	@Override
	protected void dispatch(final Handler handler) {
		handler.onUpdate(this);
	}
	
}
