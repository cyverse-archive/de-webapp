package org.iplantc.de.client.viewer.commands;

import org.iplantc.core.uicommons.client.models.diskresources.File;
import org.iplantc.core.uicommons.client.util.WindowUtil;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.viewer.views.FileViewer;

/**
 * @author sriram
 * 
 */
public class PdfDataViewCommand implements ViewCommand {

    @Override
    public FileViewer execute(File file) {
        String fileId = file.getId();
        if (fileId != null && !fileId.isEmpty()) {
            // // we got the url of the PDF file, so open it in a new window
            WindowUtil.open(Services.FILE_EDITOR_SERVICE.getServletDownloadUrl(fileId) + "&attachment=0"); //$NON-NLS-1$
        }

        return null;
    }
}
