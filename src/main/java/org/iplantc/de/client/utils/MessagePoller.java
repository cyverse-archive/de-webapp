package org.iplantc.de.client.utils;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.events.NotificationCountUpdateEvent;
import org.iplantc.de.client.services.MessageServiceFacade;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Periodically retrieves messages for the current user from the server.
 */
public class MessagePoller {
    private PollingTimer timer;
    private static MessagePoller instance;

    private MessagePoller() {
    }

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

    /**
     * Starts polling.
     */
    public void start() {
        if (timer == null) {
            timer = new PollingTimer();
            TaskRunner.getInstance().addTask(timer);
        }
    }

    /**
     * Stops polling.
     */
    public void stop() {
        if (timer != null) {
            TaskRunner.getInstance().removeTask(timer);
            timer = null;
        }
    }

    private class PollingTimer implements Runnable {

        @Override
        public void run() {
            MessageServiceFacade facade = new MessageServiceFacade();

            facade.getUnSeenMessageCount(new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    // currently we do nothing on failure
                }

                @Override
                public void onSuccess(String result) {
                    JSONObject obj = JsonUtil.getObject(result);
                    NotificationCountUpdateEvent event = new NotificationCountUpdateEvent(Integer
                            .parseInt(JsonUtil.getString(obj, "total")));
                    EventBus.getInstance().fireEvent(event);
                }
            });
        }
    }

}
