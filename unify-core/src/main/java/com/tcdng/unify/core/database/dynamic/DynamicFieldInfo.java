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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.annotation.DynamicFieldType;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.constant.EntityFieldType;
import com.tcdng.unify.core.database.sql.SqlEntitySchemaInfo;
import com.tcdng.unify.core.database.sql.SqlFieldInfo;
import com.tcdng.unify.core.database.sql.SqlFieldSchemaInfo;
import com.tcdng.unify.core.util.DynamicEntityUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Dynamic field information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class DynamicFieldInfo implements SqlFieldSchemaInfo {

	private DynamicFieldType type;

	private EntityFieldType fieldType;

	private DataType dataType;

	private String columnName;

	private String fieldName;

	private String mapped;

	private String enumClassName;

	private boolean descriptive;

	private boolean tenantId;

	private SqlFieldInfo sqlFieldInfo;
	
	public DynamicFieldInfo(DynamicFieldType type, EntityFieldType fieldType, DataType dataType, String columnName,
			String fieldName, String mapped, boolean descriptive, boolean tenantId) {
		this(type, fieldType, dataType, columnName, fieldName, mapped, null, descriptive, tenantId);
	}

	public DynamicFieldInfo(DynamicFieldType type, EntityFieldType fieldType, DataType dataType, String columnName,
			String fieldName, String mapped, String enumClassName, boolean descriptive, boolean tenantId) {
		this.type = type;
		this.fieldType = fieldType;
		this.dataType = dataType;
		this.columnName = columnName;
		this.fieldName = fieldName;
		this.mapped = mapped;
		this.enumClassName = enumClassName;
		this.descriptive = descriptive;
		this.tenantId = tenantId;
	}

	public DynamicFieldType getType() {
		return type;
	}

	public EntityFieldType getFieldType() {
		return fieldType;
	}

	public DataType getDataType() {
		return dataType;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getEnumClassName() {
		return enumClassName;
	}

	public boolean isEnum() {
		return enumClassName != null;
	}

	public boolean isDescriptive() {
		return descriptive;
	}

	public boolean isTenantId() {
		return tenantId;
	}

	public boolean isGeneration() {
		return DynamicFieldType.GENERATION.equals(type);
	}

	public boolean isWithMapping() {
		return mapped != null;
	}

	public String getMapping() {
		return mapped;
	}

	public final void finalizeResolution() throws UnifyException {
		Resolution resolution = doFinalizeResolution();
		dataType = resolution.dataType;
		enumClassName = resolution.enumClassName;
	}

	@Override
	public String getName() {
		return getSqlFieldnfo().getName();
	}

	@Override
	public Long getMarker() {
		return getSqlFieldnfo().getMarker();
	}

	@Override
	public String getPreferredColumnName() {
		return getSqlFieldnfo().getPreferredColumnName();
	}

	@Override
	public String getConstraint() {
		return getSqlFieldnfo().getConstraint();
	}

	@Override
	public boolean isNullable() {
		return getSqlFieldnfo().isNullable();
	}

	@Override
	public boolean isFosterParentType() {
		return getSqlFieldnfo().isFosterParentType();
	}

	@Override
	public boolean isFosterParentId() {
		return getSqlFieldnfo().isFosterParentId();
	}

	@Override
	public boolean isCategoryColumn() {
		return getSqlFieldnfo().isCategoryColumn();
	}

	@Override
	public int getLength() {
		return getSqlFieldnfo().getLength();
	}

	@Override
	public int getPrecision() {
		return getSqlFieldnfo().getPrecision();
	}

	@Override
	public int getScale() {
		return getSqlFieldnfo().getScale();
	}

	@Override
	public ColumnType getColumnType() {
		return getSqlFieldnfo().getColumnType();
	}

	@Override
	public Class<?> getFieldClass() {
		return getSqlFieldnfo().getFieldClass();
	}

	@Override
	public String getDefaultVal() {
		return getSqlFieldnfo().getDefaultVal();
	}

	@Override
	public boolean isWithDefaultVal() {
		return getSqlFieldnfo().isWithDefaultVal();
	}

	@Override
	public boolean isPrimaryKey() {
		return getSqlFieldnfo().isPrimaryKey();
	}

	@Override
	public boolean isListOnly() {
		return getSqlFieldnfo().isListOnly();
	}

	@Override
	public boolean isSameSchema(SqlFieldSchemaInfo sqlFieldSchemaInfo) {
		return getSqlFieldnfo().isSameSchema(sqlFieldSchemaInfo);
	}

	@Override
	public boolean isIgnoreFkConstraint() {
		return getSqlFieldnfo().isIgnoreFkConstraint();
	}

	@Override
	public SqlEntitySchemaInfo getForeignEntityInfo() {
		return getSqlFieldnfo().getForeignEntityInfo();
	}

	@Override
	public SqlFieldSchemaInfo getForeignFieldInfo() {
		return getSqlFieldnfo().getForeignFieldInfo();
	}

	@Override
	public String getForeignEntityPreferredAlias() {
		return getSqlFieldnfo().getForeignEntityPreferredAlias();
	}

	@Override
	public SqlFieldSchemaInfo getForeignKeyFieldInfo() {
		return getSqlFieldnfo().getForeignKeyFieldInfo();
	}

	@Override
	public String toString() {
		return StringUtils.toXmlString(this);
	}

	private SqlFieldInfo getSqlFieldnfo() {
		if (sqlFieldInfo == null) {
			synchronized(this) {
				if (sqlFieldInfo == null) {
					sqlFieldInfo = DynamicEntityUtils.createSqlFieldInfo(this);
				}
			}
		}
		
		return sqlFieldInfo;
	}

	protected Resolution doFinalizeResolution() throws UnifyException {
		return new Resolution(dataType, enumClassName);
	}

	protected class Resolution {

		private final DataType dataType;

		private final String enumClassName;

		public Resolution(DataType dataType, String enumClassName) {
			this.dataType = dataType;
			this.enumClassName = enumClassName;
		}

		public DataType getDataType() {
			return dataType;
		}

		public String getEnumClassName() {
			return enumClassName;
		}
	}
}
