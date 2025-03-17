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

    AUTO("AUT", ConnectFieldDataType.STRING, false),
    CHARACTER("CHR", ConnectFieldDataType.CHAR, false),
    BLOB("BLB", ConnectFieldDataType.BLOB, false),
    BOOLEAN("BLN", ConnectFieldDataType.BOOLEAN, false),
    BOOLEAN_ARRAY("BLA", ConnectFieldDataType.STRING, true),
    CLOB("CLB", ConnectFieldDataType.CLOB, false),
    DATE("DTE", ConnectFieldDataType.DATE, false),
    DATE_ARRAY("DTA", ConnectFieldDataType.STRING, true),
    DECIMAL("DEC", ConnectFieldDataType.DECIMAL, false),
    DECIMAL_ARRAY("DEA", ConnectFieldDataType.STRING, true),
    DOUBLE("DBL", ConnectFieldDataType.DOUBLE, false),
    DOUBLE_ARRAY("DBA", ConnectFieldDataType.STRING, true),
    FLOAT("FLT", ConnectFieldDataType.FLOAT, false),
    FLOAT_ARRAY("FLA", ConnectFieldDataType.STRING, true),
    SHORT("SHT", ConnectFieldDataType.SHORT, false),
    SHORT_ARRAY("SHA", ConnectFieldDataType.STRING, true),
    INTEGER("INT", ConnectFieldDataType.INTEGER, false),
    INTEGER_ARRAY("INA", ConnectFieldDataType.STRING, true),
    LONG("LNG", ConnectFieldDataType.LONG, false),
    LONG_ARRAY("LNA", ConnectFieldDataType.STRING, true),
    STRING("STR", ConnectFieldDataType.STRING, false),
    STRING_ARRAY("STA", ConnectFieldDataType.STRING, true),
    TIMESTAMP_UTC("TSU", ConnectFieldDataType.TIMESTAMP_UTC, false),
    TIMESTAMP("TSP", ConnectFieldDataType.TIMESTAMP, false),
    ENUMCONST("ECT", ConnectFieldDataType.ENUM, false);

    private final String code;

    private final ConnectFieldDataType connectType;

    private final boolean array;
    
    private ColumnType(String code, ConnectFieldDataType connectType, boolean array) {
        this.code = code;
        this.connectType = connectType;
        this.array = array;
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
    	return array;
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
