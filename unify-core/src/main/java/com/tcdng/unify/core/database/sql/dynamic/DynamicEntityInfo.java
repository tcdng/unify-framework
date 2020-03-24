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

package com.tcdng.unify.core.database.sql.dynamic;

import java.util.List;

import com.tcdng.unify.core.util.DataUtils;

/**
 * Dynamic entity information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DynamicEntityInfo {

    private String className;
    
    private List<DynamicFieldInfo> fieldInfoList;

    public DynamicEntityInfo(String className, List<DynamicFieldInfo> fieldInfoList) {
        this.className = className;
        this.fieldInfoList = DataUtils.unmodifiableList(fieldInfoList);
    }

    public String getClassName() {
        return className;
    }

    public List<DynamicFieldInfo> getFieldInfoList() {
        return fieldInfoList;
    }
}
