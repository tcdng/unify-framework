/*
 * Copyright 2018 The Code Department
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

/**
 * Abstract value store.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractValueStore<T> implements ValueStore {

	protected T storage;

	private int dataIndex;

	public AbstractValueStore(T storage, int dataIndex) {
		this.storage = storage;
		this.dataIndex = dataIndex;
	}

	@Override
	public Object retrieve(int storageIndex, String name) throws UnifyException {
		return retrieve(name);
	}

	@Override
	public Object retrieve(String name) throws UnifyException {
		return doRetrieve(name);
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
		doStore(name, value, null);
	}

	@Override
	public void store(String name, Object value, Formatter<?> formatter) throws UnifyException {
		doStore(name, value, formatter);
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

	protected abstract Object doRetrieve(String property) throws UnifyException;

	protected abstract void doStore(String property, Object value, Formatter<?> formatter) throws UnifyException;

}
