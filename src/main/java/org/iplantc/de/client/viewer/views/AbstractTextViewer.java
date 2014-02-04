package org.iplantc.de.client.viewer.views;

import org.iplantc.de.commons.client.models.diskresources.File;

public abstract class AbstractTextViewer extends AbstractFileViewer {

    public AbstractTextViewer(File file, String infoType) {
        super(file, infoType);
    }

    public abstract void loadDataWithHeader(boolean header);

    public abstract void skipRows(int val);

}
