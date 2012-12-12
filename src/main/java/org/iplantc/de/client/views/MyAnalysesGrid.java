package org.iplantc.de.client.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.iplantc.core.client.widgets.Hyperlink;
import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uicommons.client.util.CommonStoreSorter;
import org.iplantc.core.uicommons.client.util.DateParser;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.dispatchers.WindowDispatcher;
import org.iplantc.de.client.events.AnalysisUpdateEvent;
import org.iplantc.de.client.events.AnalysisUpdateEventHandler;
import org.iplantc.de.client.factories.WindowConfigFactory;
import org.iplantc.de.client.models.AnalysisExecution;
import org.iplantc.de.client.models.DataWindowConfig;
import org.iplantc.de.client.models.WizardWindowConfig;
import org.iplantc.de.client.services.AnalysisServiceFacade;
import org.iplantc.de.client.utils.MyDataViewContextExecutor;
import org.iplantc.de.client.views.panels.MyAnalysesPanel;
import org.iplantc.de.client.views.panels.MyAnalysesPanel.EXECUTION_STATUS;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A grid that is used to display users Analyses
 * 
 * @author sriram
 * 
 */
public class MyAnalysesGrid extends Grid<AnalysisExecution> {

    private String idCurrentSelection;
    private ArrayList<HandlerRegistration> handlers;
    private static RowExpander expander;
    private static XTemplate tpl;

    /**
     * Create a new MyAnalysesGrid
     * 
     * @param store store to be used by the grid
     * @param cm column model describing the columns in the grid
     */
    public MyAnalysesGrid(ListStore<AnalysisExecution> store, ColumnModel cm) {
        super(store, cm);
        registerHandlers();
    }

    /**
     * Set the id of our current selection.
     * 
     * @param idCurrentSelection id of currently selected analysis.
     */
    public void setCurrentSelection(final String idCurrentSelection) {
        this.idCurrentSelection = idCurrentSelection;
    }

    private void registerHandlers() {
        handlers = new ArrayList<HandlerRegistration>();
        EventBus eventbus = EventBus.getInstance();
        handlers.add(eventbus.addHandler(AnalysisUpdateEvent.TYPE, new AnalysisUpdateEventHandler() {

            @Override
            public void onUpdate(AnalysisUpdateEvent event) {
                if (event.getPayload() != null) {
                    handleMessage(event.getPayload());
                }
            }

        }));
    }

    private void handleMessage(JSONObject payload) {
        updateStore(payload);
    }

    private void updateStore(JSONObject payload) {
        String analysisId = JsonUtil.getString(payload, "id"); //$NON-NLS-1$

        String status = JsonUtil.getString(payload, "status"); //$NON-NLS-1$
        MyAnalysesPanel.EXECUTION_STATUS enumStatus = MyAnalysesPanel.EXECUTION_STATUS
                .fromTypeString(status);
        status = enumStatus.toString();

        if (getStore().findModel("id", analysisId) != null) { //$NON-NLS-1$
            String endDate = JsonUtil.getString(payload, "enddate"); //$NON-NLS-1$
            String startDate = JsonUtil.getString(payload, "startdate"); //$NON-NLS-1$
            String resultFolder = JsonUtil.getString(payload, "resultfolderid"); //$NON-NLS-1$

            switch (enumStatus) {
                case COMPLETED:
                    updateEndExecStatus(analysisId, status, resultFolder, DateParser.parseDate(endDate));
                    break;

                case FAILED:
                    updateEndExecStatus(analysisId, status, resultFolder, DateParser.parseDate(endDate));
                    break;

                case RUNNING:
                    updateRunExecStatus(analysisId, status, DateParser.parseDate(startDate));
                    break;

                case SUBMITTED:
                    updateRunExecStatus(analysisId, status, DateParser.parseDate(startDate));
                    break;

                default:
                    updateExecStatus(analysisId, status);
                    break;
            }
        }
    }

    private void sort() {
        getStore().sort("startdate", SortDir.DESC); //$NON-NLS-1$
    }

    private void updateExecStatus(String id, String status) {
        AnalysisExecution ae = getStore().findModel("id", id); //$NON-NLS-1$

        if (ae != null) {
            ae.setStatus(status);
            getStore().update(ae);
        }
    }

    private static XTemplate initExpander() {

        String tmpl = "<p><b>Description:</b>{description}</p>"; //$NON-NLS-1$

        return XTemplate.create(tmpl);
    }

    private void updateEndExecStatus(String id, String status, String resultfolderid, Date enddate) {
        AnalysisExecution ae = getStore().findModel("id", id); //$NON-NLS-1$

        if (ae != null) {
            ae.setStatus(status);
            if (enddate != null) {
                ae.setEndDate(enddate);
            }
            ae.setResultFolderId(resultfolderid);
            getStore().update(ae);
        }
    }

    private void updateRunExecStatus(String id, String status, Date startdate) {
        AnalysisExecution ae = getStore().findModel("id", id); //$NON-NLS-1$

        if (ae != null) {
            ae.setStatus(status);
            if (startdate != null) {
                ae.setStartDate(startdate);
            }
            getStore().update(ae);
        }
    }

    /**
     * Allocate default instance.
     * 
     * @return newly allocated my analysis grid.
     */
    @SuppressWarnings("unchecked")
    public static MyAnalysesGrid createInstance(CheckBoxSelectionModel<AnalysisExecution> sm,
            PagingLoader<PagingLoadResult<AnalysisExecution>> remoteLoader) {
        ListStore<AnalysisExecution> gstore;
        if (remoteLoader != null) {
            gstore = new ListStore<AnalysisExecution>(remoteLoader);
        } else {
            gstore = new ListStore<AnalysisExecution>();
        }

        gstore.setStoreSorter(new CommonStoreSorter());
        final ColumnModel colModel = buildColumnModel(sm);
        MyAnalysesGrid grid = new MyAnalysesGrid(gstore, colModel);
        grid.setSelectionModel(sm);
        grid.addPlugin(sm);
        grid.addPlugin(expander);
        grid.setAutoExpandMax(2048);
        grid.getView().setForceFit(true);

        return grid;
    }

    private static ColumnModel buildColumnModel(CheckBoxSelectionModel<AnalysisExecution> sm) {

        tpl = initExpander();
        expander = new RowExpander(tpl);
        sm.getColumn().setMenuDisabled(true);
        expander.setMenuDisabled(true);

        ColumnConfig name = new ColumnConfig("name", I18N.DISPLAY.name(), 175); //$NON-NLS-1$
        ColumnConfig analysisname = new ColumnConfig("analysis_name", I18N.DISPLAY.appName(), 250); //$NON-NLS-1$
        ColumnConfig start = new ColumnConfig("startdate", I18N.DISPLAY.startDate(), 150); //$NON-NLS-1$
        ColumnConfig end = new ColumnConfig("enddate", I18N.DISPLAY.endDate(), 150); //$NON-NLS-1$
        ColumnConfig status = new ColumnConfig("status", I18N.DISPLAY.status(), 100); //$NON-NLS-1$

        DateTimeFormat format = DateTimeFormat
                .getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
        start.setDateTimeFormat(format);
        end.setDateTimeFormat(format);
        analysisname.setRenderer(new AppNameCellRenderer());
        name.setRenderer(new AnalysisNameCellRenderer());

        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        columns.addAll(Arrays.asList(sm.getColumn(), expander, name, analysisname, start, end, status));

        return new ColumnModel(columns);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
    }

    // get a list of analyses for which status needs to be updated
    public List<AnalysisExecution> getUpdateList() {

        List<AnalysisExecution> list = new ArrayList<AnalysisExecution>();
        for (AnalysisExecution ae : store.getModels()) {
            if (ae.getStatus().equalsIgnoreCase(EXECUTION_STATUS.RUNNING.toString())
                    || ae.getStatus().equalsIgnoreCase(EXECUTION_STATUS.SUBMITTED.toString())
                    || ae.getStatus().equalsIgnoreCase(EXECUTION_STATUS.IDLE.toString())) {
                list.add(ae);

            }
        }

        return list;

    }

    public void loadData(List<AnalysisExecution> execs) {
        getStore().add(execs);
        sort();
        selectModel(idCurrentSelection);
    }

    /**
     * {@inheritDoc}
     */
    public void cleanup() {
        EventBus eventbus = EventBus.getInstance();

        // unregister
        for (HandlerRegistration reg : handlers) {
            eventbus.removeHandler(reg);
        }

        // clear our list
        handlers.clear();
    }

    /**
     * Select a row in the grid
     * 
     */
    public void selectModel(final String idSelection) {
        if (idSelection != null) {
            AnalysisExecution exec = getStore().findModel("id", idSelection); //$NON-NLS-1$

            if (exec != null) {
                getView().ensureVisible(getStore().indexOf(exec), 0, false);
                getSelectionModel().select(exec, false);
            }
        }
    }

}

class AppNameCellRenderer implements GridCellRenderer<AnalysisExecution> {
    @Override
    public Object render(final AnalysisExecution model, String property, ColumnData config,
            int rowIndex, int colIndex, ListStore<AnalysisExecution> store, Grid<AnalysisExecution> grid) {
        Hyperlink link = new Hyperlink(model.getAnalysisName(), "analysis_name"); //$NON-NLS-1$
        link.setToolTip(I18N.DISPLAY.executeThisAnalysis());
        link.addListener(Events.OnClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                relaunchAnalysis(model.getId());
            }
        });

        return link;
    }

    private void relaunchAnalysis(final String id) {
        AnalysisServiceFacade asf = new AnalysisServiceFacade();
        asf.relaunchAnalysis(id, new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                WizardWindowConfig config = new WizardWindowConfig(null);
                config.setWizardConfig(JsonUtil.getObject(result));
                WindowConfigFactory configFactory = new WindowConfigFactory();
                JSONObject windowConfig = configFactory.buildWindowConfig(Constants.CLIENT.wizardTag(), //$NON-NLS-1$
                        config);
                WindowDispatcher dispatcher = new WindowDispatcher(windowConfig);
                dispatcher.dispatchAction(id);
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.analysisRelaunchError(), caught);
            }
        });
    }
}

class AnalysisNameCellRenderer implements GridCellRenderer<AnalysisExecution> {
    @Override
    public Object render(final AnalysisExecution model, String property, ColumnData config,
            int rowIndex, int colIndex, ListStore<AnalysisExecution> store, Grid<AnalysisExecution> grid) {
        Hyperlink link = new Hyperlink(model.getName(), "analysis_name"); //$NON-NLS-1$
        link.setToolTip(I18N.DISPLAY.selectAnalysisOutputs());
        link.addListener(Events.OnClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                if (model != null && model.getResultFolderId() != null
                        && !model.getResultFolderId().isEmpty()) {
                    JSONObject context = new JSONObject();
                    context.put(DataWindowConfig.FOLDER_ID, new JSONString(model.getResultFolderId()));

                    MyDataViewContextExecutor contextExec = new MyDataViewContextExecutor();
                    contextExec.execute(context.toString());
                }
            }
        });

        return link;
    }
}