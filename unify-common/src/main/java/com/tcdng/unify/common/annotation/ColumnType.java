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
package com.tcdng.unify.common.annotation;

import com.tcdng.unify.common.constants.ConnectFieldDataType;
import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.common.util.EnumUtils;

/**
 * Database column type enumeration.
 * 
 * @author The Code Department
 * @since 1.0
 */
public enum ColumnType implements EnumConst {

    AUTO("AUT", ConnectFieldDataType.STRING),
    CHARACTER("CHR", ConnectFieldDataType.CHAR),
    BLOB("BLB", ConnectFieldDataType.BLOB),
    BOOLEAN("BLN", ConnectFieldDataType.BOOLEAN),
    BOOLEAN_ARRAY("BLA", ConnectFieldDataType.BOOLEAN_ARRAY),
    CLOB("CLB", ConnectFieldDataType.CLOB),
    DATE("DTE", ConnectFieldDataType.DATE),
    DATE_ARRAY("DTA", ConnectFieldDataType.DATE_ARRAY),
    DECIMAL("DEC", ConnectFieldDataType.DECIMAL),
    DECIMAL_ARRAY("DEA", ConnectFieldDataType.DECIMAL_ARRAY),
    DOUBLE("DBL", ConnectFieldDataType.DOUBLE),
    DOUBLE_ARRAY("DBA", ConnectFieldDataType.DOUBLE_ARRAY),
    FLOAT("FLT", ConnectFieldDataType.FLOAT),
    FLOAT_ARRAY("FLA", ConnectFieldDataType.FLOAT_ARRAY),
    SHORT("SHT", ConnectFieldDataType.SHORT),
    SHORT_ARRAY("SHA", ConnectFieldDataType.SHORT_ARRAY),
    INTEGER("INT", ConnectFieldDataType.INTEGER),
    INTEGER_ARRAY("INA", ConnectFieldDataType.INTEGER_ARRAY),
    LONG("LNG", ConnectFieldDataType.LONG),
    LONG_ARRAY("LNA", ConnectFieldDataType.LONG_ARRAY),
    STRING("STR", ConnectFieldDataType.STRING),
    STRING_ARRAY("STA", ConnectFieldDataType.STRING_ARRAY),
    TIMESTAMP_UTC("TSU", ConnectFieldDataType.TIMESTAMP_UTC),
    TIMESTAMP("TSP", ConnectFieldDataType.TIMESTAMP),
    ENUMCONST("ECT", ConnectFieldDataType.ENUM);

    private final String code;

    private final ConnectFieldDataType connectType;
    
    private ColumnType(String code, ConnectFieldDataType connectType) {
        this.code = code;
        this.connectType = connectType;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return AUTO.code;
    }

    public ConnectFieldDataType connectType() {
    	return connectType;
    }
    
    public boolean isAuto() {
    	return AUTO.equals(this);
    }
    
    public boolean isArray() {
    	return connectType.isArray();
    }

    public boolean isDate() {
    	return DATE.equals(this);
    }

    public boolean isLob() {
    	return BLOB.equals(this) || CLOB.equals(this);
    }

    public boolean isString() {
    	return STRING.equals(this);
    }

    public boolean isTimestamp() {
    	return TIMESTAMP.equals(this) || TIMESTAMP_UTC.equals(this);
    }
    
    public static ColumnType fromCode(String code) {
        return EnumUtils.fromCode(ColumnType.class, code);
    }

    public static ColumnType fromName(String name) {
        return EnumUtils.fromName(ColumnType.class, name);
    }
}
