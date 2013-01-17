package org.iplantc.de.client.utils;

import org.iplantc.de.client.I18N;

import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * A util class for opening pop-ups .If pop-up is blocked by the client, Call showDialogOnPopUpBlock
 * method to display url as link
 * 
 * @author sriram
 * 
 */
public class WindowUtil {
    /**
     * Attempts to open a new window for the given URL and tries to detect if the window was
     * "Popup blocked". Displays a "popup warning" dialog with showDialogOnPopUpBlock if the window open
     * was blocked.
     * 
     * @param url
     */
    public static void open(final String url) {
        open(url, "");
    }

    /**
     * Attempts to open a new window for the given URL and options, and tries to detect if the window was
     * "Popup blocked". Displays a "popup warning" dialog with showDialogOnPopUpBlock if the window open
     * was blocked.
     * 
     * @param url
     * @param options
     */
    public static void open(final String url, final String options) {
        if (!open(url, "", options)) {
            showDialogOnPopUpBlock(url, "", options);
        }
    }

    /**
     * Attempts to open a new window for the given URL and options, and tries to detect if the window was
     * "Popup blocked".
     * 
     * @param url
     * @param window_name
     * @param options
     * @return False if the window open was blocked, otherwise focuses the new window and returns true.
     */
    public static native boolean open(String url, String window_name, String options) /*-{
        var popup = $wnd.open(url, window_name, options);

        if (!popup || popup.closed || typeof popup == 'undefined' || typeof popup.closed == 'undefined') {
            return false;
        }

        popup.focus();
        return true;
    }-*/;

    /**
     * Displays a "popup warning" dialog with an OK button that will open a new window when clicked for
     * the given URL and window options.
     * 
     * @param url
     * @param window_name
     * @param options
     */
    public static void showDialogOnPopUpBlock(final String url, final String window_name,
            final String options) {
        final Window win = new Window();
        win.setHeadingText(I18N.DISPLAY.popUpWarning());
        win.setSize("370", "100");
        win.setClosable(false);

        VerticalLayoutContainer vlc = new VerticalLayoutContainer();
        Label label = new Label(I18N.DISPLAY.popWarningMsg());
        vlc.add(label);

        ButtonBar bar = new ButtonBar();
        TextButton tb = new TextButton("Ok");
        tb.setWidth(50);
        tb.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                com.google.gwt.user.client.Window.open(url, window_name, options);
                win.hide();
            }
        });
        bar.add(tb);
        vlc.add(bar);
        win.add(vlc);
        win.show();
    }

}
