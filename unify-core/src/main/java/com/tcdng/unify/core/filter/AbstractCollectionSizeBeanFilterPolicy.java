/*
 * Copyright 2018-2020 The Code Department.
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

import java.lang.reflect.Array;
import java.util.Collection;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.criterion.RestrictionField;
import com.tcdng.unify.core.criterion.SingleParamRestriction;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Convenient abstract base class for collection size filter policies.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractCollectionSizeBeanFilterPolicy implements BeanFilterPolicy {

    private boolean inverted;

    public AbstractCollectionSizeBeanFilterPolicy(boolean inverted) {
        this.inverted = inverted;
    }

    @Override
    public boolean match(Object bean, Restriction restriction) throws UnifyException {
        SingleParamRestriction singleParamRestriction = (SingleParamRestriction) restriction;
        Object fieldVal = DataUtils.getNestedBeanProperty(bean, singleParamRestriction.getFieldName());
        if (fieldVal != null) {
            int collSize =
                    fieldVal.getClass().isArray() ? Array.getLength(fieldVal) : ((Collection<?>) fieldVal).size();
            Object param = singleParamRestriction.getParam();
            if (param instanceof RestrictionField) {
                return doMatch(collSize, DataUtils.convert(int.class,
                        DataUtils.getNestedBeanProperty(bean, ((RestrictionField) param).getName()), null));
            } else {
                return doMatch(collSize, DataUtils.convert(int.class, param, null));
            }
        }

        return inverted;
    }

    protected abstract boolean doMatch(int collSize, int param) throws UnifyException;
}
