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
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.criterion.SingleParamRestriction;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Convenient abstract base class for single parameter bean filter policies.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractSingleParamBeanFilterPolicy implements BeanFilterPolicy {

    private boolean useFieldParams;

    private boolean inverted;

    public AbstractSingleParamBeanFilterPolicy(boolean useFieldParams, boolean inverted) {
        this.useFieldParams = useFieldParams;
        this.inverted = inverted;
    }

    @Override
    public boolean match(Object bean, Restriction restriction) throws UnifyException {
        SingleParamRestriction singleParamRestriction = (SingleParamRestriction) restriction;
        Object fieldVal = DataUtils.getNestedBeanProperty(bean, singleParamRestriction.getFieldName());
        if (fieldVal != null) {
            if (useFieldParams) {
                return doMatch(fieldVal,
                        DataUtils.getNestedBeanProperty(bean, String.valueOf(singleParamRestriction.getParam())));
            } else {
                return doMatch(fieldVal,
                        DataUtils.convert(fieldVal.getClass(), singleParamRestriction.getParam(), null));
            }
        }

        return inverted && singleParamRestriction.getParam() != null;
    }

    protected abstract boolean doMatch(Object fieldVal, Object param) throws UnifyException;
}
