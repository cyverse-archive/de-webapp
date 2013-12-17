package org.iplantc.de.client.viewer.views;

import org.iplantc.core.resources.client.IplantResources;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.viewer.events.SaveFileEvent;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;

public class TextViewPagingToolBar extends AbstractPagingToolbar {

    private CheckBox cbxWrap;
    private TextButton saveBtn;
    private boolean editing;
    private AbstractFileViewer view;

    public TextViewPagingToolBar(AbstractFileViewer view, boolean editing) {
        super(view.getFileSize());
        this.view = view;
        this.editing = editing;
        cbxWrap = new CheckBox();
        cbxWrap.setBoxLabel(I18N.DISPLAY.wrap());
        add(new FillToolItem());
        add(cbxWrap);
        saveBtn = new TextButton(I18N.DISPLAY.save(), IplantResources.RESOURCES.save());
        add(new FillToolItem());
        add(saveBtn);
        saveBtn.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                TextViewPagingToolBar.this.fireEvent(new SaveFileEvent());

            }
        });
        saveBtn.setEnabled(editing);
    }

    public void addWrapCbxChangeHandler(ValueChangeHandler<Boolean> changeHandler) {
        cbxWrap.addValueChangeHandler(changeHandler);
    }

    public boolean isWrapText() {
        return cbxWrap.getValue();
    }

    public void setWordWrap(boolean wrap) {
        cbxWrap.setValue(wrap);
    }

    @Override
    public void onFirst() {
        view.loadData();

    }

    @Override
    public void onLast() {
        view.loadData();

    }

    @Override
    public void onPrev() {
        view.loadData();

    }

    @Override
    public void onNext() {
        view.loadData();

    }

    @Override
    public void onPageSizeChange() {
        view.loadData();

    }

    @Override
    public void onPageSelect() {
        view.loadData();

    }
}
