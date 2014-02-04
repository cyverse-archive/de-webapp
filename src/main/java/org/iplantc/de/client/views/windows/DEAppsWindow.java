package org.iplantc.de.client.views.windows;

import org.iplantc.de.apps.client.gin.AppsInjector;
import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.commons.client.models.WindowState;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.views.windows.configs.AppsWindowConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;

public class DEAppsWindow extends IplantWindowBase {

    private final AppsView.Presenter presenter;

    public DEAppsWindow(AppsWindowConfig config) {
        super(null, null);
        presenter = AppsInjector.INSTANCE.getAppsViewPresenter();

        setSize("600", "375");
        presenter.go(this, config.getSelectedAppGroup(), config.getSelectedApp());
        setHeadingText(I18N.DISPLAY.applications());
    }

    @Override
    public void doHide() {
        presenter.cleanUp();
        super.doHide();
    }

    @Override
    public WindowState getWindowState() {
        AppsWindowConfig config = ConfigFactory.appsWindowConfig();
        config.setSelectedApp(presenter.getSelectedApp());
        config.setSelectedAppGroup(presenter.getSelectedAppGroup());
        return createWindowState(config);
    }

}
