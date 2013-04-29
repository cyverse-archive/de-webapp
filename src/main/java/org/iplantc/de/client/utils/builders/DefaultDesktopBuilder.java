package org.iplantc.de.client.utils.builders;

import org.iplantc.de.client.Constants;
import org.iplantc.de.client.DeResources;
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
        addShortcut(res.css().iplantcMydataShortcut(), "3",
                "Manage and organize all your data here.", Constants.CLIENT.windowTag(), //$NON-NLS-1$
                ConfigFactory.diskResourceWindowConfig());

        addShortcut(res.css().iplantcCatalogShortcut(), "4",
                "Find and runs scientific apps. You can create new apps.", Constants.CLIENT.windowTag(), //$NON-NLS-1$
                ConfigFactory.appsWindowConfig());

        addShortcut(
                res.css().iplantcMyanalysisShortcut(),
                "5",
                "Find status of your submitted app runs. You can also access results for the completed or failed ones.", Constants.CLIENT.windowTag(), //$NON-NLS-1$
                ConfigFactory.analysisWindowConfig());
    }
}
