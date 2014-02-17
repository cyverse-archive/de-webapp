package org.iplantc.de.client.views.windows.configs;

import org.iplantc.de.client.models.analysis.Analysis;

import java.util.List;

public interface AnalysisWindowConfig extends WindowConfig {

    List<Analysis> getSelectedAnalyses();
    
    void setSelectedAnalyses(List<Analysis> selectedAnalyses);

}
