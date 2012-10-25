package org.iplantc.de.client.views.panels;

import org.iplantc.de.client.I18N;
import org.iplantc.de.client.services.DiskResourceRenameCallback;
import org.iplantc.de.client.services.DiskResourceServiceFacade;

import com.extjs.gxt.ui.client.widget.Component;

/**
 * Provides a user interface to prompt the user for renaming a file.
 */
public class RenameFileDialogPanel extends RenameDiskResourceDialogPanel {

    /**
     * Instantiate from an id, the file's original name, and a component to unmask in the callback.
     * 
     * @param id unique id of file to be re-named.
     * @param nameOrig original file before re-naming.
     * @param maskingParent a component that will be unmasked in the service callback.
     */
    public RenameFileDialogPanel(String id, String nameOrig, Component maskingParent) {
        super(I18N.DISPLAY.fileName(), id, nameOrig, maskingParent);
    }

    @Override
    protected void callRenameService(String srcName, String destName) {
        DiskResourceServiceFacade facade = new DiskResourceServiceFacade(maskingParent);
        facade.rename(srcName, destName, new DiskResourceRenameCallback(srcName, destName));
    }
}
