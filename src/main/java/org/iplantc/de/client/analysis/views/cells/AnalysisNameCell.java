/**
 * 
 */
package org.iplantc.de.client.analysis.views.cells;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.analysis.models.Analysis;
import org.iplantc.de.client.events.WindowShowRequestEvent;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author sriram
 * 
 */
public class AnalysisNameCell extends AbstractCell<Analysis> {

    public AnalysisNameCell() {
        super("click");
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, Analysis model,
            SafeHtmlBuilder sb) {
        if (model != null && model.getResultFolderId() != null && !model.getResultFolderId().isEmpty()) {
            sb.appendHtmlConstant("<div style=\"cursor:pointer;text-decoration:underline;white-space:pre-wrap;\">"
                    + model.getName() + "</div>");
        } else {
            sb.appendHtmlConstant("<div style=\"white-space:pre-wrap;\">" + model.getName() + "</div>");
        }

    }

    @Override
    public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent,
            Analysis value, NativeEvent event, ValueUpdater<Analysis> valueUpdater) {
        if (value == null) {
            return;
        }

        // Call the super handler, which handlers the enter key.
        super.onBrowserEvent(context, parent, value, event, valueUpdater);

        if (value != null && value.getResultFolderId() != null && !value.getResultFolderId().isEmpty()) {
            EventBus.getInstance().fireEvent(new WindowShowRequestEvent(ConfigFactory.diskResourceWindowConfig()));
        }

    }

}
