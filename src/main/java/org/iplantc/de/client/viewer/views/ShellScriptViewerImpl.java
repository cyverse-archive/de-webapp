package org.iplantc.de.client.viewer.views;

import org.iplantc.de.client.models.diskResources.File;

import com.google.gwt.core.client.JavaScriptObject;

import com.sencha.gxt.core.client.dom.XElement;

public class ShellScriptViewerImpl extends TextViewerImpl {

    public ShellScriptViewerImpl(File file, boolean editing) {
        super(file, "", editing);
    }

    @Override
    public void setData(Object data) {
        clearDisplay();
        jso = displayData(center.getElement(), (String)data, center.getElement().getOffsetWidth(),
                center.getElement().getOffsetHeight(), toolbar.isWrapText(), editing);
    }

    public static native JavaScriptObject displayData(XElement textArea, String val, int width,
            int height, boolean wrap, boolean editing) /*-{
		var myCodeMirror = $wnd.CodeMirror(textArea, {
			value : val,
			mode : 'shell',
			lineNumbers : true
		});
		myCodeMirror.setOption("lineWrapping", wrap);
		myCodeMirror.setSize(width, height);
		myCodeMirror.setOption("readOnly", editing);

		return myCodeMirror;
    }-*/;

}
