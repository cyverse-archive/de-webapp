package org.iplantc.de.client.sysmsgs.services;

import org.iplantc.de.client.sysmsgs.model.IdList;
import org.iplantc.de.client.sysmsgs.model.MessageList;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This is the service layer interface for system messages.
 */
public interface Services {

    /**
     * Retrieves all of the active system messages for a given user.
     *
     * @param callback called on RPC completion.
     */
    public abstract void getAllMessages(AsyncCallback<MessageList> callback);

    /**
     * Retrieves the new active system messages for a given user.
     * 
     * @param callback called on RPC completion.
     */
    public abstract void getNewMessages(AsyncCallback<MessageList> callback);

    /**
     * Marks a list of system messages as received by the user
     * 
     * @param msgIds the Ids of the messages to be marked
     * @param callback called on RPC completion
     */
    public abstract void markReceived(IdList msgIds, AsyncCallback<Void> callback);

    /**
     * Marks s list of system messages as seen by the user.
     * 
     * @param msgIds the Ids of the messages to be marked
     * @param callback called on RPC completion.
     */
    public abstract void acknowledgeMessages(IdList msgIds, AsyncCallback<Void> callback);

    /**
     * Hides a list of active system messages from a user
     * 
     * @param msgIds the Ids of the messages to hide
     * @param callback called on RPC completion.
     */
    public abstract void hideMessages(IdList msgIds, AsyncCallback<Void> callback);

}