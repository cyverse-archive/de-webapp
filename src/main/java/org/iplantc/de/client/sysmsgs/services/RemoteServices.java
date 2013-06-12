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
public final class RemoteServices implements Services {
	
    private static final class MsgListCB extends AsyncCallbackConverter<String, MessageList> {
        public MsgListCB(final AsyncCallback<MessageList> callback) {
            super(callback);
        }

        @Override
        protected MessageList convertFrom(final String json) {
            return AutoBeanCodex.decode(MessageFactory.INSTANCE, MessageList.class, json).as();
        }
    }
	
    /**
     * @see Services#getAllMessages(AsyncCallback)
     */
    @Override
    public final void getAllMessages(final AsyncCallback<MessageList> callback) {
        getMessages("/messages", callback); //$NON-NLS-1$
    }	

    /**
     * @see Services#getNewMessages(AsyncCallback)
     */
    @Override
    public final void getNewMessages(final AsyncCallback<MessageList> callback) {
        getMessages("/new-messages", callback); //$NON-NLS-1$
    }

    /**
     * @see Services#getUnseenMessages(AsyncCallback)
     */
    @Override
    public final void getUnseenMessages(final AsyncCallback<MessageList> callback) {
        getMessages("/unseen-messages", callback); //$NON-NLS-1$
    }

    /**
     * @see Services#markAllReceived(AsyncCallback)
     */
    @Override
    public void markAllReceived(final AsyncCallback<Void> callback) {
        final String address = makeAddress("/mark-all-received");  //$NON-NLS-1$
        final AutoBean<User> user = MessageFactory.INSTANCE.makeUser();
        user.as().setUser(UserInfo.getInstance().getUsername());
        final String payload = AutoBeanCodex.encode(user).getPayload();
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, payload);
        final AsyncCallback<String> voidedCB = new StringToVoidCallbackConverter(callback);
        DEServiceFacade.getInstance().getServiceData(wrapper, voidedCB);
    }

    /**
     * @see Services#markReceived(IdList, AsyncCallback)
     */
    @Override
    public void markReceived(final IdList msgIds, final AsyncCallback<Void> callback) {
        final String address = makeAddress("/received");  //$NON-NLS-1$
        final Splittable split = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(msgIds));
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, split.getPayload());
        final AsyncCallback<String> voidedCB = new StringToVoidCallbackConverter(callback);
        DEServiceFacade.getInstance().getServiceData(wrapper, voidedCB);
    }

    /**
     * @see Services#acknowledgeMessages(IdList, AsyncCallback)
     */
    @Override
    public void acknowledgeMessages(final IdList msgIds, final AsyncCallback<Void> callback) {
        final String address = makeAddress("/seen"); //$NON-NLS-1$
        final Splittable split = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(msgIds));
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, split.getPayload());
        final AsyncCallback<String> voidedCB = new StringToVoidCallbackConverter(callback);
        DEServiceFacade.getInstance().getServiceData(wrapper, voidedCB);
    }

    /**
     * @see Services#hideMessages(IdList, AsyncCallback)
     */
    @Override
    public void hideMessages(final IdList msgIds, final AsyncCallback<Void> callback) {
        final String address = makeAddress("/delete");  //$NON-NLS-1$
        final Splittable split = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(msgIds));
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, split.getPayload());
        final AsyncCallback<String> voidedCB = new StringToVoidCallbackConverter(callback);
        DEServiceFacade.getInstance().getServiceData(wrapper, voidedCB);
    }

    private void getMessages(final String relSvcPath, final AsyncCallback<MessageList> callback) {
        final String address = makeAddress(relSvcPath);
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, address);
        DEServiceFacade.getInstance().getServiceData(wrapper, new MsgListCB(callback));
    }

    private String makeAddress(final String relPath) {
        final String base = DEProperties.getInstance().getMuleServiceBaseUrl();
        return base + "notifications/system" + relPath;  //$NON-NLS-1$
    }

}