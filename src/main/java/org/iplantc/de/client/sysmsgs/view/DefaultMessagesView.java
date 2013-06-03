package org.iplantc.de.client.sysmsgs.view;

import java.util.Date;
import java.util.List;

import org.iplantc.de.client.sysmsgs.events.DismissEvent;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.ListViewSelectionModel;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.ResizeContainer;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

/**
 * This is the default implementation of the messages view.
 * 
 * @param <M> the type of message to view
 */
final class DefaultMessagesView<M> extends Composite implements MessagesView<M> {

    interface Binder extends UiBinder<Widget, DefaultMessagesView<?>> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
    private static final <M> ListView<M, M> makeMessageList(final Cell<M> sumCell, final MessageProperties<M> msgProps) {
        final ListStore<M> store = new ListStore<M>(msgProps.id());
        store.addSortInfo(new StoreSortInfo<M>(msgProps.activationTime(), SortDir.DESC));
        final IdentityValueProvider<M> prov = new IdentityValueProvider<M>();
        final SummaryListAppearance<M> appr = new SummaryListAppearance<M>();
        final ListView<M, M> list = new ListView<M, M>(store, prov, appr);
        list.setCell(sumCell);
        list.setSelectionModel(new SelectionModel<M>());
        return list;
    }

	@UiField
    DefaultMessagesViewResources res;

    @UiField
    CardLayoutContainer layout;

    @UiField
    Widget loadingPanel;

    @UiField
    Widget noMessagesPanel;

    @UiField
    ResizeContainer messagesPanel;

    @UiField
	HTML messageView;
	
    @UiField
    Label expiryView;

    @UiField(provided = true)
    final ListView<M, M> messageList;

    private final MessageSummaryCell<M> summaryCell;
    private final Presenter<M> presenter;

    /**
     * the constructor
     * 
     * @param presenter the corresponding presenter
     * @param messageProperties the message properties provider
     * @param activationRenderer the renderer for the activation time
     */
    DefaultMessagesView(final Presenter<M> presenter, final MessageProperties<M> messageProperties, final Renderer<Date> activationRenderer) {
        this.presenter = presenter;
        summaryCell = new MessageSummaryCell<M>(messageProperties, activationRenderer);
        messageList = makeMessageList(summaryCell, messageProperties);
        initWidget(binder.createAndBindUi(this));
        res.style().ensureInjected();
        initHandlers();
    }

    private void initHandlers() {
        messageList.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<M>() {
            @Override
            public void onSelectionChanged(final SelectionChangedEvent<M> event) {
                handleMessageSelection(event);
            }
        });
        summaryCell.addHandler(new DismissEvent.Handler<M>() {
            @Override
            public void handleDismiss(final DismissEvent<M> event) {
                presenter.handleDismissMessage(event.getDismissed());
            }
        }, DismissEvent.TYPE);
    }

    /**
     * @see MessagesView#getMessageStore()
     */
    @Override
    public ListStore<M> getMessageStore() {
        return messageList.getStore();
    }

    /**
     * @see MessagesView#getSelectionModel()
     */
    @Override
    public ListViewSelectionModel<M> getSelectionModel() {
        return messageList.getSelectionModel();
    }

    /**
     * @see MessagesView#setExpiryMessage(String)
     */
	@Override
    public void setExpiryMessage(final String expiryMsg) {
        expiryView.setText(expiryMsg);
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
        layout.setActiveWidget(loadingPanel);
    }

    /**
     * @see MessagesView#showMessages()
     */
    @Override
    public void showMessages() {
        layout.setActiveWidget(messagesPanel);
    }

    /**
     * @see MessagesView#showNoMessages()
     */
    @Override
    public void showNoMessages() {
        layout.setActiveWidget(noMessagesPanel);
    }

    /*
     * This method is overridden to force the message panel to be laid out a second time in case
     * the expiry message wrapped or unwrapped.
     */
    /**
     * @see Composite#onResize(int, int)
     */
    @Override
    protected void onResize(final int width, final int height) {
        super.onResize(width, height);
        if (layout.getActiveWidget() == messagesPanel) {
            Scheduler.get().scheduleFinally(new ScheduledCommand() {
                @Override
                public void execute() {
                    messagesPanel.forceLayout();
                }
            });
        }
    }

    private void handleMessageSelection(final SelectionChangedEvent<M> event) {
        final List<M> selection = event.getSelection();
        if (!selection.isEmpty()) {
            presenter.handleSelectMessage(selection.get(0));
        }
    }

}

