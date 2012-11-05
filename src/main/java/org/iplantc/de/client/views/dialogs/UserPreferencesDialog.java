package org.iplantc.de.client.views.dialogs;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.events.SettingsUpdatedEvent;
import org.iplantc.de.client.views.panels.UserSettingPanel;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;

/**
 * A dialog to collect user general settings for the DE
 * 
 * 
 * @author sriram
 * 
 */
public class UserPreferencesDialog extends Dialog {

    private UserSettingPanel settingPanel;

    public UserPreferencesDialog() {
        init();
    }

    private void init() {
        initDialog();
        buildSettingPanel();
        layout();
        addListener(Events.Hide, new Listener<ComponentEvent>() {

            @Override
            public void handleEvent(ComponentEvent be) {
                settingPanel.saveData();
                EventBus.getInstance().fireEvent(new SettingsUpdatedEvent());
            }
        });
    }

    private void initDialog() {
        setHeading(I18N.DISPLAY.preferences());
        setSize(450, 380);
        setButtons();
        setResizable(false);
        setHideOnButtonClick(true);

    }

    private void setButtons() {
        ButtonBar buttonBar = getButtonBar();
        buttonBar.removeAll();
        setDefaultsButton();
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

    private void setDefaultsButton() {
        Button def = new Button(I18N.DISPLAY.restoreDefaults());
        def.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                settingPanel.setDefaultValues();
            }
        });
        def.setId("btn_default");
        getButtonBar().add(def);
    }

    private void buildSettingPanel() {
        settingPanel = new UserSettingPanel();
        add(settingPanel);
    }

}
