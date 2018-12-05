/*
 * Copyright 2014 The Code Department
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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A fluent generic map.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class FluentMap<T, U> {

	private Map<T, U> map;

	public FluentMap() {
		map = new LinkedHashMap<T, U>();
	}

	public FluentMap(FluentMap<T, U> genericMap) {
		this();
		this.map.putAll(genericMap.map);
	}

	public FluentMap<T, U> addAll(Map<? extends T, ? extends U> map) {
		this.map.putAll(map);
		return this;
	}

	public FluentMap<T, U> add(T key, U value) {
		map.put(key, value);
		return this;
	}

	public FluentMap<T, U> remove(T key) {
		map.remove(key);
		return this;
	}

	public FluentMap<T, U> clear() {
		map.clear();
		return this;
	}

	public Set<T> keySet() {
		return this.map.keySet();
	}

	public Set<Map.Entry<T, U>> entrySet() {
		return this.map.entrySet();
	}

	public Collection<U> values() {
		return this.map.values();
	}

	public U get(T key) {
		return map.get(key);
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}
}
