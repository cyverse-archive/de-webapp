package org.iplantc.de.client.viewer.commands;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.core.uicommons.client.models.diskresources.File;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.viewer.views.StrcturedTextViewerImpl;
import org.iplantc.de.client.viewer.views.TextViewerImpl;

import com.google.common.base.Strings;

/**
 * @author sriram
 */
public class TextDataViewCommand implements ViewCommand {

    @Override
    public List<FileViewer> execute(final File file, String infoType) {
        final List<FileViewer> viewers = getViewerByInfoType(file, infoType);
        return viewers;
    }

    private List<FileViewer> getViewerByInfoType(final File file, String infoType) {
        List<FileViewer> viewers = new ArrayList<FileViewer>();
        if (!Strings.isNullOrEmpty(infoType)) {
            if (infoType.equals("csv") || infoType.equals("tsv") || infoType.equals("vcf")) {
                viewers.add(new StrcturedTextViewerImpl(file, infoType));

            }
        }
        viewers.add(new TextViewerImpl(file, infoType));
        return viewers;
    }
}
