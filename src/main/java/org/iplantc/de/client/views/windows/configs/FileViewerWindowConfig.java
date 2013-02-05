package org.iplantc.de.client.views.windows.configs;

import org.iplantc.core.uidiskresource.client.models.File;

public interface FileViewerWindowConfig extends WindowConfig {

    File getFile();

    void setFile(File file);

    boolean isShowTreeTab();

    void setShowTreeTab(boolean b);
}
