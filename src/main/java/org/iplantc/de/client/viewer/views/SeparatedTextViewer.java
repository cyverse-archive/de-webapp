package org.iplantc.de.client.viewer.views;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.models.diskresources.File;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.viewer.models.SeparatedText;
import org.iplantc.de.client.viewer.models.SeparatedTextAutoBeanFactory;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.LiveGridView;

public class SeparatedTextViewer implements FileViewer {

    private Grid<JSONObject> grid;
    private SeparatedTextViewToolBar toolbar;
    private ListStore<JSONObject> store;
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
        north.setCollapsible(false);
        north.setHeaderVisible(false);

        center = new ContentPanel();
        center.setCollapsible(false);
        center.setHeaderVisible(false);

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
                AutoBean<SeparatedText> bean = AutoBeanCodex
                        .decode(factory, SeparatedText.class, result);
                SeparatedText text_bean = bean.as();
                if (grid == null) {
                    initGrid(Integer.parseInt(text_bean.getMaxColumns()));
                }
                Splittable sp = StringQuoter.split(result);
                setData(sp);
                container.unmask();
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.unableToRetrieveFileData(file.getName()), caught);
                container.unmask();
            }
        });
    }

    private void initGrid(int columns) {
        List<ColumnConfig<JSONObject, ?>> configs = new ArrayList<ColumnConfig<JSONObject, ?>>();
        if (columns > 0) {
            for (int i = 0; i < columns; i++) {
                final int index = i;
                ColumnConfig<JSONObject, String> col = new ColumnConfig<JSONObject, String>(
                        new ValueProvider<JSONObject, String>() {

                            @Override
                            public String getValue(JSONObject object) {
                                return object.get(index + "").isString().stringValue();
                            }

                            @Override
                            public void setValue(JSONObject object, String value) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public String getPath() {
                                // TODO Auto-generated method stub
                                return null;
                            }
                        });
                col.setHeader("" + (index + 1));
                configs.add(col);
            }
        }

        grid = new Grid<JSONObject>(getStore(), new ColumnModel<JSONObject>(configs));
        center.add(grid);
    }

    private ListStore<JSONObject> getStore() {
        if (store == null) {
            store = new ListStore<JSONObject>(new ModelKeyProvider<JSONObject>() {

                private int index;

                @Override
                public String getKey(JSONObject item) {
                    return index++ + "";
                }

            });
        }

        return store;
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
        Splittable textData = (Splittable)data;
        JSONObject obj = JsonUtil.getObject(textData.getPayload());
        JSONArray arr = obj.get("csv").isArray();

        if (arr != null && arr.size() > 0) {
            for (int i = 0; i < arr.size(); i++) {
                store.add(arr.get(i).isObject());
            }
        }

    }

    @Override
    public Widget asWidget() {
        SimpleContainer widget = new SimpleContainer();
        widget.add(container);
        return widget;
    }

}
