package org.iplantc.de.client.analysis.services;

import java.util.List;

import org.iplantc.de.commons.client.DEServiceFacade;
import org.iplantc.de.commons.client.models.DEProperties;
import org.iplantc.de.commons.client.models.HasId;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.common.base.Strings;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;

/**
 * Provides access to remote services for analyses management operations.
 */
public class AnalysisServiceFacade {

    /**
     * Get all the analyses for a given workspace.
     * 
     * @param workspaceId unique id for a user's workspace.
     * @param loadConfig optional remote paging and sorting configs.
     * @param callback executed when RPC call completes.
     */
    public void getAnalyses(String workspaceId, FilterPagingLoadConfig loadConfig,
            AsyncCallback<String> callback) {
        StringBuilder address = new StringBuilder(DEProperties.getInstance().getMuleServiceBaseUrl());

        address.append("workspaces/"); //$NON-NLS-1$
        address.append(workspaceId);
        address.append("/executions/list"); //$NON-NLS-1$

        if (loadConfig != null) {
            address.append("?limit="); //$NON-NLS-1$
            address.append(loadConfig.getLimit());

            address.append("&offset="); //$NON-NLS-1$
            address.append(loadConfig.getOffset());

            List<? extends SortInfo> sortInfoList = loadConfig.getSortInfo();
            if (sortInfoList != null && !sortInfoList.isEmpty()) {
                SortInfo sortInfo = sortInfoList.get(0);

                String sortField = sortInfo.getSortField();
                if (!Strings.isNullOrEmpty(sortField)) {
                    address.append("&sort-field="); //$NON-NLS-1$
                    address.append(sortField);
                }

                SortDir sortDir = sortInfo.getSortDir();
                if (sortDir == SortDir.ASC || sortDir == SortDir.DESC) {
                    address.append("&sort-order="); //$NON-NLS-1$
                    address.append(sortDir.toString());
                }
            }

            List<FilterConfig> filters = loadConfig.getFilters();
            if (filters != null && !filters.isEmpty()) {
                JSONArray jsonFilters = new JSONArray();
                int filterIndex = 0;

                for (FilterConfig filter : filters) {
                    String field = filter.getField();
                    String value = filter.getValue();

                    if (!Strings.isNullOrEmpty(field) && !Strings.isNullOrEmpty(value)) {
                        JSONObject jsonFilter = new JSONObject();

                        jsonFilter.put("field", new JSONString(field)); //$NON-NLS-1$
                        jsonFilter.put("value", new JSONString(value)); //$NON-NLS-1$

                        jsonFilters.set(filterIndex++, jsonFilter);
                    }
                }

                if (jsonFilters.size() > 0) {
                    address.append("&filter="); //$NON-NLS-1$
                    address.append(URL.encodeQueryString(jsonFilters.toString()));
                }
            }
        }

        ServiceCallWrapper wrapper = new ServiceCallWrapper(address.toString());
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /**
     * Delete an analysis execution
     * 
     * @param workspaceId unique id for a user's workspace.
     * @param json id of analysis to delete.
     * @param callback executed when RPC call completes.
     */
    public void deleteAnalysis(String workspaceId, String json, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "workspaces/" //$NON-NLS-1$
                + workspaceId + "/executions" + "/delete"; //$NON-NLS-1$ //$NON-NLS-2$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT, address, json);

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /**
     * Stop a currently running analysis
     * 
     * @param analysisId id of the analysis to be stopped.
     * @param callback executed when RPC call completes.
     */
    public void stopAnalysis(String analysisId, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "stop-analysis/"
                + analysisId;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE, address);

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    public void getAnalysisParams(String analysisId, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl()
                + "get-property-values/" + analysisId;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /**
     * Launch a wizard analysis
     * 
     * @param workspaceId unique id for a user's workspace.
     * @param json JSON configuration of analysis to launch.
     * @param callback executed when RPC call completes.
     */
    public void launchAnalysis(String workspaceId, String json, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "workspaces/" //$NON-NLS-1$
                + workspaceId + "/newexperiment"; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT, address, json);

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /**
     * get json to relaunch an analysis
     * 
     * @param analyisId
     */
    public void relaunchAnalysis(HasId analyisId, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getUnproctedMuleServiceBaseUrl() + "analysis-rerun-info/" + analyisId.getId();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

}
