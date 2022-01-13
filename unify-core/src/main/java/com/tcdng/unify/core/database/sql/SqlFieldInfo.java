/*
 * Copyright 2018-2020 The Code Department.
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
package com.tcdng.unify.core.database.sql;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.transform.Transformer;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Holds SQL information, relational information, reflection information and
 * other information for a record field.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class SqlFieldInfo implements SqlFieldSchemaInfo {

    private Long marker;

    private ColumnType columnType;

    private SqlEntityInfo foreignEntityInfo;

    private SqlFieldInfo foreignFieldInfo;

    private SqlFieldInfo foreignKeyFieldInfo;

    private String name;

    private String columnName;

    private String preferredColumnName;

    private String constraintName;

    private String defaultVal;

    private String foreignEntityPreferredAlias;

    private Transformer<?, ?> transformer;

    private boolean primaryKey;

    private boolean foreignKey;

    private boolean ignoreFkConstraint;

    private boolean listOnly;

    private boolean nullable;
    
    private boolean fosterParentType;
    
    private boolean fosterParentId;
    
    private boolean categoryColumn;

    private SqlFieldDimensions sqlFieldDimensions;

    private Field field;

    private Method getter;

    private Method setter;

    private int orderIndex;

    public SqlFieldInfo(int orderIndex, ColumnType columnType, SqlEntityInfo foreignSqlEntityInfo,
            SqlFieldInfo foreignSqlFieldInfo, SqlFieldInfo foreignKeySqlFieldInfo, String name, String columnName,
            String preferredColumnName, String constraintName, String foreignEntityPreferredAlias, boolean primaryKey,
            boolean foreignKey, boolean listOnly, boolean ignoreFkConstraint, Transformer<?, ?> transformer,
            SqlFieldDimensions sqlFieldDimensions, boolean nullable, boolean fosterParentType, boolean fosterParentId,
            boolean categoryColumn, String defaultVal, Field field, Method getter, Method setter,
            boolean isAllObjectsInLowerCase) {
        this(null, orderIndex, columnType, foreignSqlEntityInfo, foreignSqlFieldInfo, foreignKeySqlFieldInfo, name,
                columnName, preferredColumnName, constraintName, foreignEntityPreferredAlias, primaryKey, foreignKey,
                listOnly, ignoreFkConstraint, transformer, sqlFieldDimensions, nullable, fosterParentType,
                fosterParentId, categoryColumn, defaultVal, field, getter, setter, isAllObjectsInLowerCase);
    }

    public SqlFieldInfo(Long marker, int orderIndex, ColumnType columnType, SqlEntityInfo foreignSqlEntityInfo,
            SqlFieldInfo foreignSqlFieldInfo, SqlFieldInfo foreignKeySqlFieldInfo, String name, String columnName,
            String preferredColumnName, String constraintName, String foreignEntityPreferredAlias, boolean primaryKey,
            boolean foreignKey, boolean listOnly, boolean ignoreFkConstraint, Transformer<?, ?> transformer,
            SqlFieldDimensions sqlFieldDimensions, boolean nullable, boolean fosterParentType, boolean fosterParentId,
            boolean categoryColumn, String defaultVal, Field field, Method getter, Method setter, boolean isAllObjectsInLowerCase) {
        this.marker = marker;
        this.columnType = columnType;
        this.foreignEntityInfo = foreignSqlEntityInfo;
        this.foreignFieldInfo = foreignSqlFieldInfo;
        this.foreignKeyFieldInfo = foreignKeySqlFieldInfo;
        this.name = name;
        this.columnName = columnName;
        this.preferredColumnName = preferredColumnName;
        this.constraintName = constraintName;
        this.primaryKey = primaryKey;
        this.foreignKey = foreignKey;
        this.foreignEntityPreferredAlias = foreignEntityPreferredAlias;
        this.listOnly = listOnly;
        this.ignoreFkConstraint = ignoreFkConstraint;
        this.transformer = transformer;
        this.nullable = nullable;
        this.fosterParentType = fosterParentType;
        this.fosterParentId = fosterParentId;
        this.categoryColumn = categoryColumn;
        this.sqlFieldDimensions = sqlFieldDimensions;
        this.defaultVal = defaultVal;
        this.field = field;
        this.getter = getter;
        this.setter = setter;
        this.orderIndex = orderIndex;
        
        if (isAllObjectsInLowerCase) {
            this.columnName = StringUtils.toLowerCase(columnName);
            this.preferredColumnName = StringUtils.toLowerCase(preferredColumnName);
            this.constraintName = StringUtils.toLowerCase(constraintName);
        }
    }

    @Override
    public Long getMarker() {
        return marker;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    public String getPreferredColumnName() {
        return preferredColumnName;
    }

    @Override
    public String getConstraint() {
        return constraintName;
    }

    @Override
    public String getDefaultVal() {
        return defaultVal;
    }

    @Override
    public String getForeignEntityPreferredAlias() {
        return foreignEntityPreferredAlias;
    }

    @Override
    public boolean isListOnly() {
        return listOnly;
    }

    public Transformer<?, ?> getTransformer() {
        return transformer;
    }

    public boolean isTransformed() {
        return transformer != null;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public boolean isFosterParentType() {
        return fosterParentType;
    }

    @Override
    public boolean isFosterParentId() {
        return fosterParentId;
    }

    @Override
    public boolean isCategoryColumn() {
        return categoryColumn;
    }

    public SqlFieldDimensions getSqlFieldDimensions() {
        return sqlFieldDimensions;
    }

    @Override
    public int getLength() {
        return sqlFieldDimensions.getLength();
    }

    @Override
    public int getPrecision() {
        return sqlFieldDimensions.getPrecision();
    }

    @Override
    public int getScale() {
        return sqlFieldDimensions.getScale();
    }

    @Override
    public ColumnType getColumnType() {
        return columnType;
    }

    @Override
    public boolean isWithDefaultVal() {
        return defaultVal != null;
    }

    @Override
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public boolean isForeignKey() {
        return foreignKey;
    }

    @Override
    public boolean isIgnoreFkConstraint() {
        return ignoreFkConstraint;
    }

    public Field getField() {
        return field;
    }

    public Method getGetter() {
        return getter;
    }

    public Method getSetter() {
        return setter;
    }

    @Override
    public Class<?> getFieldType() {
        return field.getType();
    }

    @Override
    public boolean isSameSchema(SqlFieldSchemaInfo sqlFieldSchemaInfo) {
        if (defaultVal != null) {
            if (!defaultVal.equals(sqlFieldSchemaInfo.getDefaultVal())) {
                return false;
            }
        }

        return columnType.equals(sqlFieldSchemaInfo.getColumnType())
                && columnName.equals(sqlFieldSchemaInfo.getColumnName())
                && getLength() == sqlFieldSchemaInfo.getLength() && getPrecision() == sqlFieldSchemaInfo.getPrecision()
                && getScale() == sqlFieldSchemaInfo.getScale();
    }

    @Override
    public SqlEntityInfo getForeignEntityInfo() {
        return foreignEntityInfo;
    }

    @Override
    public SqlFieldInfo getForeignFieldInfo() {
        return foreignFieldInfo;
    }

    @Override
    public SqlFieldInfo getForeignKeyFieldInfo() {
        return foreignKeyFieldInfo;
    }

    public boolean isUnresolvedForeignKey() {
        return foreignKey && foreignEntityInfo == null;
    }

    public boolean isUnresolvedListOnly() {
        return listOnly && foreignEntityInfo == null;
    }
    
    public void resolveForeignKey(SqlEntityInfo foreignEntityInfo, SqlFieldInfo foreignFieldInfo) {
        if (isUnresolvedForeignKey()) {
            this.foreignEntityInfo = foreignEntityInfo;
            this.foreignFieldInfo = foreignFieldInfo;
            this.sqlFieldDimensions = foreignFieldInfo.sqlFieldDimensions;
        }
    }
    
    public void resolveListOnly(SqlEntityInfo foreignEntityInfo) {
        if (isUnresolvedListOnly()) {
            this.foreignEntityInfo = foreignEntityInfo;
        }
    }
    
    public String toDimensionString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{columnName = ").append(columnName);
        sb.append(", columnType = ").append(columnType);
        sb.append(", length = ").append(getLength());
        sb.append(", precision = ").append(getPrecision());
        sb.append(", scale = ").append(getScale());
        sb.append(", defaultVal = ").append(defaultVal).append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toDimensionString();
    }
}
