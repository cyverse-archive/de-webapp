package org.iplantc.de.client.viewer.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import org.iplantc.de.client.viewer.events.SaveFileEvent.SaveFileEventHandler;

public class SaveFileEvent extends GwtEvent<SaveFileEventHandler> {

    public interface SaveFileEventHandler extends EventHandler {
        /**
         * Method to call when save button is pressed
         * 
         * @param event
         */
        void onSave(SaveFileEvent event);

    }

    public static final Type<SaveFileEventHandler> TYPE = new Type<SaveFileEventHandler>();

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<SaveFileEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SaveFileEventHandler handler) {
        handler.onSave(this);
    }

}
