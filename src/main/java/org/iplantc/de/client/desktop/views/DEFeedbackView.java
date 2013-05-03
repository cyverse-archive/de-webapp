package org.iplantc.de.client.desktop.views;

import org.iplantc.core.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

public class DEFeedbackView implements IsWidget {

    private static DEFeedbackViewUiBinder uiBinder = GWT.create(DEFeedbackViewUiBinder.class);

    interface DEFeedbackViewUiBinder extends UiBinder<Widget, DEFeedbackView> {
    }

    final Widget widget;
    @UiField
    VerticalLayoutContainer container;

    @UiField
    Radio vastField;
    @UiField
    Radio swsatField;
    @UiField
    Radio okField;
    @UiField
    Radio swdField;
    @UiField
    Radio nsField;

    @UiField
    CheckBox expField;
    @UiField
    CheckBox mngField;
    @UiField
    CheckBox runField;
    @UiField
    CheckBox chkField;
    @UiField
    CheckBox appField;
    @UiField
    CheckBox otrField;
    @UiField
    TextField otherField;

    @UiField
    CheckBox yesField;
    @UiField
    CheckBox swField;
    @UiField
    CheckBox noField;
    @UiField
    CheckBox notField;
    @UiField
    CheckBox tskOtrField;
    @UiField
    TextField otherCompField;
    @UiField
    TextArea featureTextArea;
    @UiField
    TextArea otherTextArea;

    @UiField
    FieldLabel reasonField;

    @UiField
    FieldLabel compelteField;

    @UiField
    FieldLabel satisfyField;

    @UiField
    FieldLabel featureField;

    @UiField
    FieldLabel anythingField;

    public DEFeedbackView() {
        widget = uiBinder.createAndBindUi(this);
        container.setScrollMode(ScrollMode.AUTOY);
        ToggleGroup group = new ToggleGroup();
        group.add(vastField);
        group.add(swsatField);
        group.add(okField);
        group.add(swdField);
        group.add(nsField);
        reasonField.setHTML(buildRequiredFieldLabel(reasonField.getText()));
        compelteField.setHTML(buildRequiredFieldLabel(compelteField.getText()));
        satisfyField.setHTML(buildRequiredFieldLabel(satisfyField.getText()));
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    public boolean validate() {
        if (validateQ1() && validateQ2() && validateQ3()) {
            return true;
        } else {
            AlertMessageBox amb = new AlertMessageBox(I18N.DISPLAY.warning(),
                    I18N.DISPLAY.publicSubmitTip());
            amb.show();
            return false;
        }
    }

    private String buildRequiredFieldLabel(String label) {
        if (label == null) {
            return null;
        }

        return "<span style='color:red; top:-5px;' >*</span> " + label; //$NON-NLS-1$
    }

    private boolean validateQ1() {
        return (expField.getValue() || mngField.getValue() || runField.getValue() || chkField.getValue()
                || appField.getValue() || otrField.getValue());
    }

    private boolean validateQ2() {
        return (yesField.getValue() || swField.getValue() || noField.getValue() || notField.getValue() || tskOtrField
                .getValue());
    }

    private boolean validateQ3() {
        return (vastField.getValue() || swsatField.getValue() || okField.getValue()
                || swdField.getValue() || nsField.getValue());
    }

    // public JSONObject toJson() {
    //
    // }

}
