package org.iplantc.de.client.viewer.commands;

import java.util.Arrays;
import java.util.List;

import org.iplantc.core.uicommons.client.models.diskresources.File;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.viewer.views.TreeViwerImpl;

/**
 * @author sriram
 * 
 */
public class TreeViewerCommand implements ViewCommand {

    @Override
    public List<FileViewer> execute(File file, String infoType) {
        FileViewer viewer = new TreeViwerImpl(file);
        return Arrays.asList(viewer);
    }

}
