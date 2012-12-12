package org.iplantc.de.client.views.panels;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uicommons.client.models.UserInfo;
import org.iplantc.core.uicommons.client.widgets.SearchField;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.images.Resources;
import org.iplantc.de.client.models.AnalysisExecution;
import org.iplantc.de.client.models.JsAnalysisExecution;
import org.iplantc.de.client.services.AnalysisServiceFacade;
import org.iplantc.de.client.utils.NotificationHelper;
import org.iplantc.de.client.utils.NotifyInfo;
import org.iplantc.de.client.views.DEPagingToolbar;
import org.iplantc.de.client.views.MyAnalysesGrid;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseFilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * A container panel of MyAanalysesGrid
 * 
 * @author sriram
 * 
 */
public class MyAnalysesPanel extends ContentPanel {
    private final String DELETE_ITEM_ID = "idDeleteBtn"; //$NON-NLS-1$
    private final String CANCEL_ANALYSIS_ITEM_ID = "idCancelAnalysisBtn"; //$NON-NLS-1$
    private static final String VIEW_PARAMETER_ITEM_ID = "idViewParameter"; //$NON-NLS-1$

    private MyAnalysesGrid analysisGrid;
    private PagingLoader<PagingLoadResult<AnalysisExecution>> remoteLoader;

    private final HashMap<String, Button> analyses_buttons;
    private final HashMap<String, Menu> menus;

    private ToolBar topComponentMenu;

    private ArrayList<HandlerRegistration> handlers;

    private String idWorkspace;

    private String idCurrentSelection;

    private final AnalysisServiceFacade facadeAnalysisService;

    protected static CheckBoxSelectionModel<AnalysisExecution> sm;
    private SearchField filterField;

    private Status status;

    private DEPagingToolbar pagingToolbar;

    /**
     * Indicates the status of an analysis.
     */
    public static enum EXECUTION_STATUS {
        /** analysis status unknown */
        UNKNOWN(I18N.CONSTANT.unknown()),
        /** analysis is ready */
        SUBMITTED(I18N.CONSTANT.submitted()),
        /** analysis is running */
        RUNNING(I18N.CONSTANT.running()),
        /** analysis is complete */
        COMPLETED(I18N.CONSTANT.completed()),
        /** analysis timed out */
        HELD(I18N.CONSTANT.held()),
        /** analysis failed */
        FAILED(I18N.CONSTANT.failed()),
        /** analysis was stopped */
        SUBMISSION_ERR(I18N.CONSTANT.subErr()),
        /** analysis is idle */
        IDLE(I18N.CONSTANT.idle()),
        /** analysis is removed */
        REMOVED(I18N.CONSTANT.removed());

        private String displayText;

        private EXECUTION_STATUS(String displaytext) {
            this.displayText = displaytext;
        }

        /**
         * Returns a string that identifies the EXECUTION_STATUS.
         * 
         * @return
         */
        public String getTypeString() {
            return toString().toLowerCase();
        }

        /**
         * Null-safe and case insensitive variant of valueOf(String)
         * 
         * @param typeString name of an EXECUTION_STATUS constant
         * @return
         */
        public static EXECUTION_STATUS fromTypeString(String typeString) {
            if (typeString == null || typeString.isEmpty()) {
                return null;
            }

            return valueOf(typeString.toUpperCase());
        }

        @Override
        public String toString() {
            return displayText;
        }
    }

    /**
     * Create a new MyAnalysisPanel
     * 
     * @param caption text to be displayed as caption
     * @param idCurrentSelection
     * 
     */
    public MyAnalysesPanel(final String caption, final String idCurrentSelection) {
        this.idCurrentSelection = idCurrentSelection;
        analyses_buttons = new LinkedHashMap<String, Button>();
        menus = new HashMap<String, Menu>();
        init(caption);
        initWorkspaceId();

        facadeAnalysisService = new AnalysisServiceFacade();
    }

    private void initWorkspaceId() {
        idWorkspace = UserInfo.getInstance().getWorkspaceId();
    }

    private void init(String caption) {
        setHeading(caption);
        setLayout(new FitLayout());

        initRemoteLoader();
        buildTopComponent();
        setTopComponent(topComponentMenu);

        registerHandlers();
    }

    private void registerHandlers() {
        handlers = new ArrayList<HandlerRegistration>();
    }

    private void buildTopComponent() {
        topComponentMenu = new ToolBar();
        topComponentMenu.setHeight(30);
        topComponentMenu.add(buildViewParamsButton());
        topComponentMenu.add(new SeparatorToolItem());
        topComponentMenu.add(buildDeleteButton());
        topComponentMenu.add(buildCancelAnalysisButton());
        topComponentMenu.add(buildFilterField());
        topComponentMenu.add(new FillToolItem());
        Button refreshBtn = pagingToolbar.getRefreshButton();
        refreshBtn.setText(I18N.DISPLAY.refresh());
        topComponentMenu.add(refreshBtn);
        buildStatusBar();
        topComponentMenu.add(status);
    }

    private void buildStatusBar() {
        status = new Status();
        status.setBox(true);
    }

    private void setButtonState() {
        int selectionSize = 0;

        if (analysisGrid != null) {
            selectionSize = analysisGrid.getSelectionModel().getSelectedItems().size();
        }

        switch (selectionSize) {
            case 0:
                for (Button btn : analyses_buttons.values()) {
                    btn.disable();
                }

                break;

            case 1:
                enableDeleteButtonByStatus();
                enableViewButtonByStatus();
                enableCancelAnalysisButtonByStatus();
                break;

            default:
                analyses_buttons.get(DELETE_ITEM_ID).enable();
                analyses_buttons.get(VIEW_PARAMETER_ITEM_ID).disable();
                enableCancelAnalysisButtonByStatus();
        }
    }

    private Button buildDeleteButton() {
        Button b = new Button(I18N.DISPLAY.delete());
        b.setId(DELETE_ITEM_ID);
        b.setIcon(AbstractImagePrototype.create(Resources.ICONS.cancel()));
        b.setEnabled(false);
        b.addSelectionListener(new DeleteSelectionListener());
        analyses_buttons.put(DELETE_ITEM_ID, b);
        return b;
    }

    private Button buildCancelAnalysisButton() {
        Button b = new Button(I18N.DISPLAY.cancelAnalysis());
        b.setId(CANCEL_ANALYSIS_ITEM_ID);
        b.setIcon(AbstractImagePrototype.create(Resources.ICONS.stop()));
        b.setEnabled(false);
        b.addSelectionListener(new CancelAnalysisSelectListener());
        analyses_buttons.put(CANCEL_ANALYSIS_ITEM_ID, b);

        return b;
    }

    private Button buildViewParamsButton() {
        Button b = new Button(I18N.DISPLAY.viewParamLbl());
        b.setId(VIEW_PARAMETER_ITEM_ID);
        b.setIcon(AbstractImagePrototype.create(Resources.ICONS.fileView()));
        b.setEnabled(false);
        b.addSelectionListener(new ViewParamSelectionListener());
        analyses_buttons.put(VIEW_PARAMETER_ITEM_ID, b);
        return b;
    }

    /**
     * Builds a text field for filtering items displayed in the data container.
     */
    private TextField<String> buildFilterField() {
        filterField = new SearchField("name"); //$NON-NLS-1$
        filterField.setEmptyText(I18N.DISPLAY.filterAnalysesList());
        filterField.setMaxLength(255);

        return filterField;
    }

    /**
     * Initializes the RpcProxy and BasePagingLoader to support paging for the AnalysesGrid.
     */
    private void initRemoteLoader() {
        RpcProxy<PagingLoadResult<AnalysisExecution>> proxy = new RpcProxy<PagingLoadResult<AnalysisExecution>>() {

            @Override
            protected void load(Object loadConfig,
                    final AsyncCallback<PagingLoadResult<AnalysisExecution>> callback) {
                final FilterPagingLoadConfig pagingConfig = (FilterPagingLoadConfig)loadConfig;

                mask(I18N.DISPLAY.loadingMask());

                AnalysisServiceFacade facade = new AnalysisServiceFacade();

                facade.getAnalyses(UserInfo.getInstance().getWorkspaceId(), pagingConfig,
                        new GetAnalysesServiceCallback(callback, pagingConfig.getOffset()));
            }
        };

        remoteLoader = new BasePagingLoader<PagingLoadResult<AnalysisExecution>>(proxy) {

            @Override
            protected Object newLoadConfig() {
                return new BaseFilterPagingLoadConfig();
            }
        };

        remoteLoader.setRemoteSort(true);

        pagingToolbar = new DEPagingToolbar(10);
        pagingToolbar.bind(remoteLoader);

        setBottomComponent(pagingToolbar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);

        buildCheckBoxSelectionModel(menus);
        analysisGrid = MyAnalysesGrid.createInstance(sm, remoteLoader);
        if (getIdCurrentSelection() != null && analysisGrid.getStore() != null) {
            analysisGrid.setCurrentSelection(getIdCurrentSelection());
        }

        analysisGrid.addPlugin(buildGridFilters());

        analysisGrid.getView().setEmptyText(I18N.DISPLAY.noAnalyses());
        add(analysisGrid);
        addGridEventListeners();

        remoteLoader.load();
    }

    private GridFilters buildGridFilters() {
        GridFilters filters = new GridFilters() {
            @Override
            protected void onContextMenu(GridEvent<?> be) {
                // intentionally do nothing in order to hide the filter fields from the column menus.
            }
        };

        final StringFilter appFilter = new StringFilter("analysis_name"); //$NON-NLS-1$
        final StringFilter nameFilter = filterField.getFilter();

        Listener<BaseEvent> filterListener = new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                appFilter.setValue(nameFilter.getValue());
            }
        };
        nameFilter.addListener(Events.Update, filterListener);
        nameFilter.addListener(Events.Activate, filterListener);

        filters.addFilter(nameFilter);
        filters.addFilter(appFilter);

        return filters;
    }

    /**
     * Builds the CheckBoxSelectionModel used in the ColumnDisplay.ALL ColumnModel.
     * 
     * @param menus2
     */
    protected static void buildCheckBoxSelectionModel(final HashMap<String, Menu> floating_menus) {
        if (sm != null) {
            return;
        }
        sm = new CheckBoxSelectionModel<AnalysisExecution>();
        sm.getColumn().setAlignment(HorizontalAlignment.CENTER);
    }

    @SuppressWarnings({"rawtypes"})
    private void addGridEventListeners() {
        analysisGrid.getSelectionModel().addListener(Events.SelectionChange, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                setButtonState();
                AnalysisExecution ae = analysisGrid.getSelectionModel().getSelectedItem();
                if (ae != null) {
                    idCurrentSelection = ae.getId();
                }
            }
        });
        analysisGrid.getStore().addListener(Store.Update, new Listener<StoreEvent>() {

            @Override
            public void handleEvent(StoreEvent be) {
                setButtonState();
            }
        });
    }

    private void doDelete() {
        if (analysisGrid.getSelectionModel().getSelectedItems().size() > 0) {
            final List<AnalysisExecution> execs = analysisGrid.getSelectionModel().getSelectedItems();
            MessageBox.confirm(I18N.DISPLAY.warning(), I18N.DISPLAY.analysesExecDeleteWarning(),
                    new DeleteMessageBoxListener(execs));
        }
    }

    private void doCancelAnalysis() {
        if (analysisGrid.getSelectionModel().getSelectedItems().size() > 0) {
            final List<AnalysisExecution> execs = analysisGrid.getSelectionModel().getSelectedItems();
            for (AnalysisExecution ae : execs) {
                if (ae.getStatus().equalsIgnoreCase((EXECUTION_STATUS.SUBMITTED.toString()))
                        || ae.getStatus().equalsIgnoreCase((EXECUTION_STATUS.IDLE.toString()))
                        || ae.getStatus().equalsIgnoreCase((EXECUTION_STATUS.RUNNING.toString()))) {
                    facadeAnalysisService
                            .stopAnalysis(ae.getId(), new CancelAnalysisServiceCallback(ae));
                }
            }
        }

    }

    private void enableViewButtonByStatus() {
        AnalysisExecution ae = analysisGrid.getSelectionModel().getSelectedItem();
        if (ae != null) {
            if (ae.getStatus().equalsIgnoreCase((EXECUTION_STATUS.COMPLETED.toString()))
                    || ae.getStatus().equalsIgnoreCase((EXECUTION_STATUS.FAILED.toString()))) {
                analyses_buttons.get(VIEW_PARAMETER_ITEM_ID).enable();
            } else {
                analyses_buttons.get(VIEW_PARAMETER_ITEM_ID).enable();
            }
        }
    }

    private void enableDeleteButtonByStatus() {
        List<AnalysisExecution> aes = analysisGrid.getSelectionModel().getSelectedItems();
        boolean enable = true;
        for (AnalysisExecution ae : aes) {
            if (ae != null) {
                if (!ae.getStatus().equalsIgnoreCase((EXECUTION_STATUS.COMPLETED.toString()))
                        && !ae.getStatus().equalsIgnoreCase((EXECUTION_STATUS.FAILED.toString()))) {
                    enable = false;
                    break;
                }
            }
        }
        analyses_buttons.get(DELETE_ITEM_ID).setEnabled(enable);
    }

    private void enableCancelAnalysisButtonByStatus() {
        List<AnalysisExecution> aes = analysisGrid.getSelectionModel().getSelectedItems();
        boolean enable = false;
        for (AnalysisExecution ae : aes) {
            if (ae != null) {
                if (ae.getStatus().equalsIgnoreCase((EXECUTION_STATUS.SUBMITTED.toString()))
                        || ae.getStatus().equalsIgnoreCase((EXECUTION_STATUS.IDLE.toString()))
                        || ae.getStatus().equalsIgnoreCase((EXECUTION_STATUS.RUNNING.toString()))) {
                    enable = true;
                    break;
                }
            }
        }
        analyses_buttons.get(CANCEL_ANALYSIS_ITEM_ID).setEnabled(enable);

    }

    private class DeleteSelectionListener extends SelectionListener<ButtonEvent> {
        @Override
        public void componentSelected(ButtonEvent ce) {
            doDelete();
        }
    }

    private class CancelAnalysisSelectListener extends SelectionListener<ButtonEvent> {
        @Override
        public void componentSelected(ButtonEvent ce) {
            doCancelAnalysis();
        }
    }

    private class ViewParamSelectionListener extends SelectionListener<ButtonEvent> {
        @Override
        public void componentSelected(ButtonEvent ce) {
            AnalysisExecution ae = analysisGrid.getSelectionModel().getSelectedItem();
            Dialog d = new Dialog();
            d.setModal(true);
            d.setLayout(new FitLayout());
            d.setResizable(false);
            d.setHeading(I18N.DISPLAY.viewParameters(ae.getName()));
            d.add(new AnalysisParameterViewerPanel(ae.getId()));
            d.setSize(520, 375);
            d.setButtons(Dialog.OK);
            d.setHideOnButtonClick(true);
            d.show();
        }
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
        analysisGrid.cleanup();
    }

    /**
     * update id of the execution that needs to selected
     * 
     * @param id id of the analysis execution that needs to selected
     */
    public void updateSelection(String idCurrentSelection) {
        if (analysisGrid != null) {
            analysisGrid.selectModel(idCurrentSelection);
        }
    }

    /**
     * @return the idCurrentSelection
     */
    public String getIdCurrentSelection() {
        return idCurrentSelection;
    }

    private void setStatus(JSONObject resultObj) {
        status.clearStatus(I18N.DISPLAY.lastUpdated()
                + ": " //$NON-NLS-1$
                + DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM).format(
                        new Date(Long.parseLong(JsonUtil.getString(resultObj, "timestamp"))))); //$NON-NLS-1$
    }

    private final class DeleteSeviceCallback implements AsyncCallback<String> {
        private final List<AnalysisExecution> execs;
        private final List<AnalysisExecution> items_to_delete;

        private DeleteSeviceCallback(List<AnalysisExecution> items_to_delete,
                List<AnalysisExecution> execs) {
            this.execs = execs;
            this.items_to_delete = items_to_delete;
        }

        @Override
        public void onSuccess(String arg0) {
            updateGrid(execs);

        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(I18N.ERROR.deleteAnalysisError(), caught);
        }

        private void updateGrid(List<AnalysisExecution> execs) {
            for (AnalysisExecution ae : items_to_delete) {
                analysisGrid.getStore().remove(ae);
            }

            if (items_to_delete == null || execs.size() != items_to_delete.size()) {
                MessageBox.alert(I18N.DISPLAY.warning(), I18N.DISPLAY.analysesNotDeleted(), null);
            }
        }
    }

    private final class CancelAnalysisServiceCallback implements AsyncCallback<String> {

        private final AnalysisExecution ae;

        public CancelAnalysisServiceCallback(final AnalysisExecution ae) {
            this.ae = ae;
        }

        @Override
        public void onSuccess(String result) {
            NotifyInfo.notify(NotificationHelper.Category.ANALYSIS, I18N.DISPLAY.success(),
                    I18N.DISPLAY.analysisStopSuccess(ae.getName()), null);
        }

        @Override
        public void onFailure(Throwable caught) {
            /*
             * JDS Send generic error message. In the future, the "error_code" string should be parsed
             * from the JSON to provide more detailed user feedback.
             */
            ErrorHandler.post(I18N.ERROR.stopAnalysisError(ae.getName()), caught);
        }

    }

    private final class DeleteMessageBoxListener implements Listener<MessageBoxEvent> {
        private final List<AnalysisExecution> execs;
        private final List<AnalysisExecution> items_to_delete;

        private DeleteMessageBoxListener(List<AnalysisExecution> execs) {
            this.execs = execs;
            items_to_delete = new ArrayList<AnalysisExecution>();
        }

        @Override
        public void handleEvent(MessageBoxEvent ce) {
            Button btn = ce.getButtonClicked();

            // did the user click yes?
            if (btn.getItemId().equals(Dialog.YES)) {
                String body = buildDeleteRequestBody(execs);
                facadeAnalysisService.deleteAnalysis(idWorkspace, body, new DeleteSeviceCallback(
                        items_to_delete, execs));
            }
        }

        private String buildDeleteRequestBody(List<AnalysisExecution> execs) {
            JSONObject obj = new JSONObject();
            JSONArray items = new JSONArray();
            int count = 0;
            for (AnalysisExecution ae : execs) {
                if (ae.getStatus().equalsIgnoreCase((EXECUTION_STATUS.COMPLETED.toString()))
                        || ae.getStatus().equalsIgnoreCase((EXECUTION_STATUS.FAILED.toString()))) {
                    items.set(count++, new JSONString(ae.getId()));
                    items_to_delete.add(ae);
                }

            }
            obj.put("executions", items); //$NON-NLS-1$
            return obj.toString();
        }
    }

    /**
     * An AsyncCallback for the AnalysisServiceFacade that will load paged results into the AnalysesGrid.
     * 
     * @author psarando
     * 
     */
    private final class GetAnalysesServiceCallback implements AsyncCallback<String> {
        private final AsyncCallback<PagingLoadResult<AnalysisExecution>> pagingCallback;
        private final int offset;

        public GetAnalysesServiceCallback(
                AsyncCallback<PagingLoadResult<AnalysisExecution>> pagingCallback, int offset) {
            this.pagingCallback = pagingCallback;
            this.offset = offset;
        }

        @Override
        public void onSuccess(String result) {
            JSONObject jsonResult = JsonUtil.getObject(result);

            JSONArray items = JsonUtil.getArray(jsonResult, "analyses"); //$NON-NLS-1$
            if (items != null) {
                JsArray<JsAnalysisExecution> jsAnalyses = JsonUtil.asArrayOf(items.toString());

                final List<AnalysisExecution> results = new ArrayList<AnalysisExecution>();

                for (int i = 0; i < jsAnalyses.length(); i++) {
                    results.add(new AnalysisExecution(jsAnalyses.get(i)));
                }

                Number totalLength = JsonUtil.getNumber(jsonResult, "total"); //$NON-NLS-1$

                pagingCallback.onSuccess(new AnalysesPagingLoadResult(results, totalLength, offset));
            }

            setStatus(jsonResult);
            unmask();
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(I18N.DISPLAY.analysesRetrievalFailure(), caught);
            pagingCallback.onFailure(caught);
            unmask();
        }
    }

    /**
     * A PagingLoadResult for loading paged results into the AnalysesGrid.
     * 
     * @author psarando
     * 
     */
    private final class AnalysesPagingLoadResult implements PagingLoadResult<AnalysisExecution> {
        private final List<AnalysisExecution> results;
        private Number totalResultsLength;
        private int offset;

        AnalysesPagingLoadResult(List<AnalysisExecution> results, Number totalResultsLength, int offset) {
            this.results = results;
            this.totalResultsLength = totalResultsLength;
            this.offset = offset;
        }

        @Override
        public List<AnalysisExecution> getData() {
            return results;
        }

        @Override
        public void setTotalLength(int totalLength) {
            this.totalResultsLength = totalLength;
        }

        @Override
        public void setOffset(int offset) {
            this.offset = offset;
        }

        @Override
        public int getTotalLength() {
            return totalResultsLength == null ? results.size() : totalResultsLength.intValue();
        }

        @Override
        public int getOffset() {
            return offset;
        }
    }
}
