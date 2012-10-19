/**
 * 
 */
package org.iplantc.de.client.events;

import java.util.List;

import org.iplantc.core.uidiskresource.client.models.DiskResource;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author sriram
 * 
 */
public class LoadDataSearchResultsEvent extends GwtEvent<LoadDataSearchResultsEventHandler> {

    /**
     * Defines the GWT Event Type.
     * 
     * @see org.iplantc.de.client.events.LoadDataSearchResultsEventHandler
     */
    public static final GwtEvent.Type<LoadDataSearchResultsEventHandler> TYPE = new GwtEvent.Type<LoadDataSearchResultsEventHandler>();

    private List<DiskResource> results;

    public LoadDataSearchResultsEvent(List<DiskResource> results) {
        this.setResults(results);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<LoadDataSearchResultsEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(LoadDataSearchResultsEventHandler handler) {
        handler.onLoad(this);

    }

    /**
     * @return the results
     */
    public List<DiskResource> getResults() {
        return results;
    }

    /**
     * @param results the results to set
     */
    public void setResults(List<DiskResource> results) {
        this.results = results;
    }

}
