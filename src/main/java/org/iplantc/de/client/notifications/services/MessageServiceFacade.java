package org.iplantc.de.client.notifications.services;

import org.iplantc.core.uicommons.client.DEServiceFacade;
import org.iplantc.core.uicommons.client.models.DEProperties;
import org.iplantc.core.uicommons.client.models.UserInfo;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Provides access to remote services to acquire messages and notifications.
 *
 * @author amuir
 *
 */
public class MessageServiceFacade {
    /**
     * Get notifications from the server.
     *
     * @param maxNotifications the maximum number of notifications to retrieve.
     * @param callback called on RPC completion.
     */
    public <C extends NotificationCallback> void getNotifications(int limit, int offset, String filter, String sortDir, C callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl(); //$NON-NLS-1$

        StringBuilder builder = new StringBuilder("notifications/messages?limit=" + limit + "&offset="
                + offset);
        if (filter != null && !filter.isEmpty()) {
            builder.append("&filter=" + URL.encodeQueryString(filter));
        }

        if (sortDir != null && !sortDir.isEmpty() && !sortDir.equalsIgnoreCase("NONE")) {
            builder.append("&sortDir=" + URL.encodeQueryString(sortDir));
        }

        address = address + builder.toString();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /**
     * Get messages from the server.
     *
     * @param callback called on RPC completion.
     */
    public void getMessages(AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl()
                + "notifications/unseen-messages"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    public void markAsSeen(final JSONObject seenIds, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "notifications/seen"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                seenIds.toString());

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /**
     * Delete messages from the server.
     *
     * @param arrDeleteIds array of notification ids to delete from the server.
     * @param callback called on RPC completion.
     */
    public void deleteMessages(final JSONObject deleteIds, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "notifications/delete"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                deleteIds.toString());

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /**
     * Get messages from the server.
     *
     * @param callback called on RPC completion.
     */
    public <C extends NotificationCallback> void getRecentMessages(C callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl()
                + "notifications/last-ten-messages"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);

    }

    public void getUnSeenMessageCount(AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl()
                + "notifications/count-messages?seen=false"; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    public void deleteAll(AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "notifications/delete-all"; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE, address);

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    public void acknowledgeAll(AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl()
                + "notifications/mark-all-seen"; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                UserInfo.getInstance().getUsername());

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /**
     * Retrieves all of the active system messages for a given user.
     *
     * @param callback called on RPC completion.
     */
    public final void getAllSystemMessages(final AsyncCallback<String> callback) {
        final String address = DEProperties.getInstance().getMuleServiceBaseUrl() 
        		+ "system/messages"; //$NON-NLS-1$
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, 
        		address);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /**
     * Retrieves all of the unseen, active system messages for a given user.
     *
     * @param callback called on RPC completion.
     */
    public final void getUnseenSystemMessages(final AsyncCallback<String> callback) {
        final String address = DEProperties.getInstance().getMuleServiceBaseUrl() 
        		+ "system/unseen-messages"; //$NON-NLS-1$
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, 
        		address);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /**
     * Marks all of the user's system messages as seen.
     * 
     * @param user the user name
     * @param callback called on RPC completion.
     */
    public void acknowledgeAllSystemMessages(final JSONObject user, 
    		final AsyncCallback<String> callback) {
        final String address = DEProperties.getInstance().getMuleServiceBaseUrl() 
        		+ "system/mark-all-seen"; //$NON-NLS-1$
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, 
        		address, user.toString());
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /**
     * Hides a list of active system messages from a user
     * 
     * @param msgIds
     * @param callback called on RPC completion.
     */
    public void hideSystemMessages(final JSONObject msgIds, final AsyncCallback<String> callback) {
        final String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "system/delete"; //$NON-NLS-1$
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, 
        		address, msgIds.toString());
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }
    
}
