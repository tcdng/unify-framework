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

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Map values.
 * 
 * @author Lateef Ojulari
 * @version 1.0
 */
public class MapValues {

	private Map<String, MapValue> map;

	public MapValues() {
		this.map = new HashMap<String, MapValue>();
	}

	public void addValue(String name, Class<?> type) {
		MapValue mapValue = new MapValue(type);
		this.map.put(name, mapValue);
	}

	public void addValue(String name, Class<?> type, Object value) throws UnifyException {
		MapValue mapValue = new MapValue(type);
		if (value != null && !type.isAssignableFrom(value.getClass())) {
			value = DataUtils.convert(type, value);
		}

		mapValue.setValue(value);
		this.map.put(name, mapValue);
	}

	public Object getValue(String name) throws UnifyException {
		MapValue mapValue = this.map.get(name);
		if (mapValue == null) {
			throw new UnifyException(UnifyCoreErrorConstants.MAPVALUESTORE_NO_SUCH_ENTRY, name);
		}
		return mapValue.getValue();
	}

	public void setValue(String name, Object value, Formatter<?> formatter) throws UnifyException {
		MapValue mapValue = this.map.get(name);
		if (mapValue == null) {
			throw new UnifyException(UnifyCoreErrorConstants.MAPVALUESTORE_NO_SUCH_ENTRY, name);
		}
		mapValue.setValue(DataUtils.convert(mapValue.getType(), value, formatter));
	}

	public boolean isMapValue(String name) {
		return this.map.containsKey(name);
	}
}

class MapValue {

	private Class<?> type;

	private Object value;

	public MapValue(Class<?> type) {
		this.type = type;
	}

	public Class<?> getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
