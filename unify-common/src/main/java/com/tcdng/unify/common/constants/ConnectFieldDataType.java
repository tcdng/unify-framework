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
package com.tcdng.unify.common.constants;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Interconnect field data type.
 * 
 * @author The Code Department
 * @since 1.0
 */
public enum ConnectFieldDataType {

    CHAR(
            Character.class, false),
    BOOLEAN(
            Boolean.class, false),
    SHORT(
            Short.class, false),
    INTEGER(
            Integer.class, false),
    LONG(
            Long.class, false),
    FLOAT(
            Float.class, false),
    DOUBLE(
            Double.class, false),
    DECIMAL(
            BigDecimal.class, false),
    DATE(
            Date.class, false),
    BOOLEAN_ARRAY(
            Boolean.class, true),
    SHORT_ARRAY(
            Short.class, true),
    INTEGER_ARRAY(
            Integer.class, true),
    LONG_ARRAY(
            Long.class, true),
    FLOAT_ARRAY(
            Float.class, true),
    DOUBLE_ARRAY(
            Double.class, true),
    DECIMAL_ARRAY(
            BigDecimal.class, true),
    DATE_ARRAY(
            Date.class, true),
    TIMESTAMP_UTC(
            Date.class, false),
    TIMESTAMP(
            Date.class, false),
    CLOB(
            String.class, false),
    BLOB(
            byte[].class, false),
    STRING(
            String.class, false),
    STRING_ARRAY(
            String.class, true),
    ENUM(
            String.class, false),
    ENUM_REF(
            String.class, false),
    REF(
            Long.class, false),
    LIST_ONLY(
            null, false),
    CHILD(
            null, false),
    CHILD_LIST(
            null, false);

    private final Class<?> javaClass;

    private final boolean array;
    
    private ConnectFieldDataType(Class<?> javaClass, boolean array) {
        this.javaClass = javaClass;
        this.array = array;
    }

    public Class<?> javaClass() {
        return javaClass;
    }
    
    public boolean isArray() {
        return array;
    }
    
    public boolean references() {
        return this.equals(REF) || this.equals(LIST_ONLY) || this.equals(CHILD) || this.equals(CHILD_LIST);
    }
    
    public boolean isRef() {
        return this.equals(REF);
    }
    
    public boolean isEnum() {
        return this.equals(ENUM) || this.equals(ENUM_REF);
    }
    
    public boolean isListOnly() {
        return this.equals(LIST_ONLY);
    }
    
    public boolean isChild() {
        return this.equals(CHILD);
    }
    
    public boolean isChildList() {
        return this.equals(CHILD_LIST);
    }

    public boolean isDate() {
        return this.equals(DATE);
    }

    public boolean isTimestamp() {
        return TIMESTAMP.equals(this) || TIMESTAMP_UTC.equals(this);
    }

    public boolean isBoolean() {
        return BOOLEAN.equals(this);
    }

    public boolean isLob() {
        return BLOB.equals(this) || CLOB.equals(this);
    }

    public boolean isString() {
        return STRING.equals(this);
    }
    
    public boolean isInteger() {
        return LONG.equals(this) || INTEGER.equals(this) || SHORT.equals(this);
    }
    
    public boolean isDouble() {
        return DOUBLE.equals(this) || FLOAT.equals(this);
    }
    
    public boolean isDecimal() {
        return DECIMAL.equals(this);
    }
}
