package org.iplantc.de.client;

import org.iplantc.de.commons.client.DEClientConstants;

import com.google.gwt.core.client.GWT;

/**
 * Static access to client constants.
 * 
 * @author lenards
 * 
 */
public class Constants {
    public static final DEClientConstants CLIENT = GWT.create(DEClientConstants.class);
}
