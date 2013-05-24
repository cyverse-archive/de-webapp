package org.iplantc.de.client.views.windows;

import org.iplantc.core.resources.client.messages.I18N;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.de.client.sysmsgs.presenter.MessagesPresenter;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.SystemMessagesWindowConfig;

import com.google.gwt.user.client.Window;

/**
 * The window for displaying all active system messages.
 */
public final class SystemMessagesWindow extends IplantWindowBase {

    private static int computeDefaultWidth() {
        return Math.max(400, Window.getClientWidth() / 3);
    }

    private static int computeDefaultHeight() {
        return Math.max(300, Window.getClientHeight() / 3);
    }

	private final MessagesPresenter presenter;
	
	/**
	 * the constructor
	 * 
	 * @param config unused
	 */
	public SystemMessagesWindow(final SystemMessagesWindowConfig unused) {
		super("");
        this.presenter = new MessagesPresenter();
		setTitle(I18N.DISPLAY.systemMessagesLabel());
        this.setWidth(computeDefaultWidth());
        this.setHeight(computeDefaultHeight());
        presenter.go(this);
	}

    /**
     * @see IplantWindowBase#getWindowState()
     */
	@Override
	public WindowState getWindowState() {
		return createWindowState(ConfigFactory.systemMessagesWindowConfig());
	}
	
    /**
     * @see IplantWindowBase#doHide()
     */
    @Override
    protected void doHide() {
        presenter.stop();
        super.doHide();
    }

}
