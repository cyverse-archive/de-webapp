package org.iplantc.de.client.notifications.util;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uicommons.client.models.CommonModelAutoBeanFactory;
import org.iplantc.core.uicommons.client.models.HasId;
import org.iplantc.core.uidiskresource.client.models.DiskResourceAutoBeanFactory;
import org.iplantc.core.uidiskresource.client.models.File;
import org.iplantc.core.uidiskresource.client.util.DiskResourceUtil;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.analysis.models.AnalysesAutoBeanFactory;
import org.iplantc.de.client.analysis.models.Analysis;
import org.iplantc.de.client.events.NotificationCountUpdateEvent;
import org.iplantc.de.client.events.WindowShowRequestEvent;
import org.iplantc.de.client.notifications.events.DeleteNotificationsUpdateEvent;
import org.iplantc.de.client.notifications.models.NotificationAutoBeanFactory;
import org.iplantc.de.client.notifications.models.NotificationMessage;
import org.iplantc.de.client.notifications.models.payload.PayloadToolRequest;
import org.iplantc.de.client.notifications.models.payload.ToolRequestHistory;
import org.iplantc.de.client.notifications.views.dialogs.ToolRequestHistoryDialog;
import org.iplantc.de.client.views.windows.configs.AnalysisWindowConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.DiskResourceWindowConfig;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * helps with notifications for the user.
 *
 *
 * @author lenards, sriram
 *
 */
public class NotificationHelper {
    /**
     * Represents a notification category.
     *
     * XXX JDS If these enum fields were the same name as what comes in (e.g. ANALYSIS could be
     * Analysis), then they could be deserialized directly into the autobean.
     */
    public enum Category {
        /** All notification categories */
        ALL(I18N.CONSTANT.notificationCategoryAll()),
        /** System notifications */
        SYSTEM(I18N.CONSTANT.notificationCategorySystem()),
        /** Data notifications */
        DATA(I18N.CONSTANT.notificationCategoryData()),
        /** Analysis notifications */
        ANALYSIS(I18N.CONSTANT.notificationCategoryAnalysis()),

        /** tool rquest status update notification */
        TOOLREQUEST(I18N.CONSTANT.toolRequest()),

        /** unseen notifications */
        NEW(I18N.CONSTANT.notificationCategoryUnseen());

        private String displayText;

        private Category(String displayText) {
            this.displayText = displayText;
        }

        /**
         * Null-safe and case insensitive variant of valueOf(String)
         *
         * @param typeString
         * @return
         */
        public static Category fromTypeString(String typeString) {
            if (typeString == null || typeString.isEmpty()) {
                return null;
            }
            String temp = typeString.replaceAll("\\s", "");
            return valueOf(temp.toUpperCase());
        }

        @Override
        public String toString() {
            return displayText;
        }
    }

    private static NotificationHelper instance = null;

    private int total;

    private final DiskResourceAutoBeanFactory drFactory = GWT.create(DiskResourceAutoBeanFactory.class);
    private final CommonModelAutoBeanFactory cFactory = GWT.create(CommonModelAutoBeanFactory.class);
    private final AnalysesAutoBeanFactory analysesFactory = GWT.create(AnalysesAutoBeanFactory.class);
    private final NotificationAutoBeanFactory notificationFactory = GWT
            .create(NotificationAutoBeanFactory.class);

    private NotificationHelper() {
    }


    /** View a notification */
    public void view(NotificationMessage msg) {
        if (msg == null) {
            return;
        }

        // did we get a category?
        NotificationHelper.Category category = msg.getCategory();
        if (category == null) {
            return;
        }

        // did we get a context to execute?
        String context = msg.getContext();
        if (context == null) {
            return;
        }

        switch (category) {
            case DATA:
                // execute data context
                AutoBean<File> fAb = AutoBeanCodex.decode(drFactory, File.class, context);
                ArrayList<HasId> newArrayList = Lists.newArrayList();
                newArrayList.add(fAb.as());

                DiskResourceWindowConfig dataWindowConfig = ConfigFactory.diskResourceWindowConfig();
                HasId folderId = DiskResourceUtil.getFolderIdFromFile(cFactory, fAb.as());
                dataWindowConfig.setSelectedFolder(folderId);
                dataWindowConfig.setSelectedDiskResources(newArrayList);
                EventBus.getInstance().fireEvent(new WindowShowRequestEvent(dataWindowConfig, true));

                break;

            case ANALYSIS:
                AutoBean<Analysis> hAb = AutoBeanCodex.decode(analysesFactory, Analysis.class, context);

                AnalysisWindowConfig analysisWindowConfig = ConfigFactory.analysisWindowConfig();
                analysisWindowConfig.setSelectedAnalyses(Lists.newArrayList(hAb.as()));
                EventBus.getInstance().fireEvent(new WindowShowRequestEvent(analysisWindowConfig, true));

                break;

            case TOOLREQUEST:
                PayloadToolRequest toolRequest = AutoBeanCodex.decode(notificationFactory,
                        PayloadToolRequest.class, context).as();

                List<ToolRequestHistory> history = toolRequest.getHistory();

                ToolRequestHistoryDialog dlg = new ToolRequestHistoryDialog(history);
                dlg.show();

                break;

            default:
                break;
        }
    }

    /**
     * Return the shared, singleton instance of the manager.
     *
     * @return a singleton reference to the notification manager.
     */
    public static NotificationHelper getInstance() {
        if (instance == null) {
            instance = new NotificationHelper();
        }

        return instance;
    }

    private void doDelete(final List<NotificationMessage> notifications, final JSONObject json,
            final Command callback) {
        if (json != null) {
            Services.MESSAGE_SERVICE.deleteMessages(json, new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(I18N.ERROR.notificationDeletFail(), caught);

                }

                @Override
                public void onSuccess(String result) {
                    if (callback != null) {
                        callback.execute();
                        DeleteNotificationsUpdateEvent event = new DeleteNotificationsUpdateEvent(
                                notifications);
                        EventBus.getInstance().fireEvent(event);
                    }
                }
            });
        }
    }

    /**
     * Mark notifications as seen
     *
     */
    public void markAsSeen(List<NotificationMessage> list) {
        if (list != null && list.size() > 0) {
            JSONArray arr = buildSeenServiceRequestBody(list);

            if (arr.size() > 0) {
                JSONObject obj = new JSONObject();
                obj.put("uuids", arr);
                Services.MESSAGE_SERVICE.markAsSeen(obj, new AsyncCallback<String>() {

                    @Override
                    public void onSuccess(String result) {
                        JSONObject obj = JsonUtil.getObject(result);
                        int new_count = Integer.parseInt(JsonUtil.getString(obj, "count"));
                        // fire update of the new unseen count;
                        NotificationCountUpdateEvent event = new NotificationCountUpdateEvent(new_count);
                        EventBus.getInstance().fireEvent(event);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        ErrorHandler.post(caught);
                    }
                });
            }
        }
    }

    private JSONArray buildSeenServiceRequestBody(List<NotificationMessage> list) {
        JSONArray arr = new JSONArray();
        int i = 0;

        for (NotificationMessage n : list) {
            if (!n.isSeen()) {
                arr.set(i++, new JSONString(n.getId()));
                n.setSeen(true);
            }
        }
        return arr;
    }

    /**
     * Delete a list of notifications.
     *
     * @param notifications notifications to be deleted.
     */
    public void delete(final List<NotificationMessage> notifications, Command callback) {
        // do we have any notifications to delete?
        if (notifications != null && !notifications.isEmpty()) {
            JSONObject obj = new JSONObject();
            JSONArray arr = new JSONArray();
            int i = 0;
            for (NotificationMessage n : notifications) {
                arr.set(i++, new JSONString(n.getId()));
            }
            obj.put("uuids", arr);

            doDelete(notifications, obj, callback);
        }
    }

    /**
     * @param total the total to set
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }
}
