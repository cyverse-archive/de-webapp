package org.iplantc.de.client.sysmsgs.view;

import org.iplantc.core.resources.client.messages.I18N;
import org.iplantc.de.client.sysmsgs.model.Message;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.ListViewSelectionModel;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/**
 * TODO document
 */
public final class MessagesView extends Composite implements DisplaysMessages {

	interface Binder extends UiBinder<BorderLayoutContainer, MessagesView> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	private static final ListStore<Message> makeDefaultStore() {
		return new ListStore<Message>(new ModelKeyProvider<Message>() {
				@Override
				public String getKey(final Message item) {
					return "";
				}});
	}
	
	@UiField
	BorderLayoutContainer messagesPanel;
	
	@UiField
	Label expirationLabel;
	
	@UiField
	HTML messageView;
	
	@UiField(provided = true)
	final ListView<Message, Message> messageList;
	
	private final SimpleContainer basePanel;
	private final CenterLayoutContainer statusPanel;
	private final Status status;
	private final MessageSummaryCell summaryCell;
		
	public MessagesView() {
		summaryCell = new MessageSummaryCell(); 
		messageList = new ListView<Message, Message>(makeDefaultStore(), 
				new IdentityValueProvider<Message>(), summaryCell);
		binder.createAndBindUi(this);
		basePanel = new SimpleContainer();
		statusPanel = new CenterLayoutContainer();
		status = new Status();
		statusPanel.add(status);
		initWidget(basePanel);
		messageList.setSelectionModel(new SelectionModel());
		showNoMessages();
	}
	
	@Override
	public ListViewSelectionModel<Message> getMessageSelectionModel() {
		return messageList.getSelectionModel();
	}
	
	@Override
	public void setPresenter(final Presenter presenter) {
		messageList.setStore(presenter.getMessageStore());
		summaryCell.addHandler(new DismissMessageEvent.Handler() {
			@Override
			public void handleDismiss(final DismissMessageEvent event) {
					presenter.handleDismissMessageEvent(event.getMessage());
				}}, DismissMessageEvent.TYPE);
	}
	
	@Override
	public void setExpiryText(final String expiryText) {
		expirationLabel.setText(expiryText);
	}

	@Override
	public void setMessageBody(final SafeHtml msgBody) {
		messageView.setHTML(msgBody);
	}

	@Override
	public void showLoading() {
		basePanel.setWidget(statusPanel);
		status.clearStatus("");
		status.setBusy("");
	}
	
	@Override
	public void showMessages() {
		basePanel.setWidget(messagesPanel);
		basePanel.forceLayout();
	}
	
	@Override
	public void showNoMessages() {
		basePanel.setWidget(statusPanel);
		status.clearStatus(I18N.DISPLAY.noSystemMessages());
	}
	
}
