package org.iplantc.de.client.sysmsgs.events;

import org.iplantc.de.client.sysmsgs.model.Message;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class DismissMessageEvent extends GwtEvent<DismissMessageEvent.Handler> {
	
	public interface Handler extends EventHandler {
		
		void handleDismiss(DismissMessageEvent event);
		
	}

	public static final Type<Handler> TYPE = new Type<Handler>();
	
	private final Message message;
	
	public DismissMessageEvent(final Message message) {
		this.message = message;
	}
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	public Message getMessage() {
		return message;
	}
	
	@Override
	protected void dispatch(final Handler handler) {
		handler.handleDismiss(this);
	}
	
}
