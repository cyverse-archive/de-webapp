package org.iplantc.de.client.dataLink.view;

import java.util.List;

import org.iplantc.core.uicommons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.core.uidiskresource.client.models.DiskResource;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.dataLink.models.DataLink;

import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.tips.QuickTip;
import com.sencha.gxt.widget.core.client.tree.Tree;

public class DataLinkPanel<M extends DiskResource> extends IPlantDialog implements IsWidget {

    public interface Presenter<M> {

        void deleteDataLink(DataLink dataLink);

        void deleteDataLinks(List<DataLink> dataLinks);

        IPlantDialog getView();

        void createDataLinks(List<M> selectedItems);

        String getSelectedDataLinkText();

        String getDataLinkUrlPrefix();

    }

    @UiTemplate("DataLinkPanel.ui.xml")
    interface DataLinkPanelUiBinder extends UiBinder<Widget, DataLinkPanel<?>> {
    }

    private static DataLinkPanelUiBinder uiBinder = GWT.create(DataLinkPanelUiBinder.class);

    @UiField
    TreeStore<M> store;

    @UiField
    Tree<M, M> tree;

    @UiField
    TextButton createDataLinksBtn;

    @UiField
    TextButton expandAll;

    @UiField
    TextButton collapseAll;

    @UiField
    TextButton copyDataLinkButton;

    private final Widget widget;

    private Presenter<M> presenter;

    public DataLinkPanel(List<M> sharedResources) {
        widget = uiBinder.createAndBindUi(this);
        widget.setHeight("300");

        // Set the tree's node close/open icons to an empty image. Images for our tree will be controlled
        // from the cell.
        ImageResourcePrototype emptyImgResource = new ImageResourcePrototype("",
                UriUtils.fromString(""), 0, 0, 0, 0, false, false);
        tree.getStyle().setNodeCloseIcon(emptyImgResource);
        tree.getStyle().setNodeOpenIcon(emptyImgResource);

        tree.getSelectionModel().addSelectionHandler(
                new TreeSelectionHandler(createDataLinksBtn, copyDataLinkButton, tree));
        new QuickTip(widget);

    }

    public void setPresenter(Presenter<M> presenter) {
        this.presenter = presenter;
        tree.setCell(new DataLinkPanelCell<M>(this.presenter));
    }

    public void addRoots(List<M> roots) {
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
    void onCreateDataLinksSelected(SelectEvent event) {
        presenter.createDataLinks(tree.getSelectionModel().getSelectedItems());

    }

    @UiHandler("expandAll")
    void onExpandAllSelected(SelectEvent event) {
        tree.expandAll();
    }

    @UiHandler("collapseAll")
    void onCollapseAllSelected(SelectEvent event) {
        tree.collapseAll();
    }

    @UiHandler("copyDataLinkButton")
    void onCopyDataLinkButtonSelected(SelectEvent event) {
        // Open dialog window with text selected.
        Dialog dlg = new Dialog();
        dlg.setHeading(I18N.DISPLAY.copy());
        dlg.setLayout(new FitLayout());
        dlg.setHideOnButtonClick(true);
        dlg.setResizable(false);
        dlg.setWidth(535);
        TextBox textBox = new TextBox();
        textBox.setReadOnly(true);
        textBox.setText(presenter.getSelectedDataLinkText());
        dlg.add(textBox);
        // dlg.add(new Label(I18N.DISPLAY.copyPasteInstructions()));
        dlg.setFocusWidget(textBox);
        dlg.show();
        textBox.selectAll();
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    // @Override
    // public void handleOkClick() {
    // // Do nothing
    // }
    //
    // @Override
    // public Widget getDisplayWidget() {
    // return widget;
    // }

    /**
     * A handler who controls this widgets button visibility based on tree check selection.
     * 
     * @author jstroot
     * 
     */
    private final class TreeSelectionHandler implements SelectionHandler<M> {

        private final HasEnabled createBtn;
        private final Tree<M, M> tree;
        private final HasEnabled copyDataLinkButton;

        public TreeSelectionHandler(HasEnabled createBtn, HasEnabled copyDataLinkButton, Tree<M, M> tree) {
            this.createBtn = createBtn;
            this.copyDataLinkButton = copyDataLinkButton;
            this.tree = tree;
        }

        @Override
        public void onSelection(SelectionEvent<M> event) {
            createBtn.setEnabled(false);
            copyDataLinkButton.setEnabled(false);
            if ((tree.getSelectionModel().getSelectedItems().size() == 1)
                    && (tree.getSelectionModel().getSelectedItems().get(0) instanceof DataLink)) {
                copyDataLinkButton.setEnabled(true);
            }
            for (M item : tree.getSelectionModel().getSelectedItems()) {
                if (!(item instanceof DataLink)) {
                    createBtn.setEnabled(true);
                } else {
                    createBtn.setEnabled(false);
                    break;
                }
            }

        }

    }

    public Tree<M, M> getTree() {
        return tree;
    }

}
