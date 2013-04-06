package org.iplantc.de.client.utils.builders.context;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uidiskresource.client.util.DiskResourceUtil;

import com.google.common.base.Strings;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * Build a JSON string to provide context when a user clicks on an item with a data context associated
 * it.
 * 
 * @author amuir
 * 
 */
public class DataContextBuilder extends AbstractContextBuilder {

    private String getParentId(final JSONObject objPayload) {
        String ret = null; // assume failure

        if (objPayload != null) {
            JSONObject objData = JsonUtil.getObject(objPayload, "data"); //$NON-NLS-1$

            if (objData != null) {
                ret = JsonUtil.getString(objData, "parentFolderId"); //$NON-NLS-1$
            }
        }

        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String build(final JSONObject objPayload) {
        String ret = null; // assume failure

        String action = getAction(objPayload);

        if (action != null) {
            if (action.equals("file_uploaded")) { //$NON-NLS-1$
                ret = getUploadedFile(objPayload);
            } else if (action.equals("share") || action.equals("unshare")) { //$NON-NLS-1$ //$NON-NLS-2$
                ret = getSharingFile(objPayload);
            }
        }

        return ret;
    }

    private String getUploadedFile(final JSONObject payload) {
        JSONObject data = JsonUtil.getObject(payload, "data"); //$NON-NLS-1$

        if (data != null) {
            String id = JsonUtil.getString(data, "id"); //$NON-NLS-1$
            String name = JsonUtil.getString(data, "name"); //$NON-NLS-1$
            String idParent = getParentId(payload);

            return buildContext(id, name, idParent);
        }

        return null;
    }

    private String getSharingFile(final JSONObject payload) {
        JSONArray paths = JsonUtil.getArray(payload, "paths"); //$NON-NLS-1$

        if (paths != null && paths.size() > 0) {
            // TODO Just use the first path found in the array for now.
            JSONString path = paths.get(0).isString();

            if (path != null) {
                return build(path.stringValue());
            }
        }

        return null;
    }

    /**
     * Build context json from a disk resource id.
     * 
     * @param resource resource containing id, and name of resource.
     * @return String representation of context JSON. null on failure.
     */
    public String build(final String idDiskResource) {
        if (Strings.isNullOrEmpty(idDiskResource)) {
            return null;
        }

        String resourceName = DiskResourceUtil.parseNameFromPath(idDiskResource);
        String parentId = DiskResourceUtil.parseParent(idDiskResource);

        return buildContext(idDiskResource, resourceName, parentId);
    }

    private String buildContext(final String idDiskResource, String resourceName, String parentId) {
        JSONString id = new JSONString(idDiskResource);
        JSONString name = new JSONString(resourceName);
        JSONString idParent = new JSONString(parentId);
        JSONArray diskresourceIds = new JSONArray();
        diskresourceIds.set(0, id);

        JSONObject obj = new JSONObject();
        obj.put("id", id); //$NON-NLS-1$
        obj.put("name", name); //$NON-NLS-1$
        obj.put("idParent", idParent); //$NON-NLS-1$
        obj.put("folderId", idParent); //$NON-NLS-1$
        obj.put("diskresourceIds", diskresourceIds); //$NON-NLS-1$

        return obj.toString();
    }
}
