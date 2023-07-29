/*
 * Copyright 2018-2023 The Code Department.
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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyOperationException;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.constant.ChildFetch;
import com.tcdng.unify.core.constant.FetchChild;
import com.tcdng.unify.core.constant.IncludeListOnly;
import com.tcdng.unify.core.constant.MustMatch;
import com.tcdng.unify.core.constant.QueryAgainst;
import com.tcdng.unify.core.constant.UpdateChild;
import com.tcdng.unify.core.criterion.AdditionExpression;
import com.tcdng.unify.core.criterion.Aggregate;
import com.tcdng.unify.core.criterion.AggregateFunction;
import com.tcdng.unify.core.criterion.Amongst;
import com.tcdng.unify.core.criterion.DivisionExpression;
import com.tcdng.unify.core.criterion.MultiplicationExpression;
import com.tcdng.unify.core.criterion.Select;
import com.tcdng.unify.core.criterion.SubtractionExpression;
import com.tcdng.unify.core.criterion.Update;
import com.tcdng.unify.core.data.Aggregation;
import com.tcdng.unify.core.data.GroupAggregation;
import com.tcdng.unify.core.database.CallableProc;
import com.tcdng.unify.core.database.DatabaseSession;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.EntityPolicy;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.core.util.SqlUtils;

/**
 * Implementation of an SQL database session.
 * 
 * @author The Code Department
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
	public boolean isReadOnly() throws UnifyException {
		return sqlDataSource.isReadOnly();
	}

	@Override
	public String getDataSourceName() {
		return sqlDataSource.getName();
	}

	@Override
	public Object create(Entity record) throws UnifyException {
		ensureWritable();
		SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.findSqlEntityInfo(SqlUtils.getEntityClass(record));
		if (sqlEntityInfo.isViewOnly()) {
			throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_OPERATION_UNSUPPORTED,
					sqlEntityInfo.getEntityClass(), "CREATE");
		}

		return create(sqlEntityInfo, record);
	}

	@Override
	public <T extends Entity> T find(Class<T> clazz, Object id) throws UnifyException {
		return find(clazz, id, FetchChild.TRUE);
	}

	@Override
	public <T extends Entity> T find(Class<T> clazz, Object id, final Object versionNo) throws UnifyException {
		return find(clazz, id, versionNo, FetchChild.TRUE);
	}

	@Override
	public <T extends Entity> T find(Query<T> query) throws UnifyException {
		return find(query, FetchChild.TRUE);
	}

	@Override
	public <T extends Entity> T findFirst(Query<T> query) throws UnifyException {
		final Long id = min(Long.class, "id", query);
		return id != null ? find(query.getEntityClass(), id) : null;
	}

	@Override
	public <T extends Entity> T findLast(Query<T> query) throws UnifyException {
		final Long id = max(Long.class, "id", query);
		return id != null ? find(query.getEntityClass(), id) : null;
	}

	@Override
	public <T extends Entity> T findLean(Class<T> clazz, Object id) throws UnifyException {
		return find(clazz, id, FetchChild.FALSE);
	}

	@Override
	public <T extends Entity> T findLean(Class<T> clazz, Object id, final Object versionNo) throws UnifyException {
		return find(clazz, id, versionNo, FetchChild.FALSE);
	}

	@Override
	public <T extends Entity> T findLean(Query<T> query) throws UnifyException {
		return find(query, FetchChild.FALSE);
	}

	@Override
	public <T extends Entity> T findLeanFirst(Query<T> query) throws UnifyException {
		final Long id = min(Long.class, "id", query);
		return id != null ? findLean(query.getEntityClass(), id) : null;
	}

	@Override
	public <T extends Entity> T findLeanLast(Query<T> query) throws UnifyException {
		final Long id = max(Long.class, "id", query);
		return id != null ? findLean(query.getEntityClass(), id) : null;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T extends Entity> T findConstraint(T record) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.findSqlEntityInfo(record.getClass());
		if (sqlEntityInfo.isUniqueConstraints()) {
			Query<T> query = Query.of((Class<T>) record.getClass());
			for (SqlUniqueConstraintInfo suci : sqlEntityInfo.getUniqueConstraintList().values()) {
				query.clear();
				for (String fieldName : suci.getFieldNameList()) {
					query.addEquals(fieldName, ReflectUtils.getBeanProperty(record, fieldName));
				}

				if (suci.isWithConditionList()) {
					for (SqlQueryRestrictionInfo sqlQueryRestrictionInfo : suci.getConditionList()) {
						query.addRestriction(sqlQueryRestrictionInfo.getRestriction());
					}
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
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
		if (entityPolicy != null) {
			entityPolicy.preQuery(query);
		}

		if (sqlEntityInfo.testTrueFieldNamesOnly(query.getRestrictedFields())) {
			return getSqlStatementExecutor().executeMultipleRecordResultQuery(connection,
					sqlDataSourceDialect.prepareFindStatement(query, QueryAgainst.TABLE));
		}

		return getSqlStatementExecutor().executeMultipleRecordResultQuery(connection,
				sqlDataSourceDialect.prepareFindStatement(query, QueryAgainst.VIEW));
	}

	@Override
	public <T extends Entity> List<T> findAllWithChildren(Query<T> query) throws UnifyException {
		List<T> list = findAll(query);
		if (!list.isEmpty()) {
			SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
			for (T record : list) {
				fetchChildRecords(sqlEntityInfo, record, query.getSelect(), ChildFetch.ALL, IncludeListOnly.FALSE);
			}
		}

		return list;
	}

	@Override
	public <T, U extends Entity> Map<T, U> findAllMap(Class<T> keyClass, String keyName, Query<U> query)
			throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
		if (entityPolicy != null) {
			entityPolicy.preQuery(query);
		}

		if (sqlEntityInfo.testTrueFieldNamesOnly(query.getRestrictedFields())) {
			return getSqlStatementExecutor().executeMultipleRecordResultQuery(connection, keyClass, keyName,
					sqlDataSourceDialect.prepareFindStatement(query, QueryAgainst.TABLE));
		}

		return getSqlStatementExecutor().executeMultipleRecordResultQuery(connection, keyClass, keyName,
				sqlDataSourceDialect.prepareFindStatement(query, QueryAgainst.VIEW));
	}

	@Override
	public <T, U extends Entity> Map<T, List<U>> findAllListMap(Class<T> keyClass, String keyName, Query<U> query)
			throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
		if (entityPolicy != null) {
			entityPolicy.preQuery(query);
		}

		if (sqlEntityInfo.testTrueFieldNamesOnly(query.getRestrictedFields())) {
			return getSqlStatementExecutor().executeMultipleRecordListResultQuery(connection, keyClass, keyName,
					sqlDataSourceDialect.prepareFindStatement(query, QueryAgainst.TABLE));
		}

		return getSqlStatementExecutor().executeMultipleRecordListResultQuery(connection, keyClass, keyName,
				sqlDataSourceDialect.prepareFindStatement(query, QueryAgainst.VIEW));
	}

	@Override
	public <T extends Entity> void findChildren(T record) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(record.getClass());
		fetchChildRecords(sqlEntityInfo, record, null, ChildFetch.ALL, IncludeListOnly.FALSE);
	}

	@Override
	public <T extends Entity> void findEditableChildren(T record) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(record.getClass());
		fetchChildRecords(sqlEntityInfo, record, null, ChildFetch.EDITABLE_ONLY, IncludeListOnly.FALSE);
	}

	@Override
	public <T extends Entity> void findReadOnlyChildren(T record) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(record.getClass());
		fetchChildRecords(sqlEntityInfo, record, null, ChildFetch.READ_ONLY, IncludeListOnly.FALSE);
	}

	@Override
	public <T extends Entity> T list(Class<T> clazz, Object id) throws UnifyException {
		return list(clazz, id, FetchChild.TRUE);
	}

	@Override
	public <T extends Entity> T list(Class<T> clazz, Object id, final Object versionNo) throws UnifyException {
		return list(clazz, id, versionNo, FetchChild.TRUE);
	}

	@Override
	public <T extends Entity> T list(Query<T> query) throws UnifyException {
		return list(query, FetchChild.TRUE);
	}

	@Override
	public <T extends Entity> T listFirst(Query<T> query) throws UnifyException {
		final Long id = min(Long.class, "id", query);
		return id != null ? list(query.getEntityClass(), id) : null;
	}

	@Override
	public <T extends Entity> T listLast(Query<T> query) throws UnifyException {
		final Long id = max(Long.class, "id", query);
		return id != null ? list(query.getEntityClass(), id) : null;
	}

	@Override
	public <T extends Entity> T listLean(Class<T> clazz, Object id) throws UnifyException {
		return list(clazz, id, FetchChild.FALSE);
	}

	@Override
	public <T extends Entity> T listLean(Class<T> clazz, Object id, final Object versionNo) throws UnifyException {
		return list(clazz, id, versionNo, FetchChild.FALSE);
	}

	@Override
	public <T extends Entity> T listLean(Query<T> query) throws UnifyException {
		return list(query, FetchChild.FALSE);
	}

	@Override
	public <T extends Entity> T listLeanFirst(Query<T> query) throws UnifyException {
		final Long id = min(Long.class, "id", query);
		return id != null ? listLean(query.getEntityClass(), id) : null;
	}

	@Override
	public <T extends Entity> T listLeanLast(Query<T> query) throws UnifyException {
		final Long id = max(Long.class, "id", query);
		return id != null ? listLean(query.getEntityClass(), id) : null;
	}

	@Override
	public <T extends Entity> List<T> listAll(Query<T> query) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
		if (entityPolicy != null) {
			entityPolicy.preQuery(query);
		}

		return getSqlStatementExecutor().executeMultipleRecordResultQuery(connection,
				sqlDataSourceDialect.prepareListStatement(query));
	}

	@Override
	public <T extends Entity> List<T> listAllWithChildren(Query<T> query) throws UnifyException {
		List<T> list = listAll(query);
		if (!list.isEmpty()) {
			SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
			for (T record : list) {
				fetchChildRecords(sqlEntityInfo, record, query.getSelect(), ChildFetch.ALL, IncludeListOnly.TRUE);
			}
		}

		return list;
	}

	@Override
	public <T, U extends Entity> Map<T, U> listAll(Class<T> keyClass, String keyName, Query<U> query)
			throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
		if (entityPolicy != null) {
			entityPolicy.preQuery(query);
		}

		return getSqlStatementExecutor().executeMultipleRecordResultQuery(connection, keyClass, keyName,
				sqlDataSourceDialect.prepareListStatement(query));
	}

	@Override
	public <T, U extends Entity> Map<T, List<U>> listAllListMap(Class<T> keyClass, String keyName, Query<U> query)
			throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
		if (entityPolicy != null) {
			entityPolicy.preQuery(query);
		}

		return getSqlStatementExecutor().executeMultipleRecordListResultQuery(connection, keyClass, keyName,
				sqlDataSourceDialect.prepareListStatement(query));
	}

	@Override
	public <T extends Entity> void listChildren(T record) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(record.getClass());
		fetchChildRecords(sqlEntityInfo, record, null, ChildFetch.ALL, IncludeListOnly.TRUE);
	}

	@Override
	public <T extends Entity> void listEditableChildren(T record) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(record.getClass());
		fetchChildRecords(sqlEntityInfo, record, null, ChildFetch.EDITABLE_ONLY, IncludeListOnly.TRUE);
	}

	@Override
	public <T extends Entity> void listReadOnlyChildren(T record) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(record.getClass());
		fetchChildRecords(sqlEntityInfo, record, null, ChildFetch.READ_ONLY, IncludeListOnly.TRUE);
	}

	@Override
	public <T, U extends Entity> List<T> valueList(Class<T> fieldClass, String fieldName, final Query<U> query)
			throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
		if (entityPolicy != null) {
			entityPolicy.preQuery(query);
		}

		final Select select = query.getSelect();
		try {
			query.setSelect(new Select(fieldName).setDistinct(query.isDistinct()));
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
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
		if (entityPolicy != null) {
			entityPolicy.preQuery(query);
		}

		final Select select = query.getSelect();
		try {
			query.setSelect(new Select(fieldName).setDistinct(true));
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
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
		if (entityPolicy != null) {
			entityPolicy.preQuery(query);
		}

		final Select select = query.getSelect();
		try {
			query.setSelect(new Select(keyName, valueName).setDistinct(true));
			return getSqlStatementExecutor().executeMultipleObjectMapResultQuery(connection, keyClass, keyName,
					valueClass, valueName, sqlDataSourceDialect.prepareListStatement(query));
		} finally {
			query.setSelect(select);
		}
	}

	@Override
	public <T, U, V extends Entity> Map<T, List<U>> valueListMap(Class<T> keyClass, String keyName, Class<U> valueClass,
			String valueName, Query<V> query) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
		if (entityPolicy != null) {
			entityPolicy.preQuery(query);
		}

		final Select select = query.getSelect();
		try {
			query.setSelect(new Select(keyName, valueName).setDistinct(true));
			return getSqlStatementExecutor().executeMultipleObjectListMapResultQuery(connection, keyClass, keyName,
					valueClass, valueName, sqlDataSourceDialect.prepareListStatement(query));
		} finally {
			query.setSelect(select);
		}
	}

	@Override
	public <T, U extends Entity> T value(Class<T> fieldClass, String fieldName, Query<U> query) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
		if (entityPolicy != null) {
			entityPolicy.preQuery(query);
		}

		final Select select = query.getSelect();
		try {
			query.setSelect(new Select(fieldName).setDistinct(true));
			return getSqlStatementExecutor().executeSingleObjectResultQuery(connection, fieldClass,
					sqlDataSourceDialect.getSqlTypePolicy(sqlEntityInfo.getListFieldInfo(fieldName).getColumnType()),
					sqlDataSourceDialect.prepareListStatement(query), MustMatch.fromBoolean(query.isMustMatch()));
		} finally {
			query.setSelect(select);
		}
	}

	@Override
	public <T extends Number, U extends Entity> T min(Class<T> fieldClass, String fieldName, Query<U> query)
			throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
		if (entityPolicy != null) {
			entityPolicy.preQuery(query);
		}

		SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getListFieldInfo(fieldName);
		return getSqlStatementExecutor().executeSingleObjectResultQuery(connection, fieldClass,
				sqlDataSourceDialect.getSqlTypePolicy(sqlFieldInfo.getColumnType()),
				sqlDataSourceDialect.prepareMinStatement(sqlFieldInfo.getPreferredColumnName(), query),
				MustMatch.FALSE);
	}

	@Override
	public <T extends Number, U extends Entity> T max(Class<T> fieldClass, String fieldName, Query<U> query)
			throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
		if (entityPolicy != null) {
			entityPolicy.preQuery(query);
		}

		SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getListFieldInfo(fieldName);
		return getSqlStatementExecutor().executeSingleObjectResultQuery(connection, fieldClass,
				sqlDataSourceDialect.getSqlTypePolicy(sqlFieldInfo.getColumnType()),
				sqlDataSourceDialect.prepareMaxStatement(sqlFieldInfo.getPreferredColumnName(), query),
				MustMatch.FALSE);
	}

	@Override
	public <T extends Number, U extends Entity> int add(Class<T> fieldClass, String fieldName, T val, Query<U> query)
			throws UnifyException {
		return updateAll(query, new Update().add(fieldName, new AdditionExpression(val)));
	}

	@Override
	public <T extends Number, U extends Entity> int subtract(Class<T> fieldClass, String fieldName, T val,
			Query<U> query) throws UnifyException {
		return updateAll(query, new Update().add(fieldName, new SubtractionExpression(val)));
	}

	@Override
	public <T extends Number, U extends Entity> int multiply(Class<T> fieldClass, String fieldName, T val,
			Query<U> query) throws UnifyException {
		return updateAll(query, new Update().add(fieldName, new MultiplicationExpression(val)));
	}

	@Override
	public <T extends Number, U extends Entity> int divide(Class<T> fieldClass, String fieldName, T val, Query<U> query)
			throws UnifyException {
		return updateAll(query, new Update().add(fieldName, new DivisionExpression(val)));
	}

	@Override
	public void populateListOnly(Entity record) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.findSqlEntityInfo(record.getClass());
		try {
			Map<String, Object> fkItemMap = new HashMap<String, Object>();
			for (SqlForeignKeyInfo sqlForeignKeyInfo : sqlEntityInfo.getForeignKeyList()) {
				SqlFieldInfo fkSqlFieldInfo = sqlForeignKeyInfo.getSqlFieldInfo();
				if (!fkSqlFieldInfo.isIgnoreFkConstraint()) {
					Object fkId = fkSqlFieldInfo.getGetter().invoke(record);
					if (fkId != null) {
						SqlEntityInfo fkSqlEntityInfo = fkSqlFieldInfo.getForeignEntityInfo();
						Object fkRecord = null;
						SqlStatement sqlStatement = sqlDataSourceDialect
								.prepareListByPkStatement(fkSqlEntityInfo.getKeyClass(), fkId);
						try {
							fkRecord = getSqlStatementExecutor().executeSingleRecordResultQuery(connection,
									sqlStatement, MustMatch.TRUE);
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
			throw new UnifyOperationException(e);
		}
	}

	@Override
	public int updateById(Entity record) throws UnifyException {
		ensureWritable();
		return updateById(record, ChildFetch.ALL, UpdateChild.TRUE);
	}

	@Override
	public int updateByIdVersion(Entity record) throws UnifyException {
		ensureWritable();
		return updateByIdVersion(record, ChildFetch.ALL, UpdateChild.TRUE);
	}

	@Override
	public int updateByIdEditableChildren(Entity record) throws UnifyException {
		ensureWritable();
		return updateById(record, ChildFetch.EDITABLE_ONLY, UpdateChild.TRUE);
	}

	@Override
	public int updateByIdVersionEditableChildren(Entity record) throws UnifyException {
		ensureWritable();
		return updateByIdVersion(record, ChildFetch.EDITABLE_ONLY, UpdateChild.TRUE);
	}

	@Override
	public int updateLeanById(Entity record) throws UnifyException {
		ensureWritable();
		return updateById(record, null, UpdateChild.FALSE);
	}

	@Override
	public int updateLeanByIdVersion(Entity record) throws UnifyException {
		ensureWritable();
		return updateByIdVersion(record, null, UpdateChild.FALSE);
	}

	@Override
	public int updateById(Class<? extends Entity> clazz, Object id, Update update) throws UnifyException {
		ensureWritable();
		return getSqlStatementExecutor().executeUpdate(connection,
				sqlDataSourceDialect.prepareUpdateStatement(clazz, id, update));
	}

	@Override
	public int updateAll(Query<? extends Entity> query, Update update) throws UnifyException {
		ensureWritable();
		try {
			SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
			EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
			if (entityPolicy != null) {
				entityPolicy.preQuery(query);
			}

			if (sqlEntityInfo.isViewOnly()) {
				throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_OPERATION_UNSUPPORTED,
						sqlEntityInfo.getEntityClass(), "UPDATE");
			}

			if (sqlEntityInfo.isViewOnly()) {
				throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_OPERATION_UNSUPPORTED,
						sqlEntityInfo.getEntityClass(), "UPDATE");
			}

			if (sqlDataSourceDialect.isQueryOffsetOrLimit(query)
					|| (sqlEntityInfo.testTrueFieldNamesOnly(query.getRestrictedFields()))) {
				return getSqlStatementExecutor().executeUpdate(connection,
						sqlDataSourceDialect.prepareUpdateStatement(query, update));
			}

			SqlFieldInfo idFieldInfo = sqlEntityInfo.getIdFieldInfo();
			List<?> idList = valueList(idFieldInfo.getFieldType(), idFieldInfo.getName(), query);
			if (!idList.isEmpty()) {
				Query<? extends Entity> updateQuery = query.copyNoAll();
				updateQuery.addRestriction(new Amongst(idFieldInfo.getName(), idList));
				return getSqlStatementExecutor().executeUpdate(connection,
						sqlDataSourceDialect.prepareUpdateStatement(updateQuery, update));
			}
		} catch (UnifyException e) {
			throw e;
		} catch (Exception e) {
			throw new UnifyOperationException(e, getClass().getSimpleName());
		}
		return 0;
	}

	@Override
	public int deleteById(Entity record) throws UnifyException {
		ensureWritable();
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(record);
		if (sqlEntityInfo.isViewOnly()) {
			throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_OPERATION_UNSUPPORTED,
					sqlEntityInfo.getEntityClass(), "DELETE_BY_ID");
		}

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
		ensureWritable();
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(record);
		if (sqlEntityInfo.isViewOnly()) {
			throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_OPERATION_UNSUPPORTED,
					sqlEntityInfo.getEntityClass(), "DELETE_BY_ID_VERSION");
		}

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
		ensureWritable();
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(clazz);
		if (sqlEntityInfo.isViewOnly()) {
			throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_OPERATION_UNSUPPORTED,
					sqlEntityInfo.getEntityClass(), "DELETE");
		}

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

	@Override
	public int deleteAll(Query<? extends Entity> query) throws UnifyException {
		ensureWritable();
		try {
			SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
			if (sqlEntityInfo.isViewOnly()) {
				throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_OPERATION_UNSUPPORTED,
						sqlEntityInfo.getEntityClass(), "DELETE_ALL");
			}

			EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
			if (entityPolicy != null) {
				entityPolicy.preQuery(query);
			}

			if (sqlDataSourceDialect.isQueryOffsetOrLimit(query) || (!sqlEntityInfo.isChildList()
					&& sqlEntityInfo.testTrueFieldNamesOnly(query.getRestrictedFields()))) {
				return getSqlStatementExecutor().executeUpdate(connection,
						sqlDataSourceDialect.prepareDeleteStatement(query));
			}

			SqlFieldInfo idFieldInfo = sqlEntityInfo.getIdFieldInfo();
			List<?> idList = valueList(idFieldInfo.getFieldType(), idFieldInfo.getName(), query);
			if (!idList.isEmpty()) {
				if (sqlEntityInfo.isOnDeleteCascadeList()) {
					final String tableName = sqlEntityInfo.getTableName();
					for (OnDeleteCascadeInfo odci : sqlEntityInfo.getOnDeleteCascadeInfoList()) {
						Query<? extends Entity> attrQuery = Query.of(odci.getChildEntityClass());
						if (odci.isWithChildFkType()) {
							attrQuery.addEquals(odci.getChildFkTypeField().getName(), tableName);
						}

						if (odci.isWithChildCat()) {
							attrQuery.addEquals(odci.getChildCatField().getName(), odci.getCategory());
						}

						attrQuery.addAmongst(odci.getChildFkIdField().getName(), idList);
						deleteAll(attrQuery);
					}
				}

				Query<? extends Entity> deleteQuery = Query.of(sqlEntityInfo.getEntityClass());
				deleteQuery.addAmongst(idFieldInfo.getName(), idList);
				return getSqlStatementExecutor().executeUpdate(connection,
						sqlDataSourceDialect.prepareDeleteStatement(deleteQuery));
			}
		} catch (UnifyException e) {
			throw e;
		} catch (Exception e) {
			throw new UnifyOperationException(e, getClass().getSimpleName());
		}
		return 0;
	}

	@Override
	public int count(Query<? extends Entity> query) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
		if (entityPolicy != null) {
			entityPolicy.preQuery(query);
		}

		// Check is fetch from table
		if (sqlEntityInfo.testTrueFieldNamesOnly(query.getRestrictedFields())) {
			return getSqlStatementExecutor().executeSingleObjectResultQuery(connection, int.class,
					sqlDataSourceDialect.getSqlTypePolicy(int.class),
					sqlDataSourceDialect.prepareCountStatement(query, QueryAgainst.TABLE), MustMatch.TRUE);
		}

		// Fetch from view
		return getSqlStatementExecutor().executeSingleObjectResultQuery(connection, int.class,
				sqlDataSourceDialect.getSqlTypePolicy(int.class),
				sqlDataSourceDialect.prepareCountStatement(query, QueryAgainst.VIEW), MustMatch.TRUE);
	}

	@Override
	public Entity getExtendedInstance(Class<? extends Entity> entityClass) throws UnifyException {
		return ReflectUtils.newInstance(resolveSqlEntityInfo(entityClass).getEntityClass());
	}

	@Override
	public Date getNow() throws UnifyException {
		return getSqlStatementExecutor().executeSingleObjectResultQuery(connection, Date.class,
				sqlDataSourceDialect.getSqlTypePolicy(ColumnType.TIMESTAMP_UTC),
				sqlDataSourceDialect.generateUTCTimestampSql(), MustMatch.TRUE);
	}

	@Override
	public Aggregation aggregate(AggregateFunction aggregateFunction, Query<? extends Entity> query)
			throws UnifyException {
		try {
			SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
			EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
			if (entityPolicy != null) {
				entityPolicy.preQuery(query);
			}

			return getSqlStatementExecutor().executeSingleAggregateResultQuery(aggregateFunction, connection,
					sqlDataSourceDialect.getSqlTypePolicy(int.class),
					sqlDataSourceDialect.prepareAggregateStatement(aggregateFunction, query));
		} catch (UnifyException e) {
			throw e;
		} catch (Exception e) {
			throw new UnifyOperationException(e, getClass().getSimpleName());
		}
	}

	@Override
	public List<Aggregation> aggregateMany(Aggregate aggregate, Query<? extends Entity> query) throws UnifyException {
		try {
			SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
			EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
			if (entityPolicy != null) {
				entityPolicy.preQuery(query);
			}

			return getSqlStatementExecutor().executeMultipleAggregateResultQuery(aggregate.getFunctionList(),
					connection, sqlDataSourceDialect.getSqlTypePolicy(int.class),
					sqlDataSourceDialect.prepareAggregateStatement(aggregate.getFunctionList(), query));
		} catch (UnifyException e) {
			throw e;
		} catch (Exception e) {
			throw new UnifyOperationException(e, getClass().getSimpleName());
		}
	}

	@Override
	public List<GroupAggregation> aggregateGroupMany(Aggregate aggregate, Query<? extends Entity> query)
			throws UnifyException {
		try {
			SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
			EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
			if (entityPolicy != null) {
				entityPolicy.preQuery(query);
			}

			return getSqlStatementExecutor().executeMultipleAggregateResultQuery(aggregate.getFunctionList(),
					query.getGroupBy(), connection, sqlDataSourceDialect.getSqlTypePolicy(int.class),
					sqlDataSourceDialect.prepareAggregateStatement(aggregate.getFunctionList(), query));
		} catch (UnifyException e) {
			throw e;
		} catch (Exception e) {
			throw new UnifyOperationException(e, getClass().getSimpleName());
		}
	}

	@Override
	public void executeCallable(CallableProc callableProc) throws UnifyException {
		SqlCallableStatement sqlCallableStatement = sqlDataSourceDialect.prepareCallableStatement(callableProc);
		try {
			getSqlStatementExecutor().executeCallable(connection, callableProc, sqlCallableStatement);
		} finally {
			sqlDataSourceDialect.restoreCallableStatement(sqlCallableStatement);
		}
	}

	@Override
	public Map<Class<?>, List<?>> executeCallableWithResults(CallableProc callableProc) throws UnifyException {
		SqlCallableStatement sqlCallableStatement = sqlDataSourceDialect.prepareCallableStatement(callableProc);
		try {
			return getSqlStatementExecutor().executeCallableWithResults(connection, callableProc, sqlCallableStatement);
		} finally {
			sqlDataSourceDialect.restoreCallableStatement(sqlCallableStatement);
		}
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
		if (!closed) {
			try {
				while (!savepointStack.empty()) {
					connection.releaseSavepoint(savepointStack.pop());
				}
			} catch (Exception e) {
				throw new UnifyException(e, UnifyCoreErrorConstants.DATASOURCE_SESSION_ERROR, getDataSourceName());
			} finally {
				sqlDataSource.restoreConnection(connection);
				connection = null;
				closed = true;
			}
		}
	}

	@Override
	public void commit() throws UnifyException {
		try {
			connection.commit();
		} catch (Exception e) {
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

	@Override
	protected void finalize() throws Throwable {
		close();
	}

	private void ensureWritable() throws UnifyException {
		if (isReadOnly()) {
			throw new UnifyException(UnifyCoreErrorConstants.DATASOURCE_IN_READONLY_MODE, sqlDataSource.getName());
		}
	}

	private <T extends Entity> T find(Class<T> clazz, Object id, FetchChild fetchChild) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(clazz);
		SqlStatement sqlStatement = sqlDataSourceDialect.prepareFindByPkStatement(clazz, id);
		try {
			T record = getSqlStatementExecutor().executeSingleRecordResultQuery(connection, sqlStatement,
					MustMatch.TRUE);
			if (record == null) {
				throw new UnifyException(UnifyCoreErrorConstants.RECORD_WITH_PK_NOT_FOUND, clazz, id);
			}

			if (fetchChild.isTrue()) {
				fetchChildRecords(sqlEntityInfo, record, null, ChildFetch.ALL, IncludeListOnly.FALSE);
			}
			return record;
		} finally {
			sqlDataSourceDialect.restoreStatement(sqlStatement);
		}
	}

	private <T extends Entity> T find(Class<T> clazz, Object id, final Object versionNo, FetchChild fetchChild)
			throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(clazz);
		SqlStatement sqlStatement = sqlDataSourceDialect.prepareFindByPkVersionStatement(clazz, id, versionNo);
		try {
			T record = getSqlStatementExecutor().executeSingleRecordResultQuery(connection, sqlStatement,
					MustMatch.TRUE);
			if (record == null) {
				throw new UnifyException(UnifyCoreErrorConstants.RECORD_WITH_PK_VERSION_NOT_FOUND, clazz, id,
						versionNo);
			}

			if (fetchChild.isTrue()) {
				fetchChildRecords(sqlEntityInfo, record, null, ChildFetch.ALL, IncludeListOnly.FALSE);
			}
			return record;
		} finally {
			sqlDataSourceDialect.restoreStatement(sqlStatement);
		}
	}

	private <T extends Entity> T find(Query<T> query, FetchChild fetchChild) throws UnifyException {
		T record = null;
		try {
			SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
			EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
			if (entityPolicy != null) {
				entityPolicy.preQuery(query);
			}

			// Check is fetch from table
			if (sqlEntityInfo.testTrueFieldNamesOnly(query.getRestrictedFields())) {
				record = getSqlStatementExecutor().executeSingleRecordResultQuery(connection,
						sqlDataSourceDialect.prepareFindStatement(query, QueryAgainst.TABLE), MustMatch.FALSE);
			} else {
				// Fetch from view
				record = getSqlStatementExecutor().executeSingleRecordResultQuery(connection,
						sqlDataSourceDialect.prepareFindStatement(query, QueryAgainst.VIEW), MustMatch.FALSE);
			}

			if (fetchChild.isTrue()) {
				fetchChildRecords(sqlEntityInfo, record, query.getSelect(), ChildFetch.ALL, IncludeListOnly.FALSE);
			}
		} catch (UnifyException e) {
			throw e;
		} catch (Exception e) {
			throw new UnifyOperationException(e, getClass().getSimpleName());
		}
		return record;
	}

	private <T extends Entity> T list(Class<T> clazz, Object id, FetchChild fetchChild) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(clazz);
		SqlStatement sqlStatement = sqlDataSourceDialect.prepareListByPkStatement(clazz, id);
		try {
			T record = getSqlStatementExecutor().executeSingleRecordResultQuery(connection, sqlStatement,
					MustMatch.TRUE);
			if (record == null) {
				throw new UnifyException(UnifyCoreErrorConstants.RECORD_WITH_PK_NOT_FOUND, clazz, id);
			}

			if (fetchChild.isTrue()) {
				fetchChildRecords(sqlEntityInfo, record, null, ChildFetch.ALL, IncludeListOnly.TRUE);
			}
			return record;
		} finally {
			sqlDataSourceDialect.restoreStatement(sqlStatement);
		}
	}

	private <T extends Entity> T list(Class<T> clazz, Object id, final Object versionNo, FetchChild fetchChild)
			throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(clazz);
		SqlStatement sqlStatement = sqlDataSourceDialect.prepareListByPkVersionStatement(clazz, id, versionNo);
		try {
			T record = getSqlStatementExecutor().executeSingleRecordResultQuery(connection, sqlStatement,
					MustMatch.TRUE);
			if (record == null) {
				throw new UnifyException(UnifyCoreErrorConstants.RECORD_WITH_PK_VERSION_NOT_FOUND, clazz, id,
						versionNo);
			}

			if (fetchChild.isTrue()) {
				fetchChildRecords(sqlEntityInfo, record, null, ChildFetch.ALL, IncludeListOnly.TRUE);
			}
			return record;
		} finally {
			sqlDataSourceDialect.restoreStatement(sqlStatement);
		}
	}

	private <T extends Entity> T list(Query<T> query, FetchChild fetchChild) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(query);
		EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
		if (entityPolicy != null) {
			entityPolicy.preQuery(query);
		}

		T record = getSqlStatementExecutor().executeSingleRecordResultQuery(connection,
				sqlDataSourceDialect.prepareListStatement(query), MustMatch.FALSE);

		if (fetchChild.isTrue()) {
			fetchChildRecords(sqlEntityInfo, record, query.getSelect(), ChildFetch.ALL, IncludeListOnly.TRUE);
		}
		return record;
	}

	private <T extends Entity> void fetchChildRecords(SqlEntityInfo sqlEntityInfo, T record, Select select,
			ChildFetch childFetch, IncludeListOnly includeListOnly) throws UnifyException {
		if (record != null) {
			try {
				if (sqlEntityInfo.isChildList()) {
					boolean isSelect = select != null && !select.isEmpty();
					Object id = record.getId();

					if (sqlEntityInfo.isSingleChildList()) {
						final String tableName = sqlEntityInfo.getTableName();
						for (ChildFieldInfo clfi : sqlEntityInfo.getSingleChildInfoList()) {
							if (!clfi.qualifies(childFetch)) {
								continue;
							}

							if (isSelect && !select.contains(clfi.getName())) {
								continue;
							}

							SqlEntityInfo childSqlEntityInfo = sqlDataSourceDialect
									.findSqlEntityInfo(clfi.getChildEntityClass());
							Query<? extends Entity> query = Query.of(clfi.getChildEntityClass());
							if (clfi.isWithChildFkType()) {
								query.addEquals(clfi.getChildFkTypeField().getName(), tableName);
							}

							if (clfi.isWithChildCat()) {
								query.addEquals(clfi.getChildCatField().getName(), clfi.getCategory());
							}

							query.addEquals(clfi.getChildFkIdField().getName(), id)
									.addOrder(childSqlEntityInfo.getIdFieldInfo().getName());
							List<? extends Entity> childList = null;
							if (includeListOnly.isTrue()) {
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
								if (childSqlEntityInfo.isChildList()) {
									fetchChildRecords(childSqlEntityInfo, childRecord, null, ChildFetch.ALL,
											includeListOnly);
								}
							}

							// Set child record
							clfi.getSetter().invoke(record, childRecord);
						}
					}

					if (sqlEntityInfo.isManyChildList()) {
						final String tableName = sqlEntityInfo.getTableName();
						for (ChildFieldInfo clfi : sqlEntityInfo.getManyChildInfoList()) {
							if (!clfi.qualifies(childFetch)) {
								continue;
							}

							if (isSelect && !select.contains(clfi.getName())) {
								continue;
							}

							SqlEntityInfo childSqlEntityInfo = sqlDataSourceDialect
									.findSqlEntityInfo(clfi.getChildEntityClass());
							Query<? extends Entity> query = Query.of(clfi.getChildEntityClass());
							if (clfi.isWithChildFkType()) {
								query.addEquals(clfi.getChildFkTypeField().getName(), tableName);
							}

							if (clfi.isWithChildCat()) {
								query.addEquals(clfi.getChildCatField().getName(), clfi.getCategory());
							}

							query.addEquals(clfi.getChildFkIdField().getName(), id)
									.addOrder(childSqlEntityInfo.getIdFieldInfo().getName());
							List<? extends Entity> childList = null;
							if (includeListOnly.isTrue()) {
								childList = listAll(query);
							} else {
								childList = findAll(query);
							}

							// Check if child has child list and load if necessary
							if (!childList.isEmpty()) {
								if (childSqlEntityInfo.isChildList()) {
									for (Entity childRecord : childList) {
										fetchChildRecords(childSqlEntityInfo, childRecord, null, ChildFetch.ALL,
												includeListOnly);
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
				throw new UnifyOperationException(e, getClass().getSimpleName());
			}
		}
	}

	private int updateById(Entity record, ChildFetch fetch, UpdateChild updateChild) throws UnifyException {
		int result;
		SqlStatement sqlStatement = null;
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(record);
		if (sqlEntityInfo.isViewOnly()) {
			throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_OPERATION_UNSUPPORTED,
					sqlEntityInfo.getEntityClass(), "UPDATE");
		}

		EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
		try {
			if (entityPolicy != null) {
				if (entityPolicy.isSetNow()) {
					entityPolicy.preUpdate(record, getNow());
				} else {
					entityPolicy.preUpdate(record, null);
				}
			}

			ensureRecordTenantId(sqlEntityInfo, record);
			sqlStatement = sqlDataSourceDialect.prepareUpdateByPkStatement(record);
			result = getSqlStatementExecutor().executeUpdate(connection, sqlStatement);
			if (result == 0) {
				throw new UnifyException(UnifyCoreErrorConstants.RECORD_WITH_PK_NOT_FOUND, record.getClass(),
						record.getId());
			}

			if (updateChild.isTrue() && sqlEntityInfo.isChildList()) {
				updateChildRecords(sqlEntityInfo, record, fetch, false);
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

	private int updateByIdVersion(Entity record, ChildFetch fetch, UpdateChild updateChild) throws UnifyException {
		int result;
		SqlStatement sqlStatement = null;
		SqlEntityInfo sqlEntityInfo = resolveSqlEntityInfo(record);
		if (sqlEntityInfo.isViewOnly()) {
			throw new UnifyException(UnifyCoreErrorConstants.RECORD_VIEW_OPERATION_UNSUPPORTED,
					sqlEntityInfo.getEntityClass(), "UPDATE");
		}

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

				ensureRecordTenantId(sqlEntityInfo, record);
				sqlStatement = sqlDataSourceDialect.prepareUpdateByPkVersionStatement(record, oldVersionNo);
			} else {
				if (entityPolicy != null) {
					if (entityPolicy.isSetNow()) {
						entityPolicy.preUpdate(record, getNow());
					} else {
						entityPolicy.preUpdate(record, null);
					}
				}

				ensureRecordTenantId(sqlEntityInfo, record);
				sqlStatement = sqlDataSourceDialect.prepareUpdateByPkStatement(record);
			}

			result = getSqlStatementExecutor().executeUpdate(connection, sqlStatement);
			if (result == 0) {
				throw new UnifyException(UnifyCoreErrorConstants.RECORD_WITH_PK_VERSION_NOT_FOUND, record.getClass(),
						sqlEntityInfo.getIdFieldInfo().getGetter().invoke(record), oldVersionNo);
			}

			if (updateChild.isTrue() && sqlEntityInfo.isChildList()) {
				updateChildRecords(sqlEntityInfo, record, fetch, true);
			}
		} catch (Exception e) {
			if (entityPolicy != null) {
				entityPolicy.onUpdateError(record);
			}

			if (e instanceof UnifyException) {
				throw ((UnifyException) e);
			}
			throw new UnifyOperationException(e, getClass().getSimpleName());
		} finally {
			if (sqlStatement != null) {
				sqlDataSourceDialect.restoreStatement(sqlStatement);
			}
		}
		return result;
	}

	private Object create(SqlEntityInfo sqlEntityInfo, Entity record) throws UnifyException {
		EntityPolicy entityPolicy = sqlEntityInfo.getEntityPolicy();
		Object id = null;
		if (entityPolicy != null) {
			if (entityPolicy.isSetNow()) {
				id = entityPolicy.preCreate(record, getNow());
			} else {
				id = entityPolicy.preCreate(record, null);
			}
		}

		ensureRecordTenantId(sqlEntityInfo, record);
		SqlStatement sqlStatement = null;
		if (sqlEntityInfo.isIdentityManaged()) {
			sqlStatement = sqlDataSourceDialect.prepareCreateStatement(record);
		} else {
			sqlStatement = sqlDataSourceDialect.prepareCreateStatementWithUnmanagedIdentity(record);
		}

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

	@SuppressWarnings({ "unchecked" })
	private void createChildRecords(SqlEntityInfo sqlEntityInfo, Entity record, Object id) throws UnifyException {
		try {
			final String tableName = sqlEntityInfo.getTableName();
			if (sqlEntityInfo.isSingleChildList()) {
				for (ChildFieldInfo alfi : sqlEntityInfo.getSingleChildInfoList()) {
					Entity childRecord = (Entity) alfi.getGetter().invoke(record);
					if (childRecord != null) {
						alfi.getChildFkIdSetter().invoke(childRecord, id);
						if (alfi.isWithChildFkType()) {
							alfi.getChildFkTypeSetter().invoke(childRecord, tableName);
						}

						if (alfi.isWithChildCat()) {
							alfi.getChildCatSetter().invoke(childRecord, alfi.getCategory());
						}

						create(childRecord);
					}
				}
			}

			if (sqlEntityInfo.isManyChildList()) {
				for (ChildFieldInfo alfi : sqlEntityInfo.getManyChildInfoList()) {
					List<? extends Entity> attrList = (List<? extends Entity>) alfi.getGetter().invoke(record);
					if (attrList != null) {
						Method childFkIdSetter = alfi.getChildFkIdSetter();
						Method childFkTypeSetter = alfi.getChildFkTypeSetter();
						Method childCatSetter = alfi.getChildCatSetter();
						String category = alfi.getCategory();
						for (Entity attrRecord : attrList) {
							childFkIdSetter.invoke(attrRecord, id);
							if (childFkTypeSetter != null) {
								childFkTypeSetter.invoke(attrRecord, tableName);
							}

							if (childCatSetter != null) {
								childCatSetter.invoke(attrRecord, category);
							}

							create(attrRecord);
						}
					}
				}
			}
		} catch (UnifyException e) {
			throw e;
		} catch (Exception e) {
			throw new UnifyOperationException(e, getClass().getSimpleName());
		}
	}

	@SuppressWarnings("unchecked")
	private void updateChildRecords(SqlEntityInfo sqlEntityInfo, Entity record, ChildFetch fetch, boolean versionNo)
			throws UnifyException {
		Object id = record.getId();
		try {
			final String tableName = sqlEntityInfo.getTableName();
			if (sqlEntityInfo.isSingleChildList()) {
				for (ChildFieldInfo alfi : sqlEntityInfo.getSingleChildInfoList()) {
					if (!alfi.qualifies(fetch)) {
						continue;
					}

					Entity childRecord = (Entity) alfi.getGetter().invoke(record);
					if (childRecord != null) {
						if (childRecord.getId() == null) {
							setParentAttributes(alfi, childRecord, id, tableName);
							deleteChildRecords(alfi, tableName, id);
							create(childRecord);
						} else {
							if (versionNo) {
								updateByIdVersion(childRecord);
							} else {
								updateById(childRecord);
							}
						}
					} else {
						deleteChildRecords(alfi, tableName, id);
					}
				}
			}

			if (sqlEntityInfo.isManyChildList()) {
				for (ChildFieldInfo alfi : sqlEntityInfo.getManyChildInfoList()) {
					if (!alfi.qualifies(fetch)) {
						continue;
					}

					List<? extends Entity> childList = (List<? extends Entity>) alfi.getGetter().invoke(record);
					if (childList != null) {
						boolean clear = fetch.isEditableOnly();
						if (!clear && alfi.isIdNumber()) {
							Number last = null;
							for (Entity childRecord : childList) {
								Number cid = (Number) childRecord.getId();
								if (cid == null || (last != null && cid.longValue() < last.longValue())) {
									clear = true;
									break;
								}

								last = cid;
							}
						}

						if (clear) {
							deleteChildRecords(alfi, tableName, id);

							for (Entity childRecord : childList) {
								setParentAttributes(alfi, childRecord, id, tableName);
								create(childRecord);
							}
						} else {
							Set<Object> targetIds = getDeleteChildRecordIds(alfi, tableName, id);
							if (versionNo) {
								for (Entity childRecord : childList) {
									updateByIdVersion(childRecord);
									targetIds.remove(childRecord.getId());
								}
							} else {
								for (Entity childRecord : childList) {
									updateById(childRecord);
									targetIds.remove(childRecord.getId());
								}
							}

							deleteChildRecords(alfi, targetIds); // Delete the rest
						}
					} else {
						deleteChildRecords(alfi, tableName, id);
					}
				}
			}
		} catch (UnifyException e) {
			throw e;
		} catch (Exception e) {
			throw new UnifyOperationException(e, getClass().getSimpleName());
		}
	}

	private void setParentAttributes(ChildFieldInfo alfi, Entity childRecord, Object parentId, String tableName)
			throws Exception {
		alfi.getChildFkIdSetter().invoke(childRecord, parentId);
		if (alfi.isWithChildFkTypeSetter()) {
			alfi.getChildFkTypeSetter().invoke(childRecord, tableName);
		}

		if (alfi.isWithChildCatSetter()) {
			alfi.getChildCatSetter().invoke(childRecord, alfi.getCategory());
		}
	}
	
	private void deleteChildRecords(SqlEntityInfo sqlEntityInfo, Object id) throws UnifyException {
		final String tableName = sqlEntityInfo.getTableName();
		for (OnDeleteCascadeInfo odci : sqlEntityInfo.getOnDeleteCascadeInfoList()) {
			deleteChildRecords(odci, tableName, id);
		}
	}

	private void deleteChildRecords(OnDeleteCascadeInfo odci, String tableName, Object id) throws UnifyException {
		Query<? extends Entity> query = Query.of(odci.getChildEntityClass());
		if (odci.isWithChildFkType()) {
			query.addEquals(odci.getChildFkTypeField().getName(), tableName);
		}

		if (odci.isWithChildCat()) {
			query.addEquals(odci.getChildCatField().getName(), odci.getCategory());
		}

		query.addEquals(odci.getChildFkIdField().getName(), id);
		deleteAll(query);
	}

	private Set<Object> getDeleteChildRecordIds(OnDeleteCascadeInfo odci, String tableName, Object id)
			throws UnifyException {
		Query<? extends Entity> query = Query.of(odci.getChildEntityClass());
		if (odci.isWithChildFkType()) {
			query.addEquals(odci.getChildFkTypeField().getName(), tableName);
		}

		if (odci.isWithChildCat()) {
			query.addEquals(odci.getChildCatField().getName(), odci.getCategory());
		}

		query.addEquals(odci.getChildFkIdField().getName(), id);
		return valueSet(Object.class, "id", query);
	}

	private void deleteChildRecords(OnDeleteCascadeInfo odci, Collection<Object> targetIds) throws UnifyException {
		if (!targetIds.isEmpty()) {
			Query<? extends Entity> query = Query.of(odci.getChildEntityClass());
			query.addAmongst("id", targetIds);
			deleteAll(query);
		}
	}

	private void ensureRecordTenantId(SqlEntityInfo sqlEntityInfo, Entity record) throws UnifyException {
		if (sqlEntityInfo.isWithTenantId()) {
			final String tenantFieldName = sqlEntityInfo.getTenantIdFieldInfo().getName();
			if (DataUtils.getBeanProperty(Long.class, record, tenantFieldName) == null) {
				final Long tenantId = sqlDataSourceDialect.isTenancyEnabled() ? sqlDataSourceDialect.getUserTenantId()
						: Entity.PRIMARY_TENANT_ID;
				DataUtils.setBeanProperty(record, tenantFieldName, tenantId);
			}
		}
	}

	private SqlEntityInfo resolveSqlEntityInfo(Entity record) throws UnifyException {
		return resolveSqlEntityInfo(SqlUtils.getEntityClass(record));
	}

	private SqlEntityInfo resolveSqlEntityInfo(Query<?> query) throws UnifyException {
		return resolveSqlEntityInfo(SqlUtils.getEntityClass(query));
	}

	private SqlEntityInfo resolveSqlEntityInfo(Class<?> clazz) throws UnifyException {
		SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.findSqlEntityInfo(clazz);
		if (sqlEntityInfo.isExtended()) {
			return sqlEntityInfo.getExtensionSqlEntityInfo();
		}

		return sqlEntityInfo;
	}

	private SqlStatementExecutor getSqlStatementExecutor() throws UnifyException {
		if (closed) {
			throw new UnifyException(UnifyCoreErrorConstants.DATASOURCE_SESSION_IS_CLOSED, getDataSourceName());
		}
		return sqlStatementExecutor;
	}
}
