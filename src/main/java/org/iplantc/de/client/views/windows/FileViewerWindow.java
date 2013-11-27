package org.iplantc.de.client.views.windows;

import java.util.Date;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.resources.client.messages.I18N;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.core.uicommons.client.models.diskresources.File;
import org.iplantc.core.uidiskresource.client.services.errors.DiskResourceErrorAutoBeanFactory;
import org.iplantc.core.uidiskresource.client.services.errors.ErrorGetManifest;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.events.FileEditorWindowClosedEvent;
import org.iplantc.de.client.viewer.presenter.FileViewerPresenter;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.views.windows.configs.FileViewerWindowConfig;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
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
        setSize("670px", "400px");
        this.file = configAB.getFile();
        getFileManifest();
        if (file != null) {
            setTitle(file.getName());
        } else {
            setTitle("Untitled-" + Math.random());
        }
    }

    private void initWidget() {
        tabPanel = new PlainTabPanel();
        add(tabPanel);
        forceLayout();
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
        if (file != null) {
            EventBus eventbus = EventBus.getInstance();
            FileEditorWindowClosedEvent event = new FileEditorWindowClosedEvent(file.getId());
            eventbus.fireEvent(event);
        }
    }

    @Override
    public PlainTabPanel getWidget() {
        return tabPanel;
    }

    @Override
    public WindowState getWindowState() {
        return createWindowState(configAB);
    }

    private boolean isTreeTab(JSONObject obj) {
        if (obj == null) {
            return false;
        }
        String info_type = JsonUtil.getString(obj, "info-type");
        if (info_type == null || info_type.isEmpty()) {
            return false;
        }

        return (info_type.equalsIgnoreCase("nexus") || info_type.equalsIgnoreCase("nexml")
                || info_type.equalsIgnoreCase("newick") || info_type.equalsIgnoreCase("phyloxml"));

    }

    private void getFileManifest() {
        if (file != null) {
            Services.FILE_EDITOR_SERVICE.getManifest(file.getId(), new AsyncCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    if (result != null) {
                        manifest = JsonUtil.getObject(result);
                        FileViewer.Presenter p = new FileViewerPresenter(file, manifest,
                                isTreeTab(manifest), configAB.isEditing());
                        initWidget();
                        p.go(FileViewerWindow.this);
                    } else {
                        onFailure(null);
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    DiskResourceErrorAutoBeanFactory factory = GWT
                            .create(DiskResourceErrorAutoBeanFactory.class);
                    String message = caught.getMessage();
                    FileViewerWindow.this.hide();

                    if (JsonUtils.safeToEval(message)) {
                        AutoBean<ErrorGetManifest> errorBean = AutoBeanCodex.decode(factory,
                                ErrorGetManifest.class, message);
                        ErrorHandler.post(errorBean.as(), caught);
                    } else {
                        ErrorHandler.post(I18N.ERROR.retrieveStatFailed(), caught);
                    }
                }
            });
        } else {
            if (configAB.isEditing()) {
                JSONObject manifest = new JSONObject();
                manifest.put("content-type", new JSONString("plain"));
                FileViewer.Presenter p = new FileViewerPresenter(file, manifest, isTreeTab(manifest),
                        configAB.isEditing());
                initWidget();
                p.go(FileViewerWindow.this);

            }
        }
    }

}
