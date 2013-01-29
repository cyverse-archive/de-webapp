package org.iplantc.de.client.views.widgets;

import org.iplantc.de.client.models.Collaborator;
import org.iplantc.de.client.views.widgets.UserSearchResultSelected.UserSearchResultSelectedEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class UserSearchResultSelected extends GwtEvent<UserSearchResultSelectedEventHandler> {

    public interface UserSearchResultSelectedEventHandler extends EventHandler {

        void onUserSearchResultSelected(UserSearchResultSelected userSearchResultSelected);
    }

    public static final GwtEvent.Type<UserSearchResultSelectedEventHandler> TYPE = new GwtEvent.Type<UserSearchResultSelected.UserSearchResultSelectedEventHandler>();
    private final Collaborator collaborator;

    public UserSearchResultSelected(Collaborator collaborator) {
        this.collaborator = collaborator;
    }

    @Override
    public GwtEvent.Type<UserSearchResultSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(UserSearchResultSelectedEventHandler handler) {
        handler.onUserSearchResultSelected(this);
    }

    public Collaborator getCollaborator() {
        return collaborator;
    }

}
