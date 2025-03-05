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
package com.tcdng.unify.common.data;

import java.util.List;
import java.util.Set;

import com.tcdng.unify.common.constants.ConnectFieldDataType;

/**
 * Field information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class EntityFieldInfo {

    private ConnectFieldDataType type;

    private Class<?> fieldClass;

    private String name;

    private String description;

    private String column;

    private String references;

    private Class<? extends Enum<?>> enumImplClass;

    private int precision;

    private int scale;

    private int length;

    private boolean nullable;

    public EntityFieldInfo(ConnectFieldDataType type, Class<?> fieldClass, String name, String description,
            String column, String references, Class<? extends Enum<?>> enumImplClass, int precision, int scale,
            int length, boolean nullable) {
        this.type = type;
        this.fieldClass = fieldClass;
        this.name = name;
        this.description = description;
        this.column = column;
        this.references = references;
        this.enumImplClass = enumImplClass;
        this.precision = precision;
        this.scale = scale;
        this.length = length;
        this.nullable = nullable;
    }

    public void overrideBlank(EntityFieldInfo _entityFieldInfo) {
        if (column == null || column.isEmpty()) {
            column = _entityFieldInfo.column;
        }
    }

    public ConnectFieldDataType getType() {
        return type;
    }

    public Class<?> getConvertClass() {
        return fieldClass != null && fieldClass.isPrimitive() ? fieldClass : type.javaClass();
    }

    public Class<?> getFieldClass() {
        return fieldClass;
    }

    public Class<?> getJavaClass() {
        return type.javaClass();
    }

    public String getName() {
        return name;
    }

    public String getReferences() {
        return references;
    }

    public Class<? extends Enum<?>> getEnumImplClass() {
        return enumImplClass;
    }

    public String getDescription() {
        return description;
    }

    public String getColumn() {
        return column;
    }

    public int getScale() {
        return scale;
    }

    public int getPrecision() {
        return precision;
    }

    public int getLength() {
        return length;
    }

    public boolean isSet() {
        return fieldClass != null && Set.class.isAssignableFrom(fieldClass);
    }

    public boolean isList() {
        return fieldClass != null && List.class.isAssignableFrom(fieldClass);
    }
    
    public boolean isNullable() {
        return nullable;
    }

    public boolean references() {
        return type.references();
    }

    public boolean isRef() {
        return type.isRef();
    }

    public boolean isEnum() {
        return type.isEnum();
    }

    public boolean isLob() {
        return type.isLob();
    }

    public boolean isListOnly() {
        return type.isListOnly();
    }

    public boolean isChild() {
        return type.isChild();
    }

    public boolean isChildList() {
        return type.isChildList();
    }

    public boolean isDate() {
        return type.isDate();
    }

    public boolean isTimestamp() {
        return type.isTimestamp();
    }

    public boolean isBoolean() {
        return type.isBoolean();
    }

    public boolean isString() {
        return type.isString();
    }

    public boolean isInteger() {
        return type.isInteger();
    }

    public boolean isDouble() {
        return type.isDouble();
    }

    public boolean isDecimal() {
        return type.isDecimal();
    }

}
