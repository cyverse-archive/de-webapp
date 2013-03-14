package org.iplantc.de.client.views.windows.configs;

import org.iplantc.core.pipelineBuilder.client.json.autobeans.Pipeline;

import com.google.web.bindery.autobean.shared.Splittable;

/**
 * @author psarando
 * 
 */
public interface PipelineEditorWindowConfig extends WindowConfig {

    public Pipeline getPipeline();

    public void setPipeline(Pipeline pipeline);

    Splittable getLegacyAppTemplateJson();

    void setLegacyAppTemplateJson(Splittable legacyJson);
}
