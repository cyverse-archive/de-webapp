package org.iplantc.de.client.views.panels;

import java.util.Arrays;
import java.util.List;

import org.iplantc.core.uidiskresource.client.util.DiskResourceUtil;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.images.Resources;
import org.iplantc.de.client.models.DataSharing;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * A Dialog to display user to file share relationships in a grouping grid, which can be grouped by user
 * or data from toolbar buttons.
 * 
 * @author psarando
 * 
 */
public class ShareBreakdownDialog extends Dialog {

    public ShareBreakdownDialog(List<DataSharing> shares) {
        init();

        GroupingStore<DataSharing> store = buildGroupingStore(shares);

        ToolBar toolbar = new ToolBar();
        toolbar.add(buildGroupByUserButton(store));
        toolbar.add(buildGroupByDataButton(store));

        setTopComponent(toolbar);

        add(buildGrid(store, buildColumnModel()));
    }

    private void init() {
        setLayout(new FitLayout());
        setSize(640, 480);
        setHideOnButtonClick(true);
        setModal(true);
    }

    private GroupingStore<DataSharing> buildGroupingStore(List<DataSharing> shares) {
        GroupingStore<DataSharing> store = new GroupingStore<DataSharing>();

        store.setKeyProvider(new ModelKeyProvider<DataSharing>() {
            @Override
            public String getKey(DataSharing model) {
                return model.getKey();
            }
        });

        // Group by data initially.
        store.groupBy(DataSharing.PATH);
        store.add(shares);

        return store;
    }

    private ColumnModel buildColumnModel() {
        ColumnConfig sharee = new ColumnConfig(DataSharing.USER, I18N.DISPLAY.name(), 170);

        ColumnConfig diskResource = new ColumnConfig(DataSharing.PATH, I18N.DISPLAY.data(), 170);
        diskResource.setRenderer(new GridCellRenderer<ModelData>() {

            @Override
            public Object render(ModelData model, String property, ColumnData config, int rowIndex,
                    int colIndex, ListStore<ModelData> store, Grid<ModelData> grid) {
                return DiskResourceUtil.parseNameFromPath((String)model.get(property));
            }
        });

        ColumnConfig permissions = new ColumnConfig(DataSharing.DISPLAY_PERMISSION,
                I18N.DISPLAY.permissions(), 100);

        return new ColumnModel(Arrays.asList(sharee, diskResource, permissions));
    }

    private Grid<DataSharing> buildGrid(ListStore<DataSharing> store, final ColumnModel cm) {
        Grid<DataSharing> grid = new Grid<DataSharing>(store, cm);

        GroupingView view = new GroupingView();
        view.setShowGroupedColumn(false);
        view.setForceFit(true);

        view.setGroupRenderer(new GridGroupRenderer() {
            @Override
            public String render(GroupColumnData data) {
                if (DataSharing.PATH.equals(data.field)) {
                    return DiskResourceUtil.parseNameFromPath(data.group);
                }

                return data.group;
            }
        });

        grid.setView(view);

        return grid;
    }

    private Button buildGroupByUserButton(GroupingStore<DataSharing> store) {
        Button btn = new Button(I18N.DISPLAY.groupByUser());
        btn.setIcon(AbstractImagePrototype.create(Resources.ICONS.viewCurrentCollabs()));
        btn.addSelectionListener(new GroupByButtonListener(DataSharing.USER, store));

        return btn;
    }

    private Button buildGroupByDataButton(GroupingStore<DataSharing> store) {
        Button btn = new Button(I18N.DISPLAY.groupByData());
        btn.setIcon(AbstractImagePrototype.create(Resources.ICONS.folder()));
        btn.addSelectionListener(new GroupByButtonListener(DataSharing.PATH, store));

        return btn;
    }

    private class GroupByButtonListener extends SelectionListener<ButtonEvent> {
        private final String groupByField;
        private final GroupingStore<DataSharing> store;

        public GroupByButtonListener(String groupByField, GroupingStore<DataSharing> store) {
            this.groupByField = groupByField;
            this.store = store;
        }

        @Override
        public void componentSelected(ButtonEvent ce) {
            store.groupBy(groupByField);
        }
    }
}
