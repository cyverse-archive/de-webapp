/**
 * 
 */
package org.iplantc.de.client.views.dialogs;

import org.iplantc.de.client.I18N;
import org.iplantc.de.client.views.panels.ManageCollaboratorsPanel;
import org.iplantc.de.client.views.panels.ManageCollaboratorsPanel.MODE;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;

/**
 * A dialog to display collaborators
 * 
 * @author sriram
 * 
 */
public class CollaboratorsDialog extends Dialog {
    private ManageCollaboratorsPanel collabPanel;

    public CollaboratorsDialog() {
        initDialog();
    }

    public void init() {
        buildCollaboratorsPanel();
        layout();
    }

    private void initDialog() {
        setHeading(I18N.DISPLAY.collaborators());
        setSize(450, 450);
        setButtons();
        setResizable(false);
        setHideOnButtonClick(true);
        ToolButton helpBtn = new ToolButton("x-tool-help"); //$NON-NLS-1$
        helpBtn.setToolTip(buildHelpToolTip(I18N.HELP.collaboratorsHelp()));
        getHeader().addTool(helpBtn);

    }

    private ToolTipConfig buildHelpToolTip(String helpText) {
        ToolTipConfig ttc = getToolTipConfig();
        ttc.setTitle(I18N.DISPLAY.help());
        ttc.setText(helpText);
        return ttc;
    }

    private ToolTipConfig getToolTipConfig() {
        ToolTipConfig config = new ToolTipConfig();
        config.setMouseOffset(new int[] {0, 0});
        config.setAnchor("left"); //$NON-NLS-1$
        config.setCloseable(true);
        return config;
    }

    private void setButtons() {
        ButtonBar buttonBar = getButtonBar();
        buttonBar.removeAll();
        setOkButton();
        buttonBar.setAlignment(HorizontalAlignment.RIGHT);
    }

    private void setOkButton() {
        Button ok = new Button(I18N.DISPLAY.done());
        ok.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();

            }
        });
        ok.setId(Dialog.OK);
        getButtonBar().add(ok);
    }

    private void buildCollaboratorsPanel() {
        removeAll();
        collabPanel = new ManageCollaboratorsPanel(MODE.MANAGE, 435, 270);
        add(collabPanel);
    }

}
