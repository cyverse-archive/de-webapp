package org.iplantc.de.client.sysmsgs.services;

import org.iplantc.core.uicommons.client.DEServiceFacade;
import org.iplantc.core.uicommons.client.models.DEProperties;
import org.iplantc.core.uicommons.client.services.AsyncCallbackConverter;
import org.iplantc.core.uicommons.client.services.StringToVoidCallbackConverter;
import org.iplantc.de.client.sysmsgs.model.IdList;
import org.iplantc.de.client.sysmsgs.model.MessageFactory;
import org.iplantc.de.client.sysmsgs.model.MessageList;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
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
    private final CommandSequencer callSequencer;
	
	public ServiceFacade() {
		baseURL = DEProperties.getInstance().getMuleServiceBaseUrl() + "notifications/system";  //$NON-NLS-1$
        callSequencer = new CommandSequencer();
	}
	
    /**
     * Retrieves all of the active system messages for a given user.
     *
     * @param callback called on RPC completion.
     */
    public final void getAllMessages(final AsyncCallback<MessageList> callback) {
        callSequencer.schedule(new ChainableCommand<MessageList>(callback) {
            @Override
            protected void execute(final AsyncCallback<MessageList> wrappedCB) {
                final String address = baseURL + "/messages";
                final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, address);
                DEServiceFacade.getInstance().getServiceData(wrapper, new MsgListCB(wrappedCB));
            }
        });
    }	

    /**
     * Retrieves the new active system messages for a given user.
     * 
     * @param callback called on RPC completion.
     */
    public final void getNewMessages(final AsyncCallback<MessageList> callback) {
        callSequencer.schedule(new ChainableCommand<MessageList>(callback) {
            @Override
            protected void execute(final AsyncCallback<MessageList> wrappedCB) {
                final String address = baseURL + "/new-messages";
                final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, address);
                DEServiceFacade.getInstance().getServiceData(wrapper, new MsgListCB(wrappedCB));
            }
        });
    }

    /**
     * Marks a list of system messages as received by the user
     * 
     * @param msgIds the Ids of the messages to be marked
     * @param callback called on RPC completion
     */
    public void markReceived(final IdList msgIds, final AsyncCallback<Void> callback) {
        callSequencer.schedule(new ChainableCommand<Void>(callback) {
            @Override
            protected void execute(final AsyncCallback<Void> wrappedCB) {
                final String address = baseURL + "/received";  //$NON-NLS-1$
                final Splittable split = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(msgIds));
                final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, split.getPayload());
                final AsyncCallback<String> voidedCB = new StringToVoidCallbackConverter(wrappedCB);
                DEServiceFacade.getInstance().getServiceData(wrapper, voidedCB);
            }
        });
    }

    /**
     * Marks s list of system messages as seen by the user.
     * 
     * @param msgIds the Ids of the messages to be marked
     * @param callback called on RPC completion.
     */
    public void acknowledgeMessages(final IdList msgIds, final AsyncCallback<Void> callback) {
        callSequencer.schedule(new ChainableCommand<Void>(callback) {
            @Override
            protected void execute(final AsyncCallback<Void> wrappedCB) {
                final String address = baseURL + "/seen"; //$NON-NLS-1$
                final Splittable split = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(msgIds));
                final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, split.getPayload());
                final AsyncCallback<String> voidedCB = new StringToVoidCallbackConverter(wrappedCB);
                DEServiceFacade.getInstance().getServiceData(wrapper, voidedCB);
            }
        });
    }

    /**
     * Hides a list of active system messages from a user
     * 
     * @param msgIds the Ids of the messages to hide
     * @param callback called on RPC completion.
     */
    public void hideMessages(final IdList msgIds, final AsyncCallback<Void> callback) {
        callSequencer.schedule(new ChainableCommand<Void>(callback) {
            @Override
            protected void execute(final AsyncCallback<Void> wrappedCB) {
                final String address = baseURL + "/delete";  //$NON-NLS-1$
                final Splittable split = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(msgIds));
                final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, split.getPayload());
                final AsyncCallback<String> voidedCB = new StringToVoidCallbackConverter(wrappedCB);
                DEServiceFacade.getInstance().getServiceData(wrapper, voidedCB);
            }
        });
    }
    
}
