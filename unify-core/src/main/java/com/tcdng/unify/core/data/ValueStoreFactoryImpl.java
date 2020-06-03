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

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;

/**
 * Default implementation of a value store factory.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_VALUESTOREFACTORY)
public class ValueStoreFactoryImpl extends AbstractUnifyComponent implements ValueStoreFactory {

    @Override
    public ValueStore getValueStore(Object storageObject) throws UnifyException {
        return getValueStore(storageObject, null, -1);
    }

    @Override
    public ValueStore getValueStore(Object storageObject, String dataMarker, int dataIndex) throws UnifyException {
        if (storageObject != null) {
            if (storageObject instanceof PackableDoc) {
                return new PackableDocStore((PackableDoc) storageObject, dataMarker, dataIndex);
            }

            if (storageObject instanceof MapValues) {
                return new MapValuesStore((MapValues) storageObject, dataMarker, dataIndex);
            }

            return new BeanValueStore(storageObject, dataMarker, dataIndex);
        }

        return null;
    }

    @Override
    public ValueStore getArrayValueStore(Object[] storageObject, String dataMarker, int dataIndex)
            throws UnifyException {
        if (storageObject != null) {
            if (storageObject instanceof PackableDoc[]) {
                return new PackableDocArrayStore((PackableDoc[]) storageObject, dataMarker, dataIndex);
            }

            if (storageObject instanceof MapValues[]) {
                return new MapValuesArrayStore((MapValues[]) storageObject, dataMarker, dataIndex);
            }

            return new BeanValueArrayStore(storageObject, dataMarker, dataIndex);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ValueStore getListValueStore(Class<T> clazz, List<T> storageObject, String dataMarker,
            int dataIndex) throws UnifyException {
        if (storageObject != null) {
            if (PackableDoc.class.equals(clazz)) {
                return new PackableDocListStore((List<PackableDoc>) storageObject, dataMarker, dataIndex);
            }

            if (MapValues.class.equals(clazz)) {
                return new MapValuesListStore((List<MapValues>) storageObject, dataMarker, dataIndex);
            }

            return new BeanValueListStore((List<Object>) storageObject, dataMarker, dataIndex);
        }

        return null;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }
}
