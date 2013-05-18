/**
 * 
 */
package org.iplantc.de.client.viewer.commands;

import org.iplantc.core.uidiskresource.client.models.File;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.viewer.views.ImageViewerImpl;

/**
 * @author sriram
 * 
 */
public class ImageDataViewCommand implements ViewCommand {

    @Override
    public FileViewer execute(File file) {

        FileViewer view = null;

        if (file != null && !file.getId().isEmpty()) {
            // we got the url of an image... lets add a tab
            view = new ImageViewerImpl(Services.FILE_EDITOR_SERVICE.getServletDownloadUrl(file.getId()));
        }
        return view;

    }

}
