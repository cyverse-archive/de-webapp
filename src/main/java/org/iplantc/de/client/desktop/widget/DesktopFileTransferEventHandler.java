package org.iplantc.de.client.desktop.widget;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.core.uicommons.client.models.UserInfo;
import org.iplantc.core.uicommons.client.views.dialogs.IPlantSubmittableDialog;
import org.iplantc.core.uidiskresource.client.events.RequestBulkDownloadEvent;
import org.iplantc.core.uidiskresource.client.events.RequestBulkDownloadEvent.RequestBulkDownloadEventHandler;
import org.iplantc.core.uidiskresource.client.events.RequestBulkUploadEvent;
import org.iplantc.core.uidiskresource.client.events.RequestBulkUploadEvent.RequestBulkUploadEventHandler;
import org.iplantc.core.uidiskresource.client.events.RequestImportFromUrlEvent;
import org.iplantc.core.uidiskresource.client.events.RequestImportFromUrlEvent.RequestImportFromUrlEventHandler;
import org.iplantc.core.uidiskresource.client.events.RequestSimpleDownloadEvent;
import org.iplantc.core.uidiskresource.client.events.RequestSimpleDownloadEvent.RequestSimpleDownloadEventHandler;
import org.iplantc.core.uidiskresource.client.events.RequestSimpleUploadEvent;
import org.iplantc.core.uidiskresource.client.events.RequestSimpleUploadEvent.RequestSimpleUploadEventHandler;
import org.iplantc.core.uidiskresource.client.models.autobeans.DiskResource;
import org.iplantc.core.uidiskresource.client.models.autobeans.File;
import org.iplantc.core.uidiskresource.client.models.autobeans.Folder;
import org.iplantc.core.uidiskresource.client.services.DiskResourceServiceFacade;
import org.iplantc.core.uidiskresource.client.util.DiskResourceUtil;
import org.iplantc.core.uidiskresource.client.views.dialogs.FileUploadByUrlDialog;
import org.iplantc.core.uidiskresource.client.views.dialogs.SimpleFileUploadDialog;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.events.AsyncUploadCompleteHandler;
import org.iplantc.de.client.idroplite.util.IDropLiteUtil;
import org.iplantc.de.client.models.IDropLiteWindowConfig;
import org.iplantc.de.client.models.SimpleDownloadWindowConfig;
import org.iplantc.de.client.views.panels.FileUploadDialogPanel;

import com.extjs.gxt.ui.client.core.FastMap;
import com.google.common.collect.Lists;
import com.google.gwt.safehtml.shared.UriUtils;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

class DesktopFileTransferEventHandler implements RequestBulkDownloadEventHandler,
        RequestBulkUploadEventHandler, RequestImportFromUrlEventHandler,
        RequestSimpleDownloadEventHandler, RequestSimpleUploadEventHandler {

    private IPlantSubmittableDialog dlgUpload;
    private final Desktop desktop;
    private final DiskResourceServiceFacade drService = Services.DISK_RESOURCE_SERVICE;

    DesktopFileTransferEventHandler(Desktop desktop) {
        this.desktop = desktop;
    }

    @Override
    public void onRequestSimpleUpload(RequestSimpleUploadEvent event) {
        Folder uploadDest = event.getDestinationFolder();
        // promptUploadImportForm(FileUploadDialogPanel.MODE.FILE_ONLY, uploadDest);

        SimpleFileUploadDialog dlg = new SimpleFileUploadDialog(uploadDest, 
                drService, 
                UriUtils.fromTrustedString(Constants.CLIENT.fileUploadServlet()), 
                UserInfo.getInstance().getUsername());
        dlg.show();
    }

    @Override
    public void onRequestUploadFromUrl(RequestImportFromUrlEvent event) {
        Folder uploadDest = event.getDestinationFolder();
        
        String userName = UserInfo.getInstance().getUsername();
        FileUploadByUrlDialog dlg = new FileUploadByUrlDialog(uploadDest, drService, userName);
        dlg.show();
    }

    @Override
    public void onRequestBulkUpload(RequestBulkUploadEvent event) {
        Folder uploadDest = event.getDestinationFolder();
        if (canUpload(uploadDest)) {
            // Build window config
            IDropLiteWindowConfig configData = new IDropLiteWindowConfig();
            configData.setDisplayMode(IDropLiteUtil.DISPLAY_MODE_UPLOAD);
            configData.setUploadFolderDest(uploadDest);
            configData.setCurrentFolder(uploadDest);

            desktop.showWindow(Constants.CLIENT.iDropLiteTag(), configData);
        }
    }

    @Override
    public void onRequestSimpleDownload(RequestSimpleDownloadEvent event) {
        List<DiskResource> resources = Lists.newArrayList(event.getRequestedResources());
        if (isDownloadable(resources)) {
            if (resources.size() == 1) {
                // Download now
                Services.DISK_RESOURCE_SERVICE.simpleDownload(resources.get(0).getId());
            } else {
                List<String> paths = new ArrayList<String>();

                for (DiskResource resource : resources) {
                    if (resource instanceof File) {
                        paths.add(resource.getId());
                    }
                }

                SimpleDownloadWindowConfig configData = new SimpleDownloadWindowConfig();
                configData.setDownloadPaths(paths);

                desktop.showWindow(Constants.CLIENT.simpleDownloadTag(), configData);
            }
        } else {
            showErrorMsg();
        }

    }

    @Override
    public void onRequestBulkDownload(RequestBulkDownloadEvent event) {
        List<DiskResource> resources = Lists.newArrayList(event.getRequestedResources());
        if (isDownloadable(resources)) {

            // Build window config
            IDropLiteWindowConfig configData = new IDropLiteWindowConfig();
            configData.setDisplayMode(IDropLiteUtil.DISPLAY_MODE_DOWNLOAD);
            configData.setDownloadPaths(resources);
            configData.setCurrentFolder(event.getCurrentFolder());

            desktop.showWindow(Constants.CLIENT.iDropLiteTag(), configData);

        } else {
            showErrorMsg();
        }
    }

    private boolean isDownloadable(List<DiskResource> resources) {
        if ((resources == null) || resources.isEmpty()) {
            return false;
        }

        for (DiskResource dr : resources) {
            if (!dr.getPermissions().isReadable()) {
                return false;
            }
        }
        return true;
    }

    private boolean canUpload(Folder uploadDest) {
        if (uploadDest != null && DiskResourceUtil.canUploadTo(uploadDest)) {
            return true;
        } else {
            showErrorMsg();
            return false;
        }
    }

    private void showErrorMsg() {
        new AlertMessageBox(I18N.DISPLAY.permissionErrorTitle(), I18N.DISPLAY.permissionErrorMessage()).show();
    }

    private void promptUploadImportForm(FileUploadDialogPanel.MODE mode, Folder uploadDest) {

        if (canUpload(uploadDest)) {
            String uploadDestId = uploadDest.getId();
            String username = UserInfo.getInstance().getUsername();

            // provide key/value pairs for hidden fields
            FastMap<String> hiddenFields = new FastMap<String>();
            hiddenFields.put(FileUploadDialogPanel.HDN_PARENT_ID_KEY, uploadDestId);
            hiddenFields.put(FileUploadDialogPanel.HDN_USER_ID_KEY, username);

            // define a handler for upload completion
            AsyncUploadCompleteHandler handler = new AsyncUploadCompleteHandler(uploadDestId) {
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

}
