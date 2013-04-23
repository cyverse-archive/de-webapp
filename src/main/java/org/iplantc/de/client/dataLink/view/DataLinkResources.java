package org.iplantc.de.client.dataLink.view;


import org.iplantc.core.resources.client.IplantResources;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

interface DataLinkResources extends IplantResources {

    interface DataLinkPanelCellStyle extends CssResource {
        String dataLinkDelete();

        String dataLinkFileIcon();

        String pasteIcon();

        String dataLinkImfoImg();
    }

    @Source("DataLinkPanelCell.css")
    DataLinkResources.DataLinkPanelCellStyle css();

    @Source("images/link_add.png")
    ImageResource linkAdd();

    @Source("images/link_delete.png")
    ImageResource linkDelete();

    @Source("images/paste_plain.png")
    ImageResource paste();

    @Source("images/tree_collapse.png")
    ImageResource collapse();

    @Source("images/tree_expand.png")
    ImageResource expand();
}