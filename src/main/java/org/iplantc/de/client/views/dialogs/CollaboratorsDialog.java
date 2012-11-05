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

/**
 * A dialog to display collaborators
 * 
 * @author sriram
 * 
 */
public class CollaboratorsDialog extends Dialog {
    private ManageCollaboratorsPanel collabPanel;

    public CollaboratorsDialog() {
        init();
    }

    private void init() {
        initDialog();
        buildCollaboratorsPanel();
        layout();
    }

    private void initDialog() {
        setHeading(I18N.DISPLAY.collaborators());
        setSize(450, 280);
        setButtons();
        setResizable(false);
        setHideOnButtonClick(true);

    }

    private void setButtons() {
        ButtonBar buttonBar = getButtonBar();
        buttonBar.removeAll();
        buttonBar.setAlignment(HorizontalAlignment.RIGHT);
        setOkButton();

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
        collabPanel = new ManageCollaboratorsPanel(MODE.MANAGE, 435, 270);
        add(collabPanel);
    }

}
