/**
 * 
 */
package org.iplantc.de.client.views.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.models.UserInfo;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.models.Collaborator;
import org.iplantc.de.client.models.JsCollaborators;
import org.iplantc.de.client.services.UserSessionServiceFacade;
import org.iplantc.de.client.utils.NotifyInfo;
import org.iplantc.de.client.views.panels.ManageCollaboratorsPanel.MODE;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

/**
 * @author sriram
 * 
 */
public class CollaboratorsPanel extends ContentPanel {

    private Grid<Collaborator> grid;
    private ManageCollaboratorsPanel.MODE mode;

    // list that holds user's collaborators
    private List<Collaborator> my_collaborators;

    public CollaboratorsPanel(String title, ManageCollaboratorsPanel.MODE mode, int width, int height) {
        setHeading(title);
        setSize(width, height);
        setLayout(new FitLayout());
        setBodyBorder(false);
        setBorders(false);
        this.mode = mode;
        init();
    }

    private void init() {

        my_collaborators = new ArrayList<Collaborator>();
        ListStore<Collaborator> store = new ListStore<Collaborator>();
        grid = new Grid<Collaborator>(store, buildColumnModel());
        grid.setAutoExpandColumn(Collaborator.NAME);
        grid.setBorders(false);
        grid.getView().setEmptyText(I18N.DISPLAY.noCollaborators());

        add(grid);
        new GridDragSource(grid) {
            @Override
            protected void onDragStart(DNDEvent e) {
                e.setData(grid.getSelectionModel().getSelectedItems());
            }

            @Override
            protected void onDragDrop(DNDEvent e) {
                // do nothing intentionally
            }
        };

    }

    private ColumnModel buildColumnModel() {
        ColumnConfig name = new ColumnConfig(Collaborator.NAME, I18N.DISPLAY.name(), 150);
        name.setRenderer(new NameCellRenderer());

        ColumnConfig email = new ColumnConfig(Collaborator.EMAIL, I18N.DISPLAY.email(), 200);
        return new ColumnModel(Arrays.asList(name, email));

    }

    public void loadResults(List<Collaborator> collaborators) {
        // clear results before adding. Sort alphabetically
        ListStore<Collaborator> store = grid.getStore();
        store.removeAll();
        store.add(collaborators);
        store.sort(Collaborator.NAME, SortDir.ASC);
    }

    public List<Collaborator> parseResults(String result) {
        JSONObject obj = JSONParser.parseStrict(result).isObject();
        String json = obj.get("users").toString();
        JsArray<JsCollaborators> collabs = JsonUtil.asArrayOf(json);
        List<Collaborator> collaborators = new ArrayList<Collaborator>();
        for (int i = 0; i < collabs.length(); i++) {
            Collaborator c = new Collaborator(collabs.get(i));
            collaborators.add(c);
        }
        return collaborators;
    }

    public void clearStore() {
        grid.getStore().removeAll();
    }

    public void parseAndLoad(String result) {
        loadResults(parseResults(result));
    }

    public void setMode(ManageCollaboratorsPanel.MODE mode) {
        this.mode = mode;
    }

    /**
     * A custom renderer that renders with add / delete icon
     * 
     * @author sriram
     * 
     */
    private class NameCellRenderer implements GridCellRenderer<Collaborator> {

        private static final String REMOVE_BUTTON_STYLE = "remove_button";
        private static final String ADD_BUTTON_STYLE = "add_button";
        private static final String DELETE_BUTTON_STYLE = "delete_button";
        private static final String DONE_BUTTON_STYLE = "done_button";

        @Override
        public Object render(final Collaborator model, String property, ColumnData config, int rowIndex,
                int colIndex, ListStore<Collaborator> store, final Grid<Collaborator> grid) {

            final HorizontalPanel hp = new HorizontalPanel();

            if (mode.equals(MODE.SEARCH)) {
                if (!isCurrentCollaborator(model)) {
                    IconButton ib = buildButton(ADD_BUTTON_STYLE, model);
                    hp.add(ib);
                    ib.setToolTip("Add");
                } else {
                    IconButton ib = buildButton(DONE_BUTTON_STYLE, model);
                    hp.add(ib);
                    ib.setToolTip(I18N.DISPLAY.collaborators());
                }
            } else {
                IconButton ib = buildButton(REMOVE_BUTTON_STYLE, model);
                hp.add(ib);
                ib.setToolTip("Remove");
            }
            hp.sinkEvents(Events.OnMouseDown.getEventCode());
            hp.addListener(Events.OnMouseDown, new Listener<BaseEvent>() {

                @Override
                public void handleEvent(BaseEvent be) {
                    grid.getSelectionModel().select(false, model);
                }
            });

            hp.add(new Label(model.getName()));
            hp.setSpacing(3);
            // new DragSource(hp) {
            // @Override
            // protected void onDragStart(DNDEvent e) {
            // // List<Collaborator> list = new ArrayList<Collaborator>();
            // // list.add(model);
            // // e.setData(list);
            // }
            //
            // @Override
            // protected void onDragDrop(DNDEvent e) {
            // // do nothing intentionally
            // }
            // / };
            return hp;
        }

        private IconButton buildButton(final String style, final Collaborator model) {
            final IconButton btn = new IconButton(style, new SelectionListener<IconButtonEvent>() {

                @Override
                public void componentSelected(IconButtonEvent ce) {
                    IconButton src = (IconButton)ce.getSource();
                    String existing_style = src.getStyleName();
                    if (existing_style.contains(ADD_BUTTON_STYLE)) {
                        if (!checkCurrentUser(model)) {
                            addCollaborators(model);
                            if (mode.equals(MODE.SEARCH)) {
                                src.changeStyle(DONE_BUTTON_STYLE);
                                src.setToolTip("Added");
                            } else {
                                src.changeStyle(REMOVE_BUTTON_STYLE);
                                src.setToolTip("Remove");
                            }
                            return;
                        } else {
                            MessageBox.alert(I18N.DISPLAY.error(), I18N.ERROR.selfCollabAddError(), null);
                        }
                    }

                    if (existing_style.contains(REMOVE_BUTTON_STYLE)) {
                        src.changeStyle(DELETE_BUTTON_STYLE);
                        src.setToolTip(I18N.DISPLAY.confirmRemove());
                        return;
                    }

                    if (existing_style.contains(DELETE_BUTTON_STYLE)) {
                        removeCollaborators(model);
                        return;
                    }

                }
            });
            return btn;
        }
    }

    private boolean checkCurrentUser(Collaborator model) {
        if (model.getUserName().equalsIgnoreCase(UserInfo.getInstance().getUsername())) {
            return true;
        }

        return false;
    }

    private void addCollaborators(final Collaborator model) {
        UserSessionServiceFacade facade = new UserSessionServiceFacade();
        JSONObject obj = buildJSONModel(model);
        facade.addCollaborators(obj, new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {

                if (!isCurrentCollaborator(model)) {
                    my_collaborators.add(model);
                }

                NotifyInfo.display(I18N.DISPLAY.collaboratorAdded(),
                        I18N.DISPLAY.collaboratorAddConfirm(model.getName()));
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.addCollabErrorMsg(), caught);
            }
        });

    }

    private void removeCollaborators(final Collaborator model) {
        UserSessionServiceFacade facade = new UserSessionServiceFacade();
        JSONObject obj = buildJSONModel(model);
        facade.removeCollaborators(obj, new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                my_collaborators.remove(model);
                grid.getStore().remove(model);
                NotifyInfo.display(I18N.DISPLAY.collaboratorRemoved(),
                        I18N.DISPLAY.collaboratorRemoveConfirm(model.getName()));

            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.removeCollabErrorMsg(), caught);
            }
        });

    }

    private JSONObject buildJSONModel(final Collaborator model) {
        JSONArray arr = new JSONArray();
        JSONObject user = new JSONObject();
        user.put("username", new JSONString(model.getUserName()));
        arr.set(0, user);

        JSONObject obj = new JSONObject();
        obj.put("users", arr);
        return obj;
    }

    public void setCurrentCollaborators(List<Collaborator> collaborators) {
        my_collaborators = collaborators;
    }

    public void showCurrentCollborators() {
        loadResults(my_collaborators);
    }

    public List<Collaborator> getCurrentCollaborators() {
        return my_collaborators;
    }

    private boolean isCurrentCollaborator(Collaborator c) {
        for (Collaborator current : my_collaborators) {
            if (current.getId().equals(c.getId())) {
                return true;
            }
        }

        return false;
    }

}
