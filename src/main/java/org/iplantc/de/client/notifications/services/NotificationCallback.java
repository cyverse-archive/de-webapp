package org.iplantc.de.client.notifications.services;

import java.util.Collections;
import java.util.List;

import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uidiskresource.client.models.DiskResourceAutoBeanFactory;
import org.iplantc.core.uidiskresource.client.models.File;
import org.iplantc.de.client.notifications.models.Notification;
import org.iplantc.de.client.notifications.models.NotificationAutoBeanFactory;
import org.iplantc.de.client.notifications.models.NotificationList;
import org.iplantc.de.client.notifications.models.NotificationMessage;
import org.iplantc.de.client.notifications.models.payload.PayloadAnalysis;
import org.iplantc.de.client.notifications.models.payload.PayloadData;
import org.iplantc.de.client.notifications.util.NotificationHelper.Category;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

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
            Splittable payload = n.getNotificationPayload();

            if (payload == null) {
                continue;
            }

            switch (msg.getCategory()) {
                case ALL:
                    GWT.log("ALL Analysis category");
                    break;

                case ANALYSIS:
                    PayloadAnalysis analysisPayload = AutoBeanCodex.decode(notFactory,
                            PayloadAnalysis.class, payload).as();
                    String analysisAction = analysisPayload.getAction();

                    if ("job_status_change".equals(analysisAction)) {
                        msg.setContext(payload.getPayload());
                    } else {
                        GWT.log("Unhandled Analysis action type!!");
                    }
                    break;

                case DATA:
                    PayloadData dataPayload = AutoBeanCodex.decode(notFactory,
                            PayloadData.class, payload).as();
                    String dataAction = dataPayload.getAction();

                    if ("file_uploaded".equals(dataAction)) {
                        AutoBean<File> fileAb = AutoBeanUtils.getAutoBean(dataPayload.getData());
                        msg.setContext(AutoBeanCodex.encode(fileAb).getPayload());
                    }
                    break;

                case NEW:
                    GWT.log("NEW  category");
                    break;

                case SYSTEM:
                    GWT.log("SYSTEM  category");
                    break;

                case TOOLREQUEST:
                    GWT.log("TOOLREQUEST  category");
                    msg.setContext(payload.getPayload());
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
