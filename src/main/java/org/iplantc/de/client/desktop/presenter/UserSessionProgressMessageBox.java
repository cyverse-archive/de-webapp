package org.iplantc.de.client.desktop.presenter;

import java.util.List;

import org.iplantc.core.uicommons.client.models.UserSession;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.core.uicommons.client.views.gxt3.dialogs.IsHideable;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.desktop.views.DEView;
import org.iplantc.de.client.desktop.views.DEView.Presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;

class UserSessionProgressMessageBox extends AutoProgressMessageBox implements IsHideable {

    protected enum ProgressBoxType {
        RESTORE_SESSION, SAVE_SESSION;
    }

    public static UserSessionProgressMessageBox saveSession(DEView.Presenter presenter) {
        UserSessionProgressMessageBox saveSessionMb = new UserSessionProgressMessageBox(I18N.DISPLAY.savingSession(), I18N.DISPLAY.savingSessionWaitNotice(), 
                ProgressBoxType.SAVE_SESSION,
                presenter);
        saveSessionMb.setProgressText(I18N.DISPLAY.savingMask());
        saveSessionMb.setClosable(false);
        return saveSessionMb;
    }

    public static UserSessionProgressMessageBox restoreSession(DEView.Presenter presenter) {
        UserSessionProgressMessageBox restoreSessionMb = new UserSessionProgressMessageBox(I18N.DISPLAY.loadingSession(), I18N.DISPLAY.loadingSessionWaitNotice(), 
                ProgressBoxType.RESTORE_SESSION,
                presenter);
        restoreSessionMb.setProgressText(I18N.DISPLAY.loadingMask());
        restoreSessionMb.setPredefinedButtons(PredefinedButton.CANCEL);
        restoreSessionMb.setClosable(false);
        return restoreSessionMb;
    }

    private final ProgressBoxType type;
    private final DEView.Presenter presenter;
    private final GetUserSessionCallback restoreSessionCallback;
    private final UserSessionFactory factory = GWT.create(UserSessionFactory.class);

    private UserSessionProgressMessageBox(SafeHtml headingHtml, SafeHtml messageHtml, ProgressBoxType type, DEView.Presenter presenter) {
        super(headingHtml, messageHtml);
        this.type = type;
        this.presenter = presenter;
        restoreSessionCallback = new GetUserSessionCallback(this, presenter, factory);
    }

    @Override
    protected void onButtonPressed(TextButton button) {
        if (button == getButtonBar().getItemByItemId(PredefinedButton.CLOSE.name())) {
            restoreSessionCallback.cancelLoad();
        }
        super.onButtonPressed(button);
    }

    @Override
    public void show() {
        super.show();
        if (type.equals(ProgressBoxType.RESTORE_SESSION)) {
            Services.USER_SESSION_SERVICE.getUserSession(restoreSessionCallback);
        } else if (type.equals(ProgressBoxType.SAVE_SESSION)) {
            AutoBean<UserSession> userSession = factory.userSession();
            userSession.as().setWindowStates(presenter.getOrderedWindowStates());
            Services.USER_SESSION_SERVICE.saveUserSession(userSession.as(), new SaveUserSessionCallback(this));
        }

    }

    interface UserSessionFactory extends AutoBeanFactory {
        AutoBean<UserSession> userSession();
    }

    private final class SaveUserSessionCallback implements AsyncCallback<String> {
        private final IsHideable msgBox;
    
        public SaveUserSessionCallback(IsHideable msgBox) {
            this.msgBox = msgBox;
        }
    
        @Override
        public void onSuccess(String result) {
            msgBox.hide();
        }
    
        @Override
        public void onFailure(Throwable caught) {
            GWT.log(I18N.ERROR.saveSessionFailed(), caught);
            msgBox.hide();
        }
    }

    private final class GetUserSessionCallback implements AsyncCallback<String> {
        private final IsHideable msgBox;
        private final Presenter presenter;
        private final UserSessionFactory factory;

        private boolean loadCancelled = false;

        public GetUserSessionCallback(IsHideable msgBox, DEView.Presenter presenter, UserSessionFactory factory) {
            this.msgBox = msgBox;
            this.presenter = presenter;
            this.factory = factory;
        }

        public void cancelLoad() {
            loadCancelled = true;
        }

        @Override
        public void onSuccess(String result) {
            if (!loadCancelled) {
                AutoBean<UserSession> userSession = AutoBeanCodex.decode(factory, UserSession.class, result);
                List<WindowState> windowStates = userSession.as().getWindowStates();
                if (windowStates != null) {
                    presenter.restoreWindows(windowStates);
                }
            }
            msgBox.hide();
        }

        @Override
        public void onFailure(Throwable caught) {
            GWT.log(I18N.ERROR.loadSessionFailed(), caught);
            msgBox.hide();
        }
    }

}
