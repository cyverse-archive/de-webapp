package org.iplantc.de.client.sysmsgs.presenter;

import java.util.Date;
import java.util.List;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.sysmsgs.cache.SystemMessageCache;
import org.iplantc.de.client.sysmsgs.events.MessagesUpdatedEvent;
import org.iplantc.de.client.sysmsgs.model.Message;
import org.iplantc.de.client.sysmsgs.view.MessagesView;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.loader.ListLoadResult;

/**
 * The system messages presenter.
 */
public final class MessagesPresenter implements MessagesView.Presenter<Message> {

    interface MessageProperties extends MessagesView.MessageProperties<Message> {
    }

    private static final MessageProperties MSG_PROPS = GWT.create(MessageProperties.class);
    private static final MessagesView.Factory<Message> VIEW_FACTORY = GWT.create(MessagesView.Factory.class);
    
    private static boolean withinPreviousWeek(final Date successor, final Date predecessor) {
        if (predecessor.after(successor)) {
            return false;
        }
        return CalendarUtil.getDaysBetween(predecessor, successor) < 7;
    }

    private final MessagesView<Message> view;

    /**
     * the constructor
     */
    public MessagesPresenter() {
        final StoreSortInfo<Message> sort = new StoreSortInfo<Message>(MSG_PROPS.activationTime(), SortDir.DESC);
        view = VIEW_FACTORY.make(this, MSG_PROPS, sort, SelectionMode.SINGLE);
        initStore();
	}
	
	private void initStore() {
		updateStoreAsync();
        EventBus.getInstance().addHandler(MessagesUpdatedEvent.TYPE, new MessagesUpdatedEvent.Handler() {
            @Override
            public void onUpdate(final MessagesUpdatedEvent event) {
                updateStoreAsync();
            }
        });
		}
	
    /**
     * @see MessageView.Presenter<T>#dismissMessage(T)
     */
    @Override
    public void handleDismissMessage(final Message message) {
		// TODO mask view
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
	
    /**
     * @see MessageView.Presenter<T>#selectMessage(T)
     */
    @Override
    public void handleSelectMessage(final Message msg) {
        view.getSelectionModel().select(false, msg);
        final SafeHtmlBuilder bodyBuilder = new SafeHtmlBuilder();
        bodyBuilder.appendHtmlConstant(msg.getBody());
        view.setMessageBody(bodyBuilder.toSafeHtml());
        final DateTimeFormat expiryFmt = DateTimeFormat.getFormat("dd MMMM yyyy");
        final String expiryStr = expiryFmt.format(msg.getDeactivationTime());
        view.setExpiryMessage(I18N.DISPLAY.expirationMessage(expiryStr));
    }

    /**
     * @see MessageView.Presenter#formatActivationTime(Date)
     */
    @Override
    public String formatActivationTime(final Date activationTime) {
        final Date now = new Date();
        String actMsg = "";
        if (CalendarUtil.isSameDate(now, activationTime)) {
            actMsg = I18N.DISPLAY.today();
        } else if (withinPreviousWeek(now, activationTime)) {
            actMsg = DateTimeFormat.getFormat("cccc").format(activationTime);
        } else {
            actMsg = DateTimeFormat.getFormat("dd MMMM yyyy").format(activationTime);
        }
        return actMsg;
    }

    /**
     * Starts the presenter and attaches the view to the provided container. This also starts the
     * message caching.
     * 
     * @param container The container that will hold the view.
     */
	public void go(final AcceptsOneWidget container) {
        if (container == null) {
            stop();
        } else {
            SystemMessageCache.instance().startSyncing();
            container.setWidget(view);
            view.showLoading();
        }
	}
	
    /**
     * This should be called when the container holding the view has been closed. It stops the
     * message caching.
     */
    public void stop() {
        SystemMessageCache.instance().stopSyncing();
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
        final Message curSelect = view.getSelectionModel().getSelectedItem();
        final ListStore<Message> store = view.getMessageStore();
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
        view.getMessageStore().remove(message);
        if (view.getMessageStore().size() <= 0) {
            view.showNoMessages();
		}
	}
	
	private void showMessageSelected(final int index) {
        view.showMessages();
        view.getSelectionModel().select(index, false);
	}

}
