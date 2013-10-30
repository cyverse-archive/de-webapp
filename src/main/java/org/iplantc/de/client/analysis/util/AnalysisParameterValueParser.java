package org.iplantc.de.client.analysis.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.iplantc.core.uiapps.widgets.client.models.ArgumentType;
import org.iplantc.core.uiapps.widgets.client.models.util.AppTemplateUtils;
import org.iplantc.de.client.analysis.models.AnalysesAutoBeanFactory;
import org.iplantc.de.client.analysis.models.AnalysisParameter;
import org.iplantc.de.client.analysis.models.SelectionValue;
import org.iplantc.de.client.analysis.models.SimpleValue;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;

public class AnalysisParameterValueParser {

    static AnalysesAutoBeanFactory factory = GWT.create(AnalysesAutoBeanFactory.class);

    private static List<String> REFERENCE_GENOME_TYPES
        = Arrays.asList("ReferenceAnnotation", "ReferenceSequence", "ReferenceGenome");

    private static boolean isReferenceGenomeType(final String typeName) {
        return Iterables.any(REFERENCE_GENOME_TYPES, new Predicate<String>() {
            public boolean apply(String input) {
                return input.equalsIgnoreCase(typeName);
            }
        });
    }

    private static final Set<ArgumentType> INPUT_TYPES = Sets.immutableEnumSet(
            ArgumentType.Input, ArgumentType.FileInput, ArgumentType.FolderInput,
            ArgumentType.MultiFileSelector);

    private static boolean isInputType(ArgumentType type) {
        return INPUT_TYPES.contains(type);
    }

    public static List<AnalysisParameter> parse(final List<AnalysisParameter> paramList) {

        List<AnalysisParameter> parsedList = new ArrayList<AnalysisParameter>();
        for (AnalysisParameter ap : paramList) {
            if (AppTemplateUtils.isTextType(ap.getType()) || ap.getType().equals(ArgumentType.Flag)) {
                parsedList.addAll(parseStringValue(ap));
            } else if (isInputType(ap.getType())) {
                if (isReferenceGenomeType(ap.getInfoType())) {
                    parsedList.addAll(parseStringValue(ap));
                } else {
                    parsedList.addAll(parseSelectionValue(ap));
                }
            } else if (AppTemplateUtils.isSelectionArgumentType(ap.getType())) {
                parsedList.addAll(parseSelectionValue(ap));
            }
        }

        return parsedList;

    }

    static List<AnalysisParameter> parseSelectionValue(final AnalysisParameter ap) {
        Splittable s = ap.getValue();
        Splittable val = s.get("value");
        if ((val != null) && (Strings.isNullOrEmpty(val.getPayload()) || !val.isKeyed())) {
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
