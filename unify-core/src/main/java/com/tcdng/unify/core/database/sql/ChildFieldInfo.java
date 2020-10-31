/*
 * Copyright 2018-2020 The Code Department.
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

import com.tcdng.unify.core.database.Entity;

/**
 * Child field information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ChildFieldInfo extends OnDeleteCascadeInfo {

    private Method childFkIdSetter;

    private Method childFkTypeSetter;

    private Field field;

    private Method getter;

    private Method setter;

    private boolean list;

    public ChildFieldInfo(Class<? extends Entity> childEntityClass, Field childFkIdField, Method childFkIdSetter,
            Field childFkTypeField, Method childFkTypeSetter, Field field, Method getter, Method setter, boolean list) {
        super(childEntityClass, childFkIdField, childFkTypeField);
        this.childFkIdSetter = childFkIdSetter;
        this.childFkTypeSetter = childFkTypeSetter;
        this.field = field;
        this.getter = getter;
        this.setter = setter;
        this.list = list;
    }

    public String getName() {
        return field.getName();
    }

    public Method getChildFkIdSetter() {
        return childFkIdSetter;
    }

    public Method getChildFkTypeSetter() {
        return childFkTypeSetter;
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

    public boolean isList() {
        return list;
    }

    public boolean isWithChildFkType() {
        return childFkTypeSetter != null;
    }
}
