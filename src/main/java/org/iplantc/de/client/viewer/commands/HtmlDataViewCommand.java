package org.iplantc.de.client.viewer.commands;

import java.util.List;

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
    public List<FileViewer> execute(File file, String infoType) {
        WindowUtil.open(Services.FILE_EDITOR_SERVICE.getServletDownloadUrl(file.getId())
                + "&attachment=0"); //$NON-NLS-1$
        return null;
    }

}
