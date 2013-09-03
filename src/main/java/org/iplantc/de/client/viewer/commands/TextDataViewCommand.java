package org.iplantc.de.client.viewer.commands;

import org.iplantc.core.uicommons.client.models.diskresources.File;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.viewer.views.TextViewerImpl;

/**
 * @author sriram
 */
public class TextDataViewCommand implements ViewCommand {

    @Override
    public FileViewer execute(final File file, String infoType) {
        final FileViewer view = new TextViewerImpl(file, infoType);
        return view;
    }
}
