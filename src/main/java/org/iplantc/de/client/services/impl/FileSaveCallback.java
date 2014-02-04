package org.iplantc.de.client.services.impl;

import org.iplantc.de.jsonutil.client.JsonUtil;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uicommons.client.models.diskresources.DiskResourceAutoBeanFactory;
import org.iplantc.core.uicommons.client.models.diskresources.File;
import org.iplantc.core.uicommons.client.util.DiskResourceUtil;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.events.DefaultUploadCompleteHandler;
import org.iplantc.de.client.viewer.events.FileSavedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.sencha.gxt.widget.core.client.Component;

public class FileSaveCallback implements AsyncCallback<String> {

    private final String parentFolder;
    private final String fileName;
    private Component maskingContainer;
    private boolean newFile;

    public FileSaveCallback(String path, boolean newFile, Component container) {
        this.fileName = DiskResourceUtil.parseNameFromPath(path);
        this.parentFolder = DiskResourceUtil.parseParent(path);
        this.maskingContainer = container;
        this.newFile = newFile;
    }

    @Override
    public void onSuccess(String result) {
        maskingContainer.unmask();
        JSONObject obj = JSONParser.parseStrict(result).isObject();
        DefaultUploadCompleteHandler uch = new DefaultUploadCompleteHandler(parentFolder);
        String fileJson = JsonUtil.getObject(obj, "file").toString();
        if (newFile) {
            uch.onCompletion(fileName, fileJson);
        }
        DiskResourceAutoBeanFactory factory = GWT.create(DiskResourceAutoBeanFactory.class);
        AutoBean<File> fileAB = AutoBeanCodex.decode(factory, File.class, fileJson);
        FileSavedEvent evnt = new FileSavedEvent(fileAB.as());
        EventBus.getInstance().fireEvent(evnt);
    }

    @Override
    public void onFailure(Throwable caught) {
        maskingContainer.unmask();
        ErrorHandler.post(I18N.ERROR.fileUploadFailed(fileName), caught);
    }

}
