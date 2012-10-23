package org.iplantc.de.client.controllers;

import java.util.List;
import java.util.Map;

import org.iplantc.core.uidiskresource.client.models.File;

import com.google.gwt.json.client.JSONObject;

public interface DataMonitor {
    /**
     * Handle the addition of a new folder.
     * 
     * @param idParentFolder id of parent folder.
     * @param jsonFolder JSON object of new folder.
     */
    void folderCreated(String idParentFolder, JSONObject jsonFolder);

    /**
     * Handle the addition of a new file.
     * 
     * @param idParentFolder id of destination folder for this file.
     * @param info File info model.
     */
    void addFile(String path, File info);

    /**
     * Handle a file being saved with a different name.
     * 
     * @param idOrig id of original file.
     * @param idParent id of parent folder.
     * @param info File info model.
     */
    void fileSavedAs(String idOrig, String idParentFolder, File info);

    /**
     * Rename a file.
     * 
     * @param id id of file to re-name.
     * @param name new file name.
     */
    void rename(String id, String name);

    /**
     * Delete disk resources by id.
     * 
     * @param diskResources list of ids to be deleted.
     * 
     */
    void deleteResources(List<String> diskResources);

    /**
     * Move disk resource
     * 
     * @param resources map containing source and destination
     * 
     */
    void moveResource(Map<String, String> resource);

    /**
     * copy disk resources by id.
     * 
     * @param map of copy paths
     */
    void copyResources(Map<String, String> paths);
}
