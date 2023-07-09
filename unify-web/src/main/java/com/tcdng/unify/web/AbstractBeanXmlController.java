/*
 * Copyright 2018-2023 The Code Department.
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

import com.tcdng.unify.core.UnifyException;

/**
 * Abstract bean XML controller.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractBeanXmlController<T, U> extends AbstractPlainXmlController {

    private final Class<T> responseClass;

    private final Class<U> requestClass;

	public AbstractBeanXmlController(Class<T> responseClass, Class<U> requestClass) {
		this.responseClass = responseClass;
		this.requestClass = requestClass;
	}

	@Override
	protected final String doExecute(String xmlRequest) throws UnifyException {
		U request = getObjectFromRequestXml(requestClass, xmlRequest);
		T response = doExecute(request);
		return getResponseXmlFromObject(response);
	}

	protected final Class<T> getResponseClass() {
		return responseClass;
	}

	protected final Class<U> getRequestClass() {
		return requestClass;
	}

	protected abstract T doExecute(U request) throws UnifyException;

}
