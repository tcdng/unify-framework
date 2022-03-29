/*
 * Copyright 2018-2022 The Code Department.
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

package com.tcdng.unify.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.tcdng.unify.core.criterion.AbstractCompoundRestriction;
import com.tcdng.unify.core.criterion.AbstractDoubleParamRestriction;
import com.tcdng.unify.core.criterion.AbstractMultipleParamRestriction;
import com.tcdng.unify.core.criterion.AbstractSimpleRestriction;
import com.tcdng.unify.core.criterion.AbstractSingleParamRestriction;
import com.tcdng.unify.core.criterion.CompoundRestriction;
import com.tcdng.unify.core.criterion.DoubleParamRestriction;
import com.tcdng.unify.core.criterion.FilterConditionType;
import com.tcdng.unify.core.criterion.MultipleParamRestriction;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.criterion.SimpleRestriction;
import com.tcdng.unify.core.criterion.SingleParamRestriction;
import com.tcdng.unify.core.criterion.ZeroParamRestriction;

/**
 * Criteria utilities.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class CriteriaUtils {

    private CriteriaUtils() {

    }

    public static CompoundRestriction unmodifiableRestriction(CompoundRestriction compoundRestriction) {
        return new UnCompoundRestriction((AbstractCompoundRestriction) compoundRestriction);
    }

    public static SingleParamRestriction unmodifiableRestriction(SingleParamRestriction restriction) {
        return new UnSingleParamRestriction((AbstractSingleParamRestriction) restriction);
    }

    public static DoubleParamRestriction unmodifiableRestriction(DoubleParamRestriction restriction) {
        return new UnDoubleParamRestriction((AbstractDoubleParamRestriction) restriction);
    }

    public static MultipleParamRestriction unmodifiableRestriction(MultipleParamRestriction restriction) {
        return new UnMultipleParamRestriction((AbstractMultipleParamRestriction) restriction);
    }

    public static ZeroParamRestriction unmodifiableRestriction(ZeroParamRestriction restriction) {
        return restriction;
    }
    
    public static Restriction unmodifiableRestriction(Restriction restriction) {
        FilterConditionType type = restriction.getConditionType();
        if (type.isCompound()) {
            return new UnCompoundRestriction((AbstractCompoundRestriction) restriction);
        }
        
        if (type.isSingleParam()) {
            return new UnSingleParamRestriction((AbstractSingleParamRestriction) restriction);
        }
        
        if (type.isRange()) {
            return new UnDoubleParamRestriction((AbstractDoubleParamRestriction) restriction);
        }
        
        if (type.isAmongst()) {
            return new UnMultipleParamRestriction((AbstractMultipleParamRestriction) restriction);
        }
        
        return restriction;
    }
    
    private static class UnCompoundRestriction implements CompoundRestriction {
        
        private AbstractCompoundRestriction co;

        private List<Restriction> restrictionList;
        
        public UnCompoundRestriction(AbstractCompoundRestriction co) {
            this.co = co;
            restrictionList = new ArrayList<Restriction>();
            for(Restriction restriction: co.getRestrictionList()) {
                restrictionList.add(CriteriaUtils.unmodifiableRestriction(restriction));
            }
            
            restrictionList = Collections.unmodifiableList(restrictionList);
        }

        @Override
        public FilterConditionType getConditionType() {
            return co.getConditionType();
        }

        @Override
        public void writeRestrictedFields(Set<String> restrictedFields) {
            co.writeRestrictedFields(restrictedFields);
        }

        @Override
        public boolean isRestrictedField(String fieldName) {
            return co.isRestrictedField(fieldName);
        }

        @Override
        public boolean isSimple() {
            return co.isSimple();
        }

        @Override
        public boolean isValid() {
            return co.isValid();
        }

        @Override
        public CompoundRestriction add(Restriction restriction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Restriction> getRestrictionList() {
            return restrictionList;
        }

        @Override
        public boolean isEmpty() {
            return co.isEmpty();
        }

        @Override
        public int size() {
            return co.size();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replaceAll(String propertyName, Object val) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replaceAll(String propertyName, Object val1, Object val2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replaceAll(String propertyName, Collection<Object> val) {
            throw new UnsupportedOperationException();
        }
    }

    private static abstract class UnAbstractSimpleRestriction implements SimpleRestriction {

        protected AbstractSimpleRestriction ao;

        public UnAbstractSimpleRestriction(AbstractSimpleRestriction ao) {
            this.ao = ao;
        }

        @Override
        public void writeRestrictedFields(Set<String> restrictedFields) {
            ao.writeRestrictedFields(restrictedFields);
        }

        @Override
        public String getFieldName() {
            return ao.getFieldName();
        }

        @Override
        public boolean isRestrictedField(String fieldName) {
            return ao.isRestrictedField(fieldName);
        }

        @Override
        public boolean isEmpty() {
            return ao.isEmpty();
        }

        @Override
        public boolean isSimple() {
            return ao.isSimple();
        }

        @Override
        public FilterConditionType getConditionType() {
            return ao.getConditionType();
        }

        @Override
        public boolean isValid() {
            return ao.isValid();
        }
    }

    private static class UnSingleParamRestriction extends UnAbstractSimpleRestriction
            implements SingleParamRestriction {

        public UnSingleParamRestriction(AbstractSingleParamRestriction ao) {
            super(ao);
        }

        @Override
        public Object getParam() {
            return ((SingleParamRestriction) ao).getParam();
        }

        @Override
        public void setParam(Object param) {
            throw new UnsupportedOperationException();
        }

    }

    private static class UnDoubleParamRestriction extends UnAbstractSimpleRestriction
            implements DoubleParamRestriction {

        public UnDoubleParamRestriction(AbstractDoubleParamRestriction ao) {
            super(ao);
        }

        @Override
        public Object getFirstParam() {
            return ((DoubleParamRestriction) ao).getFirstParam();
        }

        @Override
        public Object getSecondParam() {
            return ((DoubleParamRestriction) ao).getSecondParam();
        }

        @Override
        public void setParams(Object firstParam, Object secondParam) {
            throw new UnsupportedOperationException();
        }

    }

    private static class UnMultipleParamRestriction extends UnAbstractSimpleRestriction
            implements MultipleParamRestriction {

        public UnMultipleParamRestriction(AbstractMultipleParamRestriction ao) {
            super(ao);
        }

        @Override
        public Collection<?> getParams() {
            return ((MultipleParamRestriction) ao).getParams();
        }

        @Override
        public void setParams(Collection<?> params) {
            throw new UnsupportedOperationException();
        }

    }
}
