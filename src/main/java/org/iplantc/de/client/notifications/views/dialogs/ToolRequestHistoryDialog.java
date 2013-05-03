package org.iplantc.de.client.notifications.views.dialogs;

import java.util.Date;
import java.util.List;

import org.iplantc.core.resources.client.messages.I18N;
import org.iplantc.de.client.notifications.models.payload.ToolRequestHistory;
import org.iplantc.de.client.notifications.models.payload.ToolRequestHistoryProperties;
import org.iplantc.de.client.notifications.models.payload.ToolRequestStatus;
import org.iplantc.de.client.notifications.views.cells.ToolRequestStatusCell;

import com.google.common.collect.Lists;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

/**
 * A Dialog for displaying Tool Request Status history in a grid.
 * 
 * @author psarando
 * 
 */
public class ToolRequestHistoryDialog extends Dialog {

    private static ToolRequestHistoryPanelUiBinder uiBinder = GWT
            .create(ToolRequestHistoryPanelUiBinder.class);
    private static ToolRequestHistoryProperties historyProperties = GWT
            .create(ToolRequestHistoryProperties.class);

    @UiTemplate("ToolRequestHistoryPanel.ui.xml")
    interface ToolRequestHistoryPanelUiBinder extends UiBinder<Widget, ToolRequestHistoryDialog> {
    }

    @UiField
    Grid<ToolRequestHistory> grid;

    @UiField
    ColumnModel<ToolRequestHistory> cm;

    @UiField
    ListStore<ToolRequestHistory> listStore;

    @UiField
    GridView<ToolRequestHistory> gridView;

    @UiFactory
    ColumnModel<ToolRequestHistory> createColumnModel() {
        List<ColumnConfig<ToolRequestHistory, ?>> list = Lists.newArrayList();

        ColumnConfig<ToolRequestHistory, ToolRequestStatus> status = new ColumnConfig<ToolRequestHistory, ToolRequestStatus>(
                historyProperties.status(), 50, I18N.DISPLAY.status());
        status.setCell(new ToolRequestStatusCell());

        ColumnConfig<ToolRequestHistory, Date> statusDate = new ColumnConfig<ToolRequestHistory, Date>(
                historyProperties.statusDate(), 100, I18N.DISPLAY.date());
        PredefinedFormat statusDateFormat = DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM;
        statusDate.setCell(new DateCell(DateTimeFormat.getFormat(statusDateFormat)));

        ColumnConfig<ToolRequestHistory, String> comments = new ColumnConfig<ToolRequestHistory, String>(
                historyProperties.comments(), 100, I18N.DISPLAY.comments());

        list.add(status);
        list.add(statusDate);
        list.add(comments);

        return new ColumnModel<ToolRequestHistory>(list);
    }

    @UiFactory
    ListStore<ToolRequestHistory> createListStore() {
        ListStore<ToolRequestHistory> store = new ListStore<ToolRequestHistory>(
                new ModelKeyProvider<ToolRequestHistory>() {

                    @Override
                    public String getKey(ToolRequestHistory item) {
                        return item.getStatus().toString() + item.getStatusDate();
                    }
                });

        return store;
    }

    public ToolRequestHistoryDialog(List<ToolRequestHistory> history) {
        setHeadingText(I18N.DISPLAY.toolRequestStatus());
        setSize("480", "320"); //$NON-NLS-1$ //$NON-NLS-2$
        setResizable(true);
        setHideOnButtonClick(true);

        add(uiBinder.createAndBindUi(this));

        StoreSortInfo<ToolRequestHistory> sortInfo = new StoreSortInfo<ToolRequestHistory>(
                historyProperties.statusDate(), SortDir.DESC);
        grid.getStore().addSortInfo(sortInfo);
        grid.getStore().addAll(history);

        new QuickTip(grid);
    }

}
