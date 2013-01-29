package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * EventHandler for CollaboratorsAddedEvents.
 * 
 * @author psarando
 * 
 */
public interface CollaboratorsAddedEventHandler extends EventHandler {

    public void onAdd(CollaboratorsAddedEvent event);
}
