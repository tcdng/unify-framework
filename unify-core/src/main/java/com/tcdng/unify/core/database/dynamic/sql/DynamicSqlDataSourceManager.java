/*
 * Copyright 2018-2025 The Code Department.
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
package com.tcdng.unify.core.database.dynamic.sql;

import java.sql.Connection;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.NativeQuery;
import com.tcdng.unify.core.database.sql.SqlColumnInfo;
import com.tcdng.unify.core.database.sql.SqlDataSource;
import com.tcdng.unify.core.database.sql.SqlDataSourceManager;
import com.tcdng.unify.core.database.sql.SqlTableInfo;
import com.tcdng.unify.core.database.sql.SqlTableType;

/**
 * Dynamic SQL data source manager.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface DynamicSqlDataSourceManager extends SqlDataSourceManager {

    /**
     * Configures data source using supplied configuration.
     * 
     * @param dynamicSqlDataSourceConfig
     *            the configuration to use
     * @throws UnifyException
     *             if data source is already configured. If an error occurs
     */
    void configure(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig) throws UnifyException;

    /**
     * Reconfigures data source if exists using supplied configuration.
     * 
     * @param dynamicSqlDataSourceConfig
     *            the configuration to use
     * @return true if data source is existing and is reconfigured
     * @throws UnifyException
     *             if an error occurs
     */
    boolean reconfigure(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig) throws UnifyException;

    /**
     * Returns true if data source is configured.
     * 
     * @param dataSourceConfigName
     *            the data source configuration name
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isConfigured(String dataSourceConfigName) throws UnifyException;

    /**
     * Returns the number of data sources currently being managed.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    int getDataSourceCount() throws UnifyException;

    /**
     * Returns a list of schemas in data source.
     * 
     * @param dataSourceConfigName
     *            the data source configuration name
     * @throws UnifyException
     *             if configuration is unknown. if an error occurs
     */
    List<String> getSchemas(String dataSourceConfigName) throws UnifyException;

    /**
     * Returns a list information on tables that belong to supplied schema.
     * 
     * @param dataSourceConfigName
     *            the data source configuration name
     * @param schemaName
     *            the name of schema to check
     * @param sqlTableType
     *            the table type
     * @return list of table information. Empty list is returned if schemaName is
     *         null.
     * @throws UnifyException
     *             if configuration is unknown. if an error occurs
     */
    List<SqlTableInfo> getTables(String dataSourceConfigName, String schemaName, SqlTableType sqlTableType)
            throws UnifyException;

    /**
     * Returns a list information on columns that belong to specified table in a
     * particular schema.
     * 
     * @param dataSourceConfigName
     *            the data source configuration name
     * @param schemaName
     *            the schema name
     * @param tableName
     *            the table name
     * @return list of column information. Empty list is returned if schemaName or
     *         tableName is null.
     * @throws UnifyException
     *             if configuration is unknown. if an error occurs
     */
    List<SqlColumnInfo> getColumns(String dataSourceConfigName, String schemaName, String tableName)
            throws UnifyException;

    /**
     * Executes supplied native query and returns rows.
     * 
     * @param dataSourceConfigName
     *            the data source configuration name
     * @param query
     *            the native query to run
     * @return a list of rows. A row is represented by an array of objects in
     *         sequence determined by column sequence in native query.
     * @throws UnifyException
     *             if an error occurs
     */
    List<Object[]> getRows(String dataSourceConfigName, NativeQuery query) throws UnifyException;

    /**
     * Returns dynamic data source for supplied configuration.
     * 
     * @param dataSourceConfigName
     *            the data source configuration name
     * @throws UnifyException
     *             if an error occurs
     */
    SqlDataSource getDataSource(String dataSourceConfigName) throws UnifyException;

    /**
     * Gets a connection object from configured data source connection pool.
     * 
     * @param dataSourceConfigName
     *            the data source configuration name
     * @throws UnifyException
     *             if data source with supplied configuration name is not
     *             configured. If an error occurs
     */
    Connection getConnection(String dataSourceConfigName) throws UnifyException;

    /**
     * Restores connection to configured data source connection pool.
     * 
     * @param dataSourceConfigName
     *            the data source configuration name
     * @param connection
     *            the connection to restore
     * @return a true value if connection was restored to the right data source
     * @throws UnifyException
     *             if data source with supplied configuration name is not
     *             configured. If an error occurs
     */
    boolean restoreConnection(String dataSourceConfigName, Connection connection) throws UnifyException;

    /**
     * Terminates configuration from this dynamic SQl data source manager.
     * 
     * @param dataSourceConfigName
     *            the data source configuration name
     * @throws UnifyException
     *             if data source with supplied configuration name is not
     *             configured. If an error occurs
     */
    void terminateConfiguration(String dataSourceConfigName) throws UnifyException;

    /**
     * Terminates all configuration from this dynamic SQl data source manager.
     * 
     * @throws UnifyException
     *             If an error occurs
     */
    void terminateAll() throws UnifyException;

    /**
     * Tests a runtime data source configuration.
     * 
     * @param dynamicSqlDataSourceConfig
     *            the configuration to test.
     * @return true if test was successful
     * @throws UnifyException
     *             if an error occurs
     */
    boolean testConfiguration(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig) throws UnifyException;

    /**
     * Tests a runtime data source native query.
     * 
     * @param dynamicSqlDataSourceConfig
     *            the data source configuration.
     * @param query
     *            the native query object
     * @return the result count
     * @throws UnifyException
     *             if an error occurs
     */
    int testNativeQuery(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig, NativeQuery query) throws UnifyException;

    /**
     * Tests a runtime data source native query.
     * 
     * @param dynamicSqlDataSourceConfig
     *            the datasource configuration.
     * @param nativeSql
     *            the native query SQL
     * @return the result count
     * @throws UnifyException
     *             if an error occurs
     */
    int testNativeQuery(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig, String nativeSql) throws UnifyException;

    /**
     * Tests a runtime data source native update.
     * 
     * @param dynamicSqlDataSourceConfig
     *            the datasource configuration.
     * @param updateSql
     *            the native update SQL
     * @return the update count
     * @throws UnifyException
     *             if an error occurs
     */
    int testNativeUpdate(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig, String updateSql) throws UnifyException;

}
