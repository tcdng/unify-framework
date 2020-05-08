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

package com.tcdng.unify.core.criterion;

/**
 * Convenient abstract base class for double parameter restrictions.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractDoubleParamRestriction extends AbstractSimpleRestriction
        implements DoubleParamRestriction {

    private Object firstParam;

    private Object secondParam;

    public AbstractDoubleParamRestriction(String propertyName, Object firstParam, Object secondParam) {
        super(propertyName);
        this.firstParam = firstParam;
        this.secondParam = secondParam;
    }

    @Override
    public Object getFirstParam() {
        return firstParam;
    }

    @Override
    public Object getSecondParam() {
        return secondParam;
    }

    @Override
    public void setParams(Object firstParam, Object secondParam) {
        this.firstParam = firstParam;
        this.secondParam = secondParam;
    }

    @Override
    public boolean isValid() {
        return firstParam != null && secondParam != null;
    }

}
