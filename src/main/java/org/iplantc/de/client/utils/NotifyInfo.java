package org.iplantc.de.client.utils;

import org.iplantc.core.uicommons.client.info.IplantAnnouncer;
import org.iplantc.de.client.notifications.util.NotificationHelper;

import com.extjs.gxt.ui.client.util.Params;

/**
 * Provides a uniform way of presenting notification information to the user.
 * 
 * Optionally, this notification information may be included in a user's My Notification.
 * 
 * Implementation essentially wraps functionality provided in GXT by Info & InfoConfig.
 * 
 * @see com.extjs.gxt.ui.client.widget.Info
 * @see com.extjs.gxt.ui.client.widget.InfoConfig
 * @deprecated Class needs to be ported to GXT3
 */
@Deprecated
public class NotifyInfo {
    /**
     * Provide an informative message to the user and include as a notification.
     * 
     * Allows for the text argument to be a parameterized message.
     * 
     * @param category notification category
     * @param title represents a title for the message.
     * @param text represents the message text to display.
     * @param parameters parameters to be merged into the text argument.
     */
    public static void notify(NotificationHelper.Category category, final String title,
            final String text, Params parameters) {
        doDisplay(category, title, text, parameters);
    }

    /**
     * Provide an informative message to the user and optionally include as a notification.
     * 
     * @param title represents a title for the message.
     * @param text represents the message text to display.
     */
    public static void display(final String title, final String text) {
        IplantAnnouncer.schedule(text);
    }

    private static void doDisplay(NotificationHelper.Category category, final String title,
            final String text, Params parameters) {
        IplantAnnouncer.schedule(text);

        includeAsNotification(category, text, parameters);
    }

    private static void includeAsNotification(NotificationHelper.Category category, final String text,
            Params parameters) {
        // NotificationHelper mgr = NotificationHelper.getInstance();

        // only add to the notification manager when we want inclusion
        // TODO: fix add notification add
        if (parameters == null) {
            // mgr.add(category, new Notification(text));
        } else {
            // mgr.add(category, new Notification(Format.substitute(text, parameters)));
        }
    }
}
