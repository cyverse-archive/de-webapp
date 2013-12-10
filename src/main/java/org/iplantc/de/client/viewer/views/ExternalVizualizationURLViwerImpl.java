/**
 * 
 */
package org.iplantc.de.client.viewer.views;

import java.util.LinkedList;
import java.util.List;

import org.iplantc.core.uicommons.client.models.diskresources.File;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.viewer.models.VizUrl;
import org.iplantc.de.client.viewer.models.VizUrlProperties;
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
public class ExternalVizualizationURLViwerImpl extends AbstractFileViewer {

    private static TreeViwerUiBinder uiBinder = GWT.create(TreeViwerUiBinder.class);

    @UiTemplate("ExternalVizualizationURLViwer.ui.xml")
    interface TreeViwerUiBinder extends UiBinder<Widget, ExternalVizualizationURLViwerImpl> {
    }

    private final Widget widget;

    Grid<VizUrl> grid;

    @UiField(provided = true)
    ListStore<VizUrl> listStore;

    @UiField(provided = true)
    ColumnModel<VizUrl> cm;

    @UiField
    GridView<VizUrl> gridView;

    public ExternalVizualizationURLViwerImpl(File file) {
        super(file, null);
        this.cm = buildColumnModel();
        this.listStore = new ListStore<VizUrl>(new TreeUrlKeyProvider());
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
        List<VizUrl> urls = (List<VizUrl>)data;
        listStore.addAll(urls);
    }

    @Override
    public void loadData() {
        // do nothing intentionally

    }

    private ColumnModel<VizUrl> buildColumnModel() {
        VizUrlProperties props = GWT.create(VizUrlProperties.class);
        List<ColumnConfig<VizUrl, ?>> configs = new LinkedList<ColumnConfig<VizUrl, ?>>();
        ColumnConfig<VizUrl, String> label = new ColumnConfig<VizUrl, String>(props.label(), 75);
        label.setHeader(I18N.DISPLAY.label());
        configs.add(label);

        ColumnConfig<VizUrl, VizUrl> url = new ColumnConfig<VizUrl, VizUrl>(
                new IdentityValueProvider<VizUrl>(), 280);
        url.setHeader("URL");
        url.setCell(new TreeUrlCell());
        configs.add(url);

        return new ColumnModel<VizUrl>(configs);

    }

    private class TreeUrlKeyProvider implements ModelKeyProvider<VizUrl> {
        @Override
        public String getKey(VizUrl item) {
            return item.getLabel();
        }

    }

    @Override
    public String getViewName() {
        return "Vizualization:" + file.getName();
    }

}
