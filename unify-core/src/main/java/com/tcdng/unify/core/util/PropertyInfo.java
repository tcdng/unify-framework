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
package com.tcdng.unify.core.util;

/**
 * Property information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class PropertyInfo implements Comparable<PropertyInfo> {

    private String name;

    private Class<?> type;

    private Class<?> argumentType0;

    private Class<?> argumentType1;

    public PropertyInfo(String name, Class<?> type, Class<?> argumentType0, Class<?> argumentType1) {
        this.name = name;
        this.type = type;
        this.argumentType0 = argumentType0;
        this.argumentType1 = argumentType1;
    }

    public String getName() {
        return name;
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

    public boolean isParameterArgumented0() {
        return this.argumentType0 != null;
    }

    public boolean isParameterArgumented1() {
        return this.argumentType1 != null;
    }

    @Override
    public int compareTo(PropertyInfo o) {
        return name.compareTo(o.name);
    }
}
