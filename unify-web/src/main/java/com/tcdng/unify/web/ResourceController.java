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

import java.io.OutputStream;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;

/**
 * Serves as the controller component for a resource request.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface ResourceController extends UserInterfaceController {

	/**
	 * Returns the resource content type.
	 */
	String getContentType();

	/**
	 * Sets the content type.
	 * 
	 * @param contentType
	 *            the content type to set
	 */
	void setContentType(String contentType);

	/**
	 * Sets the resource name.
	 * 
	 * @param resourceName
	 *            the resource name to set
	 */
	void setResourceName(String resourceName);

	/**
	 * Sets the attachment flag for this resource
	 * 
	 * @param attachment
	 *            the flag to set
	 */
	void setAttachment(boolean attachment);

	/**
	 * Gets resource meta-data keys.
	 * 
	 * @return A set of meta-data keys
	 */
	Set<String> getMetaDataKeys();

	/**
	 * Gets a meta data by name.
	 * 
	 * @param name
	 *            the non-case sensitive meta data name
	 * @return the meta data if found, otherwise null
	 */
	String getMetaData(String name);

	/**
	 * Prepare for execution.
	 * 
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void prepareExecution() throws UnifyException;

	/**
	 * Executes resource controller action.
	 * 
	 * @param outputStream
	 *            the output stream to stream resource to
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void execute(OutputStream outputStream) throws UnifyException;
}
