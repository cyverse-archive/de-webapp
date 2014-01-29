package org.iplantc.de.client.views.windows;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uicommons.client.events.diskresources.DiskResourceRefreshEvent;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.core.uicommons.client.views.gxt3.dialogs.IplantInfoBox;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.idroplite.presenter.IDropLitePresenter;
import org.iplantc.de.client.idroplite.util.IDropLiteUtil;
import org.iplantc.de.client.idroplite.views.IDropLiteView;
import org.iplantc.de.client.idroplite.views.IDropLiteView.Presenter;
import org.iplantc.de.client.idroplite.views.IDropLiteViewImpl;
import org.iplantc.de.client.views.windows.configs.IDropLiteWindowConfig;

import com.google.common.base.Strings;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.Style.HideMode;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent.DeactivateHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

/**
 * @author sriram
 * 
 */
public class IDropLiteAppletWindow extends IplantWindowBase {

    private final IDropLiteWindowConfig idlwc;

    public IDropLiteAppletWindow(IDropLiteWindowConfig config) {
        super("");
        this.idlwc = config;
        setSize("850", "430");
        setResizable(false);
        init();
    }

    private void init() {
        // These settings enable the window to be minimized or moved without reloading the applet.
        removeFromParentOnHide = false;
        setHideMode(HideMode.VISIBILITY);

        initViewMode();
        initListeners();

        IDropLiteView view = new IDropLiteViewImpl();
        Presenter p = new IDropLitePresenter(view, idlwc);
        p.go(this);
    }

    private void initListeners() {
        if (GXT.isWindows()) {
            // In Windows, the applet always stays on top, blocking access to everything else.
            // So minimize this window if it loses focus.
            addDeactivateHandler(new DeactivateHandler<Window>() {

                @Override
                public void onDeactivate(DeactivateEvent<Window> event) {
                    minimize();
                }
            });
        }
    }

    private int initViewMode() {
        // Set the heading and add the correct simple mode button based on the applet display mode.
        int displayMode = idlwc.getDisplayMode();
        if (displayMode == IDropLiteUtil.DISPLAY_MODE_UPLOAD) {
            setTitle(I18N.DISPLAY.upload());

        } else if (displayMode == IDropLiteUtil.DISPLAY_MODE_DOWNLOAD) {
            setTitle(I18N.DISPLAY.download());
        }

        return displayMode;

    }

    protected void confirmHide() {
        super.doHide();

        // refresh manage data window
        String refreshPath = idlwc.getCurrentFolder().getId();
        if (!Strings.isNullOrEmpty(refreshPath)) {
            DiskResourceRefreshEvent event = new DiskResourceRefreshEvent(refreshPath, null);
            EventBus.getInstance().fireEvent(event);
        }
    }

    @Override
    protected void doHide() {
        promptRemoveApplet(new Command() {
            @Override
            public void execute() {
                confirmHide();
            }
        });
    }

    private void promptRemoveApplet(final Command cmdRemoveAppletConfirmed) {
        if (GXT.isWindows()) {
            // In Windows, the applet always stays on top, blocking access to the confirmation dialog,
            // which is modal and blocks access to everything else.
            minimize();
        }

        final ConfirmMessageBox cmb = new ConfirmMessageBox(I18N.DISPLAY.idropLiteCloseConfirmTitle(),
                I18N.DISPLAY.idropLiteCloseConfirmMessage());

        cmb.addHideHandler(new HideHandler() {

            @Override
            public void onHide(HideEvent event) {
                if (GXT.isWindows()) {
                    show();
                }

                if (cmb.getHideButton().getText().equalsIgnoreCase("yes")) {
                    // The user confirmed closing the applet.
                    cmdRemoveAppletConfirmed.execute();
                }

            }
        });

        cmb.show();
    }

    @Override
    public WindowState getWindowState() {
        return createWindowState(idlwc);
    }

}
