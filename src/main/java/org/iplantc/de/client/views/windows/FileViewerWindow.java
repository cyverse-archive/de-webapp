package org.iplantc.de.client.views.windows;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.core.uidiskresource.client.models.File;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.events.FileEditorWindowClosedEvent;
import org.iplantc.de.client.services.callbacks.DiskResourceServiceCallback;
import org.iplantc.de.client.viewer.presenter.FileViewerPresenter;
import org.iplantc.de.client.viewer.views.FileViewer.Presenter;
import org.iplantc.de.client.views.windows.configs.FileViewerWindowConfig;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.sencha.gxt.widget.core.client.PlainTabPanel;

/**
 * @author sriram
 * 
 */
public class FileViewerWindow extends IplantWindowBase {

    private PlainTabPanel tabPanel;
    protected JSONObject manifest;
    protected File file;
    private final FileViewerWindowConfig configAB;

    public FileViewerWindow(FileViewerWindowConfig config) {
        super(null, null);
        this.configAB = config;
        init();
    }

    private void init() {
        setSize("640px", "438px");
        this.file = configAB.getFile();
        getFileManifest();
        setTitle(file.getName());
        tabPanel = new PlainTabPanel();
        add(tabPanel);
    }

    /**
     * Returns an array from the manifest for a given key, or null if no array exists under that key.
     * 
     * @param key
     * @return
     */
    protected JSONValue getItems(String key) {
        return (key != null && manifest != null && manifest.containsKey(key)) ? manifest.get(key) : null;
    }

    @Override
    public void doHide() {
        super.doHide();
        doClose();
    }

    private void doClose() {
        EventBus eventbus = EventBus.getInstance();
        FileEditorWindowClosedEvent event = new FileEditorWindowClosedEvent(file.getId());
        eventbus.fireEvent(event);
    }

    @Override
    public PlainTabPanel getWidget() {
        return tabPanel;
    }

    @Override
    public WindowState getWindowState() {
        return createWindowState(configAB);
    }

    private void getFileManifest() {
        Services.FILE_EDITOR_SERVICE.getManifest(file.getId(),
                new DiskResourceServiceCallback(null) {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    manifest = JsonUtil.getObject(result);
                    Presenter p = new FileViewerPresenter(file, manifest, configAB.isShowTreeTab());
                    p.go(FileViewerWindow.this);
                } else {
                    onFailure(null);
                }
            }

            @Override
            protected String getErrorMessageDefault() {
                return I18N.ERROR.unableToRetrieveFileManifest(file.getName());
            }

            @Override
            protected String getErrorMessageByCode(ErrorCode code, JSONObject jsonError) {
                return getErrorMessageForFiles(code, file.getName());
            }
        });
    }

}
