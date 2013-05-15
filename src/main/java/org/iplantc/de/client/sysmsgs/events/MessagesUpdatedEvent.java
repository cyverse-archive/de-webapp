package org.iplantc.de.client.sysmsgs.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This event indicates that the system message records have been updated.
 */
public final class MessagesUpdatedEvent extends GwtEvent<MessagesUpdatedEvent.Handler> {

	/**
	 * Classes that implement this interface are able to receive MessagesUpdateEvent objects.
	 */
	public interface Handler extends EventHandler {

		/**
		 * This method is called when an MessagesUpdatedEvent is dispatched.
		 * 
		 * @param event The event being dispatched.
		 */
		void onUpdate(MessagesUpdatedEvent event);
		
	}

	/**
	 * The type object associated with MessagesUpdatedEvents objects.
	 */
	public static final Type<Handler> TYPE = new Type<Handler>();
	
	private final boolean newMessages;
	
	/**
	 * the constructor
	 * 
	 * @param newMessages This parameter indicates whether or not there are any new unseen messages.
	 */
	public MessagesUpdatedEvent(final boolean newMessages) {
		this.newMessages = newMessages;
	}
	
	
	/**
	 * @see GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * Indicates whether or not the update includes new messages;
	 * 
	 * @return true if there are new messages, otherwise false
	 */
	public boolean areNewMessages() {
		return newMessages;
	}
	
	/**
	 * @see GwtEvent#dispatch(Handler)
	 */
	@Override
	protected void dispatch(final Handler handler) {
		handler.onUpdate(this);
	}
	
}
