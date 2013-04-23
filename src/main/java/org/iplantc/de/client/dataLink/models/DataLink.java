package org.iplantc.de.client.dataLink.models;

import java.util.Date;

import org.iplantc.core.uidiskresource.client.models.DiskResource;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;
import com.sencha.gxt.widget.core.client.tree.Tree;

/**
 * Represents a link for sharing <code>DiskResource</code>s outside of the <b>Discovery Environment</b>.
 * The interface, <code>IDiskResource</code>, is extended so that <code>DataLink</code> objects can be
 * displayed in the same {@link Tree} as a <code>DiskResource</code>.
 * 
 * @author jstroot
 * 
 */
public interface DataLink extends DiskResource {

    @Override
    @PropertyName("ticket-id")
    String getId();

    @Override
    @PropertyName("ticket-id")
    void setId(String id);

    @Override
    String getPath();

    @PropertyName("expiry")
    Date getExpirationDate();

    @PropertyName("uses-limit")
    String getUseLimit();
    
    @Override
    @PropertyName("ticket-id")
    String getName();

}
