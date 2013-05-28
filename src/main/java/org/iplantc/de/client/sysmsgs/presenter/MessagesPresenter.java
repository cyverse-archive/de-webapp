package org.iplantc.de.client.sysmsgs.presenter;

import java.util.List;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.sysmsgs.cache.SystemMessageCache;
import org.iplantc.de.client.sysmsgs.events.MessagesUpdatedEvent;
import org.iplantc.de.client.sysmsgs.model.Message;
import org.iplantc.de.client.sysmsgs.view.MessagesView;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
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
public final class MessagesPresenter implements MessagesView.Presenter {

	private final ListStore<Message> store;
	private final MessagesView view;
	
    public MessagesPresenter() {
        store = new ListStore<Message>(MessageProperties.INSTANCE.id());
        view = GWT.create(MessagesView.class);
		initStore();
		initSelectionModel();
        view.setPresenter(this);
	}
	
	private void initStore() {
		store.addSortInfo(new StoreSortInfo<Message>(MessageProperties.INSTANCE.creationTime(), 
				SortDir.DESC));
		updateStoreAsync();
		EventBus.getInstance().addHandler(MessagesUpdatedEvent.TYPE, 
				new MessagesUpdatedEvent.Handler() {
					@Override
					public void onUpdate(final MessagesUpdatedEvent event) {
						updateStoreAsync();
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
	public void handleDismissMessageEvent(final Message message) {
		// TODO mask view
		if (message == null) {
			return;
		}
		SystemMessageCache.instance().dismissMessage(message, new Callback<Void, Throwable>() {
			@Override
			public void onFailure(final Throwable reason) {
				// FIXME handle failure
				Window.alert(reason.getMessage());
			}
			@Override
			public void onSuccess(Void unused) {
				removeMessage(message);
				// TODO unmask  view
			}});
	}
	
	public void go(final AcceptsOneWidget container) {
        SystemMessageCache.instance().startSyncing();
		container.setWidget(view);
		view.showLoading();
	}
	
    public void stop() {
        SystemMessageCache.instance().stopSyncing();
    }

	private void selectMessage(final Message msg) {
		view.getMessageSelectionModel().select(false, msg);
		final SafeHtmlBuilder bodyBuilder = new SafeHtmlBuilder();
		bodyBuilder.appendHtmlConstant(msg.getBody());
		view.setMessageBody(bodyBuilder.toSafeHtml());
		view.setExpiryText(msg.getDeactivationTime().toString());
	}

	private void updateStoreAsync() {
		SystemMessageCache.instance().load(null, 
				new Callback<ListLoadResult<Message>, Throwable>() {
					@Override
					public void onFailure(final Throwable reason) {
						// TODO implement
						Window.alert("Failed to retrieve messages");
					}
					@Override
					public void onSuccess(final ListLoadResult<Message> result) {
						updateStore(result.getData());
					}});
	}
	
	private void updateStore(final List<Message> updatedMessages) {
		final Message curSelect = view.getMessageSelectionModel().getSelectedItem();
		store.replaceAll(updatedMessages);
		if (curSelect != null && store.findModel(curSelect) == null) {
			store.add(curSelect);
		}
		if (store.size() > 0) {
			if (curSelect != null) {
				showMessageSelected(store.indexOf(curSelect));
			} else {
				showMessageSelected(0);
			}
		} else {
			view.showNoMessages();
		}
	}

	private void removeMessage(final Message message) {
		store.remove(message);
		if (store.size() <= 0) {
			view.showNoMessages();
		}
	}
	
	private void showMessageSelected(final int index) {
		view.getMessageSelectionModel().deselectAll();
		view.getMessageSelectionModel().select(index, false);
		view.showMessages();
	}
	
}
