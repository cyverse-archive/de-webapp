package org.iplantc.de.client.notifications.models.payload;

import java.util.Date;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * A Status History AutoBean for PayloadToolRequest.
 * 
 * @author psarando
 * 
 */
public interface ToolRequestHistory {

    ToolRequestStatus getStatus();

    @PropertyName("updated_by")
    String getUpdatedBy();

    @PropertyName("status_date")
    Date getStatusDate();

    String getComments();
}
