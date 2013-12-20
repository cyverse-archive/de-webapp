/**
 * 
 */
package org.iplantc.de.client.viewer.commands;

import java.util.Arrays;
import java.util.List;

import org.iplantc.core.uicommons.client.info.ErrorAnnouncementConfig;
import org.iplantc.core.uicommons.client.info.IplantAnnouncer;
import org.iplantc.core.uicommons.client.models.diskresources.File;
import org.iplantc.de.client.viewer.views.AbstractFileViewer;
import org.iplantc.de.client.viewer.views.ImageViewerImpl;

/**
 * @author sriram
 * 
 */
public class ImageDataViewCommand implements ViewCommand {

    @Override
    public List<AbstractFileViewer> execute(File file, String infoType, boolean editing) {

        AbstractFileViewer view = null;

        if (editing) {
            ErrorAnnouncementConfig config = new ErrorAnnouncementConfig(
                    "Editing is not supported for this type of file!");
            IplantAnnouncer.getInstance().schedule(config);
        }

        if (file != null && !file.getId().isEmpty()) {
            // we got the url of an image... lets add a tab
            view = new ImageViewerImpl(file);
        }
        return Arrays.asList(view);

    }

}
