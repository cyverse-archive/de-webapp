package org.iplantc.de.client.views.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.iplantc.core.uidiskresource.client.models.DiskResource;
import org.iplantc.core.uidiskresource.client.models.Permissions;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.images.Resources;
import org.iplantc.de.client.models.Collaborator;
import org.iplantc.de.client.models.DataSharing;
import org.iplantc.de.client.models.Sharing;
import org.iplantc.de.client.views.dialogs.SelectCollaboratorsDialog;

import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * A panel to display a list of sharee and their permissions
 * 
 * @author sriram
 * 
 */
public class SharePanel extends ContentPanel {
    private static final String ID_PERM_GROUP = "idPermGroup"; //$NON-NLS-1$
    private EditorGrid<DataSharing> grid;
    private LayoutContainer explainPanel;
    private final FastMap<List<DataSharing>> unshareList;
    private final FastMap<DiskResource> resources;
    private ToolBar toolbar;
    private static final String ID_BTN_ADD_COLLABS = "idBtnAddCollabs"; //$NON-NLS-1$
    private static final String ID_BTN_REMOVE = "idBtnRemove"; //$NON-NLS-1$
    private FastMap<List<DataSharing>> sharingMap;
    private FastMap<List<DataSharing>> originalList;

    public SharePanel(List<DiskResource> resourceList) {
        unshareList = new FastMap<List<DataSharing>>();
        resources = new FastMap<DiskResource>();

        for (DiskResource data : resourceList) {
            resources.put(data.getId(), data);
        }

        init();
    }

    private void init() {
        setLayout(new FitLayout());

        initGrid();

        add(grid);
        addToolBar();
        addExplainPanel();
    }

    private void initGrid() {
        ListStore<DataSharing> store = new ListStore<DataSharing>();
        store.setKeyProvider(new ModelKeyProvider<DataSharing>() {
            @Override
            public String getKey(DataSharing model) {
                return model.getKey();
            }
        });

        grid = new EditorGrid<DataSharing>(store, buildColumnModel());

        grid.setClicksToEdit(ClicksToEdit.ONE);

        GridView view = grid.getView();
        view.setEmptyText(I18N.DISPLAY.sharePanelEmptyText());
        view.setForceFit(true);

        grid.addListener(Events.AfterEdit, new Listener<GridEvent<Sharing>>() {
            @Override
            public void handleEvent(GridEvent<Sharing> be) {
                // edited row can only be of instance DataSharing
                Object value = be.getValue();
                if (value != null) {
                    DataSharing ds = (DataSharing)be.getRecord().getModel();
                    updatePermissions(value.toString(), ds.getUserName());
                }
            }
        });
    }

    private void addExplainPanel() {
        explainPanel = new HorizontalPanel();

        explainPanel.add(new LabelToolItem(I18N.DISPLAY.variablePermissionsNotice() + ":")); //$NON-NLS-1$

        Button explainBtn = new Button(I18N.DISPLAY.explain(), new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                ArrayList<DataSharing> shares = new ArrayList<DataSharing>();
                for (String user : sharingMap.keySet()) {
                    shares.addAll(sharingMap.get(user));
                }

                ShareBreakdownDialog explainDlg = new ShareBreakdownDialog(shares);
                explainDlg.show();
            }
        });

        explainPanel.add(explainBtn);

        ToolBar topBar = new ToolBar();
        topBar.add(new LabelToolItem(I18N.DISPLAY.whoHasAccess() + ":")); //$NON-NLS-1$
        topBar.add(new FillToolItem());
        topBar.add(explainPanel);

        setTopComponent(topBar);
    }

    private void addToolBar() {
        toolbar = new ToolBar();

        toolbar.add(buildAddCollabsButton());

        setBottomComponent(toolbar);
    }

    private Button buildAddCollabsButton() {
        Button addCollabsBtn = new Button(I18N.DISPLAY.addCollabs(),
                AbstractImagePrototype.create(Resources.ICONS.viewCurrentCollabs()));
        addCollabsBtn.setId(ID_BTN_ADD_COLLABS);
        addCollabsBtn.addSelectionListener(new AddCollaboratorsListener());

        return addCollabsBtn;
    }

    private class AddCollaboratorsListener extends SelectionListener<ButtonEvent> {
        @Override
        public void componentSelected(ButtonEvent ce) {
            final SelectCollaboratorsDialog sd = new SelectCollaboratorsDialog();
            sd.getDoneButton().setText(I18N.DISPLAY.add());
            sd.getDoneButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    List<Collaborator> selectedCollaborators = sd.getSelectedCollaborators();
                    if (selectedCollaborators != null) {
                        for (Collaborator user : selectedCollaborators) {
                            addCollaborator(user);
                        }
                    }
                }
            });
            sd.showCurrentCollborators();
            sd.show();
        }

    }

    private void addCollaborator(Collaborator user) {
        String userName = user.getUserName();
        if (sharingMap.get(userName) == null) {
            List<DataSharing> shareList = new ArrayList<DataSharing>();
            DataSharing displayShare = null;

            for (String path : resources.keySet()) {
                DataSharing share = new DataSharing(user, new Permissions(true, false, false), path);
                shareList.add(share);

                if (displayShare == null) {
                    displayShare = share.copy();
                    grid.getStore().add(displayShare);
                }
            }

            sharingMap.put(userName, shareList);
        }
    }

    public void loadSharingData(FastMap<List<DataSharing>> sharingMap) {
        this.sharingMap = sharingMap;
        originalList = new FastMap<List<DataSharing>>();

        ListStore<DataSharing> store = grid.getStore();
        store.removeAll();
        explainPanel.hide();

        for (String userName : sharingMap.keySet()) {
            List<DataSharing> dataShares = sharingMap.get(userName);

            if (dataShares != null && !dataShares.isEmpty()) {
                List<DataSharing> newList = new ArrayList<DataSharing>();
                for (DataSharing share : dataShares) {
                    DataSharing copyShare = share.copy();
                    newList.add(copyShare);
                }
                originalList.put(userName, newList);

                // Add a dummy display share to the grid.
                DataSharing displayShare = dataShares.get(0).copy();
                if (hasVaryingPermissions(dataShares)) {
                    // Set the display permission to "varies" if this user's share list has varying
                    // permissions.
                    displayShare.setDisplayPermission(I18N.DISPLAY.varies());
                    explainPanel.show();
                }

                store.add(displayShare);
            }
        }
    }

    private ColumnModel buildColumnModel() {
        ColumnConfig sharee = new ColumnConfig(DataSharing.USER, I18N.DISPLAY.name(), 170);
        sharee.setMenuDisabled(true);

        ColumnConfig permissions = new ColumnConfig(DataSharing.DISPLAY_PERMISSION,
                I18N.DISPLAY.permissions(), 100);
        permissions.setEditor(buildPermissionsEditor());
        permissions.setMenuDisabled(true);

        ColumnConfig actions = new ColumnConfig("actions", "", 25); //$NON-NLS-1$ //$NON-NLS-2$
        actions.setFixed(true);
        actions.setMenuDisabled(true);
        actions.setSortable(false);
        actions.setRenderer(new GridCellRenderer<DataSharing>() {

            @Override
            public Object render(final DataSharing model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<DataSharing> store, Grid<DataSharing> grid) {
                Button remove = new Button();
                remove.setIcon(AbstractImagePrototype.create(Resources.ICONS.cancel()));
                remove.setId(ID_BTN_REMOVE + model.getKey());
                remove.addSelectionListener(new SelectionListener<ButtonEvent>() {

                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        removeModel(model);
                        checkExplainPanelVisibility();
                    }
                });

                return remove;
            }
        });

        return new ColumnModel(Arrays.asList(sharee, permissions, actions));
    }

    private CellEditor buildPermissionsEditor() {
        final SimpleComboBox<String> combo = buildPermissionsCombo();

        CellEditor editor = new CellEditor(combo) {
            @Override
            public Object preProcessValue(Object value) {
                if (value == null) {
                    return value;
                }
                return combo.findModel(value.toString());
            }

            @Override
            public Object postProcessValue(Object value) {
                if (value == null) {
                    return value;
                }
                return ((ModelData)value).get("value"); //$NON-NLS-1$
            }
        };

        return editor;

    }

    private SimpleComboBox<String> buildPermissionsCombo() {
        final SimpleComboBox<String> combo = new SimpleComboBox<String>();
        combo.setId(ID_PERM_GROUP);
        combo.setForceSelection(true);
        combo.setAllowBlank(false);
        combo.add(I18N.DISPLAY.read());
        combo.add(I18N.DISPLAY.write());
        combo.add(I18N.DISPLAY.own());
        combo.setEditable(false);

        combo.setTriggerAction(TriggerAction.ALL);
        return combo;
    }

    /**
     * 
     * 
     * @return the sharing list
     */
    public FastMap<List<DataSharing>> getSharingMap() {
        FastMap<List<DataSharing>> sharingList = new FastMap<List<DataSharing>>();
        for (DataSharing share : grid.getStore().getModels()) {
            String userName = share.getUserName();
            List<DataSharing> dataShares = sharingMap.get(userName);
            List<DataSharing> updatedSharingList = getUpdatedSharingList(userName, dataShares);
            if (updatedSharingList != null && updatedSharingList.size() > 0) {
                sharingList.put(userName, updatedSharingList);
            }
        }

        return sharingList;
    }

    /**
     * check the list with original to see if things have changed. ignore unchanged records
     * 
     * @param userName
     * @param list
     * @return
     */
    private List<DataSharing> getUpdatedSharingList(String userName, List<DataSharing> list) {
        List<DataSharing> updateList = new ArrayList<DataSharing>();
        if (list != null && userName != null) {
            List<DataSharing> fromOriginal = originalList.get(userName);

            if (fromOriginal == null || fromOriginal.isEmpty()) {
                updateList = list;
            } else {
                for (DataSharing s : list) {
                    if (!fromOriginal.contains(s)) {
                        updateList.add(s);
                    }
                }
            }
        }

        return updateList;
    }

    /**
     * check if a sharing recored originally existed. Needed to remove false submission of unshare list
     * 
     * @return
     */
    private boolean isExistedOriginally(DataSharing s) {
        String userName = s.getUserName();
        List<DataSharing> fromOriginal = originalList.get(userName);
        if (fromOriginal != null && fromOriginal.contains(s)) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * @return the unshareList
     */
    public FastMap<List<DataSharing>> getUnshareList() {
        return unshareList;
    }

    private void updatePermissions(String perm, String username) {
        List<DataSharing> models = sharingMap.get(username);
        if (models != null) {
            boolean own = perm.equals(I18N.DISPLAY.own());
            boolean write = own || perm.equals(I18N.DISPLAY.write());
            boolean read = true;

            for (DataSharing share : models) {
                if (own) {
                    share.setOwner(true);
                } else if (write) {
                    share.setWritable(true);
                } else {
                    share.setReadable(true);
                }
            }

            if (resources.size() != models.size()) {
                Collaborator user = models.get(0).getCollaborator();
                Permissions perms = new Permissions(read, write, own);

                for (String path : resources.keySet()) {
                    boolean shared = false;
                    for (DataSharing existingShare : models) {
                        if (path.equals(existingShare.getPath())) {
                            shared = true;
                            break;
                        }
                    }

                    if (!shared) {
                        models.add(new DataSharing(user, perms, path));
                    }
                }
            }

            checkExplainPanelVisibility();
        }
    }

    private void removeModel(DataSharing model) {
        // prepared unshared list here
        ListStore<DataSharing> store = grid.getStore();
        String userName = model.getUserName();
        List<DataSharing> list = unshareList.get(userName);
        if (list == null) {
            list = new ArrayList<DataSharing>();
        }

        DataSharing sharing = store.findModel(model);
        if (sharing != null) {
            List<DataSharing> removeList = sharingMap.get(userName);
            for (DataSharing remItem : removeList) {
                if (isExistedOriginally(remItem)) {
                    list.add(remItem);
                }
            }

            store.remove(sharing);
        }

        if (!list.isEmpty()) {
            unshareList.put(userName, list);
        }
    }

    /**
     * Checks if the explainPanel should be hidden after permissions have been updated or removed.
     */
    private void checkExplainPanelVisibility() {
        if (explainPanel.isVisible()) {
            boolean permsVary = false;

            for (DataSharing dataShare : grid.getStore().getModels()) {
                permsVary = hasVaryingPermissions(sharingMap.get(dataShare.getUserName()));

                if (permsVary) {
                    // Stop checking after the first user is found with variable permissions.
                    break;
                }
            }

            if (!permsVary) {
                explainPanel.hide();
            }
        }
    }

    /**
     * @param dataShares
     * @return true if the given dataShares list has a different size than the resources list, or if not
     *         every permission in the given dataShares list is the same; false otherwise.
     */
    private boolean hasVaryingPermissions(List<DataSharing> dataShares) {
        if (dataShares == null || dataShares.size() != resources.size()) {
            return true;
        } else {
            String displayPermission = dataShares.get(0).getDisplayPermission();

            for (DataSharing share : dataShares) {
                if (!displayPermission.equals(share.getDisplayPermission())) {
                    return true;
                }
            }
        }

        return false;
    }
}
