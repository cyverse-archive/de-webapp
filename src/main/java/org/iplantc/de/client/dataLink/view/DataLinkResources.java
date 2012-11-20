package org.iplantc.de.client.dataLink.view;

import org.iplantc.de.client.images.Icons;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

interface DataLinkResources extends Icons{
    
    interface DataLinkPanelCellStyle extends CssResource{
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
    
    @Source("images/bullet_toggle_minus.png")
    ImageResource collapse();

    @Source("images/bullet_toggle_plus.png")
    ImageResource expand();
}