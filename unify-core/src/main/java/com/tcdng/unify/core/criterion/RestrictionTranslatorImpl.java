/*
 * Copyright 2018-2025 The Code Department.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.tcdng.unify.core.criterion;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Default implementation of restriction translator.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component(ApplicationComponents.APPLICATION_RESTRICTIONTRANSLATOR)
public class RestrictionTranslatorImpl extends AbstractRestrictionTranslator {

    private Map<FilterConditionType, Translator> translators;

    @Override
    public String translate(Restriction restriction) throws UnifyException {
        return translate(restriction, null);
    }

    @Override
    public String translate(Restriction restriction, RestrictionTranslatorMapper mapper) throws UnifyException {
        if (restriction == null) {
            return getSessionMessage("filter.fetchall");
        }

        StringBuilder sb = new StringBuilder();
        translators.get(restriction.getConditionType()).translate(sb, restriction, mapper, 0);
        return sb.toString();
    }

    @Override
    protected void onInitialize() throws UnifyException {
        super.onInitialize();

        Map<FilterConditionType, Translator> map = new EnumMap<FilterConditionType, Translator>(
                FilterConditionType.class);
        map.put(FilterConditionType.EQUALS, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.IEQUALS, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.NOT_EQUALS, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.INOT_EQUALS, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.LESS_THAN, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.LESS_OR_EQUAL, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.GREATER_THAN, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.GREATER_OR_EQUAL, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.BETWEEN, new DoubleParamRestrictionTranslator());
        map.put(FilterConditionType.NOT_BETWEEN, new DoubleParamRestrictionTranslator());
        map.put(FilterConditionType.AMONGST, new MultipleParamRestrictionTranslator());
        map.put(FilterConditionType.NOT_AMONGST, new MultipleParamRestrictionTranslator());
        map.put(FilterConditionType.LIKE, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.ILIKE, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.NOT_LIKE, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.BEGINS_WITH, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.IBEGINS_WITH, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.NOT_BEGIN_WITH, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.IENDS_WITH, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.ENDS_WITH, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.NOT_END_WITH, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.IS_NULL, new ZeroParamRestrictionTranslator());
        map.put(FilterConditionType.IS_NOT_NULL, new ZeroParamRestrictionTranslator());
        map.put(FilterConditionType.EQUALS_FIELD, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.NOT_EQUALS_FIELD, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.LESS_THAN_FIELD, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.LESS_OR_EQUAL_FIELD, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.GREATER_THAN_FIELD, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.GREATER_OR_EQUAL_FIELD, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.BETWEEN_FIELD, new DoubleParamRestrictionTranslator());
        map.put(FilterConditionType.NOT_BETWEEN_FIELD, new DoubleParamRestrictionTranslator());
        map.put(FilterConditionType.LIKE_FIELD, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.NOT_LIKE_FIELD, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.BEGINS_WITH_FIELD, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.NOT_BEGIN_WITH_FIELD, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.ENDS_WITH_FIELD, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.NOT_END_WITH_FIELD, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.EQUALS_SESSIONPARAM, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.NOT_EQUALS_SESSIONPARAM, new SingleParamRestrictionTranslator());
        map.put(FilterConditionType.AND, new CompoundRestrictionTranslator());
        map.put(FilterConditionType.OR, new CompoundRestrictionTranslator());
        translators = Collections.unmodifiableMap(map);
    }

    private interface Translator {

        void translate(StringBuilder sb, Restriction restriction, RestrictionTranslatorMapper mapper, int depth)
                throws UnifyException;
    }

    private abstract class AbstractRestrictionTranslator implements Translator {

        protected void appendField(StringBuilder sb, String fieldName, RestrictionTranslatorMapper mapper)
                throws UnifyException {
            String fieldLabel = mapper != null ? mapper.getLabelTranslation(fieldName) : null;
            if (!StringUtils.isBlank(fieldLabel)) {
                sb.append(fieldLabel);
            } else {
                sb.append("$f{").append(fieldName).append("}");
            }
        }

        protected void appendParam(StringBuilder sb, String fieldName, Object param, RestrictionTranslatorMapper mapper)
                throws UnifyException {
            if (mapper != null) {
                Object mapped = mapper.getValueTranslation(fieldName, param);
                param = mapped != null ? mapped : param;
            }

            if (param instanceof String) {
                sb.append('\'').append(param).append('\'');
            } else {
                // TODO Use formatter
                sb.append(param);
            }
        }
    }

    private class ZeroParamRestrictionTranslator extends AbstractRestrictionTranslator {

        @Override
        public void translate(StringBuilder sb, Restriction restriction, RestrictionTranslatorMapper mapper, int depth)
                throws UnifyException {
            appendField(sb, ((ZeroParamRestriction) restriction).getFieldName(), mapper);
            sb.append(' ');
            sb.append(getSessionMessage(restriction.getConditionType().symbolKey()));
        }

    }

    private class SingleParamRestrictionTranslator extends AbstractRestrictionTranslator {

        @Override
        public void translate(StringBuilder sb, Restriction restriction, RestrictionTranslatorMapper mapper, int depth)
                throws UnifyException {
            SingleParamRestriction singleParamRestriction = (SingleParamRestriction) restriction;
            final String fieldName = singleParamRestriction.getFieldName();
            appendField(sb, fieldName, mapper);
            sb.append(' ');
            sb.append(getSessionMessage(restriction.getConditionType().symbolKey()));
            sb.append(' ');

            Object param = singleParamRestriction.getParam();
            if (param instanceof RestrictionField) {
                appendField(sb, ((RestrictionField) param).getName(), mapper);
            } else {
                appendParam(sb, fieldName, param, mapper);
            }
        }

    }

    private class DoubleParamRestrictionTranslator extends AbstractRestrictionTranslator {

        @Override
        public void translate(StringBuilder sb, Restriction restriction, RestrictionTranslatorMapper mapper, int depth)
                throws UnifyException {
            DoubleParamRestriction doubleParamRestriction = (DoubleParamRestriction) restriction;
            final String fieldName = doubleParamRestriction.getFieldName();
            appendField(sb, fieldName, mapper);
            sb.append(' ');
            sb.append(getSessionMessage(restriction.getConditionType().symbolKey()));
            sb.append(" (");

            Object param = doubleParamRestriction.getFirstParam();
            if (param instanceof RestrictionField) {
                appendField(sb, ((RestrictionField) param).getName(), mapper);
            } else {
                appendParam(sb, fieldName, param, mapper);
            }

            sb.append(", ");
            param = doubleParamRestriction.getSecondParam();
            if (param instanceof RestrictionField) {
                appendField(sb, ((RestrictionField) param).getName(), mapper);
            } else {
                appendParam(sb, fieldName, param, mapper);
            }
            sb.append(")");
        }

    }

    private class MultipleParamRestrictionTranslator extends AbstractRestrictionTranslator {

        @Override
        public void translate(StringBuilder sb, Restriction restriction, RestrictionTranslatorMapper mapper, int depth)
                throws UnifyException {
            MultipleParamRestriction multipleParamRestriction = (MultipleParamRestriction) restriction;
            final String fieldName = multipleParamRestriction.getFieldName();
            appendField(sb, fieldName, mapper);
            sb.append(' ');
            sb.append(getSessionMessage(restriction.getConditionType().symbolKey()));
            sb.append(" (");
            boolean appendSym = false;
            for (Object param : multipleParamRestriction.getParams()) {
                if (appendSym) {
                    sb.append(", ");
                } else {
                    appendSym = true;
                }

                appendParam(sb, fieldName, param, mapper);
            }
            sb.append(")");
        }

    }

    private class CompoundRestrictionTranslator extends AbstractRestrictionTranslator {

        @Override
        public void translate(StringBuilder sb, Restriction restriction, RestrictionTranslatorMapper mapper, int depth)
                throws UnifyException {
            boolean sub = depth > 0;
            CompoundRestriction compoundRestriction = (CompoundRestriction) restriction;
            if (sub) {
                sb.append("(");
            }

            String sym = " " + getSessionMessage(restriction.getConditionType().symbolKey()) + " ";
            depth++;
            boolean appendSym = false;
            for (Restriction subRestriction : compoundRestriction.getRestrictionList()) {
                if (appendSym) {
                    sb.append(sym);
                } else {
                    appendSym = true;
                }

                translators.get(subRestriction.getConditionType()).translate(sb, subRestriction, mapper, depth);
            }

            if (sub) {
                sb.append(")");
            }
        }

    }
}
