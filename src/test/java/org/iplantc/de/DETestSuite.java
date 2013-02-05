package org.iplantc.de;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.iplantc.de.client.GwtTestMultiPartServiceWrapper;
import org.iplantc.de.client.GwtTestUserInfo;

import com.google.gwt.junit.tools.GWTTestSuite;

/**
 * A class that builds a test suite for Discovery Environment unit tests
 * 
 * @author sriram
 * 
 */
public class DETestSuite extends GWTTestSuite {
    /**
     * Build test suite
     * 
     * @return
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("Tests for Discovery Environment"); //$NON-NLS-1$
        suite.addTestSuite(GwtTestMultiPartServiceWrapper.class);
        suite.addTestSuite(GwtTestUserInfo.class);
        // suite.addTestSuite(GwtTestNotification.class);

        return suite;
    }
}
