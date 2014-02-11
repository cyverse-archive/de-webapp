package org.iplantc.de.client.notifications.models.payload;

import org.iplantc.de.apps.client.models.toolrequest.ToolRequestHistory;
import org.iplantc.de.client.models.HasId;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * Payload AutoBean for Tool Request Notifications.
 * 
 * @author psarando
 * 
 */
public interface PayloadToolRequest extends HasId, HasName {

    @Override
    @PropertyName("uuid")
    String getId();

    List<ToolRequestHistory> getHistory();
}
