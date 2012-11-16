package org.iplantc.de.client.views.panels;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.iplantc.core.client.widgets.Hyperlink;
import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uidiskresource.client.models.DiskResource;
import org.iplantc.core.uidiskresource.client.models.Permissions;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.utils.DataUtils;
import org.iplantc.de.client.views.dialogs.DataSharingDialog;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Panel for listing details of a selected resource in the Manage Data window.
 * 
 * @author psarando
 * 
 */
public class DataDetailListPanel extends ContentPanel {

    private List<DiskResource> selection;

    public DataDetailListPanel() {
        init();
    }

    /**
     * Initializes this content panel.
     */
    private void init() {
        setHeaderVisible(false);
        setHeight(170);
        setBodyStyle("background-color: #EDEDED"); //$NON-NLS-1$

        TableLayout layout = new TableLayout(2);
        layout.setCellPadding(2);

        setLayout(layout);
        update(null);
    }

    /**
     * Update the this panel with the details of the given resource list. Hides the panel if the list
     * does not contain exactly one resource.
     * 
     * @param resources
     */
    public void update(final List<DiskResource> resources) {
        removeAll();
        selection = resources;
        if (selection != null && selection.size() == 1) {
            getDetails(selection.get(0).getId());
        } else {
            Text fieldLabel = new Text(I18N.DISPLAY.dataDetailsPrompt()); //$NON-NLS-1$
            fieldLabel.addStyleName("data_details_label"); //$NON-NLS-1$
            add(fieldLabel, new TableData(HorizontalAlignment.CENTER, VerticalAlignment.TOP));
        }
        layout();
    }

    /**
     * Adds the given "label: value" field to this panel.
     * 
     * @param label
     * @param value
     */
    private void addLabel(String label, String value) {
        Text fieldLabel = new Text(label + ": "); //$NON-NLS-1$
        fieldLabel.addStyleName("data_details_label"); //$NON-NLS-1$

        Text fieldValue = new Text(value);
        fieldValue.addStyleName("data_details_value"); //$NON-NLS-1$

        add(fieldLabel, new TableData(HorizontalAlignment.LEFT, VerticalAlignment.TOP));
        add(fieldValue, new TableData());
    }

    /**
     * Parses a timestamp string into a formatted date string and adds it to this panel.
     * 
     * @param label
     * @param value
     */
    private void addDateLabel(String label, Date date) {
        String value = "";

        if (date != null) {
            DateTimeFormat formatter = DateTimeFormat
                    .getFormat(DateTimeFormat.PredefinedFormat.RFC_2822);

            value = formatter.format(date);
        }

        addLabel(label, value);
    }

    /**
     * Parses a Long number into a formatted size string and adds it to this panel.
     * 
     * @param label
     * @param size
     */
    private void addSizeLabel(String label, Long size) {
        addLabel(label, DataUtils.getSizeForDisplay(size));
    }

    /**
     * Add permissions detail
     * 
     * @param label label to display
     * @param readable boolean true if the resource is readable else false
     * @param writable boolean true if the resource is writable else false
     */
    private void addPermissionsLabel(String label, Permissions p) {
        if (p.isOwner()) {
            addLabel(label, I18N.DISPLAY.owner());
            return;
        }
        if (!p.isWritable()) {
            addLabel(label, I18N.DISPLAY.readOnly());
        } else {
            addLabel(label, I18N.DISPLAY.readWrite());
        }
    }

    /**
     * 
     * Add sharing info
     * 
     */

    private void addSharingInfo(String label, int shareCount) {
        Hyperlink link = null;
        if (shareCount == 0) {
            link = new Hyperlink(I18N.DISPLAY.nosharing(), "sharingInfo");
        } else {
            link = new Hyperlink("" + shareCount, "sharingInfo");
        }

        link.addListener(Events.OnClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                DataSharingDialog dsd = new DataSharingDialog(Arrays.asList(selection.get(0)));
                dsd.initView();
                dsd.show();
            }
        });

        Text fieldLabel = new Text(label + ": "); //$NON-NLS-1$
        fieldLabel.addStyleName("data_details_label"); //$NON-NLS-1$

        add(fieldLabel, new TableData(HorizontalAlignment.LEFT, VerticalAlignment.TOP));
        add(link, new TableData());

    }

    /**
     * Gets details for given path, then adds details of the results to this panel.
     * 
     * @param path
     */
    private void getDetails(final String path) {
        DiskResourceServiceFacade facade = new DiskResourceServiceFacade();
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        arr.set(0, new JSONString(path));
        obj.put("paths", arr);

        facade.getStat(obj.toString(), new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // race condition gaurd
                if (selection.size() == 1 && selection.get(0).getId().equalsIgnoreCase(path)) {
                    JSONObject json = JsonUtil.getObject(result);
                    if (json != null) {
                        JSONObject pathsObj = JsonUtil.getObject(json, "paths");
                        JSONObject details = JsonUtil.getObject(pathsObj, path);
                        JSONObject perm = JsonUtil.getObject(details, "permissions");
                        addDateLabel(I18N.DISPLAY.lastModified(),
                                new Date(Long.parseLong(JsonUtil.getString(details, "modified"))));
                        addDateLabel(I18N.DISPLAY.createdDate(),
                                new Date(Long.parseLong(JsonUtil.getString(details, "created"))));
                        Number n = (JsonUtil.getNumber(details, "share-count"));
                        addPermissionsLabel(I18N.DISPLAY.permissions(), new Permissions(perm));
                        if (n != null) {
                            addSharingInfo(I18N.DISPLAY.share(), n.intValue());
                        }
                        String type = JsonUtil.getString(details, "type");

                        if (type.equalsIgnoreCase("file")) {
                            addSizeLabel(I18N.DISPLAY.size(),
                                    (JsonUtil.getNumber(details, "size").longValue()));
                        } else {
                            addLabel(I18N.DISPLAY.folders(),
                                    String.valueOf(JsonUtil.getNumber(details, "dir-count").intValue()));
                            addLabel(I18N.DISPLAY.files(),
                                    String.valueOf(JsonUtil.getNumber(details, "file-count").intValue()));
                        }
                    }
                }

                layout();
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.retrieveStatFailed(), caught);
            }
        });
    }
}
