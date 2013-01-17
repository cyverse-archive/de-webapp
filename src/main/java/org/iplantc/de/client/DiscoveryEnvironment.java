package org.iplantc.de.client;

import org.iplantc.de.client.desktop.presenter.DEPresenter;
import org.iplantc.de.client.desktop.views.DEView;
import org.iplantc.de.client.desktop.views.DEViewImpl;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

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
        setEntryPointTitle();
        DeResources resources = GWT.create(DeResources.class);
        resources.css().ensureInjected();
        DEView view = new DEViewImpl(resources);
        new DEPresenter(view, resources);
        // HistoryManager mgrHistory = new HistoryManager();
        // String token = History.getToken();
        //
        // if (token == null || token.isEmpty()) {
        //            mgrHistory.handleToken("workspace"); //$NON-NLS-1$
        // } else {
        // mgrHistory.processCommand(token);
        // }
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
