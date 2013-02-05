package org.iplantc.de.client.views.windows;

import org.iplantc.core.uiapplications.client.Services;
import org.iplantc.core.uiapplications.client.services.AppUserServiceFacade;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.models.autobeans.WindowState;
import org.iplantc.core.widgets.client.appWizard.view.AppWizardView;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.views.windows.configs.AppWizardConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AppWizardWindow extends IplantWindowBase {

    private final AppWizardView.Presenter presenter;
    private final AppUserServiceFacade templateService = Services.USER_APP_SERVICE;
    private final String appId;
    
    public AppWizardWindow(AppWizardConfig config) {
        super(null, null);
        setSize("640", "410");
        setBorders(false);

        presenter = GWT.create(AppWizardView.Presenter.class);
        appId = config.getAppId();
        init(presenter, config);
    }

    private void init(final AppWizardView.Presenter presenter, AppWizardConfig config) {
        if (config.getAppTemplate() != null) {
            setHeadingText(config.getAppTemplate().getLabel());
            presenter.go(this, config.getAppTemplate());
            // KLUDGE JDS This call to forceLayout should not be necessary.
            forceLayout();
        } else {
            templateService.getTemplate(config.getAppId(), new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(I18N.ERROR.unableToRetrieveWorkflowGuide(), caught);
                }

                @Override
                public void onSuccess(String json) {
                    presenter.go(AppWizardWindow.this, json);
                    AppWizardWindow.this.setHeadingText(presenter.getAppTemplate().getLabel());
                    // KLUDGE JDS This call to forceLayout should not be necessary.
                    AppWizardWindow.this.forceLayout();
                }
            });
        }
    }

    @Override
    public WindowState getWindowState() {
        AppWizardConfig config = ConfigFactory.appWizardConfig(appId);
        config.setAppTemplate(presenter.getAppTemplate());
        return createWindowState(config);
    }

}
