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
package com.tcdng.unify.core.constant;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.tcdng.unify.convert.constants.EnumConst;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Data type constants.
 * 
 * @author The Code Department
 * @since 1.0
 */
@StaticList(name = "datatypelist", description = "$m{staticlist.datatypelist}")
public enum DataType implements EnumConst {

    CHAR("CH", Character.class, ColumnType.CHARACTER),
    BOOLEAN("BL", Boolean.class, ColumnType.BOOLEAN),
    SHORT("SH", Short.class, ColumnType.SHORT),
    INTEGER("IN", Integer.class, ColumnType.INTEGER, SHORT),
    LONG("LN", Long.class, ColumnType.LONG, INTEGER, SHORT),
    FLOAT("FL", Float.class, ColumnType.FLOAT),
    DOUBLE("DB", Double.class, ColumnType.DOUBLE, FLOAT),
    DECIMAL("DC", BigDecimal.class, ColumnType.DECIMAL, DOUBLE, FLOAT, LONG, INTEGER, SHORT),
    DATE("DT", Date.class, ColumnType.DATE),
    TIMESTAMP("TS", Date.class, ColumnType.TIMESTAMP, DATE),
    TIMESTAMP_UTC("TU", Date.class, ColumnType.TIMESTAMP_UTC, DATE, TIMESTAMP),
    CLOB("CT", String.class, ColumnType.CLOB),
    BLOB("BT", byte[].class, ColumnType.BLOB),
    STRING("ST", String.class, ColumnType.STRING, CHAR, BOOLEAN, DECIMAL, DOUBLE, FLOAT, LONG, INTEGER, SHORT, CLOB);

    private final String code;

    private final Class<?> javaClass;

    private final ColumnType columnType;

    private final Set<DataType> convertibleFromTypes;

    private DataType(String code, Class<?> javaClass, ColumnType columnType, DataType... convertibleFromTypes) {
        this.code = code;
        this.javaClass = javaClass;
        this.columnType = columnType;
        Set<DataType> set = new HashSet<DataType>();
        set.add(this);
        if (convertibleFromTypes != null && convertibleFromTypes.length > 0) {
            set.addAll(Arrays.asList(convertibleFromTypes));
        }

        this.convertibleFromTypes = Collections.unmodifiableSet(set);
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

    public Set<DataType> convertibleFromTypes() {
        return convertibleFromTypes;
    }

    public boolean isConvertibleFrom(DataType dataType) {
        return this.equals(dataType) || convertibleFromTypes.contains(dataType);
    }

    public static DataType fromCode(String code) {
        return EnumUtils.fromCode(DataType.class, code);
    }

    public static DataType fromName(String name) {
        return EnumUtils.fromName(DataType.class, name);
    }
}
