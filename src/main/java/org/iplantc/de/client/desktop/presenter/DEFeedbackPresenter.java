package org.iplantc.de.client.desktop.presenter;

import org.iplantc.core.resources.client.messages.I18N;
import org.iplantc.de.client.desktop.views.DEFeedbackView;
import org.iplantc.de.client.desktop.views.DEFeedbackView.Presenter;
import org.iplantc.de.client.desktop.views.DEFeedbackViewImpl;

import com.google.gwt.user.client.ui.HasOneWidget;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

public class DEFeedbackPresenter implements Presenter {

    DEFeedbackView view;

    public DEFeedbackPresenter() {
        view = new DEFeedbackViewImpl();
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view);
    }

    @Override
    public void validateAndSubmit() {
        System.out.println("-->" + view.validate());
        if (view.validate()) {
            System.out.println("-->" + view.toJson());
        } else {
            AlertMessageBox amb = new AlertMessageBox(I18N.DISPLAY.warning(),
                    I18N.DISPLAY.publicSubmitTip());
            amb.setModal(true);
            amb.show();
        }

    }
}
