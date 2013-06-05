package org.iplantc.de.client.desktop.presenter;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.events.NotificationCountUpdateEvent;
import org.iplantc.de.client.events.SystemMessageCountUpdateEvent;
import org.iplantc.de.client.notifications.models.Counts;
import org.iplantc.de.client.notifications.services.MessageServiceFacade;

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
        new MessageServiceFacade().getMessageCounts(new AsyncCallback<Counts>() {
            @Override
            public void onFailure(final Throwable caught) {}
            @Override
            public void onSuccess(final Counts cnts) {
                dispatchCounts(cnts);
            }
        });
	}

    private void dispatchCounts(final Counts counts) {
        final int unseenNoteCnt = counts.getUnseenNotificationCount();
        if (unseenNoteCnt > 0) {
            EventBus.getInstance().fireEvent(new NotificationCountUpdateEvent(unseenNoteCnt));
        }
        if (counts.getNewSystemMessageCount() > 0) {
            // TODO fire new system messages event
        }
        final int unseenSysMsgCnt = counts.getUnseenSystemMessageCount();
        EventBus.getInstance().fireEvent(new SystemMessageCountUpdateEvent(unseenSysMsgCnt));
    }
	
}