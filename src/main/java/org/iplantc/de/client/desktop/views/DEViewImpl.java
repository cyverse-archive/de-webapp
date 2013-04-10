/**
 * 
 */
package org.iplantc.de.client.desktop.views;

import java.util.List;

import org.iplantc.core.uicommons.client.collaborators.presenter.ManageCollaboratorsPresenter.MODE;
import org.iplantc.core.uicommons.client.collaborators.views.ManageCollaboratorsDailog;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uicommons.client.models.UserInfo;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.core.uicommons.client.widgets.IPlantAnchor;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.DeResources;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.desktop.widget.Desktop;
import org.iplantc.de.client.events.NotificationCountUpdateEvent;
import org.iplantc.de.client.events.NotificationCountUpdateEvent.NotificationCountUpdateEventHandler;
import org.iplantc.de.client.events.ShowAboutWindowEvent;
import org.iplantc.core.resources.client.IplantResources;
import org.iplantc.de.client.preferences.views.PreferencesDialog;
import org.iplantc.de.client.utils.WindowUtil;
import org.iplantc.de.client.views.panels.ViewNotificationMenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * Default DE View as Desktop
 * 
 * FIXME JDS Move more UI construction into ui.xml
 * 
 * @author sriram
 * 
 */
public class DEViewImpl implements DEView {

    private static DEViewUiBinder uiBinder = GWT.create(DEViewUiBinder.class);

    @UiField
    HorizontalLayoutContainer headerPanel;
    @UiField
    SimpleContainer mainPanel;

    @UiField
    MarginData centerData;
    @UiField
    BorderLayoutContainer con;

    private NotificationIndicator lblNotifications;
    private ViewNotificationMenu notificationsView;

    private final Widget widget;

    private final DeResources resources;
    private final EventBus eventBus;

    private DEView.Presenter presenter;
    private final Desktop desktop;

    @UiTemplate("DEView.ui.xml")
    interface DEViewUiBinder extends UiBinder<Widget, DEViewImpl> {
    }

    public DEViewImpl(final DeResources resources, final EventBus eventBus) {
        this.resources = resources;
        this.eventBus = eventBus;
        widget = uiBinder.createAndBindUi(this);

        desktop = new Desktop(resources, eventBus);
        con.remove(con.getCenterWidget());
        con.setCenterWidget(desktop, centerData);

        con.setStyleName(resources.css().iplantcBackground());
        initEventHandlers();
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    private void initEventHandlers() {
        EventBus eventbus = EventBus.getInstance();

        // handle data events
        eventbus.addHandler(NotificationCountUpdateEvent.TYPE,
                new NotificationCountUpdateEventHandler() {

                    @Override
                    public void onCountUpdate(NotificationCountUpdateEvent ncue) {
                        int new_count = ncue.getTotal();
                        if (new_count > 0 && new_count > lblNotifications.getCount()) {
                            notificationsView.fetchUnseenNotifications();
                        }
                        notificationsView.setUnseenCount(new_count);
                        lblNotifications.setCount(new_count);

                    }
                });
    }

    @Override
    public void drawHeader() {
        headerPanel.add(buildLogoPanel());
        headerPanel.add(buildHtmlActionsPanel());
    }

    private VerticalLayoutContainer buildLogoPanel() {
        VerticalLayoutContainer panel = new VerticalLayoutContainer();
        panel.setWidth("80%");

        Image logo = new Image(IplantResources.RESOURCES.headerLogo().getSafeUri());
        logo.addStyleName(resources.css().iplantcLogo());
        logo.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent arg0) {
                WindowUtil.open(Constants.CLIENT.iplantHome());
            }
        });

        panel.add(logo);

        return panel;
    }

    private ToolBar buildHtmlActionsPanel() {
        ToolBar panel = new ToolBar();
        panel.setWidth(175);
        panel.add(buildNotificationMenu(I18N.DISPLAY.notifications()));
        panel.add(lblNotifications);
        panel.add(buildActionsMenu(UserInfo.getInstance().getUsername(), buildUserMenu()));

        return panel;
    }

    private TextButton buildNotificationMenu(String menuHeaderText) {
        lblNotifications = new NotificationIndicator(0);
        lblNotifications.ensureDebugId("lblNotifyCnt");

        final TextButton button = new TextButton(menuHeaderText);
        button.ensureDebugId("id" + menuHeaderText);
        notificationsView = new ViewNotificationMenu(eventBus);
        notificationsView.setStyleName(resources.css().de_header_menu_body());
        notificationsView.addShowHandler(new ShowHandler() {

            @Override
            public void onShow(ShowEvent event) {
                button.addStyleName(resources.css().de_header_menu_button_selected());
                notificationsView.addStyleName(resources.css().de_header_menu());
            }
        });
        notificationsView.addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                button.removeStyleName(resources.css().de_header_menu_button_selected());
                notificationsView.removeStyleName(resources.css().de_header_menu());
            }
        });
        button.setMenu(notificationsView);

        return button;
    }

    private TextButton buildActionsMenu(String menuHeaderText, final Menu menu) {
        final TextButton button = new TextButton();
        button.setIcon(IplantResources.RESOURCES.userMenu());
        button.ensureDebugId("id" + menuHeaderText);
        button.setMenu(menu);
        menu.addShowHandler(new ShowHandler() {

            @Override
            public void onShow(ShowEvent event) {
                button.addStyleName(resources.css().de_header_menu_button_selected());
                menu.addStyleName(resources.css().de_header_menu());

            }
        });
        menu.addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                button.removeStyleName(resources.css().de_header_menu_button_selected());
                menu.removeStyleName(resources.css().de_header_menu());
            }
        });

        return button;
    }

    private Menu buildUserMenu() {
        final Menu userMenu = buildMenu();

        userMenu.add(new IPlantAnchor(I18N.DISPLAY.preferences(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                buildAndShowPreferencesDialog();
            }
        }));
        userMenu.add(new IPlantAnchor(I18N.DISPLAY.collaborators(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ManageCollaboratorsDailog dialog = new ManageCollaboratorsDailog(MODE.MANAGE);
                dialog.show();
            }
        }));

        userMenu.add(new IPlantAnchor(I18N.DISPLAY.documentation(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                WindowUtil.open(Constants.CLIENT.deHelpFile());
            }
        }));
        userMenu.add(new IPlantAnchor(I18N.DISPLAY.forums(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                WindowUtil.open(Constants.CLIENT.forumsUrl());
            }
        }));
        userMenu.add(new IPlantAnchor(I18N.DISPLAY.contactSupport(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                WindowUtil.open(Constants.CLIENT.supportUrl());
            }
        }));
        userMenu.add(new IPlantAnchor(I18N.DISPLAY.about(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // displayAboutDe();
                EventBus.getInstance().fireEvent(new ShowAboutWindowEvent());
            }
        }));

        userMenu.add(new IPlantAnchor(I18N.DISPLAY.logout(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // doLogout();
                presenter.doLogout();
            }
        }));

        return userMenu;
    }

    private void buildAndShowPreferencesDialog() {
        PreferencesDialog d = new PreferencesDialog();
        d.show();
    }

    private Menu buildMenu() {
        Menu d = new Menu();
        d.setStyleName(resources.css().de_header_menu_body());
        return d;
    }

    @Override
    public void setPresenter(DEView.Presenter presenter) {
        this.presenter = presenter;
    }

    /**
     * A Label with a setCount method that can set the label's styled text to the count when it's greater
     * than 0, or setting empty text and removing the style for a count of 0 or less.
     * 
     * @author psarando
     * 
     */
    private class NotificationIndicator extends HTML {

        int count;

        public NotificationIndicator(int initialCount) {
            super();
            setWidth("18px");
            setStyleName(resources.css().de_notification_indicator());
            setCount(initialCount);
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
            if (count > 0) {
                setText(String.valueOf(count));
                addStyleName(resources.css().de_notification_indicator_highlight());
                Window.setTitle("(" + count + ") " + I18N.DISPLAY.rootApplicationTitle());
            } else {
                setHTML(SafeHtmlUtils.fromSafeConstant("&nbsp;&nbsp;"));
                removeStyleName(resources.css().de_notification_indicator_highlight());
                Window.setTitle(I18N.DISPLAY.rootApplicationTitle());
            }
        }
    }

    @Override
    public List<WindowState> getOrderedWindowStates() {
        return desktop.getOrderedWindowStates();
    }

    @Override
    public void restoreWindows(List<WindowState> windowStates) {
        for (WindowState ws : windowStates) {
            desktop.restoreWindow(ws);
        }
    }

	@Override
	public final void showSystemNotification(final String msg) {
        // TODO: Implement
	}
}
