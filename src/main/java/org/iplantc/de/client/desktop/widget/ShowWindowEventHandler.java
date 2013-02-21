package org.iplantc.de.client.desktop.widget;

import org.iplantc.core.uiapplications.client.events.CreateNewAppEvent;
import org.iplantc.core.uiapplications.client.events.CreateNewAppEvent.CreateNewAppEventHandler;
import org.iplantc.core.uiapplications.client.events.EditAppEvent;
import org.iplantc.core.uiapplications.client.events.EditAppEvent.EditAppEventHandler;
import org.iplantc.core.uiapplications.client.events.RunAppEvent;
import org.iplantc.core.uiapplications.client.events.RunAppEvent.RunAppEventHandler;
import org.iplantc.core.uiapplications.client.events.handlers.CreateNewWorkflowEventHandler;
import org.iplantc.core.uidiskresource.client.events.ShowFilePreviewEvent;
import org.iplantc.core.uidiskresource.client.events.ShowFilePreviewEvent.ShowFilePreviewEventHandler;
import org.iplantc.de.client.events.ShowAboutWindowEvent;
import org.iplantc.de.client.events.ShowAboutWindowEvent.ShowAboutWindowEventHandler;
import org.iplantc.de.client.events.WindowShowRequestEvent;
import org.iplantc.de.client.events.WindowShowRequestEvent.WindowShowRequestEventHandler;
import org.iplantc.de.client.views.windows.configs.AppWizardConfig;
import org.iplantc.de.client.views.windows.configs.AppsIntegrationWindowConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.FileViewerWindowConfig;

import com.google.web.bindery.autobean.shared.Splittable;

final class ShowWindowEventHandler implements ShowAboutWindowEventHandler, ShowFilePreviewEventHandler,
 CreateNewAppEventHandler, CreateNewWorkflowEventHandler, WindowShowRequestEventHandler,
 RunAppEventHandler, EditAppEventHandler {
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
    public void onWindowShowRequest(WindowShowRequestEvent event) {
        desktop.showWindow(event.getWindowConfig(), event.updateWithConfig());
    }

    @Override
    public void onRunAppActionInitiated(RunAppEvent event) {
        AppWizardConfig config = ConfigFactory.appWizardConfig(event.getAppToRun().getId());
        desktop.showWindow(config);
    }

    @Override
    public void createNewApp(CreateNewAppEvent event) {
        desktop.showWindow(ConfigFactory.appsIntegrationWindowConfig("NEW_APP_TEMPLATE"));
    }

    @Override
    public void onEditApp(EditAppEvent event) {
        AppsIntegrationWindowConfig config = ConfigFactory.appsIntegrationWindowConfig(event.getAppToEdit().getId());
        Splittable legacyAppTemplate = event.getLegacyAppTemplate();
        config.setLegacyAppTemplateJson(legacyAppTemplate);
        desktop.showWindow(config);
    }

    @Override
    public void createNewWorkflow() {
        desktop.showWindow(ConfigFactory.workflowIntegrationWindowConfig());
    }
}