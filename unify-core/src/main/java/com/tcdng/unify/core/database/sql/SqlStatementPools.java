/*
 * Copyright 2018-2022 The Code Department.
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
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.data.AbstractPool;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.transform.Transformer;

/**
 * SQL statement information pool by SQL statement type.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class SqlStatementPools {

    private FactoryMap<SqlStatementType, SqlStatementPool> poolMap;

    private SqlEntityInfo sqlEntityInfo;

    private Map<ColumnType, SqlDataTypePolicy> sqlDataTypePolicies;

    private SqlCache sqlCache;

    private long getTimeout;

    private int minObjects;

    private int maxObjects;

    public SqlStatementPools(SqlEntityInfo sqlEntityInfo, Map<ColumnType, SqlDataTypePolicy> sqlDataTypePolicies,
            SqlCache sqlCache, long getTimeout, int minObjects, int maxObjects) {
        this.sqlEntityInfo = sqlEntityInfo;
        this.sqlDataTypePolicies = sqlDataTypePolicies;
        this.sqlCache = sqlCache;
        this.getTimeout = getTimeout;
        this.minObjects = minObjects;
        this.maxObjects = maxObjects;

        this.poolMap = new FactoryMap<SqlStatementType, SqlStatementPool>() {
            @Override
            protected SqlStatementPool create(SqlStatementType type, Object... params) throws Exception {
                switch (type) {
                    case CREATE:
                        return new CreateSqlStatementInfoPool();
                    case CREATE_UNMANAGED_IDENTITY:
                        return new CreateUnmanagedIdentitySqlStatementInfoPool();
                    case COUNT:
                    case MIN:
                    case MAX:
                        break;
                    case DELETE:
                        break;
                    case DELETE_BY_PK:
                        return new DeleteByPkSqlStatementInfoPool();
                    case DELETE_BY_PK_VERSION:
                        return new DeleteByPkVersionSqlStatementInfoPool();
                    case FIND:
                        break;
                    case FIND_BY_PK:
                        return new FindByPkSqlStatementInfoPool();
                    case FIND_BY_PK_VERSION:
                        return new FindByPkVersionSqlStatementInfoPool();
                    case LIST:
                        break;
                    case LIST_BY_PK:
                        return new ListByPkSqlStatementInfoPool();
                    case LIST_BY_PK_VERSION:
                        return new ListByPkVersionSqlStatementInfoPool();
                    case UPDATE:
                        break;
                    case UPDATE_BY_PK:
                        return new UpdateByPkSqlStatementInfoPool();
                    case UPDATE_BY_PK_VERSION:
                        return new UpdateByPkVersionSqlStatementInfoPool();
                    default:
                        break;
                }
                return null;
            }
        };
    }

    public SqlStatement getSqlStatement(SqlStatementType type, Object... params) throws UnifyException {
        return poolMap.get(type).borrowObject(params);
    }

    public void restore(SqlStatement sqlStatement) throws UnifyException {
        poolMap.get(sqlStatement.getType()).returnObject(sqlStatement);
    }

    private abstract class SqlStatementPool extends AbstractPool<SqlStatement> {

        private List<SqlResult> sqlResultList;

        public SqlStatementPool(long getTimeout, int minObjects, int maxObjects) {
            super(getTimeout, minObjects, maxObjects, true);
        }

        @Override
        protected void destroyObject(SqlStatement object) {

        }

        protected List<SqlResult> getSqlResultList(List<SqlFieldInfo> sqlFieldInfoList) {
            if (sqlResultList == null) {
                synchronized (SqlStatementPool.class) {
                    if (sqlResultList == null) {
                        sqlResultList = new ArrayList<SqlResult>();
                        for (SqlFieldInfo sqlFieldInfo : sqlFieldInfoList) {
                            sqlResultList.add(
                                    new SqlResult(sqlDataTypePolicies.get(sqlFieldInfo.getColumnType()), sqlFieldInfo));
                        }
                    }
                }
            }

            return sqlResultList;
        }
    }

    private class CreateSqlStatementInfoPool extends SqlStatementPool {

        public CreateSqlStatementInfoPool() {
            super(getTimeout, minObjects, maxObjects);
        }

        @Override
        protected SqlStatement createObject(Object... params) throws Exception {
            List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
            for (SqlFieldInfo sqlFieldInfo : sqlEntityInfo.getFieldInfos()) {
                parameterInfoList.add(new SqlParameter(sqlDataTypePolicies.get(sqlFieldInfo.getColumnType())));
            }
            return new SqlStatement(sqlEntityInfo, SqlStatementType.CREATE, sqlCache.getCreateSql(), parameterInfoList);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onGetObject(SqlStatement sqlStatement, Object... params) throws Exception {
            List<SqlParameter> parameterInfoList = sqlStatement.getParameterInfoList();
            int i = 0;
            for (SqlFieldInfo sqlFieldInfo : sqlEntityInfo.getFieldInfos()) {
                if (sqlFieldInfo.isTransformed()) {
                    parameterInfoList.get(i++).setValue(((Transformer<Object, Object>) sqlFieldInfo.getTransformer())
                            .forwardTransform(sqlFieldInfo.getGetter().invoke(params[0])));
                } else {
                    parameterInfoList.get(i++).setValue(sqlFieldInfo.getGetter().invoke(params[0]));
                }
            }
        }

    }

    private class CreateUnmanagedIdentitySqlStatementInfoPool extends SqlStatementPool {

        public CreateUnmanagedIdentitySqlStatementInfoPool() {
            super(getTimeout, minObjects, maxObjects);
        }

        @Override
        protected SqlStatement createObject(Object... params) throws Exception {
            List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
            for (SqlFieldInfo sqlFieldInfo : sqlEntityInfo.getFieldInfos()) {
                if (!sqlFieldInfo.isPrimaryKey()) {
                    parameterInfoList.add(new SqlParameter(sqlDataTypePolicies.get(sqlFieldInfo.getColumnType())));
                }
            }
            return new SqlStatement(sqlEntityInfo, SqlStatementType.CREATE_UNMANAGED_IDENTITY,
                    sqlCache.getCreateUnmanagedIdentitySql(), parameterInfoList);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onGetObject(SqlStatement sqlStatement, Object... params) throws Exception {
            List<SqlParameter> parameterInfoList = sqlStatement.getParameterInfoList();
            int i = 0;
            for (SqlFieldInfo sqlFieldInfo : sqlEntityInfo.getFieldInfos()) {
                if (!sqlFieldInfo.isPrimaryKey()) {
                    if (sqlFieldInfo.isTransformed()) {
                        parameterInfoList.get(i++)
                                .setValue(((Transformer<Object, Object>) sqlFieldInfo.getTransformer())
                                        .forwardTransform(sqlFieldInfo.getGetter().invoke(params[0])));
                    } else {
                        parameterInfoList.get(i++).setValue(sqlFieldInfo.getGetter().invoke(params[0]));
                    }
                }
            }
        }

    }


    private class DeleteByPkSqlStatementInfoPool extends SqlStatementPool {

        public DeleteByPkSqlStatementInfoPool() {
            super(getTimeout, minObjects, maxObjects);
        }

        @Override
        protected SqlStatement createObject(Object... params) throws Exception {
            List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
            parameterInfoList
                    .add(new SqlParameter(sqlDataTypePolicies.get(sqlEntityInfo.getIdFieldInfo().getColumnType())));
            return new SqlStatement(sqlEntityInfo, SqlStatementType.DELETE_BY_PK, sqlCache.getDeleteByPkSql(),
                    parameterInfoList);
        }

        @Override
        protected void onGetObject(SqlStatement sqlStatement, Object... params) throws Exception {
            sqlStatement.getParameterInfoList().get(0).setValue(params[0]);
        }
    }

    private class DeleteByPkVersionSqlStatementInfoPool extends SqlStatementPool {

        public DeleteByPkVersionSqlStatementInfoPool() {
            super(getTimeout, minObjects, maxObjects);
        }

        @Override
        protected SqlStatement createObject(Object... params) throws Exception {
            List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
            parameterInfoList
                    .add(new SqlParameter(sqlDataTypePolicies.get(sqlEntityInfo.getIdFieldInfo().getColumnType())));
            parameterInfoList.add(
                    new SqlParameter(sqlDataTypePolicies.get(sqlEntityInfo.getVersionFieldInfo().getColumnType())));
            return new SqlStatement(sqlEntityInfo, SqlStatementType.DELETE_BY_PK_VERSION,
                    sqlCache.getDeleteByPkVersionSql(), parameterInfoList);
        }

        @Override
        protected void onGetObject(SqlStatement sqlStatement, Object... params) throws Exception {
            sqlStatement.getParameterInfoList().get(0).setValue(params[0]);
            sqlStatement.getParameterInfoList().get(1).setValue(params[1]);
        }
    }

    private class FindByPkSqlStatementInfoPool extends SqlStatementPool {

        public FindByPkSqlStatementInfoPool() {
            super(getTimeout, minObjects, maxObjects);
        }

        @Override
        protected SqlStatement createObject(Object... params) throws Exception {
            List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
            parameterInfoList
                    .add(new SqlParameter(sqlDataTypePolicies.get(sqlEntityInfo.getIdFieldInfo().getColumnType())));
            if (sqlEntityInfo.isViewOnly()) {
                return new SqlStatement(sqlEntityInfo, SqlStatementType.FIND_BY_PK, sqlCache.getFindByPkSql(),
                        parameterInfoList, getSqlResultList(sqlEntityInfo.getListFieldInfos()));
            }

            return new SqlStatement(sqlEntityInfo, SqlStatementType.FIND_BY_PK, sqlCache.getFindByPkSql(),
                    parameterInfoList, getSqlResultList(sqlEntityInfo.getFieldInfos()));
        }

        @Override
        protected void onGetObject(SqlStatement sqlStatement, Object... params) throws Exception {
            sqlStatement.getParameterInfoList().get(0).setValue(params[0]);
        }
    }

    private class FindByPkVersionSqlStatementInfoPool extends SqlStatementPool {

        public FindByPkVersionSqlStatementInfoPool() {
            super(getTimeout, minObjects, maxObjects);
        }

        @Override
        protected SqlStatement createObject(Object... params) throws Exception {
            List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
            parameterInfoList
                    .add(new SqlParameter(sqlDataTypePolicies.get(sqlEntityInfo.getIdFieldInfo().getColumnType())));
            parameterInfoList.add(
                    new SqlParameter(sqlDataTypePolicies.get(sqlEntityInfo.getVersionFieldInfo().getColumnType())));

            if (sqlEntityInfo.isViewOnly()) {
                return new SqlStatement(sqlEntityInfo, SqlStatementType.FIND_BY_PK_VERSION,
                        sqlCache.getFindByPkVersionSql(), parameterInfoList,
                        getSqlResultList(sqlEntityInfo.getListFieldInfos()));
            }

            return new SqlStatement(sqlEntityInfo, SqlStatementType.FIND_BY_PK_VERSION,
                    sqlCache.getFindByPkVersionSql(), parameterInfoList,
                    getSqlResultList(sqlEntityInfo.getFieldInfos()));
        }

        @Override
        protected void onGetObject(SqlStatement sqlStatement, Object... params) throws Exception {
            sqlStatement.getParameterInfoList().get(0).setValue(params[0]);
            sqlStatement.getParameterInfoList().get(1).setValue(params[1]);
        }
    }

    private class ListByPkSqlStatementInfoPool extends SqlStatementPool {

        public ListByPkSqlStatementInfoPool() {
            super(getTimeout, minObjects, maxObjects);
        }

        @Override
        protected SqlStatement createObject(Object... params) throws Exception {
            List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
            parameterInfoList
                    .add(new SqlParameter(sqlDataTypePolicies.get(sqlEntityInfo.getIdFieldInfo().getColumnType())));

            return new SqlStatement(sqlEntityInfo, SqlStatementType.LIST_BY_PK, sqlCache.getListByPkSql(),
                    parameterInfoList, getSqlResultList(sqlEntityInfo.getListFieldInfos()));
        }

        @Override
        protected void onGetObject(SqlStatement sqlStatement, Object... params) throws Exception {
            sqlStatement.getParameterInfoList().get(0).setValue(params[0]);
        }
    }

    private class ListByPkVersionSqlStatementInfoPool extends SqlStatementPool {

        public ListByPkVersionSqlStatementInfoPool() {
            super(getTimeout, minObjects, maxObjects);
        }

        @Override
        protected SqlStatement createObject(Object... params) throws Exception {
            List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
            parameterInfoList
                    .add(new SqlParameter(sqlDataTypePolicies.get(sqlEntityInfo.getIdFieldInfo().getColumnType())));
            parameterInfoList.add(
                    new SqlParameter(sqlDataTypePolicies.get(sqlEntityInfo.getVersionFieldInfo().getColumnType())));

            return new SqlStatement(sqlEntityInfo, SqlStatementType.LIST_BY_PK_VERSION,
                    sqlCache.getListByPkVersionSql(), parameterInfoList,
                    getSqlResultList(sqlEntityInfo.getListFieldInfos()));
        }

        @Override
        protected void onGetObject(SqlStatement sqlStatement, Object... params) throws Exception {
            sqlStatement.getParameterInfoList().get(0).setValue(params[0]);
            sqlStatement.getParameterInfoList().get(1).setValue(params[1]);
        }
    }

    private class UpdateByPkSqlStatementInfoPool extends SqlStatementPool {

        public UpdateByPkSqlStatementInfoPool() {
            super(getTimeout, minObjects, maxObjects);
        }

        @Override
        protected SqlStatement createObject(Object... params) throws Exception {
            List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
            for (SqlFieldInfo sqlFieldInfo : sqlEntityInfo.getFieldInfos()) {
                if (!sqlFieldInfo.isPrimaryKey()) {
                    parameterInfoList.add(new SqlParameter(sqlDataTypePolicies.get(sqlFieldInfo.getColumnType())));
                }
            }

            parameterInfoList
                    .add(new SqlParameter(sqlDataTypePolicies.get(sqlEntityInfo.getIdFieldInfo().getColumnType())));
            return new SqlStatement(sqlEntityInfo, SqlStatementType.UPDATE_BY_PK, sqlCache.getUpdateByPkSql(),
                    parameterInfoList);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onGetObject(SqlStatement sqlStatement, Object... params) throws Exception {
            List<SqlParameter> parameterInfoList = sqlStatement.getParameterInfoList();
            int i = 0;
            for (SqlFieldInfo sqlFieldInfo : sqlEntityInfo.getFieldInfos()) {
                if (!sqlFieldInfo.isPrimaryKey()) {
                    if (sqlFieldInfo.isTransformed()) {
                        parameterInfoList.get(i++)
                                .setValue(((Transformer<Object, Object>) sqlFieldInfo.getTransformer())
                                        .forwardTransform(sqlFieldInfo.getGetter().invoke(params[0])));
                    } else {
                        parameterInfoList.get(i++).setValue(sqlFieldInfo.getGetter().invoke(params[0]));
                    }
                }
            }
            parameterInfoList.get(i).setValue(sqlEntityInfo.getIdFieldInfo().getGetter().invoke(params[0]));
        }
    }

    private class UpdateByPkVersionSqlStatementInfoPool extends SqlStatementPool {

        public UpdateByPkVersionSqlStatementInfoPool() {
            super(getTimeout, minObjects, maxObjects);
        }

        @Override
        protected SqlStatement createObject(Object... params) throws Exception {
            List<SqlParameter> parameterInfoList = new ArrayList<SqlParameter>();
            for (SqlFieldInfo sqlFieldInfo : sqlEntityInfo.getFieldInfos()) {
                if (!sqlFieldInfo.isPrimaryKey()) {
                    parameterInfoList.add(new SqlParameter(sqlDataTypePolicies.get(sqlFieldInfo.getColumnType())));
                }
            }

            parameterInfoList
                    .add(new SqlParameter(sqlDataTypePolicies.get(sqlEntityInfo.getIdFieldInfo().getColumnType())));
            parameterInfoList.add(
                    new SqlParameter(sqlDataTypePolicies.get(sqlEntityInfo.getVersionFieldInfo().getColumnType())));
            return new SqlStatement(sqlEntityInfo, SqlStatementType.UPDATE_BY_PK_VERSION,
                    sqlCache.getUpdateByPkVersionSql(), parameterInfoList);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onGetObject(SqlStatement sqlStatement, Object... params) throws Exception {
            List<SqlParameter> parameterInfoList = sqlStatement.getParameterInfoList();
            int i = 0;
            for (SqlFieldInfo sqlFieldInfo : sqlEntityInfo.getFieldInfos()) {
                if (!sqlFieldInfo.isPrimaryKey()) {
                    if (sqlFieldInfo.isTransformed()) {
                        parameterInfoList.get(i++)
                                .setValue(((Transformer<Object, Object>) sqlFieldInfo.getTransformer())
                                        .forwardTransform(sqlFieldInfo.getGetter().invoke(params[0])));
                    } else {
                        parameterInfoList.get(i++).setValue(sqlFieldInfo.getGetter().invoke(params[0]));
                    }
                }
            }
            parameterInfoList.get(i++).setValue(sqlEntityInfo.getIdFieldInfo().getGetter().invoke(params[0]));
            parameterInfoList.get(i).setValue(params[1]);
        }
    }
}
