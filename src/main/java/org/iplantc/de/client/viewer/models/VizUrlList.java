/**
 * 
 */
package org.iplantc.de.client.viewer.models;

import java.util.List;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * @author sriram
 * 
 */
public interface VizUrlList {

    @PropertyName("tree-urls")
    List<VizUrl> getUrls();

}
