package org.iplantc.de.client.notifications.models.payload;

import java.util.List;

/**
 * Payload AutoBean for Tool Request Notifications.
 * 
 * @author psarando
 * 
 */
public interface PayloadToolRequest {

    List<ToolRequestHistory> getHistory();
}
