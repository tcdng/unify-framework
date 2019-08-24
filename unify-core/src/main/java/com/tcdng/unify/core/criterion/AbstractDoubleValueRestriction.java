/*
 * Copyright 2018-2019 The Code Department.
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

/**
 * Convenient abstract base class for double-value restrictions.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractDoubleValueRestriction extends AbstractSimpleRestriction implements DoubleValueRestriction {

    private Object firstValue;

    private Object secondValue;

    public AbstractDoubleValueRestriction(String propertyName, Object firstValue, Object secondValue) {
        super(propertyName);
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }

    @Override
    public Object getFirstValue() {
        return firstValue;
    }

    @Override
    public Object getSecondValue() {
        return secondValue;
    }

}
