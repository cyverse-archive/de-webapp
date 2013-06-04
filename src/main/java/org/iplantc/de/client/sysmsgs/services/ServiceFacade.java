package org.iplantc.de.client.sysmsgs.services;

import org.iplantc.core.uicommons.client.DEServiceFacade;
import org.iplantc.core.uicommons.client.models.DEProperties;
import org.iplantc.core.uicommons.client.models.UserInfo;
import org.iplantc.core.uicommons.client.services.AsyncCallbackConverter;
import org.iplantc.core.uicommons.client.services.StringToVoidCallbackConverter;
import org.iplantc.de.client.sysmsgs.model.IdList;
import org.iplantc.de.client.sysmsgs.model.MessageFactory;
import org.iplantc.de.client.sysmsgs.model.MessageList;
import org.iplantc.de.client.sysmsgs.model.User;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

/**
 * Provides access to remote services to acquire system messages.
 */
public final class ServiceFacade {
	
    private static final class MsgListCB extends AsyncCallbackConverter<String, MessageList> {
        public MsgListCB(final AsyncCallback<MessageList> callback) {
            super(callback);
        }

        @Override
        protected MessageList convertFrom(final String json) {
            return AutoBeanCodex.decode(MessageFactory.INSTANCE, MessageList.class, json).as();
        }
    }

	private final String baseURL;
	
	public ServiceFacade() {
		baseURL = DEProperties.getInstance().getMuleServiceBaseUrl() + "notifications/system";  //$NON-NLS-1$
	}
	
    /**
     * Retrieves all of the active system messages for a given user.
     *
     * @param callback called on RPC completion.
     */
    public final void getAllMessages(final AsyncCallback<MessageList> callback) {
        final String address = baseURL + "/messages";
		final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, address);
        DEServiceFacade.getInstance().getServiceData(wrapper, new MsgListCB(callback));
    }	

    /**
     * Retrieves the new active system messages for a given user.
     * 
     * @param callback called on RPC completion.
     */
    public final void getNewMessages(final AsyncCallback<MessageList> callback) {
        final String address = baseURL + "/new-messages";
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, address);
        DEServiceFacade.getInstance().getServiceData(wrapper, new MsgListCB(callback));
    }

    /**
     * Marks a list of system messages as received by the user
     * 
     * @param msgIds the Ids of the messages to be marked
     * @param callback called on RPC completion
     */
    public void markReceived(final IdList msgIds, final AsyncCallback<Void> callback) {
        final String address = baseURL + "/received";  //$NON-NLS-1$
        final Splittable split = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(msgIds));
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, split.getPayload());
        final AsyncCallback<String> voidedCB = new StringToVoidCallbackConverter(callback);
        DEServiceFacade.getInstance().getServiceData(wrapper, voidedCB);
    }

    /**
     * Marks s list of system messages as seen by the user.
     * 
     * @param msgIds the Ids of the messages to be marked
     * @param callback called on RPC completion.
     */
    public void acknowledgeMessages(final IdList msgIds, final AsyncCallback<Void> callback) {
        final String address = baseURL + "/seen"; //$NON-NLS-1$
        final Splittable split = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(msgIds));
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, split.getPayload());
        final AsyncCallback<String> voidedCB = new StringToVoidCallbackConverter(callback);
        DEServiceFacade.getInstance().getServiceData(wrapper, voidedCB);
    }

    /**
     * Marks all of the user's system messages as seen.
     * 
     * @param callback called on RPC completion.
     */
    public void acknowledgeAllMessages(final AsyncCallback<Void> callback) {
        final String address = baseURL + "/mark-all-seen"; //$NON-NLS-1$
        final AutoBean<User> userDTO = MessageFactory.INSTANCE.makeUser();
        userDTO.as().setUser(UserInfo.getInstance().getUsername());
        final Splittable split = AutoBeanCodex.encode(userDTO);
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, 
        		split.getPayload());
        final AsyncCallback<String> voidedCB = new StringToVoidCallbackConverter(callback);
        DEServiceFacade.getInstance().getServiceData(wrapper, voidedCB);
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
        final AsyncCallback<String> voidedCB = new StringToVoidCallbackConverter(callback);
        DEServiceFacade.getInstance().getServiceData(wrapper, voidedCB);
    }
    
}
