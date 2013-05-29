package org.iplantc.de.client.sysmsgs.view;

import org.iplantc.core.resources.client.messages.I18N;
import org.iplantc.de.client.sysmsgs.model.Message;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.IdentityValueProvider;
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
 * This is the default implementation of the messages view.
 */
public final class DefaultMessagesView extends Composite implements MessagesView {

	interface Binder extends UiBinder<BorderLayoutContainer, DefaultMessagesView> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
    private static ListStore<Message> makeDefaultStore() {
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

    /**
     * the constructor
     */
	public DefaultMessagesView() {
        expireLayoutData = new BorderLayoutData();
        expireLayoutData.setMinSize(2);
		summaryCell = new MessageSummaryCell(); 
        messageList = new ListView<Message, Message>(makeDefaultStore(), new IdentityValueProvider<Message>(), new SummaryListAppearance());
        messageList.setCell(summaryCell);
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
	
    /**
     * @see MessagesView#getMessageSelectionModel()
     */
	@Override
	public ListViewSelectionModel<Message> getMessageSelectionModel() {
		return messageList.getSelectionModel();
	}
	
    /**
     * @see MessagesView#setPresenter(MessagesView.Presenter)
     */
	@Override
	public void setPresenter(final Presenter presenter) {
		messageList.setStore(presenter.getMessageStore());
		summaryCell.addHandler(new DismissMessageEvent.Handler() {
			@Override
			public void handleDismiss(final DismissMessageEvent event) {
					presenter.handleDismissMessageEvent(event.getMessage());
				}}, DismissMessageEvent.TYPE);
	}
	
    /**
     * @see MessagesView#setExpiryMessage(String)
     */
	@Override
    public void setExpiryMessage(final String expiryMsg) {
        expiryView.setHTML(makeExpiryHtml(expiryMsg));
        if (messagesPanel.isAttached()) {
            layoutMessagesPanel();
        }
	}

    /**
     * @see MessagesView#setMessageBody(SafeHtml)
     */
	@Override
	public void setMessageBody(final SafeHtml msgBody) {
		messageView.setHTML(msgBody);
	}

    /**
     * @see MessagesView#showLoading()
     */
	@Override
	public void showLoading() {
		basePanel.setWidget(statusPanel);
		status.clearStatus("");
		status.setBusy("");
	}
	
    /**
     * @see MessagesView#showMessages()
     */
	@Override
	public void showMessages() {
		basePanel.setWidget(messagesPanel);
        layoutMessagesPanel();
	}
	
    /**
     * @see MessagesView#showNoMessages()
     */
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

    private SafeHtml makeExpiryHtml(final String expiryMsg) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("<div class='" + res.style().expiry() + "'>");
        builder.appendEscaped(expiryMsg);
        builder.appendHtmlConstant("</div>");
        return builder.toSafeHtml();
    }

}
