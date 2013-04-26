package org.iplantc.de.client.periodic;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.events.NotificationCountUpdateEvent;
import org.iplantc.de.client.notifications.services.MessageServiceFacade;
import org.iplantc.de.client.utils.TaskRunner;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Polls for messages from the backend.
 */
public final class MessagePoller {
	
	private static final class GetUnseenNotifications implements Runnable {

		@Override
		public void run() {
			new MessageServiceFacade().getUnSeenMessageCount(new AsyncCallback<String>() {
	            @Override
	            public void onFailure(final Throwable caught) {
	                // currently we do nothing on failure
	            }
	            @Override
	            public void onSuccess(final String result) {
	                JSONObject obj = JsonUtil.getObject(result);
	                NotificationCountUpdateEvent event = new NotificationCountUpdateEvent(Integer
	                        .parseInt(JsonUtil.getString(obj, "total")));
	                EventBus.getInstance().fireEvent(event);
	            }
	        });
		}
		
	}
	
    private static MessagePoller instance;

    /**
     * Retrieve singleton instance.
     * 
     * @return the singleton instance.
     */
    public static MessagePoller getInstance() {
        if (instance == null) {
            instance = new MessagePoller();
        }

        return instance;
    }

    private final GetUnseenNotifications getUnseenNotifications = new GetUnseenNotifications();
    
    /**
     * Ensures only 1 MessagePoller at a time is added to the TaskRunner.
     */
    private boolean polling = false;

    private MessagePoller() {
    }

    /**
     * Starts polling.
     */
    public void start() {
        if (!polling) {
            TaskRunner.getInstance().addTask(getUnseenNotifications);
            polling = true;
        }
    }

    /**
     * Stops polling.
     */
    public void stop() {
        if (polling) {
            TaskRunner.getInstance().removeTask(getUnseenNotifications);
            polling = false;
        }
    }

}
