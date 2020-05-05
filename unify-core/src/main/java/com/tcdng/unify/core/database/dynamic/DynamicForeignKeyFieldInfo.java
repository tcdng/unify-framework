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

package com.tcdng.unify.core.database.dynamic;

import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.constant.EntityFieldType;

/**
 * Dynamic foreign key field information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DynamicForeignKeyFieldInfo extends DynamicFieldInfo {

    private DynamicEntityInfo parentDynamicEntityInfo;

    private boolean nullable;

    public DynamicForeignKeyFieldInfo(DynamicEntityInfo parentDynamicEntityInfo, String columnName, String fieldName,
            boolean nullable) {
        super(EntityFieldType.FOREIGN_KEY, DataType.LONG, columnName, fieldName);
        this.parentDynamicEntityInfo = parentDynamicEntityInfo;
        this.nullable = nullable;
    }

    public DynamicEntityInfo getParentDynamicEntityInfo() {
        return parentDynamicEntityInfo;
    }

    public boolean isNullable() {
        return nullable;
    }

}
