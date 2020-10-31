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

import com.tcdng.unify.core.database.Entity;

/**
 * On delete cascade information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class OnDeleteCascadeInfo {

    private Class<? extends Entity> childEntityClass;

    private Field childFkIdField;

    private Field childFkTypeField;

    public OnDeleteCascadeInfo(Class<? extends Entity> childEntityClass, Field childFkIdField, Field childFkTypeField) {
        this.childEntityClass = childEntityClass;
        this.childFkIdField = childFkIdField;
        this.childFkTypeField = childFkTypeField;
    }

    public Class<? extends Entity> getChildEntityClass() {
        return childEntityClass;
    }

    public Field getChildFkIdField() {
        return childFkIdField;
    }

    public Field getChildFkTypeField() {
        return childFkTypeField;
    }

    public boolean isWithChildFkType() {
        return childFkTypeField != null;
    }
}
