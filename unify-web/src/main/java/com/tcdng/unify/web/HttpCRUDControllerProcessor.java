/*
 * Copyright 2018-2025 The Code Department.
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

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.Parameters;
import com.tcdng.unify.web.data.Response;
import com.tcdng.unify.web.http.HttpRequestHeaders;

/**
 * HTTP CRUD controller processor.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface HttpCRUDControllerProcessor extends UnifyComponent {

	/**
	 * Performs a count operation.
	 * 
	 * @param headers    the request headers
	 * @param parameters the request parameters
	 * @return the response object
	 * @throws UnifyException if an error occurs
	 */
	Response count(HttpRequestHeaders headers, Parameters parameters) throws UnifyException;

	/**
	 * Performs a create operation.
	 * 
	 * @param headers    the request headers
	 * @param parameters the request parameters
	 * @param body       the request body
	 * @return the response object
	 * @throws UnifyException if an error occurs
	 */
	Response create(HttpRequestHeaders headers, Parameters parameters, String body) throws UnifyException;

	/**
	 * Performs a read operation.
	 * 
	 * @param headers    the request headers
	 * @param parameters the request parameters
	 * @param resourceId optional resource ID
	 * @return the response object
	 * @throws UnifyException if an error occurs
	 */
	Response read(HttpRequestHeaders headers, Parameters parameters, Long resourceId) throws UnifyException;

	/**
	 * Performs an update operation.
	 * 
	 * @param headers    the request headers
	 * @param parameters the request parameters
	 * @param body       the request body
	 * @param resourceId the resource ID
	 * @return the response object
	 * @throws UnifyException if an error occurs
	 */
	Response update(HttpRequestHeaders headers, Parameters parameters, String body, Long resourceId)
			throws UnifyException;

	/**
	 * Performs a delete operation.
	 * 
	 * @param headers    the request headers
	 * @param parameters the request parameters
	 * @param resourceId the resource ID
	 * @return the response object
	 * @throws UnifyException if an error occurs
	 */
	Response delete(HttpRequestHeaders headers, Parameters parameters, Long resourceId) throws UnifyException;

	/**
	 * Checks is processor supports create
	 * 
	 * @return true if supported otherwise false
	 * @throws UnifyException if an error occurs
	 */
	boolean isSupportCreate() throws UnifyException;

	/**
	 * Checks is processor supports read
	 * 
	 * @return true if supported otherwise false
	 * @throws UnifyException if an error occurs
	 */
	boolean isSupportRead() throws UnifyException;

	/**
	 * Checks is processor supports update
	 * 
	 * @return true if supported otherwise false
	 * @throws UnifyException if an error occurs
	 */
	boolean isSupportUpdate() throws UnifyException;

	/**
	 * Checks is processor supports delete
	 * 
	 * @return true if supported otherwise false
	 * @throws UnifyException if an error occurs
	 */
	boolean isSupportDelete() throws UnifyException;
}
