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

import java.util.List;

import com.tcdng.unify.core.util.DataUtils;

/**
 * Holds callable result information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class SqlCallableResultInfo {

    private Class<?> callableResultClass;

    private List<SqlCallableFieldInfo> fieldList;

    private boolean useIndexing;
    
    public SqlCallableResultInfo(Class<?> callableResultClass, List<SqlCallableFieldInfo> fieldList, boolean useIndexing) {
        this.callableResultClass = callableResultClass;
        this.fieldList = DataUtils.unmodifiableList(fieldList);
        this.useIndexing = useIndexing;
    }

    public Class<?> getCallableResultClass() {
        return callableResultClass;
    }

    public List<SqlCallableFieldInfo> getFieldList() {
        return fieldList;
    }

    public boolean isUseIndexing() {
        return useIndexing;
    }

}
