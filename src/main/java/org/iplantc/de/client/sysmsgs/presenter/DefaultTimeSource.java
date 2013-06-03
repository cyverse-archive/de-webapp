package org.iplantc.de.client.sysmsgs.presenter;

import java.util.Date;

/**
 * Generates the current time using the default Date() constructor.
 */
final class DefaultTimeSource implements ProvidesTime {

    /**
     * @see ProvidesTime#now()
     */
    @Override
    public Date now() {
        return new Date();
    }

}
