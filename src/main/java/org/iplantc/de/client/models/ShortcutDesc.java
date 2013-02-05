package org.iplantc.de.client.models;

import org.iplantc.de.client.views.windows.configs.WindowConfig;

/**
 * Models the data associated to a desktop shortcut.
 * 
 * @author amuir
 * 
 */
public class ShortcutDesc {
    private final String id;
    private final String caption;
    private String action;
    private String tag;
    private final WindowConfig config;

    public ShortcutDesc(String id, String caption, String action, WindowConfig config) {
        this.id = id;
        this.caption = caption;
        if (action != null) {
            this.action = action;
        }

        this.config = config;
    }

    public WindowConfig getWindowConfig() {
        return config;
    }

    /**
     * Retrieves the unique identifier.
     * 
     * @return unique identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the caption.
     * 
     * @return shortcut caption.
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Retrieve action.
     * 
     * @return action field.
     */
    public String getAction() {
        return action;
    }

    /**
     * Retrieve tag.
     * 
     * @return tag field.
     */
    public String getTag() {
        return tag;
    }
}
