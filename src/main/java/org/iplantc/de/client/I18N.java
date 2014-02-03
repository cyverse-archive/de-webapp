package org.iplantc.de.client;

import com.google.gwt.core.client.GWT;

/**
 * Provides static access to localized strings.
 *
 * @author lenards
 *
 */
public class I18N extends org.iplantc.de.resources.client.messages.I18N {
    /** Strings displayed in the UI */
    public static final DEDisplayStaticText CONSTANT = (DEDisplayStaticText)GWT
            .create(DEDisplayStaticText.class);
}
