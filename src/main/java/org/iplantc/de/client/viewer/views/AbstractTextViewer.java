package org.iplantc.de.client.viewer.views;

import org.iplantc.core.uicommons.client.models.diskresources.File;

public abstract class AbstractTextViewer extends AbstractFileViewer {

    public AbstractTextViewer(File file, String infoType) {
        super(file, infoType);
    }

    public abstract void loadDataWithHeader(boolean header);

}
