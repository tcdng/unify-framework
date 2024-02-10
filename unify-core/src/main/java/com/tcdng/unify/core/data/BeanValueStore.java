/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.core.data;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * A bean value store.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class BeanValueStore extends AbstractSingleObjectValueStore<Object> {

    public BeanValueStore(Object valueBean) {
        this(valueBean, null, -1);
    }

    public BeanValueStore(Object valueBean, String dataMarker, int dataIndex) {
        super(valueBean, dataMarker, dataIndex);
    }

    @Override
    public boolean isGettable(String name) throws UnifyException {
        return isTempValue(name) || (storage != null && ReflectUtils.isGettableField(storage.getClass(), name));
    }

    @Override
    public boolean isSettable(String name) throws UnifyException {
        return storage != null && ReflectUtils.isSettableField(storage.getClass(), name);
    }

    @Override
    protected Object doRetrieve(String property) throws UnifyException {
    	Object val = getTempValue(property);
        return val == null ? ReflectUtils.findNestedBeanProperty(storage, property) : val;
    }

    @Override
    protected void doStore(String property, Object value, Formatter<?> formatter) throws UnifyException {
        DataUtils.setNestedBeanProperty(storage, property, value, formatter);
    }
}
