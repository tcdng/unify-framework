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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.GetterSetterInfo;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Abstract list value store.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractListValueStore<T> implements ValueStore {

    protected List<? extends T> storage;

    protected int dataIndex;

    private String dataMarker;

    private Map<String, Object> temp;
    
    private ValueStoreReader reader;
    
    private ValueStoreWriter writer;

    public AbstractListValueStore(List<? extends T> storage, String dataMarker, int dataIndex) {
        this.storage = storage;
        this.dataMarker = dataMarker;
        this.dataIndex = dataIndex;
    }

    @Override
    public Object retrieve(int storageIndex, String name) throws UnifyException {
        checkStorageIndex(storageIndex);
        return doRetrieve(storage.get(storageIndex), name);
    }

    @Override
    public Object retrieve(String name) throws UnifyException {
        return doRetrieve(storage.get(dataIndex), name);
    }

    @Override
    public <U> U retrieve(Class<U> type, String name) throws UnifyException {
        return DataUtils.convert(type, retrieve(name));
    }

    @Override
    public <U> U retrieve(Class<U> type, String name, Formatter<?> formatter) throws UnifyException {
        return DataUtils.convert(type, retrieve(name), formatter);
    }

    @Override
    public <U> U retrieve(Class<U> type, int storageIndex, String name) throws UnifyException {
        checkStorageIndex(storageIndex);
        return DataUtils.convert(type, retrieve(storageIndex, name));
    }

    @Override
    public void store(int storageIndex, String name, Object value) throws UnifyException {
        checkStorageIndex(storageIndex);
        doStore(storage.get(storageIndex), name, value, null);
    }

    @Override
    public void store(int storageIndex, String name, Object value, Formatter<?> formatter) throws UnifyException {
        checkStorageIndex(storageIndex);
        doStore(storage.get(storageIndex), name, value, formatter);
    }

    @Override
    public void store(String name, Object value) throws UnifyException {
        doStore(storage.get(dataIndex), name, value, null);
    }

    @Override
    public void store(String name, Object value, Formatter<?> formatter) throws UnifyException {
        doStore(storage.get(dataIndex), name, value, formatter);
    }

    @Override
    public void storeOnNull(int storageIndex, String name, Object value) throws UnifyException {
        checkStorageIndex(storageIndex);
        if (retrieve(storageIndex, name) == null) {
            doStore(storage.get(storageIndex), name, value, null);
        }
    }

    @Override
    public void storeOnNull(int storageIndex, String name, Object value, Formatter<?> formatter) throws UnifyException {
        checkStorageIndex(storageIndex);
        if (retrieve(storageIndex, name) == null) {
            doStore(storage.get(storageIndex), name, value, formatter);
        }
    }

    @Override
    public void storeOnNull(String name, Object value) throws UnifyException {
        if (retrieve(name) == null) {
            doStore(storage.get(dataIndex), name, value, null);
        }
    }

    @Override
    public void storeOnNull(String name, Object value, Formatter<?> formatter) throws UnifyException {
        if (retrieve(name) == null) {
            doStore(storage.get(dataIndex), name, value, formatter);
        }
    }

    @Override
    public Object getTempValue(String name) throws UnifyException {
        if (temp != null) {
            return temp.get(name);
        }
        
        return null;
    }

    @Override
    public <U> U getTempValue(Class<U> type, String name) throws UnifyException {
        if (temp != null) {
            return DataUtils.convert(type, temp.get(name));
        }
        
        return null;
    }

    @Override
    public void setTempValue(String name, Object value) throws UnifyException {
        if (temp == null) {
            temp = new HashMap<String, Object>();
        }
        
        temp.put(name, value);
    }

    @Override
    public boolean isTempValue(String name) {
        if (temp != null) {
            return temp.containsKey(name);
        }
        
        return false;
    }

    @Override
    public boolean isGettable(String name) throws UnifyException {
        return doGettable(storage.get(dataIndex), name);
    }

    @Override
    public boolean isSettable(String name) throws UnifyException {
        return doSettable(storage.get(dataIndex), name);
    }

    @Override
    public String getDataMarker() {
        return dataMarker;
    }

    @Override
    public void setDataMarker(String dataMarker) {
        this.dataMarker = dataMarker;
    }

    @Override
    public int getDataIndex() {
        return dataIndex;
    }

    @Override
    public void setDataIndex(int dataIndex) {
        this.dataIndex = dataIndex;
    }

    @Override
    public Object getValueObject() {
        return storage;
    }

    @Override
    public ValueStoreReader getReader() {
        if (reader == null) {
            synchronized(this) {
                if(reader == null) {
                    reader = new ValueStoreReader(this);
                }
            }
        }

        return reader;
    }

    @Override
    public ValueStoreWriter getWriter() {
        if (writer == null) {
            synchronized(this) {
                if(writer == null) {
                    writer = new ValueStoreWriter(this);
                }
            }
        }

        return writer;
    }

    @Override
    public void copy(ValueStore source) throws UnifyException {
        for (GetterSetterInfo getterSetterInfo : ReflectUtils.getGetterSetterList(storage.get(dataIndex).getClass())) {
            if (getterSetterInfo.isGetterSetter()) {
                String fieldName = getterSetterInfo.getName();
                store(fieldName, source.retrieve(fieldName));
            }
        }
    }

    @Override
    public void copyWithExclusions(ValueStore source, String... exclusionFieldNames) throws UnifyException {
        Set<String> exclusion = new HashSet<String>(Arrays.asList(exclusionFieldNames));
        for (GetterSetterInfo getterSetterInfo : ReflectUtils.getGetterSetterList(storage.get(dataIndex).getClass())) {
            if (getterSetterInfo.isGetterSetter()) {
                String fieldName = getterSetterInfo.getName();
                if (!exclusion.contains(fieldName)) {
                    store(fieldName, source.retrieve(fieldName));
                }
            }
        }
    }

    @Override
    public void copyWithInclusions(ValueStore source, String... inclusionFieldNames) throws UnifyException {
        Set<String> inclusion = new HashSet<String>(Arrays.asList(inclusionFieldNames));
        for (GetterSetterInfo getterSetterInfo : ReflectUtils.getGetterSetterList(storage.get(dataIndex).getClass())) {
            if (getterSetterInfo.isGetterSetter()) {
                String fieldName = getterSetterInfo.getName();
                if (inclusion.contains(fieldName)) {
                    store(fieldName, source.retrieve(fieldName));
                }
            }
        }
    }

    @Override
    public int size() {
        return storage.size();
    }
    
    private void checkStorageIndex(int storageIndex) throws UnifyException {
        if (storage == null) {
            throw new UnifyException(UnifyCoreErrorConstants.VALUESTORE_STORAGE_INDEX_OUT_BOUNDS, storageIndex, 0);
        }

        if (storageIndex < 0 || storageIndex >= storage.size()) {
            throw new UnifyException(UnifyCoreErrorConstants.VALUESTORE_STORAGE_INDEX_OUT_BOUNDS, storageIndex, storage.size());
        }
    }

    protected abstract boolean doSettable(T storage, String property) throws UnifyException;

    protected abstract boolean doGettable(T storage, String property) throws UnifyException;

    protected abstract Object doRetrieve(T storage, String property) throws UnifyException;

    protected abstract void doStore(T storage, String property, Object value, Formatter<?> formatter)
            throws UnifyException;

}
