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
package com.tcdng.unify.core.database.sql;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.transform.Transformer;

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

    private String column;

    private String constraintName;

    private String defaultValue;

    private Transformer<?, ?> transformer;

    private boolean primaryKey;

    private boolean foreignKey;

    private boolean ignoreFkConstraint;

    private boolean nullable;

    private int length;

    private int precision;

    private int scale;

    private Field field;

    private Method getter;

    private Method setter;

    private int orderIndex;

    public SqlFieldInfo(int orderIndex, ColumnType columnType, SqlEntityInfo foreignSqlEntityInfo,
            SqlFieldInfo foreignSqlFieldInfo, SqlFieldInfo foreignKeySqlFieldInfo, String name, String column,
            String constraintName, boolean primaryKey, boolean foreignKey, boolean ignoreFkConstraint,
            Transformer<?, ?> transformer, boolean nullable, int length, int precision, int scale, Field field,
            Method getter, Method setter) {
        this(null, orderIndex, columnType, foreignSqlEntityInfo, foreignSqlFieldInfo, foreignKeySqlFieldInfo, name,
                column, constraintName, primaryKey, foreignKey, ignoreFkConstraint, transformer, nullable, length,
                precision, scale, field, getter, setter);
    }

    public SqlFieldInfo(Long marker, int orderIndex, ColumnType columnType, SqlEntityInfo foreignSqlEntityInfo,
            SqlFieldInfo foreignSqlFieldInfo, SqlFieldInfo foreignKeySqlFieldInfo, String name, String column,
            String constraintName, boolean primaryKey, boolean foreignKey, boolean ignoreFkConstraint,
            Transformer<?, ?> transformer, boolean nullable, int length, int precision, int scale, Field field,
            Method getter, Method setter) {
        this.marker = marker;
        this.columnType = columnType;
        this.foreignEntityInfo = foreignSqlEntityInfo;
        this.foreignFieldInfo = foreignSqlFieldInfo;
        this.foreignKeyFieldInfo = foreignKeySqlFieldInfo;
        this.name = name;
        this.column = column;
        this.constraintName = constraintName;
        this.primaryKey = primaryKey;
        this.foreignKey = foreignKey;
        this.ignoreFkConstraint = ignoreFkConstraint;
        this.transformer = transformer;
        this.nullable = nullable;
        this.length = length;
        this.precision = precision;
        this.scale = scale;
        this.field = field;
        this.getter = getter;
        this.setter = setter;
        this.orderIndex = orderIndex;
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
    public String getColumn() {
        return column;
    }

    @Override
    public String getConstraint() {
        return constraintName;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isListOnly() {
        return foreignKeyFieldInfo != null;
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
    public int getLength() {
        return length;
    }

    @Override
    public int getPrecision() {
        return precision;
    }

    @Override
    public int getScale() {
        return scale;
    }

    @Override
    public ColumnType getColumnType() {
        return columnType;
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

    public Class<?> getFieldClass() {
        return field.getType();
    }

    @Override
    public boolean isSameSchema(SqlFieldSchemaInfo sqlFieldSchemaInfo) {
        if (defaultValue != null) {
            if (!defaultValue.equals(sqlFieldSchemaInfo.getDefaultValue())) {
                return false;
            }
        }

        return columnType.equals(sqlFieldSchemaInfo.getColumnType()) && column.equals(sqlFieldSchemaInfo.getColumn())
                && length == sqlFieldSchemaInfo.getLength() && precision == sqlFieldSchemaInfo.getPrecision()
                && scale == sqlFieldSchemaInfo.getScale();
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
}
