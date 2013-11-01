package org.iplantc.de.client.viewer.models;

import com.google.gwt.json.client.JSONArray;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface SeparatedTextData {
    @PropertyName("csv")
    JSONArray getData();
}
