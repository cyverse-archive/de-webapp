package org.iplantc.de.client.viewer.commands;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.de.commons.client.models.diskresources.File;
import org.iplantc.de.client.viewer.models.InfoType;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.viewer.views.StrcturedTextViewerImpl;
import org.iplantc.de.client.viewer.views.TextViewerImpl;

import com.google.common.base.Strings;

/**
 * @author sriram
 */
public class TextDataViewCommand implements ViewCommand {

    @Override
    public List<FileViewer> execute(final File file, String infoType, boolean editing) {
        List<FileViewer> viewers = null;
        viewers = getViewerByInfoType(file, infoType, editing);
        return viewers;
    }

    private List<FileViewer> getViewerByInfoType(final File file, String infoType, boolean editing) {
        List<FileViewer> viewers = new ArrayList<FileViewer>();
        if (!Strings.isNullOrEmpty(infoType)) {
            if (infoType.equals(InfoType.CSV.toString()) || infoType.equals(InfoType.TSV.toString())
                    || infoType.equals(InfoType.VCF.toString())
                    || infoType.equals(InfoType.GFF.toString())) {
                viewers.add(new StrcturedTextViewerImpl(file, infoType));

            }
        }
        viewers.add(new TextViewerImpl(file, infoType, editing));
        return viewers;
    }
}
