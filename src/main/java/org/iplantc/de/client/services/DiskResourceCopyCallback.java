/**
 * 
 */
package org.iplantc.de.client.services;

import com.google.gwt.json.client.JSONObject;

/**
 * @author sriram
 * 
 */
public class DiskResourceCopyCallback extends DiskResourceMoveCallback {

    /*
     * (non-Javadoc)
     * 
     * @see org.iplantc.de.client.services.DiskResourceMoveCallback#getPayloadAction()
     */
    @Override
    protected String getPayloadAction() {
        return "copy";
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
        return getErrorMessageForFiles(code, parsePathsToNameList(jsonError));
    }

}
