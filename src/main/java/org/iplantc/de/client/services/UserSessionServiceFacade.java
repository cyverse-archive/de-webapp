package org.iplantc.de.client.services;

import org.iplantc.de.commons.client.DEServiceFacade;
import org.iplantc.de.commons.client.models.DEProperties;
import org.iplantc.de.commons.client.models.UserSession;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

/**
 * A service facade to save and retrieve user session
 * 
 * @author sriram
 * 
 */
public class UserSessionServiceFacade {

    public void getUserSession(AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "sessions"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    public void saveUserSession(UserSession userSession, AsyncCallback<String> callback){
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "sessions"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(userSession)).getPayload());
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    public void clearUserSession(AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "sessions"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE, address);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    public void getUserPreferences(AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "preferences"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    public void saveUserPreferences(JSONObject json, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "preferences"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                json.toString());
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    public void postClientNotification(JSONObject notification, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getUnproctedMuleServiceBaseUrl()
                + "send-notification";

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                notification.toString());

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);

    }

    public void getSearchHistory(AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "search-history";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    public void saveSearchHistory(JSONObject body, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "search-history";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                body.toString());
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);

    }

}
