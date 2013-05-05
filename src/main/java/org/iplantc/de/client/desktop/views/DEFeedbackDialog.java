package org.iplantc.de.client.desktop.views;

import org.iplantc.core.resources.client.messages.I18N;
import org.iplantc.core.uicommons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.client.desktop.presenter.DEFeedbackPresenter;
import org.iplantc.de.client.desktop.views.DEFeedbackView.Presenter;

import com.sencha.gxt.core.client.Style.HideMode;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class DEFeedbackDialog extends IPlantDialog {

    public DEFeedbackDialog() {
        setHeadingText("Discovery Environment Feedback");
        setSize("400", "500");
        setHideOnButtonClick(false);
        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        setOkButtonText(I18N.DISPLAY.submit());
        final Presenter p = new DEFeedbackPresenter();
        p.go(this);

        addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                p.validateAndSubmit();
            }
        });
        setAutoHide(false);

    }
}
