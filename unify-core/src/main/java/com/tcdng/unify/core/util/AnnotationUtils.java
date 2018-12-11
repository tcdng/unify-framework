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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Parameter;
import com.tcdng.unify.core.annotation.Parameters;
import com.tcdng.unify.core.annotation.Schedulable;
import com.tcdng.unify.core.constant.AnnotationConstants;

/**
 * Provides utility methods for annotations.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class AnnotationUtils {

	public static String getAnnotationString(String value) {
		if (AnnotationConstants.NONE.equals(value)) {
			return null;
		}
		return value;
	}

	public static String getComponentName(Class<?> clazz) throws UnifyException {
		String name = null;
		Component ca = clazz.getAnnotation(Component.class);
		if (ca != null) {
			name = getAnnotationString(ca.value());
			if (StringUtils.isBlank(name)) {
				name = getAnnotationString(ca.name());
			}
		}

		return name;
	}

	public static List<Parameter> getParameters(Class<?> typeClazz) throws UnifyException {
		Map<String, Parameter> map = new LinkedHashMap<String, Parameter>();
		Schedulable sa = typeClazz.getAnnotation(Schedulable.class);
		if (sa != null) {
			for (Parameter pa : sa.parameters()) {
				map.put(pa.name(), pa);
			}
		}

		for (Class<?> type : ReflectUtils.getClassHierachyList(typeClazz)) {
			Parameters paa = type.getAnnotation(Parameters.class);
			if (paa != null) {
				for (Parameter pa : paa.value()) {
					map.put(pa.name(), pa);
				}
			}
		}

		return new ArrayList<Parameter>(map.values());
	}
}
