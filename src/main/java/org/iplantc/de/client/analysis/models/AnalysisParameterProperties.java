/**
 * 
 */
package org.iplantc.de.client.analysis.models;

import org.iplantc.de.apps.widgets.client.models.ArgumentType;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author sriram
 * 
 */
public interface AnalysisParameterProperties extends PropertyAccess<AnalysisParameter> {

    ValueProvider<AnalysisParameter, String> name();

    ValueProvider<AnalysisParameter, ArgumentType> type();
}