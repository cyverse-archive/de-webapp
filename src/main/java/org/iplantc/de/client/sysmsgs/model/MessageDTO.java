package org.iplantc.de.client.sysmsgs.model;

import java.util.Date;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * TODO document
 */
interface MessageDTO {

	@PropertyName("deactivation_date")
	Date getDeactivationTime();

	@PropertyName("dismissable")
	boolean isDismissable();
	
	@PropertyName("activation_date")
	Date getActivationTime();
	
	@PropertyName("date_created")
	Date getCreationTime();
	
	@PropertyName("uuid")
	String getId();
	
	@PropertyName("type")
	String getType();
	
	@PropertyName("message")
	String getBody();
	
	@PropertyName("logins_disabled")
	boolean loginsDisabled();

}
