package org.iplantc.de.client.views.panels;

import java.util.List;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uidiskresource.client.models.DiskResource;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.client.events.DiskResourceSelectionChangedEventHandler;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.google.gwt.json.client.JSONObject;

public class DataDetailsPanel extends AbstractDataPanel {
    private DataDetailListPanel pnlDetails;
    private SearchHistoryPanel searchHistoryPanel;

    public DataDetailsPanel(final String tag) {
        super(tag);

        initListeners();

        initPanels();
    }

    public void setSearchHistory(JSONObject obj) {
        searchHistoryPanel.setSearchHistory(obj);
    }

    public JSONObject getSearchHistory() {
        return searchHistoryPanel.getSearchHistory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init() {
        setBodyStyle("background-color: #EDEDED"); //$NON-NLS-1$
    }

    private void initListeners() {
        addListener(Events.Resize, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                if (pnlDetails != null) {
                    pnlDetails.layout();
                }
                if (searchHistoryPanel != null) {
                    searchHistoryPanel.layout();
                }
            }
        });
    }

    private void initPanels() {
        pnlDetails = new DataDetailListPanel();
        searchHistoryPanel = new SearchHistoryPanel();
    }

    @Override
    protected void registerHandlers() {
        super.registerHandlers();

        EventBus eventbus = EventBus.getInstance();

        handlers.add(eventbus.addHandler(DiskResourceSelectionChangedEvent.TYPE,
                new DiskResourceSelectionChangedEventHandler() {
                    @Override
                    public void onChange(DiskResourceSelectionChangedEvent event) {
                        if (event.getTag().equals(tag)) {
                            List<DiskResource> resources = event.getSelected();

                            pnlDetails.update(resources);
                        }
                    }
                }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void compose() {
        add(pnlDetails);
        add(searchHistoryPanel);
    }

    /**
     * clear details panel
     * 
     */
    public void resetDeatils() {
        pnlDetails.update(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setHeading() {
        setHeading(I18N.DISPLAY.details());
    }

}
