package org.iplantc.de.client.sysmsgs.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.periodic.MessagePoller;
import org.iplantc.de.client.sysmsgs.events.NewSystemMessagesEvent;
import org.iplantc.de.client.sysmsgs.model.IdListDTO;
import org.iplantc.de.client.sysmsgs.model.MessageDTO;
import org.iplantc.de.client.sysmsgs.model.MessageFactory;
import org.iplantc.de.client.sysmsgs.model.MessageListDTO;
import org.iplantc.de.client.sysmsgs.services.ServiceFacade;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.shared.loader.DataProxy;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;


/**
 * TODO document
 */
public final class SystemMessageCache 
		implements DataProxy<ListLoadConfig, ListLoadResult<MessageDTO>> {
	
	private static SystemMessageCache instance = null;
	
	public static SystemMessageCache instance() {
		if (instance == null) {
			instance = new SystemMessageCache();
		}
		return instance;
 	}

	private final ServiceFacade services = new ServiceFacade();
	private final HashMap<String, MessageDTO> messages = new HashMap<String, MessageDTO>();
	private final ArrayList<Callback<ListLoadResult<MessageDTO>, Throwable>> loadCallbacks 
			= new ArrayList<Callback<ListLoadResult<MessageDTO>, Throwable>>();
	
	private boolean amPolling = false;
	private boolean syncedOnce = false;
	
	private SystemMessageCache() {
	}
	
	@Override
	public void load(final ListLoadConfig unused, 
			final Callback<ListLoadResult<MessageDTO>, Throwable> callback) {
		if (syncedOnce) {
			callback.onSuccess(makeLoadResult());
		} else {
			loadCallbacks.add(callback);
		}
	}

	public void startSyncing() {
		if (!amPolling) {
			requestAllMessages();
			MessagePoller.getInstance().addTask(new Runnable() {
				@Override
				public void run() {
					sync();
				}});
			amPolling = true;
		}
	}
	
	public long countUnseen() {
		long count = 0;
		for (MessageDTO msg: messages.values()) {
			if (!msg.isSeen()) {
				count++;
			}
		}
		return count;
	}
	
	public void acknowledgeAllMessages(final Callback<Void, Throwable> callback) {
		services.acknowledgeAllMessages(new AsyncCallback<Void>() {
			@Override
			public void onFailure(final Throwable caught) {
				callback.onFailure(caught);
			}
			@Override
			public void onSuccess(final Void unused) {
				markMessagesAsAcknowledged();
				callback.onSuccess(null);
			}});
	}
	
	public void hideMessage(final String msgId, final Callback<Void, Throwable> callback) {
		if (messages.containsKey(msgId)) {
			final MessageDTO msg = messages.get(msgId);
			if (msg.isDismissable()) {
				hideHideableMessage(msg, callback);
			} else {
				// TODO Should an exception be returned when a message cannot be hidden?
				callback.onSuccess(null);
			}
		}  else {
			// TODO should an exception be returned when a message doesn't exist?
			callback.onSuccess(null);			
		}
	}

	private void hideHideableMessage(final MessageDTO msg, 
			final Callback<Void, Throwable> callback) {
		final IdListDTO idsDTO = MessageFactory.INSTANCE.makeIdList().as();
		idsDTO.setUUIDs(Arrays.asList(msg.getId()));
		services.hideMessages(idsDTO, new AsyncCallback<Void>() {
			@Override
			public void onFailure(final Throwable caught) {
				callback.onFailure(caught);
			}
			@Override
			public void onSuccess(final Void unused) {
				messages.remove(msg.getId());
				callback.onSuccess(null);
			}});
	}
	
	private void markMessagesAsAcknowledged() {
		for (MessageDTO msg : messages.values()) {
			msg.setSeen(true);
		}
	}

	private void sync() {
		if (syncedOnce) {
			requestUnseenMessages();
		} else {
			requestAllMessages();
		}		
	}
	
	private void requestAllMessages() {
		services.getAllMessages(new AsyncCallback<MessageListDTO>() {
			@Override
			public void onFailure(final Throwable exn) {
				notifyLoadersOfFailure(exn);
			}
			@Override
			public void onSuccess(final MessageListDTO messages) {
				addMessagesAndNotifyLoaders(messages);
			}});
	}

	private void requestUnseenMessages() {
		services.getUnseenMessages(new AsyncCallback<MessageListDTO>() {
			@Override
			public void onFailure(final Throwable unused) {
			}
			@Override
			public void onSuccess(final MessageListDTO messages) {
				addMessages(messages);
			}});
	}

	private void addMessagesAndNotifyLoaders(final MessageListDTO newMessages) {
		addMessages(newMessages);
		syncedOnce = true;
		final ArrayList<MessageDTO> msgLst = new ArrayList<MessageDTO>(messages.values());
		for (Callback<ListLoadResult<MessageDTO>, Throwable> callback : loadCallbacks) {
			callback.onSuccess(new ListLoadResult<MessageDTO>() {
					private static final long serialVersionUID = 8785407136143469894L;
					@Override
					public List<MessageDTO> getData() {
						return msgLst;
					}});
		}
		loadCallbacks.clear();
	}
	
	private void notifyLoadersOfFailure(final Throwable exn) {
		for (Callback<ListLoadResult<MessageDTO>, Throwable> callback : loadCallbacks) {
			callback.onFailure(exn);
		}
		loadCallbacks.clear();
	}
	
	private void addMessages(final MessageListDTO msgsDTO) {
		final long initNumUnseen = countUnseen();
		for (MessageDTO msg: msgsDTO.getList()) {
			if (!isMessageStateDated(msg)) {
				messages.put(msg.getId(), msg);
			}
		}
		if (initNumUnseen < countUnseen()) {
			EventBus.getInstance().fireEvent(new NewSystemMessagesEvent());
		}
	}
	
	private boolean isMessageStateDated(final MessageDTO msg) {
		final MessageDTO oldMsg = messages.get(msg.getId());
		return oldMsg != null && oldMsg.isSeen() && !msg.isSeen();
	}
	
	private ListLoadResult<MessageDTO> makeLoadResult() {
		final ArrayList<MessageDTO> msgLst = new ArrayList<MessageDTO>(messages.values());
		return new ListLoadResult<MessageDTO>() {
				private static final long serialVersionUID = 8785407136143469894L;
				@Override
				public List<MessageDTO> getData() {
					return msgLst;
				}};
	}
	
}
