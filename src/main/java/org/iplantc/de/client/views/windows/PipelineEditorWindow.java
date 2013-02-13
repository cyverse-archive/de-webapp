package org.iplantc.de.client.views.windows;

import org.iplantc.core.client.pipelines.gxt3.presenter.PipelineViewPresenter;
import org.iplantc.core.client.pipelines.gxt3.views.PipelineView;
import org.iplantc.core.client.pipelines.gxt3.views.PipelineViewImpl;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.PipelineEditorWindowConfig;
import org.iplantc.de.client.views.windows.configs.WindowConfig;

import com.google.gwt.user.client.Command;

public class PipelineEditorWindow extends IplantWindowBase {
    private final PipelineView.Presenter presenter;

    public PipelineEditorWindow(WindowConfig config) {
        super(null, null);

        setHeadingText(I18N.DISPLAY.pipeline());
        setSize("1024", "600"); //$NON-NLS-1$ //$NON-NLS-2$

        PipelineView view = new PipelineViewImpl();
        presenter = new PipelineViewPresenter(view, new PublishCallbackCommand());
        presenter.go(this);
    }

// /**
// * {@inheritDoc}
// */
// @Override
// public void show() {
// super.show();
// if (config != null) {
// editorPanel.configure(((PipelineEditorWindowConfig)config).getPipelineConfig());
// setWindowViewState();
// // reset config
// config = null;
// }
//
// }
//
// /**
// * Applies a window configuration to the window.
// *
// * @param config
// */
// @Override
// public void setWindowConfig(WindowConfig config) {
// if (config instanceof PipelineEditorWindowConfig) {
// this.config = config;
// }
// }
//
// @Override
// public void cleanup() {
// super.cleanup();
// editorPanel.cleanup();
// categoryPanel.cleanup();
// }

    class PublishCallbackCommand implements Command {
        @Override
        public void execute() {
            hide();
        }

    }

    @Override
    public WindowState getWindowState() {
        PipelineEditorWindowConfig configData = ConfigFactory.workflowIntegrationWindowConfig();
        configData.setPipeline(presenter.getPipeline());
        return createWindowState(configData);
    }
}
