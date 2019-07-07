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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.data.AbstractPool;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.database.CallableProc;

/**
 * SQL statement information pool by SQL callable statement type.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class SqlCallableStatementPools {

    private FactoryMap<SqlCallableInfo, SqlCallableStatementPool> poolMap;

    private Map<ColumnType, SqlDataTypePolicy> sqlDataTypePolicies;

    private long getTimeout;

    private int minObjects;

    private int maxObjects;

    public SqlCallableStatementPools(Map<ColumnType, SqlDataTypePolicy> sqlDataTypePolicies, long getTimeout,
            int minObjects, int maxObjects) {
        this.sqlDataTypePolicies = sqlDataTypePolicies;
        this.getTimeout = getTimeout;
        this.minObjects = minObjects;
        this.maxObjects = maxObjects;

        this.poolMap = new FactoryMap<SqlCallableInfo, SqlCallableStatementPool>() {

            @Override
            protected SqlCallableStatementPool create(SqlCallableInfo sqlCallableInfo, Object... params)
                    throws Exception {
                return new SqlCallableStatementPool(sqlCallableInfo);
            }
        };
    }

    public SqlCallableStatement getSqlCallableStatement(SqlCallableInfo sqlCallableInfo, Object... params)
            throws UnifyException {
        return poolMap.get(sqlCallableInfo).borrowObject(params);
    }

    public void restore(SqlCallableStatement sqlCallableStatement) throws UnifyException {
        poolMap.get(sqlCallableStatement.getSqlCallableInfo()).returnObject(sqlCallableStatement);
    }

    private class SqlCallableStatementPool extends AbstractPool<SqlCallableStatement> {

        private SqlCallableInfo sqlCallableInfo;

        private String sql;

        private Map<Class<?>, List<SqlCallableResult>> resultInfoListByTypes;

        public SqlCallableStatementPool(SqlCallableInfo sqlCallableInfo) {
            super(getTimeout, minObjects, maxObjects);
            this.sqlCallableInfo = sqlCallableInfo;
        }

        public SqlCallableStatementPool(long getTimeout, int minObjects, int maxObjects) {
            super(getTimeout, minObjects, maxObjects, true);
        }

        @Override
        protected SqlCallableStatement createObject(Object... params) throws Exception {
            List<SqlParameter> paramInfoList = Collections.emptyList();
            if (sqlCallableInfo.isParams()) {
                paramInfoList = new ArrayList<SqlParameter>();
                for (SqlCallableParamInfo sqlCallableParamInfo : sqlCallableInfo.getParamInfoList()) {
                    paramInfoList.add(
                            new SqlParameter(sqlDataTypePolicies.get(sqlCallableParamInfo.getDataType().columnType()),
                                    sqlCallableParamInfo.isInput(), sqlCallableParamInfo.isOutput()));
                }

                paramInfoList = Collections.unmodifiableList(paramInfoList);
            }

            return new SqlCallableStatement(sqlCallableInfo, getSql(), paramInfoList, getResultInfoListByTypes());
        }

        @Override
        protected void onGetObject(SqlCallableStatement sqlCallableStatement, Object... params) throws Exception {
            CallableProc callableProc = (CallableProc) params[0];
            if (sqlCallableInfo.isParams()) {
                // Populate statement parameters with value from callableProc object
                List<SqlCallableParamInfo> paramInfoList = sqlCallableInfo.getParamInfoList();
                List<SqlParameter> sqlParamList = sqlCallableStatement.getParameterInfoList();
                int len = paramInfoList.size();
                for (int i = 0; i < len; i++) {
                    SqlCallableParamInfo sqlCallableParamInfo = paramInfoList.get(i);
                    if (sqlCallableParamInfo.isInput()) {
                        sqlParamList.get(i).setValue(sqlCallableParamInfo.getGetter().invoke(callableProc));
                    }
                }
            }
        }

        @Override
        protected void destroyObject(SqlCallableStatement object) {

        }

        private Map<Class<?>, List<SqlCallableResult>> getResultInfoListByTypes() {
            if (resultInfoListByTypes == null) {
                synchronized(SqlCallableStatementPool.class) {
                    if (resultInfoListByTypes == null) {
                        resultInfoListByTypes = Collections.emptyMap();
                        if (sqlCallableInfo.isResults()) {
                            resultInfoListByTypes = new LinkedHashMap<Class<?>, List<SqlCallableResult>>();
                            for (SqlCallableResultInfo sqlCallableResultInfo : sqlCallableInfo.getResultInfoList()) {
                                List<SqlCallableResult> resultFieldList = new ArrayList<SqlCallableResult>();
                                for (SqlCallableFieldInfo sqlCallableFieldInfo : sqlCallableResultInfo.getFieldList()) {
                                    resultFieldList.add(new SqlCallableResult(
                                            sqlDataTypePolicies.get(sqlCallableFieldInfo.getDataType().columnType()),
                                            sqlCallableFieldInfo));
                                }

                                resultInfoListByTypes.put(sqlCallableResultInfo.getCallableResultClass(), resultFieldList);
                            }

                            resultInfoListByTypes = Collections.unmodifiableMap(resultInfoListByTypes);
                        }
                    }
                }
            }

            return resultInfoListByTypes;
        }

        private String getSql() {
            if (sql == null) {
                synchronized(SqlCallableStatementPool.class) {
                    if (sql == null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("{call ");
                        sb.append(sqlCallableInfo.getSchemaProcedureName());
                        sb.append("(");
                        if (sqlCallableInfo.isParams()) {
                            boolean isAppendSym = false;
                            int paramCount = sqlCallableInfo.getParamInfoList().size();
                            for (int i = 0; i < paramCount; i++) {
                                if (isAppendSym) {
                                    sb.append(", ");
                                } else {
                                    isAppendSym = true;
                                }
                                sb.append("?");
                            }
                        }
                        sb.append(")}");

                        sql = sb.toString();
                    }
                }
            }

            return sql;
        }
    }

}
