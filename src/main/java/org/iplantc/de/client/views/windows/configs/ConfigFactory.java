package org.iplantc.de.client.views.windows.configs;

import org.iplantc.core.uicommons.client.models.autobeans.WindowType;
import org.iplantc.core.uidiskresource.client.models.File;
import org.iplantc.de.client.notifications.util.NotificationHelper.Category;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

public class ConfigFactory {
    private static ConfigAutoBeanFactory factory = GWT.create(ConfigAutoBeanFactory.class);

    public static AboutWindowConfig aboutWindowConfig() {
        AboutWindowConfig awc = applyWindowType(WindowType.ABOUT, factory.aboutWindowConfig()).as();
        return awc;
    }

    public static AnalysisWindowConfig analysisWindowConfig() {
        AnalysisWindowConfig awc = applyWindowType(WindowType.ANALYSES, factory.analysisWindowConfig()).as();
        return awc;
    }
    
    public static AppsIntegrationWindowConfig appsIntegrationWindowConfig(){
        AppsIntegrationWindowConfig aiwc = applyWindowType(WindowType.APP_INTEGRATION, factory.appsIntegrationWindowConfig()).as();
        return aiwc;
    }

    public static AppsWindowConfig appsWindowConfig() {
        AppsWindowConfig awc = applyWindowType(WindowType.APPS, factory.appsWindowConfig()).as();
        return awc;
    }

    public static AppWizardConfig appWizardConfig(String appId) {
        AutoBean<AppWizardConfig> awc = applyWindowType(WindowType.APP_WIZARD, factory.appWizardConfig());
        applyTag(appId, awc);
        awc.as().setAppId(appId);
        return awc.as();
    }

    public static DiskResourceWindowConfig diskResourceWindowConfig() {
        DiskResourceWindowConfig drwc = applyWindowType(WindowType.DATA, factory.diskResourceWindowConfig()).as();
        return drwc;
    }

    public static FileViewerWindowConfig fileViewerWindowConfig(File file, boolean b) {
        AutoBean<FileViewerWindowConfig> fvwc = applyWindowType(WindowType.DATA_VIEWER, factory.fileViewerWindowConfig());
        fvwc.as().setFile(file);
        fvwc.as().setShowTreeTab(b);
        applyTag(file.getId(), fvwc);

        return fvwc.as();
    }

    public static IDropLiteWindowConfig iDropLiteWindowConfig() {
        IDropLiteWindowConfig idlwc = applyWindowType(WindowType.IDROP_LITE, factory.iDropLiteWindowConfig()).as();
        return idlwc;
    }

    public static NotifyWindowConfig notifyWindowConfig(Category category) {
        NotifyWindowConfig nwc = applyWindowType(WindowType.NOTIFICATIONS, factory.notifyWindowConfig()).as();
        nwc.setSortCategory(category);
        return nwc;
    }

    public static PipelineEditorWindowConfig workflowIntegrationWindowConfig() {
        PipelineEditorWindowConfig config = applyWindowType(WindowType.WORKFLOW_INTEGRATION,
                factory.pipelineEditorWindowConfig()).as();
        return config;
    }

    public static SimpleDownloadWindowConfig simpleDownloadWindowConfig() {
        SimpleDownloadWindowConfig sdwc = applyWindowType(WindowType.SIMPLE_DOWNLOAD, factory.simpleDownloadWindowConfig()).as();
        return sdwc;
    }

    private static <C extends WindowConfig> AutoBean<C> applyWindowType(WindowType type, AutoBean<C> wc) {
        Splittable data = StringQuoter.createSplittable();
        StringQuoter.create(type.toString()).assign(data, "windowType");
        AutoBeanCodex.decodeInto(data, wc);
        return wc;
    }

    private static <C extends WindowConfig> AutoBean<C> applyTag(String tag, AutoBean<C> wc) {
        Splittable data = StringQuoter.createSplittable();
        StringQuoter.create(tag).assign(data, "tag");
        AutoBeanCodex.decodeInto(data, wc);
        return wc;
    }

}
