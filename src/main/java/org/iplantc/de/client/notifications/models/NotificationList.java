/**
 * 
 */
package org.iplantc.de.client.notifications.models;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * @author sriram
 * 
 */
public interface NotificationList {
    @PropertyName("messages")
    List<Notification> getNotifications();
}
