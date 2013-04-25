package org.iplantc.de.client.views.windows.configs;

import java.util.List;

import org.iplantc.de.client.analysis.models.Analysis;

public interface AnalysisWindowConfig extends WindowConfig {

    List<Analysis> getSelectedAnalyses();
    
    void setSelectedAnalyses(List<Analysis> selectedAnalyses);

}
