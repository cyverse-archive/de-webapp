package org.iplantc.de.client.periodic;

import org.iplantc.de.client.utils.DEStateManager;

/**
 * Saves user session information.
 */
public class SessionSaver implements Runnable {

    /**
     * Used to persist the user session information.
     */
    private final DEStateManager stateManager;

    /**
     * @param stateManager the state manager used to persist the user session information.
     */
    public SessionSaver(DEStateManager stateManager) {
        this.stateManager = stateManager;
    }

    /**
     * Saves the user's session infomration.
     */
    public void run() {
        stateManager.persistUserSession(true, null);
    }
}
