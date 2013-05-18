package org.iplantc.de.client.notifications.models.payload;

import java.util.Date;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * A PropertyAccess interface for ToolRequestHistory AutoBeans.
 * 
 * @author psarando
 * 
 */
public interface ToolRequestHistoryProperties extends PropertyAccess<ToolRequestHistory> {

    ValueProvider<ToolRequestHistory, ToolRequestStatus> status();

    ValueProvider<ToolRequestHistory, String> updatedBy();

    ValueProvider<ToolRequestHistory, Date> statusDate();

    ValueProvider<ToolRequestHistory, String> comments();
}
