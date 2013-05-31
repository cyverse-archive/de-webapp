package org.iplantc.de.client.sysmsgs.view;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.widget.core.client.ListView;

/**
 * This interface describes the needed functionality of something that displays a list of system
 * messages. Implementations should allow the selection of a single message.
 * 
 * @param <M> the message type
 */
public interface MessagesView<M> extends IsWidget {

    /**
     * Initializes the widget
     * 
     * @param summariesView the summaries list view to attach
     */
    void init(ListView<M, M> summariesView);

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
