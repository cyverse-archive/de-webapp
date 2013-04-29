package org.iplantc.de.client.sysmsgs.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * TODO document
 */
public final class NewSystemMessagesEvent extends GwtEvent<NewSystemMessagesEvent.Handler> {

	public interface Handler extends EventHandler {
		
		void onNewMessage(NewSystemMessagesEvent event);
		
	}

	public static final Type<Handler> TYPE = new Type<Handler>();
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.onNewMessage(this);
	}
	
}
