package org.iplantc.de.client.views.windows;

import java.util.List;

import org.iplantc.core.resources.client.uiapps.widgets.AppsWidgetsPropertyPanelLabels;
import org.iplantc.core.uiapps.client.events.AppPublishedEvent;
import org.iplantc.core.uiapps.client.events.AppPublishedEvent.AppPublishedEventHandler;
import org.iplantc.core.uiapps.client.models.autobeans.App;
import org.iplantc.core.uiapps.integration.client.presenter.AppsIntegrationPresenterImpl;
import org.iplantc.core.uiapps.integration.client.view.AppsIntegrationView;
import org.iplantc.core.uiapps.integration.client.view.AppsIntegrationViewImpl;
import org.iplantc.core.uiapps.widgets.client.models.AppTemplate;
import org.iplantc.core.uiapps.widgets.client.models.AppTemplateAutoBeanFactory;
import org.iplantc.core.uiapps.widgets.client.models.Argument;
import org.iplantc.core.uiapps.widgets.client.models.ArgumentGroup;
import org.iplantc.core.uiapps.widgets.client.models.util.AppTemplateUtils;
import org.iplantc.core.uiapps.widgets.client.services.AppMetadataServiceFacade;
import org.iplantc.core.uiapps.widgets.client.services.AppTemplateServices;
import org.iplantc.core.uiapps.widgets.client.services.DeployedComponentServices;
import org.iplantc.core.uiapps.widgets.client.services.impl.AppTemplateCallbackConverter;
import org.iplantc.core.uiapps.widgets.client.view.AppWizardView.RenameWindowHeaderCommand;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.errorHandling.models.SimpleServiceError;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uicommons.client.info.ErrorAnnouncementConfig;
import org.iplantc.core.uicommons.client.info.IplantAnnouncer;
import org.iplantc.core.uicommons.client.models.CommonModelUtils;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.core.uicommons.client.widgets.ContextualHelpToolButton;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.UUIDServiceAsync;
import org.iplantc.de.client.events.WindowHeadingUpdatedEvent;
import org.iplantc.de.client.views.windows.configs.AppsIntegrationWindowConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.WindowConfig;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent.MaximizeHandler;
import com.sencha.gxt.widget.core.client.event.RestoreEvent;
import com.sencha.gxt.widget.core.client.event.RestoreEvent.RestoreHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;

/**
 * A window for the App Integration editor 
 * 
 * XXX JDS Much of the Apps Integration module config, presenter, and window closely mimic that of the App Wizard.
 * 
 * @author jstroot, sriram, psarando
 * 
 */
public class AppIntegrationWindow extends IplantWindowBase implements AppPublishedEventHandler {
    interface PublicAppTitleTemplate extends SafeHtmlTemplates {
        @Template("<div>"
                + "<span class='{3}'>{2}</span>" 
                + "<span class='{1}'>{0}</span>"
                + "</div>")
        SafeHtml editPublicAppWarningTitle(SafeHtml title, String titleStyle, String warningText, String warningStyle);
    }

    interface TitleStyles extends CssResource{
        String warning();
        
        String title();
    }
    
    interface Resources extends ClientBundle{
        @Source("AppIntegrationWindowTitleStyles.css")
        TitleStyles titleStyles();
    }

    private final static PublicAppTitleTemplate templates = GWT.create(PublicAppTitleTemplate.class);

    private final AppsIntegrationView.Presenter presenter;
    protected List<HandlerRegistration> handlers;
    private final AppTemplateServices templateService;
    private final AppTemplateAutoBeanFactory factory = GWT.create(AppTemplateAutoBeanFactory.class);
    private final DeployedComponentServices dcServices = GWT.create(DeployedComponentServices.class);
    private final AppsWidgetsPropertyPanelLabels labels = org.iplantc.core.resources.client.messages.I18N.APPS_LABELS;
    private final RenameWindowHeaderCmdImpl renameCmd;

    final ContextualHelpToolButton editPublicAppContextHlpTool = new ContextualHelpToolButton(new HTML(I18N.APPS_HELP.editPublicAppHelp()));
    final Resources res = GWT.create(Resources.class);

    public AppIntegrationWindow(AppsIntegrationWindowConfig config, final EventBus eventBus,
            final UUIDServiceAsync uuidService, final AppMetadataServiceFacade appMetadataService) {
        super(null, config);
        res.titleStyles().ensureInjected();

        AppsIntegrationView view = new AppsIntegrationViewImpl(uuidService, appMetadataService);
        templateService = GWT.create(AppTemplateServices.class);
        presenter = new AppsIntegrationPresenterImpl(view, eventBus, templateService, I18N.ERROR,
                I18N.DISPLAY, uuidService);
        setTitle(I18N.DISPLAY.createApps());
        setSize("800", "480");
        setMinWidth(725);
        setMinHeight(375);

        final WindowHandler windowHandler = new WindowHandler();
        addRestoreHandler(windowHandler);
        addMaximizeHandler(windowHandler);
        addShowHandler(windowHandler);

        renameCmd = new RenameWindowHeaderCmdImpl(this);
        init(presenter, config);

        // JDS Add presenter as a before hide handler to determine if user has changes before closing.
        HandlerRegistration hr = this.addBeforeHideHandler(presenter);
        presenter.setBeforeHideHandlerRegistration(hr);
        eventBus.addHandler(AppPublishedEvent.TYPE, this);
    }

    private void init(final AppsIntegrationView.Presenter presenter,
            final AppsIntegrationWindowConfig config) {
        if (config.getAppTemplate() != null) {
            // JDS Use converter for convenience.
            AppTemplateCallbackConverter at = new AppTemplateCallbackConverter(factory, dcServices,
                    new AsyncCallback<AppTemplate>() {

                        @Override
                        public void onSuccess(AppTemplate result) {
                            // KLUDGE until service returns this value in JSON response.
                            result.setPublic(result.isPublic() || config.isOnlyLabelEditMode());
                            renameCmd.setAppTemplate(result);
                            presenter.go(AppIntegrationWindow.this, result, renameCmd);
                            AppIntegrationWindow.this.forceLayout();
                            AppIntegrationWindow.this.center();
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            /*
                             * JDS Do nothing since this this callback converter is called manually below
                             * (i.e. no over-the-wire integration)
                             */
                        }
                    });
            at.onSuccess(config.getAppTemplate().getPayload());
        } else if (config.getAppId().equalsIgnoreCase(Constants.CLIENT.newAppTemplate())) {
            setTitle(I18N.DISPLAY.createApps());
            // Create empty AppTemplate
            AppTemplateAutoBeanFactory factory = GWT.create(AppTemplateAutoBeanFactory.class);

            AppTemplate newAppTemplate = factory.appTemplate().as();
            newAppTemplate.setName(labels.appDefaultName());
            ArgumentGroup argGrp = factory.argumentGroup().as();
            argGrp.setName("");
            argGrp.setLabel(labels.groupDefaultLabel(1));
            argGrp.setArguments(Lists.<Argument> newArrayList());
            newAppTemplate.setArgumentGroups(Lists.<ArgumentGroup> newArrayList(argGrp));

            /*
             * JDS Set the id of the AppTemplate passed to the rename command to newAppTemplate. This is
             * to ensure that the window title is not changed until a new app has been saved.
             */
            final AppTemplate copyAppTemplate = AppTemplateUtils.copyAppTemplate(newAppTemplate);
            copyAppTemplate.setId(Constants.CLIENT.newAppTemplate());
            renameCmd.setAppTemplate(copyAppTemplate);

            presenter.go(this, newAppTemplate, renameCmd);
            AppIntegrationWindow.this.forceLayout();
        } else {
            mask(I18N.DISPLAY.loadingMask());
            templateService.getAppTemplateForEdit(
                    CommonModelUtils.createHasIdFromString(config.getAppId()),
                    new AsyncCallback<AppTemplate>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            SimpleServiceError serviceError = AutoBeanCodex.decode(factory,
                                    SimpleServiceError.class, caught.getMessage()).as();
                            IplantAnnouncer.getInstance().schedule(
                                    new ErrorAnnouncementConfig(I18N.ERROR
                                            .unableToRetrieveWorkflowGuide()
                                            + ": "
                                            + serviceError.getReason()));
                            ErrorHandler.post(I18N.ERROR.unableToRetrieveWorkflowGuide(), caught);
                            AppIntegrationWindow.this.hide();
                        }

                        @Override
                        public void onSuccess(AppTemplate result) {
                            // KLUDGE until service returns this value in JSON response.
                            result.setPublic(result.isPublic() || config.isOnlyLabelEditMode());
                            renameCmd.setAppTemplate(result);
                            presenter.go(AppIntegrationWindow.this, result, renameCmd);
                            AppIntegrationWindow.this.unmask();
                            AppIntegrationWindow.this.forceLayout();
                            AppIntegrationWindow.this.center();
                        }
                    });
        }
    }


    private void setEditPublicAppHeader(String appName) {
        setHeadingHtml(templates.editPublicAppWarningTitle(SafeHtmlUtils.fromString(appName), res.titleStyles().title(), 
                I18N.APPS_MESSAGES.editPublicAppWarning(), res.titleStyles().warning()));

        // JDS Only insert if not there.
        if (getHeader().getTool(0) != editPublicAppContextHlpTool) {
            getHeader().insertTool(editPublicAppContextHlpTool, 0);
        }
    }

    @Override
    public WindowState getWindowState() {
        return createWindowState(getUpdatedConfig());
    }

    private AppsIntegrationWindowConfig getUpdatedConfig() {
        AppsIntegrationWindowConfig config = ConfigFactory.appsIntegrationWindowConfig("");
        AppTemplate appTemplate = presenter.getAppTemplate();
        config.setAppTemplate(AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(appTemplate)));
        return config;
    }

    @Override
    public <C extends WindowConfig> void update(C config) {
        AppsIntegrationWindowConfig appIntConfig = (AppsIntegrationWindowConfig)config;
        init(presenter, appIntConfig);
    }

    @Override
    public void onAppPublished(AppPublishedEvent appPublishedEvent) {
        App publishedApp = appPublishedEvent.getPublishedApp();
        AppTemplate currentAt = presenter.getAppTemplate();
        // JDS If the published App is the current edited AppTemplate, refetch app Template
        if (publishedApp.getId().equalsIgnoreCase(currentAt.getId())) {
    
            if (presenter.isEditorDirty()) {
                // JDS If the editor has unsaved changes, inform user that they will be thrown away.
                IplantAnnouncer.getInstance().schedule(new ErrorAnnouncementConfig("This app has been published before the current changes were saved. All unsaved changes have been discarded."));
            }
            AppsIntegrationWindowConfig appIntConfig = ConfigFactory.appsIntegrationWindowConfig(publishedApp.getId());
            appIntConfig.setOnlyLabelEditMode(true);
            update(appIntConfig);
        }
    
    }

    /**
     * This handler is used to manage window events.
     * 
     * @author jstroot
     * 
     */
    private final class WindowHandler implements RestoreHandler, MaximizeHandler, ShowHandler {
        @Override
        public void onRestore(RestoreEvent event) {
            AppIntegrationWindow.this.maximized = false;
        }
    
        @Override
        public void onMaximize(MaximizeEvent event) {
            AppIntegrationWindow.this.maximized = true;
        }
    
        @Override
        public void onShow(ShowEvent event) {
            AppIntegrationWindow.this.maximize();
        }
    }

    /**
     * This command is passed to the {@link AppsIntegrationView.Presenter} to communicate when this
     * window's title should be updated.
     * 
     * @author jstroot
     * 
     */
    private final class RenameWindowHeaderCmdImpl implements RenameWindowHeaderCommand {
        private AppTemplate appTemplate;
        private final AppIntegrationWindow window;
    
        public RenameWindowHeaderCmdImpl(AppIntegrationWindow window) {
            this.window = window;
        }
    
        @Override
        public void execute() {
            // JDS Don't update window title for new, un-saved apps.
            if (appTemplate.getId().equalsIgnoreCase(Constants.CLIENT.newAppTemplate())) {
                return;
            }
            final String name = !Strings.isNullOrEmpty(appTemplate.getName()) ? appTemplate.getName() : I18N.DISPLAY.createApps();
            if (appTemplate.isPublic()) {
                window.setEditPublicAppHeader(name);
            } else {
                window.getHeader().removeTool(window.editPublicAppContextHlpTool);
                window.setHeadingText(name);
            }
            window.fireEvent(new WindowHeadingUpdatedEvent(name));
    
        }
    
        @Override
        public void setAppTemplate(AppTemplate appTemplate) {
            this.appTemplate = appTemplate;
        }
    
    }
}
