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

package com.tcdng.unify.core.util;

import com.tcdng.unify.core.UnifyException;

/**
 * Class for name provider.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface ClassForNameProvider {

	/**
	 * Gets class by name.
	 * 
	 * @param className the class name
	 * @return type if found otherwise null
	 * @throws UnifyException if an error occurs
	 */
	Class<?> classForName(String className) throws UnifyException;
	
	/**
	 * Gets the type argument for a list field.
	 * 
	 * @param className the class name
	 * @param fieldName the class field name
	 * @return the list argument type if found otherwise null
	 * @throws UnifyException if an error occurs
	 */
	String getListTypeArgument(String className, String fieldName) throws UnifyException;
}
