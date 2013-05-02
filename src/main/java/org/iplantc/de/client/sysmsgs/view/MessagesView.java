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
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.ResizeContainer;
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
	ResizeContainer expirationContainer;
	
	@UiField
	Label expirationLabel;
	
	@UiField
	HTML messageView;
	
	@UiField(provided = true)
	final ListView<Message, Message> messageList;
	
	@UiField(provided = true)
	final BorderLayoutContainer.BorderLayoutData messageListLayoutData;
	
	private final SimpleContainer basePanel;
	private final CenterLayoutContainer statusPanel;
	private final Status status;
	
	public MessagesView() {
		messageList = new ListView<Message, Message>(makeDefaultStore(), 
				new IdentityValueProvider<Message>(), new MessageSummaryCell());
		messageListLayoutData = new BorderLayoutData();
		binder.createAndBindUi(this);
		basePanel = new SimpleContainer();
		statusPanel = new CenterLayoutContainer();
		status = new Status();
		statusPanel.add(status);
		initWidget(basePanel);
		showNoMessages();
	}
	
	@Override
	public void setPresenter(final Presenter presenter) {
		messageList.setStore(presenter.getMessageStore());
		messageList.setSelectionModel(presenter.getMessageSelectionModel());
	}
	
	@Override
	public void setExpiryText(final String expiryText) {
		expirationLabel.setText(expiryText);
		expirationContainer.forceLayout();
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
	}
	
	@Override
	public void showNoMessages() {
		basePanel.setWidget(statusPanel);
		status.clearStatus(I18N.DISPLAY.noSystemMessages());
	}
	
}
