package org.iplantc.de.client.desktop.views;

import org.iplantc.core.resources.client.messages.I18N;
import org.iplantc.core.uicommons.client.views.gxt3.dialogs.IPlantDialog;

public class DEFeedbackDialog extends IPlantDialog {

    public DEFeedbackDialog() {
        setHeadingText("Discovery Environment Feedback");
        setSize("400", "500");
        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        setOkButtonText(I18N.DISPLAY.submit());
    }

}
