/**
 * 
 */
package org.iplantc.de.client.models;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A javascript object for search result
 * 
 * @author sriram
 * 
 */
public class JsSearchResult extends JavaScriptObject {

    protected JsSearchResult() {

    }

    public final native String getName() /*-{
		return this.name;
    }-*/;

    public final native String getId() /*-{
		return this._id;
    }-*/;

    public final native String getType() /*-{
		return this._type;
    }-*/;

    public final native String getUser() /*-{
		return this.user;
    }-*/;

    public final native String getIndex() /*-{
		return this._index;
    }-*/;

    public final native String getScore() /*-{
		return this._score;
    }-*/;

}
