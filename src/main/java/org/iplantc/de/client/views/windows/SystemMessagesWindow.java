package org.iplantc.de.client.views.windows;

import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.SystemMessagesWindowConfig;

public final class SystemMessagesWindow extends IplantWindowBase {

	public SystemMessagesWindow(final SystemMessagesWindowConfig config) {
		super("");
	}

	@Override
	public WindowState getWindowState() {
		return createWindowState(ConfigFactory.systemMessagesWindowConfig());
	}
}
