package org.iplantc.de.client.views.windows.configs;

import org.iplant.pipeline.client.json.autobeans.Pipeline;

/**
 * @author psarando
 * 
 */
public interface PipelineEditorWindowConfig extends WindowConfig {

    public Pipeline getPipeline();

    public void setPipeline(Pipeline pipeline);
}
