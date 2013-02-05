/**
 * 
 */
package org.iplantc.de.client.views.panels;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.notifications.views.NotificationListView;

import com.sencha.gxt.widget.core.client.menu.Menu;

/**
 * @author sriram
 * 
 */
public class ViewNotificationMenu extends Menu {

    private final NotificationListView view;

    public ViewNotificationMenu(EventBus eventBus) {
        view = new NotificationListView(eventBus);
        add(view.asWidget());
    }

    @Override
    public void showAt(int x, int y) {
        super.showAt(x, y);
        view.highlightNewNotifications();
        view.markAsSeen();
    }

    public void fetchUnseenNotifications() {
        view.fetchUnseenNotifications();

    }

    public void setUnseenCount(int new_count) {
        view.setUnseenCount(new_count);
    }

}
