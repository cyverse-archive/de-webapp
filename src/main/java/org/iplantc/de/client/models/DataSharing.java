/**
 * 
 */
package org.iplantc.de.client.models;

import org.iplantc.core.uidiskresource.client.models.Permissions;
import org.iplantc.core.uidiskresource.client.util.DiskResourceUtil;

/**
 * @author sriram
 * 
 */
public class DataSharing extends Sharing {

    /**
     * 
     */
    private static final long serialVersionUID = -6995661215235527506L;
    public static final String READ = "read";
    public static final String WRITE = "write";
    public static final String OWN = "own";
    public static final String PATH = "path";
    public static final String DISPLAY_PERMISSION = "displayPermission";

    public static enum TYPE {
        FILE, FOLDER
    };

    public DataSharing(Collaborator c, Permissions p, String path) {
        super(c);
        set(PATH, path);
        if (p != null) {
            setReadable(p.isReadable());
            setWritable(p.isWritable());
            setOwner(p.isOwner());
        }
        set(Sharing.NAME, DiskResourceUtil.parseNameFromPath(path));

    }

    public boolean isReadable() {
        return Boolean.parseBoolean(get(READ) != null ? get(READ).toString() : "false");
    }

    public boolean isWritable() {
        return Boolean.parseBoolean(get(WRITE) != null ? get(WRITE).toString() : "false");
    }

    public boolean isOwner() {
        return Boolean.parseBoolean(get(OWN) != null ? get(OWN).toString() : "false");
    }

    public void setReadable(boolean read) {
        if (read) {
            set(WRITE, false);
            set(OWN, false);
            setDisplayPermission(READ);
        }
        set(READ, read);
    }

    public void setWritable(boolean write) {
        if (write) {
            set(READ, true);
            setDisplayPermission(WRITE);
        }
        set(WRITE, write);
        set(OWN, false);

    }

    public void setOwner(boolean own) {
        if (own) {
            set(READ, true);
            set(WRITE, true);
            setDisplayPermission(OWN);
        }
        set(OWN, own);

    }

    public String getPath() {
        return get(PATH).toString();
    }

    @Override
    public String getKey() {
        return super.getKey() + get(PATH).toString();
    }

    public void setDisplayPermission(String perm) {
        set(DISPLAY_PERMISSION, perm);
    }

}
