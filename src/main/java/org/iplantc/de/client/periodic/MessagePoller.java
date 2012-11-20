package org.iplantc.de.client.periodic;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.events.NotificationCountUpdateEvent;
import org.iplantc.de.client.services.MessageServiceFacade;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Polls for notification messages.
 */
public class MessagePoller implements Runnable {

    /**
     * Polls for notification messages if notification polling is enabled.
     */
    public void run() {
        new MessageServiceFacade().getUnSeenMessageCount(new AsyncCallback<String>() {
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
