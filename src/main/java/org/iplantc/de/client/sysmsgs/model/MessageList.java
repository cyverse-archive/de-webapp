package org.iplantc.de.client.sysmsgs.model;

import java.util.List;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * TODO document
 */
public interface MessageList {

	@PropertyName("system-messages")
	List<Message> getList();
	
}
