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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Parameters.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class Parameters {

	private Map<String, Object> values;

	public Parameters(Map<String, Object> values) {
		this.values = values;
	}

	public Parameters() {
		this.values = new ConcurrentHashMap<String, Object>();
	}

	public Set<String> getParamNames() {
		return values.keySet();
	}

	public void setParam(String name, Object val) {
		values.put(name, val);
	}
	
	public boolean isParam(String name) {
		return values.containsKey(name);
	}

	public Object getParam(String name) throws UnifyException {
		return values.get(name);
	}

	public <T> T getParam(Class<T> type, String name) throws UnifyException {
		return DataUtils.convert(type, values.get(name));
	}
}
