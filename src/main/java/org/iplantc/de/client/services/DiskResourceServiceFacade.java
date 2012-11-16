package org.iplantc.de.client.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.DEServiceFacade;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.models.DEProperties;
import org.iplantc.core.uicommons.client.models.UserInfo;
import org.iplantc.core.uidiskresource.client.models.DiskResource;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.util.WindowUtil;
import org.iplantc.de.shared.SharedDataApiServiceFacade;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Provides access to remote services for folder operations.
 * 
 * @author amuir
 * 
 */
public class DiskResourceServiceFacade {
    private final String serviceNamePrefix = "org.iplantc.services.de-data-mgmt"; //$NON-NLS-1$
    private final Component maskingCaller;
    private final UUIDServiceAsync uuidService = GWT.create(UUIDService.class);

    public DiskResourceServiceFacade() {
        this(null);
    }

    public DiskResourceServiceFacade(Component maskingCaller) {
        this.maskingCaller = maskingCaller;
    }

    private void maskCaller() {
        if (maskingCaller != null) {
            maskingCaller.mask(I18N.DISPLAY.loadingMask());
        }
    }

    /**
     * Call service to retrieve the root folder info for the current user
     * 
     * @param callback executed when RPC call completes.
     */
    public void getHomeFolder(AsyncCallback<String> callback) {
        String address = serviceNamePrefix + ".root-folders"; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        callService(callback, wrapper);
    }

    /**
     * Called to retrieve the entire contents of a folder.
     * 
     * @param path path to requested folder.
     * @param callback executed when RPC call completes.
     */
    public void getFolderContents(final String path, AsyncCallback<String> callback) {
        getFolderContents(path, true, callback);
    }

    /**
     * get user's default analyses output folder
     * 
     * @param folderName
     * @param callback
     */
    public void getDefaultOutput(final String folderName, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "default-output-dir?name="
                + folderName;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /**
     * set user's default analyses output folder
     * 
     * @param folderName
     * @param callback
     */
    public void putDefaultOutput(AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "default-output-dir?name="
                + DEProperties.getInstance().getDefaultOutputFolderName();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                "{\"path\":\"" + DEProperties.getInstance().getDefaultOutputFolderName() + "\"}");
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /**
     * Called to retrieve the contents of a folder, with or without its file listing.
     * 
     * @param path path to requested folder.
     * @param includeFiles whether or not to include the file listing of the given folder
     * @param callback executed when RPC call completes.
     */
    public void getFolderContents(final String path, boolean includeFiles, AsyncCallback<String> callback) {
        String fullAddress = serviceNamePrefix + ".directory?includefiles=" + (includeFiles ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        if (path != null && !path.isEmpty()) {
            fullAddress += "&path=" + URL.encodePathSegment(path); //$NON-NLS-1$
        }

        ServiceCallWrapper wrapper = new ServiceCallWrapper(fullAddress);
        callService(callback, wrapper);
    }

    /**
     * Call service to create a new folder
     * 
     * @param folderpath path of the folder to be created
     * @param callback executed when RPC call completes.
     */
    public void createFolder(String folderpath, AsyncCallback<String> callback) {
        String fullAddress = serviceNamePrefix + ".directory-create"; //$NON-NLS-1$
        JSONObject obj = new JSONObject();
        obj.put("path", new JSONString(folderpath)); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, fullAddress,
                obj.toString());
        callService(callback, wrapper);
    }

    /**
     * Check if a list of files or folders exist.
     * 
     * @param diskResourceIds paths to desired resources.
     * @param callback callback executed when RPC call completes.
     */
    public void diskResourcesExist(List<String> diskResourceIds, AsyncCallback<String> callback) {
        String address = serviceNamePrefix + ".exists"; //$NON-NLS-1$

        JSONObject json = new JSONObject();
        json.put("paths", JsonUtil.buildArrayFromStrings(diskResourceIds)); //$NON-NLS-1$
        String body = json.toString();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address, body);
        callService(callback, wrapper);
    }

    /**
     * Fetch preview data for a file.
     * 
     * @param path path to desired file.
     * @param callback callback executed when RPC call completes.
     */
    public void previewFile(final String path, AsyncCallback<String> callback) {
        String address = serviceNamePrefix + ".file-preview"; //$NON-NLS-1$
        JSONObject body = new JSONObject();
        body.put("source", new JSONString(path)); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                body.toString());
        callService(callback, wrapper);
    }

    /**
     * Calls the move folder and move file services for the list of given disk resource ids.
     * 
     * @param diskResources list of file and folder ids to move.
     * @param idDestFolder id of the destination folder.
     */
    public void moveDiskResources(final List<DiskResource> diskResources, final String idDestFolder) {
        ArrayList<String> srcList = new ArrayList<String>();

        for (DiskResource src : diskResources) {
            String srcId = src.getId();
            if (!srcId.equals(idDestFolder)) {
                srcList.add(srcId);
            }
        }

        move(srcList, idDestFolder, new DiskResourceMoveCallback());
    }

    /**
     * Call service to move the given ids to the given destination folder.
     * 
     * @param idSrc list of folder ids to move.
     * @param idDestFolder id of the destination folder.
     * @param callback service success/failure callback
     */
    public void move(final List<String> idSrc, final String idDestFolder, AsyncCallback<String> callback) {
        String address = serviceNamePrefix + ".move"; //$NON-NLS-1$
        String body = "{" + JsonUtil.buildStringArray("sources", idSrc) + ", \"dest\": \"" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + idDestFolder + "\"}"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address, body);
        callService(callback, wrapper);
    }

    /**
     * Call service rename a diskResource.
     * 
     * @param srcName
     * @param destName
     * @param callback service success/failure callback
     */
    public void rename(final String srcName, final String destName, AsyncCallback<String> callback) {
        String fullAddress = serviceNamePrefix + ".rename"; //$NON-NLS-1$
        JSONObject body = new JSONObject();
        body.put("source", new JSONString(srcName)); //$NON-NLS-1$
        body.put("dest", new JSONString(destName)); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, fullAddress,
                body.toString());
        callService(callback, wrapper);
    }

    /**
     * Call service to upload a file from a given URL.
     * 
     * @param url
     * @param dest id of the destination folder.
     * @param callback service success/failure callback
     */
    public void importFromUrl(final String url, final String dest, AsyncCallback<String> callback) {
        String fullAddress = serviceNamePrefix + ".file-urlupload"; //$NON-NLS-1$
        JSONObject body = new JSONObject();
        body.put("dest", new JSONString(dest)); //$NON-NLS-1$
        body.put("address", new JSONString(url)); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, fullAddress,
                body.toString());
        callService(callback, wrapper);
    }

    /**
     * Call service to retrieve upload configuration values for idrop-lite.
     * 
     * @param callback executed when RPC call completes.
     */
    public void upload(AsyncCallback<String> callback) {
        String address = serviceNamePrefix + ".idrop-upload"; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        callService(callback, wrapper);
    }

    /**
     * Call service to retrieve upload configuration values for idrop-lite.
     * 
     * @param callback executed when RPC call completes.
     */
    public void download(JSONArray paths, AsyncCallback<String> callback) {
        String address = serviceNamePrefix + ".idrop-download"; //$NON-NLS-1$

        JSONObject body = new JSONObject();
        body.put("paths", paths); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                body.toString());
        callService(callback, wrapper);
    }

    /**
     * Opens a window to download the file with the given path.
     * 
     * @param path Path of the file to download.
     */
    public void simpleDownload(String path) {
        // We must proxy the download requests through a servlet, since the actual download service may
        // be on a port behind a firewall that the servlet can access, but the client can not.
        String address = Format.substitute("{0}{1}?user={2}&path={3}", GWT.getModuleBaseURL(), //$NON-NLS-1$
                Constants.CLIENT.fileDownloadServlet(), UserInfo.getInstance().getUsername(), path);

        WindowUtil.open(URL.encode(address), "width=100,height=100"); //$NON-NLS-1$
    }

    /**
     * Call service to delete folders.
     * 
     * @param pathsAsJsonArray
     * @param callback executed when RPC call completes.
     */
    public void delete(String pathsAsJsonArray, AsyncCallback<String> callback) {
        String fullAddress = serviceNamePrefix + ".delete"; //$NON-NLS-1$
        String body = "{\"paths\": " + pathsAsJsonArray + "}"; //$NON-NLS-1$ //$NON-NLS-2$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, fullAddress,
                body);
        callService(callback, wrapper);
    }

    /**
     * call service to get metadata
     * 
     * @param path path of resource
     * @param callback execute when RPC call complete
     */
    public void getMetaData(String path, AsyncCallback<String> callback) {
        String fullAddress = serviceNamePrefix + ".metadata" + "?path=" + URL.encodePathSegment(path); //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, fullAddress);
        callService(callback, wrapper);
    }

    /**
     * call service to set metadata
     * 
     * @param path path of resource
     * @param body metadata in json format
     * @param callback execute when RPC call complete
     */
    public void setMetaData(String path, String body, AsyncCallback<String> callback) {
        String fullAddress = serviceNamePrefix
                + ".metadata-batch" + "?path=" + URL.encodePathSegment(path); //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, fullAddress,
                body);
        callService(callback, wrapper);
    }

    /**
     * 
     * Share a resource with give user with permission
     * 
     * 
     * @param body - Post body in JSONObject format
     * @param callback callback object
     */
    public void shareDiskResource(JSONObject body, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "share"; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                body.toString());

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /**
     * UnShare a resource with give user with permission
     * 
     * @param body - Post body in JSONObject format
     * @param callback callback object
     */
    public void unshareDiskResource(JSONObject body, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() + "unshare"; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                body.toString());

        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /**
     * get user permission info on selected disk resources
     * 
     * @param body - Post body in JSONObject format
     * @param callback callback object
     */
    public void getPermissions(JSONObject body, AsyncCallback<String> callback) {
        String fullAddress = serviceNamePrefix + ".permissions"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, fullAddress,
                body.toString());
        callService(callback, wrapper);
    }

    /**
     * copy selected disk resources to destination folder
     * 
     * @param body - Post body in JSONObject format
     * @param callback callback object
     */
    public void copyDiskResource(JSONObject body, AsyncCallback<String> callback) {
        String fullAddress = serviceNamePrefix + ".copy"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, fullAddress,
                body.toString());
        callService(callback, wrapper);
    }

    /**
     * restore a deleted disk resources
     * 
     * @param body
     * @param callback
     */
    public void restoreDiskResource(JSONObject body, AsyncCallback<String> callback) {
        String fullAddress = serviceNamePrefix + ".restore"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, fullAddress,
                body.toString());
        callService(callback, wrapper);
    }

    /**
     * empty user's trash
     * 
     * @param user
     * @param callback
     */
    public void emptyTrash(String user, AsyncCallback<String> callback) {
        String address = serviceNamePrefix + ".empty-trash"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE, address);
        callService(callback, wrapper);
    }

    /**
     * search data
     * 
     * @param term search termt
     * @param callback
     */
    public void search(String term, int size, String type, AsyncCallback<String> callback) {
        String fullAddress = DEProperties.getInstance().getMuleServiceBaseUrl() + "search?search-term="
                + URL.encodePathSegment(term) + "&size=" + size;

        if (type != null && !type.isEmpty()) {
            fullAddress = fullAddress + "&type=" + type;
        }

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, fullAddress);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    /**
     * get users trash path
     * 
     * @param userName
     * @param callback
     */
    public void getUserTrashPath(String userName, AsyncCallback<String> callback) {
        String fullAddress = serviceNamePrefix
                + ".user-trash-dir" + "?path=" + URL.encodePathSegment(userName); //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, fullAddress);
        callService(callback, wrapper);
    }

    public void getStat(String body, AsyncCallback<String> callback) {
        String fullAddress = serviceNamePrefix + ".stat"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, fullAddress,
                body.toString());
        callService(callback, wrapper);
    }
    
    /**
     * Creates public data links with generated UUIDs as data link id.
     * 
     * @param diskResourceIds a list of disk resource ids for which data links will be created
     * @param callback
     */
    public void createDataLinks(final List<String> diskResourceIds, final AsyncCallback<String> callback){
        uuidService.getUUIDs(diskResourceIds.size(), new AsyncCallback<List<String>>() {
            
            @Override
            public void onSuccess(List<String> uuids) {
                
                Map<String, String> resourceIdToDataLinkIdMap = Maps.newHashMap();
                for(String drId : diskResourceIds){
                    resourceIdToDataLinkIdMap.put(drId, uuids.get(diskResourceIds.indexOf(drId)));
                }
                
                
                createDataLinks(resourceIdToDataLinkIdMap, true, callback);
            }
            
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }
        });
    }

    /**
     * Creates a set of public data links for the given disk resources.
     * 
     * @param resourceIdToDataLinkIdMap the id of the disk resource for which the ticket will be created.
     * @param isPublicTicket
     * @param callback
     */
    public void createDataLinks(Map<String, String> resourceIdToDataLinkIdMap, boolean isPublicTicket, AsyncCallback<String> callback){
        String fullAddress = serviceNamePrefix + ".add-tickets";
        String args = "public=" + (isPublicTicket ? 1 : 0);

        JSONObject body = new JSONObject();
        JSONArray tickets = new JSONArray();
        int index = 0;
        for(Entry<String, String> mapEntry : resourceIdToDataLinkIdMap.entrySet()){
            JSONObject subBody = new JSONObject();
            subBody.put("path", new JSONString(mapEntry.getKey()));
            subBody.put("ticket-id", new JSONString(mapEntry.getValue()));
            tickets.set(index, subBody);
            index++;
        }
        body.put("tickets", tickets);

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, fullAddress,
                body.toString());
        wrapper.setArguments(args);
        callService(callback, wrapper);
        
    }
    
    /**
     * Requests a listing of all the tickets for the given disk resources.
     * 
     * @param diskResourceIds the disk resources whose tickets will be listed.
     * @param callback
     */
    public void listDataLinks(List<String> diskResourceIds, AsyncCallback<String> callback){
        String fullAddress = serviceNamePrefix + ".list-tickets";

        JSONObject body = new JSONObject();
        body.put("paths", JsonUtil.buildArrayFromStrings(diskResourceIds));

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, fullAddress,
                body.toString());
        callService(callback, wrapper);
        
    }
    
    /**
     * Requests that the given Kif Share tickets will be deleted.
     * 
     * @param dataLinkIds the tickets which will be deleted.
     * @param callback
     */
    public void deleteDataLinks(List<String> dataLinkIds, AsyncCallback<String> callback){
        String fullAddress = serviceNamePrefix + ".delete-tickets"; 

        JSONObject body = new JSONObject();
        body.put("tickets", JsonUtil.buildArrayFromStrings(dataLinkIds));
        
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, fullAddress,
                body.toString());
        callService(callback, wrapper);
    }
    
    /**
     * Performs the actual service call.
     * 
     * @param callback executed when RPC call completes.
     * @param wrapper the wrapper used to get to the actual service via the service proxy.
     */
    private void callService(AsyncCallback<String> callback, ServiceCallWrapper wrapper) {
        if (callback instanceof DiskResourceServiceCallback) {
            ((DiskResourceServiceCallback)callback).setMaskedCaller(maskingCaller);
        }

        maskCaller();

        SharedDataApiServiceFacade.getInstance().getServiceData(wrapper, callback);
    }
}
