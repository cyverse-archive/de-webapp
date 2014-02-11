package org.iplantc.de.client.viewer.commands;

import org.iplantc.de.client.Services;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.util.WindowUtil;

import java.util.List;

/**
 * @author sriram
 * 
 */
public class HtmlDataViewCommand implements ViewCommand {

    @Override
    public List<FileViewer> execute(File file, String infoType, boolean editing) {
        if (editing) {
            ErrorAnnouncementConfig config = new ErrorAnnouncementConfig(
                    "Editing is not supported for this type of file!");
            IplantAnnouncer.getInstance().schedule(config);
        }
        WindowUtil.open(Services.FILE_EDITOR_SERVICE.getServletDownloadUrl(file.getId())
                + "&attachment=0"); //$NON-NLS-1$
        return null;
    }
}
