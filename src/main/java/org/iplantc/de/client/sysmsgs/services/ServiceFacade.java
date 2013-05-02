package org.iplantc.de.client.sysmsgs.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

import org.iplantc.core.uicommons.client.DEServiceFacade;
import org.iplantc.core.uicommons.client.models.DEProperties;
import org.iplantc.core.uicommons.client.models.UserInfo;
import org.iplantc.de.client.sysmsgs.model.IdList;
import org.iplantc.de.client.sysmsgs.model.MessageFactory;
import org.iplantc.de.client.sysmsgs.model.MessageList;
import org.iplantc.de.client.sysmsgs.model.User;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.ServiceCallWrapper;

/**
 * Provides access to remote services to acquire system messages.
 */
public class ServiceFacade {
	
	private final CallbackConverter callbackConverter;
	private final String baseURL;
	
	public ServiceFacade() {
		callbackConverter  = new CallbackConverter(MessageFactory.INSTANCE);
		baseURL = DEProperties.getInstance().getMuleServiceBaseUrl() + "notifications/system";  //$NON-NLS-1$
	}
	
    /**
     * Retrieves all of the active system messages for a given user.
     *
     * @param callback called on RPC completion.
     */
    public final void getAllMessages(final AsyncCallback<MessageList> callback) {
        getMessagesFrom("/messages", callback);  //$NON-NLS-1$
    }	

    /**
     * Retrieves all of the unseen, active system messages for a given user.
     *
     * @param callback called on RPC completion.
     */
    public final void getUnseenMessages(final AsyncCallback<MessageList> callback) {
        getMessagesFrom("/unseen-messages", callback);  //$NON-NLS-1$
    }

    /**
     * Marks all of the user's system messages as seen.
     * 
     * @param user the user name
     * @param callback called on RPC completion.
     */
    public void acknowledgeAllMessages(final AsyncCallback<Void> callback) {
        final String address = baseURL + "/mark-all-seen"; //$NON-NLS-1$
        final AutoBean<User> bean = MessageFactory.INSTANCE.makeUser();
        bean.as().setUser(UserInfo.getInstance().getUsername());
        final Splittable split = AutoBeanCodex.encode(bean);
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, 
        		split.getPayload());
        final AsyncCallback<String> voidedCallback = callbackConverter.voidResponse(callback);        
        DEServiceFacade.getInstance().getServiceData(wrapper, voidedCallback);
    }

    /**
     * Hides a list of active system messages from a user
     * 
     * @param msgIds the Ids of the messages to hide
     * @param callback called on RPC completion.
     */
    public void hideMessages(final IdList msgIds, final AsyncCallback<Void> callback) {
        final String address = baseURL + "/delete";  //$NON-NLS-1$
        final Splittable split = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(msgIds));
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, 
        		split.getPayload());
        final AsyncCallback<String> voidedCallback = callbackConverter.voidResponse(callback);        
        DEServiceFacade.getInstance().getServiceData(wrapper, voidedCallback);
    }
    
    private void getMessagesFrom(final String systemEndPoint, 
    		final AsyncCallback<MessageList> callback) {
        final String address = baseURL + systemEndPoint;
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, address);
        final AsyncCallback<String> convertedCallback = callbackConverter.convert(callback, 
        		MessageList.class);
        DEServiceFacade.getInstance().getServiceData(wrapper, convertedCallback);
    }
    
}
