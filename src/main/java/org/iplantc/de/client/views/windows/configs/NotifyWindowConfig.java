package org.iplantc.de.client.views.windows.configs;

import org.iplantc.de.client.notifications.util.NotificationHelper.Category;

public interface NotifyWindowConfig extends WindowConfig {

    Category getSortCategory();

    void setSortCategory(Category category);
}
