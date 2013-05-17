package org.iplantc.de.client.viewer.commands;

import org.iplantc.core.uidiskresource.client.models.File;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.viewer.views.TextViewerImpl;

/**
 * @author sriram
 */
public class TextDataViewCommand implements ViewCommand {

    @Override
    public FileViewer execute(final File file) {
        final FileViewer view = new TextViewerImpl(file);
        return view;
    }
}
