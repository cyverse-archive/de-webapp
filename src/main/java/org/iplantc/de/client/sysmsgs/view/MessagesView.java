package org.iplantc.de.client.sysmsgs.view;

import org.iplantc.de.client.sysmsgs.model.Message;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ListViewSelectionModel;

/**
 * TODO document
 */
public interface MessagesView extends IsWidget {

	public interface Presenter {
		
		ListStore<Message> getMessageStore();

		void handleDismissMessageEvent(Message message);

	}
	
	ListViewSelectionModel<Message> getMessageSelectionModel();
	
	public void setPresenter(Presenter presenter);
		
    public void setExpiryText(String expiry);

	public void setMessageBody(SafeHtml msgBody);
	
	public void showLoading();
	
	public void showMessages();
	
	public void showNoMessages();
	
}
