package org.iplantc.de.client.services.impl;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.util.DiskResourceUtil;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.events.DefaultUploadCompleteHandler;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.widget.core.client.Component;

public class FileSaveCallback implements AsyncCallback<String> {

    private final String parentFolder;
    private final String fileName;
    private Component maskingContainer;

    public FileSaveCallback(String path, Component container) {
        this.fileName = DiskResourceUtil.parseNameFromPath(path);
        this.parentFolder = DiskResourceUtil.parseParent(path);
        this.maskingContainer = container;
    }

    @Override
    public void onSuccess(String result) {
        maskingContainer.unmask();
        JSONObject obj = JSONParser.parseStrict(result).isObject();
        DefaultUploadCompleteHandler uch = new DefaultUploadCompleteHandler(parentFolder);
        uch.onCompletion(fileName, JsonUtil.getObject(obj, "file").toString());
    }

    @Override
    public void onFailure(Throwable caught) {
        maskingContainer.unmask();
        ErrorHandler.post(I18N.ERROR.fileUploadFailed(fileName), caught);
    }

}
