package org.iplantc.de.client.viewer.commands;

import org.iplantc.core.uicommons.client.models.diskresources.File;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.viewer.views.SeparatedTextViewer;
import org.iplantc.de.client.viewer.views.TextViewerImpl;

import com.google.common.base.Strings;

/**
 * @author sriram
 */
public class TextDataViewCommand implements ViewCommand {

    @Override
    public FileViewer execute(final File file, String infoType) {
        final FileViewer view = getViewerByInfoType(file, infoType);
        return view;
    }

    private FileViewer getViewerByInfoType(final File file, String infoType) {
        if (!Strings.isNullOrEmpty(infoType)) {
            if (infoType.equals("csv") || infoType.equals("tsv")) {
                return new SeparatedTextViewer(file, infoType);

            }
        }
        return new TextViewerImpl(file, infoType);
    }
}
