package org.iplantc.de.client.viewer.commands;

import java.util.Arrays;
import java.util.List;

import org.iplantc.core.uicommons.client.models.diskresources.File;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.viewer.views.ShellScriptViewerImpl;

public class ShellScriptViewCommand implements ViewCommand {

    @Override
    public List<FileViewer> execute(File file, String infoType) {
        FileViewer view = new ShellScriptViewerImpl(file);
        return Arrays.asList(view);
    }

}
