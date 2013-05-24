package org.iplantc.de.client.sysmsgs.view;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * This defines the resources required for displaying system messages.
 */
public interface Resources extends ClientBundle {

    /**
     * This is the interface of the style used for rendering the system messages view.
     */
	public interface Style extends CssResource {
        /**
         * The styling of the dismiss button
         */
		String dismiss();

        /**
         * The styling of a message that has already been seen
         */
        String seenMessageSummary();

        /**
         * The styling of a message that has not already been seen
         */
        String unseenMessageSummary();
	}
	
    /**
     * The image used to render a dismiss button when the mouse is not over the button.
     */
	@Source("button_exit.png")
	ImageResource dismissImg();

    /**
     * The image used to render a dismiss button when the mouse is over the button.
     */
	@Source("button_exit_hover.png")
	ImageResource dismissOnHoverImg();
	
    /**
     * The style used to render the system messages view.
     */
    @Source("MessagesView.css")
	Style style();

}
