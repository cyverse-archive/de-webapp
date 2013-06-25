package org.iplantc.de.client.viewer.commands;

import org.iplantc.core.uicommons.client.models.diskresources.File;
import org.iplantc.core.uicommons.client.util.WindowUtil;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.viewer.views.FileViewer;

/**
 * @author sriram
 * 
 */
public class HtmlDataViewCommand implements ViewCommand {

    @Override
    public FileViewer execute(File file) {
        WindowUtil.open(Services.FILE_EDITOR_SERVICE.getServletDownloadUrl(file.getId())
 + "&attachment=0"); //$NON-NLS-1$
        return null;
    }

}
