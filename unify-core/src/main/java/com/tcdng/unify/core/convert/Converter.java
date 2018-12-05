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
package com.tcdng.unify.core.convert;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;

/**
 * Used for converting a value to a particular type.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface Converter<T> {

	/**
	 * Convert supplied value to type. Returns a null if value is inconvertible.
	 * 
	 * @param value
	 *            the value to convert
	 * @param formatter
	 *            for conversions that need formatting information. Can be null.
	 * @return the converted value
	 * @throws UnifyException
	 *             if an error occurs
	 */
	T convert(Object value, Formatter<?> formatter) throws UnifyException;
}
