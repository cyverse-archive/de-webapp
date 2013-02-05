package org.iplantc.de.client.views.windows.configs;

import org.iplantc.core.uiapplications.client.models.autobeans.App;
import org.iplantc.core.uiapplications.client.models.autobeans.AppGroup;

public interface AppsWindowConfig extends WindowConfig {

    AppGroup getSelectedAppGroup();

    App getSelectedApp();

    void setSelectedAppGroup(AppGroup appGroup);

    void setSelectedApp(App app);

}
