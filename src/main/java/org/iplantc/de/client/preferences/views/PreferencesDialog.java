/**
 * 
 */
package org.iplantc.de.client.preferences.views;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.events.PreferencesUpdatedEvent;
import org.iplantc.de.client.preferences.presenter.PreferencesPresenterImpl;
import org.iplantc.de.client.preferences.views.PreferencesView.Presenter;

import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * A dialog to show preferences view
 * 
 * @author sriram
 * 
 */
public class PreferencesDialog extends Dialog {

    private Presenter presenter;
    private ToolButton help_tool;

    public PreferencesDialog() {
        setHeadingText(I18N.DISPLAY.preferences());
        help_tool = new ToolButton(ToolButton.QUESTION);
        getHeader().addTool(help_tool);
        setPixelSize(450, 380);
        setButtons();
        PreferencesView view = new PreferencesViewImpl();
        presenter = new PreferencesPresenterImpl(view);
        presenter.go(this);
    }

    private void setButtons() {
        ButtonBar buttonBar = getButtonBar();
        buttonBar.clear();
        setDefaultsButton();
        setOkButton();

    }

    private void setOkButton() {
        TextButton ok = new TextButton(I18N.DISPLAY.done());
        ok.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                if (presenter.validateAndSave()) {
                    hide();
                    PreferencesUpdatedEvent pue = new PreferencesUpdatedEvent();
                    EventBus.getInstance().fireEvent(pue);
                }
            }
        });

        ok.setId("idbtnPrefDone");
        getButtonBar().add(ok);
    }

    private void setDefaultsButton() {
        TextButton def = new TextButton(I18N.DISPLAY.restoreDefaults());

        def.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                presenter.setDefaults();

            }
        });
        def.setId("btn_default");
        getButtonBar().add(def);
    }
}
