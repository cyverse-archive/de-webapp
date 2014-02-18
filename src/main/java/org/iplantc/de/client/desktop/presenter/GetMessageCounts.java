package org.iplantc.de.client.desktop.presenter;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.NewSystemMessagesEvent;
import org.iplantc.de.client.events.NotificationCountUpdateEvent;
import org.iplantc.de.client.events.SystemMessageCountUpdateEvent;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.notifications.Counts;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This task requests the message counts from the backend
 */
final class GetMessageCounts implements Runnable {

    /**
     * @see Runnable#run()
     */
    @Override
    public void run() {
        ServicesInjector.INSTANCE.getMessageServiceFacade().getMessageCounts(new AsyncCallback<Counts>() {
            @Override
            public void onFailure(final Throwable caught) {
            }

            @Override
            public void onSuccess(final Counts cnts) {
                fireEvents(cnts);
            }
        });
    }

    private void fireEvents(final Counts counts) {
        final EventBus bus = EventBus.getInstance();
        final int unseenNoteCnt = counts.getUnseenNotificationCount();
        bus.fireEvent(new NotificationCountUpdateEvent(unseenNoteCnt));
        final int unseenSysMsgCnt = counts.getUnseenSystemMessageCount();
        bus.fireEvent(new SystemMessageCountUpdateEvent(unseenSysMsgCnt));
        if (counts.getNewSystemMessageCount() > 0) {
            bus.fireEvent(new NewSystemMessagesEvent());
        }
    }

}