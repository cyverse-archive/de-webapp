/**
 * 
 */
package org.iplantc.de.client.views;

import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;

/**
 * @author sriram
 * 
 */
public class DEPagingToolbar extends PagingToolBar {

    public DEPagingToolbar(int pageSize) {
        super(pageSize);
    }

    public Button getRefreshButton() {
        return refresh;
    }

}
