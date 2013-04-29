package org.iplantc.de.client.sysmsgs.model;

import java.util.Date;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * TODO document
 */
public interface MessageDTO {

	@PropertyName("uuid")
	String getId();
	
	@PropertyName("type")
	String getType();
	
	@PropertyName("date_created")
	Date getCreationTime();
	
	@PropertyName("activation_date")
	Date getActivationTime();
	
	@PropertyName("deactivation_date")
	Date getDeactivationTime();

	@PropertyName("seen")
	boolean isSeen();
	
	@PropertyName("seen")
	void setSeen(boolean seen);
	
	@PropertyName("dismissable")
	boolean isDismissable();
	
	@PropertyName("logins_disabled")
	boolean loginsDisabled();

	@PropertyName("message")
	String getBody();
	
}
