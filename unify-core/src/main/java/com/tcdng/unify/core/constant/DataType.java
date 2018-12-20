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
package com.tcdng.unify.core.constant;

import java.math.BigDecimal;
import java.util.Date;

import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.data.PackableDoc;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Data type constants.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@StaticList("datatypelist")
public enum DataType implements EnumConst {

    CHAR("CH", Character.class, Character[].class),
    BOOLEAN("BL", Boolean.class, Boolean[].class),
    BYTE("BT", Byte.class, Byte[].class),
    SHORT("SH", Short.class, Short[].class),
    INTEGER("IN", Integer.class, Integer[].class),
    LONG("LN", Long.class, Long[].class),
    FLOAT("FL", Float.class, Float[].class),
    DOUBLE("DB", Double.class, Double[].class),
    DECIMAL("DC", BigDecimal.class, BigDecimal[].class),
    DATE("DT", Date.class, Date[].class),
    STRING("ST", String.class, String[].class),
    COMPLEX("CX", PackableDoc.class, PackableDoc[].class);

    private final String code;

    private final Class<?> javaClass;

    private final Class<?> javaArrClass;

    private DataType(String code, Class<?> javaClass, Class<?> javaArrClass) {
        this.code = code;
        this.javaClass = javaClass;
        this.javaArrClass = javaArrClass;
    }

    @Override
    public String code() {
        return this.code;
    }

    public Class<?> javaClass(boolean isArray) {
        if (isArray) {
            return javaArrClass;
        }
        
        return javaClass;
    }

    public Class<?> javaClass() {
        return javaClass;
    }

    public Class<?> javaArrayClass() {
        return javaArrClass;
    }

    public static DataType fromCode(String code) {
        return EnumUtils.fromCode(DataType.class, code);
    }

    public static DataType fromName(String name) {
        return EnumUtils.fromName(DataType.class, name);
    }
}
