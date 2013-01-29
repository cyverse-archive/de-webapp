package org.iplantc.de.client.utils;

import java.util.List;

import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.de.client.models.Collaborator;

import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
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
    private String lastQueryText = ""; //$NON-NLS-1$

    public UserSearchRpcProxy() {
    }

    public String getLastQueryText() {
        return lastQueryText;
    }

    @Override
    protected void load(Object loadConfig, final AsyncCallback<List<Collaborator>> callback) {
        // Get the proxy's search params.
        BasePagingLoadConfig config = (BasePagingLoadConfig)loadConfig;

        // Cache the query text.
        lastQueryText = config.get("query"); //$NON-NLS-1$

        if (lastQueryText == null || lastQueryText.isEmpty()) {
            // nothing to search
            return;
        }

        CollaboratorsUtil.search(lastQueryText, new AsyncCallback<Void>() {
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
