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

package com.tcdng.unify.core.database.dynamic;

import com.tcdng.unify.core.annotation.DynamicFieldType;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.constant.DynamicEntityFieldType;

/**
 * Dynamic foreign key field information.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class DynamicForeignKeyFieldInfo extends DynamicFieldInfo {

    private DynamicEntityInfo parentDynamicEntityInfo;

    private String defaultVal;

    private boolean unlinked;

    private boolean nullable;

    public DynamicForeignKeyFieldInfo(DynamicFieldType type, DynamicEntityInfo parentDynamicEntityInfo,
            String columnName, String fieldName, String defaultVal, boolean unlinked, boolean nullable) {
        super(type, DynamicEntityFieldType.FOREIGN_KEY, DataType.LONG, columnName, fieldName, null, null, false, false);
        this.parentDynamicEntityInfo = parentDynamicEntityInfo;
        this.defaultVal = defaultVal;
        this.unlinked = unlinked;
        this.nullable = nullable;
    }

    public DynamicForeignKeyFieldInfo(DynamicFieldType type, String enumClassName, String columnName, String fieldName,
            String defaultVal, boolean unlinked, boolean nullable) {
        super(type, DynamicEntityFieldType.FOREIGN_KEY, DataType.STRING, columnName, fieldName, null, enumClassName, false, false);
        this.defaultVal = defaultVal;
        this.unlinked = unlinked;
        this.nullable = nullable;
    }

    public DynamicEntityInfo getParentDynamicEntityInfo() {
        return parentDynamicEntityInfo;
    }

    public void updateParentDynamicEntityInfo(DynamicEntityInfo _parentDynamicEntityInfo) {
        if (parentDynamicEntityInfo != null && parentDynamicEntityInfo.isSelfReference()) {
            parentDynamicEntityInfo = _parentDynamicEntityInfo;
        }
    }

    public String getDefaultVal() {
        return defaultVal;
    }

    public boolean isUnlinked() {
		return unlinked;
	}

	public boolean isNullable() {
        return nullable;
    }

}
