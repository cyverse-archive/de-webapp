package org.iplantc.de.client.sysmsgs.presenter;

import java.util.Date;

import org.iplantc.de.client.sysmsgs.model.Message;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;


/**
 * TODO document
 */
public interface MessageProperties extends PropertyAccess<Message> {
	
	public static final MessageProperties INSTANCE = GWT.create(MessageProperties.class);
	
	ModelKeyProvider<Message> id();
	
	ValueProvider<Message, Date> creationTime();
	
	ValueProvider<Message, Boolean> dismissible();
	
}
