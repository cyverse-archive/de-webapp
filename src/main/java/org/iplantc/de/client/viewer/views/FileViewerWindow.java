package org.iplantc.de.client.viewer.views;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uicommons.client.models.WindowConfig;
import org.iplantc.core.uidiskresource.client.models.File;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.events.FileEditorWindowClosedEvent;
import org.iplantc.de.client.models.ViewerWindowConfig;
import org.iplantc.de.client.services.callbacks.DiskResourceServiceCallback;
import org.iplantc.de.client.viewer.presenter.FileViewerPresenter;
import org.iplantc.de.client.viewer.views.FileViewer.Presenter;
import org.iplantc.de.client.views.windows.Gxt3IplantWindow;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.sencha.gxt.widget.core.client.PlainTabPanel;

/**
 * @author sriram
 * 
 */
public class FileViewerWindow extends Gxt3IplantWindow {

    private PlainTabPanel tabPanel;
    protected JSONObject manifest;
    protected File file;

    public FileViewerWindow(String tag, ViewerWindowConfig config) {
        super(tag, config);
        init();
    }

    /**
     * Applies a window configuration to the window.
     * 
     * @param config
     */
    @Override
    public void setWindowConfig(WindowConfig config) {
        this.config = config;
    }

    private void init() {
        setSize("640px", "438px");
        this.file = ((ViewerWindowConfig)config).getFile();
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

    /*
     * (non-Javadoc)
     * 
     * @see org.iplantc.de.client.views.windows.IPlantWindowInterface#getWindowState()
     */
    @Override
    public JSONObject getWindowState() {
        return null;
    }

    private void getFileManifest() {
        Services.FILE_EDITOR_SERVICE.getManifest(file.getId(),
                new DiskResourceServiceCallback(null) {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    manifest = JsonUtil.getObject(result);
                    Presenter p = new FileViewerPresenter(file, manifest, ((ViewerWindowConfig)config)
                            .isShowTreeTab());
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
