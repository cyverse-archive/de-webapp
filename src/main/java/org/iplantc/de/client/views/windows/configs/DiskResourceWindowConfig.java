package org.iplantc.de.client.views.windows.configs;

import java.util.List;

import org.iplantc.core.uidiskresource.client.models.DiskResource;
import org.iplantc.core.uidiskresource.client.models.Folder;

public interface DiskResourceWindowConfig extends WindowConfig {

    Folder getSelectedFolder();

    List<DiskResource> getSelectedDiskResources();
    
    void setSelectedFolder(Folder selectedFolder);
    
    void setSelectedDiskResources(List<DiskResource> selectedResources);

}
