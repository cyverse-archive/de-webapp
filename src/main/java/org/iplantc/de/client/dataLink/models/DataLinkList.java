package org.iplantc.de.client.dataLink.models;

import java.util.List;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * A convenience bean definition for deserializing lists of <code>DataLink</code>s
 * 
 * @author jstroot
 * 
 */
public interface DataLinkList {

    @PropertyName("tickets")
    List<DataLink> getTickets();

}
