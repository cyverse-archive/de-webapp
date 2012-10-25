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
import org.iplantc.core.uidiskresource.client.models.Folder;
import org.iplantc.core.uidiskresource.client.util.DiskResourceUtil;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.events.DataSearchResultSelectedEvent;
import org.iplantc.de.client.views.panels.DataPreviewPanel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
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

    public MyDataSearchGrid(String searchTerm, ListStore<DiskResource> store, ColumnModel colModel) {
        super(store, colModel);
        setLoadMask(true);
        getView().setEmptyText(I18N.DISPLAY.noSearchResults(searchTerm));
    }

    private static MyDataSearchGrid createInstanceImpl(String searchTerm, List<DiskResource> results,
            String tag) {
        ListStore<DiskResource> store = new ListStore<DiskResource>();
        store.add(results);
        MyDataSearchGrid grid = new MyDataSearchGrid(searchTerm, store, buildColumnModel(searchTerm));
        return grid;
    }

    /**
     * Allocate default instance.
     * 
     * @return newly allocated my data grid.
     */
    public static MyDataSearchGrid createInstance(String searchTerm, List<DiskResource> results,
            String tag) {
        return createInstanceImpl(searchTerm, results, tag);
    }

    /*
     * * Create the column model for the grid.
     * 
     * @return an instance of ColumnModel representing the columns visible in a grid
     */
    protected static ColumnModel buildColumnModel(String searchTerm) {
        // build column configs and add them to a list for the ColumnModel.
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        ColumnConfig name = new ColumnConfig(COLUMN_ID_NAME, I18N.DISPLAY.name(), 235);
        name.setRenderer(new SearchNameCellRenderer(searchTerm));

        ColumnConfig date = new ColumnConfig(COLUMN_ID_DATE_MODIFIED, I18N.DISPLAY.lastModified(), 150);
        date.setDateTimeFormat(DateTimeFormat
                .getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM));

        ColumnConfig path = new ColumnConfig(COLUMN_ID_PATH, I18N.DISPLAY.location(), 200);
        path.setRenderer(new LocationCellRenderer(searchTerm));
        path.setSortable(false);
        path.setMenuDisabled(true);

        columns.addAll(Arrays.asList(name, path));

        return new ColumnModel(columns);
    }

}

class LocationCellRenderer implements GridCellRenderer<DiskResource> {

    private String searchTerm;

    public LocationCellRenderer(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    @Override
    public Object render(final DiskResource model, String property, ColumnData config, int rowIndex,
            int colIndex, ListStore<DiskResource> store, Grid<DiskResource> grid) {
        Hyperlink link = null;

        if (model instanceof Folder) {
            link = new Hyperlink(Format.ellipse(model.getId(), 30), "mydata_name"); //$NON-NLS-1$
        } else {
            link = new Hyperlink(Format.ellipse(DiskResourceUtil.parseParent(model.getId()), 30),
                    "mydata_name");
        }
        link.setToolTip(model.getId());
        link.addListener(Events.OnClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                DataSearchResultSelectedEvent e = new DataSearchResultSelectedEvent(searchTerm, model,
                        Arrays.asList(model.getId()));
                EventBus.getInstance().fireEvent(e);
            }
        });

        link.setWidth(model.getId().length());
        return link;
    }
}

/**
 * A custom renderer that renders folder / file names as hyperlink
 * 
 * @author sriram
 * 
 */
class SearchNameCellRenderer implements GridCellRenderer<DiskResource> {

    private String searchTerm;

    public SearchNameCellRenderer(String searchTerm) {
        this.searchTerm = searchTerm;
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
                DataSearchResultSelectedEvent e = new DataSearchResultSelectedEvent(searchTerm, model,
                        Arrays.asList(model.getId()));
                EventBus.getInstance().fireEvent(e);
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
