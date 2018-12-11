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
package com.tcdng.unify.core.task;

import java.lang.reflect.Method;

/**
 * A periodic execution information object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class PeriodicExecutionInfo {

	private String componentName;

	private Method method;

	public PeriodicExecutionInfo(String componentName, Method method) {
		this.componentName = componentName;
		this.method = method;
	}

	public String getComponentName() {
		return componentName;
	}

	public Method getMethod() {
		return method;
	}
}
