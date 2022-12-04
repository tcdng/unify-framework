/*
 * Copyright 2018-2022 The Code Department.
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

package com.tcdng.unify.core.database.dynamic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyOperationException;
import com.tcdng.unify.core.annotation.DynamicEntityType;
import com.tcdng.unify.core.annotation.DynamicFieldType;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.system.entities.AbstractSequencedEntity;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Dynamic entity information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DynamicEntityInfo {

	public static final DynamicEntityInfo SELF_REFERENCE = new DynamicEntityInfo(true);

	public enum ManagedType {
		MANAGED, NOT_MANAGED
	}

	private DynamicEntityType type;

	private String tableName;

	private String baseClassName;

	private String className;

	private Map<String, DynamicFieldInfo> fieldInfos;

	private boolean withChildField;

	private boolean selfReference;

	private ManagedType managed;

	private boolean resolved;
	
	private long version;

	private DynamicEntityInfo(boolean selfReference) {
		this.selfReference = selfReference;
	}

	private DynamicEntityInfo(DynamicEntityType type, String tableName, String baseClassName, String className,
			ManagedType managed, long version) {
		this.type = type;
		this.tableName = tableName;
		this.baseClassName = baseClassName;
		this.className = className;
		this.managed = managed;
		this.version = version;
		this.fieldInfos = Collections.emptyMap();
	}

	public DynamicEntityType getType() {
		return type;
	}

	public boolean isWithChildField() {
		return withChildField;
	}

	public boolean isGeneration() {
		return type.isGeneration();
	}

	public boolean isManaged() {
		return ManagedType.MANAGED.equals(managed);
	}

	public String getTableName() {
		return tableName;
	}

	public String getBaseClassName() {
		return baseClassName;
	}

	public String getClassName() {
		return className;
	}

	public List<DynamicFieldInfo> getFieldInfos() {
		return new ArrayList<DynamicFieldInfo>(fieldInfos.values());
	}

	public DynamicFieldInfo getDynamicFieldInfo(String fieldName) throws UnifyException {
		DynamicFieldInfo dynamicFieldInfo = fieldInfos.get(fieldName);
		if (dynamicFieldInfo == null) {
			throw new UnifyOperationException(getClass(),
					"Class [" + className + "] field with name [" + fieldName + "] is unknown.");
		}

		return dynamicFieldInfo;
	}

	public boolean isSelfReference() {
		return selfReference;
	}

	public long getVersion() {
		return version;
	}

	public boolean isResolved() {
		return resolved;
	}
    
    @Override
	public String toString() {
		return StringUtils.toXmlString(this);
	}

	public void finalizeResolution() throws UnifyException {
		if (!resolved) {
			synchronized(this) {
				if (!resolved) {
					for (DynamicFieldInfo dynamicFieldInfo: fieldInfos.values()) {
						dynamicFieldInfo.finalizeResolution();
					}
					
					resolved = true;
				}
			}
		}
	}

	public static Builder newBuilder(DynamicEntityType type, String className, ManagedType managed) {
		return new Builder(type, className, managed);
	}

	public static Builder newBuilder(String className, ManagedType managed) {
		return new Builder(DynamicEntityType.INFO_ONLY, className, managed);
	}

	public static class Builder {

		private DynamicEntityInfo info;

		private DynamicEntityType type;

		private String tableName;

		private String baseClassName;

		private String className;

		private Map<String, DynamicForeignKeyFieldInfo> fkFields;

		private Map<String, DynamicColumnFieldInfo> columnFields;

		private Map<String, DynamicListOnlyFieldInfo> listOnlyFields;
		
		private Map<String, DynamicFieldInfo> childFieldInfos;
		
		private ManagedType managed;

		private long version;
		
		private boolean withChildField;

		private Builder(DynamicEntityType type, String className, ManagedType managed) {
			this.type = type;
			this.className = className;
			this.managed = managed;
			baseClassName = AbstractSequencedEntity.class.getCanonicalName();
			fkFields = new LinkedHashMap<String, DynamicForeignKeyFieldInfo>();
			columnFields = new LinkedHashMap<String, DynamicColumnFieldInfo>();
			listOnlyFields = new LinkedHashMap<String, DynamicListOnlyFieldInfo>();
			childFieldInfos =  new LinkedHashMap<String, DynamicFieldInfo>();
		}

		public Builder tableName(String tableName) {
			this.tableName = tableName;
			return this;
		}

		public Builder baseClassName(String baseClassName) {
			this.baseClassName = baseClassName;
			return this;
		}

		public Builder version(long version) {
			this.version = version;
			return this;
		}

		public Builder addForeignKeyField(DynamicFieldType type, DynamicEntityInfo parentDynamicEntityInfo,
				String columnName, String fieldName, String defaultVal, boolean nullable) throws UnifyException {
			checkFieldNameExist(fieldName);
			fkFields.put(fieldName, new DynamicForeignKeyFieldInfo(type, parentDynamicEntityInfo, columnName, fieldName,
					defaultVal, nullable));
			return this;
		}

		public Builder addForeignKeyField(DynamicFieldType type, String enumClassName, String columnName,
				String fieldName, String defaultVal, boolean nullable) throws UnifyException {
			checkFieldNameExist(fieldName);
			fkFields.put(fieldName,
					new DynamicForeignKeyFieldInfo(type, enumClassName, columnName, fieldName, defaultVal, nullable));
			return this;
		}

		public Builder addField(DynamicFieldType type, DataType dataType, String columnName, String fieldName,
				String defaultVal, int length, int precision, int scale, boolean nullable, boolean descriptive)
				throws UnifyException {
			return addField(type, dataType, columnName, fieldName, null, defaultVal, length, precision, scale, nullable,
					descriptive);
		}

		public Builder addField(DynamicFieldType type, DataType dataType, String columnName, String fieldName,
				String transformer, String defaultVal, int length, int precision, int scale, boolean nullable,
				boolean descriptive) throws UnifyException {
			checkFieldNameExist(fieldName);
			columnFields.put(fieldName, new DynamicColumnFieldInfo(type, dataType, columnName, fieldName, transformer,
					defaultVal, length, precision, scale, nullable, descriptive));
			return this;
		}

		public Builder addField(DynamicFieldType type, String enumClassName, String columnName, String fieldName,
				String defaultVal, boolean nullable, boolean descriptive) throws UnifyException {
			checkFieldNameExist(fieldName);
			columnFields.put(fieldName,
					new DynamicColumnFieldInfo(type, enumClassName, columnName, fieldName, nullable, descriptive));
			return this;
		}

		public Builder addListOnlyField(DynamicFieldType type, String columnName, String fieldName, String key,
				String property, boolean descriptive) throws UnifyException {
			checkFieldNameExist(fieldName);
			DynamicForeignKeyFieldInfo fkFieldInfo = fkFields.get(key);
			if (fkFieldInfo == null) {
				throw new UnifyOperationException(getClass(), "Class [" + className + "] unknown foreign key [" + key
						+ "] referenced by [" + fieldName + "].");
			}

			if (fkFieldInfo.isEnum()) {
				if (!"name".equals(property) && !"description".equals(property)) {
					throw new UnifyOperationException(getClass(), "Class [" + className + "] enumeration property ["
							+ property + "] referenced by [" + fieldName + "] is not supported.");
				}

				listOnlyFields.put(fieldName,
						new DynamicListOnlyFieldInfo(type, columnName, fieldName, key, property, false));
			} else {
				DynamicFieldInfo _dynamicFieldInfo = null;
				boolean resolved = false;
				if (fkFieldInfo.getParentDynamicEntityInfo().isSelfReference()) {
					_dynamicFieldInfo = columnFields.get(property);
					if (_dynamicFieldInfo == null) {
						_dynamicFieldInfo = fkFields.get(property);
					}
					
					resolved = true;
				} else {
					_dynamicFieldInfo = fkFieldInfo;
				}

				listOnlyFields.put(fieldName, new DynamicListOnlyFieldInfo(type, _dynamicFieldInfo, columnName,
						fieldName, key, property, descriptive, resolved));
			}
			
			return this;
		}
		
		public Builder addChildField(DynamicFieldType type, DynamicEntityInfo childDynamicEntityInfo,
				String fieldName) throws UnifyException {
			checkFieldNameExist(fieldName);
			childFieldInfos.put(fieldName, new DynamicChildFieldInfo(type, childDynamicEntityInfo, fieldName));
			withChildField = true;
			return this;
		}

		public Builder addChildListField(DynamicFieldType type, DynamicEntityInfo childDynamicEntityInfo,
				String fieldName) throws UnifyException {
			checkFieldNameExist(fieldName);
			childFieldInfos.put(fieldName, new DynamicChildListFieldInfo(type, childDynamicEntityInfo, fieldName));
			withChildField = true;
			return this;
		}

		private void checkFieldNameExist(String fieldName) throws UnifyException {
			if (fkFields.containsKey(fieldName) || columnFields.containsKey(fieldName)
					|| listOnlyFields.containsKey(fieldName) || childFieldInfos.containsKey(fieldName)) {
				throw new UnifyOperationException(getClass(), "Field with name [" + fieldName + "] already exists.");
			}
		}

		public DynamicEntityInfo prefetch() {
			if (info == null) {
				synchronized (this) {
					if (info == null) {
						info = new DynamicEntityInfo(type, tableName, baseClassName, className, managed, version);
					}
				}
			}

			return info;
		}

		public DynamicEntityInfo build() {
			Map<String, DynamicFieldInfo> fieldInfos = new LinkedHashMap<String, DynamicFieldInfo>();
			fieldInfos.putAll(fkFields);
			fieldInfos.putAll(columnFields);
			fieldInfos.putAll(listOnlyFields);
			fieldInfos.putAll(childFieldInfos);
			DynamicEntityInfo _info = prefetch();
			_info.fieldInfos = fieldInfos;
			_info.withChildField = withChildField;
			for (DynamicForeignKeyFieldInfo fkField : fkFields.values()) {
				fkField.updateParentDynamicEntityInfo(_info);
			}

			return _info;
		}
	}

}
