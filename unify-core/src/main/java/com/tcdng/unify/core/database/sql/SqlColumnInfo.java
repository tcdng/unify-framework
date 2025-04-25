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
package com.tcdng.unify.core.database.sql;

import com.tcdng.unify.common.data.Listable;

/**
 * SQL table column information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class SqlColumnInfo implements Listable {

    private Class<?> javaType;

    private String typeName;

    private String columnName;

    private String defaultVal;

    private int sqlType;

    private int size;

    private int decimalDigits;

    private boolean nullable;

    public SqlColumnInfo(Class<?> javaType, String typeName, String columnName, String defaultVal, int sqlType,
            int size, int decimalDigits, boolean nullable) {
        this.javaType = javaType;
        this.typeName = typeName;
        this.columnName = columnName;
        this.defaultVal = defaultVal;
        this.sqlType = sqlType;
        this.size = size;
        this.decimalDigits = decimalDigits;
        this.nullable = nullable;
    }

    @Override
    public String getListKey() {
        return columnName;
    }

    @Override
    public String getListDescription() {
        return columnName;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDefaultVal() {
        return defaultVal;
    }

    public int getSqlType() {
        return sqlType;
    }

    public int getSize() {
        return size;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    public boolean isNullable() {
        return nullable;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{columnName = ").append(columnName);
        sb.append(", typeName = ").append(typeName);
        sb.append(", sqlType = ").append(sqlType);
        sb.append(", size = ").append(size);
        sb.append(", decimalDigits = ").append(decimalDigits);
        sb.append(", defaultVal = ").append(defaultVal).append("}");
        return sb.toString();
    }
}
