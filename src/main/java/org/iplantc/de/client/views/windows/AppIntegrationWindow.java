package org.iplantc.de.client.views.windows;

import java.util.List;

import org.iplantc.core.uiapps.client.Services;
import org.iplantc.core.uiapps.client.services.AppUserServiceFacade;
import org.iplantc.core.uiapps.integration.client.presenter.AppsIntegrationPresenterImpl;
import org.iplantc.core.uiapps.integration.client.services.AppTemplateServices;
import org.iplantc.core.uiapps.integration.client.view.AppsIntegrationView;
import org.iplantc.core.uiapps.integration.client.view.AppsIntegrationViewImpl;
import org.iplantc.core.uiapps.widgets.client.models.AppTemplateAutoBeanFactory;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.views.windows.configs.AppsIntegrationWindowConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

/**
 * A window for the App Integration editor
 * XXX JDS Much of the Apps Integration module config, presenter, and window closely mimic that of the App Wizard.
 
 * @author jstroot, sriram, psarando
 * 
 */
public class AppIntegrationWindow extends IplantWindowBase {

    private final AppsIntegrationView.Presenter presenter;
    private final AppUserServiceFacade templateService = Services.USER_APP_SERVICE;
    protected List<HandlerRegistration> handlers;

    public AppIntegrationWindow(AppsIntegrationWindowConfig config, final EventBus eventBus) {
        super(null, config);

        AppsIntegrationView view = new AppsIntegrationViewImpl(eventBus);
        AppTemplateServices atService = GWT.create(AppTemplateServices.class);
        presenter = new AppsIntegrationPresenterImpl(view, eventBus, atService, I18N.ERROR, I18N.DISPLAY);
        setTitle(I18N.DISPLAY.createApps());
        setSize("800", "410");

        init(presenter, config);
    }

    private void init(final AppsIntegrationView.Presenter presenter, AppsIntegrationWindowConfig config) {
        Splittable legacyAppTemplateJson = config.getLegacyAppTemplateJson();
        if (config.getAppTemplate() != null) {
            presenter.go(this, config.getAppTemplate());
        } else if ((legacyAppTemplateJson != null) && (!legacyAppTemplateJson.getPayload().isEmpty())) {
            presenter.goLegacy(this, config.getLegacyAppTemplateJson());
        } else if(config.getAppId().equalsIgnoreCase(Constants.CLIENT.newAppTemplate())){
            // Create empty AppTemplate
            AppTemplateAutoBeanFactory factory = GWT.create(AppTemplateAutoBeanFactory.class);
            presenter.go(this, factory.appTemplate().as());
        }else {
            templateService.getTemplate(config.getAppId(), new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    AppIntegrationWindow.this.hide();
                    ErrorHandler.post(I18N.ERROR.unableToRetrieveWorkflowGuide(), caught);
                }

                @Override
                public void onSuccess(String json) {
                    presenter.goLegacy(AppIntegrationWindow.this, StringQuoter.split(json));
                }
            });
        }

    }

    @Override
    public WindowState getWindowState() {
        return createWindowState(getUpdatedConfig());
    }

    /**
     * TBI JDS This method needs to query the presenter in order to create a current config.
     * 
     * @return
     */
    private AppsIntegrationWindowConfig getUpdatedConfig() {
        
        AppsIntegrationWindowConfig config = ConfigFactory.appsIntegrationWindowConfig("");
        config.setAppTemplate(presenter.getAppTemplate());
        return config;
    }
}
