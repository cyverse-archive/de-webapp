package org.iplantc.de.client.desktop.widget;

import org.iplantc.core.uiapplications.client.events.AppLoadEvent;
import org.iplantc.core.uiapplications.client.events.AppLoadEvent.AppLoadEventHandler;
import org.iplantc.core.uiapplications.client.events.handlers.CreateNewAppEventHandler;
import org.iplantc.core.uidiskresource.client.events.ShowFilePreviewEvent;
import org.iplantc.core.uidiskresource.client.events.ShowFilePreviewEvent.ShowFilePreviewEventHandler;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.events.ShowAboutWindowEvent;
import org.iplantc.de.client.events.ShowAboutWindowEvent.ShowAboutWindowEventHandler;
import org.iplantc.de.client.models.TitoWindowConfig;
import org.iplantc.de.client.models.ViewerWindowConfig;

final class ShowWindowEventHandler implements ShowAboutWindowEventHandler, ShowFilePreviewEventHandler, CreateNewAppEventHandler, AppLoadEventHandler {
    private final Desktop desktop;

    ShowWindowEventHandler(Desktop desktop) {
        this.desktop = desktop;
    }

    @Override
    public void showAboutWindowRequested(ShowAboutWindowEvent event) {
        desktop.showWindow(Constants.CLIENT.myAboutTag(), null);
    }

    @Override
    public void showFilePreview(ShowFilePreviewEvent event) {
        ViewerWindowConfig config = new ViewerWindowConfig();
        config.setFile(event.getFile());
        config.setShowTreeTab(false);
        desktop.showWindow(Constants.CLIENT.dataViewerTag(), config);
    }

    @Override
    public void createNewApp() {
        // TBI JDS
        TitoWindowConfig config = new TitoWindowConfig(null);

        desktop.showWindow(Constants.CLIENT.titoTag(), config);
        // this.titoController.dispatcher.launchTitoWindow(TitoWindowConfig.VIEW_NEW_TOOL, null);
        // Just build the config and showWindow from here
    }

    @Override
    public void onLoad(AppLoadEvent event) {
        // String viewMode = null;
        // if (event.getMode() == AppLoadEvent.MODE.EDIT) {
        // viewMode = TitoWindowConfig.VIEW_APP_EDIT;
        // }

        // TBI JDS
        TitoWindowConfig config = new TitoWindowConfig(null);

        desktop.showWindow(Constants.CLIENT.titoTag(), config);
        // this.titoController.dispatcher.launchTitoWindow(viewMode, event.getIdTemplate());
    }
}