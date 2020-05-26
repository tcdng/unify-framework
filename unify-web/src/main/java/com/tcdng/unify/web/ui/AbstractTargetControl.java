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

package com.tcdng.unify.web.ui;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;

/**
 * Base class for controls with target element.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@UplAttributes({
    @UplAttribute(name = "staticBindingValue", type = String.class),
    @UplAttribute(name = "alwaysValueIndex", type = boolean.class),
    @UplAttribute(name = "debounce", type = boolean.class)})
public abstract class AbstractTargetControl extends AbstractControl implements TargetControl {

    @Override
    public String getStaticBindingValue() throws UnifyException {
        return getUplAttribute(String.class, "staticBindingValue");
    }

    @Override
    public String getTargetId() throws UnifyException {
        return getPrefixedId("trg_");
    }

    @Override
    public boolean isAlwaysValueIndex() throws UnifyException {
        return getUplAttribute(boolean.class, "alwaysValueIndex");
    }

    @Override
    public boolean isDebounce() throws UnifyException {
        return getUplAttribute(boolean.class, "debounce");
    }
}
