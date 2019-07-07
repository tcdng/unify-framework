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
import java.util.Map;

/**
 * SQL callable statement information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class SqlCallableStatement {

    private SqlCallableInfo sqlCallableInfo;

    private String sql;

    private List<SqlParameter> parameterInfoList;

    private Map<Class<?>, List<SqlCallableResult>> resultInfoListByType;

    public SqlCallableStatement(SqlCallableInfo sqlCallableInfo, String sql,
            List<SqlParameter> parameterInfoList, Map<Class<?>, List<SqlCallableResult>> resultInfoListByType) {
        this.sqlCallableInfo = sqlCallableInfo;
        this.sql = sql;
        this.parameterInfoList = parameterInfoList;
        this.resultInfoListByType = resultInfoListByType;
    }

    public SqlCallableInfo getSqlCallableInfo() {
        return sqlCallableInfo;
    }

    public String getSql() {
        return sql;
    }

    public List<SqlParameter> getParameterInfoList() {
        return parameterInfoList;
    }

    public Map<Class<?>, List<SqlCallableResult>> getResultInfoListByType() {
        return resultInfoListByType;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[sql = ").append(sql);
        if (parameterInfoList != null && !parameterInfoList.isEmpty()) {
            sb.append(":");
            boolean isAppendSymbol = false;
            for (SqlParameter sqlParameter : parameterInfoList) {
                if (isAppendSymbol) {
                    sb.append(", ");
                } else {
                    isAppendSymbol = true;
                }

                sb.append("{").append(sqlParameter.toString()).append("}");
            }
        }

        sb.append("]");
        return sb.toString();
    }
}

