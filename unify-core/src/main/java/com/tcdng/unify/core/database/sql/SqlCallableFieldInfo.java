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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.tcdng.unify.core.annotation.CallableDataType;

/**
 * Holds callable field information.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class SqlCallableFieldInfo {

    private CallableDataType dataType;

    private String name;

    private String columnName;

    private Field field;

    private Method getter;

    private Method setter;

    public SqlCallableFieldInfo(CallableDataType dataType, String name, String columnName, Field field, Method getter,
            Method setter) {
        this.dataType = dataType;
        this.name = name;
        this.columnName = columnName;
        this.field = field;
        this.getter = getter;
        this.setter = setter;
    }

    public CallableDataType getDataType() {
        return dataType;
    }

    public String getName() {
        return name;
    }

    public String getColumnName() {
        return columnName;
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
}
