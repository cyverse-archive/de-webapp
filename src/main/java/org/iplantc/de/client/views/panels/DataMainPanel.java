package org.iplantc.de.client.views.panels;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uidiskresource.client.models.DiskResource;
import org.iplantc.core.uidiskresource.client.models.Folder;
import org.iplantc.core.uidiskresource.client.models.FolderData;
import org.iplantc.core.uidiskresource.client.util.DiskResourceUtil;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.client.events.LoadDataSearchResultsEvent;
import org.iplantc.de.client.events.LoadDataSearchResultsEventHandler;
import org.iplantc.de.client.events.disk.mgmt.DiskResourceSelectedEvent;
import org.iplantc.de.client.events.disk.mgmt.DiskResourceSelectedEventHandler;
import org.iplantc.de.client.events.disk.mgmt.NavFolderSelectedEvent;
import org.iplantc.de.client.events.disk.mgmt.NavFolderSelectedEventHandler;
import org.iplantc.de.client.models.ClientDataModel;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.utils.DataUtils;
import org.iplantc.de.client.views.DataActionsMenu;
import org.iplantc.de.client.views.MyDataGrid;
import org.iplantc.de.client.views.MyDataSearchGrid;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.dnd.GridDropTarget;
import com.extjs.gxt.ui.client.dnd.ScrollSupport;
import com.extjs.gxt.ui.client.dnd.StatusProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DataMainPanel extends AbstractDataPanel implements DataContainer {
    private MyDataGrid grid;
    private Component maskingParent;
    private List<String> selectedResourceIds;
    private boolean enableDragAndDrop = true;

    private ClientDataModel model;
    private final DataActionsMenu menuActions;
    private final DataMainToolBar toolbar;
    private MyDataGridDragSource dndSource;
    private MyDataSearchGrid searchGrid;

    public DataMainPanel(final String tag, final ClientDataModel model) {
        this(tag, model, null);
    }

    public DataMainPanel(final String tag, final ClientDataModel model, List<String> selectedResourceId) {
        super(tag);

        this.model = model;

        this.setSelectedResource(selectedResourceId);
        menuActions = new DataActionsMenu(tag);

        toolbar = new DataMainToolBar(tag, this, menuActions);
        setTopComponent(toolbar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init() {
        setLayout(new FitLayout());
        setHeaderVisible(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void compose() {
        // intentionally do nothing.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerHandlers() {
        super.registerHandlers();

        EventBus eventbus = EventBus.getInstance();

        handlers.add(eventbus.addHandler(DiskResourceSelectedEvent.TYPE,
                new DiskResourceSelectedEventHandlerImpl()));

        handlers.add(eventbus.addHandler(NavFolderSelectedEvent.TYPE,
                new NavFolderSelectedEventHandlerImpl()));

        handlers.add(eventbus.addHandler(LoadDataSearchResultsEvent.TYPE,
                new LoadDataSearchResultsEventHandlerImpl()

        ));
    }

    private void initDragAndDrop() {
        dndSource = new MyDataGridDragSource(grid);

        GridDropTarget target = new GridDropTargetImpl(grid);
        target.setAllowSelfAsSource(true);
    }

    /**
     * Disables Drag-and-Drop in the main data panel. Must be set before seed is called.
     */
    public void disableDragAndDrop() {
        enableDragAndDrop = false;
    }

    public void seed(final ClientDataModel model) {
        seed(model, tag, new SelectionChangeListenerImpl());
    }

    public void seed(final ClientDataModel model, String tag, Listener<BaseEvent> selctionChangeListener) {

        gridCleanUp();

        toolbar.cleanup();
        toolbar.registerHandlers();

        if (searchGrid != null && searchGrid.isAttached()) {
            remove(searchGrid);
            searchGrid = null;
        }
        if (grid != null) {
            remove(grid);
        }

        if (model != null) {
            this.model = model;
            grid = MyDataGrid.createInstance(model.getRootFolderId(), tag, model, menuActions);

            if (grid.isRendered()) {
                grid.setSize(getWidth(), getHeight());
            }

            grid.getView().setShowDirtyCells(false);

            // notify when selection changes
            if (selctionChangeListener != null) {
                grid.getSelectionModel().addListener(Events.SelectionChange, selctionChangeListener);
            }

            grid.getStore().setKeyProvider(new ModelKeyProvider<DiskResource>() {
                @Override
                public String getKey(DiskResource resource) {
                    return resource.getId();
                }
            });

            add(grid);

            if (enableDragAndDrop) {
                initDragAndDrop();
            }

            layout();
        }
    }

    @Override
    public void cleanup() {
        gridCleanUp();
        toolbar.cleanup();
        super.cleanup();
    }

    private void gridCleanUp() {
        if (grid != null) {
            grid.cleanup();
            grid.getSelectionModel().removeAllListeners();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCurrentPath() {
        return (model == null) ? null : model.getCurrentPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DiskResource> getSelectedItems() {
        return (grid == null) ? new ArrayList<DiskResource>() : grid.getSelectionModel()
                .getSelectedItems();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListStore<DiskResource> getDataStore() {
        return (grid == null) ? new ListStore<DiskResource>() : grid.getStore();
    }

    /**
     * Sets the parent component of this panel that should be masked while the data grid is updating.
     * 
     * @param parent
     */
    public void setMaskingParent(Component parent) {
        maskingParent = parent;
        toolbar.setMaskingParent(parent);
    }

    private void updateGrid() {
        if (grid == null) {
            return;
        }

        List<Component> items = getItems();
        if (items.contains(searchGrid)) {
            remove(searchGrid);
            add(grid);
        } else if (!items.contains(grid)) {
            add(grid);
        }

        ListStore<DiskResource> store = grid.getStore();

        // start with an empty grid
        store.removeAll();
        grid.getView().setEmptyText(I18N.DISPLAY.noItemsToDisplay());

        FolderData page = model.getPage();

        if (page != null) {
            List<DiskResource> resources = page.getResources();

            for (DiskResource resource : resources) {
                store.add(resource);
            }

            if (selectedResourceIds != null) {
                for (String id : selectedResourceIds) {
                    select(id, true);
                }
            }

            grid.getView().refresh(true);
            grid.getView().layout();
            layout(true);
        }
    }

    private void buildPage(final String path) {
        // mask the entire data window while the folder contents load so that
        // the user doesn't click another folder while this one is still
        // loading, to prevent race-conditions where the wrong content might
        // load in the end.
        if (maskingParent != null) {
            maskingParent.mask(I18N.DISPLAY.loadingMask());
        }

        DiskResourceServiceFacade facade = new DiskResourceServiceFacade();
        facade.getFolderContents(path, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                if (maskingParent != null) {
                    maskingParent.unmask();
                }

                ErrorHandler.post(I18N.ERROR.retrieveFolderInfoFailed(), caught);
            }

            @Override
            public void onSuccess(String json) {
                model.updatePage(json);
                updateGrid();

                if (maskingParent != null) {
                    maskingParent.unmask();
                }
            }
        });
    }

    /**
     * Sets the selection mode of the data grid. Must be called after seed is called.
     * 
     * @param mode SelectionMode to set.
     */
    public void setSelectionMode(SelectionMode mode) {
        if (grid != null) {
            grid.getSelectionModel().setSelectionMode(mode);
        }
    }

    /**
     * Tells the panel which file should be shown as "selected."
     * 
     * @param resourceId the file / folder in the data browser grid to select
     */
    public void select(String resourceId, boolean keepExisting) {
        if (grid != null) {
            DiskResource exists = findDiskResource(resourceId);
            if (exists != null) {
                grid.getSelectionModel().select(exists, keepExisting);
            }
        }
    }

    public void addDiskResource(final String path, final DiskResource resource) {
        // are we in the folder that's the parent of the created resource?
        if (model.isCurrentPage(path)) {
            // first remove any potentially outdated resource models
            ListStore<DiskResource> store = grid.getStore();
            for (DiskResource model : store.getModels()) {
                if (model.getId().equals(resource.getId())) {
                    store.remove(model);
                }
            }

            store.add(resource);
            grid.getView().focusRow(store.indexOf(resource));
        }
    }

    private void updatePath(final String pathOrig, final String path) {
        if (model == null) {
            return;
        }

        if (model.isCurrentPage(pathOrig)) {
            // The currently viewed folder was renamed.
            model.getPage().setPath(path);

            // Update the IDs of all disk resources loaded in the grid's store.
            ListStore<DiskResource> store = grid.getStore();
            for (DiskResource resource : store.getModels()) {
                resource.setId(path + "/" + DiskResourceUtil.parseNameFromPath(resource.getId())); //$NON-NLS-1$
            }
        } else if (model.isCurrentPage(DiskResourceUtil.parseParent(path))) {
            // A folder inside the currently viewed folder was renamed.
            DiskResource resource = findDiskResource(pathOrig);

            if (resource != null) {
                resource.setId(path);
                resource.setName(DiskResourceUtil.parseNameFromPath(path));

                grid.getStore().update(resource);
            }
        }
    }

    public void rename(final String nameOrig, final String nameNew) {
        if (grid != null) {
            DiskResource resource = findDiskResource(nameOrig);

            if (resource != null) {
                // drop the path from our name
                resource.setName(DiskResourceUtil.parseNameFromPath(nameNew));

                // our id is the fully qualified path and name
                resource.setId(nameNew);

                grid.getStore().update(resource);
                if (resource instanceof Folder) {
                    updatePath(nameOrig, nameNew);
                }
            }

        }
    }

    /**
     * Deselects the disk resources with the given paths in the grid, then removes them from the grid.
     * 
     * @param paths
     */
    public void deleteDiskResources(final List<String> paths) {
        if (paths != null && grid != null) {
            List<DiskResource> resources = new ArrayList<DiskResource>();
            for (String path : paths) {
                DiskResource resource = findDiskResource(path);

                if (resource != null) {
                    resources.add(resource);
                }
            }

            grid.getSelectionModel().deselect(resources);

            for (DiskResource resource : resources) {
                grid.getStore().remove(resource);
            }
        }
    }

    /**
     * Removes the disk resource with the given path from the grid.
     * 
     * @param path
     */
    public void deleteDiskResource(final String path) {
        DiskResource resource = findDiskResource(path);

        if (resource != null) {
            grid.getStore().remove(resource);
        }
    }

    private DiskResource findDiskResource(final String id) {
        if (grid != null) {
            return grid.getStore().findModel(id);
        }

        return null;
    }

    /**
     * @param selectedResource the selectedResource to set
     */
    public void setSelectedResource(List<String> selectedResourceIds) {
        this.selectedResourceIds = selectedResourceIds;
    }

    private final class NavFolderSelectedEventHandlerImpl implements NavFolderSelectedEventHandler {
        @Override
        public void onSelected(NavFolderSelectedEvent event) {
            DiskResource resource = event.getResource();

            if (resource instanceof Folder && tag.equals(event.getTag())) {
                buildPage(resource.getId());
            }

        }
    }

    private final class LoadDataSearchResultsEventHandlerImpl implements
            LoadDataSearchResultsEventHandler {
        @Override
        public void onLoad(LoadDataSearchResultsEvent event) {
            mask(I18N.DISPLAY.loadingMask());

            // swap the view
            if (grid != null && grid.isAttached()) {
                remove(grid);
            }

            if (searchGrid != null && searchGrid.isAttached()) {
                remove(searchGrid);
            }
            searchGrid = MyDataSearchGrid.createInstance(event.getSearchTerm(), event.getResults(), tag);

            add(searchGrid);
            layout(true);
            unmask();
        }
    }

    private class DiskResourceSelectedEventHandlerImpl implements DiskResourceSelectedEventHandler {
        @Override
        public void onSelected(DiskResourceSelectedEvent event) {
            DiskResource resource = event.getResource();

            if (resource instanceof Folder && tag.equals(event.getTag())) {
                buildPage(resource.getId());
            }
        }
    }

    private class SelectionChangeListenerImpl implements Listener<BaseEvent> {
        @Override
        public void handleEvent(BaseEvent be) {
            EventBus eventbus = EventBus.getInstance();
            List<DiskResource> selected = grid.getSelectionModel().getSelectedItems();

            DiskResourceSelectionChangedEvent event = new DiskResourceSelectionChangedEvent(tag,
                    selected, model.getCurrentPath());
            eventbus.fireEvent(event);
        }
    }

    /**
     * A GridDragSource implementation that will automatically select a disk resource on drag-start if
     * that resource is not already selected, and deselect automatically selected resources on drag
     * failures.
     * 
     * @author psarando
     * 
     */
    private class MyDataGridDragSource extends GridDragSource {
        private ModelData autoSelectedModel;

        public MyDataGridDragSource(Grid<DiskResource> grid) {
            super(grid);

            setStatusText(I18N.DISPLAY.dataDragDropStatusText());
        }

        /**
         * Deselects resources automatically selected at drag-start.
         */
        private void deselectAutoSelection() {
            if (autoSelectedModel != null) {
                grid.getSelectionModel().deselect(autoSelectedModel);
            }

            autoSelectedModel = null;
        }

        @Override
        protected void onDragStart(DNDEvent event) {
            // if the row at drag-start is not selected, select only that row so it can be dragged.
            Element targetRow = grid.getView().findRow(event.getDragEvent().getStartElement()).cast();
            if (targetRow != null) {
                GridSelectionModel<ModelData> selectionModel = grid.getSelectionModel();
                ModelData model = grid.getStore().getAt(grid.getView().findRowIndex(targetRow));

                if (!selectionModel.isSelected(model)) {
                    autoSelectedModel = model;
                    selectionModel.select(autoSelectedModel, false);
                }
            }

            super.onDragStart(event);
        }

        @Override
        protected void onDragCancelled(DNDEvent event) {
            deselectAutoSelection();
        }

        @Override
        public void onDragDrop(DNDEvent event) {
            // intentionally do nothing
        }

        @Override
        protected void onDragFail(DNDEvent event) {
            deselectAutoSelection();
        }
    }

    private class GridDropTargetImpl extends GridDropTarget {
        private ScrollSupport scrollSupport;
        private int dropIndex;

        public GridDropTargetImpl(Grid<DiskResource> grid) {
            super(grid);
        }

        @Override
        protected void onDragCancelled(DNDEvent event) {
            scrollSupport.stop();
        }

        @Override
        protected void onDragEnter(DNDEvent event) {
            if (scrollSupport == null) {
                scrollSupport = new ScrollSupport(grid.getView().getScroller());
            } else if (scrollSupport.getScrollElement() == null) {
                scrollSupport.setScrollElement(grid.getView().getScroller());
            }

            scrollSupport.start();
        }

        @Override
        protected void onDragFail(DNDEvent event) {
            scrollSupport.stop();
        }

        @Override
        protected void onDragLeave(DNDEvent e) {
            scrollSupport.stop();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void onDragMove(DNDEvent event) {
            super.onDragMove(event);

            // super may have cancelled the event
            if (event.isCancelled()) {
                return;
            }

            DiskResource target = null;
            Element targetRow = grid.getView().findRow(event.getTarget()).cast();

            if (targetRow != null) {
                dropIndex = grid.getView().findRowIndex(targetRow);
                target = (DiskResource)(grid.getStore().getAt(dropIndex));
            }

            StatusProxy eventStatus = event.getStatus();
            List<DiskResource> dragSources = (List<DiskResource>)event.getData();

            // Reset the status text, in case a previous onDragMove set it to the permission error text.
            eventStatus.update(Format.substitute(dndSource.getStatusText(), dragSources.size()));

            // if the target is not a folder, don't allow a drop there
            if (!(target instanceof Folder)) {
                event.setCancelled(true);
                eventStatus.setStatus(false);
                return;
            }

            // check source and target permissions
            if (!DataUtils.isMovable(dragSources) || !target.getPermissions().isWritable()) {
                eventStatus.setStatus(false);
                eventStatus.update(I18N.DISPLAY.permissionErrorMessage());
                event.setCancelled(true);
                return;
            }

            for (DiskResource src : dragSources) {
                // if the target folder is one of the src folders, don't allow a drop there
                if (target == src) {
                    event.setCancelled(true);
                    eventStatus.setStatus(false);
                }
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onDragDrop(DNDEvent event) {
            scrollSupport.stop();

            // get our dest folder
            DiskResource res = (DiskResource)(grid.getStore().getAt(dropIndex));
            String idDestFolder = res.getId();

            // call service to move files and folders
            DiskResourceServiceFacade facade = new DiskResourceServiceFacade(maskingParent);
            facade.moveDiskResources((List<DiskResource>)event.getData(), idDestFolder);

            dropIndex = -1;
        }

    }
}
