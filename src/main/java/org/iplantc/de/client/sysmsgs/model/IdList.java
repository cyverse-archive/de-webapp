package org.iplantc.de.client.sysmsgs.model;

import java.util.List;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * TODO document
 */
public interface IdList {
	
	@PropertyName("uuids")
	List<String> getIds();
	
	@PropertyName("uuids")
	void setIds(List<String> ids);
	
}
