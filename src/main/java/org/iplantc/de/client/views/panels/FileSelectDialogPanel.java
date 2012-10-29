package org.iplantc.de.client.views.panels;

import java.util.List;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uidiskresource.client.models.DiskResource;
import org.iplantc.core.uidiskresource.client.models.File;
import org.iplantc.core.uidiskresource.client.models.Folder;
import org.iplantc.core.uidiskresource.client.util.DiskResourceUtil;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.events.DataSearchResultSelectedEvent;
import org.iplantc.de.client.events.DataSearchResultSelectedEventHandler;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;

/**
 * Inner panel for the file selector dialog.
 * 
 * @author lenards
 * 
 */
public class FileSelectDialogPanel extends ResourceSelectDialogPanel {
    /**
     * Construct an instance a file selection panel.
     * 
     * @param file
     * @param currentFolderId
     */
    public FileSelectDialogPanel(File file, String currentFolderId, String tag) {
        super(file, currentFolderId, tag);
        EventBus.getInstance().addHandler(DataSearchResultSelectedEvent.TYPE,
                new DataSearchResultSelectedEventHandler() {

                    @Override
                    public void onSelection(DataSearchResultSelectedEvent event) {
                        if (event.getTag().equals(FileSelectDialogPanel.this.tag)) {
                            setSelection(event.getModel());
                        }

                    }
                });

        mainSelectionChangeListener = new SelectionChangeListenerImpl();
    }

    /**
     * Initialize all components used by this widget.
     */
    @Override
    protected void initComponents() {
        initComponents(I18N.DISPLAY.selectedFile());
    }

    /**
     * Retrieve the file which the user selected.
     * 
     * @return file the user has selected.
     */
    public File getSelectedFile() {
        return (File)selectedResource;
    }

    @Override
    public void select(DiskResource resource) {
        if (pnlMain != null) {
            pnlMain.select(resource.getId(), false);
        }
        String parentFolderId = DiskResourceUtil.parseParent(resource.getId());
        Folder parentFolder = model.getFolder(parentFolderId);
        if (parentFolder != null) {
            pnlNavigation.expandFolder(parentFolder);
        }
        selectFolder(parentFolderId);
    }

    private void setSelection(DiskResource dr) {
        setParentOkButton();

        if (dr instanceof File && dr != null) {
            txtResourceName.setValue(dr.getName());
            selectedResource = dr;
            setCurrentFolderId(DiskResourceUtil.parseParent(dr.getId()));
            enableParentOkButton();
            return;
        }

        // disable OK button if a file is not selected
        txtResourceName.setValue(""); //$NON-NLS-1$
        disableParentOkButton();
    }

    private class SelectionChangeListenerImpl implements Listener<BaseEvent> {
        @Override
        public void handleEvent(BaseEvent be) {
            List<DiskResource> selectedItems = pnlMain.getSelectedItems();
            setSelection(selectedItems.get(0));
        }
    }
}
