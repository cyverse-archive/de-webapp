package org.iplantc.de.client.views.windows;

import org.iplantc.core.resources.client.messages.I18N;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.de.client.sysmsgs.presenter.SystemMessagePresenter;
import org.iplantc.de.client.sysmsgs.view.SystemMessagesView;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.SystemMessagesWindowConfig;

/**
 * The window for displaying all active system messages.
 */
public final class SystemMessagesWindow extends IplantWindowBase {

	private final SystemMessagePresenter presenter;
	
	/**
	 * the constructor
	 * 
	 * @param config unused
	 */
	public SystemMessagesWindow(final SystemMessagesWindowConfig unused) {
		super("");
		this.presenter = new SystemMessagePresenter(new SystemMessagesView());
		setTitle(I18N.DISPLAY.systemMessagesLabel());
        setSize("400", "300");
        presenter.go(this);
	}

	@Override
	public WindowState getWindowState() {
		return createWindowState(ConfigFactory.systemMessagesWindowConfig());
	}
	
}
