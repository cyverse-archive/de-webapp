package org.iplantc.de.client.sysmsgs.presenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.iplantc.de.client.sysmsgs.model.SystemMessage;
import org.iplantc.de.client.sysmsgs.view.DisplaysSystemMessages;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.widget.core.client.ListViewSelectionModel;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

/**
 * The system messages presenter.
 */
public final class SystemMessagePresenter implements DisplaysSystemMessages.Presenter {

	static final String crap = "2013-04-22 16:07:09.495 java[15630:407] [Java CocoaComponent compatibility mode]: Enabled"
			+ "2013-04-22 16:07:09.495 java[15630:407] [Java CocoaComponent compatibility mode]: Setting timeout for SWT to 0.100000"
			+ "[WARN] Server class 'com.google.gwt.junit.server.JUnitHostImpl' could not be found in the web app, but was found on the system classpath"
			+ "[WARN] Adding classpath entry 'file:/Users/tedgin/eclipse/plugins/com.google.gwt.eclipse.sdkbundle_2.5.0.v201212122042-rel-r42/gwt-2.5.0/gwt-user.jar' to the web app classpath for this session"
			+ "For additional info see: file:/Users/tedgin/eclipse/plugins/com.google.gwt.eclipse.sdkbundle_2.5.0.v201212122042-rel-r42/gwt-2.5.0/doc/helpInfo/webAppClassPath.html"
			+ "[WARN] Server class 'junit.framework.AssertionFailedError' could not be found in the web app, but was found on the system classpath"
			+ "[WARN] Adding classpath entry 'file:/Users/tedgin/.m2/repository/junit/junit/4.5/junit-4.5.jar' to the web app classpath for this session"
			+ "For additional info see: file:/Users/tedgin/eclipse/plugins/com.google.gwt.eclipse.sdkbundle_2.5.0.v201212122042-rel-r42/gwt-2.5.0/doc/helpInfo/webAppClassPath.html";


	private final ListStore<SystemMessage> messageStore;
	private final DisplaysSystemMessages view;
	private final ListViewSelectionModel<SystemMessage> messageSelectionModel;
		
	public SystemMessagePresenter(final DisplaysSystemMessages view) {
		this.view = view;
		this.messageStore = new ListStore<SystemMessage>(SystemMessageProperties.instance.id());
		final Store.StoreSortInfo<SystemMessage> sortInfo = new Store.StoreSortInfo<SystemMessage>(
				SystemMessageProperties.instance.startTime(), SortDir.DESC);
		messageStore.addSortInfo(sortInfo);
		this.messageSelectionModel = new ListViewSelectionModel<SystemMessage>();
		messageSelectionModel.setSelectionMode(Style.SelectionMode.SINGLE);
		messageSelectionModel.addSelectionChangedHandler(
				new SelectionChangedHandler<SystemMessage>() {
					@Override
					public void onSelectionChanged(
							final SelectionChangedEvent<SystemMessage> event) {
						final List<SystemMessage> selection = event.getSelection();
						if (!selection.isEmpty()) {
							selectMessage(selection.get(0));
						}
					}
	      });

        ArrayList<SystemMessage> msgs = new ArrayList<SystemMessage>();
        for(int i = 0; i < 5; i++) {
        	final String id = Integer.toString(i);
        	final Date startDate = new Date(1000*i);
        	final Date endDate = new Date(1000*(i + 3600));
        	msgs.add(new SystemMessage(id, "Maintenance", crap, startDate, endDate, false));
        }
        messageStore.replaceAll(msgs);

        view.setPresenter(this);
	}
	
	@Override
	public ListViewSelectionModel<SystemMessage> getMessageSelectionModel() {
		return messageSelectionModel;
	}

	@Override
	public ListStore<SystemMessage> getMessageStore() {
		return messageStore;
	}
		
	@Override
	public void handleDeleteButtonClick() {
		final SystemMessage selectedMsg = messageSelectionModel.getSelectedItem();
		if (selectedMsg != null) {
			final int msgIdx = messageStore.indexOf(selectedMsg);
			final int newSelectedIdx = (msgIdx + 1 == messageStore.size()) ? msgIdx - 1 : msgIdx;
			messageStore.remove(msgIdx);
			if (messageStore.size() > 0) {
				messageSelectionModel.select(false, messageStore.get(newSelectedIdx));
			}
		}
	}
	
	public void go(final AcceptsOneWidget container) {
		container.setWidget(view);
		view.showNoMessages(false);
        selectMessage(messageStore.get(0));
	}
	
	private void selectMessage(final SystemMessage msg) {
		messageSelectionModel.select(false, msg);
		final SafeHtmlBuilder bodyBuilder = new SafeHtmlBuilder();
		bodyBuilder.appendHtmlConstant(msg.getBody());
		view.setMessageBody(bodyBuilder.toSafeHtml());
		view.setExpiryText(msg.getEndTime().toString());
	}

}
