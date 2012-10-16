package org.iplantc.de.client.services;

import java.util.List;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.factories.EventJSONFactory.ActionType;
import org.iplantc.de.client.utils.NotifyInfo;

import com.google.gwt.json.client.JSONObject;

/**
 * Defines an asynchronous callback for DiskResource (a file or folder) Delete event.
 * 
 * @author amuir
 * 
 */
public abstract class DiskResourceDeleteCallback extends DiskResourceActionCallback {
    protected List<String> listDiskResources;

    /**
     * Instantiate from a list of files and folders.
     * 
     * @param listDiskResources list of folders to delete.
     */
    public DiskResourceDeleteCallback(List<String> listDiskResources) {
        this.listDiskResources = listDiskResources;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ActionType getActionType() {
        return ActionType.DELETE;
    }

    @Override
    public void onSuccess(String result) {
        super.onSuccess(result);
        NotifyInfo.display(org.iplantc.de.client.I18N.DISPLAY.delete(), I18N.DISPLAY.deleteMsg());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JSONObject buildPayload(final JSONObject jsonResult) {
        JSONObject ret = new JSONObject();

        ret.put(getResourceListKey(), JsonUtil.buildArrayFromStrings(listDiskResources));

        return ret;
    }

    /**
     * @return The payload JSON key for this callback's disk resource list ("files" or "folders").
     */
    protected abstract String getResourceListKey();
}
