/**
 * Sencha GXT 3.0.1 - Sencha for GWT Copyright(c) 2007-2012, Sencha, Inc. licensing@sencha.com
 * 
 * http://www.sencha.com/products/gxt/license/
 */
package org.iplantc.de.client.desktop.widget;

import org.iplantc.de.client.models.ShortcutDesc;
import org.iplantc.de.client.views.windows.configs.WindowConfig;

import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * A desktop shortcut.
 */
public class Shortcut extends IconButton {

    private final String action;
    private final String tag;
    private final WindowConfig windowConfig;

    /**
     * Creates a new shortcut.
     */
    public Shortcut(ShortcutDesc desc, SelectHandler handler) {
        super(desc.getId());
        setId(desc.getId());
        setSize("64px", "64px");
        this.windowConfig = desc.getWindowConfig();

        this.action = desc.getAction();
        this.tag = desc.getTag();

        this.addSelectHandler(handler);
        setBorders(false);
    }

    public WindowConfig getWindowConfig() {
        return windowConfig;
    }

    /**
     * Retrieve the action for dispatch.
     * 
     * @return the action associated with this shortcut.
     */
    public String getAction() {
        return action;
    }

    /**
     * Retrieve the tag for dispatch.
     * 
     * @return the tag associated with this shortcut.
     */
    public String getTag() {
        return tag;
    }
}
