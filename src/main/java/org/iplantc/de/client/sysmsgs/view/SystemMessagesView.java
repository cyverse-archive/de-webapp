package org.iplantc.de.client.sysmsgs.view;

import org.iplantc.core.resources.client.messages.I18N;
import org.iplantc.de.client.sysmsgs.model.Message;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.ResizeContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

/**
 * TODO document
 */
public final class SystemMessagesView extends Composite implements DisplaysSystemMessages {

	interface Binder extends UiBinder<BorderLayoutContainer, SystemMessagesView> {
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
	TextButton deleteButton;
	
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
	
	private Presenter presenter = null;
	
	public SystemMessagesView() {
		messageList = new ListView<Message, Message>(makeDefaultStore(), 
				new IdentityValueProvider<Message>(), new SystemMessageSummaryCell());
		messageListLayoutData = new BorderLayoutData();
		binder.createAndBindUi(this);
		basePanel = new SimpleContainer();
		statusPanel = new CenterLayoutContainer();
		status = new Status();
		statusPanel.add(status);
		deleteButton.setText("Delete");
		initWidget(basePanel);
		showNoMessages();
		addAttachHandler(new AttachEvent.Handler() {
			@Override
			public void onAttachOrDetach(final AttachEvent event) {
				layout();
			}});
	}
	
	@Override
	public void setPresenter(final Presenter presenter) {
		this.presenter = presenter;
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
	
	@UiHandler("deleteButton")
	void handleDeleteButtonClick(final SelectEvent unused) {
		if (presenter != null) {
			presenter.handleDeleteButtonClick();
		}
	}

	private void layout() {
		if (messagesPanel.isAttached()) {
			layoutMessageList();
		}
	}
	
	private void layoutMessageList() {
		int maxWidth = 0;
		for (int idx = 0; idx < messageList.getItemCount(); idx++) {
			final XElement msgElmt = messageList.getElement(idx);
			final int typeWid = msgElmt.child(".type").getTextWidth();
			final int startWid = msgElmt.child(".startTime").getTextWidth();
			final int elmtWid = msgElmt.getMargins(Side.LEFT, Side.RIGHT) 
					+ msgElmt.getFrameWidth(Side.LEFT, Side.RIGHT) + Math.max(typeWid, startWid);
			maxWidth = Math.max(maxWidth, elmtWid);
		}
		maxWidth += messageList.getElement().getMargins(Side.LEFT, Side.RIGHT) 
				+ messageList.getElement().getFrameWidth(Side.LEFT, Side.RIGHT);
		messageListLayoutData.setMinSize(maxWidth);
		messageListLayoutData.setMaxSize(maxWidth);
		messageListLayoutData.setSize(maxWidth);			
	}
	
}
