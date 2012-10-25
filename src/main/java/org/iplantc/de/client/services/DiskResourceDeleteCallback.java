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
public class DiskResourceDeleteCallback extends DiskResourceActionCallback {
    protected List<String> listDiskResources;
    private boolean notify;

    /**
     * Instantiate from a list of files and folders.
     * 
     * @param listDiskResources list of folders to delete.
     */
    public DiskResourceDeleteCallback(List<String> listDiskResources) {
        this(listDiskResources, true);
    }

    /**
     * Instantiate from a list of files and folders.
     * 
     * @param listDiskResources list of folders to delete.
     */
    public DiskResourceDeleteCallback(List<String> listDiskResources, boolean notify) {
        this.listDiskResources = listDiskResources;
        this.notify = notify;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ActionType getActionType() {
        return ActionType.DISKRESOURCE_DELETE;
    }

    @Override
    public void onSuccess(String result) {
        super.onSuccess(result);
        if (notify) {
            NotifyInfo.display(org.iplantc.de.client.I18N.DISPLAY.delete(), I18N.DISPLAY.deleteMsg());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JSONObject buildPayload(final JSONObject jsonResult) {
        JSONObject ret = new JSONObject();

        ret.put("diskResources", JsonUtil.buildArrayFromStrings(listDiskResources));

        return ret;
    }

    @Override
    protected String getErrorMessageDefault() {
        return I18N.ERROR.deleteFailed();
    }

    @Override
    protected String getErrorMessageByCode(ErrorCode code, JSONObject jsonError) {
        return getErrorMessage(code, parsePathsToNameList(jsonError));
    }
}
