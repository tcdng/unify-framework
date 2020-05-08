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
 * Convenient abstract base class for single parameter restrictions.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractSingleParamRestriction extends AbstractSimpleRestriction
        implements SingleParamRestriction {

    private Object param;

    public AbstractSingleParamRestriction(String propertyName, Object param) {
        super(propertyName);
        this.param = param;
    }

    @Override
    public Object getParam() {
        return param;
    }

    @Override
    public void setParam(Object val) {
        this.param = val;
    }

    @Override
    public boolean isValid() {
        return param != null;
    }

}
