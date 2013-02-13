package org.iplantc.de.client.views.windows.configs;

import org.iplantc.core.widgets.client.appWizard.models.AppTemplate;

import com.google.web.bindery.autobean.shared.Splittable;

public interface AppWizardConfig extends WindowConfig {
    
    AppTemplate getAppTemplate();

    String getAppId();

    void setAppId(String appId);

    void setAppTemplate(AppTemplate appTemplate);

    Splittable getLegacyAppTemplateJson();

    void setLegacyAppTemplateJson(Splittable legacyJson);

}

