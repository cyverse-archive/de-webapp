package org.iplantc.de.client.dataLink.view;

import java.util.List;

import org.iplantc.core.uicommons.client.views.panels.IPlantDialogPanel;
import org.iplantc.core.uidiskresource.client.models.IDiskResource;
import org.iplantc.de.client.dataLink.models.DataLink;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tips.QuickTip;
import com.sencha.gxt.widget.core.client.tree.Tree;

public class DataLinkPanel<M extends IDiskResource> extends IPlantDialogPanel implements IsWidget {
    
    public interface Presenter<M> {

        void deleteDataLink(DataLink dataLink);
        
        void deleteDataLinks(List<DataLink> dataLinks);

        IPlantDialogPanel getView();

        void createDataLinks(List<M> selectedItems);

        void deleteAllDataLinksFor(IDiskResource value);
        
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
    
    private final Widget widget;
    
    private Presenter<M> presenter;
    
    public DataLinkPanel(List<M> sharedResources) {
        widget = uiBinder.createAndBindUi(this);
        widget.setHeight("300");
        
        // Set the tree's node close/open icons to an empty image. Images for our tree will be controlled from the cell.
        ImageResourcePrototype emptyImgResource = new ImageResourcePrototype("", UriUtils.fromString(""), 0, 0, 0, 0, false, false);
        tree.getStyle().setNodeCloseIcon(emptyImgResource);
        tree.getStyle().setNodeOpenIcon(emptyImgResource);
        
        tree.getSelectionModel().addSelectionChangedHandler(new TreeSelectionHandler(createDataLinksBtn, tree));
        tree.getSelectionModel().addBeforeSelectionHandler(new TreeSelectionHandler(createDataLinksBtn, tree));
        
        new QuickTip(widget);

    }
    
    public void setPresenter(Presenter<M> presenter){
        this.presenter = presenter;
        tree.setCell(new DataLinkPanelCell<M>(this.presenter));
    }
    
    public void addRoots(List<M> roots){
        store.add(roots);
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
        presenter.createDataLinks(tree.getSelectionModel().getSelectedItems());
        
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
    private final class TreeSelectionHandler implements SelectionChangedHandler<M>, BeforeSelectionHandler<M> {
    
        private final HasEnabled createBtn;
        private final Tree<M, M> tree;
    
        public TreeSelectionHandler(HasEnabled createBtn, Tree<M, M> tree) {
            this.createBtn = createBtn;
            this.tree = tree;
        }
    
        @Override
        public void onSelectionChanged(SelectionChangedEvent<M> event) {
            createBtn.setEnabled(false);
            for(M item : tree.getSelectionModel().getSelectedItems()){
                if(!(item instanceof DataLink)){
                    createBtn.setEnabled(true);
                }else{
                    tree.getSelectionModel().deselect(item);
                }
            }
            
        }

        @Override
        public void onBeforeSelection(BeforeSelectionEvent<M> event) {
            // Not allowing selection of DataLinks
            if(event.getItem() instanceof DataLink){
                event.cancel();
            }
            
        }
    }


    public Tree<M, M> getTree() {
        return tree;
    }


    

    

}
