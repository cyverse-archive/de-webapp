package org.iplantc.de.client.notifications.services;

import java.util.Collections;
import java.util.List;

import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uidiskresource.client.models.DiskResourceAutoBeanFactory;
import org.iplantc.core.uidiskresource.client.models.File;
import org.iplantc.de.client.notifications.models.Notification;
import org.iplantc.de.client.notifications.models.NotificationAnalysisContext;
import org.iplantc.de.client.notifications.models.NotificationAutoBeanFactory;
import org.iplantc.de.client.notifications.models.NotificationList;
import org.iplantc.de.client.notifications.models.NotificationMessage;
import org.iplantc.de.client.notifications.models.NotificationPayload;
import org.iplantc.de.client.notifications.util.NotificationHelper.Category;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * @author jstroot
 *
 */
public class NotificationCallback implements AsyncCallback<String> {

    private final NotificationAutoBeanFactory notFactory = GWT.create(NotificationAutoBeanFactory.class);
    private final DiskResourceAutoBeanFactory drFactory = GWT.create(DiskResourceAutoBeanFactory.class);
    private List<Notification> notifications;

    @Override
    public void onSuccess(String result) {
        AutoBean<NotificationList> bean = AutoBeanCodex.decode(notFactory, NotificationList.class, result);
        List<Notification> notifications = bean.as().getNotifications();
        for (Notification n : notifications) {
            NotificationMessage msg = n.getMessage();
            msg.setCategory(Category.fromTypeString(n.getCategory()));
            NotificationPayload payload = n.getNotificationPayload();
            if (Strings.isNullOrEmpty(payload.getAction())) {
                continue;
            }
            String id = payload.getId();
            switch (msg.getCategory()) {
                case ALL:
                    GWT.log("ALL Analysis category: Action = " + payload.getAction());
                    break;

                case ANALYSIS:
                    if (payload.getAction().equals("job_status_change")) {
                        AutoBean<NotificationAnalysisContext> contextBean = notFactory
                                .getNotificationAnalysisContext();

                        NotificationAnalysisContext context = contextBean.as();
                        context.setId(payload.getId());
                        context.setName(payload.getName());

                        msg.setContext(AutoBeanCodex.encode(contextBean).getPayload());
                    } else {
                        GWT.log("Unhandled Analysis action type!!");
                    }
                    break;

                case DATA:
                    if (payload.getAction().equals("file_uploaded")) {
                        AutoBean<File> fileAb = AutoBeanCodex.decode(drFactory, File.class, payload.getData());
                        msg.setContext(AutoBeanCodex.encode(fileAb).getPayload());
                    }
                    break;

                case NEW:
                    GWT.log("NEW  category: Action = " + payload.getAction());
                    break;

                case SYSTEM:
                    GWT.log("SYSTEM  category: Action = " + payload.getAction());
                    break;

                case TOOLREQUEST:
                	GWT.log("SYSTEM  category: Action = " + payload.getAction());
                    break;

                default:
                    break;
            }
        }

        setNotifications(notifications);
    }

    private void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    protected List<Notification> getNotifications() {
        if (notifications == null) {
            return Collections.<Notification> emptyList();
        } else {
            return notifications;
        }
    }

    @Override
    public void onFailure(Throwable caught) {
        ErrorHandler.post(caught);
    }

}
