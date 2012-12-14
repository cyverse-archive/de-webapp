package org.iplantc.de.client.views.form;

import org.iplantc.core.uicommons.client.widgets.SearchField;
import org.iplantc.de.client.I18N;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;

/**
 * A SearchField that filters Analyses by Name and App Name simultaneously, or alternatively by Analysis
 * ID. Filtering by Analysis ID will clear and disable the Name and App filters, until this field is
 * cleared or another query is triggered, at which time the ID filter will be cleared and disabled.
 * 
 * @author psarando
 * 
 */
public class AnalysisSearchField extends SearchField {
    private StringFilter analysisIdFilter;
    private StringFilter appFilter;

    public AnalysisSearchField() {
        // Initialize default filter to the Analysis Name field.
        super("name"); //$NON-NLS-1$

        setEmptyText(I18N.DISPLAY.filterAnalysesList());

        initFilters();
    }

    /**
     * Initialize the App and ID filters, adding a listener to the Name filter that keeps its value in
     * sync with the App filter value.
     */
    private void initFilters() {
        analysisIdFilter = new StringFilter("id"); //$NON-NLS-1$
        analysisIdFilter.setActive(false, true);

        appFilter = new StringFilter("analysis_name"); //$NON-NLS-1$

        final StringFilter nameFilter = getNameFilter();
        Listener<BaseEvent> filterListener = new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                appFilter.setValue(nameFilter.getValue());
            }
        };
        nameFilter.addListener(Events.Update, filterListener);
        nameFilter.addListener(Events.Activate, filterListener);
    }

    public StringFilter getNameFilter() {
        return getFilter();
    }

    public StringFilter getAppFilter() {
        return appFilter;
    }

    public StringFilter getAnalysisIdFilter() {
        return analysisIdFilter;
    }

    /**
     * Enables the ID filter with the given analysisId, setting the field's text to the given
     * analysisName, and disabling the Name and App filters (until this field is cleared or a new query
     * is entered).
     * 
     * @param analysisId
     * @param analysisName
     */
    public void filterByAnalysisId(String analysisId, String analysisName) {
        analysisIdFilter.setActive(true, true);
        appFilter.setActive(false, true);
        getNameFilter().setActive(false, true);

        analysisIdFilter.setValue(analysisId);

        setValue(analysisName);
        setTriggerMode(TriggerMode.CLEAR);
    }

    private void clearAnalysisIdFilter() {
        analysisIdFilter.setValue(null);
        analysisIdFilter.setActive(false, true);
        appFilter.setActive(true, true);
        getNameFilter().setActive(true, true);
    }

    @Override
    public void doQuery(String filterValue) {
        if (analysisIdFilter.isActive()) {
            clearAnalysisIdFilter();
        }
        super.doQuery(filterValue);
    }

    @Override
    protected void clearFilter() {
        if (analysisIdFilter.isActive()) {
            clearAnalysisIdFilter();
        }
        super.clearFilter();
    }
}
