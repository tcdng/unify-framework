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
package com.tcdng.unify.core.util;

import java.util.List;

/**
 * Provides utility methods for queries.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class QueryUtils {

	private QueryUtils() {

	}

	/**
	 * Tests if supplied string is not null and is not blank.
	 * 
	 * @param value
	 *            the value to test
	 */
	public static boolean isValidStringCriteria(String value) {
		return value != null && !value.trim().isEmpty();
	}

	/**
	 * Tests if supplied string array is not null and has at least one element
	 * (element can be null).
	 * 
	 * @param value
	 *            the array to test
	 */
	public static boolean isValidStringArrayCriteria(String[] value) {
		return value != null && value.length > 0;
	}

	/**
	 * Tests if supplied long value is not null and is not zero.
	 * 
	 * @param value
	 *            the value to test
	 */
	public static boolean isValidLongCriteria(Long value) {
		return value != null && !value.equals(Long.valueOf(0L));
	}

	/**
	 * Tests if supplied long array is not null and has at least one element
	 * (element can be null).
	 * 
	 * @param value
	 *            the array to test
	 */
	public static boolean isValidLongArrayCriteria(Long[] value) {
		return value != null && value.length > 0;
	}

	/**
	 * Tests if supplied list is not null and has at least one element
	 * 
	 * @param value
	 *            the array to test
	 */
	public static boolean isValidListCriteria(List<?> value) {
		return value != null && !value.isEmpty();
	}

	/**
	 * Returns true if supplied value is reserved.
	 * 
	 * @param value
	 *            the value to testr
	 */
	public static boolean isReserved(Long value) {
		return value <= 0L;
	}
}
