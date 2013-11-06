package org.iplantc.de.client.viewer.commands;

import org.iplantc.core.uicommons.client.models.diskresources.File;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.viewer.views.TreeViwerImpl;

/**
 * @author sriram
 * 
 */
public class TreeViewerCommand implements ViewCommand {

    @Override
    public FileViewer execute(File file, String infoType) {
        FileViewer viewer = new TreeViwerImpl(file);
        return viewer;
    }

}
