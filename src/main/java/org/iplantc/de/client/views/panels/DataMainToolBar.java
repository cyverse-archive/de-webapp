package org.iplantc.de.client.views.panels;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uicommons.client.models.DEProperties;
import org.iplantc.core.uidiskresource.client.models.DiskResource;
import org.iplantc.core.uidiskresource.client.models.File;
import org.iplantc.core.uidiskresource.client.models.Folder;
import org.iplantc.core.uidiskresource.client.models.Permissions;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.events.DataSearchHistorySelectedEvent;
import org.iplantc.de.client.events.DataSearchHistorySelectedEventHandler;
import org.iplantc.de.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.client.events.DiskResourceSelectionChangedEventHandler;
import org.iplantc.de.client.events.LoadDataSearchResultsEvent;
import org.iplantc.de.client.events.ManageDataRefreshEvent;
import org.iplantc.de.client.images.Resources;
import org.iplantc.de.client.models.JsSearchResult;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.utils.DataUtils;
import org.iplantc.de.client.utils.NotifyInfo;
import org.iplantc.de.client.utils.PanelHelper;
import org.iplantc.de.client.views.DataActionsMenu;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class DataMainToolBar extends ToolBar {
    private static final String BTN_ACTIONS_ID = "idDataMainToolBarActions"; //$NON-NLS-1$

    private final ArrayList<HandlerRegistration> handlers = new ArrayList<HandlerRegistration>();

    private final String tag;
    private final DataContainer container;
    private final DataActionsMenu menuActions;

    private TextField<String> searchField;
    private Button btnRefresh;
    private Button btnActions;
    private Component maskingParent;

    public DataMainToolBar(final String tag, final DataContainer container, DataActionsMenu menu) {
        this.tag = tag;
        this.container = container;
        menuActions = menu;
        setSpacing(4);
        addButtons();
        registerHandlers();
    }

    private void addButtons() {
        btnRefresh = buildRefreshButton();
        add(btnRefresh);
        searchField = buildSearchField();
        add(searchField);
        add(new FillToolItem());
        btnActions = buildActionsButton();
        add(btnActions);
    }

    private Button buildActionsButton() {
        final Button btn = new Button(I18N.DISPLAY.moreActions()) {
            @Override
            protected String getMenuClass() {
                return "more-actions-menu-icon"; //$NON-NLS-1$
            }
        };
        btn.setId(BTN_ACTIONS_ID);
        btn.setMenu(menuActions);
        btn.disable();
        return btn;
    }

    private Button buildRefreshButton() {
        Button refresh = PanelHelper.buildButton("idRefresh", I18N.DISPLAY.refresh(), //$NON-NLS-1$
                new RereshButtonListener());
        refresh.setIcon(AbstractImagePrototype.create(Resources.ICONS.refresh()));
        return refresh;
    }

    // changed from private to public so that i can re-add handlers after refresh.
    public void registerHandlers() {
        EventBus eventbus = EventBus.getInstance();

        handlers.add(eventbus.addHandler(DiskResourceSelectionChangedEvent.TYPE,
                new DiskResourceSelectionChangedEventHandler() {
                    @Override
                    public void onChange(final DiskResourceSelectionChangedEvent event) {
                        if (event.getTag().equals(tag)) {
                            updateActionsButton(event.getSelected());
                        }
                    }
                }));
        handlers.add(eventbus.addHandler(DataSearchHistorySelectedEvent.TYPE,
                new DataSearchHistorySelectedEventHandler() {

                    @Override
                    public void onSelection(DataSearchHistorySelectedEvent event) {
                        searchField.setValue(event.getSearchHistoryTerm());
                        doSearch(event.getSearchHistoryTerm());

                    }
                }));

        menuActions.cleanup();
        menuActions.registerHandlers();
    }

    private void updateActionsButton(final List<DiskResource> resources) {
        final List<DataUtils.Action> actions = DataUtils.getSupportedActions(resources,
                container.getCurrentPath());

        if (actions.isEmpty()) {
            btnActions.disable();
        } else {
            btnActions.enable();
        }
    }

    void cleanup() {
        menuActions.cleanup();

        // unregister
        for (HandlerRegistration reg : handlers) {
            reg.removeHandler();
        }

        // clear our list
        handlers.clear();
    }

    /**
     * Builds a text field for filtering items displayed in the data container.
     */
    private TextField<String> buildSearchField() {
        TextField<String> filterField = new TextField<String>() {
            @Override
            public void onKeyUp(FieldEvent fe) {
                String filter = getValue();
                // TODO temp. remove data search
                // if (filter != null && filter.length() >= 3) {
                // doSearch(filter);
                // }
                if (filter != null && !filter.isEmpty()) {
                    container.getDataStore().filter("name", filter); //$NON-NLS-1$
                } else {
                    container.getDataStore().clearFilters();
                }
            }
        };

        filterField.setEmptyText(I18N.DISPLAY.dataSearch());
        return filterField;
    }

    private void doSearch(final String term) {
        DiskResourceServiceFacade facade = new DiskResourceServiceFacade();
        maskContainer();
        String type = (tag.equalsIgnoreCase(I18N.CONSTANT.selectAFile()) ? "file" : null);
        facade.search(term, DEProperties.getInstance().getMaxSearchResults(), type,
                new AsyncCallback<String>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        ErrorHandler.post(I18N.ERROR.searchError(), caught);
                        unmaskContainer();
                    }

                    @Override
                    public void onSuccess(String result) {
                        JSONObject obj = JsonUtil.getObject(result);
                        int total = JsonUtil.getNumber(obj, "total").intValue();
                        JsArray<JsSearchResult> resultsArr = JsonUtil.asArrayOf(JsonUtil.getArray(obj,
                                "hits").toString());
                        List<DiskResource> resources = buildDataSearchResultset(resultsArr);
                        if (resources.size() < total) {
                            NotifyInfo.display(I18N.DISPLAY.searching() + " " + term,
                                    I18N.DISPLAY.searchThresholdMsg(DEProperties.getInstance()
                                            .getMaxSearchResults()));
                        }
                        LoadDataSearchResultsEvent event = new LoadDataSearchResultsEvent(term,
                                resources);
                        EventBus.getInstance().fireEvent(event);
                        unmaskContainer();

                    }

                });
    }

    private void maskContainer() {
        if (maskingParent != null) {
            maskingParent.mask(I18N.DISPLAY.searching());
        }
    }

    private void unmaskContainer() {
        if (maskingParent != null) {
            maskingParent.unmask();
        }
    }

    private List<DiskResource> buildDataSearchResultset(JsArray<JsSearchResult> results) {
        List<DiskResource> resources = new ArrayList<DiskResource>();
        for (int i = 0; i < results.length(); i++) {
            JsSearchResult result = results.get(i);
            if (result.getType().equalsIgnoreCase("file")) {
                resources.add(buildNewFile(result));
            } else if (result.getType().equalsIgnoreCase("folder")) {
                resources.add(buildNewFolder(result));
            }

        }
        return resources;

    }

    private File buildNewFile(JsSearchResult result) {
        File f = new File(result.getId(), result.getName(), new Permissions(true, false, false));
        return f;
    }

    private Folder buildNewFolder(JsSearchResult result) {
        Folder f = new Folder(result.getId(), result.getName(), false, new Permissions(true, false,
                false));
        return f;
    }

    private class RereshButtonListener extends SelectionListener<ButtonEvent> {

        @Override
        public void componentSelected(ButtonEvent ce) {
            ManageDataRefreshEvent event = new ManageDataRefreshEvent(tag, container.getCurrentPath(),
                    container.getSelectedItems());
            EventBus.getInstance().fireEvent(event);
        }

    }

    public final void setMaskingParent(final Component maskingParent) {
        this.maskingParent = maskingParent;
        menuActions.setMaskingParent(maskingParent);
    }

    public void setRefreshButtonState(boolean enable) {
        btnRefresh.setEnabled(enable);
    }
}
