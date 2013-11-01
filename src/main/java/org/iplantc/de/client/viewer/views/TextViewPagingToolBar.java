package org.iplantc.de.client.viewer.views;

import org.iplantc.de.client.I18N;

import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.sencha.gxt.theme.gray.client.toolbar.GrayPagingToolBarAppearance;
import com.sencha.gxt.widget.core.client.Slider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class TextViewPagingToolBar extends ToolBar {

    GrayPagingToolBarAppearance appearance = new GrayPagingToolBarAppearance();
    protected TextButton first, prev, next, last;
    protected LabelToolItem beforePage, afterText;
    protected NumberField<Integer> pageText;
    protected CheckBox cbxWrap;
    protected Slider pageSize;
    private LabelToolItem sliderLabel;

    public TextViewPagingToolBar() {

        sliderLabel = new LabelToolItem(I18N.DISPLAY.pageSize());
        initPageSizeSlider();

        first = new TextButton();
        first.setIcon(appearance.first());

        prev = new TextButton();
        prev.setIcon(appearance.prev());

        next = new TextButton();
        next.setIcon(appearance.next());

        last = new TextButton();
        last.setIcon(appearance.last());

        beforePage = new LabelToolItem();
        // beforePage.setLabel(getMessages().beforePageText());

        afterText = new LabelToolItem();

        pageText = new NumberField<Integer>(new NumberPropertyEditor.IntegerPropertyEditor());
        pageText.setWidth("30px");

        cbxWrap = new CheckBox();
        cbxWrap.setBoxLabel(I18N.DISPLAY.wrap());

        addToolbarItems();
        pageText.setValue(1);
    }

    private void addToolbarItems() {
        add(sliderLabel);
        add(pageSize);
        add(first);
        add(prev);
        add(new SeparatorToolItem());
        add(beforePage);
        add(pageText);
        add(afterText);
        add(new SeparatorToolItem());
        add(next);
        add(last);
        add(new SeparatorToolItem());
        add(new FillToolItem());
        add(cbxWrap);
    }

    private void initPageSizeSlider() {
        pageSize = new Slider();
        pageSize.setMinValue(FileViewer.MIN_PAGE_SIZE_KB);
        pageSize.setMaxValue(FileViewer.MAX_PAGE_SIZE_KB);
        pageSize.setIncrement(FileViewer.PAGE_INCREMENT_SIZE_KB);
        pageSize.setValue(FileViewer.MIN_PAGE_SIZE_KB);
        pageSize.setWidth(100);
    }

    /**
     * 
     * @return page size in bytes
     */
    public long getPageSize() {
        return pageSize.getValue() * 1024;
    }

    public void addFirstSelectHandler(SelectHandler handler) {
        first.addSelectHandler(handler);
    }

    public void addPrevSelectHandler(SelectHandler handler) {
        prev.addSelectHandler(handler);
    }

    public void addNextSelectHandler(SelectHandler handler) {
        next.addSelectHandler(handler);
    }

    public void addLastSelectHandler(SelectHandler handler) {
        last.addSelectHandler(handler);
    }

    public void addSelectPageKeyHandler(KeyDownHandler handler) {
        pageText.addKeyDownHandler(handler);
    }

    public void setPrevEnabled(boolean enabled) {
        prev.setEnabled(enabled);
    }

    public void setFirstEnabled(boolean enabled) {
        first.setEnabled(enabled);
    }

    public void setNextEnabled(boolean enabled) {
        next.setEnabled(enabled);
    }

    public void setLastEnabled(boolean enabled) {
        last.setEnabled(enabled);
    }

    public void addWrapCbxChangeHandler(ValueChangeHandler<Boolean> changeHandler) {
        cbxWrap.addValueChangeHandler(changeHandler);
    }

    public boolean isWrapText() {
        return cbxWrap.getValue();
    }

    public int getPageNumber() {
        return pageText.getCurrentValue();
    }

    public void setPageNumber(int i) {
        pageText.setValue(i);
    }

    public void setWordWrap(boolean wrap) {
        cbxWrap.setValue(wrap);
    }

    public void setTotalPagesText(int totalPages) {
        afterText.setLabel("of " + totalPages);
    }

    public void addPageSizeChangeHandler(ValueChangeHandler<Integer> changeHandler) {
        pageSize.addValueChangeHandler(changeHandler);
    }

}
