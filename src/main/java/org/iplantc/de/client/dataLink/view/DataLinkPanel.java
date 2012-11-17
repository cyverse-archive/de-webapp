package org.iplantc.de.client.dataLink.view;

import java.util.List;
import java.util.Map;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.views.panels.IPlantDialogPanel;
import org.iplantc.core.uidiskresource.client.models.Folder;
import org.iplantc.core.uidiskresource.client.models.IDiskResource;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.dataLink.models.DataLink;
import org.iplantc.de.client.dataLink.models.DataLinkFactory;
import org.iplantc.de.client.dataLink.models.DataLinkList;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.UUIDService;
import org.iplantc.de.client.services.UUIDServiceAsync;
import org.iplantc.de.client.utils.DataUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.CheckChangedEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangedEvent.CheckChangedHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckCascade;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckState;

public class DataLinkPanel<M extends IDiskResource> extends IPlantDialogPanel implements IsWidget {

    @UiTemplate("DataLinkPanel.ui.xml")
    interface DataLinkPanelUiBinder extends UiBinder<Widget, DataLinkPanel<?>> {}
    
    private static DataLinkPanelUiBinder uiBinder = GWT.create(DataLinkPanelUiBinder.class);
    
    @UiField
    TreeStore<M> store;
    
    @UiField
    Tree<M, M> tree;

    @UiField
    TextButton createDataLinksBtn;
    
    @UiField
    TextButton deleteDataLinksBtn;

    private final Widget widget;
    
    private final DataLinkFactory dlFactory = GWT.create(DataLinkFactory.class);
    private final UUIDServiceAsync uuidService = GWT.create(UUIDService.class);
    private final DiskResourceServiceFacade drService = new DiskResourceServiceFacade();
    
    public DataLinkPanel(List<M> sharedResources) {
        widget = uiBinder.createAndBindUi(this);
        widget.setHeight("300");
        tree.setCheckable(true);
        tree.setCheckStyle(CheckCascade.CHILDREN);
        
        // Set the tree's node close/open icons to an empty image. Images for our tree will be controlled from the cell.
        ImageResourcePrototype emptyImgResource = new ImageResourcePrototype("", UriUtils.fromString(""), 0, 0, 0, 0, false, false);
        tree.getStyle().setNodeCloseIcon(emptyImgResource);
        tree.getStyle().setNodeOpenIcon(emptyImgResource);
        tree.addCheckChangedHandler(new TreeCheckChangedHandler(deleteDataLinksBtn, createDataLinksBtn, tree));
        
        // Remove Folders
        List<M> allowedResources = Lists.newArrayList();
        for(M m : sharedResources){
            if(!(m instanceof Folder)){
                allowedResources.add(m);
            }
        }
        tree.setCell(new DataLinkPanelCell<M>());
        // Retrieve tickets for root nodes
        getExistingDataLinks(allowedResources);
    }
    
    private void getExistingDataLinks(List<M> resources) {
        // Add roots
        store.add(resources);
        
        drService.listDataLinks(DataUtils.getDiskResourceIdList(resources), new ListDataLinksCallback(tree));
        
    }
    
    @UiFactory
    ValueProvider<M, M> createValueProvider() {
        return new IdentityValueProvider<M>();
    }
    
    @UiFactory
    TreeStore<M> createTreeStore() {
        return new TreeStore<M>(new ModelKeyProvider<M>() {

            @Override
            public String getKey(M item) {
                return item.getId();
            }
        });
    }
    
    @UiHandler("createDataLinksBtn")
    void onCreateDataLinksSelected(SelectEvent event){
        final List<String> drResourceIds = Lists.newArrayList();
        for(M dr : store.getRootItems()){
            if(tree.isChecked(dr)){
                drResourceIds.add(dr.getId());
            }
        }

        uuidService.getUUIDs(drResourceIds.size(), new CreateDataLinkUuidsCallback(drResourceIds));
    }

    @UiHandler("deleteDataLinksBtn")
    void onDeleteDataLinksSelected(SelectEvent event) {
        List<String> dataLinkIds = Lists.newArrayList();
        for (M dl : tree.getCheckedSelection()) {
            if(dl instanceof DataLink){
                dataLinkIds.add(dl.getId());
            }
        }
        drService.deleteDataLinks(dataLinkIds, new DeleteDataLinksCallback(tree));

    }
    
    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void handleOkClick() {
        // Do nothing
    }

    @Override
    public Widget getDisplayWidget() {
        return widget;
    }

    /**
     * A handler who controls this widgets button visibility based on tree check selection.
     * @author jstroot
     *
     */
    private final class TreeCheckChangedHandler implements CheckChangedHandler<M> {
    
        private final HasEnabled createBtn;
        private final HasEnabled deleteBtn;
        private final Tree<M, M> tree;
    
        public TreeCheckChangedHandler(HasEnabled deleteBtn, HasEnabled createBtn, Tree<M, M> tree) {
            this.deleteBtn = deleteBtn;
            this.createBtn = createBtn;
            this.tree = tree;
        }
    
        @Override
        public void onCheckChanged(CheckChangedEvent<M> event) {
            deleteBtn.setEnabled(false);
            createBtn.setEnabled(false);
            for(M item : tree.getCheckedSelection()){
                if(item instanceof DataLink){
                    deleteBtn.setEnabled(true);
                }else{
                    // If the selection is not a DataLink, then it must be a file or folder
                    createBtn.setEnabled(true);
                }
            }
            
        }
    }


    private final class ListDataLinksCallback implements AsyncCallback<String> {
        private final Tree<M, M> tree;
    
        public ListDataLinksCallback(Tree<M, M> tree) {
            this.tree = tree;
        }
    
        @SuppressWarnings("unchecked")
        @Override
        public void onSuccess(String result) {
            // Get tickets by resource id, add them to the tree.
            JSONObject response = JsonUtil.getObject(result);
            JSONObject tickets = JsonUtil.getObject(response, "tickets");
            
            Splittable placeHolder;
            for(String key : tickets.keySet()){
                placeHolder = StringQuoter.createSplittable();
                M dr = tree.getStore().findModelWithKey(key);
                
                JSONArray dlIds = JsonUtil.getArray(tickets, key);
                Splittable splittable = StringQuoter.split(dlIds.toString());
                splittable.assign(placeHolder, "tickets");
                AutoBean<DataLinkList> ticketsAB = AutoBeanCodex.decode(dlFactory, DataLinkList.class, placeHolder);
                
                List<DataLink> dlList = ticketsAB.as().getTickets();
                
                for(DataLink dl : dlList){
                    tree.getStore().add(dr, (M)dl);
                    tree.setChecked((M)dl, CheckState.CHECKED);
                }
            }
            // Select all roots automatically
            tree.setCheckedSelection(tree.getStore().getAll());
            for (M m : tree.getStore().getAll()) {
                tree.setExpanded(m, true);
            }
        }
    
        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(I18N.ERROR.listDataLinksError(), caught);
        }
    }


    private final class DeleteDataLinksCallback implements AsyncCallback<String> {
        private final Tree<M,M> tree;

        public DeleteDataLinksCallback(Tree<M,M> tree) {
            this.tree = tree;
        }

        @Override
        public void onSuccess(String result) {
            JSONObject response = JsonUtil.getObject(result);
            JSONArray tickets = JsonUtil.getArray(response, "tickets");
            
            for(int i = 0; i < tickets.size(); i++){
                String ticketId = tickets.get(i).isString().toString().replace("\"", "");
                M m = tree.getStore().findModelWithKey(ticketId);
                if(m != null){
                    tree.getStore().remove(m);
                }
            }
    
        }
    
        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(I18N.ERROR.deleteDataLinksError(), caught);
        }
    }


    private class CreateDataLinkUuidsCallback implements AsyncCallback<List<String>> {
        private final List<String> drResourceIds;

        public CreateDataLinkUuidsCallback(List<String> drResourceIds) {
            this.drResourceIds = drResourceIds;
        }

        @Override
        public void onSuccess(List<String> uuids) {
            Map<String, String> ticketIdToResourceIdMap = Maps.newHashMap();
            for (String drId : drResourceIds) {
                ticketIdToResourceIdMap.put(uuids.get(drResourceIds.indexOf(drId)), drId);
            }

            drService.createDataLinks(ticketIdToResourceIdMap, new CreateDataLinksCallback(
                    ticketIdToResourceIdMap, dlFactory, tree));
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(caught);
        }

    }

    private class CreateDataLinksCallback implements AsyncCallback<String> {
        private final Map<String, String> ticketIdToResourceIdMap;
        private final DataLinkFactory factory;
        private final Tree<M, M> tree;

        public CreateDataLinksCallback(Map<String, String> ticketIdToResourceIdMap, final DataLinkFactory factory, final Tree<M,M> tree) {
            this.ticketIdToResourceIdMap = ticketIdToResourceIdMap;
            this.factory = factory;
            this.tree = tree;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onSuccess(String result) {
            AutoBean<DataLinkList> tickets = AutoBeanCodex.decode(factory, DataLinkList.class, result);
            List<DataLink> dlList = tickets.as().getTickets();

            TreeStore<M> treeStore = tree.getStore();
            for(DataLink dl : dlList){
                String parentId = ticketIdToResourceIdMap.get(dl.getId());

                M parent = treeStore.findModelWithKey(parentId);
                if (parent != null) {
                    treeStore.add(parent, (M)dl);
                    tree.setExpanded(parent, true);
                    tree.setChecked((M)dl, CheckState.CHECKED);
                }
            }
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(I18N.ERROR.createDataLinksError(), caught);
        }
    }

}
