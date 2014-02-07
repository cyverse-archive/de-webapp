package org.iplantc.de.client.viewer.callbacks;

import org.iplantc.de.commons.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.views.IsMaskable;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IplantInfoBox;
import org.iplantc.de.client.I18N;

import com.google.common.base.Strings;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LoadGenomeInCoGeCallback implements AsyncCallback<String> {

    private IsMaskable container;

    public LoadGenomeInCoGeCallback(IsMaskable container) {
        this.container = container;
    }

    @Override
    public void onFailure(Throwable caught) {
        container.unmask();
        ErrorHandler.post(I18N.ERROR.cogeError(), caught);
    }

    @Override
    public void onSuccess(String result) {
        JSONObject resultObj = JsonUtil.getObject(result);
        String url = JsonUtil.getString(resultObj, "coge_genome_url");
        if (!Strings.isNullOrEmpty(url)) {
            IplantInfoBox iib = new IplantInfoBox(I18N.DISPLAY.coge(), I18N.DISPLAY.cogeResponse(url));
            iib.show();
        } else {
            onFailure(null);
        }
        container.unmask();

    }

}
