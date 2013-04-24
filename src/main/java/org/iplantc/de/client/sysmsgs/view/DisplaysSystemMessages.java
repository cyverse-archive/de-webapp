package org.iplantc.de.client.sysmsgs.view;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ListViewSelectionModel;

import org.iplantc.de.client.sysmsgs.model.SystemMessage;

/**
 * TODO document
 */
public interface DisplaysSystemMessages extends IsWidget {

	public interface Presenter {
		
		ListViewSelectionModel<SystemMessage> getMessageSelectionModel();
		
		ListStore<SystemMessage> getMessageStore();
		
		void handleDeleteButtonClick();
		
	}
	
	public void setPresenter(Presenter presenter);
		
	public void setExpiryText(String expiryText);

	public void setMessageBody(SafeHtml msgBody);
	
	public void showNoMessages(boolean show);
}
