package org.iplantc.de.client.views.windows;

import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.de.client.DeResources;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.images.Resources;
import org.iplantc.de.client.models.AboutApplicationData;
import org.iplantc.de.client.models.DeModelAutoBeanFactory;
import org.iplantc.de.client.views.windows.configs.AboutWindowConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.shared.services.AboutApplicationServiceFacade;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

/**
 * Models a user interface for "about" application information.
 * 
 * @author lenards
 */
public class AboutApplicationWindow extends IplantWindowBase {
    private AboutApplicationData model;

    private Label lblNSFStatement;
    private final DeResources res;

    public AboutApplicationWindow(AboutWindowConfig config) {
        super("");
        setSize("300", "235");
        res = GWT.create(DeResources.class);
        res.css().ensureInjected();
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
                DeModelAutoBeanFactory factory = GWT.create(DeModelAutoBeanFactory.class);
                model = AutoBeanCodex.decode(factory, AboutApplicationData.class, result).as();
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
                model.getBuildNumber(), Window.Navigator.getUserAgent()));
        pnlDetails.add(txt);

        return pnlDetails;
    }

    private String getAboutTemplate() {
        return "<p>Release: {0}</p><p> Build #: {1}</p><p>User Agent: {2}</p>"; //$NON-NLS-1$
    }

    @Override
    public WindowState getWindowState() {
        return createWindowState(ConfigFactory.aboutWindowConfig());
    }
}
