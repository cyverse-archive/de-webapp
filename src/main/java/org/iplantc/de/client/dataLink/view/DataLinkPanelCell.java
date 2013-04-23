package org.iplantc.de.client.dataLink.view;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import org.iplantc.core.uidiskresource.client.models.DiskResource;
import org.iplantc.core.uidiskresource.client.models.File;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.dataLink.models.DataLink;

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
import com.google.gwt.user.client.Event;

final class DataLinkPanelCell<M extends DiskResource> extends AbstractCell<M> {
    
    interface Templates extends SafeHtmlTemplates {
        
        // TODO JDS The image which would be clicked on for copy to clipboard would be appended to the following template definition.
        @SafeHtmlTemplates.Template("<span name=\"del\" class=\"{0}\" qtip=\"{1}\"></span><span style=\"float: left;\">&nbsp; {2} &nbsp;</span> <span id=\"{3}\" class=\"{4}\"></span>")
        SafeHtml dataLinkCellWithCopyIcon(String delImgClassName, String delImgToolTip, SafeHtml urlText, String copyToClipId, String copyImg);

        @SafeHtmlTemplates.Template("<span name=\"del\" class=\"{0}\" qtip=\"{1}\"></span><span style=\"float: left;\">&nbsp; {2} &nbsp;</span>")
        SafeHtml dataLinkCell(String delImgClassName, String delImgToolTip, SafeHtml urlText);
        
        @SafeHtmlTemplates.Template("<span name=\"fileIcon\" class=\"{0}\"></span> <span>&nbsp; {1}</span>")
        SafeHtml diskResCell(String fileIconImgClass, SafeHtml fileName);
    }

    private static String dataLinkUrlPrefix;
    private final Templates templates = GWT.create(Templates.class);
    private final DataLinkResources res = GWT.create(DataLinkResources.class);
    private final DataLinkPanel.Presenter<M> presenter;
    

    DataLinkPanelCell(DataLinkPanel.Presenter<M> presenter) {
        super(CLICK);
        this.presenter = presenter;
        res.css().ensureInjected();
        
        // Fetch the configured URL prefix for the DataLink URL.
        dataLinkUrlPrefix = presenter.getDataLinkUrlPrefix();
    }
    

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, M value,
            SafeHtmlBuilder sb) {
        
        if(value instanceof DataLink){
            String copyToClipId = "clip-id-" + value.getId();
            SafeHtml dataLinkText = SafeHtmlUtils.fromString(dataLinkUrlPrefix + value.getId());
            // sb.append(templates.dataLinkCellWithCopyIcon(res.css().dataLinkDelete(),
            // I18N.DISPLAY.deleteDataLinkToolTip(),
            // dataLinkText,
            // copyToClipId,
            //                      res.css().pasteIcon()));
            sb.append(templates.dataLinkCell(res.css().dataLinkDelete(), 
                    I18N.DISPLAY.deleteDataLinkToolTip(), 
                    dataLinkText));
            
        }else if(value instanceof File){
            sb.append(templates.diskResCell(res.css().dataLinkFileIcon(), SafeHtmlUtils.fromString(value.getName())));
        }else{
            
        }
        
    }
    
    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, M value, NativeEvent event,
            ValueUpdater<M> valueUpdater) {
        
        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        if (parent.isOrHasChild(eventTarget)) {

            switch (Event.as(event).getTypeInt()) {
                case Event.ONCLICK:
                    doOnClick(eventTarget, value, valueUpdater);
                    break;
                default:
                    break;
            }
        }
    }


    private void doOnClick(Element eventTarget, M value, ValueUpdater<M> valueUpdater) {
        if(eventTarget.getAttribute("name").equalsIgnoreCase("del")){
            presenter.deleteDataLink((DataLink)value);
        }
        
    }
    
}