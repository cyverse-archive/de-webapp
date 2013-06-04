package org.iplantc.de.client.sysmsgs.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.iplantc.de.client.periodic.MessagePoller;
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

    private static IdList filterIds(final Collection<Message> messages) {
        final ArrayList<String> ids = new ArrayList<String>();
        for (Message msg : messages) {
            ids.add(msg.getId());
        }
        final IdList idsDTO = MessageFactory.INSTANCE.makeIdList().as();
        idsDTO.setIds(ids);
        return idsDTO;
    }

    private final ServiceFacade services;
    private final HashMap<String, Message> messages;
    private final ArrayList<Callback<ListLoadResult<Message>, Throwable>> loadCallbacks;
    private final Runnable syncTask;
	
    private boolean amPolling;
    private boolean syncedOnce;
	
	private SystemMessageCache() {
        services = new ServiceFacade();
        messages = new HashMap<String, Message>();
        loadCallbacks = new ArrayList<Callback<ListLoadResult<Message>, Throwable>>();
        syncTask = new Runnable() {
            @Override
            public void run() {
                requestAllMessages();
            }
        };
        amPolling = false;
        syncedOnce = false;
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
     * Indicates whether or not the given message is in the cache
     * 
     * @return true if the message exists, otherwise false
     */
    public boolean hasMessage(final Message message) {
        return messages.containsKey(message.getId());
    }

    /**
     * Asynchronously marks as message has have been seen by the user
     * 
     * @param message the message being marked
     * @param callback the callback to call upon completion.
     */
    public void markSeen(final Message message, final Callback<Void, Throwable> callback) {
        final IdList idsDTO = MessageFactory.INSTANCE.makeIdList().as();
        idsDTO.setIds(Arrays.asList(message.getId()));
        services.acknowledgeMessages(idsDTO, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
            @Override
            public void onSuccess(Void unused) {
                message.setSeen(true);
                callback.onSuccess(null);
            }
        });
    }

    /**
     * Tells the cache to start periodically synchronizing itself with the back end.
     */
	public void startSyncing() {
		if (!amPolling) {
			requestAllMessages();
            MessagePoller.getInstance().addTask(syncTask);
			amPolling = true;
		}
	}

    /**
     * Tells the cache to stop periodically synchronizing itself with the back end.
     */
    public void stopSyncing() {
        if (amPolling) {
            MessagePoller.getInstance().removeTask(syncTask);
            amPolling = false;
        }
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

	private void requestAllMessages() {
		services.getAllMessages(new AsyncCallback<MessageList>() {
			@Override
			public void onFailure(final Throwable exn) {
				notifyLoadersOfFailure(exn);
			}
			@Override
			public void onSuccess(final MessageList messages) {
				replaceMessagesAndNotifyLoaders(messages);
			}});
	}

	private void replaceMessagesAndNotifyLoaders(final MessageList newMessages) {
		replaceMessages(newMessages);
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
        markReceived(messages.values());
    }

    private void markReceived(final Collection<Message> messages) {
        services.markReceived(filterIds(messages), new AsyncCallback<Void>() {

            @Override
            public void onFailure(final Throwable caught) {
                // TODO figure out how to handle this
            }

            @Override
            public void onSuccess(final Void unused) {
            }
        });
	}
	
	private void notifyLoadersOfFailure(final Throwable exn) {
		for (Callback<ListLoadResult<Message>, Throwable> callback : loadCallbacks) {
			callback.onFailure(exn);
		}
		loadCallbacks.clear();
	}
	
	private void replaceMessages(final MessageList updatedDTO) {
        messages.clear();
        for (Message msg : updatedDTO.getList()) {
            messages.put(msg.getId(), msg);
        }
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
