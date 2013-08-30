package org.iplantc.de.client.views.windows;

import java.util.List;

import org.iplantc.core.uicommons.client.models.HasId;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.core.uidiskresource.client.gin.DiskResourceInjector;
import org.iplantc.core.uidiskresource.client.views.DiskResourceView;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.DiskResourceWindowConfig;
import org.iplantc.de.client.views.windows.configs.WindowConfig;

import com.google.common.collect.Lists;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent;
import com.sencha.gxt.widget.core.client.event.RestoreEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent.MaximizeHandler;
import com.sencha.gxt.widget.core.client.event.RestoreEvent.RestoreHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;

public class DeDiskResourceWindow extends IplantWindowBase {

    private final DiskResourceView.Presenter presenter;

    public DeDiskResourceWindow(final DiskResourceWindowConfig config) {
        super(null, null);
        presenter = DiskResourceInjector.INSTANCE.getDiskResourceViewPresenter();

        setHeadingText(I18N.DISPLAY.data());
        setSize("700", "375");

        // Create an empty
        List<HasId> resourcesToSelect = Lists.newArrayList();
        if (config.getSelectedDiskResources() != null) {
            resourcesToSelect.addAll(config.getSelectedDiskResources());
        }
        presenter.go(this, config.getSelectedFolder(), resourcesToSelect);

        addRestoreHandler(new RestoreHandler() {

            @Override
            public void onRestore(RestoreEvent event) {
                maximized = false;
            }
        });

        addMaximizeHandler(new MaximizeHandler() {

            @Override
            public void onMaximize(MaximizeEvent event) {
                maximized = true;
            }
        });

        addShowHandler(new ShowHandler() {

            @Override
            public void onShow(ShowEvent event) {
            	if(config!= null && config.isMaximized())
                DeDiskResourceWindow.this.maximize();
            }
        });

    }

    @Override
    public WindowState getWindowState() {
        DiskResourceWindowConfig config = ConfigFactory.diskResourceWindowConfig();
        config.setSelectedFolder(presenter.getSelectedFolder());
        List<HasId> selectedResources = Lists.newArrayList();
        selectedResources.addAll(presenter.getSelectedDiskResources());
        config.setSelectedDiskResources(selectedResources);
        return createWindowState(config);
    }

    @Override
    public <C extends WindowConfig> void update(C config) {
        DiskResourceWindowConfig drConfig = (DiskResourceWindowConfig)config;
        presenter.setSelectedFolderById(drConfig.getSelectedFolder());
        presenter.setSelectedDiskResourcesById(drConfig.getSelectedDiskResources());
    }

}
