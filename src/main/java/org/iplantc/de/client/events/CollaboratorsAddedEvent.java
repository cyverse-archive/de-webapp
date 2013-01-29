package org.iplantc.de.client.events;

import java.util.List;

import org.iplantc.de.client.models.Collaborator;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An event fired when the user adds user to their collaborators.
 * 
 * @author psarando
 * 
 */
public class CollaboratorsAddedEvent extends GwtEvent<CollaboratorsAddedEventHandler> {

    /**
     * Defines the GWT Event Type.
     * 
     * @see org.iplantc.de.client.events.CollaboratorsAddedEventHandler
     */
    public static final GwtEvent.Type<CollaboratorsAddedEventHandler> TYPE = new GwtEvent.Type<CollaboratorsAddedEventHandler>();

    private final List<Collaborator> models;

    public CollaboratorsAddedEvent(List<Collaborator> models) {
        this.models = models;
    }

    @Override
    public GwtEvent.Type<CollaboratorsAddedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CollaboratorsAddedEventHandler handler) {
        handler.onAdd(this);
    }

    public List<Collaborator> getCollaborators() {
        return models;
    }
}
