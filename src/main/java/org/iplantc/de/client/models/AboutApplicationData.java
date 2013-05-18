package org.iplantc.de.client.models;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface AboutApplicationData {

    @PropertyName("buildnumber")
    String getBuildNumber();

    @PropertyName("release")
    String getReleaseVersion();

}
