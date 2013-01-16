package org.iplantc.de.client.desktop.widget;

import org.iplantc.core.uiapplications.client.events.AppLoadEvent;
import org.iplantc.core.uiapplications.client.events.AppLoadEvent.AppLoadEventHandler;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.models.TitoWindowConfig;

final class AppLoadEventHandlerImpl implements AppLoadEventHandler {

    private final Desktop desktop;

    AppLoadEventHandlerImpl(Desktop desktop) {
        this.desktop = desktop;
    }

    @Override
    public void onLoad(AppLoadEvent event) {
        String viewMode = null;
        if (event.getMode() == AppLoadEvent.MODE.EDIT) {
            viewMode = TitoWindowConfig.VIEW_APP_EDIT;
        }

        // TBI JDS
        TitoWindowConfig config = new TitoWindowConfig(null);

        desktop.showWindow(Constants.CLIENT.titoTag(), config);
        // this.titoController.dispatcher.launchTitoWindow(viewMode, event.getIdTemplate());
    }
}