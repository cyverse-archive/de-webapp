package org.iplantc.de.client.views.windows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uicommons.client.models.UserInfo;
import org.iplantc.core.uidiskresource.client.models.DiskResource;
import org.iplantc.core.uidiskresource.client.models.File;
import org.iplantc.core.uidiskresource.client.models.Folder;
import org.iplantc.core.uidiskresource.client.util.DiskResourceUtil;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.controllers.DataController;
import org.iplantc.de.client.controllers.DataMonitor;
import org.iplantc.de.client.dispatchers.WindowDispatcher;
import org.iplantc.de.client.events.DataPayloadEvent;
import org.iplantc.de.client.events.DataPayloadEventHandler;
import org.iplantc.de.client.events.DataSearchResultSelectedEvent;
import org.iplantc.de.client.events.DataSearchResultSelectedEvent.Selection;
import org.iplantc.de.client.events.DataSearchResultSelectedEventHandler;
import org.iplantc.de.client.events.ManageDataRefreshEvent;
import org.iplantc.de.client.events.ManageDataRefreshEventHandler;
import org.iplantc.de.client.events.disk.mgmt.DiskResourceSelectedEvent;
import org.iplantc.de.client.factories.EventJSONFactory.ActionType;
import org.iplantc.de.client.factories.WindowConfigFactory;
import org.iplantc.de.client.models.ClientDataModel;
import org.iplantc.de.client.models.DataWindowConfig;
import org.iplantc.de.client.models.WindowConfig;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.UserSessionServiceFacade;
import org.iplantc.de.client.utils.DataViewContextExecutor;
import org.iplantc.de.client.utils.builders.context.DataContextBuilder;
import org.iplantc.de.client.views.panels.DataDetailsPanel;
import org.iplantc.de.client.views.panels.DataMainPanel;
import org.iplantc.de.client.views.panels.DataNavigationPanel;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A window that shows a directory tree on the left panel, a directory list in the middle, and a
 * details/preview panel on the right.
 */
public class MyDataWindow extends IPlantThreePanelWindow implements DataMonitor {
    private ClientDataModel model;

    private DataNavigationPanel pnlNavigation;
    private DataMainPanel pnlMain;
    private DataDetailsPanel pnlDetails;

    protected List<HandlerRegistration> handlers;

    /**
     * {@inheritDoc}
     */
    public MyDataWindow(final String tag, final WindowConfig config) {
        super(tag, config);

        initHandlers();
        getSearhHistory();
    }

    private void initHandlers() {
        EventBus eventbus = EventBus.getInstance();

        handlers = new ArrayList<HandlerRegistration>();

        handlers.add(eventbus.addHandler(DataPayloadEvent.TYPE, new DataPayloadEventHandlerImpl()));
        handlers.add(eventbus.addHandler(ManageDataRefreshEvent.TYPE,
                new ManageDataRefreshEventHandlerImpl()));
        handlers.add(eventbus.addHandler(DataSearchResultSelectedEvent.TYPE,
                new DataSearchResultSelectedEventHandlerImpl()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initPanels() {
        pnlNavigation = new DataNavigationPanel(tag, DataNavigationPanel.Mode.EDIT);
        pnlNavigation.setMaskingParent(pnlContents);

        pnlMain = new DataMainPanel(tag, model);
        pnlMain.setMaskingParent(pnlContents);

        pnlDetails = new DataDetailsPanel(tag);
    }

    private void seed(final String username, final String json) {
        model.seed(json);
        pnlNavigation.seed(username, model);
        pnlMain.seed(model);
    }

    public void select(DiskResource resource) {
        if (pnlMain != null) {
            pnlMain.select(resource.getId(), false);
        }
        String parentFolderId = DiskResourceUtil.parseParent(resource.getId());
        Folder parentFolder = model.getFolder(parentFolderId);
        if (parentFolder != null) {
            pnlNavigation.expandFolder(parentFolder);
        }
        pnlNavigation.selectFolder(parentFolderId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void compose() {
        pnlContents.add(pnlNavigation, dataWest);
        pnlContents.add(pnlMain, dataCenter);
        pnlContents.add(pnlDetails, dataEast);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void afterRender() {
        super.afterRender();

        retrieveData(new RetrieveDataCallback());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show() {
        super.show();

        if (model != null && model.getRootFolder() != null) {
            // select node from WindowConfig
            selectConfigNode();
            setWindowViewState();
            // reset the config here so that this folder is not selected every time the window in
            // minimized and re-shown.
            config = null;

        }
    }

    @Override
    protected void doHide() {
        super.doHide();
    }

    /**
     * Applies a window configuration to the window.
     * 
     * @param config
     */
    @Override
    public void setWindowConfig(WindowConfig config) {
        if (config instanceof DataWindowConfig) {
            this.config = config;
        }
    }

    /**
     * 
     * Get user workspace's files and folder
     * 
     */
    protected void retrieveData(AsyncCallback<String> callback) {
        DiskResourceServiceFacade facade = new DiskResourceServiceFacade();

        mask(I18N.DISPLAY.loadingMask());

        facade.getHomeFolder(callback);
    }

    private final class DataSearchResultSelectedEventHandlerImpl implements
            DataSearchResultSelectedEventHandler {
        @Override
        public void onSelection(DataSearchResultSelectedEvent event) {
            if (event.getTag().equals(tag)) {
                DiskResource model = event.getModel();
                if (model != null && model instanceof Folder) {
                    pnlNavigation.selectFolder(model.getId());
                    pnlDetails.resetDeatils();
                } else {
                    if (event.getSelection().equals(Selection.LOCATION)) {
                        pnlNavigation.selectFolder(DiskResourceUtil.parseParent(model.getId()));
                        pnlMain.setSelectedResource(event.getSelectedIds());
                        pnlDetails.resetDeatils();
                        return;
                    }
                    DataViewContextExecutor executor = new DataViewContextExecutor();
                    DataContextBuilder builder = new DataContextBuilder();
                    executor.execute(builder.build(model.getId()));
                    pnlDetails.update(Arrays.asList(event.getModel()));
                }

            }
        }
    }

    private class RetrieveDataCallback implements AsyncCallback<String> {
        @Override
        public void onFailure(Throwable caught) {
            handleFailure(caught);
        }

        @Override
        public void onSuccess(String result) {
            unmask();
            seed(UserInfo.getInstance().getUsername(), result);
            // Select the folder set by the config
            selectConfigNode();
            setWindowViewState();
            // reset the config here so that this folder is not selected every time the window in
            config = null;

        }

    }

    private void getSearhHistory() {
        UserSessionServiceFacade facade = new UserSessionServiceFacade();
        facade.getSearchHistory(new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.retrieveSearchHistoryError(), caught);
            }

            @Override
            public void onSuccess(String result) {
                JSONObject obj = JsonUtil.getObject(result);
                pnlDetails.setSearchHistory(obj);

            }

        });

    }

    /**
     * Selects the folder set in the basic window config.
     * 
     * @return true if a folder was found to select, false otherwise.
     */
    private boolean selectConfigNode() {
        if (config == null || !(config instanceof DataWindowConfig)) {
            return false;
        }

        List<String> selectedIds = JsonUtil.buildStringList(((DataWindowConfig)config)
                .getDiskResourceIds());
        pnlMain.setSelectedResource(selectedIds);

        String path = ((DataWindowConfig)config).getFolderId();
        return pnlNavigation.selectFolder(path);

    }

    public void removeEventHandlers() {
        EventBus eventbus = EventBus.getInstance();

        // unregister
        for (HandlerRegistration reg : handlers) {
            eventbus.removeHandler(reg);
        }

        // clear our list
        handlers.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        super.cleanup();

        removeEventHandlers();

        pnlNavigation.cleanup();
        pnlMain.cleanup();
        pnlDetails.cleanup();
    }

    private void deleteFolders(final List<String> folders) {
        if (folders != null) {
            pnlMain.deleteDiskResources(folders);

            for (String path : folders) {
                model.deleteDiskResource(path);

                if (model.isCurrentPage(path)) {
                    Folder folder = model.getFolder(DiskResourceUtil.parseParent(path));

                    if (folder != null) {
                        EventBus.getInstance().fireEvent(new DiskResourceSelectedEvent(folder, tag));
                    }
                }
            }
        }
    }

    private void deleteFiles(final List<String> files) {
        model.deleteDiskResources(files);
        pnlMain.deleteDiskResources(files);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteResources(List<String> resources) {
        deleteFolders(resources);
        deleteFiles(resources);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rename(String id, String name) {
        if (pnlMain != null) {
            pnlMain.rename(id, name);
        }
        model.renameFolder(id, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void folderCreated(String idParentFolder, JSONObject jsonFolder) {
        Folder parentFolder = model.getFolder(idParentFolder);
        Folder folder;
        folder = new Folder(jsonFolder);
        if ((pnlNavigation != null) && pnlNavigation.isFolderLoaded(idParentFolder)) {
            folder = model.createFolder(idParentFolder, jsonFolder);
        } else {
            parentFolder.setHasSubFolders(true);
        }

        if (pnlMain != null) {
            pnlMain.addDiskResource(idParentFolder, folder);
        }

        select(folder);
    }

    private void folderMove(Map<String, String> folders) {
        if (folders != null) {
            // remove from main panel
            pnlMain.deleteDiskResources(new ArrayList<String>(folders.keySet()));

            for (String srcId : folders.keySet()) {
                String destId = folders.get(srcId);

                Folder src = model.getFolder(srcId);
                Folder dest = model.getFolder(destId);

                if (src != null && dest != null) {
                    // construct new Id
                    String newSrcId = destId + "/" + DiskResourceUtil.parseNameFromPath(srcId); //$NON-NLS-1$
                    model.moveResource(src, newSrcId, destId);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void moveResource(Map<String, String> resources) {
        if (resources != null) {
            pnlMain.deleteDiskResources(new ArrayList<String>(resources.keySet()));
            folderMove(resources);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFile(String path, File file) {
        model.createFile(path, file);

        pnlMain.addDiskResource(path, file);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fileSavedAs(String idOrig, String idParentFolder, File info) {
        addFile(idParentFolder, info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getCaption() {
        return I18N.DISPLAY.data();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getEastWidth() {
        return 180;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getWestWidth() {
        return 160;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initModel() {
        model = new ClientDataModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setInitialSize() {
        setSize(800, 410);
    }

    private void handleFailure(Throwable caught) {
        unmask();
        ErrorHandler.post(I18N.ERROR.retrieveFolderInfoFailed(), caught);
    }

    private class DataPayloadEventHandlerImpl implements DataPayloadEventHandler {
        @Override
        public void onFire(DataPayloadEvent event) {
            DataController controller = DataController.getInstance();
            controller.handleEvent(MyDataWindow.this, event.getPayload());
        }
    }

    private class ManageDataRefreshEventHandlerImpl implements ManageDataRefreshEventHandler {

        @Override
        public void onRefresh(ManageDataRefreshEvent mdre) {
            if (tag.equals(mdre.getTag())) {
                pnlNavigation.removeTreePanel();
                initModel();

                String currentFolderId = mdre.getCurrentFolderId();
                if (currentFolderId != null && !currentFolderId.isEmpty()) {
                    DataWindowConfig configData = new DataWindowConfig(config);
                    storeWindowViewState(configData);
                    configData.setFolderId(currentFolderId);
                    configData.setDiskResourceIds(mdre.getResources());
                    setWindowConfig(configData);
                }

                retrieveData(new RetrieveDataCallback());
            }
        }
    }

    @Override
    public JSONObject getWindowState() {
        // Build config data
        DataWindowConfig configData = new DataWindowConfig(config);
        storeWindowViewState(configData);

        if (pnlNavigation.getSelectedItem() != null && pnlNavigation.getSelectedItem().getId() != null) {
            configData.setFolderId(pnlNavigation.getSelectedItem().getId());
        }

        configData.setDiskResourceIds(pnlMain.getSelectedItems());

        // Build window config
        WindowConfigFactory configFactory = new WindowConfigFactory();
        JSONObject windowConfig = configFactory.buildWindowConfig(Constants.CLIENT.myDataTag(),
                configData);

        WindowDispatcher dispatcher = new WindowDispatcher(windowConfig);
        return dispatcher.getDispatchJson(Constants.CLIENT.myDataTag(), ActionType.DISPLAY_WINDOW);
    }

    @Override
    public void copyResources(Map<String, String> paths) {
        // refresh page to update with new resources
        ManageDataRefreshEvent event = new ManageDataRefreshEvent(tag, pnlMain.getCurrentPath(),
                pnlMain.getSelectedItems());
        EventBus.getInstance().fireEvent(event);

    }
}
