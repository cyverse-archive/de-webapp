package org.iplantc.de.client.desktop.presenter;

import java.util.List;
import java.util.Map;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.DEServiceFacade;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uicommons.client.models.DEProperties;
import org.iplantc.core.uicommons.client.models.UserInfo;
import org.iplantc.core.uicommons.client.models.UserSettings;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.core.uicommons.client.requests.KeepaliveTimer;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.DeResources;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.desktop.views.DEView;
import org.iplantc.de.client.periodic.MessagePoller;
import org.iplantc.de.shared.services.PropertyServiceFacade;
import org.iplantc.de.shared.services.ServiceCallWrapper;
import org.iplantc.de.shared.services.SessionManagementServiceFacade;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Defines the default view of the workspace.
 * 
 * @author sriram
 */
public class DEPresenter implements DEView.Presenter {

    private final DEView view;
    private final DeResources res;
    private final EventBus eventBus;

    /**
     * Constructs a default instance of the object.
     */
    public DEPresenter(final DEView view, final DeResources resources, EventBus eventBus) {
        this.view = view;
        this.view.setPresenter(this);
        this.res = resources;
        this.eventBus = eventBus;
        // Add a close handler to detect browser refresh events.
        Window.addCloseHandler(new CloseHandler<Window>() {

            @Override
            public void onClose(CloseEvent<Window> event) {
                UserSessionProgressMessageBox uspmb = UserSessionProgressMessageBox.saveSession(DEPresenter.this);
                uspmb.show();
            }
        });
        initializeDEProperties();
    }

    /**
     * Initializes the discovery environment configuration properties object.
     */
    private void initializeDEProperties() {
        PropertyServiceFacade.getInstance().getProperties(new AsyncCallback<Map<String, String>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.systemInitializationError(), caught);
            }
    
            @Override
            public void onSuccess(Map<String, String> result) {
                DEProperties.getInstance().initialize(result);
                getUserInfo();
                getUserPreferences();
                setBrowserContextMenuEnabled(DEProperties.getInstance().isContextClickEnabled());
            }
        });
    }

    /**
     * Retrieves the user information from the server.
     */
    private void getUserInfo() {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "bootstrap"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
    
        DEServiceFacade.getInstance().getServiceData(wrapper, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.retrieveUserInfoFailed(), caught);
            }
    
            @Override
            public void onSuccess(String result) {
                parseWorkspaceId(result);
                initializeUserInfoAttributes();
                initKeepaliveTimer();
            }
        });
    }

    private void getUserPreferences() {
        Services.USER_SESSION_SERVICE.getUserPreferences(new AsyncCallback<String>() {
    
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }
    
            @Override
            public void onSuccess(String result) {
                loadPrerences(JsonUtil.getObject(result));
            }
        });
    }

    private void getUserSession() {
        UserSessionProgressMessageBox uspmb = UserSessionProgressMessageBox.restoreSession(this);
        uspmb.show();
    }

    private void doWorkspaceDisplay() {
        view.drawHeader();
        RootPanel.get().add(view.asWidget());
        initMessagePoller();
    }

    private String parseWorkspaceId(String json) {
        JSONObject obj = JsonUtil.getObject(json);
        // Bootstrap the user-info object with session data provided in JSON
        // format
        UserInfo userInfo = UserInfo.getInstance();
        userInfo.init(obj.toString());
        return userInfo.getWorkspaceId();
    }

    private void initMessagePoller() {
        MessagePoller poller = MessagePoller.getInstance();
        poller.start();
    }

    /**
     * Initializes the session keepalive timer.
     */
    private void initKeepaliveTimer() {
        String target = DEProperties.getInstance().getKeepaliveTarget();
        int interval = DEProperties.getInstance().getKeepaliveInterval();
        if (target != null && !target.equals("") && interval > 0) {
            KeepaliveTimer.getInstance().start(target, interval);
        }
    }

    /**
     * Initializes the username and email for a user.
     * 
     * Calls the session management service to get the attributes associated with a user.
     */
    private void initializeUserInfoAttributes() {
        SessionManagementServiceFacade.getInstance().getAttributes(
                new AsyncCallback<Map<String, String>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        ErrorHandler.post(I18N.ERROR.retrieveUserInfoFailed(), caught);
                    }

                    @Override
                    public void onSuccess(Map<String, String> attributes) {
                        UserInfo userInfo = UserInfo.getInstance();

                        userInfo.setEmail(attributes.get(UserInfo.ATTR_EMAIL));
                        userInfo.setUsername(attributes.get(UserInfo.ATTR_UID));
                        userInfo.setFullUsername(attributes.get(UserInfo.ATTR_USERNAME));
                        userInfo.setFirstName(attributes.get(UserInfo.ATTR_FIRSTNAME));
                        userInfo.setLastName(attributes.get(UserInfo.ATTR_LASTNAME));
                        doWorkspaceDisplay();
                        getUserSession();
                    }
                });
    }

    private void loadPrerences(JSONObject obj) {
        UserSettings.getInstance().setValues(obj);
    }

    /**
     * Disable the context menu of the browser using native JavaScript.
     * 
     * This disables the user's ability to right-click on this widget and get the browser's context menu
     */
    private native void setBrowserContextMenuEnabled(boolean enabled)
    /*-{
		$doc.oncontextmenu = function() {
			return enabled;
		};
    }-*/;

    @Override
    public void go(HasOneWidget container) {/* Do Nothing */}

	@Override
	public void doLogout() {
		// Need to stop polling
		MessagePoller.getInstance().stop();

        UserSessionProgressMessageBox uspmb = UserSessionProgressMessageBox.saveSession(this);
        uspmb.show();

		// Need to perform actual logout redirect.
		Window.Location.assign(Window.Location.getPath() + Constants.CLIENT.logoutUrl());
		
	}

    @Override
    public void restoreWindows(List<WindowState> windowStates) {
        view.restoreWindows(windowStates);
    }

    @Override
    public List<WindowState> getOrderedWindowStates() {
        return view.getOrderedWindowStates();
    }
}
