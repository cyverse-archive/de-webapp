package org.iplantc.de.client.sysmsgs.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.periodic.MessagePoller;
import org.iplantc.de.client.sysmsgs.events.NewMessagesEvent;
import org.iplantc.de.client.sysmsgs.model.IdList;
import org.iplantc.de.client.sysmsgs.model.Message;
import org.iplantc.de.client.sysmsgs.model.MessageFactory;
import org.iplantc.de.client.sysmsgs.model.MessageList;
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
		implements DataProxy<ListLoadConfig, ListLoadResult<Message>> {
	
	private static SystemMessageCache instance = null;
	
	public static SystemMessageCache instance() {
		if (instance == null) {
			instance = new SystemMessageCache();
		}
		return instance;
 	}

	private final ServiceFacade services = new ServiceFacade();
	private final HashMap<String, Message> messages = new HashMap<String, Message>();
	private final ArrayList<Callback<ListLoadResult<Message>, Throwable>> loadCallbacks 
			= new ArrayList<Callback<ListLoadResult<Message>, Throwable>>();
	
	private boolean amPolling = false;
	private boolean syncedOnce = false;
	
	private SystemMessageCache() {
	}
	
	@Override
	public void load(final ListLoadConfig unused, 
			final Callback<ListLoadResult<Message>, Throwable> callback) {
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
		for (Message msg: messages.values()) {
// TODO when seen works, remove this
//			if (!msg.isSeen()) {
				count++;
			}
//		}
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
	
	public void dismissMessage(final Message message, final Callback<Void, Throwable> callback) {
		if (messages.containsValue(message)) {
			if (message.isDismissible()) {
				dismissDismissibleMessage(message, callback);
			} else {
				// TODO Should an exception be returned when a message cannot be hidden?
				callback.onSuccess(null);
			}
		}  else {
			// TODO should an exception be returned when a message doesn't exist?
			callback.onSuccess(null);			
		}
	}

	private void dismissDismissibleMessage(final Message message, 
			final Callback<Void, Throwable> callback) {
		final IdList idsDTO = MessageFactory.INSTANCE.makeIdList().as();
		idsDTO.setIds(Arrays.asList(message.getId()));
		services.hideMessages(idsDTO, new AsyncCallback<Void>() {
			@Override
			public void onFailure(final Throwable caught) {
				callback.onFailure(caught);
			}
			@Override
			public void onSuccess(final Void unused) {
				messages.remove(message.getId());
				callback.onSuccess(null);
			}});
	}
	
	private void markMessagesAsAcknowledged() {
		for (Message msg : messages.values()) {
// TODO fix this when seen works
//			msg.setSeen(true);
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
		services.getAllMessages(new AsyncCallback<MessageList>() {
			@Override
			public void onFailure(final Throwable exn) {
				notifyLoadersOfFailure(exn);
			}
			@Override
			public void onSuccess(final MessageList messages) {
				addMessagesAndNotifyLoaders(messages);
			}});
	}

	private void requestUnseenMessages() {
		services.getUnseenMessages(new AsyncCallback<MessageList>() {
			@Override
			public void onFailure(final Throwable unused) {
			}
			@Override
			public void onSuccess(final MessageList messages) {
				addMessages(messages);
			}});
	}

	private void addMessagesAndNotifyLoaders(final MessageList newMessages) {
		addMessages(newMessages);
		syncedOnce = true;
		final ArrayList<Message> msgLst = new ArrayList<Message>(messages.values());
		for (Callback<ListLoadResult<Message>, Throwable> callback : loadCallbacks) {
			callback.onSuccess(new ListLoadResult<Message>() {
					private static final long serialVersionUID = 8785407136143469894L;
					@Override
					public List<Message> getData() {
						return msgLst;
					}});
		}
		loadCallbacks.clear();
	}
	
	private void notifyLoadersOfFailure(final Throwable exn) {
		for (Callback<ListLoadResult<Message>, Throwable> callback : loadCallbacks) {
			callback.onFailure(exn);
		}
		loadCallbacks.clear();
	}
	
	private void addMessages(final MessageList messagesDTO) {
		final long initNumUnseen = countUnseen();
		for (Message msg: messagesDTO.getList()) {
			if (!isMessageStateDated(msg)) {
				messages.put(msg.getId(), msg);
			}
		}
		if (initNumUnseen < countUnseen()) {
			EventBus.getInstance().fireEvent(new NewMessagesEvent());
		}
	}
	
	private boolean isMessageStateDated(final Message msg) {
		final Message oldMsg = messages.get(msg.getId());
// TODO fix this when seen works
//		return oldMsg != null && oldMsg.isSeen() && !msg.isSeen();
return false;
	}
	
	private ListLoadResult<Message> makeLoadResult() {
		final ArrayList<Message> msgLst = new ArrayList<Message>(messages.values());
		return new ListLoadResult<Message>() {
				private static final long serialVersionUID = 8785407136143469894L;
				@Override
				public List<Message> getData() {
					return msgLst;
				}};
	}
	
}
