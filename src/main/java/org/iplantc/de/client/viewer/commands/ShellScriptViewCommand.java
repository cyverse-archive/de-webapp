package org.iplantc.de.client.viewer.commands;

import java.util.Arrays;
import java.util.List;

import org.iplantc.de.commons.client.models.diskresources.File;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.viewer.views.ShellScriptViewerImpl;

public class ShellScriptViewCommand implements ViewCommand {

    @Override
    public List<FileViewer> execute(File file, String infoType, boolean editing) {
        FileViewer view = new ShellScriptViewerImpl(file, editing);
        return Arrays.asList(view);
    }

}
