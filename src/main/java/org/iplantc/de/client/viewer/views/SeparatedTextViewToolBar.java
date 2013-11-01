package org.iplantc.de.client.viewer.views;

import org.iplantc.de.client.I18N;

import com.sencha.gxt.widget.core.client.Slider;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class SeparatedTextViewToolBar extends ToolBar {

    protected Slider pageSize;
    private LabelToolItem sliderLabel;

    public SeparatedTextViewToolBar() {
        sliderLabel = new LabelToolItem(I18N.DISPLAY.pageSize());
        initPageSizeSlider();
        add(sliderLabel);
        add(pageSize);
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

}
