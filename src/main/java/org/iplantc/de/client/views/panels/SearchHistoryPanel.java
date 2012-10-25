/**
 * 
 */
package org.iplantc.de.client.views.panels;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.core.client.widgets.Hyperlink;
import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.events.DataSearchHistorySelectedEvent;
import org.iplantc.de.client.events.DataSearchResultSelectedEvent;
import org.iplantc.de.client.events.DataSearchResultSelectedEventHandler;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

/**
 * @author sriram
 * 
 */
public class SearchHistoryPanel extends ContentPanel {

    List<String> searchHistory;

    public SearchHistoryPanel() {
        init();

        EventBus eventbus = EventBus.getInstance();

        eventbus.addHandler(DataSearchResultSelectedEvent.TYPE,
                new DataSearchResultSelectedEventHandler() {

                    @Override
                    public void onSelection(DataSearchResultSelectedEvent event) {
                        update(event.getSearchTerm());
                    }
                });
    }

    /**
     * Initializes this content panel.
     */
    private void init() {
        setHeaderVisible(true);
        searchHistory = new ArrayList<String>();
        setHeading(I18N.DISPLAY.searchHistory());
        setBodyStyle("background-color: #EDEDED"); //$NON-NLS-1$
        setLayout(new FitLayout());
        setScrollMode(Scroll.AUTOY);
    }

    private void update(String searchTerm) {
        searchHistory.add(searchTerm);
        renderHistory();
    }

    private void renderHistory() {
        removeAll();
        for (String term : searchHistory) {
            Hyperlink link = new Hyperlink(term, "de_search_history");
            link.addListener(Events.OnClick, new HistorySelectedListenerImpl(term));
            HorizontalPanel panel = new HorizontalPanel();
            panel.setSpacing(1);
            Hyperlink linkRmv = buildRemoveLink(term);
            panel.add(link);
            panel.add(linkRmv);
            add(panel);
        }
        layout();
    }

    private Hyperlink buildRemoveLink(String term) {
        Hyperlink linkRmv = new Hyperlink("[X]", "de_search_history");
        linkRmv.setToolTip(I18N.DISPLAY.delete());
        linkRmv.addListener(Events.OnClick, new HistoryRemoveListenerImpl(term));
        return linkRmv;
    }

    public JSONObject getSearchHistory() {
        JSONObject obj = new JSONObject();
        if (searchHistory.size() > 0) {
            obj.put("data-search", JsonUtil.buildArrayFromStrings(searchHistory));
        } else {
            obj.put("data-search", new JSONArray());
        }
        return obj;
    }

    public void setSearchHistory(JSONObject obj) {
        if (obj != null) {
            JSONArray arr = JsonUtil.getArray(obj, "data-search");
            if (arr != null) {
                for (int i = 0; i < arr.size(); i++) {
                    searchHistory.add(JsonUtil.trim(arr.get(i).isString().toString()));
                }
            }
        }

        renderHistory();
    }

    private final class HistoryRemoveListenerImpl implements Listener<BaseEvent> {
        private String history;

        public HistoryRemoveListenerImpl(String history) {
            this.history = history;
        }

        @Override
        public void handleEvent(BaseEvent be) {
            searchHistory.remove(history);
            renderHistory();
        }
    }

    private final class HistorySelectedListenerImpl implements Listener<BaseEvent> {
        private String history;

        public HistorySelectedListenerImpl(String history) {
            this.history = history;
        }

        @Override
        public void handleEvent(BaseEvent be) {
            DataSearchHistorySelectedEvent event = new DataSearchHistorySelectedEvent(history);
            EventBus.getInstance().fireEvent(event);
        }
    }

}
