package org.iplantc.de.client.analysis.models;

import org.iplantc.core.uicommons.client.models.HasDescription;
import org.iplantc.core.uicommons.client.models.HasId;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface Analysis extends HasId, HasName, HasDescription {

    @PropertyName("wiki_url")
    public void setWikiUrl(String url);

    @PropertyName("resultfolderid")
    public String getResultFolderId();

    @PropertyName("startdate")
    public long getStartDate();

    @PropertyName("enddate")
    public long getEndDate();

    @PropertyName("analysis_id")
    public String getAppId();

    @PropertyName("analysis_name")
    public String getAppName();

    @PropertyName("analysis_details")
    public String getAnalysisDetails();

    public String getStatus();

    @PropertyName("startdate")
    public void setStartDate(long startdate);

    @PropertyName("enddate")
    public void setEndDate(long enddate);

    @PropertyName("analysis_id")
    public void setAppId(String appId);

    @PropertyName("analysis_name")
    public void setAppName(String appName);

    @PropertyName("analysis_details")
    public void setAnalysisDetails(String analysis_details);

    public void setStatus(String status);

    public void setId(String id);

    @PropertyName("resultfolderid")
    public void setResultFolderId(String resultfolderid);

    @PropertyName("wiki_url")
    public String getWikiUrl();
}