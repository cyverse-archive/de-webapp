package org.iplantc.de.client.viewer.commands;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.core.uicommons.client.info.ErrorAnnouncementConfig;
import org.iplantc.core.uicommons.client.info.IplantAnnouncer;
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
    public List<FileViewer> execute(final File file, String infoType, boolean editing) {
        Long size = 0l;
        if (file != null) {
            size = Long.parseLong(file.getSize());
        }
        List<FileViewer> viewers = null;
        // currently we support editing for files of 8kb or less only
        if (editing && size <= 8192) {
            viewers = getViewerByInfoType(file, infoType, editing);
        } else {
            viewers = getViewerByInfoType(file, infoType, false);
            ErrorAnnouncementConfig config = new ErrorAnnouncementConfig(
                    "Editing is not supported for this type of file or for file sizes > 8Kb.");
            IplantAnnouncer.getInstance().schedule(config);
        }
        return viewers;
    }

    private List<FileViewer> getViewerByInfoType(final File file, String infoType, boolean editing) {
        List<FileViewer> viewers = new ArrayList<FileViewer>();
        if (!Strings.isNullOrEmpty(infoType)) {
            if (infoType.equals("csv") || infoType.equals("tsv") || infoType.equals("vcf")
                    || infoType.equals("gff")) {
                viewers.add(new StrcturedTextViewerImpl(file, infoType));

            }
        }
        viewers.add(new TextViewerImpl(file, infoType, editing));
        return viewers;
    }
}
