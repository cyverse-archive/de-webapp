/**
 * 
 */
package org.iplantc.de.client.events;

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

    private DiskResource model;

    public DataSearchResultSelectedEvent(DiskResource model) {
        this.setModel(model);
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

}
