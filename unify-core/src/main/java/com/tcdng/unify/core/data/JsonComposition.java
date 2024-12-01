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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON composition.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class JsonComposition {

	private String root;

	private Map<String, JsonObjectComposition> objects;

	public JsonComposition(List<JsonObjectComposition> objects) {
		this.root = objects.get(0).getName();
		this.objects = new LinkedHashMap<String, JsonObjectComposition>();
		for (JsonObjectComposition object: objects) {
			this.objects.put(object.getName(), object);
		}
	}

	public String getRoot() {
		return root;
	}

	public JsonObjectComposition getObjectComposition(String name) {
		return objects.get(name);
	}

}