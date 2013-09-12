package org.iplantc.de.client.viewer.commands;

import org.iplantc.core.uicommons.client.models.diskresources.File;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.viewer.views.ShellScriptViewerImpl;

public class ShellScriptViewCommand implements ViewCommand {

    @Override
    public FileViewer execute(File file, String infoType) {
        ShellScriptViewerImpl view = new ShellScriptViewerImpl(file);
        return view;
    }

}
