package org.iplantc.de.client.viewer.commands;

import org.iplantc.core.uidiskresource.client.models.File;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.utils.WindowUtil;
import org.iplantc.de.client.viewer.views.FileViewer;

/**
 * @author sriram
 * 
 */
public class HtmlDataViewCommand implements ViewCommand {

    @Override
    public FileViewer execute(File file) {
        WindowUtil.open(Services.FILE_EDITOR_SERVICE.getServletDownloadUrl(file.getId())
                + "&attachment=0");
        return null;
    }

}
