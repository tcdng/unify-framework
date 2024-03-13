/*
 * Copyright 2018-2024 The Code Department.
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

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.MustMatch;
import com.tcdng.unify.core.criterion.AggregateFunction;
import com.tcdng.unify.core.criterion.GroupingFunction;
import com.tcdng.unify.core.database.Aggregation;
import com.tcdng.unify.core.database.CallableProc;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.GroupingAggregation;

/**
 * Used to execute SQL statements with database through connection objects.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface SqlStatementExecutor extends UnifyComponent {

	/**
	 * Executes an update statement via supplied connection. Update statements here
	 * refer to statements that modify the state of the database. Includes CREATE,
	 * UPDATE AND DELETE statements.
	 * 
	 * @param connection   the database connection
	 * @param sqlStatement the update statement to execute
	 * @return the number of record
	 * @throws UnifyException
	 */
	int executeUpdate(Connection connection, SqlStatement sqlStatement) throws UnifyException;

	/**
	 * Executes a statement that returns a single value via supplied connection.
	 * 
	 * @param connection        the database connection
	 * @param clazz             the return value type
	 * @param sqlDataTypePolicy the return value data type policy
	 * @param sqlStatement      the criteria statement object
	 * @param mustMatch         indicates query must match single result
	 * @return the result value
	 * @throws UnifyException if multiple values or if must match and no value
	 *                        matches criteria. If an error occurs
	 */
	<T> T executeSingleObjectResultQuery(Connection connection, Class<T> clazz, SqlDataTypePolicy sqlDataTypePolicy,
			SqlStatement sqlStatement, MustMatch mustMatch) throws UnifyException;

	/**
	 * Executes a statement that returns a single value via supplied connection.
	 * 
	 * @param connection        the database connection
	 * @param clazz             the return value type
	 * @param sqlDataTypePolicy the return value data type policy
	 * @param sqlQuery          the criteria statement
	 * @param mustMatch         indicates query must match single result
	 * @return the result value
	 * @throws UnifyException if multiple values or if must match and no value
	 *                        matches criteria. If an error occurs
	 */
	<T> T executeSingleObjectResultQuery(Connection connection, Class<T> clazz, SqlDataTypePolicy sqlDataTypePolicy,
			String sqlQuery, MustMatch mustMatch) throws UnifyException;

	/**
	 * Executes a statement that returns a list of values via supplied connection.
	 * 
	 * @param connection        the database connection
	 * @param clazz             the return value type
	 * @param sqlDataTypePolicy the return value data type policy
	 * @param sqlStatement      the criteria statement object
	 * @return the resulting list
	 * @throws UnifyException If an error occurs
	 */
	<T> List<T> executeMultipleObjectListResultQuery(Connection connection, Class<T> clazz,
			SqlDataTypePolicy sqlDataTypePolicy, SqlStatement sqlStatement) throws UnifyException;

	/**
	 * Executes a statement that returns a set of values via supplied connection.
	 * 
	 * @param connection        the database connection
	 * @param clazz             the return value type
	 * @param sqlDataTypePolicy the return value data type policy
	 * @param sqlStatement      the criteria statement object
	 * @return the resulting set
	 * @throws UnifyException If an error occurs
	 */
	<T> Set<T> executeMultipleObjectSetResultQuery(Connection connection, Class<T> clazz,
			SqlDataTypePolicy sqlDataTypePolicy, SqlStatement sqlStatement) throws UnifyException;

	/**
	 * Executes a statement that returns a map of key values via supplied
	 * connection.
	 * 
	 * @param connection   the database connection
	 * @param keyClass     the key field type
	 * @param key          the key field name
	 * @param sqlStatement the criteria statement object
	 * @return the map of record
	 * @throws UnifyException if an error occurs
	 */
	<T, U> Map<T, U> executeMultipleObjectMapResultQuery(Connection connection, Class<T> keyClass, String key,
			Class<U> valueClass, String value, SqlStatement sqlStatement) throws UnifyException;

	/**
	 * Executes a statement that returns a map of key list values via supplied
	 * connection.
	 * 
	 * @param connection   the database connection
	 * @param keyClass     the key field type
	 * @param key          the key field name
	 * @param sqlStatement the criteria statement object
	 * @return the map of value lists
	 * @throws UnifyException if an error occurs
	 */
	<T, U> Map<T, List<U>> executeMultipleObjectListMapResultQuery(Connection connection, Class<T> keyClass, String key,
			Class<U> valueClass, String value, SqlStatement sqlStatement) throws UnifyException;

	/**
	 * Executes a statement that returns a single record via supplied connection.
	 * 
	 * @param connection   the database connection
	 * @param sqlStatement the criteria statement object
	 * @param mustMatch    indicates single record must match
	 * @return the result record if found otherwise null
	 * @throws UnifyException if multiple records are found. if must match and no
	 *                        record found. If an error occurs
	 */
	<T extends Entity> T executeSingleRecordResultQuery(Connection connection, SqlStatement sqlStatement,
			MustMatch mustMatch) throws UnifyException;

	/**
	 * Executes a statement that returns a list of record via supplied connection.
	 * 
	 * @param connection   the database connection
	 * @param sqlStatement the criteria statement object
	 * @return the resulting list of record
	 * @throws UnifyException if an error occurs
	 */
	<T extends Entity> List<T> executeMultipleRecordResultQuery(Connection connection, SqlStatement sqlStatement)
			throws UnifyException;

	/**
	 * Executes a statement that returns a map of record by field value via supplied
	 * connection.
	 * 
	 * @param connection   the database connection
	 * @param keyClass     the key field type
	 * @param key          the key field name
	 * @param sqlStatement the criteria statement object
	 * @return the map of record
	 * @throws UnifyException if an error occurs
	 */
	<T, U extends Entity> Map<T, U> executeMultipleRecordResultQuery(Connection connection, Class<T> keyClass,
			String key, SqlStatement sqlStatement) throws UnifyException;

	/**
	 * Executes a statement that returns a map of record list by field value via
	 * supplied connection.
	 * 
	 * @param connection   the database connection
	 * @param keyClass     the key field type
	 * @param key          the key field name
	 * @param sqlStatement the criteria statement object
	 * @return the map of record list
	 * @throws UnifyException if an error occurs
	 */
	<T, U extends Entity> Map<T, List<U>> executeMultipleRecordListResultQuery(Connection connection, Class<T> keyClass,
			String key, SqlStatement sqlStatement) throws UnifyException;

	/**
	 * Executes an aggregate statement that returns a single aggregate via supplied
	 * connection.
	 * 
	 * @param aggregateFunction      the aggregate function.
	 * @param connection             the database connection
	 * @param countSqlDataTypePolicy count field SQL data type policy
	 * @param sqlStatement           the criteria statement object
	 * @return aggregate result
	 * @throws UnifyException if an error occurs
	 */
	Aggregation executeSingleAggregateResultQuery(AggregateFunction aggregateFunction, Connection connection,
			SqlDataTypePolicy countSqlDataTypePolicy, SqlStatement sqlStatement) throws UnifyException;

	/**
	 * Executes an aggregate statement that returns an aggregate list via supplied
	 * connection.
	 * 
	 * @param aggregateFunctionList  the aggregate function list
	 * @param connection             the database connection
	 * @param countSqlDataTypePolicy count field SQL data type policy
	 * @param sqlStatement           the criteria statement object
	 * @return list of aggregate result
	 * @throws UnifyException if an error occurs
	 */
	List<Aggregation> executeMultipleAggregateResultQuery(List<AggregateFunction> aggregateFunctionList,
			Connection connection, SqlDataTypePolicy countSqlDataTypePolicy, SqlStatement sqlStatement)
			throws UnifyException;

	/**
	 * Executes an aggregate statement that returns a single aggregate via supplied
	 * connection.
	 * 
	 * @param aggregateFunction      the aggregate function.
	 * @param connection             the database connection
	 * @param countSqlDataTypePolicy count field SQL data type policy
	 * @param mergeSqlDataTypePolicy merge field SQL data type policy
	 * @param sqlStatement           the criteria statement object
	 * @return aggregate result
	 * @throws UnifyException if an error occurs
	 */
	List<GroupingAggregation> executeSingleAggregateResultQuery(AggregateFunction aggregateFunction,
			List<GroupingFunction> groupingFunction, Connection connection, SqlDataTypePolicy countSqlDataTypePolicy,
			SqlDataTypePolicy mergeSqlDataTypePolicy, SqlStatement sqlStatement) throws UnifyException;

	/**
	 * Executes an aggregate statement that returns an aggregate list via supplied
	 * connection.
	 * 
	 * @param aggregateFunctionList  the aggregate function list
	 * @param connection             the database connection
	 * @param countSqlDataTypePolicy count field SQL data type policy
	 * @param mergeSqlDataTypePolicy merge field SQL data type policy
	 * @param sqlStatement           the criteria statement object
	 * @return list of aggregate result
	 * @throws UnifyException if an error occurs
	 */
	List<GroupingAggregation> executeMultipleAggregateResultQuery(List<AggregateFunction> aggregateFunctionList,
			List<GroupingFunction> groupingFunction, Connection connection, SqlDataTypePolicy countSqlDataTypePolicy,
			SqlDataTypePolicy mergeSqlDataTypePolicy, SqlStatement sqlStatement) throws UnifyException;

	/**
	 * Executes a callable statement.
	 * 
	 * @param connection           the database connection
	 * @param callableProc         the callable procedure object which may have its
	 *                             output parameter fields set.
	 * @param sqlCallableStatement the callable statement to execute
	 * @throws UnifyException if an error occurs
	 */
	void executeCallable(Connection connection, CallableProc callableProc, SqlCallableStatement sqlCallableStatement)
			throws UnifyException;

	/**
	 * Executes a callable statement with results.
	 * 
	 * @param connection           the database connection
	 * @param callableProc         the callable procedure object which may have its
	 *                             output parameter fields set.
	 * @param sqlCallableStatement the callable statement to execute
	 * @return map of result items by type
	 * @throws UnifyException if an error occurs
	 */
	Map<Class<?>, List<?>> executeCallableWithResults(Connection connection, CallableProc callableProc,
			SqlCallableStatement sqlCallableStatement) throws UnifyException;

}
