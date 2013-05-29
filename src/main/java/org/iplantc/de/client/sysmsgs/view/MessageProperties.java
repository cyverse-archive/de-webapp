package org.iplantc.de.client.sysmsgs.view;

import java.util.Date;

import org.iplantc.de.client.sysmsgs.model.Message;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * The properties class used by by the summary list view model.
 */
public interface MessageProperties extends PropertyAccess<Message> {
	
    /**
     * an instance of this interface
     */
	public static final MessageProperties INSTANCE = GWT.create(MessageProperties.class);
	
    /**
     * the unique identifier of the message
     */
	ModelKeyProvider<Message> id();
	
    /**
     * the time when the message becomes active
     */
    ValueProvider<Message, Date> activationTime();
	
    /**
     * an indication of whether or not the message is dismissible by the user.
     */
	ValueProvider<Message, Boolean> dismissible();
	
}
