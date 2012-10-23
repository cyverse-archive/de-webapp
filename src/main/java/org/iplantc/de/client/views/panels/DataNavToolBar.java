package org.iplantc.de.client.views.panels;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.models.UserInfo;
import org.iplantc.core.uicommons.client.views.dialogs.IPlantDialog;
import org.iplantc.core.uicommons.client.views.dialogs.IPlantSubmittableDialog;
import org.iplantc.core.uicommons.client.views.panels.IPlantDialogPanel;
import org.iplantc.core.uidiskresource.client.models.Folder;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.dispatchers.IDropLiteWindowDispatcher;
import org.iplantc.de.client.events.AsyncUploadCompleteHandler;
import org.iplantc.de.client.images.Resources;
import org.iplantc.de.client.services.DiskResourceDeleteCallback;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.utils.DataUtils;
import org.iplantc.de.client.views.panels.DataNavigationPanel.Mode;

import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * 
 * A toolbar for the data navigation panel.
 * 
 * @author sriram
 * 
 */
public class DataNavToolBar extends ToolBar {

    private static final String ID_URL_IMPORT_BTN = "idUrlImportBtn"; //$NON-NLS-1$
    private static final String ID_DATA_IMPORT_BTN = "idDataImportBtn"; //$NON-NLS-1$
    private static final String ID_DATA_SIMPLE_IMPORT_BTN = "idSimpleImportBtn"; //$NON-NLS-1$
    private static final String ID_NEW_FOLDER_BTN = "idNewFolderBtn"; //$NON-NLS-1$
    private static final String ID_RENAME_FOLDER_BTN = "idRenameFolderBtn"; //$NON-NLS-1$
    private static final String ID_DELETE_FOLDER_BTN = "idDeleteFolderBtn"; //$NON-NLS-1$
    private static final String ID_EMPTY_TRASH_BTN = "idEmptyTrashBtn"; //$NON-NLS-1$
    private static final String ID_UPLD_MENU = "idUpldMenu";

    @SuppressWarnings("unused")
    private final String tag;
    private Folder rootFolder;
    private TreePanelSelectionModel<Folder> selectionModel;
    private Component maskingParent;
    private Button addFolder;
    private Button deleteFolder;
    private Button renameFolder;
    private Button emptyTrash;
    private IPlantSubmittableDialog dlgUpload;

    /**
     * create a new instance of this tool bar
     * 
     * @param tag a tag for this widget
     */
    public DataNavToolBar(final String tag, Mode mode) {
        this.tag = tag;

        if (mode.equals(Mode.EDIT)) {
            add(buildImportMenu());
        }

        add(buildAddFolderButton());
        add(buildRenameFolderButton());
        add(buildDeleteFolderButton());
        add(buildEmptyTrashButton());

    }

    /**
     * set the root folder
     * 
     * @param rootFolder the root folder
     */
    public final void setRootFolder(final Folder rootFolder) {
        this.rootFolder = rootFolder;
    }

    /**
     * set selection model used by the tree panel
     * 
     * @param selectionModel
     */
    public void setSelectionModel(TreePanelSelectionModel<Folder> selModel) {
        this.selectionModel = selModel;
        if (selectionModel != null) {
            selectionModel.addSelectionChangedListener(new ActionsSelectionListener());
        }
    }

    public void setMaskingParent(Component maskingParent) {
        this.maskingParent = maskingParent;
    }

    private Button buildImportMenu() {
        Menu importMenu = new Menu();

        importMenu.add(buildBulkUploadFromDesktopButton());
        importMenu.add(buildSimpleUploadFromDesktopButton());
        importMenu.add(buildUrlImportButton());

        Button ret = new Button();

        ret.setIcon(AbstractImagePrototype.create(Resources.ICONS.desktopUpload()));
        ret.setToolTip(I18N.DISPLAY.importLabel());
        ret.setId(ID_UPLD_MENU);
        ret.setMenu(importMenu);

        return ret;
    }

    private Button buildEmptyTrashButton() {
        emptyTrash = new Button();
        emptyTrash.setId(ID_EMPTY_TRASH_BTN);
        emptyTrash.setToolTip(I18N.DISPLAY.emptyTrash());
        emptyTrash.setIcon(AbstractImagePrototype.create(Resources.ICONS.emptyTrash()));
        emptyTrash.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                Listener<MessageBoxEvent> callback = new Listener<MessageBoxEvent>() {
                    @Override
                    public void handleEvent(MessageBoxEvent ce) {
                        // did the user click yes?
                        if (ce.getButtonClicked().getItemId().equals(Dialog.YES)) {
                            // TODO: call empty trash here
                        }
                    }
                };

                MessageBox.confirm(I18N.DISPLAY.emptyTrash(), I18N.DISPLAY.emptyTrashWarning(), callback);
            }
        });
        return emptyTrash;
    }

    private MenuItem buildBulkUploadFromDesktopButton() {
        MenuItem ret = new MenuItem(I18N.DISPLAY.bulkUploadFromDesktop(),
                AbstractImagePrototype.create(Resources.ICONS.desktopUpload()),
                new SelectionListener<MenuEvent>() {
                    @Override
                    public void componentSelected(MenuEvent me) {
                        promptBulkUpload();
                    }
                });

        ret.setId(ID_DATA_IMPORT_BTN);

        return ret;
    }

    private MenuItem buildSimpleUploadFromDesktopButton() {
        MenuItem ret = new MenuItem(I18N.DISPLAY.simpleUploadFromDesktop(),
                AbstractImagePrototype.create(Resources.ICONS.desktopUpload()),
                new SelectionListener<MenuEvent>() {
                    @Override
                    public void componentSelected(MenuEvent me) {
                        promptSimpleUpload();
                    }
                });

        ret.setId(ID_DATA_SIMPLE_IMPORT_BTN);

        return ret;
    }

    private MenuItem buildUrlImportButton() {
        MenuItem ret = new MenuItem(I18N.DISPLAY.urlImport(),
                AbstractImagePrototype.create(Resources.ICONS.urlImport()),
                new SelectionListener<MenuEvent>() {
                    @Override
                    public void componentSelected(MenuEvent me) {
                        promptUrlImport();
                    }
                });

        ret.setId(ID_URL_IMPORT_BTN);

        return ret;
    }

    private void promptSimpleUpload() {
        promptUploadImportForm(FileUploadDialogPanel.MODE.FILE_ONLY);
    }

    private void promptUrlImport() {
        promptUploadImportForm(FileUploadDialogPanel.MODE.URL_ONLY);
    }

    private void promptUploadImportForm(FileUploadDialogPanel.MODE mode) {
        if (selectionModel == null) {
            return;
        }

        final Folder uploadDest = getUploadDestination();

        if (canUpload(uploadDest)) {
            String uploadDestId = uploadDest.getId();
            String username = UserInfo.getInstance().getUsername();

            // provide key/value pairs for hidden fields
            FastMap<String> hiddenFields = new FastMap<String>();
            hiddenFields.put(FileUploadDialogPanel.HDN_PARENT_ID_KEY, uploadDestId);
            hiddenFields.put(FileUploadDialogPanel.HDN_USER_ID_KEY, username);

            // define a handler for upload completion
            AsyncUploadCompleteHandler handler = new AsyncUploadCompleteHandler(uploadDestId) {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void onAfterCompletion() {
                    if (dlgUpload != null) {
                        dlgUpload.hide();
                    }
                }
            };

            FileUploadDialogPanel pnlUpload = new FileUploadDialogPanel(hiddenFields,
                    Constants.CLIENT.fileUploadServlet(), handler, mode);

            dlgUpload = new IPlantSubmittableDialog(I18N.DISPLAY.upload(), 536, pnlUpload);
            dlgUpload.show();
        }
    }

    private void promptBulkUpload() {
        if (selectionModel == null) {
            return;
        }

        final Folder uploadDest = getUploadDestination();

        if (canUpload(uploadDest)) {
            String currentPath = uploadDest.getId();

            IDropLiteWindowDispatcher dispatcher = new IDropLiteWindowDispatcher();
            dispatcher.launchUploadWindow(currentPath, currentPath);
        }
    }

    /**
     * This method determines the destination folder of an upload.
     * 
     * This method requires that the selectionModel and rootFolder have been assigned.
     * 
     * @return The destination folder of an upload.
     */
    private Folder getUploadDestination() {
        assert rootFolder != null : "The root folder has not been assigned."; //$NON-NLS-1$

        if (selectionModel.getSelectedItem() == null) {
            return rootFolder;
        } else {
            return selectionModel.getSelectedItem();
        }
    }

    private boolean canUpload(Folder destination) {
        if (destination != null && DataUtils.canUploadToThisFolder(destination)) {
            return true;
        } else {
            showErrorMsg();
            return false;
        }
    }

    private void showErrorMsg() {
        MessageBox.alert(I18N.DISPLAY.permissionErrorTitle(), I18N.DISPLAY.permissionErrorMessage(),
                null);
    }

    private Button buildDeleteFolderButton() {
        deleteFolder = new Button();
        deleteFolder.setId(ID_DELETE_FOLDER_BTN);
        deleteFolder.setToolTip(I18N.DISPLAY.delete());
        deleteFolder.setIcon(AbstractImagePrototype.create(Resources.ICONS.folderDelete()));
        deleteFolder.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @SuppressWarnings("unchecked")
            @Override
            public void componentSelected(ButtonEvent ce) {
                if (selectionModel == null) {
                    return;
                }

                @SuppressWarnings("rawtypes")
                List resources = selectionModel.getSelectedItems();
                List<String> idFolders = new ArrayList<String>();
                if (!DataUtils.isDeletable(resources)) {
                    showErrorMsg();
                } else {

                    idFolders = DataUtils.getDiskResourceIdList(resources);
                    if (idFolders.size() > 0) {
                        DiskResourceServiceFacade facade = new DiskResourceServiceFacade(maskingParent);
                        facade.delete(JsonUtil.buildJsonArrayString(idFolders),
                                new DiskResourceDeleteCallback(idFolders));
                    }
                }

            }
        });
        return deleteFolder;
    }

    private Button buildRenameFolderButton() {
        renameFolder = new Button();
        renameFolder.setId(ID_RENAME_FOLDER_BTN);
        renameFolder.setToolTip(I18N.DISPLAY.rename());
        renameFolder.setIcon(AbstractImagePrototype.create(Resources.ICONS.folderRename()));
        renameFolder.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                if (selectionModel == null) {
                    return;
                }

                Folder resource = selectionModel.getSelectedItem();
                if (!DataUtils.isRenamable(resource)) {
                    showErrorMsg();
                } else {
                    IPlantDialogPanel panel = new RenameFolderDialogPanel(resource.getId(), resource
                            .getName(), maskingParent);
                    IPlantDialog dlg = new IPlantDialog(I18N.DISPLAY.rename(), 340, panel);

                    dlg.show();
                }
            }
        });
        return renameFolder;
    }

    private Button buildAddFolderButton() {
        addFolder = new Button();
        addFolder.setToolTip(I18N.DISPLAY.newFolder());
        addFolder.setId(ID_NEW_FOLDER_BTN);
        addFolder.setIcon(AbstractImagePrototype.create(Resources.ICONS.folderAdd()));
        addFolder.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                if (selectionModel == null) {
                    return;
                }

                Folder dest = selectionModel.getSelectedItem();

                // by default create it under user root
                if (dest == null) {
                    dest = rootFolder;
                }

                if (!DataUtils.canCreateFolderInThisFolder(dest)) {
                    showErrorMsg();

                } else {
                    IPlantDialog dlg = new IPlantDialog(I18N.DISPLAY.newFolder(), 340,
                            new AddFolderDialogPanel(dest.getId(), maskingParent));
                    dlg.disableOkButton();
                    dlg.show();

                }
            }
        });
        return addFolder;
    }

    private final class ActionsSelectionListener extends SelectionChangedListener<Folder> {

        @Override
        public void selectionChanged(SelectionChangedEvent<Folder> se) {
            // disable all actions by default
            boolean addMenuItemsEnabled = false;
            boolean editMenuItemsEnabled = false;

            if (selectionModel != null && rootFolder != null) {
                // check the selected folder
                Folder selectedFolder = selectionModel.getSelectedItem();

                if (selectedFolder != null
                        && !selectedFolder.getId().startsWith(UserInfo.getInstance().getTrashPath())) {
                    // we can at least add folders to a selected path under the home folder.
                    addMenuItemsEnabled = true;

                    // disable editing items for the home folder
                    editMenuItemsEnabled = !rootFolder.getId().equals(selectedFolder.getId());
                } else {
                    addMenuItemsEnabled = false;
                    editMenuItemsEnabled = false;
                    getItemByItemId(ID_UPLD_MENU).disable();
                }
            }

            // enable or disable the correct actions
            addFolder.setEnabled(addMenuItemsEnabled);
            renameFolder.setEnabled(editMenuItemsEnabled);
            deleteFolder.setEnabled(editMenuItemsEnabled);

        }
    }
}
