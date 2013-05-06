package org.iplantc.de.server;

import org.iplantc.de.client.services.DEFeedbackService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DEFeedbackServiceImpl extends RemoteServiceServlet implements DEFeedbackService {

    /**
     * 
     */
    private static final long serialVersionUID = -7150190336849965977L;

    @Override
    public String submitFeedback(String jsonFeedback) {
        System.out.println("-->" + jsonFeedback.toString());
        return null;
    }

}
