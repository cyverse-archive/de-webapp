package org.iplantc.de.client.views.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.iplantc.de.client.I18N;
import org.iplantc.de.client.models.Collaborator;
import org.iplantc.de.client.utils.CollaboratorsUtil;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.GridViewConfig;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Dialog that displays a grid of the user's Collaborators, which can be selected in a Checkbox
 * selection grid. The final selection can be retrieved from the getSelectedCollaborators method called
 * in a SelectionListener added to the OK/Done button.
 * 
 * @author psarando
 * 
 */
public class SelectCollaboratorsDialog extends Dialog {
    private Grid<Collaborator> grid;

    public SelectCollaboratorsDialog() {
        init();
    }

    private void init() {
        setSize(640, 320);
        setLayout(new FitLayout());
        setHeading(I18N.DISPLAY.selectCollabs());
        setHideOnButtonClick(true);
        setModal(true);

        buildHelpToolTip();

        setButtons();

        buildCollaboratorsGrid();

        add(grid);
    }

    private void buildHelpToolTip() {
        ToolTipConfig ttc = getToolTipConfig();
        ttc.setTitle(I18N.DISPLAY.help());
        ttc.setText(I18N.HELP.shareCollaboratorsHelp());

        ToolButton helpBtn = new ToolButton("x-tool-help"); //$NON-NLS-1$
        helpBtn.setToolTip(ttc);
        getHeader().addTool(helpBtn);
    }

    private ToolTipConfig getToolTipConfig() {
        ToolTipConfig config = new ToolTipConfig();
        config.setMouseOffset(new int[] {0, 0});
        config.setAnchor("left"); //$NON-NLS-1$
        config.setCloseable(true);
        return config;
    }

    private Widget buildCollaboratorsGrid() {
        CheckBoxSelectionModel<Collaborator> sm = new CheckBoxSelectionModel<Collaborator>();
        grid = new Grid<Collaborator>(new ListStore<Collaborator>(), buildCollaboratorColumnModel(sm));

        sm.setSelectionMode(SelectionMode.MULTI);
        grid.setSelectionModel(sm);
        grid.addPlugin(sm);

        grid.setAutoExpandColumn(Collaborator.NAME);
        grid.setBorders(false);
        grid.getView().setEmptyText(I18N.DISPLAY.noCollaborators());

        GridView view = grid.getView();
        view.setViewConfig(buildGridViewConfig());
        view.setForceFit(true);

        return grid;
    }

    private GridViewConfig buildGridViewConfig() {
        GridViewConfig config = new GridViewConfig() {

            @Override
            public String getRowStyle(ModelData model, int rowIndex, ListStore<ModelData> ds) {
                return "iplantc-select-grid"; //$NON-NLS-1$
            }

        };

        return config;
    }

    private ColumnModel buildCollaboratorColumnModel(CheckBoxSelectionModel<Collaborator> sm) {
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        ColumnConfig name = new ColumnConfig(Collaborator.NAME, I18N.DISPLAY.name(), 150);
        ColumnConfig email = new ColumnConfig(Collaborator.EMAIL, I18N.DISPLAY.email(), 200);

        columns.addAll(Arrays.asList(sm.getColumn(), name, email));

        return new ColumnModel(columns);
    }

    private void setButtons() {
        setButtons(Dialog.OKCANCEL);
        setButtonAlign(HorizontalAlignment.RIGHT);

        getDoneButton().setText(I18N.DISPLAY.done());
    }

    /**
     * @return The OK button, labeled "Done" by default.
     */
    public Button getDoneButton() {
        return getButtonById(Dialog.OK);
    }

    /**
     * Sets the grid with the given list of Collaborators.
     * 
     * @param collaborators
     */
    public void loadResults(List<Collaborator> collaborators) {
        // Clear results before adding.
        clear();

        ListStore<Collaborator> store = grid.getStore();
        store.add(collaborators);

        // Sort alphabetically.
        store.sort(Collaborator.NAME, SortDir.ASC);
    }

    public void clear() {
        grid.getStore().removeAll();
    }

    /**
     * Sets the grid with the user's current Collaborators.
     */
    public void showCurrentCollborators() {
        loadResults(CollaboratorsUtil.getCurrentCollaborators());
    }

    /**
     * @return The list of Collaborators selected in the grid.
     */
    public List<Collaborator> getSelectedCollaborators() {
        return grid.getSelectionModel().getSelectedItems();
    }

}
