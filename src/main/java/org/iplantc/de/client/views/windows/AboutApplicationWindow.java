package org.iplantc.de.client.views.windows;

import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.DeResources;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.dispatchers.WindowDispatcher;
import org.iplantc.de.client.factories.EventJSONFactory.ActionType;
import org.iplantc.de.client.factories.WindowConfigFactory;
import org.iplantc.de.client.images.Resources;
import org.iplantc.de.client.models.AboutApplicationData;
import org.iplantc.de.shared.services.AboutApplicationServiceFacade;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

/**
 * Models a user interface for "about" application information.
 * 
 * @author lenards
 */
public class AboutApplicationWindow extends Gxt3IplantWindow {
    private AboutApplicationData model;

    private Label lblNSFStatement;
    private final DeResources res;

    /**
     * Constructs an instance given a unique identifier.
     * 
     * @param tag string that serves as an identifier, or window handle.
     */
    public AboutApplicationWindow(String tag) {
        super(tag);
        setSize("300", "235");
        res = GWT.create(DeResources.class);
        res.css().ensureInjected();
        setId(tag);
        setTitle(I18N.DISPLAY.aboutDiscoveryEnvironment());
        setResizable(false);
        initComponents();
        executeServiceCall();
    }

    private void initComponents() {
        lblNSFStatement = new Label(I18N.DISPLAY.nsfProjectText());
    }

    private void executeServiceCall() {
        AboutApplicationServiceFacade.getInstance().getAboutInfo(new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                model = new AboutApplicationData(result);
                compose();
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }
        });
    }

    private void compose() {
        VerticalLayoutContainer vlc = new VerticalLayoutContainer();
        vlc.setBorders(true);
        Image logo = new Image(Resources.ICONS.iplantAbout().getSafeUri());

        vlc.add(logo);
        vlc.add(lblNSFStatement);
        vlc.add(buildDetailsContainer());
        add(vlc);
    }

    /**
     * Construct and configure the details container.
     * 
     * This is a panel containing details about the Discovery Environment like the release version, build
     * number, and the user's browser information
     * 
     * @return a configured panel containing details information.
     */
    private ContentPanel buildDetailsContainer() {
        ContentPanel pnlDetails = new ContentPanel();
        pnlDetails.setHeaderVisible(false);
        pnlDetails.setBodyStyleName(res.css().iplantcAboutPadText());

        HTML txt = new HTML(Format.substitute(getAboutTemplate(), model.getReleaseVersion(),
                model.getBuildNumber(), model.getUserAgent()));
        pnlDetails.add(txt);

        return pnlDetails;
    }

    private String getAboutTemplate() {
        return "<p>Release: {0}</p><p> Build #: {1}</p><p>User Agent: {2}</p>"; //$NON-NLS-1$
    }

    @Override
    public JSONObject getWindowState() {
        // Build window config
        JSONObject configData = config;
        if (configData == null) {
            configData = new JSONObject();
        }

        storeWindowViewState(configData);

        WindowConfigFactory configFactory = new WindowConfigFactory();
        JSONObject windowConfig = configFactory.buildWindowConfig(Constants.CLIENT.myAboutTag(),
                configData);
        WindowDispatcher dispatcher = new WindowDispatcher(windowConfig);

        return dispatcher.getDispatchJson(Constants.CLIENT.myAboutTag(), ActionType.DISPLAY_WINDOW);
    }
}
