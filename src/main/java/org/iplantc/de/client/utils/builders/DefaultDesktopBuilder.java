package org.iplantc.de.client.utils.builders;

import org.iplantc.de.client.Constants;
import org.iplantc.de.client.DeResources;

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
                Constants.CLIENT.myDataTag());

        addShortcut(res.css().iplantcMyanalysisShortcut(), "", Constants.CLIENT.windowTag(), //$NON-NLS-1$
                Constants.CLIENT.myAnalysisTag());

        addShortcut(res.css().iplantcCatalogShortcut(), "", Constants.CLIENT.windowTag(), //$NON-NLS-1$
                Constants.CLIENT.deCatalog());
    }
}
