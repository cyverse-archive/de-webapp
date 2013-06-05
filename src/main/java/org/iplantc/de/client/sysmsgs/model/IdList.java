package org.iplantc.de.client.sysmsgs.model;

import java.util.List;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * Represents a JSON document containing a list of UUIDS.
 */
public interface IdList {
	
	@PropertyName("uuids")
	List<String> getIds();
	
	@PropertyName("uuids")
	void setIds(List<String> ids);
	
}
