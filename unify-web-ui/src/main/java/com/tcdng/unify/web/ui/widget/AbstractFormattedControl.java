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
package com.tcdng.unify.web.ui.widget;

import java.util.Collection;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.web.ui.DataTransferBlock;

/**
 * Serves as a convenient base class for controls with formatted data.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@UplAttributes({ @UplAttribute(name = "formatter", type = Formatter.class) })
public abstract class AbstractFormattedControl extends AbstractControl {

    @Override
    public void populate(DataTransferBlock transferBlock) throws UnifyException {
        String binding = getBinding();
        if (binding != null) {
            getValueStore().store(transferBlock.getItemIndex(), binding, transferBlock.getValue(), getFormatter());
        }
    }

    @Override
    public <T> T getValue(Class<T> clazz) throws UnifyException {
        return DataUtils.convert(clazz, getValue(), getFormatter());
    }

    @Override
    public String getStringValue() throws UnifyException {
        return DataUtils.convert(String.class, getValue(), getFormatter());
    }

    @Override
    public <T, U extends Collection<T>> U getValue(Class<U> clazz, Class<T> dataClass) throws UnifyException {
        return DataUtils.convert(clazz, dataClass, getValue(), getFormatter());
    }

    @SuppressWarnings("unchecked")
    public Formatter<Object> getFormatter() throws UnifyException {
        return (Formatter<Object>) getUplAttribute(Formatter.class, "formatter");
    }
}
