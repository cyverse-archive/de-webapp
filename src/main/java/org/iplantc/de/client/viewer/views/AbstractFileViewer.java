/**
 * 
 */
package org.iplantc.de.client.viewer.views;

import org.iplantc.core.uicommons.client.models.diskresources.File;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author sriram
 * 
 */
public abstract class AbstractFileViewer implements FileViewer {

    protected File file;

    protected String infoType;

    protected Presenter presenter;

    public AbstractFileViewer(File file, String infoType) {
        this.file = file;
        this.infoType = infoType;
    }

    public abstract Widget asWidget();

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.iplantc.de.client.viewer.views.FileViewer#setPresenter(org.iplantc.de.client.viewer.views.
     * FileViewer.Presenter)
     */
    @Override
    public void setPresenter(Presenter p) {
        this.presenter = p;

    }

    public abstract void setData(Object data);

    public abstract void loadData();

    /*
     * (non-Javadoc)
     * 
     * @see org.iplantc.de.client.viewer.views.FileViewer#getFileSize()
     */
    @Override
    public long getFileSize() {
        if (file != null) {
            return Long.parseLong(file.getSize());
        }

        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.iplantc.de.client.viewer.views.FileViewer#getInfoType()
     */
    @Override
    public String getInfoType() {
        return infoType;
    }

}
