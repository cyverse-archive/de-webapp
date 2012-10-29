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
public class DataSearchResultSelectedEvent extends GwtEvent<DataSearchResultSelectedEventHandler> {

    /**
     * Defines the GWT Event Type.
     * 
     * @see org.iplantc.de.client.events.DataSearchResultSelectedEventHandler
     */
    public static final GwtEvent.Type<DataSearchResultSelectedEventHandler> TYPE = new GwtEvent.Type<DataSearchResultSelectedEventHandler>();

    private List<String> selectedIds;
    private DiskResource model;
    private String searchTerm;
    private String tag;

    public DataSearchResultSelectedEvent(String tag, String searchTerm, DiskResource model,
            List<String> selectedIds) {
        this.setModel(model);
        this.setSelectedIds(selectedIds);
        this.setSearchTerm(searchTerm);
        this.setTag(tag);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<DataSearchResultSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DataSearchResultSelectedEventHandler handler) {
        handler.onSelection(this);

    }

    /**
     * @return the model
     */
    public DiskResource getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(DiskResource model) {
        this.model = model;
    }

    /**
     * @return the selectedIds
     */
    public List<String> getSelectedIds() {
        return selectedIds;
    }

    /**
     * @param selectedIds the selectedIds to set
     */
    public void setSelectedIds(List<String> selectedIds) {
        this.selectedIds = selectedIds;
    }

    /**
     * @return the searchTerm
     */
    public String getSearchTerm() {
        return searchTerm;
    }

    /**
     * @param searchTerm the searchTerm to set
     */
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param tag the tag to set
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

}
