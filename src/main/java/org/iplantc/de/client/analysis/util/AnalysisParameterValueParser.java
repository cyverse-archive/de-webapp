package org.iplantc.de.client.analysis.util;

import java.util.ArrayList;
import java.util.List;

import org.iplantc.de.client.analysis.models.AnalysesAutoBeanFactory;
import org.iplantc.de.client.analysis.models.AnalysisParameter;
import org.iplantc.de.client.analysis.models.SelectionValue;
import org.iplantc.de.client.analysis.models.SimpleValue;

import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;

public class AnalysisParameterValueParser {

    public static enum TYPES {
        TEXT, MULTILINETEXT, FLAG, NUMBER, INTEGER, DOUBLE, VALUESELECTION, SELECTION, TEXTSELECTION, INTEGERSELECTION, DOUBLESELECTION, TREESELECTION, ENVIRONMENTVARIABLE, OUTPUT, INPUT
    }

    static AnalysesAutoBeanFactory factory = GWT.create(AnalysesAutoBeanFactory.class);

    public static List<AnalysisParameter> parse(final List<AnalysisParameter> paramList) {

        List<AnalysisParameter> parsedList = new ArrayList<AnalysisParameter>();
        for (AnalysisParameter ap : paramList) {
            String type = ap.getType();
            if (type.equalsIgnoreCase(TYPES.TEXT.toString())
                    || type.equalsIgnoreCase(TYPES.MULTILINETEXT.toString())
                    || type.equalsIgnoreCase(TYPES.ENVIRONMENTVARIABLE.toString())
                    || type.equalsIgnoreCase(TYPES.OUTPUT.toString())
                    || type.equalsIgnoreCase(TYPES.NUMBER.toString())
                    || type.equalsIgnoreCase(TYPES.INTEGER.toString())
                    || type.equalsIgnoreCase(TYPES.DOUBLE.toString())) {

                parsedList.addAll(parseStringValue(ap));
            } else if (type.equalsIgnoreCase(TYPES.INPUT.toString())) {
                if (!ap.getInfoType().equalsIgnoreCase("ReferenceAnnotation")) {
                    parsedList.addAll(parseStringValue(ap));
                } else {
                    parsedList.addAll(parseSelectionValue(ap));
                }

            } else if (type.equalsIgnoreCase(TYPES.VALUESELECTION.toString())
                    || type.equalsIgnoreCase(TYPES.SELECTION.toString())
                    || type.equalsIgnoreCase(TYPES.TEXTSELECTION.toString())
                    || type.equalsIgnoreCase(TYPES.INTEGERSELECTION.toString())
                    || type.equalsIgnoreCase(TYPES.DOUBLESELECTION.toString())
                    || type.equalsIgnoreCase(TYPES.TREESELECTION.toString())) {
                parsedList.addAll(parseSelectionValue(ap));
            }

        }

        return parsedList;

    }

    public static List<AnalysisParameter> parseSelectionValue(final AnalysisParameter ap) {
        List<AnalysisParameter> parsedList = new ArrayList<AnalysisParameter>();
        Splittable s = ap.getValue();
        Splittable val = s.get("value");
        AutoBean<SelectionValue> ab = AutoBeanCodex.decode(factory, SelectionValue.class, val);
        ap.setDisplayValue(ab.as().getDisplay());
        parsedList.add(ap);
        return parsedList;
    }

    public static List<AnalysisParameter> parseStringValue(final AnalysisParameter ap) {
        List<AnalysisParameter> parsedList = new ArrayList<AnalysisParameter>();
        Splittable s = ap.getValue();
        AutoBean<SimpleValue> ab = AutoBeanCodex.decode(factory, SimpleValue.class, s);
        ap.setDisplayValue(ab.as().getValue());
        parsedList.add(ap);
        return parsedList;
    }

}
