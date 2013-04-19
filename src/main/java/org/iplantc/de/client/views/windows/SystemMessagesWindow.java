package org.iplantc.de.client.views.windows;

import org.iplantc.core.resources.client.messages.I18N;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.SystemMessagesWindowConfig;

import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;

/**
 * The window for displaying all active system messages.
 */
public final class SystemMessagesWindow extends IplantWindowBase {

	/**
	 * the constructor
	 * 
	 * @param config unused
	 */
	public SystemMessagesWindow(final SystemMessagesWindowConfig unused) {
		super("");
		setTitle(I18N.DISPLAY.systemMessagesLabel());
        setSize("300", "235");
        
        final CenterLayoutContainer panel = new CenterLayoutContainer();
        panel.setWidget(new Label(I18N.DISPLAY.noSystemMessages()));
        setWidget(panel);
	}

	@Override
	public WindowState getWindowState() {
		return createWindowState(ConfigFactory.systemMessagesWindowConfig());
	}
	
}
