package org.iplantc.de.client.commands.data;

import java.util.HashMap;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.de.client.controllers.DataMonitor;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class DiskResourceCopyCommand implements DataCommand {

    @Override
    public void execute(DataMonitor monitor, JSONObject objData) {
        if (monitor != null && objData != null) {
            JSONArray paths = JsonUtil.getArray(objData, "paths"); //$NON-NLS-1$
            String dest = JsonUtil.getString(objData, "destination"); //$NON-NLS-1$

            HashMap<String, String> resource_paths = new HashMap<String, String>();

            for (int i = 0; i < paths.size(); i++) {
                JSONValue src = paths.get(i);
                if (src.isString() != null) {
                    resource_paths.put(src.isString().stringValue(), dest);
                }
            }

            monitor.copyResources(resource_paths);
        }

    }
}
