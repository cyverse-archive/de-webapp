package org.iplantc.de.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.iplantc.de.shared.services.AboutApplicationService;

/**
 * Communicates application information to include as "about" data regarding the current build and client
 * UserAgent.
 *
 * This servlet will include information about services once the modeling of software components has been
 * completed.
 *
 * @see org.iplantc.de.client.services.AboutApplicationService
 * @author lenards
 */
@SuppressWarnings("nls")
public class AboutApplicationServlet extends RemoteServiceServlet implements AboutApplicationService {

    /**
     * Generated Unique Identifier for serialization.
     */
    private static final long serialVersionUID = 6046105023536377635L;

    private static final String MANIFEST_LOC = "/META-INF/MANIFEST.MF";
    private static final String BUILD_NUMBER_ATTRIBUTE = "Hudson-Build-Number";

    /**
     * The release version will eventually come from the metadata service.
     */
    private static final String RELEASE_VERSION = DiscoveryEnvironmentProperties.getReleaseVersion();

    private static final String DEFAULT_BUILD_NUMBER = DiscoveryEnvironmentProperties
            .getDefaultBuildNumber();

    private static final String DEFAULT_RELEASE_VERSION = "unversioned";

    /**
     * The logger for error and informational messages.
     */
    private static Logger LOG = Logger.getLogger(AboutApplicationServlet.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAboutInfo() {
        return produceInfo();
    }

    private String produceInfo() {
        String buildNumber = getBuildNumberFromManifest();
        LOG.debug("Build number stored in the MANIFEST.MF is " + buildNumber);
        String relVersion = getReleaseVersion();

        // at some point the information gathered here will be more involved as it will
        // include versioning about the software components in use (bwa, cufflinks, etc)
        String json = "{ \"buildnumber\": \"" + buildNumber + "\", \"release\": \"" + relVersion
                + "\" }";
        LOG.debug("the about application JSON is: " + json);

        return json;
    }

    /**
     * Get the release version that is stored in the application properties file.
     *
     * @return a string representation of the release version.
     */
    private String getReleaseVersion() {
        String version = RELEASE_VERSION;
        return (StringUtils.isNotEmpty(version)) ? version : DEFAULT_RELEASE_VERSION;
    }

    /**
     * Get the build number that is stored in the archive's manifest file.
     *
     * @return a string representation of the build number.
     */
    private String getBuildNumberFromManifest() {
        String buildNumber = DEFAULT_BUILD_NUMBER;
        Manifest manifest;
        try {
            manifest = new Manifest(getServletContext().getResourceAsStream(MANIFEST_LOC));
            Attributes attrs = manifest.getMainAttributes();
            buildNumber = attrs.getValue(BUILD_NUMBER_ATTRIBUTE);
            if (StringUtils.isEmpty(buildNumber)) {
                buildNumber = DEFAULT_BUILD_NUMBER;
            }
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }
        return buildNumber;
    }
}