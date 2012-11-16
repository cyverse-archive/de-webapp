package org.iplantc.de.client.dataLink.models;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanFactory.Category;

@Category(DataLinkCategory.class)
public interface DataLinkFactory extends AutoBeanFactory {
    
    AutoBean<DataLink> dataLink();
    
    AutoBean<DataLinkList> dataLinkList();

}
