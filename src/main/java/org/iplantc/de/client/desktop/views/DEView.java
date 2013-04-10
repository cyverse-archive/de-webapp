package org.iplantc.de.client.desktop.views;

import java.util.List;

import org.iplantc.core.uicommons.client.models.WindowState;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * DE Main view
 * 
 * 
 * @author sriram
 * 
 */
public interface DEView extends IsWidget {

    public interface Presenter extends org.iplantc.core.uicommons.client.presenter.Presenter {

		void doLogout();

        /**
         * Restores the windows specified by the given list of <code>WindowState</code> objects.
         * This method restores the windows in the order they are given.
         * 
         * @param windowStates
         */
        void restoreWindows(List<WindowState> windowStates);

        List<WindowState> getOrderedWindowStates();
        
        /**
         * has the view display a system notification message
         * 
         * @param msg The system notification message
         */
        void showSystemNotification(String msg);
    }

    /**
     * set up DE main header logo and menus
     * 
     */
    void drawHeader();

    /**
     * Set the presenter for this view
     * 
     * @param presenter
     */
    void setPresenter(final Presenter presenter);

    /**
     * XXX JDS This method should not exist in the view.
     * Eventually, all window management functionality should be contained within the presenter.
     * 
     * @return
     */
    List<WindowState> getOrderedWindowStates();

    void restoreWindows(List<WindowState> windowStates);

    /**
     * displays a system notification message
     * 
     * @param msg The message to display.
     */
    void showSystemNotification(String msg);
}
