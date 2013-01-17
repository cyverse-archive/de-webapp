package org.iplantc.de.client.views.windows;

import java.util.List;

import org.iplantc.core.uicommons.client.widgets.Hyperlink;
import org.iplantc.core.uidiskresource.client.util.DiskResourceUtil;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.DeResources;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.dispatchers.WindowDispatcher;
import org.iplantc.de.client.factories.EventJSONFactory.ActionType;
import org.iplantc.de.client.factories.WindowConfigFactory;
import org.iplantc.de.client.models.SimpleDownloadWindowConfig;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

/**
 * An iPlant window for displaying simple download links.
 * 
 * @author psarando
 * 
 */
public class SimpleDownloadWindow extends Gxt3IplantWindow {

    private final DeResources res = GWT.create(DeResources.class);

    public SimpleDownloadWindow(String tag, SimpleDownloadWindowConfig config) {
        super(tag, false, true, true, true);
        res.css().ensureInjected();

        init(config);
    }

    private void init(SimpleDownloadWindowConfig config) {
        setHeadingText(I18N.DISPLAY.download());
        setSize("320", "320");

        // Add window contents container for the simple download links
        VerticalLayoutContainer contents = new VerticalLayoutContainer();

        contents.add(new Label("" + I18N.DISPLAY.simpleDownloadNotice()));
        buildLinks(config, contents);
        add(contents);
    }

    private void buildLinks(SimpleDownloadWindowConfig config, VerticalLayoutContainer vlc) {

        List<String> downloadPaths = config.getDownloadPaths();
        for (final String path : downloadPaths) {
            Hyperlink link = new Hyperlink(DiskResourceUtil.parseNameFromPath(path), res.css().de_hyperlink());

            link.addClickListener(new Listener<ComponentEvent>() {
                @Override
                public void handleEvent(ComponentEvent be) {
                    Services.DISK_RESOURCE_SERVICE.simpleDownload(path);
                }
            });

            vlc.add(link);
        }

    }

    @Override
    public JSONObject getWindowState() {
        SimpleDownloadWindowConfig configData = new SimpleDownloadWindowConfig(config);
        storeWindowViewState(configData);

        WindowConfigFactory configFactory = new WindowConfigFactory();
        JSONObject windowConfig = configFactory.buildWindowConfig(Constants.CLIENT.simpleDownloadTag(),
                configData);

        WindowDispatcher dispatcher = new WindowDispatcher(windowConfig);
        return dispatcher.getDispatchJson(Constants.CLIENT.simpleDownloadTag(),
                ActionType.DISPLAY_WINDOW);
    }

}
