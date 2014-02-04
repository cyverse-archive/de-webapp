package org.iplantc.de.client.viewer.commands;

import java.util.Arrays;
import java.util.List;

import org.iplantc.de.commons.client.models.diskresources.File;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.viewer.views.ExternalVizualizationURLViwerImpl;

/**
 * @author sriram
 * 
 */
public class VizURLViewerCommand implements ViewCommand {

    @Override
    public List<FileViewer> execute(File file, String infoType, boolean editing) {
        FileViewer viewer = new ExternalVizualizationURLViwerImpl(file, infoType);
        return Arrays.asList(viewer);
    }

}
