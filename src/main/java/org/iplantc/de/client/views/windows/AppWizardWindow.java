package org.iplantc.de.client.views.windows;

import org.iplantc.core.uiapps.widgets.client.events.AnalysisLaunchEvent;
import org.iplantc.core.uiapps.widgets.client.events.AnalysisLaunchEvent.AnalysisLaunchEventHandler;
import org.iplantc.core.uiapps.widgets.client.models.AppTemplate;
import org.iplantc.core.uiapps.widgets.client.services.AppTemplateServices;
import org.iplantc.core.uiapps.widgets.client.view.AppWizardView;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.models.CommonModelUtils;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.views.windows.configs.AppWizardConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AppWizardWindow extends IplantWindowBase implements AnalysisLaunchEventHandler {

    private final class AppTemplateCallback implements AsyncCallback<AppTemplate> {
        private final AppWizardView.Presenter presenter;

        private AppTemplateCallback(AppWizardView.Presenter presenter) {
            this.presenter = presenter;
        }

        @Override
        public void onSuccess(AppTemplate result) {
            presenter.go(AppWizardWindow.this, result);
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
    private final AppTemplateServices templateService = GWT.create(AppTemplateServices.class);
    private final String appId;

    public AppWizardWindow(AppWizardConfig config) {
        super(null, null);
        setSize("640", "375");
        setBorders(false);

        presenter = GWT.create(AppWizardView.Presenter.class);
        presenter.addAnalysisLaunchHandler(this);
        appId = config.getAppId();
        init(presenter, config);
    }

    private void init(final AppWizardView.Presenter presenter, AppWizardConfig config) {
        if (config.getAppTemplate() != null) {
            setHeadingText(config.getAppTemplate().getLabel());
            presenter.go(this, config.getAppTemplate());
            // KLUDGE JDS This call to forceLayout should not be necessary.
            forceLayout();
        } else if ((config.getLegacyAppTemplateJson() != null)
                && (!config.getLegacyAppTemplateJson().asString().isEmpty())) {
            presenter.goLegacy(this, config.getLegacyAppTemplateJson());
            setHeadingText(presenter.getAppTemplate().getLabel());
            forceLayout();
        } else if (config.isRelaunchAnalysis()) {
            templateService.rerunAnalysis(config.getAnalysisId(), new AppTemplateCallback(
                    presenter));
        } else {
            templateService.getAppTemplate(CommonModelUtils.createHasIdFromString(config.getAppId()), new AppTemplateCallback(presenter));
        }
    }

    @Override
    public WindowState getWindowState() {
        AppWizardConfig config = ConfigFactory.appWizardConfig(appId);
        config.setAppTemplate(presenter.getAppTemplate());
        return createWindowState(config);
    }

    @Override
    public void onAnalysisLaunch(AnalysisLaunchEvent analysisLaunchEvent) {
        if (analysisLaunchEvent.getAppTemplateId().getId().equalsIgnoreCase(appId)) {
            hide();
            // TODO JDS Need to kick off notification
        }
    }

}
