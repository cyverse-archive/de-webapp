package org.iplantc.de.client.sysmsgs.presenter;

import java.util.List;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.sysmsgs.cache.SystemMessageCache;
import org.iplantc.de.client.sysmsgs.events.NewSystemMessagesEvent;
import org.iplantc.de.client.sysmsgs.model.MessageDTO;
import org.iplantc.de.client.sysmsgs.view.DisplaysSystemMessages;

import com.google.gwt.core.client.Callback;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.widget.core.client.ListViewSelectionModel;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

/**
 * The system messages presenter.
 */
public final class SystemMessagePresenter implements DisplaysSystemMessages.Presenter {

	private final ListStore<MessageDTO> messageListViewModel;
	private final DisplaysSystemMessages view;
	private final ListViewSelectionModel<MessageDTO> messageSelectionModel;
	
	public SystemMessagePresenter(final DisplaysSystemMessages view) {
		this.view = view;
		messageListViewModel = new ListStore<MessageDTO>(SystemMessageProperties.INSTANCE.id());
		messageSelectionModel = new ListViewSelectionModel<MessageDTO>();
		initMessageListViewModel();
		initMessageSelectionModel();
        view.setPresenter(this);
	}
	
	private void initMessageListViewModel() {
		messageListViewModel.addSortInfo(new StoreSortInfo<MessageDTO>(
				SystemMessageProperties.INSTANCE.activationTime(), SortDir.DESC));
		SystemMessageCache.instance().startSyncing();
		EventBus.getInstance().addHandler(NewSystemMessagesEvent.TYPE, 
				new NewSystemMessagesEvent.Handler() {
					@Override
					public void onNewMessage(final NewSystemMessagesEvent event) {
						updateMessageListViewModel();
					}});
	}
	
	private void initMessageSelectionModel() {
		messageSelectionModel.setSelectionMode(Style.SelectionMode.SINGLE);
		messageSelectionModel.addSelectionChangedHandler(
				new SelectionChangedHandler<MessageDTO>() {
					@Override
					public void onSelectionChanged(final SelectionChangedEvent<MessageDTO> event) 
							{
						final List<MessageDTO> selection = event.getSelection();
						if (!selection.isEmpty()) {
							selectMessage(selection.get(0));
						}}});
	}
	
	// TODO invert this
	@Override
	public ListViewSelectionModel<MessageDTO> getMessageSelectionModel() {
		return messageSelectionModel;
	}

	// TODO invert this
	@Override
	public ListStore<MessageDTO> getMessageStore() {
		return messageListViewModel;
	}
		
	@Override
	public void handleDeleteButtonClick() {
		final MessageDTO selectedMsg = messageSelectionModel.getSelectedItem();
		if (selectedMsg != null) {
			final int msgIdx = messageListViewModel.indexOf(selectedMsg);
			final int newSelectedIdx = (msgIdx + 1 == messageListViewModel.size()) ? msgIdx - 1 : msgIdx;
			messageListViewModel.remove(msgIdx);
			if (messageListViewModel.size() > 0) {
				messageSelectionModel.select(false, messageListViewModel.get(newSelectedIdx));
			}
		}
	}
	
	public void go(final AcceptsOneWidget container) {
		container.setWidget(view);
		view.showNoMessages(false);
	}
	
	private void selectMessage(final MessageDTO msg) {
		messageSelectionModel.select(false, msg);
		final SafeHtmlBuilder bodyBuilder = new SafeHtmlBuilder();
		bodyBuilder.appendHtmlConstant(msg.getBody());
		view.setMessageBody(bodyBuilder.toSafeHtml());
		view.setExpiryText(msg.getDeactivationTime().toString());
	}

	private void updateMessageListViewModel() {
		SystemMessageCache.instance().load(null, 
				new Callback<ListLoadResult<MessageDTO>, Throwable>() {
					@Override
					public void onFailure(final Throwable reason) {
						// TODO implement
					}
		
					@Override
					public void onSuccess(final ListLoadResult<MessageDTO> result) {
						messageListViewModel.replaceAll(result.getData());
					}});
	}
	
}
