package org.iplantc.de.client;

import org.iplantc.core.resources.client.messages.IplantDisplayStrings;
import org.iplantc.core.resources.client.messages.IplantErrorStrings;

import com.google.gwt.core.client.GWT;

/**
 * Provides static access to localized strings.
 *
 * @author lenards
 *
 */
public class I18N {
    /** Strings displayed in the UI */
    public static final IplantDisplayStrings DISPLAY = org.iplantc.core.resources.client.messages.I18N.DISPLAY;
    /** Strings displayed in the UI */
    public static final DEDisplayStaticText CONSTANT = (DEDisplayStaticText)GWT
            .create(DEDisplayStaticText.class);
    /** Error messages */
    public static final IplantErrorStrings ERROR = org.iplantc.core.resources.client.messages.I18N.ERROR;
}
