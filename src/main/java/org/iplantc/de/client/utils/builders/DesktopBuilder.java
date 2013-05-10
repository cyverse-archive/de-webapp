package org.iplantc.de.client.utils.builders;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.de.client.models.ShortcutDesc;
import org.iplantc.de.client.views.windows.configs.WindowConfig;

/**
 * Abstract class for managing desktop shortcuts.
 * 
 * @author amuir
 * 
 */
public abstract class DesktopBuilder {
    private final List<ShortcutDesc> shortcuts = new ArrayList<ShortcutDesc>();

    /**
     * Default constructor.
     */
    public DesktopBuilder() {
        buildShortcuts();
    }

    /**
     * Creates desktop shortcut widgets.
     */
    protected abstract void buildShortcuts();

    protected void addShortcut(String id, String hoverStyle, String index, String caption,
            String action, WindowConfig config) {
        shortcuts.add(new ShortcutDesc(id, hoverStyle, index, caption, action, config));
    }

    /**
     * Retrieves all added shortcuts.
     * 
     * @return all shortcuts.
     */
    public List<ShortcutDesc> getShortcuts() {
        return shortcuts;
    }
}
