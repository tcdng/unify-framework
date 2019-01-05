/*
 * Copyright 2018 The Code Department
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.DataSource;
import com.tcdng.unify.core.database.NativeQuery;

/**
 * Interface for an SQL data source.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface SqlDataSource extends DataSource {

    /**
     * Gets the data source primary application schema.
     * 
     * @return the primary schema name
     * @throws UnifyException if an error occurs
     */
    String getApplicationSchema() throws UnifyException;

    /**
     * Gets a list of schemas in this data source.
     * 
     * @return a list of schema names
     * @throws UnifyException
     *             if an error occurs
     */
    List<String> getSchemaList() throws UnifyException;

    /**
     * Returns a list information on tables that belong to supplied schema.
     * 
     * @param schemaName
     *            the name of schema to check
     * @param sqlTableType
     *            the optional table type
     * @return list of table information. Empty list is returned if schemaName is
     *         null.
     * @throws UnifyException
     *             if an error occurs
     */
    List<SqlTableInfo> getTableList(String schemaName, SqlTableType sqlTableType) throws UnifyException;

    /**
     * Returns a map of table information by table name.
     * 
     * @param schemaName
     *            the name of schema to check
     * @param sqlTableType
     *            the optional table type
     * @return map of table information. Empty map is returned if schemaName is
     *         null.
     * @throws UnifyException
     *             if an error occurs
     */
    Map<String, SqlTableInfo> getTableMap(String schemaName, SqlTableType sqlTableType) throws UnifyException;

    /**
     * Returns a list information on columns that belong to specified table in a
     * particular schema.
     * 
     * @param schemaName
     *            the schema name
     * @param tableName
     *            the table name
     * @return list of column information. Empty list is returned if schemaName or
     *         tableName is null.
     * @throws UnifyException
     *             if an error occurs
     */
    List<SqlColumnInfo> getColumnList(String schemaName, String tableName) throws UnifyException;

    /**
     * Returns a map of column information by column name.
     * 
     * @param schemaName
     *            the schema name
     * @param tableName
     *            the table name
     * @return map of column information. Empty map is returned if schemaName or
     *         tableName is null.
     * @throws UnifyException
     *             if an error occurs
     */
    Map<String, SqlColumnInfo> getColumnMap(String schemaName, String tableName) throws UnifyException;

    /**
     * Executes supplied native query and returns rows.
     * 
     * @param query
     *            the native query to run
     * @return a list of rows. A row is represented by an array of objects in
     *         sequence determined by column sequence in native query.
     * @throws UnifyException
     *             if an error occurs
     */
    List<Object[]> getRows(NativeQuery query) throws UnifyException;

    /**
     * Returns the data source dialect.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    SqlDataSourceDialect getDialect() throws UnifyException;

    /**
     * Returns an SQL connection object from data source.
     * 
     * @return Object - the connection
     * @throws UnifyException
     *             if there is no available connection. If some other error occurs
     */
    Connection getConnection() throws UnifyException;

    /**
     * Restores an SQL connection.
     * 
     * @param connection
     *            the connection to restore
     * @return true if connection is restored.
     * @throws UnifyException
     *             if supplied connection object did not originate from this data
     *             source. if an error occurs.
     */
    boolean restoreConnection(Connection connection) throws UnifyException;

    /**
     * Tests connection to data source
     *
     * @throws UnifyException
     *             if an error occurs
     */
    boolean testConnection() throws UnifyException;

    /**
     * Tests a native query.
     * 
     * @param query
     *            the native query
     * @return the result count
     * @throws UnifyException
     *             if an error occurs
     */
    int testNativeQuery(NativeQuery query) throws UnifyException;

    /**
     * Tests a native query.
     * 
     * @param nativeSql
     *            the native SQL
     * @return the result count
     * @throws UnifyException
     *             if an error occurs
     */
    int testNativeQuery(String nativeSql) throws UnifyException;

    /**
     * Tests a native update.
     * 
     * @param nativeSql
     *            the native SQL
     * @return the update count
     * @throws UnifyException
     *             if an error occurs
     */
    int testNativeUpdate(String nativeSql) throws UnifyException;
}
