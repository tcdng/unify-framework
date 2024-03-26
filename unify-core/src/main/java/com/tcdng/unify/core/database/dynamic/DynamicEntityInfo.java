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

package com.tcdng.unify.core.database.dynamic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.common.annotation.StaticList;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyOperationException;
import com.tcdng.unify.core.annotation.DynamicEntityType;
import com.tcdng.unify.core.annotation.DynamicFieldType;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.database.MappedEntityRepository;
import com.tcdng.unify.core.database.sql.SqlEntityInfo;
import com.tcdng.unify.core.database.sql.SqlEntitySchemaInfo;
import com.tcdng.unify.core.database.sql.SqlFieldInfo;
import com.tcdng.unify.core.database.sql.SqlFieldSchemaInfo;
import com.tcdng.unify.core.database.sql.SqlForeignKeyInfo;
import com.tcdng.unify.core.database.sql.SqlForeignKeySchemaInfo;
import com.tcdng.unify.core.database.sql.SqlIndexSchemaInfo;
import com.tcdng.unify.core.database.sql.SqlUniqueConstraintSchemaInfo;
import com.tcdng.unify.core.database.sql.SqlViewRestrictionInfo;
import com.tcdng.unify.core.system.entities.AbstractSequencedEntity;
import com.tcdng.unify.core.util.DynamicEntityUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Dynamic entity information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DynamicEntityInfo implements SqlEntitySchemaInfo {

	public static final DynamicEntityInfo SELF_REFERENCE = new DynamicEntityInfo(true);

	public enum ManagedType {
		MANAGED, NOT_MANAGED
	}

	private DynamicEntityType type;

	private String alias;

	private String tableName;

	private String baseClassName;

	private String className;

	private Map<String, DynamicFieldInfo> fieldInfos;

	private Map<String, String> listTypeArgByFieldName;

	private boolean withChildField;

	private boolean withTenantIdField;

	private boolean selfReference;

	private ManagedType managed;

	private boolean resolved;

	private boolean skipPasswordFields;

	private long version;

	private SqlEntityInfo sqlEntityInfo;
	
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

	public boolean isSkipPasswordFields() {
		return skipPasswordFields;
	}

	public void setSkipPasswordFields(boolean skipPasswordFields) {
		this.skipPasswordFields = skipPasswordFields;
	}

	@Override
	public String getSchema() {
		return getSqlEntityInfo().getSchema();
	}

	@Override
	public String getPreferredTableName() {
		return getSqlEntityInfo().getPreferredTableName();
	}

	@Override
	public String getSchemaTableName() {
		return getSqlEntityInfo().getSchemaTableName();
	}

	@Override
	public String getTableAlias() {
		return getSqlEntityInfo().getTableAlias();
	}

	@Override
	public String getViewName() {
		return getSqlEntityInfo().getViewName();
	}

	@Override
	public String getPreferredViewName() {
		return getSqlEntityInfo().getPreferredViewName();
	}

	@Override
	public String getSchemaViewName() {
		return getSqlEntityInfo().getSchemaViewName();
	}

	@Override
	public Long getIndex() {
		return getSqlEntityInfo().getIndex();
	}

	@Override
	public SqlFieldSchemaInfo getIdFieldInfo() {
		return getSqlEntityInfo().getIdFieldInfo();
	}

	@Override
	public SqlFieldSchemaInfo getVersionFieldInfo() {
		return getSqlEntityInfo().getVersionFieldInfo();
	}

	@Override
	public SqlFieldSchemaInfo getTenantIdFieldInfo() {
		return getSqlEntityInfo().getTenantIdFieldInfo();
	}

	@Override
	public SqlFieldInfo getFosterParentTypeFieldInfo() {
		return getSqlEntityInfo().getFosterParentTypeFieldInfo();
	}

	@Override
	public SqlFieldInfo getFosterParentIdFieldInfo() {
		return getSqlEntityInfo().getFosterParentIdFieldInfo();
	}

	@Override
	public SqlFieldInfo getCategoryFieldInfo() {
		return getSqlEntityInfo().getCategoryFieldInfo();
	}

	@Override
	public List<? extends SqlFieldSchemaInfo> getManagedFieldInfos() {
		return getSqlEntityInfo().getManagedFieldInfos();
	}

	@Override
	public List<? extends SqlFieldSchemaInfo> getListFieldInfos() {
		return getSqlEntityInfo().getListFieldInfos();
	}

	@Override
	public List<? extends SqlFieldSchemaInfo> getManagedListFieldInfos() {
		return getSqlEntityInfo().getManagedListFieldInfos();
	}

	@Override
	public List<SqlForeignKeyInfo> getManagedForeignKeyList() {
		return getSqlEntityInfo().getManagedForeignKeyList();
	}

	@Override
	public Set<String> getFieldNames() {
		return getSqlEntityInfo().getFieldNames();
	}

	@Override
	public SqlFieldSchemaInfo getFieldInfo(String name) throws UnifyException {
		return getSqlEntityInfo().getFieldInfo(name);
	}

	@Override
	public SqlFieldInfo getManagedFieldInfo(String name) throws UnifyException {
		return getSqlEntityInfo().getManagedFieldInfo(name);
	}

	@Override
	public SqlFieldSchemaInfo getFieldInfo(Long marker) throws UnifyException {
		return getSqlEntityInfo().getFieldInfo(marker);
	}

	@Override
	public Map<String, Class<?>> getViewBaseTables() {
		return getSqlEntityInfo().getViewBaseTables();
	}

	@Override
	public List<SqlViewRestrictionInfo> getViewRestrictionList() {
		return getSqlEntityInfo().getViewRestrictionList();
	}

	@Override
	public List<? extends SqlForeignKeySchemaInfo> getForeignKeyList() {
		return getSqlEntityInfo().getForeignKeyList();
	}

	@Override
	public Map<String, ? extends SqlUniqueConstraintSchemaInfo> getUniqueConstraintList() {
		return getSqlEntityInfo().getUniqueConstraintList();
	}

	@Override
	public Map<String, ? extends SqlIndexSchemaInfo> getIndexList() {
		return getSqlEntityInfo().getIndexList();
	}

	@Override
	public List<Map<String, Object>> getStaticValueList() {
		return getSqlEntityInfo().getStaticValueList();
	}

	@Override
	public StaticList getStaticList() {
		return getSqlEntityInfo().getStaticList();
	}

	@Override
	public boolean isSchemaAlreadyManaged() {
		return getSqlEntityInfo().isSchemaAlreadyManaged();
	}

	@Override
	public void setSchemaAlreadyManaged() {
		getSqlEntityInfo().setSchemaAlreadyManaged();
	}

	@Override
	public boolean isWithTenantId() {
		return getSqlEntityInfo().isWithTenantId();
	}

	@Override
	public boolean isIdentityManaged() {
		return getSqlEntityInfo().isIdentityManaged();
	}

	@Override
	public boolean isMapped() {
		return getSqlEntityInfo().isMapped();
	}

	@Override
	public MappedEntityRepository getMappedEntityRepository() {
		return getSqlEntityInfo().getMappedEntityRepository();
	}

	@Override
	public boolean isManagedForeignKeys() {
		return getSqlEntityInfo().isManagedForeignKeys();
	}

	@Override
	public boolean isVersioned() {
		return getSqlEntityInfo().isVersioned();
	}

	@Override
	public boolean isViewable() {
		return getSqlEntityInfo().isViewable();
	}

	@Override
	public boolean isViewOnly() {
		return getSqlEntityInfo().isViewOnly();
	}

	@Override
	public boolean isViewRestriction() {
		return getSqlEntityInfo().isViewRestriction();
	}

	@Override
	public boolean isForeignKeys() {
		return getSqlEntityInfo().isForeignKeys();
	}

	@Override
	public boolean isUniqueConstraints() {
		return getSqlEntityInfo().isUniqueConstraints();
	}

	@Override
	public boolean isIndexes() {
		return getSqlEntityInfo().isIndexes();
	}

	private SqlEntityInfo getSqlEntityInfo() {
		if (sqlEntityInfo == null) {
			synchronized(this) {
				if (sqlEntityInfo == null) {
					sqlEntityInfo = DynamicEntityUtils.createSqlEntityInfo(this);
				}
			}
		}
		
		return sqlEntityInfo;
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

		private boolean withTenantIdField;

		private Builder(DynamicEntityType type, String className, ManagedType managed) {
			this.type = type;
			this.className = className;
			this.managed = managed;
			baseClassName = AbstractSequencedEntity.class.getCanonicalName();
			fkFields = new LinkedHashMap<String, DynamicForeignKeyFieldInfo>();
			columnFields = new LinkedHashMap<String, DynamicColumnFieldInfo>();
			listOnlyFields = new LinkedHashMap<String, DynamicListOnlyFieldInfo>();
			childFieldInfos = new LinkedHashMap<String, DynamicFieldInfo>();
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

		public Builder addTenantIdField(DynamicFieldType type, String columnName, String fieldName, String mapped,
				int precision, int scale) throws UnifyException {
			checkFieldNameExist(fieldName);
			if (withTenantIdField) {
				throw new UnifyOperationException(getClass(), "Tenant ID field already exists");
			}

			withTenantIdField = true;
			columnFields.put(fieldName, new DynamicColumnFieldInfo(type, DataType.LONG, columnName, fieldName, mapped,
					null, "0", 0, precision, scale, false, false, true));
			return this;
		}

		public Builder addField(DynamicFieldType type, DataType dataType, String columnName, String fieldName,
				String mapped, String defaultVal, int length, int precision, int scale, boolean nullable,
				boolean descriptive) throws UnifyException {
			return addField(type, dataType, columnName, fieldName, mapped, null, defaultVal, length, precision, scale,
					nullable, descriptive);
		}

		public Builder addField(DynamicFieldType type, DataType dataType, String columnName, String fieldName,
				String mapped, String transformer, String defaultVal, int length, int precision, int scale,
				boolean nullable, boolean descriptive) throws UnifyException {
			checkFieldNameExist(fieldName);
			columnFields.put(fieldName, new DynamicColumnFieldInfo(type, dataType, columnName, fieldName, mapped,
					transformer, defaultVal, length, precision, scale, nullable, descriptive, false));
			return this;
		}

		public Builder addField(DynamicFieldType type, String enumClassName, String columnName, String fieldName,
				String mapped, String defaultVal, boolean nullable, boolean descriptive) throws UnifyException {
			checkFieldNameExist(fieldName);
			columnFields.put(fieldName, new DynamicColumnFieldInfo(type, enumClassName, columnName, fieldName, mapped,
					nullable, descriptive, false));
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
			_info.withTenantIdField = withTenantIdField;
			for (DynamicForeignKeyFieldInfo fkField : fkFields.values()) {
				fkField.updateParentDynamicEntityInfo(_info);
			}

			return _info;
		}
	}

}
