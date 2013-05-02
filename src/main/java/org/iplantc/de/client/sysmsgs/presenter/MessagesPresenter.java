package org.iplantc.de.client.sysmsgs.presenter;

import java.util.List;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.sysmsgs.cache.SystemMessageCache;
import org.iplantc.de.client.sysmsgs.events.NewMessagesEvent;
import org.iplantc.de.client.sysmsgs.model.Message;
import org.iplantc.de.client.sysmsgs.view.DisplaysMessages;

import com.google.gwt.core.client.Callback;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

/**
 * The system messages presenter.
 */
public final class MessagesPresenter implements DisplaysMessages.Presenter {

	private final ListStore<Message> store;
	private final DisplaysMessages view;
	
	public MessagesPresenter(final DisplaysMessages view) {
		this.view = view;
		store = new ListStore<Message>(MessageProperties.INSTANCE.id());
		initStore();
		initSelectionModel();
        view.setPresenter(this);
	}
	
	private void initStore() {
		store.addSortInfo(new StoreSortInfo<Message>(MessageProperties.INSTANCE.creationTime(), 
				SortDir.DESC));
		updateStore();
		SystemMessageCache.instance().startSyncing();
		EventBus.getInstance().addHandler(NewMessagesEvent.TYPE, 
				new NewMessagesEvent.Handler() {
					@Override
					public void onNewMessage(final NewMessagesEvent event) {
						updateStore();
					}});
		}
	
	private void initSelectionModel() {
		view.getMessageSelectionModel().setSelectionMode(Style.SelectionMode.SINGLE);
		view.getMessageSelectionModel().addSelectionChangedHandler(
				new SelectionChangedHandler<Message>() {
					@Override
					public void onSelectionChanged(final SelectionChangedEvent<Message> event) 
							{
						final List<Message> selection = event.getSelection();
						if (!selection.isEmpty()) {
							selectMessage(selection.get(0));
						}}});
	}

	@Override
	public ListStore<Message> getMessageStore() {
		return store;
	}
		
	@Override
	public void handleDeleteButtonClick() {
		final Message selectedMsg = view.getMessageSelectionModel().getSelectedItem();
		if (selectedMsg != null) {
			final int msgIdx = store.indexOf(selectedMsg);
			final int newSelectedIdx = (msgIdx + 1 == store.size()) ? msgIdx - 1 : msgIdx;
			store.remove(msgIdx);
			if (store.size() > 0) {
				view.getMessageSelectionModel().select(false, store.get(newSelectedIdx));
			}
		}
	}
	
	public void go(final AcceptsOneWidget container) {
		container.setWidget(view);
		view.showLoading();
	}
	
	private void selectMessage(final Message msg) {
		view.getMessageSelectionModel().select(false, msg);
		final SafeHtmlBuilder bodyBuilder = new SafeHtmlBuilder();
		bodyBuilder.appendHtmlConstant(msg.getBody());
		view.setMessageBody(bodyBuilder.toSafeHtml());
		view.setExpiryText(msg.getDeactivationTime().toString());
	}

	private void updateStore() {
		SystemMessageCache.instance().load(null, 
				new Callback<ListLoadResult<Message>, Throwable>() {
					@Override
					public void onFailure(final Throwable reason) {
						// TODO implement
					}
					@Override
					public void onSuccess(final ListLoadResult<Message> result) {
						store.replaceAll(result.getData());
						if (store.size() > 0) {
							view.showMessages();
						} else {
							view.showNoMessages();
						}
					}});
	}
	
}
