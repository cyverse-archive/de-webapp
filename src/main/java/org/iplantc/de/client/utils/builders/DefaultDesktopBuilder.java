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
        addShortcut(res.css().iplantcMydataShortcut(), "", Constants.CLIENT.windowTag(), //$NON-NLS-1$
                ConfigFactory.diskResourceWindowConfig());

        addShortcut(res.css().iplantcMyanalysisShortcut(), "", Constants.CLIENT.windowTag(), //$NON-NLS-1$
                ConfigFactory.analysisWindowConfig());

        addShortcut(res.css().iplantcCatalogShortcut(), "", Constants.CLIENT.windowTag(), //$NON-NLS-1$
                ConfigFactory.appsWindowConfig());
    }
}
