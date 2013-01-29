package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * An EventHandler for CollaboratorsRemovedEvents.
 * 
 * @author psarando
 * 
 */
public interface CollaboratorsRemovedEventHandler extends EventHandler {

    public void onRemove(CollaboratorsRemovedEvent event);
}
