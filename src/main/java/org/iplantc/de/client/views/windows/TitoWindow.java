package org.iplantc.de.client.views.windows;

import java.util.List;

import org.iplantc.de.client.Constants;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.dispatchers.WindowDispatcher;
import org.iplantc.de.client.factories.EventJSONFactory.ActionType;
import org.iplantc.de.client.factories.WindowConfigFactory;
import org.iplantc.de.client.models.TitoWindowConfig;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * A window for Tito editor
 * TBI JDS
 * 
 * @author sriram
 * 
 */
public class TitoWindow extends Gxt3IplantWindow {
    // private TitoPanel tito;

    protected List<HandlerRegistration> handlers;

    public TitoWindow(String tag, TitoWindowConfig config) {
        super(tag, false, true, true, true);

        this.config = config;

        init();
    }

    private void init() {
        setTitle(I18N.DISPLAY.createApps());
        setSize("800", "600");
        TextBox tb = new TextBox();
        tb.setText("Work in progress. The \"Tito\" library is being refactored.");
        add(tb);
    }

    @Override
    public JSONObject getWindowState() {
        // TitoWindowConfig configData = new TitoWindowConfig(getWindowViewState());
        TitoWindowConfig configData = new TitoWindowConfig(null);

        configData.setView(TitoWindowConfig.VIEW_APP_EDIT_FROM_JSON);
        // configData.setAppJson(tito.getTitoConfig());

        WindowConfigFactory configFactory = new WindowConfigFactory();
        JSONObject windowConfig = configFactory
                .buildWindowConfig(Constants.CLIENT.titoTag(), configData);
        WindowDispatcher dispatcher = new WindowDispatcher(windowConfig);
        return dispatcher.getDispatchJson(Constants.CLIENT.titoTag(), ActionType.DISPLAY_WINDOW);
    }
}
