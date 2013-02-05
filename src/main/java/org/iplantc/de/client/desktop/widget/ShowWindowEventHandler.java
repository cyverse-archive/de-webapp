package org.iplantc.de.client.desktop.widget;

import org.iplantc.core.uiapplications.client.events.AppLoadEvent;
import org.iplantc.core.uiapplications.client.events.AppLoadEvent.AppLoadEventHandler;
import org.iplantc.core.uiapplications.client.events.RunAppEvent;
import org.iplantc.core.uiapplications.client.events.RunAppEvent.RunAppEventHandler;
import org.iplantc.core.uiapplications.client.events.handlers.CreateNewAppEventHandler;
import org.iplantc.core.uidiskresource.client.events.ShowFilePreviewEvent;
import org.iplantc.core.uidiskresource.client.events.ShowFilePreviewEvent.ShowFilePreviewEventHandler;
import org.iplantc.de.client.events.ShowAboutWindowEvent;
import org.iplantc.de.client.events.ShowAboutWindowEvent.ShowAboutWindowEventHandler;
import org.iplantc.de.client.events.WindowShowRequestEvent;
import org.iplantc.de.client.events.WindowShowRequestEvent.WindowShowRequestEventHandler;
import org.iplantc.de.client.views.windows.configs.AppWizardConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.FileViewerWindowConfig;

final class ShowWindowEventHandler implements ShowAboutWindowEventHandler, ShowFilePreviewEventHandler, CreateNewAppEventHandler, AppLoadEventHandler, WindowShowRequestEventHandler,
        RunAppEventHandler {
    private final Desktop desktop;

    ShowWindowEventHandler(Desktop desktop) {
        this.desktop = desktop;
    }

    @Override
    public void showAboutWindowRequested(ShowAboutWindowEvent event) {
        desktop.showWindow(ConfigFactory.aboutWindowConfig());
    }

    @Override
    public void showFilePreview(ShowFilePreviewEvent event) {
        FileViewerWindowConfig fileViewerWindowConfig = ConfigFactory.fileViewerWindowConfig(event.getFile(), false);
        desktop.showWindow(fileViewerWindowConfig);
    }

    @Override
    public void createNewApp() {
        // TBI JDS Implement apps integration window
    }

    @Override
    public void onLoad(AppLoadEvent event) {
        // String viewMode = null;
        // if (event.getMode() == AppLoadEvent.MODE.EDIT) {
        // viewMode = TitoWindowConfig.VIEW_APP_EDIT;
        // }

        // TBI JDS Implement apps integration window
    }

    @Override
    public void onWindowShowRequest(WindowShowRequestEvent event) {
        desktop.showWindow(event.getWindowConfig());
    }

    @Override
    public void onRunAppActionInitiated(RunAppEvent event) {
        AppWizardConfig config = ConfigFactory.appWizardConfig(event.getAppToRun().getId());
        desktop.showWindow(config);
    }
}