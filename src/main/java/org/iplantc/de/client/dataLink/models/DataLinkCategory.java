package org.iplantc.de.client.dataLink.models;

import com.google.web.bindery.autobean.shared.AutoBean;

public class DataLinkCategory {

    public static String getName(AutoBean<DataLink> instance){
        return "http://" + instance.as().getId();
    }
}
