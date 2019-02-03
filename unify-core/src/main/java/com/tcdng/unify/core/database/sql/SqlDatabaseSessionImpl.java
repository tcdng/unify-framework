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

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.data.Aggregate;
import com.tcdng.unify.core.data.AggregateType;
import com.tcdng.unify.core.database.DatabaseSession;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.EntityPolicy;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.operation.Amongst;
import com.tcdng.unify.core.operation.Select;
import com.tcdng.unify.core.operation.Update;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Implementation of an SQL database session.
 * 
 * <pre>
 * The find/update/delete/count by criteria problem:
 * 1. Entity A has properties a1,a2,a3
 * 2. a3 is a listOnly field
 * 3. Mapped table T has columns for only (true) properties a1 and a2 (since a3 is listOnly and is mapped to a view column)
 * 4. Criteria Q has conditions with all fields a1,a2 and a3
 * 5. Attempt to update and delete by criteria Q fails because table T has no a3 column
 * 
 * Implemented solution:
 * 1. Check if criteria parameter Q1 contains only true properties
 * 2. If true, execute update/delete with criteria Q1 and return
 * 3. Else fetch all IDs I[] for entities that match criteria Q1
 * 4. Create new criteria Q2 as record id amongst I[]
 * 5. Execute update/delete with criteria Q2 and return result
 * 
 * {@link #findAll(Query)}
 * {@link #updateAll(Query, Update)}
 * {@link #deleteAll(Query)}
 * {@link #count(Query)}
 * </pre>
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class SqlDatabaseSessionImpl implements DatabaseSession {

    private SqlDataSource sqlDataSource;
    private SqlDataSourceDialect sqlDataSourceDialect;
    private SqlStatementExecutor sqlStatementExecutor;
    private Connection connection;
    private Stack<Savepoint> savepointStack;
    private boolean closed;

    public SqlDatabaseSessionImpl(SqlDataSource sqlDataSource, SqlStatementExecutor sqlStatementExecutor)
            throws UnifyException {
        this.sqlDataSource = sqlDataSource;
        this.sqlStatementExecutor = sqlStatementExecutor;
        sqlDataSourceDialect = (SqlDataSourceDialect) sqlDataSource.getDialect();
        connection = (Connection) sqlDataSource.getConnection();
        savepointStack = new Stack<Savepoint>();
    }

    @Override
    public String getDataSourceName() {
        return sqlDataSource.getName();
    }

    @Override
    public Object create(Entity record) throws UnifyException {
        SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(SqlUtils.getEntityClass(record));
        EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
        Object id = null;
        if (entityPolicy != null) {
            if (entityPolicy.isSetNow()) {
                id = entityPolicy.preCreate(record, getNow());
            } else {
                id = entityPolicy.preCreate(record, null);
            }
        }
        SqlStatement sqlStatement = sqlDataSourceDialect.prepareCreateStatement(record);
        try {
            getSqlStatementExecutor().executeUpdate(connection, sqlStatement);

            if (sqlEntityInfo.isChildList()) {
                createChildRecords(sqlEntityInfo, record, id);
            }
        } catch (UnifyException e) {
            throw e;
        } finally {
            sqlDataSourceDialect.restoreStatement(sqlStatement);
        }
        return id;
    }

    @Override
    public <T extends Entity> T find(Class<T> clazz, Object id) throws UnifyException {
        return find(clazz, id, true);
    }

    @Override
    public <T extends Entity> T find(Class<T> clazz, Object id, final Object versionNo) throws UnifyException {
        return find(clazz, id, versionNo, true);
    }

    @Override
    public <T extends Entity> T find(Query<T> query) throws UnifyException {
        return find(query, true);
    }

    @Override
    public <T extends Entity> T findLean(Class<T> clazz, Object id) throws UnifyException {
        return find(clazz, id, false);
    }

    @Override
    public <T extends Entity> T findLean(Class<T> clazz, Object id, final Object versionNo) throws UnifyException {
        return find(clazz, id, versionNo, false);
    }

    @Override
    public <T extends Entity> T findLean(Query<T> query) throws UnifyException {
        return find(query, false);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T extends Entity> T findConstraint(T record) throws UnifyException {
        SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(record.getClass());
        if (sqlEntityInfo.isUniqueConstraints()) {
            Query<T> query = new Query(record.getClass());
            for (SqlUniqueConstraintInfo suci : sqlEntityInfo.getUniqueConstraintList().values()) {
                query.clear();
                for (String fieldName : suci.getFieldNameList()) {
                    query.equals(fieldName, ReflectUtils.getBeanProperty(record, fieldName));
                }
                T constrainingRecord = find(query);
                if (constrainingRecord != null) {
                    return constrainingRecord;
                }
            }

        }
        return null;
    }

    @Override
    public <T extends Entity> List<T> findAll(Query<T> query) throws UnifyException {
        try {
            SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(query.getEntityClass());
            if (sqlEntityInfo.testTrueFieldNamesOnly(query.getFields())) {
                return getSqlStatementExecutor().executeMultipleRecordResultQuery(connection,
                        sqlDataSourceDialect.prepareFindStatement(query));
            }

            SqlFieldInfo idFieldInfo = sqlEntityInfo.getIdFieldInfo();
            List<?> idList = valueList(idFieldInfo.getFieldType(), idFieldInfo.getName(), query);
            if (!idList.isEmpty()) {
                Query<T> findQuery = query.copyNoCriteria();
                findQuery.add(new Amongst(idFieldInfo.getName(), idList));
                return getSqlStatementExecutor().executeMultipleRecordResultQuery(connection,
                        sqlDataSourceDialect.prepareFindStatement(findQuery));
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, getClass().getSimpleName());
        }
        return new ArrayList<T>();
    }

    @Override
    public <T, U extends Entity> Map<T, U> findAllMap(Class<T> keyClass, String keyName, Query<U> query)
            throws UnifyException {
        try {
            SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(query.getEntityClass());
            if (sqlEntityInfo.testTrueFieldNamesOnly(query.getFields())) {
                return getSqlStatementExecutor().executeMultipleRecordResultQuery(connection, keyClass, keyName,
                        sqlDataSourceDialect.prepareFindStatement(query));
            }

            SqlFieldInfo idFieldInfo = sqlEntityInfo.getIdFieldInfo();
            List<?> idList = valueList(idFieldInfo.getFieldType(), idFieldInfo.getName(), query);
            if (!idList.isEmpty()) {
                Query<U> findQuery = query.copyNoCriteria();
                findQuery.add(new Amongst(idFieldInfo.getName(), idList));
                return getSqlStatementExecutor().executeMultipleRecordResultQuery(connection, keyClass, keyName,
                        sqlDataSourceDialect.prepareFindStatement(findQuery));
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, getClass().getSimpleName());
        }
        return new HashMap<T, U>();
    }

    @Override
    public <T, U extends Entity> Map<T, List<U>> findAllListMap(Class<T> keyClass, String keyName, Query<U> query)
            throws UnifyException {
        try {
            SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(query.getEntityClass());
            if (sqlEntityInfo.testTrueFieldNamesOnly(query.getFields())) {
                return getSqlStatementExecutor().executeMultipleRecordListResultQuery(connection, keyClass, keyName,
                        sqlDataSourceDialect.prepareFindStatement(query));
            }

            SqlFieldInfo idFieldInfo = sqlEntityInfo.getIdFieldInfo();
            List<?> idList = valueList(idFieldInfo.getFieldType(), idFieldInfo.getName(), query);
            if (!idList.isEmpty()) {
                Query<U> findQuery = query.copyNoCriteria();
                findQuery.add(new Amongst(idFieldInfo.getName(), idList));
                return getSqlStatementExecutor().executeMultipleRecordListResultQuery(connection, keyClass, keyName,
                        sqlDataSourceDialect.prepareFindStatement(findQuery));
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, getClass().getSimpleName());
        }
        return new HashMap<T, List<U>>();
    }

    @Override
    public <T extends Entity> T list(Class<T> clazz, Object id) throws UnifyException {
        return list(clazz, id, true);
    }

    @Override
    public <T extends Entity> T list(Class<T> clazz, Object id, final Object versionNo) throws UnifyException {
        return list(clazz, id, versionNo, true);
    }

    @Override
    public <T extends Entity> T list(Query<T> query) throws UnifyException {
        return list(query, true);
    }

    @Override
    public <T extends Entity> T listLean(Class<T> clazz, Object id) throws UnifyException {
        return list(clazz, id, false);
    }

    @Override
    public <T extends Entity> T listLean(Class<T> clazz, Object id, final Object versionNo) throws UnifyException {
        return list(clazz, id, versionNo, false);
    }

    @Override
    public <T extends Entity> T listLean(Query<T> query) throws UnifyException {
        return list(query, false);
    }

    @Override
    public <T extends Entity> List<T> listAll(Query<T> query) throws UnifyException {
        return getSqlStatementExecutor().executeMultipleRecordResultQuery(connection,
                sqlDataSourceDialect.prepareListStatement(query));
    }

    @Override
    public <T, U extends Entity> Map<T, U> listAll(Class<T> keyClass, String keyName, Query<U> query)
            throws UnifyException {
        return getSqlStatementExecutor().executeMultipleRecordResultQuery(connection, keyClass, keyName,
                sqlDataSourceDialect.prepareListStatement(query));
    }

    @Override
    public <T, U extends Entity> Map<T, List<U>> listAllListMap(Class<T> keyClass, String keyName, Query<U> query)
            throws UnifyException {
        return getSqlStatementExecutor().executeMultipleRecordListResultQuery(connection, keyClass, keyName,
                sqlDataSourceDialect.prepareListStatement(query));
    }

    @Override
    public <T, U extends Entity> List<T> valueList(Class<T> fieldClass, String fieldName, final Query<U> query)
            throws UnifyException {
        SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(query.getEntityClass());
        Select select = query.getSelect();
        try {
            query.setSelect(new Select(fieldName));
            return getSqlStatementExecutor().executeMultipleObjectListResultQuery(connection, fieldClass,
                    sqlDataSourceDialect.getSqlTypePolicy(sqlEntityInfo.getListFieldInfo(fieldName).getColumnType()),
                    sqlDataSourceDialect.prepareListStatement(query));
        } finally {
            query.setSelect(select);
        }
    }

    @Override
    public <T, U extends Entity> Set<T> valueSet(Class<T> fieldClass, String fieldName, Query<U> query)
            throws UnifyException {
        SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(query.getEntityClass());
        Select select = query.getSelect();
        try {
            query.setSelect(new Select(fieldName));
            return getSqlStatementExecutor().executeMultipleObjectSetResultQuery(connection, fieldClass,
                    sqlDataSourceDialect.getSqlTypePolicy(sqlEntityInfo.getListFieldInfo(fieldName).getColumnType()),
                    sqlDataSourceDialect.prepareListStatement(query));
        } finally {
            query.setSelect(select);
        }
    }

    @Override
    public <T, U, V extends Entity> Map<T, U> valueMap(Class<T> keyClass, String keyName, Class<U> valueClass,
            String valueName, Query<V> query) throws UnifyException {
        Select select = query.getSelect();
        try {
            query.setSelect(new Select(keyName, valueName));
            return getSqlStatementExecutor().executeMultipleObjectMapResultQuery(connection, keyClass, keyName,
                    valueClass, valueName, sqlDataSourceDialect.prepareListStatement(query));
        } finally {
            query.setSelect(select);
        }
    }

    @Override
    public <T, U, V extends Entity> Map<T, List<U>> valueListMap(Class<T> keyClass, String keyName, Class<U> valueClass,
            String valueName, Query<V> query) throws UnifyException {
        Select select = query.getSelect();
        try {
            query.setSelect(new Select(keyName, valueName));
            return getSqlStatementExecutor().executeMultipleObjectListMapResultQuery(connection, keyClass, keyName,
                    valueClass, valueName, sqlDataSourceDialect.prepareListStatement(query));
        } finally {
            query.setSelect(select);
        }
    }

    @Override
    public <T, U extends Entity> T value(Class<T> fieldClass, String fieldName, Query<U> query) throws UnifyException {
        SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(query.getEntityClass());
        Select select = query.getSelect();
        try {
            query.setSelect(new Select(fieldName));
            return getSqlStatementExecutor().executeSingleObjectResultQuery(connection, fieldClass,
                    sqlDataSourceDialect.getSqlTypePolicy(sqlEntityInfo.getListFieldInfo(fieldName).getColumnType()),
                    sqlDataSourceDialect.prepareListStatement(query), query.isMustMatch());
        } finally {
            query.setSelect(select);
        }
    }

    @Override
    public void populateListOnly(Entity record) throws UnifyException {
        SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(record.getClass());
        try {
            Map<String, Object> fkItemMap = new HashMap<String, Object>();
            for (SqlForeignKeyInfo sqlForeignKeyInfo : sqlEntityInfo.getForeignKeyList()) {
                SqlFieldInfo fkSqlFieldInfo = sqlForeignKeyInfo.getSqlFieldInfo();
                if (!fkSqlFieldInfo.isIgnoreFkConstraint()) {
                    Object fkId = fkSqlFieldInfo.getGetter().invoke(record);
                    if (fkId != null) {
                        SqlEntityInfo fkSqlEntityInfo = fkSqlFieldInfo.getForeignEntityInfo();
                        Object fkRecord = null;
                        SqlStatement sqlStatement =
                                sqlDataSourceDialect.prepareListByPkStatement(fkSqlEntityInfo.getKeyClass(), fkId);
                        try {
                            fkRecord = getSqlStatementExecutor().executeSingleRecordResultQuery(connection,
                                    sqlStatement, true);
                            if (fkRecord == null) {
                                throw new UnifyException(UnifyCoreErrorConstants.RECORD_WITH_PK_NOT_FOUND,
                                        fkSqlEntityInfo.getKeyClass(), fkId);
                            }
                        } finally {
                            sqlDataSourceDialect.restoreStatement(sqlStatement);
                        }

                        fkItemMap.put(fkSqlFieldInfo.getName(), fkRecord);
                    }
                }
            }

            if (!fkItemMap.isEmpty()) {
                for (SqlFieldInfo sqlFieldInfo : sqlEntityInfo.getListFieldInfos()) {
                    if (sqlFieldInfo.isListOnly()) {
                        Object fkRecord = fkItemMap.get(sqlFieldInfo.getForeignKeyFieldInfo().getName());
                        if (fkRecord != null) {
                            Object val = sqlFieldInfo.getForeignFieldInfo().getGetter().invoke(fkRecord);
                            sqlFieldInfo.getSetter().invoke(record, val);
                        }
                    }
                }
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, e);
        }
    }

    @Override
    public int updateById(Entity record) throws UnifyException {
        int result;
        SqlStatement sqlStatement = null;
        SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(record.getClass());
        EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
        try {
            if (entityPolicy != null) {
                if (entityPolicy.isSetNow()) {
                    entityPolicy.preUpdate(record, getNow());
                } else {
                    entityPolicy.preUpdate(record, null);
                }
            }

            sqlStatement = sqlDataSourceDialect.prepareUpdateByPkStatement(record);
            result = getSqlStatementExecutor().executeUpdate(connection, sqlStatement);
            if (result == 0) {
                throw new UnifyException(UnifyCoreErrorConstants.RECORD_WITH_PK_NOT_FOUND, record.getClass(),
                        record.getId());
            }

            if (sqlEntityInfo.isChildList()) {
                updateChildRecords(sqlEntityInfo, record);
            }
        } catch (UnifyException e) {
            if (entityPolicy != null) {
                entityPolicy.onUpdateError(record);
            }
            throw e;
        } finally {
            if (sqlStatement != null) {
                sqlDataSourceDialect.restoreStatement(sqlStatement);
            }
        }
        return result;
    }

    @Override
    public int updateByIdVersion(Entity record) throws UnifyException {
        int result;
        SqlStatement sqlStatement = null;
        SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(record.getClass());
        EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
        try {
            Object oldVersionNo = null;
            if (sqlEntityInfo.isVersioned()) {
                oldVersionNo = sqlEntityInfo.getVersionFieldInfo().getGetter().invoke(record);
                if (entityPolicy != null) {
                    if (entityPolicy.isSetNow()) {
                        entityPolicy.preUpdate(record, getNow());
                    } else {
                        entityPolicy.preUpdate(record, null);
                    }
                }
                sqlStatement = sqlDataSourceDialect.prepareUpdateByPkVersionStatement(record, oldVersionNo);
            } else {
                if (entityPolicy != null) {
                    if (entityPolicy.isSetNow()) {
                        entityPolicy.preUpdate(record, getNow());
                    } else {
                        entityPolicy.preUpdate(record, null);
                    }
                }
                sqlStatement = sqlDataSourceDialect.prepareUpdateByPkStatement(record);
            }

            result = getSqlStatementExecutor().executeUpdate(connection, sqlStatement);
            if (result == 0) {
                throw new UnifyException(UnifyCoreErrorConstants.RECORD_WITH_PK_VERSION_NOT_FOUND, record.getClass(),
                        sqlEntityInfo.getIdFieldInfo().getGetter().invoke(record), oldVersionNo);
            }

            if (sqlEntityInfo.isChildList()) {
                updateChildRecords(sqlEntityInfo, record);
            }
        } catch (Exception e) {
            if (entityPolicy != null) {
                entityPolicy.onUpdateError(record);
            }

            if (e instanceof UnifyException) {
                throw ((UnifyException) e);
            }
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, getClass().getSimpleName());
        } finally {
            if (sqlStatement != null) {
                sqlDataSourceDialect.restoreStatement(sqlStatement);
            }
        }
        return result;
    }

    @Override
    public int updateById(Class<? extends Entity> clazz, Object id, Update update) throws UnifyException {
        return getSqlStatementExecutor().executeUpdate(connection,
                sqlDataSourceDialect.prepareUpdateStatement(clazz, id, update));
    }

    @Override
    public int updateAll(Query<? extends Entity> query, Update update) throws UnifyException {
        try {
            SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(query.getEntityClass());
            if (sqlDataSourceDialect.isQueryOffsetOrLimit(query)
                    || (sqlEntityInfo.testTrueFieldNamesOnly(query.getFields()))) {
                return getSqlStatementExecutor().executeUpdate(connection,
                        sqlDataSourceDialect.prepareUpdateStatement(query, update));
            }

            SqlFieldInfo idFieldInfo = sqlEntityInfo.getIdFieldInfo();
            List<?> idList = valueList(idFieldInfo.getFieldType(), idFieldInfo.getName(), query);
            if (!idList.isEmpty()) {
                Query<? extends Entity> updateQuery = query.copyNoAll();
                updateQuery.add(new Amongst(idFieldInfo.getName(), idList));
                return getSqlStatementExecutor().executeUpdate(connection,
                        sqlDataSourceDialect.prepareUpdateStatement(updateQuery, update));
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, getClass().getSimpleName());
        }
        return 0;
    }

    @Override
    public int deleteById(Entity record) throws UnifyException {
        SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(record.getClass());
        SqlStatement sqlStatement = sqlDataSourceDialect.prepareDeleteByPkStatement(record.getClass(), record.getId());
        EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
        int result;
        try {
            if (entityPolicy != null) {
                if (entityPolicy.isSetNow()) {
                    entityPolicy.preDelete(record, getNow());
                } else {
                    entityPolicy.preDelete(record, null);
                }
            }

            if (sqlEntityInfo.isOnDeleteCascadeList()) {
                deleteChildRecords(sqlEntityInfo, record.getId());
            }

            result = getSqlStatementExecutor().executeUpdate(connection, sqlStatement);
            if (result == 0) {
                throw new UnifyException(UnifyCoreErrorConstants.RECORD_WITH_PK_NOT_FOUND, record.getClass(),
                        record.getId());
            }
        } catch (Exception e) {
            if (entityPolicy != null) {
                entityPolicy.onDeleteError(record);
            }

            if (e instanceof UnifyException) {
                throw ((UnifyException) e);
            }

            throw new UnifyException(e, UnifyCoreErrorConstants.DATASOURCE_SESSION_ERROR, getDataSourceName());
        } finally {
            sqlDataSourceDialect.restoreStatement(sqlStatement);
        }
        return result;
    }

    @Override
    public int deleteByIdVersion(Entity record) throws UnifyException {
        SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(record.getClass());
        EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
        SqlStatement sqlStatement = null;
        int result;
        try {
            Object oldVersionNo = null;
            if (sqlEntityInfo.isVersioned()) {
                oldVersionNo = sqlEntityInfo.getVersionFieldInfo().getGetter().invoke(record);
                if (entityPolicy != null) {
                    if (entityPolicy.isSetNow()) {
                        entityPolicy.preDelete(record, getNow());
                    } else {
                        entityPolicy.preDelete(record, null);
                    }
                }

                sqlStatement = sqlDataSourceDialect.prepareDeleteByPkVersionStatement(record.getClass(), record.getId(),
                        oldVersionNo);
            } else {
                if (entityPolicy != null) {
                    if (entityPolicy.isSetNow()) {
                        entityPolicy.preDelete(record, getNow());
                    } else {
                        entityPolicy.preDelete(record, null);
                    }
                }

                sqlStatement = sqlDataSourceDialect.prepareDeleteByPkStatement(record.getClass(), record.getId());
            }

            if (sqlEntityInfo.isOnDeleteCascadeList()) {
                deleteChildRecords(sqlEntityInfo, record.getId());
            }

            result = getSqlStatementExecutor().executeUpdate(connection, sqlStatement);
            if (result == 0) {
                throw new UnifyException(UnifyCoreErrorConstants.RECORD_WITH_PK_VERSION_NOT_FOUND, record.getClass(),
                        record.getId(), oldVersionNo);
            }
        } catch (Exception e) {
            if (entityPolicy != null) {
                entityPolicy.onDeleteError(record);
            }

            if (e instanceof UnifyException) {
                throw ((UnifyException) e);
            }

            throw new UnifyException(e, UnifyCoreErrorConstants.DATASOURCE_SESSION_ERROR, getDataSourceName());
        } finally {
            sqlDataSourceDialect.restoreStatement(sqlStatement);
        }
        return result;
    }

    @Override
    public int delete(Class<? extends Entity> clazz, final Object id) throws UnifyException {
        SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(clazz);
        SqlStatement sqlStatement = sqlDataSourceDialect.prepareDeleteByPkStatement(clazz, id);
        int result;
        try {
            if (sqlEntityInfo.isOnDeleteCascadeList()) {
                deleteChildRecords(sqlEntityInfo, id);
            }

            result = getSqlStatementExecutor().executeUpdate(connection, sqlStatement);
            if (result == 0) {
                throw new UnifyException(UnifyCoreErrorConstants.RECORD_WITH_PK_NOT_FOUND, clazz, id);
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.DATASOURCE_SESSION_ERROR, getDataSourceName());
        } finally {
            sqlDataSourceDialect.restoreStatement(sqlStatement);
        }
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public int deleteAll(Query<? extends Entity> query) throws UnifyException {
        try {
            SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(query.getEntityClass());
            if (sqlDataSourceDialect.isQueryOffsetOrLimit(query)
                    || (!sqlEntityInfo.isChildList() && sqlEntityInfo.testTrueFieldNamesOnly(query.getFields()))) {
                return getSqlStatementExecutor().executeUpdate(connection,
                        sqlDataSourceDialect.prepareDeleteStatement(query));
            }

            SqlFieldInfo idFieldInfo = sqlEntityInfo.getIdFieldInfo();
            List<?> idList = valueList(idFieldInfo.getFieldType(), idFieldInfo.getName(), query);
            if (!idList.isEmpty()) {
                if (sqlEntityInfo.isOnDeleteCascadeList()) {
                    for (OnDeleteCascadeInfo odci : sqlEntityInfo.getOnDeleteCascadeInfoList()) {
                        Query<? extends Entity> attrQuery = new Query(odci.getChildEntityClass());
                        attrQuery.amongst(odci.getChildFkField().getName(), idList);
                        deleteAll(attrQuery);
                    }
                }

                Query<? extends Entity> deleteQuery = query.copyNoAll();
                deleteQuery.add(new Amongst(idFieldInfo.getName(), idList));
                return getSqlStatementExecutor().executeUpdate(connection,
                        sqlDataSourceDialect.prepareDeleteStatement(deleteQuery));
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, getClass().getSimpleName());
        }
        return 0;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public int count(Query<? extends Entity> query) throws UnifyException {
        try {
            SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(query.getEntityClass());
            if (sqlEntityInfo.testTrueFieldNamesOnly(query.getFields())) {
                return getSqlStatementExecutor().executeSingleObjectResultQuery(connection, int.class,
                        sqlDataSourceDialect.getSqlTypePolicy(int.class),
                        sqlDataSourceDialect.prepareCountStatement(query), true);
            }

            SqlFieldInfo idFieldInfo = sqlEntityInfo.getIdFieldInfo();
            List<?> idList = valueList(idFieldInfo.getFieldType(), idFieldInfo.getName(), query);
            if (!idList.isEmpty()) {
                Query<? extends Entity> countQuery = new Query(query.getEntityClass(), query.isApplyAppQueryLimit());
                countQuery.add(new Amongst(idFieldInfo.getName(), idList));
                return getSqlStatementExecutor().executeSingleObjectResultQuery(connection, int.class,
                        sqlDataSourceDialect.getSqlTypePolicy(int.class),
                        sqlDataSourceDialect.prepareCountStatement(countQuery), true);
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, getClass().getSimpleName());
        }
        return 0;
    }

    @Override
    public List<Aggregate<?>> aggregate(AggregateType aggregateType, Query<? extends Entity> query)
            throws UnifyException {
        try {
            SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(query.getEntityClass());
            if (sqlEntityInfo.testTrueFieldNamesOnly(query.getFields())) {
                return getSqlStatementExecutor().executeMultipleAggregateResultQuery(connection,
                        sqlDataSourceDialect.getSqlTypePolicy(int.class),
                        sqlDataSourceDialect.prepareAggregateStatement(aggregateType, query));
            }

            SqlFieldInfo idFieldInfo = sqlEntityInfo.getIdFieldInfo();
            List<?> idList = valueList(idFieldInfo.getFieldType(), idFieldInfo.getName(), query);
            if (!idList.isEmpty()) {
                Query<? extends Entity> aggregateQuery = query.copyNoCriteria();
                aggregateQuery.add(new Amongst(idFieldInfo.getName(), idList));
                return getSqlStatementExecutor().executeMultipleAggregateResultQuery(connection,
                        sqlDataSourceDialect.getSqlTypePolicy(int.class),
                        sqlDataSourceDialect.prepareAggregateStatement(aggregateType, aggregateQuery));
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, getClass().getSimpleName());
        }
        return Collections.emptyList();
    }

    @Override
    public Date getNow() throws UnifyException {
        return getSqlStatementExecutor().executeSingleObjectResultQuery(connection, Date.class,
                sqlDataSourceDialect.getSqlTypePolicy(ColumnType.TIMESTAMP), sqlDataSourceDialect.generateNowSql(),
                true);
    }

    @Override
    public void clearSavepoint() throws UnifyException {
        try {
            if (!savepointStack.isEmpty()) {
                Savepoint savepoint = savepointStack.pop();
                connection.releaseSavepoint(savepoint);
            }
        } catch (SQLException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.DATASOURCE_SESSION_ERROR, getDataSourceName());
        }

    }

    @Override
    public void close() throws UnifyException {
        try {
            while (!savepointStack.empty()) {
                connection.releaseSavepoint(savepointStack.pop());
            }
        } catch (SQLException e) {
        }
        sqlDataSource.restoreConnection(connection);
        connection = null;
        closed = true;
    }

    @Override
    public void commit() throws UnifyException {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.DATASOURCE_SESSION_ERROR, getDataSourceName());
        }
    }

    @Override
    public void rollback() throws UnifyException {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.DATASOURCE_SESSION_ERROR, getDataSourceName());
        }
    }

    @Override
    public void rollbackToSavepoint() throws UnifyException {
        try {
            if (!savepointStack.isEmpty()) {
                Savepoint savepoint = savepointStack.peek();
                connection.rollback(savepoint);
            }
        } catch (SQLException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.DATASOURCE_SESSION_ERROR, getDataSourceName());
        }
    }

    @Override
    public void setSavepoint() throws UnifyException {
        try {
            Savepoint savepoint = connection.setSavepoint();
            savepointStack.push(savepoint);
        } catch (SQLException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.DATASOURCE_SESSION_ERROR, getDataSourceName());
        }
    }

    @Override
    public boolean isClosed() throws UnifyException {
        return closed;
    }

    private <T extends Entity> T find(Class<T> clazz, Object id, boolean fetchChild) throws UnifyException {
        SqlStatement sqlStatement = sqlDataSourceDialect.prepareFindByPkStatement(clazz, id);
        try {
            T record = getSqlStatementExecutor().executeSingleRecordResultQuery(connection, sqlStatement, true);
            if (record == null) {
                throw new UnifyException(UnifyCoreErrorConstants.RECORD_WITH_PK_NOT_FOUND, clazz, id);
            }

            if (fetchChild) {
                fetchChildRecords(record, null, false);
            }
            return record;
        } finally {
            sqlDataSourceDialect.restoreStatement(sqlStatement);
        }
    }

    private <T extends Entity> T find(Class<T> clazz, Object id, final Object versionNo, boolean fetchChild)
            throws UnifyException {
        SqlStatement sqlStatement = sqlDataSourceDialect.prepareFindByPkVersionStatement(clazz, id, versionNo);
        try {
            T record = getSqlStatementExecutor().executeSingleRecordResultQuery(connection, sqlStatement, true);
            if (record == null) {
                throw new UnifyException(UnifyCoreErrorConstants.RECORD_WITH_PK_VERSION_NOT_FOUND, clazz, id,
                        versionNo);
            }

            if (fetchChild) {
                fetchChildRecords(record, null, false);
            }
            return record;
        } finally {
            sqlDataSourceDialect.restoreStatement(sqlStatement);
        }
    }

    private <T extends Entity> T find(Query<T> query, boolean fetchChild) throws UnifyException {
        T record = null;
        try {
            SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(SqlUtils.getEntityClass(query));
            if (sqlEntityInfo.testTrueFieldNamesOnly(query.getFields())) {
                record = getSqlStatementExecutor().executeSingleRecordResultQuery(connection,
                        sqlDataSourceDialect.prepareFindStatement(query), false);
            } else {
                SqlFieldInfo idFieldInfo = sqlEntityInfo.getIdFieldInfo();
                List<?> idList = valueList(idFieldInfo.getFieldType(), idFieldInfo.getName(), query);
                if (!idList.isEmpty()) {
                    Query<T> findQuery = query.copyNoCriteria();
                    findQuery.add(new Amongst(idFieldInfo.getName(), idList));
                    record = getSqlStatementExecutor().executeSingleRecordResultQuery(connection,
                            sqlDataSourceDialect.prepareFindStatement(findQuery), false);
                }
            }

            if (fetchChild) {
                fetchChildRecords(record, query.getSelect(), false);
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, getClass().getSimpleName());
        }
        return record;
    }

    private <T extends Entity> T list(Class<T> clazz, Object id, boolean fetchChild) throws UnifyException {
        SqlStatement sqlStatement = sqlDataSourceDialect.prepareListByPkStatement(clazz, id);
        try {
            T record = getSqlStatementExecutor().executeSingleRecordResultQuery(connection, sqlStatement, true);
            if (record == null) {
                throw new UnifyException(UnifyCoreErrorConstants.RECORD_WITH_PK_NOT_FOUND, clazz, id);
            }

            if (fetchChild) {
                fetchChildRecords(record, null, true);
            }
            return record;
        } finally {
            sqlDataSourceDialect.restoreStatement(sqlStatement);
        }
    }

    private <T extends Entity> T list(Class<T> clazz, Object id, final Object versionNo, boolean fetchChild) throws UnifyException {
        SqlStatement sqlStatement = sqlDataSourceDialect.prepareListByPkVersionStatement(clazz, id, versionNo);
        try {
            T record = getSqlStatementExecutor().executeSingleRecordResultQuery(connection, sqlStatement, true);
            if (record == null) {
                throw new UnifyException(UnifyCoreErrorConstants.RECORD_WITH_PK_VERSION_NOT_FOUND, clazz, id,
                        versionNo);
            }

            if (fetchChild) {
                fetchChildRecords(record, null, true);
            }
            return record;
        } finally {
            sqlDataSourceDialect.restoreStatement(sqlStatement);
        }
    }

    private <T extends Entity> T list(Query<T> query, boolean fetchChild) throws UnifyException {
        T record = getSqlStatementExecutor().executeSingleRecordResultQuery(connection,
                sqlDataSourceDialect.prepareListStatement(query), false);
        if (fetchChild) {
            fetchChildRecords(record, query.getSelect(), true);
        }
        return record;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <T extends Entity> void fetchChildRecords(T record, Select select, boolean isListOnly)
            throws UnifyException {
        if (record != null) {
            try {
                SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(SqlUtils.getEntityClass(record));
                if (sqlEntityInfo.isChildList()) {
                    boolean isSelect = select != null && !select.isEmpty();
                    Object id = record.getId();

                    if (sqlEntityInfo.isSingleChildList()) {
                        for (ChildFieldInfo clfi : sqlEntityInfo.getSingleChildInfoList()) {
                            if (isSelect && !select.contains(clfi.getName())) {
                                continue;
                            }

                            Query<? extends Entity> query = new Query(clfi.getChildEntityClass());
                            query.equals(clfi.getChildFkField().getName(), id).order("id");
                            List<? extends Entity> childList = null;
                            if (isListOnly) {
                                childList = listAll(query);
                            } else {
                                childList = findAll(query);
                            }

                            // Check if child has child list and load if necessary
                            Entity childRecord = null;
                            if (!childList.isEmpty()) {
                                if (childList.size() > 1) {
                                    throw new UnifyException(UnifyCoreErrorConstants.RECORD_MULTIPLE_CHILD_FOUND,
                                            record.getClass(), record.getId(), clfi.getField().getName());
                                }

                                childRecord = childList.get(0);
                                SqlEntityInfo childSqlEntityInfo =
                                        sqlDataSourceDialect.getSqlEntityInfo(clfi.getChildEntityClass());
                                if (childSqlEntityInfo.isChildList()) {
                                    fetchChildRecords(childRecord, null, isListOnly);
                                }
                            }

                            // Set child record
                            clfi.getSetter().invoke(record, childRecord);
                        }
                    }

                    if (sqlEntityInfo.isManyChildList()) {
                        for (ChildFieldInfo clfi : sqlEntityInfo.getManyChildInfoList()) {
                            if (isSelect && !select.contains(clfi.getName())) {
                                continue;
                            }

                            Query<? extends Entity> query = new Query(clfi.getChildEntityClass());
                            query.equals(clfi.getChildFkField().getName(), id).order("id");
                            List<? extends Entity> childList = null;
                            if (isListOnly) {
                                childList = listAll(query);
                            } else {
                                childList = findAll(query);
                            }

                            // Check if child has child list and load if necessary
                            if (!childList.isEmpty()) {
                                SqlEntityInfo childSqlEntityInfo =
                                        sqlDataSourceDialect.getSqlEntityInfo(clfi.getChildEntityClass());
                                if (childSqlEntityInfo.isChildList()) {
                                    for (Entity childRecord : childList) {
                                        fetchChildRecords(childRecord, null, isListOnly);
                                    }
                                }
                            }

                            // Set child list
                            clfi.getSetter().invoke(record, childList);
                        }
                    }
                }
            } catch (UnifyException e) {
                throw e;
            } catch (Exception e) {
                throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR,
                        getClass().getSimpleName());
            }
        }
    }

    private void updateChildRecords(SqlEntityInfo sqlEntityInfo, Entity record) throws UnifyException {
        Object id = record.getId();
        deleteChildRecords(sqlEntityInfo, id);
        createChildRecords(sqlEntityInfo, record, id);
    }

    @SuppressWarnings({ "unchecked" })
    private void createChildRecords(SqlEntityInfo sqlEntityInfo, Entity record, Object id) throws UnifyException {
        try {
            if (sqlEntityInfo.isSingleChildList()) {
                for (ChildFieldInfo alfi : sqlEntityInfo.getSingleChildInfoList()) {
                    Entity childRecord = (Entity) alfi.getGetter().invoke(record);
                    if (childRecord != null) {
                        alfi.getAttrFkSetter().invoke(childRecord, id);
                        create(childRecord);
                    }
                }
            }

            if (sqlEntityInfo.isManyChildList()) {
                for (ChildFieldInfo alfi : sqlEntityInfo.getManyChildInfoList()) {
                    List<? extends Entity> attrList = (List<? extends Entity>) alfi.getGetter().invoke(record);
                    if (attrList != null) {
                        Method attrFkSetter = alfi.getAttrFkSetter();
                        for (Entity attrRecord : attrList) {
                            attrFkSetter.invoke(attrRecord, id);
                            create(attrRecord);
                        }
                    }
                }
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, getClass().getSimpleName());
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void deleteChildRecords(SqlEntityInfo sqlEntityInfo, Object id) throws UnifyException {
        for (OnDeleteCascadeInfo odci : sqlEntityInfo.getOnDeleteCascadeInfoList()) {
            Query<? extends Entity> query = new Query(odci.getChildEntityClass());
            query.equals(odci.getChildFkField().getName(), id);
            deleteAll(query);
        }
    }

    private SqlStatementExecutor getSqlStatementExecutor() throws UnifyException {
        if (closed) {
            throw new UnifyException(UnifyCoreErrorConstants.DATASOURCE_SESSION_IS_CLOSED, getDataSourceName());
        }
        return sqlStatementExecutor;
    }
}
