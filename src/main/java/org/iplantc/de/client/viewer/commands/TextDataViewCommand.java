package org.iplantc.de.client.viewer.commands;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.models.UserInfo;
import org.iplantc.core.uidiskresource.client.models.File;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.viewer.views.TextViewerImpl;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author sriram
 */
public class TextDataViewCommand implements ViewCommand {

    @Override
    public FileViewer execute(final File file) {
        final FileViewer view = new TextViewerImpl();
        String url = "file/preview?user=" + URL.encodeQueryString(UserInfo.getInstance().getUsername())
                + "&path=" + URL.encodeQueryString(file.getId());
        Services.FILE_EDITOR_SERVICE.getData(url, new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                view.setData(JsonUtil.getString(JsonUtil.getObject(result), "preview"));
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.unableToRetrieveFileData(file.getName()), caught);
            }
        });

        return view;
    }
}
