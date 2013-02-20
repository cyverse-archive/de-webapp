package org.iplantc.de.client.views.windows;

import org.iplantc.core.uiapplications.client.Services;
import org.iplantc.core.uiapplications.client.services.AppUserServiceFacade;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.core.widgets.client.appWizard.view.AppWizardView;
import org.iplantc.core.widgets.client.appWizard.view.AppWizardView.Presenter;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.analysis.services.AnalysisServiceFacade;
import org.iplantc.de.client.views.windows.configs.AppWizardConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

public class AppWizardWindow extends IplantWindowBase {

    private final class LegacyAppTemplateCallback implements AsyncCallback<String> {
        private final Presenter presenter;

        private LegacyAppTemplateCallback(Presenter presenter) {
            this.presenter = presenter;
        }

        @Override
        public void onSuccess(String result) {
            presenter.goLegacy(AppWizardWindow.this, StringQuoter.split(result));
            AppWizardWindow.this.setHeadingText(presenter.getAppTemplate().getLabel());
            // KLUDGE JDS This call to forceLayout should not be necessary.
            AppWizardWindow.this.forceLayout();
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(I18N.ERROR.unableToRetrieveWorkflowGuide(), caught);
        }
    }

    private final AppWizardView.Presenter presenter;
    private final AppUserServiceFacade templateService = Services.USER_APP_SERVICE;
    private final AnalysisServiceFacade analysisService = org.iplantc.de.client.Services.ANALYSIS_SERVICE;
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
        } else if ((config.getLegacyAppTemplateJson() != null) && (!config.getLegacyAppTemplateJson().asString().isEmpty())) {
            presenter.goLegacy(this, config.getLegacyAppTemplateJson());
            setHeadingText(presenter.getAppTemplate().getLabel());
            forceLayout();
        } else if (config.isRelaunchAnalysis()) {
            analysisService.relaunchAnalysis(config.getAnalysisId(), new LegacyAppTemplateCallback(presenter));
        } else {
            templateService.getTemplate(config.getAppId(), new LegacyAppTemplateCallback(presenter));
        }
    }

    @Override
    public WindowState getWindowState() {
        AppWizardConfig config = ConfigFactory.appWizardConfig(appId);
        config.setAppTemplate(presenter.getAppTemplate());
        return createWindowState(config);
    }

}
