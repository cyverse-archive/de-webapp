package org.iplantc.de.client.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.models.WindowConfig;
import org.iplantc.core.uidiskresource.client.models.DiskResource;
import org.iplantc.core.uidiskresource.client.models.File;
import org.iplantc.core.uidiskresource.client.models.Folder;
import org.iplantc.core.uidiskresource.client.util.DiskResourceUtil;
import org.iplantc.de.client.idroplite.util.IDropLiteUtil;

import com.google.common.collect.Lists;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;

/**
 * A Window config for the idrop-lite applet window.
 * 
 * TODO JDS Create two derived configs, one for download and one for upload
 * 
 * @author psarando
 * 
 */
public class IDropLiteWindowConfig extends WindowConfig {

    private static final String DISPLAY_MODE = "displayMode"; //$NON-NLS-1$
    private static final String UPLOAD_DEST = "uploadDest"; //$NON-NLS-1$
    private static final String DOWNLOAD_PATHS_FOLDERS = "folder_paths"; //$NON-NLS-1$
    private static final String DOWNLOAD_PATHS_FILES = "file_paths"; //$NON-NLS-1$
    private static final String MANAGE_DATA_CURRENT_PATH = "currentPath"; //$NON-NLS-1$
    private static final String TAG_SUFFIX = "tag_suffix"; //$NON-NLS-1$
    private static final String TAG_SUFFIX_UPLOAD = "_upload"; //$NON-NLS-1$
    private static final String TAG_SUFFIX_DOWNLOAD = "_download"; //$NON-NLS-1$
    private List<DiskResource> resourcesToDownload;
    private Folder uploadDest;
    private Folder currentFolder;

    public IDropLiteWindowConfig() {
        super();
    }

    public IDropLiteWindowConfig(JSONObject json) {
        super(json);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTagSuffix() {
        return "#" + JsonUtil.getString(this, TAG_SUFFIX);
    }

    public int getDisplayMode() {
        Number mode = JsonUtil.getNumber(this, DISPLAY_MODE);

        if (mode != null) {
            return mode.intValue();
        }

        return 0;
    }

    /**
     * Sets the display mode, and also sets the appropriate TAG_SUFFIX value based on this mode.
     * 
     * @param mode
     */
    public void setDisplayMode(int mode) {
        put(DISPLAY_MODE, new JSONNumber(mode));

        if (mode == IDropLiteUtil.DISPLAY_MODE_UPLOAD) {
            setString(TAG_SUFFIX, TAG_SUFFIX_UPLOAD);
        } else if (mode == IDropLiteUtil.DISPLAY_MODE_DOWNLOAD) {
            setString(TAG_SUFFIX, TAG_SUFFIX_DOWNLOAD);
        }
    }

    public Folder getUploadFolderDest() {
        return uploadDest;
    }

    public void setUploadFolderDest(Folder uploadDest) {
        this.uploadDest = uploadDest;
        setString(UPLOAD_DEST, uploadDest.getId());
    }

    public Folder getCurrentFolder() {
        return currentFolder;
    }

    public void setCurrentFolder(Folder currentFolder) {
        this.currentFolder = currentFolder;
        setString(MANAGE_DATA_CURRENT_PATH, currentFolder.getId());
    }

    public JSONArray getDownloadPaths() {
        JSONArray ret = null;

        List<String> paths = new ArrayList<String>();
        List<String> folderPaths = getFolderDownloadPaths();
        List<String> filePaths = getFileDownloadPaths();

        if (folderPaths != null) {
            paths.addAll(folderPaths);
        }
        if (filePaths != null) {
            paths.addAll(filePaths);
        }

        if (!paths.isEmpty()) {
            ret = JsonUtil.buildArrayFromStrings(paths);
        }

        return ret;
    }

    public List<DiskResource> getResourcesToDownload() {
        return resourcesToDownload;
    }

    public void setDownloadPaths(Collection<DiskResource> resources) {
        resourcesToDownload = Lists.newArrayList(resources);
        List<String> folderPaths = null;
        List<String> filePaths = null;

        if (resources != null) {
            folderPaths = new ArrayList<String>();
            filePaths = new ArrayList<String>();

            for (DiskResource resource : resources) {
                if (resource instanceof Folder) {
                    folderPaths.add(resource.getId());
                }
                if (resource instanceof File) {
                    filePaths.add(resource.getId());
                }
            }

        }

        setFolderDownloadPaths(folderPaths);
        setFileDownloadPaths(filePaths);

    }

    /**
     * Stores the list of folder paths.
     * 
     * @param paths
     */
    private void setFolderDownloadPaths(List<String> paths) {
        put(DOWNLOAD_PATHS_FOLDERS, JsonUtil.buildArrayFromStrings(paths));
    }

    private List<String> getFolderDownloadPaths() {
        return DiskResourceUtil.asStringIdList(DiskResourceUtil.extractFolders(resourcesToDownload));
    }

    /**
     * Stores the list of file paths.
     * 
     * @param paths
     */
    private void setFileDownloadPaths(List<String> paths) {
        put(DOWNLOAD_PATHS_FILES, JsonUtil.buildArrayFromStrings(paths));
    }

    private List<String> getFileDownloadPaths() {
        return DiskResourceUtil.asStringIdList(DiskResourceUtil.extractFiles(resourcesToDownload));
    }
}
