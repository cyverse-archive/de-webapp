package org.iplantc.de.client.views.widgets;


import java.util.ArrayList;

import org.iplantc.core.uiapplications.client.I18N;
import org.iplantc.core.uiapplications.client.models.Analysis;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.models.Collaborator;
import org.iplantc.de.client.utils.UserSearchRpcProxy;

import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ListModelPropertyEditor;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * A ComboBox for the App Catalog main toolbar that performs remote app searches.
 * 
 * @author psarando
 * 
 */
public class UserSearchField extends ComboBox<Collaborator> {
    // private enum TriggerMode {
    //        SEARCH("x-form-search-trigger"), //$NON-NLS-1$
    //        CLEAR("x-form-clear-trigger"); //$NON-NLS-1$
    //
    // private final String triggerStyle;
    //
    // TriggerMode(String triggerStyle) {
    // this.triggerStyle = triggerStyle;
    // }
    //
    // protected String getTriggerStyle() {
    // return triggerStyle;
    // }
    // }

    // private TriggerMode mode = TriggerMode.SEARCH;
    private final UserSearchRpcProxy searchProxy;
    private ArrayList<HandlerRegistration> handlers;

    protected String tag;

    public UserSearchField(String tag) {
        this.tag = tag;
        this.searchProxy = new UserSearchRpcProxy(tag);

        initComboBox();
        initListeners();
    }

    private void initComboBox() {
        setWidth(255);
        setItemSelector("div.search-item"); //$NON-NLS-1$
        setTemplate(buildTemplate());
        setTriggerStyle("x-form-search-trigger"); //$NON-NLS-1$
        setEmptyText(I18N.DISPLAY.searchApps());
        setMinChars(3);

        // Create a loader with our custom RpcProxy.
        ListLoader<ListLoadResult<Analysis>> loader = new BaseListLoader<ListLoadResult<Analysis>>(searchProxy);

        // Create the store
        final ListStore<Collaborator> store = new ListStore<Collaborator>(loader);

        // We need to use a custom key string that will allow the combobox to find the correct model if 2
        // apps in different groups have the same name, since the combo's SelectionChange event will find
        // the first model that matches the raw text in the combo's text field.
        final ModelKeyProvider<Collaborator> storeKeyProvider = new ModelKeyProvider<Collaborator>() {
            @Override
            public String getKey(Collaborator model) {
                return model.getUserName();
            }
        };

        store.setKeyProvider(storeKeyProvider);

        // Use the custom key provider for model lookups from the raw text in the combo's text field.
        ListModelPropertyEditor<Collaborator> propertyEditor = new ListModelPropertyEditor<Collaborator>() {
            @Override
            public String getStringValue(Collaborator value) {
                return storeKeyProvider.getKey(value);
            }

            @Override
            public Collaborator convertStringValue(String value) {
                return store.findModel(value);
            }
        };

        setStore(store);
        setPropertyEditor(propertyEditor);
    }


    public void cleanup() {
        for (HandlerRegistration handler : handlers) {
            handler.removeHandler();
        }
    }

    private void initListeners() {
//        addKeyListener(new KeyListener() {
//            @Override
//            public void componentKeyDown(ComponentEvent event) {
//                if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
//                    if (mode == TriggerMode.SEARCH && getSearchResults() != null) {
//                        collapse();
//                        fireSearchLoadedEvent();
//                        setTriggerMode(TriggerMode.CLEAR);
//                    }
//                }
//            }
//
//            @Override
//            public void componentKeyUp(ComponentEvent event) {
//                if (mode == TriggerMode.CLEAR && getSearchResults() != null) {
//                    String query = getRawValue();
//                    if (query != null && !query.equals(searchProxy.getLastQueryText())) {
//                        setTriggerMode(TriggerMode.SEARCH);
//                    }
//                }
//            }
//        });

        addSelectionChangedListener(new SelectionChangedListener<Collaborator>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<Collaborator> se) {
                Collaborator collaborator = se.getSelectedItem();

                if (collaborator != null) {
                    // Fire the search item selection event.
                    EventBus.getInstance().fireEvent(new UserSearchResultSelected(tag, collaborator));
                }
            }
        });

        // Since we don't want our custom key provider's string to display after a user selects a search
        // result, reset the raw text field to the cached user query string after a selection is made.
        addListener(Events.Select, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {
                setRawValue(searchProxy.getLastQueryText());
            }
        });
    }

    /*
     * When the trigger is clicked, the last results (or current search term) should drop down
     * and be displayed to user.
     */
    @Override
    protected void onTriggerClick(ComponentEvent ce) {
        super.onTriggerClick(ce);
        // fireEvent(Events.TriggerClick, ce);
        //
        // if (mode == TriggerMode.SEARCH && getSearchResults() != null) {
        // setRawValue(searchProxy.getLastQueryText());
        // getInputEl().focus();
        //
        // collapse();
        // fireSearchLoadedEvent();
        // setTriggerMode(TriggerMode.CLEAR);
        // } else if (mode == TriggerMode.CLEAR) {
        // setRawValue(null);
        // setTriggerMode(TriggerMode.SEARCH);
        //
        // collapse();
        // }

    }

    // private void setTriggerMode(TriggerMode mode) {
    // this.mode = mode;
    // setTriggerStyle(mode.getTriggerStyle());
    //
    // if (isRendered()) {
    //            trigger.dom.setClassName("x-form-trigger " + triggerStyle); //$NON-NLS-1$
    // }
    // }

    // private void fireSearchLoadedEvent() {
    // // Fire the search results load event.
    // EventBus.getInstance().fireEvent(new UserSearchResultSelected(tag, getSearchResults()));
    // }

    /**
     * @return A string of html for the search ComboBox's list results.
     */
    private String buildTemplate() {
        StringBuilder template = new StringBuilder();

        template.append("<tpl for=\".\"><div class=\"search-item\">"); //$NON-NLS-1$

        template.append("<h3>"); //$NON-NLS-1$

        //        template.append("<tpl if=\"is_favorite\">"); //$NON-NLS-1$
        //        template.append("<img src='./images/fav.png'></img> &nbsp;"); //$NON-NLS-1$
        //        template.append("</tpl>"); //$NON-NLS-1$

        template.append("{name}"); //$NON-NLS-1$

        //        template.append("<span><b>"); //$NON-NLS-1$
        // template.append(I18N.DISPLAY.avgRating());
        //        template.append(":</b> {email} "); //$NON-NLS-1$
        // template.append(I18N.DISPLAY.ratingOutOfTotal());
        //        template.append("</span>"); //$NON-NLS-1$

        template.append("</h3>"); //$NON-NLS-1$

        template.append("<h4>"); //$NON-NLS-1$
        template.append("<span>{email}</span>"); //$NON-NLS-1$
        //        template.append("<br />{description}"); //$NON-NLS-1$
        template.append("</h4>"); //$NON-NLS-1$

        template.append("</div></tpl>"); //$NON-NLS-1$

        return template.toString();
    }
    }
