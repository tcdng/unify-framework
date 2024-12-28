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

import java.io.OutputStream;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * A resource controller.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface ResourceStreamer extends UnifyComponent {

    /**
     * Sets the resource name.
     * 
     * @param resourceName
     *            the resource name to set
	 * @return the resource length otherwise zero
	 * @throws UnifyException if an error occurs
	 */
    int setResourceName(String resourceName) throws UnifyException;


	/**
	 * Streams the resource.
	 * 
	 * @param outputStream the stream to write to
	 * @throws UnifyException if an error occurs
	 */
	void stream(OutputStream outputStream) throws UnifyException;
}