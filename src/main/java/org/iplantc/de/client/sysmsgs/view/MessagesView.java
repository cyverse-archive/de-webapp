package org.iplantc.de.client.sysmsgs.view;

import java.util.Date;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.ListViewSelectionModel;

/**
 * This interface describes the needed functionality of something that displays a list of system
 * messages. Implementations should allow the selection of a single message.
 * 
 * @param <M> the message type
 */
public interface MessagesView<M> extends IsWidget {

    /**
     * The properties of the messages used by the view
     */
    interface MessageProperties<M> extends PropertyAccess<M> {
        /**
         * the message id provider for providing index keys
         */
        ModelKeyProvider<M> id();

        /**
         * the message type provider
         */
        ValueProvider<M, String> type();

        /**
         * the activation time provider
         */
        ValueProvider<M, Date> activationTime();

        /**
         * the seen provider
         */
        ValueProvider<M, Boolean> seen();

        /**
         * the dismissible provider
         */
        ValueProvider<M, Boolean> dismissible();
    }

    /**
     * The interface a presenter of a message view must implement
     * 
     * @param <M> the type of message to present
     */
    interface Presenter<M> {
        /**
         * handle a user request to dismiss a message
         * 
         * @param message the message to dismiss
         */
        void handleDismissMessage(M message);

        /**
         * handle a user request to select a message
         * 
         * @param message the message to select
         */
        void handleSelectMessage(M message);
    }

    /**
     * A factory for making MessageView objects
     * 
     * @param <M> the type of message to view
     */
    interface Factory<M> {
        /**
         * Initializes the widget
         * 
         * @param presetner the presenter for this view
         * @param messageProperties the message properties provider
         * @param sortInfo the sorting information to use by the summary list
         * @param selectionMode the selection mode to use by the summary list
         * @param activationRenderer the renderer used to render the activation time
         */
        MessagesView<M> make(Presenter<M> presenter, MessageProperties<M> messageProperties, StoreSortInfo<M> sortInfo, SelectionMode selectionMode, Renderer<Date> activationRenderer);
    }

    /**
     * returns the message store backing the view
     * 
     * @return the message store
     */
    ListStore<M> getMessageStore();

    /**
     * returns the selection model backing the view
     * 
     * @return the selection model
     */
    ListViewSelectionModel<M> getSelectionModel();

    /**
     * Provides the expiration message to display for the selected system message
     * 
     * @param expiryMsg the expiration message
     */
    public void setExpiryMessage(String expiryMsg);

    /**
     * Provides the body to display for the selected message
     * 
     * @param msgBody the message body
     */
	public void setMessageBody(SafeHtml msgBody);

    /**
     * Tells the view to show the loading panel.
     */
    void showLoading();

    /**
     * Tells the view to show the messages panel.
     */
    void showMessages();

    /**
     * Tells the view to show the no messages panel.
     */
    void showNoMessages();

}
