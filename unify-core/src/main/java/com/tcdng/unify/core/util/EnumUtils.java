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
package com.tcdng.unify.core.util;

import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.data.FactoryMap;

/**
 * Enumeration utilities.
 * 
 * @author Lateef Ojulari
 * @version 1.0
 */
public final class EnumUtils {

	private static FactoryMap<Class<? extends EnumConst>, EnumConstMap> enumConstMap = new FactoryMap<Class<? extends EnumConst>, EnumConstMap>() {
		@Override
		protected EnumConstMap create(Class<? extends EnumConst> key, Object... params) throws Exception {
			Map<String, EnumConst> map = new HashMap<String, EnumConst>();
			for (EnumConst enumConst : key.getEnumConstants()) {
				map.put(enumConst.code(), enumConst);
			}
			return new EnumConstMap(key);
		}
	};

	private EnumUtils() {

	}

	@SuppressWarnings("unchecked")
	public static <T extends EnumConst> T fromCode(Class<T> clazz, String code) {
		try {
			return (T) enumConstMap.get(clazz).getByCode(code);
		} catch (Exception e) {
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends EnumConst> T fromName(Class<T> clazz, String name) {
		try {
			return (T) enumConstMap.get(clazz).getByName(name);
		} catch (Exception e) {
		}
		return null;
	}

	private static class EnumConstMap {

		private Map<String, EnumConst> mapByCode;

		private Map<String, EnumConst> mapByName;

		public EnumConstMap(Class<? extends EnumConst> key) {
			this.mapByCode = new HashMap<String, EnumConst>();
			this.mapByName = new HashMap<String, EnumConst>();
			for (EnumConst enumConst : key.getEnumConstants()) {
				this.mapByCode.put(enumConst.code(), enumConst);
				this.mapByName.put(enumConst.name().toLowerCase(), enumConst);
			}
		}

		public EnumConst getByCode(String code) {
			return this.mapByCode.get(code);
		}

		public EnumConst getByName(String name) {
			return this.mapByName.get(name.toLowerCase());
		}
	}
}
