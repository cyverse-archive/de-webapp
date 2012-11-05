/**
 * 
 */
package org.iplantc.de.client.services;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.factories.EventJSONFactory.ActionType;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

/**
 * @author sriram
 * 
 */
public class EmptyTrashCallback extends DiskResourceActionCallback {

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.rpc.AsyncCallback#onSuccess(java.lang.Object)
     */
    @Override
    public void onSuccess(String result) {
        super.onSuccess(result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.iplantc.de.client.services.DiskResourceServiceCallback#getErrorMessageDefault()
     */
    @Override
    protected String getErrorMessageDefault() {
        return I18N.ERROR.emptyTrashError();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.iplantc.de.client.services.DiskResourceServiceCallback#getErrorMessageByCode(org.iplantc.de
     * .client.services.DiskResourceServiceCallback.ErrorCode, com.google.gwt.json.client.JSONObject)
     */
    @Override
    protected String getErrorMessageByCode(ErrorCode code, JSONObject jsonError) {
        return getErrorMessage(code, parsePathsToNameList(jsonError));
    }

    @Override
    protected ActionType getActionType() {
        return ActionType.EMPTYTRASH;
    }

    @Override
    protected JSONObject buildPayload(JSONObject jsonResult) {
        JSONObject ret = new JSONObject();
        JSONArray arr = JsonUtil.getArray(jsonResult, "paths");
        if (arr != null) {
            ret.put("diskResources", arr);
        }
        return ret;
    }
}
