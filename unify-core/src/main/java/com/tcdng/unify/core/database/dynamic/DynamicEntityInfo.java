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

package com.tcdng.unify.core.database.dynamic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyOperationException;
import com.tcdng.unify.core.annotation.DynamicEntityType;
import com.tcdng.unify.core.annotation.DynamicFieldType;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.system.entities.AbstractSequencedEntity;
import com.tcdng.unify.core.util.DataUtils;
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
		MANAGED, NOT_MANAGED;

		public boolean managed() {
			return MANAGED.equals(this);
		}

		public boolean notManaged() {
			return NOT_MANAGED.equals(this);
		}
	}

	private DynamicEntityType type;

	private String alias;

	private String tableName;

	private String baseClassName;

	private String className;

	private Map<String, DynamicFieldInfo> fieldInfos;

	private Map<String, String> listTypeArgByFieldName;

	private List<DynamicIndexInfo> indexes;

	private List<DynamicUniqueConstraintInfo> uniqueConstraints;

	private boolean withChildField;

	private boolean withTenantIdField;

	private boolean selfReference;

	private ManagedType managed;

	private boolean resolved;

	private boolean skipPasswordFields;

	private boolean schemaChanged;

	private long version;

	private DynamicEntityInfo(boolean selfReference) {
		this.selfReference = selfReference;
	}

	private DynamicEntityInfo(DynamicEntityType type, String tableName, String baseClassName, String className,
			ManagedType managed, boolean schemaChanged, long version) {
		this.type = type;
		this.tableName = tableName;
		this.baseClassName = baseClassName;
		this.className = className;
		this.managed = managed;
		this.schemaChanged = schemaChanged;
		this.version = version;
		this.fieldInfos = Collections.emptyMap();
		this.indexes = Collections.emptyList();
		this.uniqueConstraints = Collections.emptyList();
	}

	public DynamicEntityType getType() {
		return type;
	}

	public boolean isWithChildField() {
		return withChildField;
	}

	public boolean isWithTenantIdField() {
		return withTenantIdField;
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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public List<DynamicIndexInfo> getIndexes() {
		return indexes;
	}

	public boolean isWithIndexes() {
		return !indexes.isEmpty();
	}

	public List<DynamicUniqueConstraintInfo> getUniqueConstraints() {
		return uniqueConstraints;
	}

	public boolean isWithUniqueConstraints() {
		return !uniqueConstraints.isEmpty();
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

	public Map<String, String> getListTypeArgByFieldName() {
		if (listTypeArgByFieldName == null) {
			synchronized (this) {
				if (listTypeArgByFieldName == null) {
					listTypeArgByFieldName = new HashMap<String, String>();
					for (DynamicFieldInfo dynamicFieldInfo : fieldInfos.values()) {
						if (dynamicFieldInfo.getFieldType().isChildList()) {
							listTypeArgByFieldName.put(dynamicFieldInfo.getFieldName(),
									((DynamicChildListFieldInfo) dynamicFieldInfo).getChildDynamicEntityInfo()
											.getClassName());
						}
					}

					listTypeArgByFieldName = Collections.unmodifiableMap(listTypeArgByFieldName);
				}
			}
		}

		return listTypeArgByFieldName;
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

	public boolean isSchemaChanged() {
		return schemaChanged;
	}

	public boolean isSkipPasswordFields() {
		return skipPasswordFields;
	}

	public void setSkipPasswordFields(boolean skipPasswordFields) {
		this.skipPasswordFields = skipPasswordFields;
	}

	@Override
	public String toString() {
		return StringUtils.toXmlString(this);
	}

	public void finalizeResolution() throws UnifyException {
		if (!resolved) {
			synchronized (this) {
				if (!resolved) {
					for (DynamicFieldInfo dynamicFieldInfo : fieldInfos.values()) {
						dynamicFieldInfo.finalizeResolution();
					}

					resolved = true;
				}
			}
		}
	}

	public static Builder newBuilder(DynamicEntityType type, String className, ManagedType managed,
			boolean schemaChanged) {
		return new Builder(type, className, managed, schemaChanged);
	}

	public static Builder newBuilder(String className, ManagedType managed, boolean schemaChanged) {
		return new Builder(DynamicEntityType.INFO_ONLY, className, managed, schemaChanged);
	}

	public static class Builder {

		private DynamicEntityInfo info;

		private DynamicEntityType type;

		private String tableName;

		private String baseClassName;

		private String className;

		private List<DynamicIndexInfo> indexes;

		private List<DynamicUniqueConstraintInfo> uniqueConstraints;

		private Map<String, DynamicForeignKeyFieldInfo> fkFields;

		private Map<String, DynamicColumnFieldInfo> columnFields;

		private Map<String, DynamicListOnlyFieldInfo> listOnlyFields;

		private Map<String, DynamicFieldInfo> childFieldInfos;

		private ManagedType managed;

		private boolean schemaChanged;

		private long version;

		private boolean withChildField;

		private boolean withTenantIdField;

		private Builder(DynamicEntityType type, String className, ManagedType managed, boolean schemaChanged) {
			this.type = type;
			this.className = className;
			this.managed = managed;
			this.schemaChanged = schemaChanged;
			this.baseClassName = AbstractSequencedEntity.class.getCanonicalName();
			this.fkFields = new LinkedHashMap<String, DynamicForeignKeyFieldInfo>();
			this.columnFields = new LinkedHashMap<String, DynamicColumnFieldInfo>();
			this.listOnlyFields = new LinkedHashMap<String, DynamicListOnlyFieldInfo>();
			this.childFieldInfos = new LinkedHashMap<String, DynamicFieldInfo>();
			this.indexes = new ArrayList<DynamicIndexInfo>();
			this.uniqueConstraints = new ArrayList<DynamicUniqueConstraintInfo>();
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

		public Builder addIndex(List<String> fields) throws UnifyException {
			addIndex(fields, false);
			return this;
		}

		public Builder addIndex(List<String> fields, boolean unique) throws UnifyException {
			List<String> _fields = new ArrayList<String>();
			for (String field : fields) {
				if (isConstraintFieldExist(field)) {
					_fields.add(field);
				}
			}

			if (!DataUtils.isBlank(_fields)) {
				indexes.add(new DynamicIndexInfo(_fields, unique));
			}

			return this;
		}

		public Builder addUniqueConstraint(List<String> fields) throws UnifyException {
			List<String> _fields = new ArrayList<String>();
			for (String field : fields) {
				if (isConstraintFieldExist(field)) {
					_fields.add(field);
				}
			}

			if (!DataUtils.isBlank(_fields)) {
				uniqueConstraints.add(new DynamicUniqueConstraintInfo(_fields));
			}

			return this;
		}

		public Builder addForeignKeyField(DynamicFieldType type, DynamicEntityInfo parentDynamicEntityInfo,
				String columnName, String fieldName, String defaultVal, boolean unlinked, boolean nullable)
				throws UnifyException {
			checkFieldNameExist(fieldName);
			fkFields.put(fieldName, new DynamicForeignKeyFieldInfo(type, parentDynamicEntityInfo, columnName, fieldName,
					defaultVal, unlinked, nullable));
			return this;
		}

		public Builder addForeignKeyField(DynamicFieldType type, String enumClassName, String columnName,
				String fieldName, String defaultVal, boolean unlinked, boolean nullable) throws UnifyException {
			checkFieldNameExist(fieldName);
			fkFields.put(fieldName, new DynamicForeignKeyFieldInfo(type, enumClassName, columnName, fieldName,
					defaultVal, unlinked, nullable));
			return this;
		}

		public Builder addTenantIdField(DynamicFieldType type, String columnName, String fieldName, String mapped,
				int precision, int scale) throws UnifyException {
			checkFieldNameExist(fieldName);
			if (withTenantIdField) {
				throw new UnifyOperationException(getClass(), "Tenant ID field already exists");
			}

			withTenantIdField = true;
			columnFields.put(fieldName, new DynamicColumnFieldInfo(type, DataType.LONG, columnName, fieldName, mapped,
					null, "0", 0, precision, scale, false, false, false, true));
			return this;
		}

		public Builder addField(DynamicFieldType type, DataType dataType, String columnName, String fieldName,
				String mapped, String defaultVal, int length, int precision, int scale, boolean nullable,
				boolean descriptive, boolean array) throws UnifyException {
			return addField(type, dataType, columnName, fieldName, mapped, null, defaultVal, length, precision, scale,
					nullable, descriptive, array);
		}

		public Builder addField(DynamicFieldType type, DataType dataType, String columnName, String fieldName,
				String mapped, String transformer, String defaultVal, int length, int precision, int scale,
				boolean nullable, boolean descriptive, boolean array) throws UnifyException {
			checkFieldNameExist(fieldName);
			columnFields.put(fieldName, new DynamicColumnFieldInfo(type, dataType, columnName, fieldName, mapped,
					transformer, defaultVal, length, precision, scale, nullable, descriptive, array, false));
			return this;
		}

		public Builder addField(DynamicFieldType type, String enumClassName, String columnName, String fieldName,
				String mapped, String defaultVal, boolean nullable, boolean descriptive, boolean array)
				throws UnifyException {
			checkFieldNameExist(fieldName);
			columnFields.put(fieldName, new DynamicColumnFieldInfo(type, enumClassName, columnName, fieldName, mapped,
					nullable, descriptive, array, false));
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

		public Builder addChildField(DynamicFieldType type, DynamicEntityInfo childDynamicEntityInfo, String fieldName,
				boolean editable) throws UnifyException {
			checkFieldNameExist(fieldName);
			childFieldInfos.put(fieldName,
					new DynamicChildFieldInfo(type, childDynamicEntityInfo, fieldName, editable));
			withChildField = true;
			return this;
		}

		public Builder addChildListField(DynamicFieldType type, DynamicEntityInfo childDynamicEntityInfo,
				String fieldName, boolean editable) throws UnifyException {
			checkFieldNameExist(fieldName);
			childFieldInfos.put(fieldName,
					new DynamicChildListFieldInfo(type, childDynamicEntityInfo, fieldName, editable));
			withChildField = true;
			return this;
		}

		private void checkFieldNameExist(String fieldName) throws UnifyException {
			if (fkFields.containsKey(fieldName) || columnFields.containsKey(fieldName)
					|| listOnlyFields.containsKey(fieldName) || childFieldInfos.containsKey(fieldName)) {
				throw new UnifyOperationException(getClass(), "Field with name [" + fieldName + "] already exists.");
			}
		}

		private boolean isConstraintFieldExist(String fieldName) throws UnifyException {
			return fkFields.containsKey(fieldName) || columnFields.containsKey(fieldName);
		}

		public DynamicEntityInfo prefetch() {
			if (info == null) {
				synchronized (this) {
					if (info == null) {
						info = new DynamicEntityInfo(type, tableName, baseClassName, className, managed, schemaChanged,
								version);
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
			_info.indexes = indexes;
			_info.uniqueConstraints = uniqueConstraints;
			_info.withChildField = withChildField;
			_info.withTenantIdField = withTenantIdField;
			for (DynamicForeignKeyFieldInfo fkField : fkFields.values()) {
				fkField.updateParentDynamicEntityInfo(_info);
			}

			return _info;
		}
	}

}
