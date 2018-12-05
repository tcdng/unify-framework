/*
 * Copyright 2014 The Code Department
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.data.AggregateType;
import com.tcdng.unify.core.database.DataSourceDialect;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.operation.Operator;
import com.tcdng.unify.core.operation.Update;

/**
 * SQL data source dialect component. Used to generate native SQL, maintain SQL
 * statement information and provide SQL policy objects.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface SqlDataSourceDialect extends DataSourceDialect, SqlGenerator {

	/**
	 * Returns the SQL policy for a data type.
	 * 
	 * @param clazz
	 *            the data type
	 * @throws UnifyException
	 *             if type is not supported. If an error occurs
	 */
	SqlDataTypePolicy getSqlTypePolicy(Class<?> clazz) throws UnifyException;

	/**
	 * Returns the SQL policy for a column type.
	 * 
	 * @param columnType
	 *            the column type
	 * @throws UnifyException
	 *             if type is not supported. If an error occurs
	 */
	SqlDataTypePolicy getSqlTypePolicy(ColumnType columnType) throws UnifyException;

	/**
	 * Returns the SQL criteria policy for an operator.
	 * 
	 * @param operator
	 *            the operator
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlCriteriaPolicy getSqlCriteriaPolicy(Operator operator) throws UnifyException;

	/**
	 * Returns the SQL entity information for an entity.
	 * 
	 * @param clazz
	 *            the entity type
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlEntityInfo getSqlEntityInfo(Class<?> clazz) throws UnifyException;

	/**
	 * Returns data source initialization statements.
	 * 
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlStatement[] prepareDataSourceInitStatements() throws UnifyException;

	/**
	 * Prepares find record by primary key statement.
	 * 
	 * @param clazz
	 *            the record type
	 * @param pk
	 *            the primary key
	 * @return the find by primary key statement. Should be restored after use by
	 *         invoking {@link #restoreStatement(Class, SqlStatement)}
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlStatement prepareFindByPkStatement(Class<?> clazz, Object pk) throws UnifyException;

	/**
	 * Prepares find record by primary key and version number statement.
	 * 
	 * @param clazz
	 *            the record type
	 * @param pk
	 *            the primary key
	 * @param versionNo
	 *            the version number
	 * @return the find by primary key and version statement. Should be restored
	 *         after use by invoking {@link #restoreStatement(Class, SqlStatement)}
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlStatement prepareFindByPkVersionStatement(Class<?> clazz, Object pk, Object versionNo) throws UnifyException;

	/**
	 * Prepares find record by criteria statement.
	 * 
	 * @param query
	 *            the query object
	 * @return the find statement
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlStatement prepareFindStatement(Query<? extends Entity> query) throws UnifyException;

	/**
	 * Prepares list record by primary key statement.
	 * 
	 * @param clazz
	 *            the record type
	 * @param pk
	 *            the primary key
	 * @return the list by primary key statement. Should be restored after use by
	 *         invoking {@link #restoreStatement(Class, SqlStatement)}
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlStatement prepareListByPkStatement(Class<?> clazz, Object pk) throws UnifyException;

	/**
	 * Prepares list record by primary key and version number statement.
	 * 
	 * @param clazz
	 *            the record type
	 * @param pk
	 *            the primary key
	 * @param versionNo
	 *            the version number
	 * @return the list by primary key and version statement. Should be restored
	 *         after use by invoking {@link #restoreStatement(Class, SqlStatement)}
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlStatement prepareListByPkVersionStatement(Class<?> clazz, Object pk, Object versionNo) throws UnifyException;

	/**
	 * Prepares list record by criteria statement.
	 * 
	 * @param query
	 *            the query object
	 * @return the list statement.
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlStatement prepareListStatement(final Query<? extends Entity> query) throws UnifyException;

	/**
	 * Prepares create record statement.
	 * 
	 * @param record
	 *            the record to create
	 * @return the create statement. Should be restored after use by invoking
	 *         {@link #restoreStatement(Class, SqlStatement)}
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlStatement prepareCreateStatement(Entity record) throws UnifyException;

	/**
	 * Prepares update record by primary key statement.
	 * 
	 * @param record
	 *            the record to update
	 * @return the update by primary key statement. Should be restored after use by
	 *         invoking {@link #restoreStatement(Class, SqlStatement)}
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlStatement prepareUpdateByPkStatement(Entity record) throws UnifyException;

	/**
	 * Prepares update record by primary key and version number statement.
	 * 
	 * @param record
	 *            the record to update
	 * @param oldVersionNo
	 *            the old version number
	 * @return the update by primary key and version statement. Should be restored
	 *         after use by invoking {@link #restoreStatement(Class, SqlStatement)}
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlStatement prepareUpdateByPkVersionStatement(Entity record, Object oldVersionNo) throws UnifyException;

	/**
	 * Prepares update record by criteria statement.
	 * 
	 * @param query
	 *            the criteria object
	 * @param update
	 *            the update information
	 * @return the update by query statement.
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlStatement prepareUpdateStatement(Query<? extends Entity> query, Update update) throws UnifyException;

	/**
	 * Prepares update record by id.
	 * 
	 * @param clazz
	 *            the record type
	 * @param pk
	 *            the primary key
	 * @param update
	 *            the update information
	 * @return the update by query statement.
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlStatement prepareUpdateStatement(Class<?> clazz, Object pk, Update update) throws UnifyException;

	/**
	 * Prepares delete record by primary key statement.
	 * 
	 * @param clazz
	 *            the record type
	 * @param pk
	 *            the primary key
	 * @return the delete by primary key statement. Should be restored after use by
	 *         invoking {@link #restoreStatement(Class, SqlStatement)}
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlStatement prepareDeleteByPkStatement(Class<?> clazz, Object pk) throws UnifyException;

	/**
	 * Prepares delete record by primary key and version number statement.
	 * 
	 * @param clazz
	 *            the record type
	 * @param pk
	 *            the primary key
	 * @param versionNo
	 *            the version number
	 * @return the delete by primary key and version statement. Should be restored
	 *         after use by invoking {@link #restoreStatement(Class, SqlStatement)}
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlStatement prepareDeleteByPkVersionStatement(Class<?> clazz, Object pk, Object versionNo) throws UnifyException;

	/**
	 * Prepares delete record by criteria statement.
	 * 
	 * @param query
	 *            the record query
	 * @return the delete statement.
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlStatement prepareDeleteStatement(Query<? extends Entity> query) throws UnifyException;

	/**
	 * Prepares count record statement.
	 * 
	 * @param query
	 *            the record criteria
	 * @return the count statement.
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlStatement prepareCountStatement(Query<? extends Entity> query) throws UnifyException;

	/**
	 * Prepares an aggregate field statement.
	 * 
	 * @param aggregateType
	 *            the aggregate type
	 * @param query
	 *            the aggregation query
	 * @return the aggregate statement
	 */
	SqlStatement prepareAggregateStatement(AggregateType aggregateType, Query<? extends Entity> query)
			throws UnifyException;

	/**
	 * Returns the maximum number of values the data source would accept for
	 * multi-value conditions.
	 * 
	 * @return the maximum number. Zero or negative value if there is no limit
	 */
	int getMaxClauseValues();

	/**
	 * Returns true if there's a query limit or offset
	 * 
	 * @param query
	 *            the query to check
	 * @throws UnifyException
	 *             if an error occurs
	 */
	boolean isQueryOffsetOrLimit(Query<? extends Entity> query) throws UnifyException;

	/**
	 * Restores a statement info. This applies to implementations that maintain a
	 * statement info pool.
	 * 
	 * @param sqlStatement
	 *            the statement info to restore
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void restoreStatement(SqlStatement sqlStatement) throws UnifyException;

	/**
	 * Returns the data source shutdown hook.
	 * 
	 * @throws UnifyException
	 *             if an error occurs
	 */
	SqlShutdownHook getShutdownHook() throws UnifyException;
}
