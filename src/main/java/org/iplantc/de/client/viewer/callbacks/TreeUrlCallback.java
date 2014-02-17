package org.iplantc.de.client.viewer.callbacks;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.TreeUrlAutoBeanFactory;
import org.iplantc.de.client.models.viewer.VizUrl;
import org.iplantc.de.client.models.viewer.VizUrlList;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.commons.client.ErrorHandler;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

public class TreeUrlCallback implements AsyncCallback<String> {

    private IsMaskable container;
    private FileViewer viewer;
    private File file;

    private final static TreeUrlAutoBeanFactory factory = GWT.create(TreeUrlAutoBeanFactory.class);

    public TreeUrlCallback(File file, IsMaskable container, FileViewer viewer) {
        this.file = file;
        this.container = container;
        this.viewer = viewer;
    }

    @Override
    public void onFailure(Throwable caught) {
        container.unmask();

        String errMsg = org.iplantc.de.resources.client.messages.I18N.ERROR.unableToRetrieveTreeUrls(file.getName());
        ErrorHandler.post(errMsg, caught);

    }

    @Override
    public void onSuccess(String result) {
        if (result != null && !result.isEmpty()) {
            List<VizUrl> urlsList = getTreeUrls(result);
            if (urlsList != null) {
                viewer.setData(urlsList);
                container.unmask();
            } else {
                container.unmask();
                // couldn't find any tree URLs in the response, so display an error.
                onFailure(new Exception(result));
            }

        } else {
            // couldn't find any tree URLs in the response, so display an error.
            onFailure(new Exception(result));
            container.unmask();
        }

    }

    public static List<VizUrl> getTreeUrls(String urls) {
        if (urls != null) {
            AutoBean<VizUrlList> bean = AutoBeanCodex.decode(factory, VizUrlList.class, urls.toString());
            return bean.as().getUrls();
        }

        return null;
    }

}
