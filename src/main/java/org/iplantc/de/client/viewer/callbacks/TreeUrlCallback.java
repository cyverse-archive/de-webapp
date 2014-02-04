package org.iplantc.de.client.viewer.callbacks;

import java.util.List;

import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.models.diskresources.File;
import org.iplantc.de.commons.client.views.IsMaskable;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.viewer.models.TreeUrlAutoBeanFactory;
import org.iplantc.de.client.viewer.models.VizUrl;
import org.iplantc.de.client.viewer.models.VizUrlList;
import org.iplantc.de.client.viewer.views.FileViewer;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

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

        String errMsg = I18N.ERROR.unableToRetrieveTreeUrls(file.getName());
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
