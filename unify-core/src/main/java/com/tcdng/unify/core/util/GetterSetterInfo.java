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
package com.tcdng.unify.core.util;

import java.lang.reflect.Method;

/**
 * Getter setter information.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class GetterSetterInfo {

    private String name;

    private Method getter;

    private Method setter;

    private Class<?> type;

    private Class<?> argumentType0;

    private Class<?> argumentType1;

    private boolean field;

    public GetterSetterInfo(String name, Method getter, Method setter, Class<?> type, Class<?> argumentType0,
            Class<?> argumentType1, boolean field) {
        this.name = name;
        this.getter = getter;
        this.setter = setter;
        this.type = type;
        this.argumentType0 = argumentType0;
        this.argumentType1 = argumentType1;
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public Method getGetter() {
        return getter;
    }

    public Method getSetter() {
        return setter;
    }

    public Class<?> getType() {
        return type;
    }

    public Class<?> getArgumentType0() {
        return argumentType0;
    }

    public Class<?> getArgumentType1() {
        return argumentType1;
    }

    void setArgumentType0(Class<?> argumentType0) {
        if (this.argumentType0 == null) {
            this.argumentType0 = argumentType0;
        }
    }

    void setArgumentType1(Class<?> argumentType1) {
        if (this.argumentType1 == null) {
            this.argumentType1 = argumentType1;
        }
    }

    public boolean isGetter() {
        return this.getter != null;
    }

    public boolean isSetter() {
        return this.setter != null;
    }

    public boolean isGetterSetter() {
        return this.getter != null && this.setter != null;
    }

    public boolean isParameterArgumented0() {
        return this.argumentType0 != null;
    }

    public boolean isParameterArgumented1() {
        return this.argumentType1 != null;
    }

    public boolean isProperty() {
        return field;
    }
}
