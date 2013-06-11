package org.iplantc.de.server;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.iplantc.de.server.service.DonkeyClient;
import org.iplantc.de.shared.services.MultiPartServiceWrapper;
import org.iplantc.de.shared.services.ServiceCallWrapper;

/**
 * A class to accept files from the client.
 * 
 * This class extends the UploadAction class provided by the GWT Upload library. The executeAction method
 * must be overridden for custom behavior.
 * 
 * @author sriram
 * 
 */
@SuppressWarnings("nls")
public class NewToolRequestServlet extends UploadAction {
    private static final long serialVersionUID = 1L;

    /**
     * The logger for error and informational messages.
     */
    private static Logger LOG = Logger.getLogger(NewToolRequestServlet.class);

    /**
     * The configuration settings for the application.
     */
    private DiscoveryEnvironmentProperties deProps;

    /**
     * Used to communicate with Donkey services.
     */
    private DonkeyClient donkeyClient;

    public static final String USER_ID = "user";
    public static final String EMAIL = "email";

    protected String user;
    protected String email;
    protected JSONObject jsonErrors;
    protected JSONObject jsonInfo;

    private ServiceCallResolver serviceResolver;

    /**
     * The default constructor.
     */
    public NewToolRequestServlet() {
    }

    /**
     * @param serviceResolver used to resolve calls to aliased services.
     * @param props the configuration settings for the application.
     * @param donkeyClient the client used to communicate with Donkey services.
     */
    public NewToolRequestServlet(ServiceCallResolver serviceResolver,
            DiscoveryEnvironmentProperties deProps, DonkeyClient donkeyClient) {
        this.serviceResolver = serviceResolver;
        this.deProps = deProps;
    }

    /**
     * Initializes the servlet.
     * 
     * @throws ServletException if the servlet can't be initialized.
     * @throws IllegalStateException if the configuration settings or the iPlant e-mail client can't be
     *             found.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        if (serviceResolver == null && deProps == null) {
            this.serviceResolver = ServiceCallResolver.getServiceCallResolver(getServletContext());
            this.deProps = DiscoveryEnvironmentProperties
                    .getDiscoveryEnvironmentProperties(getServletContext());
        }
        donkeyClient = DonkeyClient.securedClient(deProps.getProtectedDonkeyBaseUrl());
    }

    /**
     * Performs the necessary operations for an upload action.
     * 
     * @param request the HTTP request associated with the action.
     * @param fileItems the file associated with the action.
     * @return a string representing data in JSON format.
     * @throws UploadActionException if there is an issue invoking the dispatch to the servlet
     */
    @Override
    public String executeAction(HttpServletRequest request, List<FileItem> fileItems) {
        String userHome = getUserHomeDir(request);
        if (userHome == null) {
            jsonErrors.put("error", "Unable to retrieve user home");
            return jsonErrors.toString();
        }
        jsonErrors = new JSONObject();
        jsonInfo = new JSONObject();
        InputStream bodyFile;
        LOG.debug("Upload Action started.");
        long fileLength;
        String mimeType;

        user = getUserName(fileItems);
        email = getUserEmail(fileItems);
        jsonInfo.put(USER_ID, user);
        jsonInfo.put(EMAIL, email);

        for (FileItem item : fileItems) {
            String fileFieldName = item.getFieldName();

            if (item.getContentType() != null && (item.getSize() > 0)) {
                try {
                    fileLength = item.getSize();
                    mimeType = item.getContentType();
                    bodyFile = item.getInputStream();

                    jsonInfo.put(
                            fileFieldName,
                            invokeService(request, userHome, item.getName(), bodyFile, fileLength,
                                    mimeType));
                } catch (IOException e) {
                    LOG.error("executeAction - Exception while getting file input stream.", e);
                    jsonErrors.put("error", e.getMessage());

                    return jsonErrors.toString();
                } catch (IRODSConfigurationException e) {
                    LOG.error("executeAction - Exception while getting users IRODS home directory.", e);
                    jsonErrors.put("error", e.getMessage());

                    return jsonErrors.toString();
                } catch (UploadActionException e) {
                    LOG.error(
                            "executeAction - Exception while getting uploading files to users home directory.",
                            e);
                    jsonErrors.put("error", e.getMessage());

                    return jsonErrors.toString();
                }

            } else {
                String contents = new String(item.get());
                jsonInfo.put(fileFieldName, contents);
            }
        }

        if (!jsonErrors.containsKey("error")) {
            try {
                donkeyClient.put(request, "tool-request", jsonInfo.toString());
                jsonErrors.put("success", "Your tool request was successfully submitted.");
            } catch (Exception e) {
                LOG.error(
                        "executeAction - Exception while sending email to support about tool request.",
                        e);
                jsonErrors.put("error", e.getMessage() == null ? e.toString() : e.getMessage());
            }
        }

        // remove files from session. this avoids duplicate submissions
        removeSessionFileItems(request, false);

        LOG.debug("executeAction - JSON returned: " + jsonErrors);
        return jsonErrors.toString();

    }

    private String getUserName(List<FileItem> fileItems) {
        String username = null;
        for (FileItem item : fileItems) {
            if (item.isFormField()) {
                String fieldName = item.getFieldName();
                byte[] contents = item.get();

                if (fieldName.equals(USER_ID)) {
                    username = new String(contents);
                    break;
                }
            }
        }
        return username;
    }

    private String getUserEmail(List<FileItem> fileItems) {
        String emailAddress = "";
        for (FileItem item : fileItems) {
            if (item.isFormField()) {
                String fieldName = item.getFieldName();
                if (fieldName.equals(EMAIL)) {
                    emailAddress = item.getString();
                    break;
                }
            }
        }
        return emailAddress;
    }

    /**
     * Handles the invocation of the file upload service.
     * 
     * @param request current HTTP request
     * @param type the file type. It can be AUTO or CSVNAMELIST
     * @param filename the name of the file being uploaded
     * @param fileContents the content of the file
     * @param fileLength the length of the file being uploaded.
     * @param mimeType content mime type
     * @return a string representing data in JSON format.
     * @throws UploadActionException if there is an issue invoking the dispatch to the servlet
     * @throws IRODSConfigurationException if there is a problem with irods config.
     */
    private String invokeService(HttpServletRequest request, String userHome, String filename,
            InputStream fileContents, long fileLength, String mimeType) throws UploadActionException,
            IRODSConfigurationException {
        String fileUrl = null;

        MultiPartServiceWrapper wrapper = createServiceWrapper(userHome, filename, fileLength, mimeType,
                fileContents);

        try { // call the RESTful service and get the results.
            DataApiServiceDispatcher dispatcher = new DataApiServiceDispatcher(serviceResolver);
            dispatcher.init(getServletConfig());
            dispatcher.setRequest(request);

            LOG.debug("invokeService - Making service call.");
            String response = dispatcher.getServiceData(wrapper);
            LOG.debug(response);

            fileUrl = extractUploadedUrl(response);
        } catch (Exception e) {
            LOG.error("unable to upload file", e); //$NON-NLS-1$

            UploadActionException uploadException = new UploadActionException(e.getMessage());
            uploadException.initCause(e);

            throw uploadException;
        }

        return fileUrl;
    }

    private String extractUploadedUrl(String json) {
        JSONObject jsonObj = JSONObject.fromObject(json);
        if (jsonObj != null) {
            JSONObject file = jsonObj.getJSONObject("file");
            if (file != null) {
                return file.getString("id");
            }
        }

        return null;
    }

    /**
     * Constructs and configures a multi-part service wrapper.
     * 
     * @param path the folder identifier for where the file will be created
     * @param filename the name of the file being uploaded
     * @param fileContents the content of the file
     * @return an instance of a multi-part service wrapper.
     */
    private MultiPartServiceWrapper createServiceWrapper(String path, String filename, long fileLength,
            String mimeType, InputStream fileContents) {
        // address key that is resolved by the service dispatcher
        String address = deProps.getUnprotectedFileIoBaseUrl() + "upload";

        MultiPartServiceWrapper wrapper = new MultiPartServiceWrapper(MultiPartServiceWrapper.Type.POST,
                address);

        wrapper.addPart(new FileHTTPPart(fileContents, "file", filename, mimeType, fileLength)); //$NON-NLS-1$
        wrapper.addPart(path, "dest");

        return wrapper;
    }

    private String getUserHomeDir(HttpServletRequest request) {
        // address key that is resolved by the service dispatcher
        String address = deProps.getDataMgmtServiceBaseUrl() + "home";

        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        String homeDir = null;

        try {
            CasServiceDispatcher dispatcher = new CasServiceDispatcher(serviceResolver);
            dispatcher.init(getServletConfig());
            dispatcher.setRequest(request);
            homeDir = dispatcher.getServiceData(wrapper);
            LOG.debug("getUserHomeDir - Making service call.");
        } catch (Exception e) {
            LOG.error("getUserHomeDir - unable get users home dir", e);
        }
        return homeDir;
    }
}
