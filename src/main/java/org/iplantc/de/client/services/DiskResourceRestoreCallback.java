/**
 * 
 */
package org.iplantc.de.client.services;

import org.iplantc.de.client.factories.EventJSONFactory.ActionType;

import com.google.gwt.json.client.JSONObject;

/**
 * @author sriram
 * 
 */
public class DiskResourceRestoreCallback extends DiskResourceActionCallback {

    @Override
    protected ActionType getActionType() {

        return null;
    }

    @Override
    protected JSONObject buildPayload(JSONObject jsonResult) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getErrorMessageDefault() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getErrorMessageByCode(ErrorCode code, JSONObject jsonError) {
        // TODO Auto-generated method stub
        return null;
    }

}
