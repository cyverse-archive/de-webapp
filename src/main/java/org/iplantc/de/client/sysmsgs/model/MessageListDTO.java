package org.iplantc.de.client.sysmsgs.model;

import java.util.List;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * TODO document
 */
public interface MessageListDTO {

	@PropertyName("system-messages")
	List<MessageDTO> getList();
	
}
