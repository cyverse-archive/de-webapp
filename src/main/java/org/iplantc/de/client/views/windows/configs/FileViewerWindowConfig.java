package org.iplantc.de.client.views.windows.configs;

import org.iplantc.core.uicommons.client.models.diskresources.File;

public interface FileViewerWindowConfig extends WindowConfig {

    File getFile();

    void setFile(File file);

    boolean isShowTreeTab();

    void setShowTreeTab(boolean b);
}
