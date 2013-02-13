package org.iplantc.de.client.views.windows;

import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.core.uicommons.client.widgets.Hyperlink;
import org.iplantc.core.uidiskresource.client.models.DiskResource;
import org.iplantc.core.uidiskresource.client.util.DiskResourceUtil;
import org.iplantc.de.client.DeResources;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.SimpleDownloadWindowConfig;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

/**
 * An iPlant window for displaying simple download links.
 * 
 * @author psarando
 * 
 */
public class SimpleDownloadWindow extends IplantWindowBase {

    private final DeResources res = GWT.create(DeResources.class);

    public SimpleDownloadWindow(SimpleDownloadWindowConfig config) {
        super(null, false, true, true, true);
        res.css().ensureInjected();
        setHeadingText(I18N.DISPLAY.download());
        setSize("320", "320");

        init(config);
    }

    private void init(SimpleDownloadWindowConfig config) {
        // Add window contents container for the simple download links
        VerticalLayoutContainer contents = new VerticalLayoutContainer();

        contents.add(new Label("" + I18N.DISPLAY.simpleDownloadNotice()));
        buildLinks(config, contents);
        add(contents);

    }

    private void buildLinks(SimpleDownloadWindowConfig config, VerticalLayoutContainer vlc) {
        for (final DiskResource dr : config.getResourcesToDownload()) {
            Hyperlink link = new Hyperlink(DiskResourceUtil.parseNameFromPath(dr.getId()), res.css().de_hyperlink());

            link.addClickListener(new Listener<ComponentEvent>() {
                @Override
                public void handleEvent(ComponentEvent be) {
                    Services.DISK_RESOURCE_SERVICE.simpleDownload(dr.getId());
                }
            });

            vlc.add(link);
        }
    }

    @Override
    public WindowState getWindowState() {
        SimpleDownloadWindowConfig config = ConfigFactory.simpleDownloadWindowConfig();
        return createWindowState(config);
    }

}
