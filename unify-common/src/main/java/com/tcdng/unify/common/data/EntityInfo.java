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
package com.tcdng.unify.common.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.common.constants.ConnectEntityBaseType;
import com.tcdng.unify.common.constants.ConnectFieldDataType;

/**
 * Entity information.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class EntityInfo {

	private String entityManagerFactory;

	private String dataSourceAlias;

	private ConnectEntityBaseType baseType;

	private String name;

	private String description;

	private String tableName;

	private String idFieldName;

	private String versionNoFieldName;

	private String handler;

	private String actionPolicy;

	private boolean dynamic;

	private boolean ignoreOnSync;

	private Class<?> implClass;

	private Map<String, EntityFieldInfo> fieldsByName;

	private List<EntityFieldInfo> refFieldList;

	private List<EntityFieldInfo> fieldList;

	private List<EntityFieldInfo> listOnlyFieldList;

	private List<EntityFieldInfo> childFieldList;

	private List<EntityFieldInfo> childListFieldList;

	private Map<String, String> fieldToLocal;

	private Map<String, String> fieldFromLocal;

	public EntityInfo(String entityManagerFactory, String dataSourceAlias, ConnectEntityBaseType baseType, String name,
			String description, String tableName, String idFieldName, String versionNoFieldName, String handler,
			String actionPolicy, boolean dynamic, boolean ignoreOnSync, Class<?> implClass,
			Map<String, EntityFieldInfo> fieldsByName) {
		this.entityManagerFactory = entityManagerFactory;
		this.dataSourceAlias = dataSourceAlias;
		this.baseType = baseType;
		this.name = name;
		this.description = description;
		this.tableName = tableName;
		this.idFieldName = idFieldName;
		this.versionNoFieldName = versionNoFieldName;
		this.handler = handler;
		this.actionPolicy = actionPolicy;
		this.dynamic = dynamic;
		this.ignoreOnSync = ignoreOnSync;
		this.implClass = implClass;
		this.fieldsByName = fieldsByName;
		this.refFieldList = new ArrayList<EntityFieldInfo>();
		this.fieldList = new ArrayList<EntityFieldInfo>();
		this.listOnlyFieldList = new ArrayList<EntityFieldInfo>();
		this.childFieldList = new ArrayList<EntityFieldInfo>();
		this.childListFieldList = new ArrayList<EntityFieldInfo>();
		for (EntityFieldInfo entityFieldInfo : fieldsByName.values()) {
			if (entityFieldInfo.isRef()) {
				this.refFieldList.add(entityFieldInfo);
			} else if (entityFieldInfo.isListOnly()) {
				this.listOnlyFieldList.add(entityFieldInfo);
			} else if (entityFieldInfo.isChild()) {
				this.childFieldList.add(entityFieldInfo);
			} else if (entityFieldInfo.isChildList()) {
				this.childListFieldList.add(entityFieldInfo);
			} else {
				this.fieldList.add(entityFieldInfo);
			}
		}

		this.refFieldList = Collections.unmodifiableList(this.refFieldList);
		this.fieldList = Collections.unmodifiableList(this.fieldList);
		this.listOnlyFieldList = Collections.unmodifiableList(this.listOnlyFieldList);
		this.childFieldList = Collections.unmodifiableList(this.childFieldList);
		this.childListFieldList = Collections.unmodifiableList(this.childListFieldList);
		this.fieldToLocal = new HashMap<String, String>();
		this.fieldFromLocal = new HashMap<String, String>();
		if (idFieldName != null) {
			this.fieldToLocal.put("id", idFieldName);
			this.fieldFromLocal.put(idFieldName, "id");
		}

		if (versionNoFieldName != null) {
			this.fieldToLocal.put("versionNo", versionNoFieldName);
			this.fieldFromLocal.put(versionNoFieldName, "versionNo");
		}
	}

	public String getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public String getDataSourceAlias() {
		return dataSourceAlias;
	}

	public ConnectEntityBaseType getBaseType() {
		return baseType;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getTableName() {
		return tableName;
	}

	public String getIdFieldName() {
		return idFieldName;
	}

	public String getVersionNoFieldName() {
		return versionNoFieldName;
	}

	public boolean isWithVersionNo() {
		return versionNoFieldName != null;
	}

	public String getHandler() {
		return handler;
	}

	public boolean isWithHandler() {
		return handler != null && !handler.trim().isEmpty();
	}

	public String getActionPolicy() {
		return actionPolicy;
	}

	public boolean isWithActionPolicy() {
		return actionPolicy != null && !actionPolicy.trim().isEmpty();
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public boolean isIgnoreOnSync() {
		return ignoreOnSync;
	}

	public Class<?> getImplClass() {
		return implClass;
	}

	public EntityFieldInfo findRefToParent(String parentEntity) {
		// TODO Use map
		for (EntityFieldInfo entityFieldInfo : refFieldList) {
			if (entityFieldInfo.getReferences().equals(parentEntity)) {
				return entityFieldInfo;
			}
		}

		return null;
	}

	public Map<String, EntityFieldInfo> getFieldsByName() {
		return fieldsByName;
	}

	public Collection<EntityFieldInfo> getAllFields() {
		return fieldsByName.values();
	}

	public List<EntityFieldInfo> getRefFieldList() {
		return refFieldList;
	}

	public List<EntityFieldInfo> getFieldList() {
		return fieldList;
	}

	public List<EntityFieldInfo> getListOnlyFieldList() {
		return listOnlyFieldList;
	}

	public List<EntityFieldInfo> getChildFieldList() {
		return childFieldList;
	}

	public List<EntityFieldInfo> getChildListFieldList() {
		return childListFieldList;
	}

	public String getLocalFieldName(String fieldName) {
		String local = fieldToLocal.get(fieldName);
		return local != null ? local : fieldName;
	}

	public String getFieldNameFromLocal(String fieldName) {
		String local = fieldFromLocal.get(fieldName);
		return local != null ? local : fieldName;
	}

	public boolean isField(String fieldName) {
		return fieldsByName.containsKey(fieldName);
	}

	public Set<String> getFieldNames() {
		return fieldsByName.keySet();
	}

	public EntityFieldInfo getEntityFieldInfo(String fieldName) throws Exception {
		String local = getLocalFieldName(fieldName);
		EntityFieldInfo entityFieldInfo = fieldsByName.get(local);
		if (entityFieldInfo == null) {
			throw new RuntimeException("Information for field [" + fieldName + "] not found.");
		}

		return entityFieldInfo;
	}

	public static Builder newBuilder() {
		return new Builder(null).dynamic(false);
	}

	public static Builder newBuilder(String entityManagerFactory) {
		return new Builder(entityManagerFactory);
	}

	public static class Builder {

		private String entityManagerFactory;

		private String dataSourceAlias;

		private ConnectEntityBaseType baseType;

		private String name;

		private String description;

		private String tableName;

		private String idFieldName;

		private String versionNoFieldName;

		private String handler;

		private String actionPolicy;

		private String implementation;

		private boolean dynamic;

		private boolean ignoreOnSync;

		private Map<String, EntityFieldInfo> fieldsByName;

		public Builder(String entityManagerFactory) {
			this.entityManagerFactory = entityManagerFactory;
			this.fieldsByName = new LinkedHashMap<String, EntityFieldInfo>();
			this.dynamic = true;
		}

		public Builder dataSourceAlias(String dataSourceAlias) {
			this.dataSourceAlias = dataSourceAlias;
			return this;
		}

		public Builder baseType(ConnectEntityBaseType baseType) {
			this.baseType = baseType;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder tableName(String tableName) {
			this.tableName = tableName;
			return this;
		}

		public Builder idFieldName(String idFieldName) {
			this.idFieldName = idFieldName;
			return this;
		}

		public Builder versionNoFieldName(String versionNoFieldName) {
			this.versionNoFieldName = versionNoFieldName;
			return this;
		}

		public Builder handler(String handler) {
			this.handler = handler;
			return this;
		}

		public Builder actionPolicy(String actionPolicy) {
			this.actionPolicy = actionPolicy;
			return this;
		}

		public Builder ignoreOnSync(boolean ignoreOnSync) {
			this.ignoreOnSync = ignoreOnSync;
			return this;
		}

		public Builder dynamic(boolean dynamic) {
			this.dynamic = dynamic;
			return this;
		}

		public Builder implementation(String implementation) {
			this.implementation = implementation;
			return this;
		}

		public boolean isPresent(String fieldName) {
			return fieldsByName.containsKey(fieldName);
		}
		
		public Builder addField(ConnectFieldDataType type, Class<?> fieldClass, String fieldName, String description,
				String column) throws Exception {
			return addField(type, fieldClass, fieldName, description, column, null, null, 0, 0, 0, true);
		}

		@SuppressWarnings("unchecked")
		public Builder addField(ConnectFieldDataType type, Class<?> fieldClass, String fieldName, String description,
				String column, String references, String enumImpl, int precision, int scale, int length,
				boolean nullable) throws Exception {
			if (type == null) {
				throw new RuntimeException("Entity information field type is required");
			}

			if (fieldsByName.containsKey(fieldName)) {
				throw new RuntimeException("Entity information for entity [" + name
						+ "] already contains information for field [" + fieldName + "]");
			}

			if (type.references() && (references == null || references.trim().isEmpty())) {
				throw new RuntimeException(
						"References property required for entity [" + name + "] field[" + fieldName + "]");
			}

			Class<? extends Enum<?>> enumImplClass = enumImpl != null
					? (Class<? extends Enum<?>>) Class.forName(enumImpl)
					: null;
			fieldsByName.put(fieldName, new EntityFieldInfo(type, fieldClass, fieldName, description, column,
					references, enumImplClass, precision, scale, length, nullable));
			return this;
		}

		public EntityInfo build() throws Exception {
			if (baseType == null) {
				throw new RuntimeException("Entity base type is required. Entity name [" + name + "]");
			}

			if (dataSourceAlias == null) {
				throw new RuntimeException("Entity datasource alias is required. Entity name [" + name + "]");
			}

			if (implementation == null) {
				throw new RuntimeException("Entity implementation class name is required. Entity name [" + name + "]");
			}

			Class<?> implClass = Class.forName(implementation);
			return new EntityInfo(entityManagerFactory, dataSourceAlias, baseType, name, description, tableName,
					idFieldName, versionNoFieldName, handler, actionPolicy, dynamic, ignoreOnSync, implClass, fieldsByName);
		}
	}

}
