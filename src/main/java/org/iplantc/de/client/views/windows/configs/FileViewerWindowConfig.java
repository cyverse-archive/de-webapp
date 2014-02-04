package org.iplantc.de.client.views.windows.configs;

import org.iplantc.de.commons.client.models.diskresources.File;

public interface FileViewerWindowConfig extends WindowConfig {

    File getFile();

    void setFile(File file);

    boolean isEditing();

    void setEditing(boolean editing);

    boolean isShowTreeTab();

    void setShowTreeTab(boolean b);
}
