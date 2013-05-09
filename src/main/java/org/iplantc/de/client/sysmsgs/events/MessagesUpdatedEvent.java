package org.iplantc.de.client.sysmsgs.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * TODO document
 */
public final class MessagesUpdatedEvent extends GwtEvent<MessagesUpdatedEvent.Handler> {

	public interface Handler extends EventHandler {
		
		void onUpdate(MessagesUpdatedEvent event);
		
	}

	public static final Type<Handler> TYPE = new Type<Handler>();
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.onUpdate(this);
	}
	
}
