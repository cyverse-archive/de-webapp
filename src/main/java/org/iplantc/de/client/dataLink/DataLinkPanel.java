package org.iplantc.de.client.dataLink;

import java.util.List;

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
import org.iplantc.de.client.utils.DataUtils;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
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
    private final DiskResourceServiceFacade drService = new DiskResourceServiceFacade();
    
    public DataLinkPanel(List<M> sharedResources) {
        widget = uiBinder.createAndBindUi(this);
        widget.setHeight("300");
        tree.setCheckable(true);
        tree.setCheckStyle(CheckCascade.CHILDREN);
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
        // TODO JDS Fetch existing data links for the given disk resources, then add the resources as roots, the tickets as their children, and select them all.

        // Add roots
        store.add(resources);
        
        drService.listDataLinks(DataUtils.getDiskResourceIdList(resources), new AsyncCallback<String>() {
            
            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(String result) {
                // Get tickets by resource id, add them to the tree.
                JSONObject response = JsonUtil.getObject(result);
                JSONObject tickets = JsonUtil.getObject(response, "tickets");
                
                Splittable placeHolder;
                for(String key : tickets.keySet()){
                    placeHolder = StringQuoter.createSplittable();
                    M dr = store.findModelWithKey(key);
                    
                    JSONArray dlIds = JsonUtil.getArray(tickets, key);
                    Splittable splittable = StringQuoter.split(dlIds.toString());
                    splittable.assign(placeHolder, "tickets");
                    AutoBean<DataLinkList> ticketsAB = AutoBeanCodex.decode(dlFactory, DataLinkList.class, placeHolder);
                    
                    List<DataLink> dlList = ticketsAB.as().getTickets();
                    
                    for(DataLink dl : dlList){
                        store.add(dr, (M)dl);
                        tree.setChecked((M)dl, CheckState.CHECKED);
                    }
                }
            }
            
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.listDataLinksError(), caught);
            }
        });
        // Select all roots automatically
        tree.setCheckedSelection(store.getAll());
        for(M m : store.getAll()){
            tree.setExpanded(m, true);
        }
        
    }

    @UiFactory
    ValueProvider<M, String> createValueProvider() {
        return new ValueProvider<M, String>() {

            @Override
            public String getValue(M object) {
                return object.getName();
            }

            @Override
            public void setValue(M object, String value) {
                // Do nothing, we are not editing.
            }

            @Override
            public String getPath() {
                // Do nothing
                return null;
            }
        };
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
        List<String> drResourceIds = Lists.newArrayList();
        List<M> selectedDiskResources = Lists.newArrayList();
        for(M dr : store.getRootItems()){
            if(tree.isChecked(dr)){
                drResourceIds.add(dr.getId());
                selectedDiskResources.add(dr);
            }
        }
        drService.createDataLinks(drResourceIds, new CreateDataLinksCallback(selectedDiskResources));
        
    }
    
    @UiHandler("deleteDataLinksBtn")
    void onDeleteDataLinksSelected(SelectEvent event) {
        List<String> dataLinkIds = Lists.newArrayList();
        List<DataLink> selectedDataLinks = Lists.newArrayList();
        for (M dl : tree.getCheckedSelection()) {
            if(dl instanceof DataLink){
                dataLinkIds.add(dl.getId());
                selectedDataLinks.add((DataLink)dl);
            }
        }
        drService.deleteDataLinks(dataLinkIds, new DeleteDataLinksCallback(selectedDataLinks));

    }
    
    private final class DeleteDataLinksCallback implements AsyncCallback<String> {
        private final List<DataLink> selectedDataLinks;

        public DeleteDataLinksCallback(List<DataLink> selectedDataLinks) {
            this.selectedDataLinks = selectedDataLinks;
        }

        @Override
        public void onSuccess(String result) {
            // TODO Auto-generated method stub
    
        }
    
        @Override
        public void onFailure(Throwable caught) {
            //TODO JDS Create error message
            ErrorHandler.post("", caught);
        }
    }


    private class CreateDataLinksCallback implements AsyncCallback<String> {
        private final List<M> sharedResources;

        public CreateDataLinksCallback(List<M> sharedResources) {
            this.sharedResources = sharedResources;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onSuccess(String result) {
            DataLinkFactory factory = GWT.create(DataLinkFactory.class);
            AutoBean<DataLinkList> tickets = AutoBeanCodex.decode(factory, DataLinkList.class, result);
            List<DataLink> dlList = tickets.as().getTickets();
            // FIXME JDS Currently, json response is not indicating to which Disk resource each ticket belongs.
            for(DataLink dl : dlList){
                M parent = sharedResources.get(dlList.indexOf(dl));
                tree.setExpanded(parent, true);
                store.add(parent, (M)dl);
                tree.setChecked((M)dl, CheckState.CHECKED);
            }
        }

        @Override
        public void onFailure(Throwable caught) {
            // TODO JDS Create error message.
            ErrorHandler.post("", caught);
        }
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void handleOkClick() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Widget getDisplayWidget() {
        return widget;
    }

}
