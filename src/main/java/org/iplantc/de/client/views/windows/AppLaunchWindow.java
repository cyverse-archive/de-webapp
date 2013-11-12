package org.iplantc.de.client.views.windows;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import org.iplantc.core.uiapps.widgets.client.events.AnalysisLaunchEvent;
import org.iplantc.core.uiapps.widgets.client.events.AnalysisLaunchEvent.AnalysisLaunchEventHandler;
import org.iplantc.core.uiapps.widgets.client.gin.AppLaunchInjector;
import org.iplantc.core.uiapps.widgets.client.models.AppTemplate;
import org.iplantc.core.uiapps.widgets.client.models.AppTemplateAutoBeanFactory;
import org.iplantc.core.uiapps.widgets.client.services.AppTemplateServices;
import org.iplantc.core.uiapps.widgets.client.services.DeployedComponentServices;
import org.iplantc.core.uiapps.widgets.client.services.impl.converters.AppTemplateCallbackConverter;
import org.iplantc.core.uiapps.widgets.client.view.AppLaunchView;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.info.ErrorAnnouncementConfig;
import org.iplantc.core.uicommons.client.info.IplantAnnouncer;
import org.iplantc.core.uicommons.client.models.CommonModelUtils;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.events.WindowHeadingUpdatedEvent;
import org.iplantc.de.client.views.windows.configs.AppWizardConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;

public class AppLaunchWindow extends IplantWindowBase implements AnalysisLaunchEventHandler {

    private final class AppTemplateCallback implements AsyncCallback<AppTemplate> {
        private final AppLaunchView.Presenter presenter1;

        private AppTemplateCallback(AppLaunchView.Presenter presenter) {
            this.presenter1 = presenter;
        }

        @Override
        public void onSuccess(AppTemplate result) {
            if (result.isAppDisabled()) {
                ErrorAnnouncementConfig config = new ErrorAnnouncementConfig(
                        I18N.DISPLAY.appUnavailable());
                IplantAnnouncer.getInstance().schedule(config);
                AppLaunchWindow.this.hide();
                return;
            }
            presenter1.go(AppLaunchWindow.this, result);
            AppLaunchWindow.this.setHeadingText(presenter1.getAppTemplate().getLabel());
            AppLaunchWindow.this.fireEvent(new WindowHeadingUpdatedEvent());
            // KLUDGE JDS This call to forceLayout should not be necessary.
            AppLaunchWindow.this.forceLayout();
            AppLaunchWindow.this.unmask();
        }

        @Override
        public void onFailure(Throwable caught) {
            AppLaunchWindow.this.unmask();
            ErrorHandler.post(I18N.ERROR.unableToRetrieveWorkflowGuide(), caught);
        }
    }

    private final AppLaunchView.Presenter presenter;
    private final AppTemplateServices templateService = GWT.create(AppTemplateServices.class);
    private final String appId;
    private final AppTemplateAutoBeanFactory factory = GWT.create(AppTemplateAutoBeanFactory.class);
    private final DeployedComponentServices dcServices = GWT.create(DeployedComponentServices.class);

    public AppLaunchWindow(AppWizardConfig config) {
        super(null, null);
        setSize("640", "375");
        setBorders(false);

        presenter = AppLaunchInjector.INSTANCE.getAppLaunchPresenter();
        presenter.addAnalysisLaunchHandler(this);
        appId = config.getAppId();
        init(presenter, config);
    }

    private void init(final AppLaunchView.Presenter presenter, AppWizardConfig config) {
        mask(I18N.DISPLAY.loadingMask());
        if (config.getAppTemplate() != null) {
            AppTemplateCallbackConverter cnvt = new AppTemplateCallbackConverter(factory, dcServices,
                    new AsyncCallback<AppTemplate>() {

                        @Override
                        public void onSuccess(AppTemplate result) {
                            setHeadingText(result.getLabel());
                            presenter.go(AppLaunchWindow.this, result);
                            AppLaunchWindow.this.unmask();
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            /*
                             * JDS Do nothing since this this callback converter is called manually below
                             * (i.e. no over-the-wire integration)
                             */
                        }
                    });
            cnvt.onSuccess(config.getAppTemplate().getPayload());

            // KLUDGE JDS This call to forceLayout should not be necessary.
            forceLayout();
        } else if (config.isRelaunchAnalysis()) {
            templateService.rerunAnalysis(config.getAnalysisId(), new AppTemplateCallback(presenter));
        } else {
            templateService.getAppTemplate(CommonModelUtils.createHasIdFromString(config.getAppId()),
                    new AppTemplateCallback(presenter));
        }
    }

    @Override
    public WindowState getWindowState() {
        AppWizardConfig config = ConfigFactory.appWizardConfig(appId);
        config.setAppTemplate(AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(presenter.getAppTemplate())));
        return createWindowState(config);
    }

    @Override
    public void onAnalysisLaunch(AnalysisLaunchEvent analysisLaunchEvent) {
        if (analysisLaunchEvent.getAppTemplateId().getId().equalsIgnoreCase(appId)) {
            hide();
        }
    }

}
