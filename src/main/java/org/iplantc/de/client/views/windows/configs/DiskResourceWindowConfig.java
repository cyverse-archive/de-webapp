package org.iplantc.de.client.views.windows.configs;

import java.util.List;

import org.iplantc.core.uicommons.client.models.HasId;

public interface DiskResourceWindowConfig extends WindowConfig {

    HasId getSelectedFolder();

    List<HasId> getSelectedDiskResources();
    
    void setSelectedFolder(HasId selectedFolder);
    
    void setSelectedDiskResources(List<HasId> selectedResources);

}
