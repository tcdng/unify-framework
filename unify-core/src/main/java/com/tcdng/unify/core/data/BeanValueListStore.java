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

package com.tcdng.unify.core.data;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Bean value list store.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class BeanValueListStore extends AbstractListValueStore<Object> {

    public BeanValueListStore(List<Object> storage, String dataMarker, int dataIndex) {
        super(storage, dataMarker, dataIndex);
    }

    @Override
    protected boolean doSettable(Object storage, String property) throws UnifyException {
        return storage != null && ReflectUtils.isSettableField(storage.getClass(), property);
    }

    @Override
    protected boolean doGettable(Object storage, String property) throws UnifyException {
        return storage != null && ReflectUtils.isGettableField(storage.getClass(), property);
    }

    @Override
    protected Object doRetrieve(Object storage, String property) throws UnifyException {
        return ReflectUtils.findNestedBeanProperty(storage, property);
    }

    @Override
    protected void doStore(Object storage, String property, Object value, Formatter<?> formatter)
            throws UnifyException {
        DataUtils.setNestedBeanProperty(storage, property, value, formatter);
    }

}
