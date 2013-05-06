package org.iplantc.de.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("feedbackService")
public interface DEFeedbackService extends RemoteService {

    public String submitFeedback(String feedback);
}
