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

package com.tcdng.unify.core.util;

/**
 * Entity type utilities.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class EntityTypeUtils {

	private EntityTypeUtils() {

	}
	
	public static boolean isReservedType(String type) {
		return EntityTypeUtils.isDynamicType(type) || EntityTypeUtils.isDelegateType(type);
	}
	
	public static boolean isDynamicType(String type) {
		return type.charAt(type.length() -1) == 'z' && type.indexOf(".z.") > 0;
	}

	public static boolean isDelegateType(String type) {
		return type.charAt(type.length() -1) == 'u' && type.indexOf(".u.") > 0;
	}
}
