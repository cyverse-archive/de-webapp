package org.iplantc.de.client.views.windows;

import org.iplantc.core.resources.client.IplantResources;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.de.client.DeResources;
import org.iplantc.de.client.I18N;
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
    private final DeResources res;

    public AboutApplicationWindow(AboutWindowConfig config) {
        super("");
        setSize("320", "260");
        setResizable(false);
        setTitle(I18N.DISPLAY.aboutDiscoveryEnvironment());
        executeServiceCall();
        res = GWT.create(DeResources.class);
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
        vlc.setStyleName(res.css().iplantcAboutPadText());
        Image logo = new Image(IplantResources.RESOURCES.iplantAbout().getSafeUri());
        vlc.add(logo);
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

        HTML txt = new HTML(Format.substitute(getAboutTemplate(), model.getReleaseVersion(),
                model.getBuildNumber(), Window.Navigator.getUserAgent(), 
                I18N.DISPLAY.projectCopyrightStatement().asString(), I18N.DISPLAY.nsfProjectText().asString()));
        pnlDetails.add(txt);

        return pnlDetails;
    }

    private String getAboutTemplate() {
        return "<p style='font-style:italic;'> {4} </p><p>Release: {0}</p><p> Build #: {1}</p><p>User Agent: {2}</p> <p style='font-weight:700'> {3}</p>"; //$NON-NLS-1$
    }

    @Override
    public WindowState getWindowState() {
        return createWindowState(ConfigFactory.aboutWindowConfig());
    }
}
