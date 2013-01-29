package org.iplantc.de.client.views.widgets;

import org.iplantc.de.client.models.Collaborator;
import org.iplantc.de.client.views.widgets.UserSearchResultSelected.UserSearchResultSelectedEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class UserSearchResultSelected extends GwtEvent<UserSearchResultSelectedEventHandler> {

    interface UserSearchResultSelectedEventHandler extends EventHandler {

        void onUserSearchResultSelected(UserSearchResultSelected userSearchResultSelected);
    }

    private static final GwtEvent.Type<UserSearchResultSelectedEventHandler> TYPE = new GwtEvent.Type<UserSearchResultSelected.UserSearchResultSelectedEventHandler>();
    private final Collaborator collaborator;

    public UserSearchResultSelected(String tag, Collaborator collaborator) {
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

}
