/*
 * Copyright (c) 2018-2025 The Code Department.
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
package com.tcdng.unify.core.resource;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Image provider.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface ImageProvider extends UnifyComponent {

	/**
	 * Provides an image as byte array.
	 * 
	 * @param name the image name
	 * @return the image
	 * @throws UnifyException if an error occurs
	 */
	byte[] provideAsByteArray(String name) throws UnifyException;

	/**
	 * Provides an image as base 64 string.
	 * 
	 * @param name the image name
	 * @return the image
	 * @throws UnifyException if an error occurs
	 */
	String provideAsBase64String(String name) throws UnifyException;
}
