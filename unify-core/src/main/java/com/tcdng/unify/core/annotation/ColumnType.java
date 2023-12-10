/*
 * Copyright 2018-2023 The Code Department.
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
package com.tcdng.unify.core.annotation;

import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Database column type enumeration.
 * 
 * @author The Code Department
 * @since 1.0
 */
public enum ColumnType implements EnumConst {

    AUTO("AUT"),
    CHARACTER("CHR"),
    BLOB("BLB"),
    BOOLEAN("BLN"),
    BOOLEAN_ARRAY("BLA"),
    CLOB("CLB"),
    DATE("DTE"),
    DECIMAL("DEC"),
    DOUBLE("DBL"),
    DOUBLE_ARRAY("DBA"),
    FLOAT("FLT"),
    FLOAT_ARRAY("FLA"),
    SHORT("SHT"),
    SHORT_ARRAY("SHA"),
    INTEGER("INT"),
    INTEGER_ARRAY("INA"),
    LONG("LNG"),
    LONG_ARRAY("LNA"),
    STRING("STR"),
    STRING_ARRAY("STA"),
    TIMESTAMP_UTC("TSU"),
    TIMESTAMP("TSP"),
    ENUMCONST("ECT");

    private final String code;

    private ColumnType(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return AUTO.code;
    }

    public boolean isAuto() {
    	return AUTO.equals(this);
    }

    public boolean isDate() {
    	return DATE.equals(this);
    }

    public boolean isLob() {
    	return BLOB.equals(this) || CLOB.equals(this);
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
