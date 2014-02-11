package org.iplantc.de.client.views.windows.configs;

import org.iplantc.de.client.analysis.models.Analysis;

import java.util.List;

public interface AnalysisWindowConfig extends WindowConfig {

    List<Analysis> getSelectedAnalyses();
    
    void setSelectedAnalyses(List<Analysis> selectedAnalyses);

}
