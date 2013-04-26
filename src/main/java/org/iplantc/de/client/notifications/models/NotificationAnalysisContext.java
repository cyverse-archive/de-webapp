package org.iplantc.de.client.notifications.models;

import org.iplantc.core.uicommons.client.models.HasId;
import org.iplantc.de.client.notifications.services.NotificationCallback;

import com.google.gwt.user.client.ui.HasName;

/**
 * A Context AutoBean with just a name and ID for the {@link NotificationCallback}.
 * 
 * @author psarando
 * 
 */
public interface NotificationAnalysisContext extends HasId, HasName {

    void setId(String id);
}
