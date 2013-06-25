package org.iplantc.de.client.views.windows.configs;

import java.util.List;

import org.iplantc.core.uicommons.client.models.diskresources.DiskResource;
import org.iplantc.core.uicommons.client.models.diskresources.Folder;

public interface IDropLiteWindowConfig extends WindowConfig {

    Folder getUploadFolderDest();

    List<DiskResource> getResourcesToDownload();

    Folder getCurrentFolder();

    int getDisplayMode();

    void setResourcesToDownload(List<DiskResource> resources);

    void setDisplayMode(int displayMode);

    void setCurrentFolder(Folder currentFolder);

    void setUploadFolderDest(Folder uploadDest);

}
