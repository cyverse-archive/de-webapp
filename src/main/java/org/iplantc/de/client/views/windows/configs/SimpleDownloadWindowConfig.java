package org.iplantc.de.client.views.windows.configs;

import java.util.List;

import org.iplantc.core.uidiskresource.client.models.DiskResource;

public interface SimpleDownloadWindowConfig extends WindowConfig {

    List<DiskResource> getResourcesToDownload();

    void setResourcesToDownload(List<DiskResource> resources);
}