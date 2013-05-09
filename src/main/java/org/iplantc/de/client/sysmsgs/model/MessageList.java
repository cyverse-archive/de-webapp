package org.iplantc.de.client.sysmsgs.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * TODO document
 */
public interface MessageList {

	static final class Category {
	
		public static void sortById(AutoBean<MessageList> instance) {
			final List<Message> msgs = instance.as().getList();
			Collections.sort(msgs, new Comparator<Message>() {
				@Override
				public int compare(final Message lhs, final Message rhs) {
					return lhs.getId().compareTo(rhs.getId());
				}});
			instance.as().setList(msgs);
		}
		
	}
	
	void sortById();
		
	@PropertyName("system-messages")
	List<Message> getList();
	
	@PropertyName("system-messages")
	void setList(List<Message> messages);
	
}
