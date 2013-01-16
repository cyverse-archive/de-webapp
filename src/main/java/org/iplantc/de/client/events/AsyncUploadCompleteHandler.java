package org.iplantc.de.client.events;

import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uidiskresource.client.models.autobeans.DiskResource;
import org.iplantc.core.uidiskresource.client.models.autobeans.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.notifications.util.NotificationHelper;
import org.iplantc.de.client.utils.NotifyInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

public class AsyncUploadCompleteHandler extends DefaultUploadCompleteHandler {

    /**
     * {@inheritDoc}
     */
    public AsyncUploadCompleteHandler(String idParent) {
        super(idParent);
    }

    /**
     * Notify user that the upload has successfully started.
     * 
     * @param sourceUrl
     * @param response
     */
    public void onImportSuccess(String sourceUrl, String response) {
        try {
            JSONObject payload = buildPayload(sourceUrl, response);
            DiskResourceAutoBeanFactory factory = GWT.create(DiskResourceAutoBeanFactory.class);
            AutoBean<DiskResource> dr = AutoBeanCodex.decode(factory, DiskResource.class, payload.toString());
            // TODO Is it possible to only display file in UI once asynchronous upload is complete?
            String filename = dr.as().getName();
            NotifyInfo.notify(NotificationHelper.Category.DATA, I18N.DISPLAY.urlImport(),
                    I18N.DISPLAY.importRequestSubmit(filename), null);
        } catch (Exception e) {
            ErrorHandler.post(I18N.ERROR.importFailed(sourceUrl), e);
        } finally {
            // TODO: consider having onCompletion and onAfterCompletion called by superclass
            // method to more appropriately confirm w/ Template Method and Command patterns
            onAfterCompletion();
        }
    }

}
