package org.iplantc.de.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DEFeedbackServiceAsync {

    void submitFeedback(String feedback, AsyncCallback<String> callback);

}
