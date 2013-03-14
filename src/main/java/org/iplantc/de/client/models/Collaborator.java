/**
 * 
 */
package org.iplantc.de.client.models;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * A model class to store collaborators attributes
 * 
 * @author sriram
 * 
 */
@SuppressWarnings("nls")
public class Collaborator extends BaseModelData {

    /**
     * 
     */
    private static final long serialVersionUID = 3956502462940674969L;

    public static final String ID = "id";
    public static final String FIRST_NAME = "firstname";
    public static final String LAST_NAME = "lastname";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String NAME = "name";
    public static final String INSTITUTION = "institution";

    public Collaborator(JsCollaborators jsCollaborators) {
        set(ID, jsCollaborators.getId());
        set(NAME, jsCollaborators.getFirstName() + " " + jsCollaborators.getLastName());
        set(EMAIL, jsCollaborators.getEmail());
        set(USERNAME, jsCollaborators.getUserName());
        set(INSTITUTION, jsCollaborators.getInstitution());
    }

    public Collaborator(String id, String username, String firstName, String lastName, String email,
            String institution) {
        if (id == null) {
            set(ID, "");
        } else {
            set(ID, id);
        }
        if (firstName == null) {
            set(NAME, "");
        } else {
            set(NAME, firstName + ((lastName != null) ? (" " + lastName) : ""));
        }
        if (email == null) {
            set(EMAIL, "");
        } else {
            set(EMAIL, email);
        }
        if (username == null) {
            set(USERNAME, "");
        } else {
            set(USERNAME, username);
        }

        if (institution == null) {
            set(INSTITUTION, "");
        } else {
            set(INSTITUTION, institution);
        }

    }

    public String getId() {
        return get(ID);
    }

    /**
     * Get users full name formatted
     * 
     * @return get first name + last name
     */
    public String getName() {
        return get(NAME);
    }

    public String getEmail() {
        return get(EMAIL);
    }

    public String getUserName() {
        return get(USERNAME);
    }

    public String getInstitution() {
        return get(INSTITUTION);
    }

    @Override
    public boolean equals(Object c) {
        if (c == null) {
            return false;
        }
        if (!(c instanceof Collaborator)) {
            return false;
        }

        Collaborator collab = (Collaborator)c;
        if (getId().equals(collab.getId())) {
            return true;
        }
        return false;

    }

    @Override
    public int hashCode() {
        return Integer.parseInt(getId());
    }

    @Override
    public String toString() {
        JSONObject ret = new JSONObject();
        ret.put(USERNAME, new JSONString(getUserName()));

        return ret.toString();
    }
}
