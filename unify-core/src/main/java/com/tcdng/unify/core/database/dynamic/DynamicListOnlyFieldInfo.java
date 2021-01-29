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

import com.tcdng.unify.core.constant.EntityFieldType;

/**
 * Dynamic list-only field information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DynamicListOnlyFieldInfo extends DynamicFieldInfo {

    private DynamicFieldInfo propertyFieldInfo;
    
    private String key;

    private String property;

    public DynamicListOnlyFieldInfo(DynamicFieldInfo propertyFieldInfo, String columnName, String fieldName, String key,
            String property) {
        super(EntityFieldType.LIST_ONLY, propertyFieldInfo.getDataType(), columnName, fieldName,
                propertyFieldInfo.getEnumClassName());
        this.propertyFieldInfo = propertyFieldInfo;
        this.key = key;
        this.property = property;
    }

    public DynamicFieldInfo getPropertyFieldInfo() {
        return propertyFieldInfo;
    }

    public String getKey() {
        return key;
    }

    public String getProperty() {
        return property;
    }

}
