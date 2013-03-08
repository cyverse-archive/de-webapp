package org.iplantc.de.client.views.windows;

import org.iplantc.core.pipelineBuilder.client.json.autobeans.Pipeline;
import org.iplantc.core.pipelines.client.presenter.PipelineViewPresenter;
import org.iplantc.core.pipelines.client.views.PipelineView;
import org.iplantc.core.pipelines.client.views.PipelineViewImpl;
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
        setSize("900", "500"); //$NON-NLS-1$ //$NON-NLS-2$

        Pipeline pipeline = null;
        if (config instanceof PipelineEditorWindowConfig) {
            PipelineEditorWindowConfig pipelineConfig = (PipelineEditorWindowConfig)config;
            pipeline = pipelineConfig.getPipeline();
        }

        PipelineView view = new PipelineViewImpl();
        presenter = new PipelineViewPresenter(view, new PublishCallbackCommand());
        presenter.go(this, pipeline);
    }

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
