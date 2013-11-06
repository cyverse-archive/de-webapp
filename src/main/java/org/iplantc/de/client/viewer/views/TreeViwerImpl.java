/**
 * 
 */
package org.iplantc.de.client.viewer.views;

import java.util.LinkedList;
import java.util.List;

import org.iplantc.core.uicommons.client.models.diskresources.File;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.viewer.models.TreeUrl;
import org.iplantc.de.client.viewer.models.TreeUrlProperties;
import org.iplantc.de.client.viewer.views.cells.TreeUrlCell;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;

/**
 * @author sriram
 * 
 */
public class TreeViwerImpl extends AbstractFileViewer {

    private static TreeViwerUiBinder uiBinder = GWT.create(TreeViwerUiBinder.class);

    @UiTemplate("TreeViewer.ui.xml")
    interface TreeViwerUiBinder extends UiBinder<Widget, TreeViwerImpl> {
    }

    private final Widget widget;

    Grid<TreeUrl> grid;

    @UiField(provided = true)
    ListStore<TreeUrl> listStore;

    @UiField(provided = true)
    ColumnModel<TreeUrl> cm;

    @UiField
    GridView<TreeUrl> gridView;

    public TreeViwerImpl(File file) {
        super(file, null);
        this.cm = buildColumnModel();
        this.listStore = new ListStore<TreeUrl>(new TreeUrlKeyProvider());
        this.widget = uiBinder.createAndBindUi(this);
        gridView.setAutoExpandColumn(cm.getColumn(1));
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setData(Object data) {
        List<TreeUrl> urls = (List<TreeUrl>)data;
        listStore.addAll(urls);
    }

    @Override
    public void loadData() {
        // do nothing intentionally

    }

    private ColumnModel<TreeUrl> buildColumnModel() {
        TreeUrlProperties props = GWT.create(TreeUrlProperties.class);
        List<ColumnConfig<TreeUrl, ?>> configs = new LinkedList<ColumnConfig<TreeUrl, ?>>();
        ColumnConfig<TreeUrl, String> label = new ColumnConfig<TreeUrl, String>(props.label(), 75);
        label.setHeader(I18N.DISPLAY.label());
        configs.add(label);

        ColumnConfig<TreeUrl, TreeUrl> url = new ColumnConfig<TreeUrl, TreeUrl>(
                new IdentityValueProvider<TreeUrl>(), 280);
        url.setHeader(I18N.DISPLAY.treeUrl());
        url.setCell(new TreeUrlCell());
        configs.add(url);

        return new ColumnModel<TreeUrl>(configs);

    }

    private class TreeUrlKeyProvider implements ModelKeyProvider<TreeUrl> {
        @Override
        public String getKey(TreeUrl item) {
            return item.getLabel();
        }

    }

    @Override
    public String getViewName() {
        return "Tree View:" + file.getName();
    }

}
