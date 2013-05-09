package org.iplantc.de.client.notifications.models.payload;

import java.util.List;

import org.iplantc.core.uicommons.client.models.HasId;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

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
