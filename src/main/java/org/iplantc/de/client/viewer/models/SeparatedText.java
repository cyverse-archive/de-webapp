package org.iplantc.de.client.viewer.models;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface SeparatedText {

    @PropertyName("chunk-size")
    public void setChunkSize(String size);

    @PropertyName("chunk-size")
    public String getChunkSize();

    @PropertyName("end")
    public void setEndPosition(String end);

    @PropertyName("end")
    public String getEndPosition();

    @PropertyName("start")
    public void setStartPosition(String start);

    @PropertyName("start")
    public String getStartPosition();

    @PropertyName("max-cols")
    public void setMaxColumns(String maxCols);

    @PropertyName("max-cols")
    public String getMaxColumns();

    SeparatedTextData getData();

}
