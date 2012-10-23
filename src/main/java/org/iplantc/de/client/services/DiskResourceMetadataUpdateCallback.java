package org.iplantc.de.client.services;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uidiskresource.client.util.DiskResourceUtil;
import org.iplantc.de.client.I18N;

import com.google.gwt.json.client.JSONObject;

/**
 * 
 * 
 * @author sriram
 * 
 */
public class DiskResourceMetadataUpdateCallback extends DiskResourceServiceCallback {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSuccess(String result) {
        // do nothing intentionally
    }

    @Override
    protected String getErrorMessageDefault() {
        return I18N.ERROR.metadataUpdateFailed();
    }

    @Override
    protected String getErrorMessageByCode(ErrorCode code, JSONObject jsonError) {
        return getErrorMessage(code,
                DiskResourceUtil.parseNameFromPath(JsonUtil.getString(jsonError, PATH)));
    }
}