package org.iplantc.de.client.sysmsgs.view;

import org.iplantc.de.client.sysmsgs.model.Message;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

final class DismissMessageEvent extends GwtEvent<DismissMessageEvent.Handler> {
	
	interface Handler extends EventHandler {
		
		void handleDismiss(DismissMessageEvent event);
		
	}

	static final Type<Handler> TYPE = new Type<Handler>();
	
	private final Message message;
	
	DismissMessageEvent(final Message message) {
		this.message = message;
	}
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.handleDismiss(this);
	}
	
	Message getMessage() {
		return message;
	}
	
}
