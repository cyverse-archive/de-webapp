package org.iplantc.de.client;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.desktop.presenter.DEPresenter;
import org.iplantc.de.client.desktop.views.DEView;
import org.iplantc.de.client.desktop.views.DEViewImpl;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Defines the web application entry point for the system.
 * 
 */
public class DiscoveryEnvironment implements EntryPoint {
    /**
     * Entry point for the application.
     */
    @Override
    public void onModuleLoad() {
        RootPanel.get().add(new Image("./images/loading_spinner.gif"));
        setEntryPointTitle();
        DeResources resources = GWT.create(DeResources.class);
        resources.css().ensureInjected();
        DEView view = new DEViewImpl(resources, EventBus.getInstance());
        new DEPresenter(view, resources, EventBus.getInstance());
    }

    /**
     * Set the title element of the root page/entry point.
     * 
     * Enables i18n of the root page.
     */
    private void setEntryPointTitle() {
        Window.setTitle(I18N.DISPLAY.rootApplicationTitle());
    }
}
