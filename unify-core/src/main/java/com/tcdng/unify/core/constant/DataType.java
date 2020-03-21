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
package com.tcdng.unify.core.constant;

import java.math.BigDecimal;
import java.util.Date;

import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Data type constants.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@StaticList("datatypelist")
public enum DataType implements EnumConst {

    CHAR("CH", Character.class, ColumnType.CHARACTER),
    BOOLEAN("BL", Boolean.class, ColumnType.BOOLEAN),
    SHORT("SH", Short.class, ColumnType.SHORT),
    INTEGER("IN", Integer.class, ColumnType.INTEGER),
    LONG("LN", Long.class, ColumnType.LONG),
    FLOAT("FL", Float.class, ColumnType.FLOAT),
    DOUBLE("DB", Double.class, ColumnType.DOUBLE),
    DECIMAL("DC", BigDecimal.class, ColumnType.DECIMAL),
    DATE("DT", Date.class, ColumnType.DATE),
    TIMESTAMP("TS", Date.class, ColumnType.TIMESTAMP),
    TIMESTAMP_UTC("TU", Date.class, ColumnType.TIMESTAMP_UTC),
    STRING("ST", String.class, ColumnType.STRING),
    BLOB("BT", byte[].class, ColumnType.BLOB);

    private final String code;

    private final Class<?> javaClass;

    private final ColumnType columnType;

    private DataType(String code, Class<?> javaClass, ColumnType columnType) {
        this.code = code;
        this.javaClass = javaClass;
        this.columnType = columnType;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return STRING.code;
    }

    public Class<?> javaClass() {
        return javaClass;
    }

    public ColumnType columnType() {
        return columnType;
    }

    public static DataType fromCode(String code) {
        return EnumUtils.fromCode(DataType.class, code);
    }

    public static DataType fromName(String name) {
        return EnumUtils.fromName(DataType.class, name);
    }
}
