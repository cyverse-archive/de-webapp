package org.iplantc.de.client.views.panels;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uidiskresource.client.models.DiskResource;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.client.events.DiskResourceSelectionChangedEventHandler;
import org.iplantc.de.client.events.ManageDataRefreshEvent;
import org.iplantc.de.client.events.disk.mgmt.DiskResourceSelectedEvent;
import org.iplantc.de.client.events.disk.mgmt.DiskResourceSelectedEventHandler;
import org.iplantc.de.client.images.Resources;
import org.iplantc.de.client.utils.DataUtils;
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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class DataMainToolBar extends ToolBar {
    private static final String BTN_ACTIONS_ID = "idDataMainToolBarActions"; //$NON-NLS-1$

    private final ArrayList<HandlerRegistration> handlers = new ArrayList<HandlerRegistration>();

    private final String tag;
    private final DataContainer container;
    private final DataActionsMenu menuActions;

    private TextField<String> filterField;
    private Button btnRefresh;
    private Button btnActions;

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
        filterField = buildFilterField();
        add(filterField);
        add(new FillToolItem());
        btnActions = buildActionsButton();
        add(btnActions);
    }

    private Button buildActionsButton() {
        final Button btn = new Button(I18N.DISPLAY.moreActions());
        btn.setId(BTN_ACTIONS_ID);
        btn.setMenu(menuActions);
        btn.setIcon(AbstractImagePrototype.create(Resources.ICONS.dataActionMenuIcon()));
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
        handlers.add(eventbus.addHandler(DiskResourceSelectedEvent.TYPE,
                new DiskResourceSelectedEventHandler() {

                    @Override
                    public void onSelected(DiskResourceSelectedEvent event) {
                        // update actions on new page
                        updateActionsButton(null);
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
    private TextField<String> buildFilterField() {
        TextField<String> filter = new TextField<String>() {
            @Override
            public void onKeyUp(FieldEvent fe) {
                String filter = getValue();
                if (filter != null && !filter.isEmpty()) {
                    container.getDataStore().filter("name", filter); //$NON-NLS-1$
                } else {
                    container.getDataStore().clearFilters();
                }

            }
        };

        filter.setEmptyText(I18N.DISPLAY.filterDataList());

        return filter;
    }

    /**
     * Clears the toolbar's filtering text field.
     */
    public void clearFilters() {
        filterField.clear();
        container.getDataStore().clearFilters();
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
        menuActions.setMaskingParent(maskingParent);
    }
}
