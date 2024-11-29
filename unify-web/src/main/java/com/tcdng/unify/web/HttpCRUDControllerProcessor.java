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
package com.tcdng.unify.web;

import java.util.Map;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.data.Response;
import com.tcdng.unify.web.http.HttpRequestHeaders;

/**
 * HTTP CRUD controller processor.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface HttpCRUDControllerProcessor extends UnifyComponent {

	/**
	 * Performs a create operation.
	 * 
	 * @param resource   the resource name
	 * @param headers    the request headers
	 * @param parameters the request parameters
	 * @param body       the request body
	 * @return the response object
	 * @throws UnifyException if an error occurs
	 */
	Response create(String resource, HttpRequestHeaders headers, Map<String, Object> parameters, String body)
			throws UnifyException;

	/**
	 * Performs a read operation.
	 * 
	 * @param resource   the resource name
	 * @param headers    the request headers
	 * @param parameters the request parameters
	 * @return the response object
	 * @throws UnifyException if an error occurs
	 */
	Response read(String resource, HttpRequestHeaders headers, Map<String, Object> parameters) throws UnifyException;

	/**
	 * Performs an update operation.
	 * 
	 * @param resource   the resource name
	 * @param headers    the request headers
	 * @param parameters the request parameters
	 * @param body       the request body
	 * @return the response object
	 * @throws UnifyException if an error occurs
	 */
	Response update(String resource, HttpRequestHeaders headers, Map<String, Object> parameters, String body)
			throws UnifyException;

	/**
	 * Performs a delete operation.
	 * 
	 * @param resource   the resource name
	 * @param headers    the request headers
	 * @param parameters the request parameters
	 * @return the response object
	 * @throws UnifyException if an error occurs
	 */
	Response delete(String resource, HttpRequestHeaders headers, Map<String, Object> parameters)
			throws UnifyException;
}
