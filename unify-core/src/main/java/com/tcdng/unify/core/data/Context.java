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

import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;

/**
 * An abstract class that represents a context. Manages basic context attribute
 * storage and retrieval.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class Context {

	private Map<String, Object> attributes;

	public Context() {
		this.attributes = new HashMap<String, Object>();
	}

	public boolean hasAttributes() {
		return !this.attributes.isEmpty();
	}

	public void setAttribute(String name, Object value) throws UnifyException {
		this.attributes.put(name, value);
	}

	public Object getAttribute(String name) throws UnifyException {
		return this.attributes.get(name);
	}

	public Object removeAttribute(String name) throws UnifyException {
		if (this.attributes != null) {
			return this.attributes.remove(name);
		}
		return null;
	}

	public void removeAttributes(String... names) throws UnifyException {
		if (this.attributes != null) {
			for (String name : names) {
				this.attributes.remove(name);
			}
		}
	}

	public void clearAttributes() {
		if (this.attributes != null) {
			this.attributes.clear();
		}
	}

	public boolean isAttribute(String name) {
		return attributes.containsKey(name);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<context>\n");
		sb.append("id = ").append(super.toString()).append("\n");
		for (Map.Entry<String, Object> entry : this.attributes.entrySet()) {
			sb.append("<attribute name=\"").append(entry.getKey()).append("\" value=\"").append(entry.getValue())
					.append("\"/>\n");
		}
		sb.append("</context>");
		return sb.toString();
	}

	protected Map<String, Object> getAttributes() {
		return attributes;
	}
}
