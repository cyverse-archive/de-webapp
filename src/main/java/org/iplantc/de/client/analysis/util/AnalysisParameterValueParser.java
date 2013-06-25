package org.iplantc.de.client.analysis.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.iplantc.core.uiapps.widgets.client.models.ArgumentType;
import org.iplantc.core.uiapps.widgets.client.models.util.AppTemplateUtils;
import org.iplantc.de.client.analysis.models.AnalysesAutoBeanFactory;
import org.iplantc.de.client.analysis.models.AnalysisParameter;
import org.iplantc.de.client.analysis.models.SelectionValue;
import org.iplantc.de.client.analysis.models.SimpleValue;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;

public class AnalysisParameterValueParser {

    /*public static enum TYPES {
        TEXT, MULTILINETEXT, FLAG, NUMBER, INTEGER, DOUBLE, VALUESELECTION, SELECTION, TEXTSELECTION, INTEGERSELECTION, DOUBLESELECTION, TREESELECTION, ENVIRONMENTVARIABLE, OUTPUT, INPUT
    }*/

    static AnalysesAutoBeanFactory factory = GWT.create(AnalysesAutoBeanFactory.class);

    public static List<AnalysisParameter> parse(final List<AnalysisParameter> paramList) {

        List<AnalysisParameter> parsedList = new ArrayList<AnalysisParameter>();
        for (AnalysisParameter ap : paramList) {
//            String type = ap.getType();
            if(AppTemplateUtils.isTextType(ap.getType())){
            /*if (type.equalsIgnoreCase(TYPES.TEXT.toString())
                    || type.equalsIgnoreCase(TYPES.MULTILINETEXT.toString())
                    || type.equalsIgnoreCase(TYPES.ENVIRONMENTVARIABLE.toString())
                    || type.equalsIgnoreCase(TYPES.OUTPUT.toString())
                    || type.equalsIgnoreCase(TYPES.NUMBER.toString())
                    || type.equalsIgnoreCase(TYPES.INTEGER.toString())
                    || type.equalsIgnoreCase(TYPES.DOUBLE.toString())) {*/

                parsedList.addAll(parseStringValue(ap));
            /*} else if (type.equalsIgnoreCase(TYPES.INPUT.toString())) {*/
            } else if (ap.getType().equals(ArgumentType.Input)) {
                if (!ap.getInfoType().equalsIgnoreCase("ReferenceAnnotation")) {
                    parsedList.addAll(parseStringValue(ap));
                } else {
                    parsedList.addAll(parseSelectionValue(ap));
                }

            /*} else if (type.equalsIgnoreCase(TYPES.VALUESELECTION.toString())
                    || type.equalsIgnoreCase(TYPES.SELECTION.toString())
                    || type.equalsIgnoreCase(TYPES.TEXTSELECTION.toString())
                    || type.equalsIgnoreCase(TYPES.INTEGERSELECTION.toString())
                    || type.equalsIgnoreCase(TYPES.DOUBLESELECTION.toString())
                    || type.equalsIgnoreCase(TYPES.TREESELECTION.toString())) {*/
            } else if(AppTemplateUtils.isSelectionArgumentType(ap.getType())){
                parsedList.addAll(parseSelectionValue(ap));
            }

        }

        return parsedList;

    }

    static List<AnalysisParameter> parseSelectionValue(final AnalysisParameter ap) {
        Splittable s = ap.getValue();
        Splittable val = s.get("value");
        if ((val != null) 
                && (Strings.isNullOrEmpty(val.getPayload()) || !val.isKeyed())) {
            return Collections.emptyList();
        }
        AutoBean<SelectionValue> ab = AutoBeanCodex.decode(factory, SelectionValue.class, val);
        ap.setDisplayValue(ab.as().getDisplay());
        return Lists.<AnalysisParameter> newArrayList(ap);
    }

    static List<AnalysisParameter> parseStringValue(final AnalysisParameter ap) {
        List<AnalysisParameter> parsedList = new ArrayList<AnalysisParameter>();
        Splittable s = ap.getValue();
        AutoBean<SimpleValue> ab = AutoBeanCodex.decode(factory, SimpleValue.class, s);
        ap.setDisplayValue(ab.as().getValue());
        parsedList.add(ap);
        return parsedList;
    }

}
