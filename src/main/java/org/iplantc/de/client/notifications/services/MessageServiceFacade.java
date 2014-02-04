package org.iplantc.de.client.notifications.services;

import org.iplantc.de.commons.client.DEServiceFacade;
import org.iplantc.de.commons.client.models.DEProperties;
import org.iplantc.de.commons.client.models.UserInfo;
import org.iplantc.de.commons.client.services.AsyncCallbackConverter;
import org.iplantc.de.client.notifications.models.Counts;
import org.iplantc.de.client.notifications.models.NotificationAutoBeanFactory;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * Provides access to remote services to acquire messages and notifications.
 *
 * @author amuir
 *
 */
public class MessageServiceFacade {

    private static final class CountsCB extends AsyncCallbackConverter<String, Counts> {
        public CountsCB(final AsyncCallback<Counts> callback) {
            super(callback);
        }

        @Override
        protected Counts convertFrom(final String json) {
            return AutoBeanCodex.decode(notesFactory, Counts.class, json).as();
        }
    }

    private static final NotificationAutoBeanFactory notesFactory = GWT.create(NotificationAutoBeanFactory.class);

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
    public void getRecentMessages(AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl()
                + "notifications/last-ten-messages"; //$NON-NLS-1$
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

    /**
     * Retrieves the message counts from the server where the seen parameter is false.
     * 
     * @param callback called on RPC completion
     */
    public void getMessageCounts(final AsyncCallback<Counts> callback) {
        final String addr = DEProperties.getInstance().getMuleServiceBaseUrl()
                + "notifications/count-messages?seen=false"; //$NON-NLS-1$
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, addr);
        final AsyncCallback<String> convCB = new CountsCB(callback);
        DEServiceFacade.getInstance().getServiceData(wrapper, convCB);
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
    
}
