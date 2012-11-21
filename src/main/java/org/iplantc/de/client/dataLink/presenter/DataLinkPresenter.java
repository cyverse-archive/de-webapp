package org.iplantc.de.client.dataLink.presenter;

import java.util.List;
import java.util.Map;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.models.DEProperties;
import org.iplantc.core.uicommons.client.views.panels.IPlantDialogPanel;
import org.iplantc.core.uidiskresource.client.models.Folder;
import org.iplantc.core.uidiskresource.client.models.IDiskResource;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.dataLink.models.DataLink;
import org.iplantc.de.client.dataLink.models.DataLinkFactory;
import org.iplantc.de.client.dataLink.models.DataLinkList;
import org.iplantc.de.client.dataLink.view.DataLinkPanel;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.UUIDService;
import org.iplantc.de.client.services.UUIDServiceAsync;
import org.iplantc.de.client.utils.DataUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckState;


public class DataLinkPresenter<M extends IDiskResource> implements DataLinkPanel.Presenter<M>{

    private final DataLinkPanel<M> view;
    private final UUIDServiceAsync uuidService = GWT.create(UUIDService.class);
    private final DiskResourceServiceFacade drService = new DiskResourceServiceFacade();
    private final DataLinkFactory dlFactory = GWT.create(DataLinkFactory.class);

    public DataLinkPresenter(List<M> resources) {
        view = new DataLinkPanel<M>(resources);
        view.setPresenter(this);
        
        // Remove Folders
        List<M> allowedResources = Lists.newArrayList();
        for(M m : resources){
            if(!(m instanceof Folder)){
                allowedResources.add(m);
            }
        }
        // Retrieve tickets for root nodes
        getExistingDataLinks(allowedResources);
    }
    
    private void getExistingDataLinks(List<M> resources) {
        // Add roots
        
        view.addRoots(resources);
        
        drService.listDataLinks(DataUtils.getDiskResourceIdList(resources), new ListDataLinksCallback(view.getTree()));
        
    }



    @Override
    public void deleteDataLink(DataLink value) {
        drService.deleteDataLinks(Lists.newArrayList(value.getId()), new DeleteDataLinksCallback(view.getTree()));
    }
    
    @Override
    public void deleteDataLinks(List<DataLink> dataLinks){
        List<String> dataLinkIds = Lists.newArrayList();
        for (DataLink dl : dataLinks) {
            dataLinkIds.add(dl.getId());
        }
        drService.deleteDataLinks(dataLinkIds, new DeleteDataLinksCallback(view.getTree()));
    }

    @Override
    public IPlantDialogPanel getView() {
        return view;
    }


    @Override
    public void createDataLinks(List<M> selectedItems) {
        final List<String> drResourceIds = Lists.newArrayList();
        for(M dr : selectedItems){
            if(!(dr instanceof DataLink)){
                drResourceIds.add(dr.getId());
            }
        }

        uuidService.getUUIDs(drResourceIds.size(), new CreateDataLinkUuidsCallback(drResourceIds));
        
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
                    ticketIdToResourceIdMap, dlFactory, view.getTree()));
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

    @Override
    public String getSelectedDataLinkText() {
        M model = view.getTree().getSelectionModel().getSelectedItem();
        if(model instanceof DataLink){
            return getDataLinkUrlPrefix() + model.getId();
        }
        return null;
    }

    @Override
    public String getDataLinkUrlPrefix() {
        return DEProperties.getInstance().getKifShareTicketBaseUrl();
    }

}
