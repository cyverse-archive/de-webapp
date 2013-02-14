/**
 *
 */
package org.iplantc.de.client.views.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.iplantc.core.client.widgets.Hyperlink;
import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uidiskresource.client.models.DiskResource;
import org.iplantc.core.uidiskresource.client.models.Folder;
import org.iplantc.core.uidiskresource.client.models.Permissions;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.models.Collaborator;
import org.iplantc.de.client.models.DataSharing;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.utils.CollaboratorsUtil;
import org.iplantc.de.client.views.panels.SharePanel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.GridViewConfig;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author sriram
 * 
 */
public class DataSharingDialog extends Dialog {

    private final List<DiskResource> resources;

    private BorderLayout layout;

    private Grid<DiskResource> diskResourceGrid;

    private FastMap<List<JSONObject>> sharingList;
    private FastMap<List<DataSharing>> dataSharingMap;

    private SharePanel sharePanel;

    public DataSharingDialog(List<DiskResource> resources) {
        this.resources = resources;
        setSize(600, 500);
        setHeading(I18N.DISPLAY.manageSharing());
        setButtons();
    }

    private void setButtons() {
        setButtons(Dialog.OKCANCEL);
        setButtonAlign(HorizontalAlignment.RIGHT);
        Button okButton = getOkButton();
        okButton.setText(I18N.DISPLAY.done());
        okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                JSONObject requestBody = buildSharingJson();
                if (requestBody != null) {
                    callSharingService(requestBody);
                }
                JSONObject unshareRequestBody = buildUnSharingJson();
                if (unshareRequestBody != null) {
                    callUnshareService(unshareRequestBody);
                }
                if (requestBody != null || unshareRequestBody != null) {
                    MessageBox.info(I18N.DISPLAY.share() + "/ " + I18N.DISPLAY.unshare(), //$NON-NLS-1$
                            I18N.DISPLAY.sharingCompleteMsg(), null);
                }

            }
        });
    }

    public void initView() {
        initLayout();
        setHideOnButtonClick(true);
        setModal(true);
        setResizable(false);
        layout();
    }

    private void initLayout() {
        layout = new BorderLayout();
        setLayout(layout);

        buildDataPanel();
        buildSharePanel();
    }

    private Button getOkButton() {
        return getButtonById(Dialog.OK);
    }

    private void buildDataPanel() {
        ContentPanel dataPanel = new ContentPanel();
        dataPanel.setLayout(new FitLayout());
        dataPanel.setHeading(I18N.DISPLAY.selectedFilesFolders());
        // TODO temp. remove help text.
        // ToolButton helpBtn = new ToolButton("x-tool-help"); //$NON-NLS-1$
        // helpBtn.setToolTip(buildHelpToolTip(I18N.HELP.shareDiskResourceHelp()));
        // dataPanel.getHeader().addTool(helpBtn);
        dataPanel.add(buildDiskResourceGrid());

        BorderLayoutData data = new BorderLayoutData(LayoutRegion.NORTH, 150, 125, 300);
        data.setMargins(new Margins(10));
        data.setSplit(true);
        add(dataPanel, data);
    }

    private void buildSharePanel() {
        sharePanel = new SharePanel(resources);
        sharePanel.setHeaderVisible(false);
        // TODO temp. remove help text.
        // ToolButton helpBtn = new ToolButton("x-tool-help"); //$NON-NLS-1$
        // helpBtn.setToolTip(buildHelpToolTip(I18N.HELP.sharingPermissionsHelp()));
        // sharePanel.getHeader().addTool(helpBtn);

        getUserPermissionsInfo();

        BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER);
        data.setMargins(new Margins(10));
        data.setSplit(true);
        data.setCollapsible(false);
        add(sharePanel, data);
        layout();
    }

    private ToolTipConfig buildHelpToolTip(String helpText) {
        ToolTipConfig ttc = getToolTipConfig();
        ttc.setTitle(I18N.DISPLAY.help());
        ttc.setText(helpText);
        return ttc;
    }

    private ToolTipConfig getToolTipConfig() {
        ToolTipConfig config = new ToolTipConfig();
        config.setMouseOffset(new int[] {0, 0});
        config.setAnchor("left"); //$NON-NLS-1$
        config.setCloseable(true);
        return config;
    }

    private GridViewConfig buildGridViewConfig() {
        GridViewConfig config = new GridViewConfig() {

            @Override
            public String getRowStyle(ModelData model, int rowIndex, ListStore<ModelData> ds) {
                return "iplantc-select-grid"; //$NON-NLS-1$
            }

        };

        return config;
    }

    private void getUserPermissionsInfo() {
        DiskResourceServiceFacade facade = new DiskResourceServiceFacade();
        sharePanel.mask(I18N.DISPLAY.loadingMask());
        facade.getPermissions(buildPermissionsRequestBody(), new LoadPermissionsCallback());
    }

    private void loadPermissions(String path, JSONArray user_arr) {
        for (int i = 0; i < user_arr.size(); i++) {
            JSONObject userPermission = JsonUtil.getObjectAt(user_arr, i);
            JSONObject perm = JsonUtil.getObject(userPermission, "permissions"); //$NON-NLS-1$
            String userName = JsonUtil.getString(userPermission, "user"); //$NON-NLS-1$

            List<JSONObject> shareList = sharingList.get(userName);
            if (shareList == null) {
                shareList = new ArrayList<JSONObject>();
                sharingList.put(userName, shareList);
            }
            perm.put("path", new JSONString(path)); //$NON-NLS-1$
            shareList.add(perm);
        }

    }

    private JSONObject buildPermissionsRequestBody() {
        JSONObject obj = new JSONObject();
        JSONArray ids = new JSONArray();
        for (int i = 0; i < resources.size(); i++) {
            ids.set(i, new JSONString(resources.get(i).getId()));
        }
        obj.put("paths", ids); //$NON-NLS-1$
        return obj;
    }

    private void callSharingService(JSONObject obj) {
        DiskResourceServiceFacade facade = new DiskResourceServiceFacade();
        facade.shareDiskResource(obj, new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                // do nothing intentionally
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);

            }
        });
    }

    private void callUnshareService(JSONObject obj) {
        DiskResourceServiceFacade facade = new DiskResourceServiceFacade();
        facade.unshareDiskResource(obj, new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                // do nothing
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);

            }
        });
    }

    private Grid<DiskResource> buildDiskResourceGrid() {
        diskResourceGrid = new Grid<DiskResource>(new ListStore<DiskResource>(),
                buildDiskResourceColumnModel());
        diskResourceGrid.getStore().add(resources);
        GridView view = diskResourceGrid.getView();
        view.setViewConfig(buildGridViewConfig());
        view.setForceFit(true);

        return diskResourceGrid;
    }

    private ColumnModel buildDiskResourceColumnModel() {
        // build column configs and add them to a list for the ColumnModel.
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        ColumnConfig name = new ColumnConfig(DiskResource.NAME, I18N.DISPLAY.name(), 235);
        name.setRenderer(new DiskResourceNameCellRenderer());
        columns.addAll(Arrays.asList(name));

        return new ColumnModel(columns);
    }

    private final class LoadPermissionsCallback implements AsyncCallback<String> {
        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(caught);
            sharePanel.unmask();
        }

        @Override
        public void onSuccess(String result) {
            JSONArray permissionsArray = JsonUtil.getArray(JsonUtil.getObject(result), "paths"); //$NON-NLS-1$
            if (permissionsArray != null) {
                sharingList = new FastMap<List<JSONObject>>();
                for (int i = 0; i < permissionsArray.size(); i++) {
                    JSONObject user_perm_obj = permissionsArray.get(i).isObject();
                    String path = JsonUtil.getString(user_perm_obj, "path"); //$NON-NLS-1$
                    JSONArray user_arr = JsonUtil.getArray(user_perm_obj, "user-permissions"); //$NON-NLS-1$
                    loadPermissions(path, user_arr);
                }

                final List<String> usernames = new ArrayList<String>();
                usernames.addAll(sharingList.keySet());
                CollaboratorsUtil.getUserInfo(usernames, new AsyncCallback<FastMap<Collaborator>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        // TODO Auto-generated method stub
                        ErrorHandler.post(caught);
                    }

                    @Override
                    public void onSuccess(FastMap<Collaborator> results) {
                        dataSharingMap = new FastMap<List<DataSharing>>();
                        for (String userName : usernames) {
                            Collaborator user = results.get(userName);
                            if (user == null) {
                                user = new Collaborator(null, userName, userName, null, null);
                            }

                            List<DataSharing> dataShares = new ArrayList<DataSharing>();

                            dataSharingMap.put(userName, dataShares);

                            for (JSONObject share : sharingList.get(userName)) {
                                String path = JsonUtil.getString(share, "path"); //$NON-NLS-1$
                                DataSharing dataSharing = new DataSharing(user, new Permissions(share),
                                        path);
                                dataShares.add(dataSharing);
                            }
                        }

                        sharePanel.loadSharingData(dataSharingMap);
                        sharePanel.unmask();
                    }
                });
            }
        }
    }

    /**
     * A custom renderer that renders folder / file names as hyperlink
     * 
     * @author sriram
     * 
     */
    class DiskResourceNameCellRenderer implements GridCellRenderer<DiskResource> {
        @Override
        public Object render(final DiskResource model, String property, ColumnData config, int rowIndex,
                int colIndex, ListStore<DiskResource> store, final Grid<DiskResource> grid) {
            Hyperlink link = null;

            if (model instanceof Folder) {
                link = new Hyperlink("<img src='./gxt/images/default/tree/folder.gif'/>&nbsp;" //$NON-NLS-1$
                        + model.getName(), "mydata_name"); //$NON-NLS-1$
            } else {
                link = new Hyperlink(
                        "<img src='./images/file.gif'/>&nbsp;" + model.getName(), "mydata_name"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            link.setToolTip(model.getId());
            link.setWidth(model.getName().length());
            link.setOnMouseOverStyle("text-decoration", "none"); //$NON-NLS-1$ //$NON-NLS-2$
            return link;
        }
    }

    private JSONObject buildSharingJson() {
        JSONObject sharingObj = new JSONObject();
        FastMap<List<DataSharing>> sharingMap = sharePanel.getSharingMap();

        if (sharingMap != null && sharingMap.size() > 0) {
            JSONArray sharingArr = new JSONArray();
            int index = 0;
            for (String userName : sharingMap.keySet()) {
                List<DataSharing> shareList = sharingMap.get(userName);
                JSONObject userObj = new JSONObject();
                userObj.put("user", new JSONString(userName)); //$NON-NLS-1$
                userObj.put("paths", buildPathArrWithPermissions(shareList)); //$NON-NLS-1$
                sharingArr.set(index++, userObj);
            }

            sharingObj.put("sharing", sharingArr); //$NON-NLS-1$
            return sharingObj;
        } else {
            return null;
        }
    }

    private JSONArray buildPathArrWithPermissions(List<DataSharing> shareList) {
        JSONArray pathArr = new JSONArray();
        int index = 0;
        JSONObject obj;
        for (DataSharing ds : shareList) {
            obj = new JSONObject();
            obj.put("path", new JSONString(ds.getPath())); //$NON-NLS-1$
            obj.put("permissions", buildSharingPermissions(ds)); //$NON-NLS-1$
            pathArr.set(index++, obj);
        }

        return pathArr;
    }

    private JSONArray buildPathArr(List<DataSharing> list) {
        JSONArray pathArr = new JSONArray();
        int index = 0;
        for (DataSharing ds : list) {
            pathArr.set(index++, new JSONString(ds.getPath()));
        }
        return pathArr;
    }

    private JSONObject buildSharingPermissions(DataSharing sh) {
        JSONObject permission = new JSONObject();
        permission.put("read", JSONBoolean.getInstance(sh.isReadable())); //$NON-NLS-1$
        permission.put("write", JSONBoolean.getInstance(sh.isWritable())); //$NON-NLS-1$
        permission.put("own", JSONBoolean.getInstance(sh.isOwner())); //$NON-NLS-1$
        return permission;
    }

    private JSONObject buildUnSharingJson() {
        JSONObject unsharingObj = new JSONObject();
        FastMap<List<DataSharing>> unSharingMap = sharePanel.getUnshareList();

        if (unSharingMap != null && unSharingMap.size() > 0) {
            JSONArray unsharingArr = new JSONArray();
            int index = 0;
            for (String userName : unSharingMap.keySet()) {
                List<DataSharing> shareList = unSharingMap.get(userName);
                JSONObject userObj = new JSONObject();
                userObj.put("user", new JSONString(userName)); //$NON-NLS-1$
                userObj.put("paths", buildPathArr(shareList)); //$NON-NLS-1$
                unsharingArr.set(index++, userObj);
            }
            unsharingObj.put("unshare", unsharingArr); //$NON-NLS-1$
            return unsharingObj;
        } else {
            return null;
        }

    }
}
