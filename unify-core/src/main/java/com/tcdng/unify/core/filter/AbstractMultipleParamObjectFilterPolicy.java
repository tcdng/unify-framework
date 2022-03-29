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
package com.tcdng.unify.core.filter;

import java.util.Collection;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.criterion.MultipleParamRestriction;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Convenient abstract base class for multiple parameter object filter policies.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractMultipleParamObjectFilterPolicy implements ObjectFilterPolicy {

    private boolean inverted;

    public AbstractMultipleParamObjectFilterPolicy(boolean inverted) {
        this.inverted = inverted;
    }

	@SuppressWarnings("unchecked")
    @Override
	public boolean match(ValueStore valueStore, Restriction restriction) throws UnifyException {
        MultipleParamRestriction multipleParamRestriction = (MultipleParamRestriction) restriction;
        Object fieldVal = valueStore.retrieve(multipleParamRestriction.getFieldName());
        if (fieldVal != null) {
            return doMatch(fieldVal,
                    DataUtils.convert(List.class, fieldVal.getClass(), multipleParamRestriction.getParams()));
        }

        return inverted;
	}

	@SuppressWarnings("unchecked")
    @Override
    public boolean match(Object bean, Restriction restriction) throws UnifyException {
        MultipleParamRestriction multipleParamRestriction = (MultipleParamRestriction) restriction;
        Object fieldVal = DataUtils.getNestedBeanProperty(bean, multipleParamRestriction.getFieldName());
        if (fieldVal != null) {
            return doMatch(fieldVal,
                    DataUtils.convert(List.class, fieldVal.getClass(), multipleParamRestriction.getParams()));
        }

        return inverted;
    }

    protected abstract boolean doMatch(Object fieldVal, Collection<?> paramA) throws UnifyException;
}
