/**
 *
 */
package org.iplantc.de.client.viewer.views;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.models.UserInfo;
import org.iplantc.core.uicommons.client.models.diskresources.File;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.services.impl.FileSaveCallback;
import org.iplantc.de.client.viewer.events.SaveFileEvent;
import org.iplantc.de.client.viewer.events.SaveFileEvent.SaveFileEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/**
 * @author sriram
 * 
 */
public class TextViewerImpl extends AbstractFileViewer implements EditingSupport {

    private static TextViewerUiBinder uiBinder = GWT.create(TextViewerUiBinder.class);

    @UiTemplate("TextViewer.ui.xml")
    interface TextViewerUiBinder extends UiBinder<Widget, TextViewerImpl> {
    }

    private final Widget widget;

    @UiField
    SimpleContainer center;

    @UiField
    BorderLayoutContainer con;

    @UiField(provided = true)
    TextViewPagingToolBar toolbar;

    private long file_size;

    private int totalPages;

    private String data;

    protected boolean editing;

    protected JavaScriptObject jso;

    public TextViewerImpl(File file, String infoType, boolean editing) {
        super(file, infoType);
        this.editing = editing;
        toolbar = initToolBar();
        widget = uiBinder.createAndBindUi(this);

        addWrapHandler();

        if (file != null) {
            loadData();
        } else {
            setData("");
        }

        center.addResizeHandler(new ResizeHandler() {

            @Override
            public void onResize(ResizeEvent event) {
                if (jso != null) {
                    resizeDisplay(jso, center.getElement().getOffsetWidth(), center.getElement()
                            .getOffsetHeight());
                }
            }
        });
    }

    TextViewPagingToolBar initToolBar() {
        TextViewPagingToolBar textViewPagingToolBar = new TextViewPagingToolBar(this, editing);
        textViewPagingToolBar.addHandler(new SaveFileEventHandler() {

            @Override
            public void onSave(SaveFileEvent event) {
                save();

            }

        }, SaveFileEvent.TYPE);
        return textViewPagingToolBar;
    }

    private void addWrapHandler() {
        toolbar.addWrapCbxChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                setData(data);
            }
        });
    }

    private JSONObject getRequestBody() {
        JSONObject obj = new JSONObject();
        obj.put("path", new JSONString(file.getId()));
        // position starts at 0
        obj.put("position", new JSONString("" + toolbar.getPageSize() * (toolbar.getPageNumber() - 1)));
        obj.put("chunk-size", new JSONString("" + toolbar.getPageSize()));
        return obj;
    }

    @Override
    public void loadData() {
        String url = "read-chunk";
        con.mask(I18N.DISPLAY.loadingMask());
        Services.FILE_EDITOR_SERVICE.getDataChunk(url, getRequestBody(), new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                data = JsonUtil.getString(JsonUtil.getObject(result), "chunk");
                setData(data);
                con.unmask();
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.unableToRetrieveFileData(file.getName()), caught);
                con.unmask();
            }
        });

    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setPresenter(Presenter p) {/* Not Used */
    }

    @Override
    public void setData(Object data) {
        clearDisplay();
        jso = displayData(center.getElement(), infoType, (String)data, center.getElement()
                .getOffsetWidth(), center.getElement().getOffsetHeight(), toolbar.isWrapText(), editing);
    }

    protected void clearDisplay() {
        center.getElement().removeChildren();
        center.forceLayout();
    }

    public static native JavaScriptObject displayData(XElement textArea, String mode, String val,
            int width, int height, boolean wrap, boolean editing) /*-{
		var myCodeMirror = $wnd.CodeMirror(textArea, {
			value : val,
			mode : mode
		});
		myCodeMirror.setOption("lineWrapping", wrap);
		myCodeMirror.setSize(width, height);
		if (editing) {
			myCodeMirror.setOption("readOnly", false);
		} else {
			myCodeMirror.setOption("readOnly", true);
		}
		return myCodeMirror;
    }-*/;

    public static native String getEditorContent(JavaScriptObject jso) /*-{
		return jso.getValue();
    }-*/;

    public static native void resizeDisplay(JavaScriptObject jso, int width, int height) /*-{
		jso.setSize(width, height);
    }-*/;

    @Override
    public void save() {
        System.out.println(getEditorContent(jso));
        String path = (file != null) ? file.getPath() : JsonUtil.trim(UserInfo.getInstance()
                .getHomePath()) + "/" + getViewName();
        Services.FILE_EDITOR_SERVICE.uploadTextAsFile(path, getEditorContent(jso), new FileSaveCallback(
                path, con));
    }
}
