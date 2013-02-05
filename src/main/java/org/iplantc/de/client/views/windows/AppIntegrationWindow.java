package org.iplantc.de.client.views.windows;

import java.util.List;

import org.iplantc.core.appsIntegration.client.presenter.AppsIntegrationPresenterImpl;
import org.iplantc.core.appsIntegration.client.view.AppsIntegrationView;
import org.iplantc.core.appsIntegration.client.view.AppsIntegrationViewImpl;
import org.iplantc.core.uicommons.client.models.autobeans.WindowState;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.views.windows.configs.AppsIntegrationWindowConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * 
 * A window for Tito editor
 * TBI JDS
 * 
 * @author sriram
 * 
 */
public class AppIntegrationWindow extends IplantWindowBase {
    // private TitoPanel tito;

    private final AppsIntegrationView.Presenter presenter;
    protected List<HandlerRegistration> handlers;

    public AppIntegrationWindow(String tag, AppsIntegrationWindowConfig config) {
        super(null, null);

        AppsIntegrationView view = new AppsIntegrationViewImpl();
        presenter = new AppsIntegrationPresenterImpl(view);

        setTitle(I18N.DISPLAY.createApps());
        setSize("800", "600");

        init();
    }

    private void init() {
        // TextBox tb = new TextBox();
        // tb.setText("Work in progress. The \"Tito\" library is being refactored.");
        // add(tb);
    }

    @Override
    public WindowState getWindowState() {
        AppsIntegrationWindowConfig config = ConfigFactory.appsIntegrationWindowConfig();
        return createWindowState(config);
    }
}
