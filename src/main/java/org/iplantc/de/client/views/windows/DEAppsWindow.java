package org.iplantc.de.client.views.windows;

import org.iplantc.core.uiapplications.client.presenter.AppsViewPresenter;
import org.iplantc.core.uiapplications.client.views.AppsView;
import org.iplantc.core.uiapplications.client.views.AppsViewImpl;
import org.iplantc.core.uicommons.client.models.autobeans.WindowState;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.views.windows.configs.AppsWindowConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;

public class DEAppsWindow extends IplantWindowBase {

    private final AppsView.Presenter presenter;

    public DEAppsWindow(AppsWindowConfig config) {
        super(null, null);
        AppsView view = new AppsViewImpl();
        presenter = new AppsViewPresenter(view);

        setSize("800", "410");
        presenter.go(this);
        presenter.go(this, config.getSelectedAppGroup(), config.getSelectedApp());
        setHeadingText(I18N.DISPLAY.applications());
    }

    @Override
    public WindowState getWindowState() {
        AppsWindowConfig config = ConfigFactory.appsWindowConfig();
        config.setSelectedApp(presenter.getSelectedApp());
        config.setSelectedAppGroup(presenter.getSelectedAppGroup());
        return createWindowState(config);
    }


    @Override
    public int getHeaderOffSetHeight() {
        return 0;
    }

}
