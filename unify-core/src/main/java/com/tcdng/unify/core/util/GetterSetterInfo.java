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
package com.tcdng.unify.core.util;

import java.lang.reflect.Method;

/**
 * Getter setter information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class GetterSetterInfo {

	private String name;

	private Method getter;

	private Method setter;

	private Class<?> type;

	private Class<?> argumentType;

	private boolean field;

	public GetterSetterInfo(String name, Method getter, Method setter, Class<?> type, Class<?> argumentType,
			boolean field) {
		this.name = name;
		this.getter = getter;
		this.setter = setter;
		this.type = type;
		this.argumentType = argumentType;
		this.field = field;
	}

	public String getName() {
		return name;
	}

	public Method getGetter() {
		return getter;
	}

	public Method getSetter() {
		return setter;
	}

	public Class<?> getType() {
		return type;
	}

	public Class<?> getArgumentType() {
		return argumentType;
	}

	public boolean isGetter() {
		return this.getter != null;
	}

	public boolean isSetter() {
		return this.setter != null;
	}

	public boolean isParameterArgumented() {
		return this.argumentType != null;
	}

	public boolean isProperty() {
		return field;
	}
}
