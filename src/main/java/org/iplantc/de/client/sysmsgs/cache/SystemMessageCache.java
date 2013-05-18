package org.iplantc.de.client.sysmsgs.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.data.shared.loader.DataProxy;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.periodic.MessagePoller;
import org.iplantc.de.client.sysmsgs.events.MessagesUpdatedEvent;
import org.iplantc.de.client.sysmsgs.model.IdList;
import org.iplantc.de.client.sysmsgs.model.Message;
import org.iplantc.de.client.sysmsgs.model.MessageFactory;
import org.iplantc.de.client.sysmsgs.model.MessageList;
import org.iplantc.de.client.sysmsgs.services.ServiceFacade;

/**
 * This class manages a local cache of the user's system messages. 
 * 
 * It is a singleton class. The singleton is lazily constructed the first time the instance() class 
 * method is called.
 * 
 * The cache needs to synchronize itself with the back end. The startSyncing() method begins this
 * process.
 * 
 * TODO refactor this class.  It does too much.
 */
public final class SystemMessageCache 
		implements DataProxy<ListLoadConfig, ListLoadResult<Message>> {
	
	private static SystemMessageCache instance = null;
	
	/**
	 * Retrieves the the singleton instance. The instance will be constructed if it hasn't already
	 * been.
	 * 
	 * @return the cache instance.
	 */
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
	
	/**
	 * @see DataProxy#load(ListLoadConfig, Callback)
	 */
	@Override
	public void load(final ListLoadConfig unused, 
			final Callback<ListLoadResult<Message>, Throwable> callback) {
		if (syncedOnce) {
			callback.onSuccess(makeLoadResult());
		} else {
			loadCallbacks.add(callback);
		}
	}

	/**
	 * Tells the cache to start periodically synchronizing itself with the back end.
	 */
	public void startSyncing() {
		if (!amPolling) {
			requestAllMessages();
			MessagePoller.getInstance().addTask(new Runnable() {
				@Override
				public void run() {
					requestAllMessages();
				}});
			amPolling = true;
		}
	}
	
	/**
	 * Returns the current number of message that the user has not acknowledged.
	 * 
	 * @return the number of unacknowledged messages
	 */
	public long countUnseen() {
		long count = 0;
		for (Message msg: messages.values()) {
			if (!msg.isSeen()) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Acknowledges all of the messages for the user. This calls through to the back end. The 
	 * provided callback is executed when the back end responds.
 	 * 
	 * @param callback The callback to execute when the back end responds.
	 */
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
	
	/**
	 * Dismisses a particular message for the user. This calls through to the back end. The provide
	 * callback is executed when the back end responds.
	 * 
	 * @param message the message to dismiss
	 * @param callback the callback to execute when the back end responds.
	 */
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
			msg.setSeen(true);
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
	
	private void addMessages(final MessageList updatedDTO) {
		updatedDTO.sortById();
		final AutoBean<MessageList> updatedBean = MessageFactory.INSTANCE.makeMessageList(
				updatedDTO);
		final AutoBean<MessageList> bean = MessageFactory.INSTANCE.makeMessageList();
		bean.as().setList(new ArrayList<Message>(messages.values()));
		bean.as().sortById();
		
		if (!AutoBeanUtils.deepEquals(updatedBean, bean)) {
			final boolean newUnseen = anyNewUnseen(updatedDTO);
			messages.clear();
			for (Message msg: updatedDTO.getList()) {
				messages.put(msg.getId(), msg);
			}
			EventBus.getInstance().fireEvent(new MessagesUpdatedEvent(newUnseen));
		}
	}
	
	private boolean anyNewUnseen(final MessageList newMessages) {
		for (Message newMsg : newMessages.getList()) {
			if (!newMsg.isSeen()) {
				final String newId = newMsg.getId();
				if (!messages.containsKey(newId) || messages.get(newMsg.getId()).isSeen()) {
					return true;
				}
			}
		}
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
