package org.iplantc.de.client.viewer.views;

import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.models.diskresources.File;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.viewer.models.SeparatedText;
import org.iplantc.de.client.viewer.models.SeparatedTextAutoBeanFactory;
import org.iplantc.de.client.viewer.models.SeparatedTextData;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.LiveGridView;

public class SeparatedTextViewer implements FileViewer {

    private Grid<SeparatedText> grid;
    private SeparatedTextViewToolBar toolbar;
    private ListStore<SeparatedText> store;
    private LiveGridView<SeparatedTextViewer> liveView;
    private BorderLayoutContainer container;
    private ContentPanel north;
    private ContentPanel center;
    private final File file;
    private final String infoType;
    private final String COMMA_SEPARATOR = ",";
    private final String TAB_SEPARATOR = "\t";
    private final String LINE_ENDING = "\n";

    private final SeparatedTextAutoBeanFactory factory = GWT.create(SeparatedTextAutoBeanFactory.class);

    public SeparatedTextViewer(File file, String infoType) {
        this.file = file;
        this.infoType = infoType;
        initLayout();
        initToolbar();
        getData();
    }

    private void initLayout() {
        container = new BorderLayoutContainer();
        north = new ContentPanel();
        center = new ContentPanel();
        container.setNorthWidget(north, getNorthData());
        container.setCenterWidget(center, getCenterData());
    }

    private void initToolbar() {
        toolbar = new SeparatedTextViewToolBar();
        north.add(toolbar);
    }

    private BorderLayoutData getNorthData() {
        BorderLayoutData northData = new BorderLayoutData(30);
        northData.setMargins(new Margins(5));
        northData.setCollapsible(true);
        northData.setSplit(false);
        return northData;
    }

    private MarginData getCenterData() {
        return new MarginData();
    }

    @Override
    public void setPresenter(Presenter p) {
        // TODO Auto-generated method stub

    }

    private void getData() {
        String url = "read-csv-chunk";
        container.mask(I18N.DISPLAY.loadingMask());
        Services.FILE_EDITOR_SERVICE.getDataChunk(url, getRequestBody(), new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                AutoBean<SeparatedTextData> bean = AutoBeanCodex.decode(factory,
                        SeparatedTextData.class, result);
                container.unmask();
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.unableToRetrieveFileData(file.getName()), caught);
                container.unmask();
            }
        });
    }

    private JSONObject getRequestBody() {
        JSONObject obj = new JSONObject();
        obj.put("path", new JSONString(file.getId()));
        obj.put("separator", new JSONString(getSeparator()));
        obj.put("line-ending", new JSONString(LINE_ENDING));
        // position starts at 0
        obj.put("position", new JSONString("0"));
        obj.put("chunk-size", new JSONString("" + toolbar.getPageSize()));
        return obj;
    }

    private String getSeparator() {
        if (infoType.equalsIgnoreCase("csv")) {
            return COMMA_SEPARATOR;
        } else if (infoType.equalsIgnoreCase("tsv")) {
            return TAB_SEPARATOR;
        } else {
            return " ";
        }
    }

    @Override
    public void setData(Object data) {
        // TODO Auto-generated method stub

    }

    @Override
    public Widget asWidget() {
        // TODO Auto-generated method stub
        return null;
    }

}
