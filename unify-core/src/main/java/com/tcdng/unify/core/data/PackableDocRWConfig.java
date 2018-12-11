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

package com.tcdng.unify.core.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;

/**
 * Packable document read/write configuration.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class PackableDocRWConfig {

	private Class<?> beanType;

	private Map<String, FieldMapping> fieldMappings;

	private Map<String, String> beanToDocNames;

	public PackableDocRWConfig(Class<?> beanType, FieldMapping... fieldMappings) {
		this(beanType, Arrays.asList(fieldMappings));
	}

	public PackableDocRWConfig(Class<?> beanType, List<FieldMapping> fieldMappingList) {
		this.beanType = beanType;
		fieldMappings = new HashMap<String, FieldMapping>();
		for (FieldMapping fieldMapping : fieldMappingList) {
			fieldMappings.put(fieldMapping.getDocFieldName(), fieldMapping);
		}
	}

	public Class<?> getBeanType() {
		return beanType;
	}

	public FieldMapping getFieldMapping(String docFieldName) throws UnifyException {
		FieldMapping fMapping = fieldMappings.get(docFieldName);
		if (fMapping == null) {
			throw new UnifyException(UnifyCoreErrorConstants.DOCUMENT_FIELDMAPPING_NOT_FOUND, docFieldName);
		}
		return fMapping;
	}

	public Collection<FieldMapping> getFieldMappings() {
		return fieldMappings.values();
	}

	public String getMappedBeanField(String docFieldName) throws UnifyException {
		return getFieldMapping(docFieldName).getBeanFieldName();
	}

	public String getMappedDocField(String beanFieldName) {
		return getBeanToDocNames().get(beanFieldName);
	}

	public static class FieldMapping {

		private String docFieldName;

		private String beanFieldName;

		private PackableDocRWConfig packableDocRWConfig;

		public FieldMapping(String docFieldName, String beanFieldName) {
			this.docFieldName = docFieldName;
			this.beanFieldName = beanFieldName;
		}

		public FieldMapping(String docFieldName, String beanFieldName, Class<?> beanType,
				FieldMapping... fieldMappings) {
			this(docFieldName, beanFieldName, beanType, Arrays.asList(fieldMappings));
		}

		public FieldMapping(String docFieldName, String beanFieldName, Class<?> beanType,
				List<FieldMapping> fieldMappingList) {
			this.docFieldName = docFieldName;
			this.beanFieldName = beanFieldName;
			this.packableDocRWConfig = new PackableDocRWConfig(beanType, fieldMappingList);
		}

		public String getDocFieldName() {
			return docFieldName;
		}

		public String getBeanFieldName() {
			return beanFieldName;
		}

		public PackableDocRWConfig getPackableDocRWConfig() {
			return packableDocRWConfig;
		}

		public boolean isComplex() {
			return this.packableDocRWConfig != null;
		}
	}

	private Map<String, String> getBeanToDocNames() {
		if (beanToDocNames == null) {
			beanToDocNames = new HashMap<String, String>();
			for (FieldMapping fieldMapping : fieldMappings.values()) {
				beanToDocNames.put(fieldMapping.getBeanFieldName(), fieldMapping.getDocFieldName());
			}
		}

		return beanToDocNames;
	}
}
