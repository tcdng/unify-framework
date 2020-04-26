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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.criterion.DoubleParamRestriction;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.criterion.RestrictionField;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Convenient abstract base class for double parameter bean filter policies.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractDoubleParamBeanFilterPolicy implements BeanFilterPolicy {

    private boolean useFieldParams;

    private boolean inverted;

    public AbstractDoubleParamBeanFilterPolicy(boolean useFieldParams, boolean inverted) {
        this.useFieldParams = useFieldParams;
    }

    @Override
    public boolean match(Object bean, Restriction restriction) throws UnifyException {
        DoubleParamRestriction doubleParamRestriction = (DoubleParamRestriction) restriction;
        Object fieldVal = DataUtils.getNestedBeanProperty(bean, doubleParamRestriction.getFieldName());
        if (fieldVal != null) {
            if (useFieldParams) {
                return doMatch(fieldVal,
                        DataUtils.getNestedBeanProperty(bean,
                                ((RestrictionField) doubleParamRestriction.getFirstParam()).getName()),
                        DataUtils.getNestedBeanProperty(bean,
                                ((RestrictionField) doubleParamRestriction.getSecondParam()).getName()));
            } else {
                return doMatch(fieldVal,
                        DataUtils.convert(fieldVal.getClass(), doubleParamRestriction.getFirstParam(), null),
                        DataUtils.convert(fieldVal.getClass(), doubleParamRestriction.getSecondParam(), null));
            }
        }

        return inverted;
    }

    protected abstract boolean doMatch(Object fieldVal, Object paramA, Object paramB) throws UnifyException;
}
