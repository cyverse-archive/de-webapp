/**
 * 
 */
package org.iplantc.de.client.analysis.models;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * @author sriram
 * 
 */
public interface AnalysesList {

    @PropertyName("analyses")
    List<Analysis> getAnalysisList();

    int getTotal();
}
