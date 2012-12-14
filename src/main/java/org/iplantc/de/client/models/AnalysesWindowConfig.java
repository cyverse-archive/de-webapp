package org.iplantc.de.client.models;

import org.iplantc.core.jsonutil.JsonUtil;

import com.google.gwt.json.client.JSONObject;

/**
 * 
 * A config class for Analysis window
 * 
 * @author sriram
 * 
 */
@SuppressWarnings("nls")
public class AnalysesWindowConfig extends WindowConfig {
    public static final String ANALYSIS_ID = "id";
    public static final String ANALYSIS_NAME = "name";

    public AnalysesWindowConfig(JSONObject json) {
        super(json);
    }

    public void setAnalysisId(String id) {
        setString(ANALYSIS_ID, id);
    }

    public String getAnalysisId() {
        return JsonUtil.getString(this, ANALYSIS_ID);
    }

    public String getAnalysisName() {
        return JsonUtil.getString(this, ANALYSIS_NAME);
    }
}
