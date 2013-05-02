package org.iplantc.de.client.sysmsgs.model;

import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * TODO document
 */
public interface MessageFactory extends AutoBeanFactory {

	static final MessageFactory INSTANCE = GWT.create(MessageFactory.class);

	AutoBean<IdList> makeIdList();

	AutoBean<IdList> makeIdList(IdList lst);

	AutoBean<Message> makeMessage();
	
	AutoBean<MessageList> makeMessageList();
		
	AutoBean<User> makeUser();
	
}
