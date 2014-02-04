package org.iplantc.de.client.desktop.services;

import org.iplantc.de.commons.client.DEServiceFacade;
import org.iplantc.de.commons.client.models.DEProperties;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Provides access to remote services for submitting user feedback.
 */
@SuppressWarnings("nls")
public class DEFeedbackServiceFacade {

    private static String FEEDBACK_SERVICE_PATH = "feedback";

    /**
     * Submits Discovery Environment feedback on behalf of the user.
     * 
     * @param feedback the feedback in the form of a JSON object.
     * @param callback executed when the RPC call completes.
     */
    public void submitFeedback(String feedback, AsyncCallback<String> callback) {
        String addr = DEProperties.getInstance().getMuleServiceBaseUrl() + FEEDBACK_SERVICE_PATH;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT, addr, feedback);
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }
}
