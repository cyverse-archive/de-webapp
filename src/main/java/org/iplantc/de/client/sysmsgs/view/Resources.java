package org.iplantc.de.client.sysmsgs.view;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {

	public interface MessageCellStyle extends CssResource {
		
		String dismiss();
		
		String dismissOnHover();
		
	}
	
	public static final Resources INSTANCE = GWT.create(Resources.class);
	
	@Source("button_exit.png")
	ImageResource dismissImg();
	
	@Source("button_exit_hover.png")
	ImageResource dismissOnHoverImg();
	
	@Source("MessageSummaryCell.css")
	MessageCellStyle messageCellCSS();

}
