package org.iplantc.de.client.views.windows.configs;

import java.util.List;

import org.iplantc.core.uicommons.client.models.HasId;

public interface AnalysisWindowConfig extends WindowConfig {

    List<HasId> getSelectedAnalyses();
    
    void setSelectedAnalyses(List<HasId> selectedAnalyses);

}
