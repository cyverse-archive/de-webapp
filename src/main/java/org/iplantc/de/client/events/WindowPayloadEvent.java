package org.iplantc.de.client.events;

import org.iplantc.de.client.events.WindowPayloadEvent.WindowPayloadEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.json.client.JSONObject;

/**
 * Window payload event.
 * 
 * @author amuir
 * 
 */
public class WindowPayloadEvent extends MessagePayloadEvent<WindowPayloadEventHandler> {
    /**
     * Called when an window payload event has fired.
     * 
     * @author amuir
     * 
     */
    public interface WindowPayloadEventHandler extends EventHandler {
        /**
         * Called when a window event is fired.
         * 
         * @param event fired event.
         */
        void onFire(WindowPayloadEvent event);
    }

    /**
     * Defines the GWT Event Type.
     */
    public static final GwtEvent.Type<WindowPayloadEventHandler> TYPE = new GwtEvent.Type<WindowPayloadEventHandler>();

    public WindowPayloadEvent(JSONObject message, JSONObject payload) {
        super(message, payload);
    }

    @Override
    protected void dispatch(WindowPayloadEventHandler handler) {
        handler.onFire(this);
    }

    @Override
    public GwtEvent.Type<WindowPayloadEventHandler> getAssociatedType() {
        return TYPE;
    }
}
