package org.iplantc.de.client.sysmsgs.presenter;

import java.util.Date;

import org.iplantc.de.client.sysmsgs.model.SystemMessage;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * TODO document
 */
public interface SystemMessageProperties extends PropertyAccess<SystemMessage> {
	
	static final SystemMessageProperties instance = GWT.create(SystemMessageProperties.class);
	
	ModelKeyProvider<SystemMessage> id();
	
	ValueProvider<SystemMessage, Date> startTime();
	
}
