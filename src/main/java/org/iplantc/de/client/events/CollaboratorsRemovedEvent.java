package org.iplantc.de.client.events;

import java.util.List;

import org.iplantc.de.client.models.Collaborator;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An event fired when the user removes users from their collaborators.
 * 
 * @author psarando
 * 
 */
public class CollaboratorsRemovedEvent extends GwtEvent<CollaboratorsRemovedEventHandler> {

    /**
     * Defines the GWT Event Type.
     * 
     * @see org.iplantc.de.client.events.CollaboratorsRemovedEventHandler
     */
    public static final GwtEvent.Type<CollaboratorsRemovedEventHandler> TYPE = new GwtEvent.Type<CollaboratorsRemovedEventHandler>();

    private final List<Collaborator> models;

    public CollaboratorsRemovedEvent(List<Collaborator> models) {
        this.models = models;
    }

    @Override
    public GwtEvent.Type<CollaboratorsRemovedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CollaboratorsRemovedEventHandler handler) {
        handler.onRemove(this);
    }

    public List<Collaborator> getCollaborators() {
        return models;
    }
}
