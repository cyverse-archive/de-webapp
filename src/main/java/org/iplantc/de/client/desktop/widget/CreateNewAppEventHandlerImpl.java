package org.iplantc.de.client.desktop.widget;

import org.iplantc.core.uiapplications.client.events.handlers.CreateNewAppEventHandler;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.models.TitoWindowConfig;

final class CreateNewAppEventHandlerImpl implements CreateNewAppEventHandler {

    private final Desktop desktop;

    CreateNewAppEventHandlerImpl(Desktop desktop) {
        this.desktop = desktop;
    }

    @Override
    public void createNewApp() {
        // TBI JDS
        TitoWindowConfig config = new TitoWindowConfig(null);

        desktop.showWindow(Constants.CLIENT.titoTag(), config);
        // this.titoController.dispatcher.launchTitoWindow(TitoWindowConfig.VIEW_NEW_TOOL, null);
        // Just build the config and showWindow from here
    }
}