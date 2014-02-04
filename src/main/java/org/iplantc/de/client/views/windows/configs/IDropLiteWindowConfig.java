package org.iplantc.de.client.views.windows.configs;

import java.util.List;
import java.util.Map;

import org.iplantc.de.commons.client.models.diskresources.DiskResource;
import org.iplantc.de.commons.client.models.diskresources.Folder;

public interface IDropLiteWindowConfig extends WindowConfig {

    Folder getUploadFolderDest();

    List<DiskResource> getResourcesToDownload();

    Folder getCurrentFolder();

    int getDisplayMode();

    void setResourcesToDownload(List<DiskResource> resources);

    void setDisplayMode(int displayMode);

    void setCurrentFolder(Folder currentFolder);

    void setUploadFolderDest(Folder uploadDest);

    void setTypeMap(Map<String, String> map);

    Map<String, String> getTypeMap();

    boolean isSelectAll();

    void setSelectAll(boolean selectAll);

}
