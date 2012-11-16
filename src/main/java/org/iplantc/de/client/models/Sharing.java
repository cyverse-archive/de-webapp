package org.iplantc.de.client.models;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

/**
 * 
 * A class that models sharing
 * 
 * @author sriram
 * 
 */
public class Sharing extends BaseTreeModel implements Cloneable {

    /**
     * 
     */
    private static final long serialVersionUID = -2830576775118848275L;
    private Collaborator collaborator;
    public static final String NAME = "name";

    public Sharing(Collaborator c) {
        this.collaborator = c;
        set(NAME, (c.getName() != null && !c.getName().isEmpty()) ? c.getName() : c.getUserName());
    }

    public String getUserName() {
        return collaborator.getUserName();
    }

    public String getName() {
        return collaborator.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Sharing)) {
            return false;
        }
        Sharing s = (Sharing)o;
        return getKey().equals(s.getKey());
    }

    /**
     * 
     * get the collaborator object
     * 
     * */
    public Collaborator getCollaborator() {
        return collaborator;
    }

    public String getKey() {
        return getUserName();
    }

    public Sharing copy() {
        return new Sharing(getCollaborator());
    }
}
