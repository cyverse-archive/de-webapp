package org.iplantc.de.client.viewer.commands;

import java.util.List;

import org.iplantc.core.uicommons.client.models.diskresources.File;
import org.iplantc.de.client.viewer.views.FileViewer;

/**
 * Basic interface for command pattern
 * 
 * @author sriram
 * 
 */
public interface ViewCommand {
    /**
     * Execute command.
     */
    List<? extends FileViewer> execute(File file, String infoType);
}
