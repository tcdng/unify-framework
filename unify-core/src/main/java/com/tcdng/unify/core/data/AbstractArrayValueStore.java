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

import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Abstract array value store.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractArrayValueStore<T> implements ValueStore {

    private T[] storage;

    private String dataMarker;

    private int dataIndex;

    private Map<String, Object> temp;

    public AbstractArrayValueStore(T[] storage, String dataMarker, int dataIndex) {
        this.storage = storage;
        this.dataMarker = dataMarker;
        this.dataIndex = dataIndex;
    }

    @Override
    public Object retrieve(int storageIndex, String name) throws UnifyException {
        return doRetrieve(storage[storageIndex], name);
    }

    @Override
    public Object retrieve(String name) throws UnifyException {
        return doRetrieve(storage[dataIndex], name);
    }

    @Override
    public <U> U retrieve(Class<U> type, String name) throws UnifyException {
        return DataUtils.convert(type, retrieve(name), null);
    }

    @Override
    public <U> U retrieve(Class<U> type, int storageIndex, String name) throws UnifyException {
        return DataUtils.convert(type, retrieve(storageIndex, name), null);
    }

    @Override
    public void store(int storageIndex, String name, Object value) throws UnifyException {
        doStore(storage[storageIndex], name, value, null);
    }

    @Override
    public void store(int storageIndex, String name, Object value, Formatter<?> formatter) throws UnifyException {
        doStore(storage[storageIndex], name, value, formatter);
    }

    @Override
    public void store(String name, Object value) throws UnifyException {
        doStore(storage[dataIndex], name, value, null);
    }

    @Override
    public void store(String name, Object value, Formatter<?> formatter) throws UnifyException {
        doStore(storage[dataIndex], name, value, formatter);
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
            return DataUtils.convert(type, temp.get(name), null);
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
        return doGettable(storage[dataIndex], name);
    }

    @Override
    public boolean isSettable(String name) throws UnifyException {
        return doSettable(storage[dataIndex], name);
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

    protected abstract boolean doSettable(T storage, String property) throws UnifyException;

    protected abstract boolean doGettable(T storage, String property) throws UnifyException;

    protected abstract Object doRetrieve(T storage, String property) throws UnifyException;

    protected abstract void doStore(T storage, String property, Object value, Formatter<?> formatter)
            throws UnifyException;

}
