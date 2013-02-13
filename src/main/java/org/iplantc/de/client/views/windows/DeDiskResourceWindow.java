package org.iplantc.de.client.views.windows;

import java.util.Set;

import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.core.uidiskresource.client.gin.DiskResourceInjector;
import org.iplantc.core.uidiskresource.client.util.DiskResourceUtil;
import org.iplantc.core.uidiskresource.client.views.DiskResourceView;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.DiskResourceWindowConfig;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DeDiskResourceWindow extends IplantWindowBase {

    private final DiskResourceView.Presenter presenter;

    public DeDiskResourceWindow(DiskResourceWindowConfig config) {
        super(null, null);
        presenter = DiskResourceInjector.INSTANCE.getDiskResourceViewPresenter();

        setHeadingText(I18N.DISPLAY.data());
        setSize("800", "410");
        presenter.go(this);
        // presenter.setSelectedFolderById("/iplant/home/jstroot/analyses/analysis1-2012-10-15-14-44-02.028/logs");
        presenter.doRefresh();

        if (config.getSelectedFolder() != null) {
            presenter.setSelectedFolderById(config.getSelectedFolder().getId());
        }
        if ((config.getSelectedDiskResources() != null) && !config.getSelectedDiskResources().isEmpty()) {
            Set<String> diskResourceIdSet = Sets.newHashSet(DiskResourceUtil.asStringIdList(config.getSelectedDiskResources()));
            presenter.setSelectedDiskResourcesById(diskResourceIdSet);
        }

    }

    @Override
    public WindowState getWindowState() {
        DiskResourceWindowConfig config = ConfigFactory.diskResourceWindowConfig();
        config.setSelectedFolder(presenter.getSelectedFolder());
        config.setSelectedDiskResources(Lists.newArrayList(presenter.getSelectedDiskResources()));
        return createWindowState(config);
    }

}
