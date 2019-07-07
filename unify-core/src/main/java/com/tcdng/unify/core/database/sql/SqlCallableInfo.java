/*
 * Copyright 2018-2019 The Code Department.
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
 * Holds callable information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class SqlCallableInfo {

    private Class<?> callableClass;

    private String procedureName;

    private String preferredProcedureName;

    private String schemaProcedureName;

    private List<SqlCallableParamInfo> paramInfoList;

    private List<SqlCallableResultInfo> resultInfoList;

    public SqlCallableInfo(Class<?> callableClass, String procedureName, String preferredProcedureName,
            String schemaProcedureName, List<SqlCallableParamInfo> paramInfoList,
            List<SqlCallableResultInfo> resultInfoList) {
        this.callableClass = callableClass;
        this.procedureName = procedureName;
        this.preferredProcedureName = preferredProcedureName;
        this.schemaProcedureName = schemaProcedureName;
        this.paramInfoList = DataUtils.unmodifiableList(paramInfoList);
        this.resultInfoList = DataUtils.unmodifiableList(resultInfoList);
    }

    public Class<?> getCallableClass() {
        return callableClass;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public String getPreferredProcedureName() {
        return preferredProcedureName;
    }

    public String getSchemaProcedureName() {
        return schemaProcedureName;
    }

    public List<SqlCallableParamInfo> getParamInfoList() {
        return paramInfoList;
    }

    public boolean isParams() {
        return !paramInfoList.isEmpty();
    }

    public List<SqlCallableResultInfo> getResultInfoList() {
        return resultInfoList;
    }

    public boolean isResults() {
        return !resultInfoList.isEmpty();
    }
}
