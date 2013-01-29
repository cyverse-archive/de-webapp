package org.iplantc.de.client.utils;

import java.util.List;

import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.de.client.models.Collaborator;

import com.extjs.gxt.ui.client.data.FilterConfig;
import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * An RpcProxy for a ListLoader that will call the searchUser service, then process the JSON results
 * into a Collaborator list using {@link CollaboratorsUtil}.
 * 
 * @author psarando, jstroot
 * 
 */
public class UserSearchRpcProxy extends RpcProxy<List<Collaborator>> {
    protected String tag;
    private String lastQueryText = ""; //$NON-NLS-1$

    public UserSearchRpcProxy(String tag) {
        this.tag = tag;
    }

    public String getLastQueryText() {
        return lastQueryText;
    }

    @Override
    protected void load(Object loadConfig, final AsyncCallback<List<Collaborator>> callback) {

        // Get the proxy's search params.
        FilterPagingLoadConfig config = (FilterPagingLoadConfig)loadConfig;

        // Cache the query text.
        lastQueryText = ""; //$NON-NLS-1$

        List<FilterConfig> filterConfigs = config.getFilterConfigs();
        if (filterConfigs != null && !filterConfigs.isEmpty()) {
            lastQueryText = (String)filterConfigs.get(0).getValue();
        }

        if (lastQueryText == null || lastQueryText.isEmpty()) {
            // nothing to search
            return;
        }

        // Cache the search text for this callback; used to sort the results.
        final String searchText = lastQueryText;

        CollaboratorsUtil.search(searchText, new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onSuccess(CollaboratorsUtil.getSearchResutls());
            }
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                callback.onFailure(caught);
            }
        });
    }

}
