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

import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Abstract single storage value store.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractSingleObjectValueStore<T> extends AbstractValueStore {

    protected T storage;

    private String dataMarker;

    private int dataIndex;

    private Map<String, Object> temp;

    public AbstractSingleObjectValueStore(T storage, String dataMarker, int dataIndex) {
        this.storage = storage;
        this.dataMarker = dataMarker;
        this.dataIndex = dataIndex;
    }

    @Override
    public Object retrieve(int storageIndex, String name) throws UnifyException {
        return retrieve(name);
    }

    @Override
    public Object retrieve(String name) throws UnifyException {
        return retrieveInternal(name);
    }

    @Override
    public <U> U retrieve(Class<U> type, String name) throws UnifyException {
        return DataUtils.convert(type, retrieveInternal(name));
    }

    @Override
    public <U> U retrieve(Class<U> type, String name, Formatter<?> formatter) throws UnifyException {
        return DataUtils.convert(type, retrieveInternal(name), formatter);
    }

    @Override
    public <U> U retrieve(Class<U> type, int storageIndex, String name) throws UnifyException {
        return DataUtils.convert(type, retrieve(storageIndex, name));
    }

    @Override
    public void store(int storageIndex, String name, Object value) throws UnifyException {
        store(name, value);
    }

    @Override
    public void store(int storageIndex, String name, Object value, Formatter<?> formatter) throws UnifyException {
        store(name, value, formatter);
    }

    @Override
    public void store(String name, Object value) throws UnifyException {
        storeInternal(name, value, null);
    }

    @Override
    public void store(String name, Object value, Formatter<?> formatter) throws UnifyException {
        storeInternal(name, value, formatter);
    }

    @Override
    public void storeOnNull(int storageIndex, String name, Object value) throws UnifyException {
        if (retrieve(storageIndex, name) == null) {
            store(name, value);
        }
    }

    @Override
    public void storeOnNull(int storageIndex, String name, Object value, Formatter<?> formatter) throws UnifyException {
        if (retrieve(storageIndex, name) == null) {
            store(name, value, formatter);
        }
    }

    @Override
    public void storeOnNull(String name, Object value) throws UnifyException {
        if (retrieve(name) == null) {
            storeInternal(name, value, null);
        }
    }

    @Override
    public void storeOnNull(String name, Object value, Formatter<?> formatter) throws UnifyException {
        if (retrieve(name) == null) {
            storeInternal(name, value, formatter);
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
	public void setTempValues(Map<String, Object> values) throws UnifyException {
		if (values != null) {
	        if (temp == null) {
	            temp = new HashMap<String, Object>();
	        }

	        temp.putAll(values);
		}
	}

	@Override
	public Object removeTempValue(String name) throws UnifyException {
        if (temp != null) {
            return temp.remove(name);
        }
        
		return null;
	}

	@Override
    public boolean isTempValue(String name) {
        if (temp != null) {
            return temp.containsKey(name);
        }

        return false;
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
    public int size() {
        return 0;
    }

    @Override
    public Object getValueObject() {
        return storage;
    }

    @Override
    public Object getValueObjectAtDataIndex() {
        return storage;
    }

 	@Override
	public <U> U getValueObject(Class<U> type) {
		return type.cast(storage);
	}

	@Override
	public <U> U getValueObjectAtDataIndex(Class<U> type) {
		return type.cast(storage);
	}

	@Override
    protected void doSetDataIndex(int dataIndex) {
        this.dataIndex = dataIndex;
    }

    @Override
    protected Class<?> getDataClass() throws UnifyException {
        return storage.getClass();
    }

    protected abstract Object doRetrieve(String property) throws UnifyException;

    protected abstract void doStore(String property, Object value, Formatter<?> formatter) throws UnifyException;

    private Object retrieveInternal(String property) throws UnifyException {
        ValueStorePolicy policy = getPolicy();
        return policy != null ? policy.onRetrieve(this, property, doRetrieve(property)) : doRetrieve(property);
    }

    private void storeInternal(String property, Object val, Formatter<?> formatter) throws UnifyException {
        ValueStorePolicy policy = getPolicy();
        if (policy != null) {
            val = policy.onStore(this, property, val);
        }

        doStore(property, val, formatter);
    }
}
