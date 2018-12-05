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
package com.tcdng.unify.web;

import java.lang.reflect.Method;

import com.tcdng.unify.core.util.StringUtils;

/**
 * Remote call handler.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class RemoteCallHandler {

	private String methodCode;

	private Method method;

	private boolean restricted;

	public RemoteCallHandler(String methodCode, Method method, boolean restricted) {
		this.methodCode = methodCode;
		this.method = method;
		this.restricted = restricted;
	}

	public String getMethodCode() {
		return methodCode;
	}

	public String getName() {
		return method.getName();
	}

	public Method getMethod() {
		return method;
	}

	public boolean isRestricted() {
		return restricted;
	}

	@SuppressWarnings("unchecked")
	public Class<? extends RemoteCallParams> getParamType() {
		return (Class<? extends RemoteCallParams>) method.getParameterTypes()[0];
	}

	@SuppressWarnings("unchecked")
	public Class<? extends RemoteCallResult> getReturnType() {
		return (Class<? extends RemoteCallResult>) method.getReturnType();
	}

	public String toString() {
		return StringUtils.beanToString(this);
	}
}
