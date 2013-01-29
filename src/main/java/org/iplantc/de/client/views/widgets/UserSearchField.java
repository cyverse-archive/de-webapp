package org.iplantc.de.client.views.widgets;

import java.util.ArrayList;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.models.Collaborator;
import org.iplantc.de.client.utils.UserSearchRpcProxy;

import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
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
 * A ComboBox for the User search in the DE sharing dialog.
 * 
 * @author psarando, jstroot
 * 
 */
public class UserSearchField extends ComboBox<Collaborator> {
    private final UserSearchRpcProxy searchProxy;
    private ArrayList<HandlerRegistration> handlers;

    public UserSearchField() {
        this.searchProxy = new UserSearchRpcProxy();

        initComboBox();
        initListeners();
    }

    private void initComboBox() {
        setWidth(255);
        setItemSelector("div.search-item"); //$NON-NLS-1$
        setTemplate(buildTemplate());
        setTriggerStyle("x-form-search-trigger"); //$NON-NLS-1$
        setEmptyText(I18N.DISPLAY.collabSearchPrompt());
        setMinChars(3);

        // Create a loader with our custom RpcProxy.
        ListLoader<ListLoadResult<Collaborator>> loader = new BaseListLoader<ListLoadResult<Collaborator>>(searchProxy);

        // Create the store
        final ListStore<Collaborator> store = new ListStore<Collaborator>(loader);

        // We need to use a custom key string that will allow the combobox to find the correct model if 2
        // user happen to have the same name, since the combo's SelectionChange event will find
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
        addSelectionChangedListener(new SelectionChangedListener<Collaborator>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<Collaborator> se) {
                Collaborator collaborator = se.getSelectedItem();

                if (collaborator != null) {
                    // Fire the search item selection event.
                    EventBus.getInstance().fireEvent(new UserSearchResultSelected(collaborator));
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

    /**
     * @return A string of html for the search ComboBox's list results.
     */
    private String buildTemplate() {
        StringBuilder template = new StringBuilder();

        template.append("<tpl for=\".\"><div class=\"search-item\">"); //$NON-NLS-1$

        template.append("<h3>"); //$NON-NLS-1$
        template.append("{name}"); //$NON-NLS-1$
        template.append("</h3>"); //$NON-NLS-1$

        template.append("<h4>"); //$NON-NLS-1$
        template.append("<span>{email}</span>"); //$NON-NLS-1$
        template.append("</h4>"); //$NON-NLS-1$

        template.append("</div></tpl>"); //$NON-NLS-1$

        return template.toString();
    }
    }
