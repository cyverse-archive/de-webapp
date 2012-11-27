package org.iplantc.de.client.periodic;

import org.iplantc.de.client.views.panels.MyAnalysesPanel;

/**
 * Periodically checks for analysis status updates.
 */
public class AnalysisStatusChecker implements Runnable {

    /**
     * The panel used to update the analysis status information.
     */
    private final MyAnalysesPanel myAnalysesPanel;

    /**
     * @param myAnalysesPanel the panel used to update the analysis status information.
     */
    public AnalysisStatusChecker(MyAnalysesPanel myAnalysesPanel) {
        this.myAnalysesPanel = myAnalysesPanel;
    }

    /**
     * Updates the analysis status information.
     */
    @Override
    public void run() {
        myAnalysesPanel.checkStatus();
    }
}
