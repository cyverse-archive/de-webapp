package org.iplantc.de.client.sysmsgs.view;

import org.iplantc.de.client.sysmsgs.model.Message;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ListViewSelectionModel;

/**
 * This interface describes the needed functionality of something that displays a list of system
 * messages. Implementations should allow the selection of a single message.
 */
public interface MessagesView extends IsWidget {

    /**
     * The presenter of the view needs to implement the following methods.
     */
	public interface Presenter {
		
        /**
         * This method should return a list store of messages to be displayed.
         */
		ListStore<Message> getMessageStore();

        /**
         * This method is the called when the user attempts to dismiss a message.
         * 
         * @param message the message the user is attempting to delete
         */
		void handleDismissMessageEvent(Message message);

	}
	
    /**
     * Retrieves the selection model used to for selecting messages.
     * 
     * @return the selection model
     */
	ListViewSelectionModel<Message> getMessageSelectionModel();
	
    /**
     * Attaches the presenter to the message view.
     * 
     * @param presenter the presenter
     */
	public void setPresenter(Presenter presenter);
		
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
     * Indicate to the user that the system messages are currently being loaded.
     */
	public void showLoading();
	
    /**
     * Show the messages to the user.
     */
	public void showMessages();
	
    /**
     * Indicate to the user that their are no active system messages.
     */
	public void showNoMessages();
	
}
