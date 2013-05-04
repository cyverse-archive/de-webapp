package org.iplantc.de.client.desktop.presenter;

import org.iplantc.de.client.desktop.views.DEFeedbackView;
import org.iplantc.de.client.desktop.views.DEFeedbackView.Presenter;
import org.iplantc.de.client.desktop.views.DEFeedbackViewImpl;

import com.google.gwt.user.client.ui.HasOneWidget;

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
        System.out.println("-->" + view.validate() + " " + view.toJson());

    }
}
