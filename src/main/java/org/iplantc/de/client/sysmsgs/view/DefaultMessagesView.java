package org.iplantc.de.client.sysmsgs.view;

import org.iplantc.core.resources.client.messages.I18N;
import org.iplantc.de.client.sysmsgs.model.Message;
import org.iplantc.de.client.sysmsgs.view.DefaultMessagesViewResources.Style;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.ListViewSelectionModel;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/**
 * TODO document
 */
public final class DefaultMessagesView extends Composite implements MessagesView {

	interface Binder extends UiBinder<BorderLayoutContainer, DefaultMessagesView> {
	}

    interface ExpiryTemplate extends XTemplates {
        @XTemplate("<div class='{style.expiry}'>{expiryText}</div>")
        SafeHtml make(String expiryText, Style style);
    }

	private static final Binder binder = GWT.create(Binder.class);
    private static final ExpiryTemplate EXPIRY_FACTORY = GWT.create(ExpiryTemplate.class);
	
	private static final ListStore<Message> makeDefaultStore() {
		return new ListStore<Message>(new ModelKeyProvider<Message>() {
				@Override
				public String getKey(final Message item) {
					return "";
				}});
	}
	
	@UiField
    DefaultMessagesViewResources res;

    @UiField
	BorderLayoutContainer messagesPanel;
	
    @UiField
	HTML messageView;
	
    @UiField
    HTML expiryView;

	@UiField(provided = true)
    final BorderLayoutData expireLayoutData;

    @UiField(provided = true)
	final ListView<Message, Message> messageList;
	
	private final SimpleContainer basePanel;
	private final CenterLayoutContainer statusPanel;
	private final Status status;
	private final MessageSummaryCell summaryCell;
		
	public DefaultMessagesView() {
        expireLayoutData = new BorderLayoutData();
        expireLayoutData.setMinSize(2);
		summaryCell = new MessageSummaryCell(); 
		messageList = new ListView<Message, Message>(makeDefaultStore(), 
				new IdentityValueProvider<Message>(), summaryCell);
		binder.createAndBindUi(this);
        res.style().ensureInjected();
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
        expiryView.setHTML(EXPIRY_FACTORY.make(expiryText, res.style()));
        if (messagesPanel.isAttached()) {
            layoutMessagesPanel();
        }
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
        layoutMessagesPanel();
	}
	
	@Override
	public void showNoMessages() {
		basePanel.setWidget(statusPanel);
		status.clearStatus(I18N.DISPLAY.noSystemMessages());
	}

    private void layoutMessagesPanel() {
        Scheduler.get().scheduleFinally(new ScheduledCommand() {
            @Override
            public void execute() {
                final XElement lblConElmt = XElement.as(expiryView.getElement());
                final XElement lblElmt = lblConElmt.child("." + res.style().expiry());
                expireLayoutData.setSize(lblElmt.getBounds().getHeight());
                basePanel.forceLayout();
            }
        });
    }

}
