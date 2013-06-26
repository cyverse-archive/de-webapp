package org.iplantc.de.client.notifications.models.payload;

import org.iplantc.core.uicommons.client.models.diskresources.File;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * Payload AutoBean for Data Notifications.
 * 
 * @author psarando
 * 
 */
public interface PayloadData {
    /**
     * XXX JDS This could be turned into an enum
     * 
     * @return
     */
    @PropertyName("action")
    String getAction();

    File getData();
}
