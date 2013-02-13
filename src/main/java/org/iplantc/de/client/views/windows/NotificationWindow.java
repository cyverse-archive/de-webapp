/**
 * 
 */
package org.iplantc.de.client.views.windows;

import java.util.LinkedList;
import java.util.List;

import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.notifications.models.NotificationMessage;
import org.iplantc.de.client.notifications.models.NotificationMessageProperties;
import org.iplantc.de.client.notifications.presenter.NotificationPresenter;
import org.iplantc.de.client.notifications.util.NotificationHelper.Category;
import org.iplantc.de.client.notifications.views.NotificationView;
import org.iplantc.de.client.notifications.views.NotificationViewImpl;
import org.iplantc.de.client.notifications.views.cells.NotificationMessageCell;
import org.iplantc.de.client.notifications.views.cells.NotificationMessageTmestampCell;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.NotifyWindowConfig;

import com.google.gwt.core.shared.GWT;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

/**
 * @author sriram
 */
public class NotificationWindow extends IplantWindowBase {

    private static CheckBoxSelectionModel<NotificationMessage> checkBoxModel;
    private final NotificationView.Presenter presenter;

    public NotificationWindow(NotifyWindowConfig config) {
        super(null, null);
        setTitle(I18N.DISPLAY.notifications());
        NotificationKeyProvider keyProvider = new NotificationKeyProvider();
        ListStore<NotificationMessage> store = new ListStore<NotificationMessage>(keyProvider);
        ColumnModel<NotificationMessage> cm = buildNotificationColumnModel();
        NotificationView view = new NotificationViewImpl(store, cm, checkBoxModel);
        presenter = new NotificationPresenter(view);
        setSize("800", "410");
        presenter.go(this);
    }

    @SuppressWarnings("unchecked")
    private static ColumnModel<NotificationMessage> buildNotificationColumnModel() {
        NotificationMessageProperties props = GWT.create(NotificationMessageProperties.class);
        List<ColumnConfig<NotificationMessage, ?>> configs = new LinkedList<ColumnConfig<NotificationMessage, ?>>();

        checkBoxModel = new CheckBoxSelectionModel<NotificationMessage>(
                new IdentityValueProvider<NotificationMessage>());
        @SuppressWarnings("rawtypes")
        ColumnConfig colCheckBox = checkBoxModel.getColumn();
        configs.add(colCheckBox);

        ColumnConfig<NotificationMessage, Category> colCategory = new ColumnConfig<NotificationMessage, Category>(
                props.category(), 100);
        colCategory.setHeader(I18N.CONSTANT.category());
        configs.add(colCategory);
        colCategory.setMenuDisabled(true);
        colCategory.setSortable(false);

        ColumnConfig<NotificationMessage, NotificationMessage> colMessage = new ColumnConfig<NotificationMessage, NotificationMessage>(
                new IdentityValueProvider<NotificationMessage>(), 420);
        colMessage.setHeader(I18N.DISPLAY.messagesGridHeader());
        colMessage.setCell(new NotificationMessageCell());
        configs.add(colMessage);
        colMessage.setSortable(false);
        colMessage.setMenuDisabled(true);

        ColumnConfig<NotificationMessage, NotificationMessage> colTimestamp = new ColumnConfig<NotificationMessage, NotificationMessage>(
                new IdentityValueProvider<NotificationMessage>(), 170);
        colTimestamp.setCell(new NotificationMessageTmestampCell());
        colTimestamp.setHeader(I18N.DISPLAY.createdDateGridHeader());

        configs.add(colTimestamp);
        ColumnModel<NotificationMessage> columnModel = new ColumnModel<NotificationMessage>(configs);
        return columnModel;
    }

    private class NotificationKeyProvider implements ModelKeyProvider<NotificationMessage> {

        @Override
        public String getKey(NotificationMessage item) {
            return item.getId();
        }

    }

    @Override
    public WindowState getWindowState() {
        NotifyWindowConfig config = ConfigFactory.notifyWindowConfig(presenter.getCurrentCategory());
        return createWindowState(config);
    }

}
