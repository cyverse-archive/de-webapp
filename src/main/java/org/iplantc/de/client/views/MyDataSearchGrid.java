/**
 * 
 */
package org.iplantc.de.client.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.iplantc.core.client.widgets.Hyperlink;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uidiskresource.client.models.DiskResource;
import org.iplantc.core.uidiskresource.client.models.File;
import org.iplantc.core.uidiskresource.client.models.Folder;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.events.DataSearchResultSelectedEvent;
import org.iplantc.de.client.utils.DataViewContextExecutor;
import org.iplantc.de.client.utils.builders.context.DataContextBuilder;
import org.iplantc.de.client.views.panels.DataPreviewPanel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * @author sriram
 * 
 */
public class MyDataSearchGrid extends Grid<DiskResource> {

    public static final String COLUMN_ID_NAME = DiskResource.NAME;
    public static final String COLUMN_ID_DATE_MODIFIED = DiskResource.DATE_MODIFIED;
    public static final String COLUMN_ID_PATH = DiskResource.ID;

    public MyDataSearchGrid(ListStore<DiskResource> store, ColumnModel colModel) {
        super(store, colModel);
    }

    private static MyDataSearchGrid createInstanceImpl(List<DiskResource> results, String tag) {
        ListStore<DiskResource> store = new ListStore<DiskResource>();
        store.add(results);
        MyDataSearchGrid grid = new MyDataSearchGrid(store, buildColumnModel(tag));
        return grid;
    }

    /**
     * Allocate default instance.
     * 
     * @return newly allocated my data grid.
     */
    public static MyDataSearchGrid createInstance(List<DiskResource> results, String tag) {
        return createInstanceImpl(results, tag);
    }

    /*
     * * Create the column model for the grid.
     * 
     * @return an instance of ColumnModel representing the columns visible in a grid
     */
    protected static ColumnModel buildColumnModel(String tag) {
        // build column configs and add them to a list for the ColumnModel.
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        ColumnConfig name = new ColumnConfig(COLUMN_ID_NAME, I18N.DISPLAY.name(), 235);
        name.setRenderer(new SearchNameCellRenderer(tag));

        ColumnConfig date = new ColumnConfig(COLUMN_ID_DATE_MODIFIED, I18N.DISPLAY.lastModified(), 150);
        date.setDateTimeFormat(DateTimeFormat
                .getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM));

        ColumnConfig path = new ColumnConfig(COLUMN_ID_PATH, "Path", 200);
        path.setSortable(false);
        path.setMenuDisabled(true);

        columns.addAll(Arrays.asList(name, path));

        return new ColumnModel(columns);
    }

}

/**
 * A custom renderer that renders folder / file names as hyperlink
 * 
 * @author sriram
 * 
 */
class SearchNameCellRenderer implements GridCellRenderer<DiskResource> {
    private final String callertag;

    SearchNameCellRenderer(String caller) {
        callertag = caller;
    }

    @Override
    public Object render(final DiskResource model, String property, ColumnData config, int rowIndex,
            int colIndex, ListStore<DiskResource> store, final Grid<DiskResource> grid) {
        Hyperlink link = null;

        if (model instanceof Folder) {
            link = new Hyperlink("<img src='./gxt/images/default/tree/folder.gif'/>&nbsp;" //$NON-NLS-1$
                    + model.getName(), "mydata_name"); //$NON-NLS-1$
            link.setToolTip(model.getName());
        } else {
            link = new Hyperlink("<img src='./images/file.gif'/>&nbsp;" + model.getName(), "mydata_name"); //$NON-NLS-1$ //$NON-NLS-2$
            addPreviewToolTip(link, model);
        }

        link.addListener(Events.OnClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                if (model instanceof Folder) {
                    DataSearchResultSelectedEvent e = new DataSearchResultSelectedEvent(model);
                    EventBus.getInstance().fireEvent(e);
                } else if (model instanceof File) {
                    DataViewContextExecutor executor = new DataViewContextExecutor();
                    DataContextBuilder builder = new DataContextBuilder();
                    executor.execute(builder.build(model.getId()));
                }
            }
        });

        link.setWidth(model.getName().length());

        return link;
    }

    private void addPreviewToolTip(Component target, final DiskResource resource) {
        ToolTipConfig ttConfig = new ToolTipConfig();
        ttConfig.setShowDelay(1000);
        ttConfig.setDismissDelay(0); // never hide tool tip while mouse is still over it
        ttConfig.setAnchorToTarget(true);
        ttConfig.setTitle(I18N.DISPLAY.preview() + ": " + resource.getName()); //$NON-NLS-1$

        final LayoutContainer pnl = new LayoutContainer();
        final DataPreviewPanel previewPanel = new DataPreviewPanel();
        pnl.add(previewPanel);
        ToolTip tip = new ToolTip(target, ttConfig) {
            // overridden to populate the preview
            @Override
            protected void updateContent() {
                getHeader().setText(title);
                if (resource != null) {
                    previewPanel.update(resource);
                }
            }
        };
        tip.setWidth(312);
        tip.add(pnl);
    }
}
