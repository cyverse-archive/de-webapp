package org.iplantc.de.client.utils.builders;

import org.iplantc.de.client.Constants;
import org.iplantc.de.client.DeResources;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;

import com.google.gwt.core.client.GWT;

/**
 * Initializes all desktop shortcuts.
 * 
 * @author amuir
 * 
 */
public class DefaultDesktopBuilder extends DesktopBuilder {
    @Override
    protected void buildShortcuts() {
        DeResources res = GWT.create(DeResources.class);
        res.css().ensureInjected();
        addShortcut(res.css().iplantcMydataShortcut(), "idMydataShortCut", res.css()
                .iplantcMydataShortcutHover(), "1",
                I18N.HELP.iconHomepageDataTip(),
                Constants.CLIENT.windowTag(), //$NON-NLS-1$
                ConfigFactory.diskResourceWindowConfig(),
                org.iplantc.de.resources.client.messages.I18N.TOUR.introDataWindow());

        addShortcut(res.css().iplantcCatalogShortcut(), "idAppsShortCut", res.css()
                .iplantcCatalogShortcutHover(), "2",
                I18N.HELP.iconHomepageAppsTip(),
                Constants.CLIENT.windowTag(), //$NON-NLS-1$
                ConfigFactory.appsWindowConfig(),
                org.iplantc.de.resources.client.messages.I18N.TOUR.introAppsWindow());

        addShortcut(res.css().iplantcMyanalysisShortcut(), "idAnalysisShortCut",
                res.css().iplantcMyanalysisShortcutHover(),
                "3", I18N.HELP.iconHomepageAnalysesTip(), Constants.CLIENT.windowTag(), //$NON-NLS-1$
                ConfigFactory.analysisWindowConfig(),
                org.iplantc.de.resources.client.messages.I18N.TOUR.introAnalysesWindow());
    }
}
