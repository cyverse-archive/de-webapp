package org.iplantc.de.client.sysmsgs.view;

import org.iplantc.de.client.sysmsgs.model.Message;
import org.iplantc.de.client.sysmsgs.view.Resources.MessageCellStyle;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.sencha.gxt.widget.core.client.ListViewSelectionModel;
import com.sencha.gxt.widget.core.client.event.XEvent;

public final class SelectionModel extends ListViewSelectionModel<Message> {

	private static final MessageCellStyle CSS;

    static {
    	CSS = Resources.INSTANCE.messageCellCSS();
    	CSS.ensureInjected();
    }
 
	@Override
	protected void handleMouseDown(final MouseDownEvent mouseEvent) {
	    final XEvent event = mouseEvent.getNativeEvent().<XEvent> cast();
	    if (!event.getEventTargetEl().hasClassName(CSS.dismiss())) {
	    	super.handleMouseDown(mouseEvent);
	    }
	}
	
}

