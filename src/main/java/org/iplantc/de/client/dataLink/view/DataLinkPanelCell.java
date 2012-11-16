package org.iplantc.de.client.dataLink.view;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import org.iplantc.core.uidiskresource.client.models.File;
import org.iplantc.core.uidiskresource.client.models.IDiskResource;
import org.iplantc.de.client.dataLink.models.DataLink;
import org.iplantc.de.client.images.Resources;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.ImageResourceRenderer;

final class DataLinkPanelCell<M extends IDiskResource> extends AbstractCell<M> {
    interface Templates extends SafeHtmlTemplates {
        
        // TODO JDS The image which would be clicked on for copy to clipboard would be appended to the following template definition.
        @SafeHtmlTemplates.Template("<span qtip=\"{1}\">{0}</span>")
        SafeHtml dataLinkCell(String urlText, String toolTip);
    }

    private static ImageResourceRenderer imgRenderer;
    private static String dataLinkUrlPrefix;
    private final Templates templates = GWT.create(Templates.class);
    
    DataLinkPanelCell() {
        super(CLICK);
        if (imgRenderer == null) {
            imgRenderer = new ImageResourceRenderer();
        }
        
        // TODO JDS Fetch the configured URL prefix for the DataLink url
        dataLinkUrlPrefix = "http://sample.com/";
    }
    

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, M value,
            SafeHtmlBuilder sb) {
        
        if(value instanceof DataLink){
            sb.append(templates.dataLinkCell(SafeHtmlUtils.fromString(dataLinkUrlPrefix + value.getId()).asString() , ""));
            
        }else if(value instanceof File){
            sb.append(imgRenderer.render(Resources.ICONS.file()));
            sb.append(SafeHtmlUtils.fromSafeConstant("&nbsp;"));
            sb.append(SafeHtmlUtils.fromString(value.getName()));
        }else{
            
        }
        
    }
    
    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, M value, NativeEvent event,
            ValueUpdater<M> valueUpdater) {
        
    }
}