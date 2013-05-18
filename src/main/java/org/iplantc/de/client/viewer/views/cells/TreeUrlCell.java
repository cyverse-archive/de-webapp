/**
 * 
 */
package org.iplantc.de.client.viewer.views.cells;

import org.iplantc.core.uicommons.client.util.WindowUtil;
import org.iplantc.de.client.viewer.models.TreeUrl;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author sriram
 * 
 */
public class TreeUrlCell extends AbstractCell<TreeUrl> {

    public TreeUrlCell() {
        super("click");
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, TreeUrl model, SafeHtmlBuilder sb) {
        // TODO JDS We should use CssResource here
        sb.appendHtmlConstant("<div style=\"cursor:pointer;text-decoration:underline;white-space:pre-wrap;\">" //$NON-NLS-1$
                + model.getUrl() + "</div>"); //$NON-NLS-1$

    }

    @Override
    public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent,
            TreeUrl value, NativeEvent event, ValueUpdater<TreeUrl> valueUpdater) {

        if (value == null) {
            return;
        }
        // Call the super handler, which handlers the enter key.
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        WindowUtil.open(value.getUrl(), "width=100,height=100"); //$NON-NLS-1$
    }

}
