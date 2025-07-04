/*
 * Copyright (c) 2018-2025 The Code Department.
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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.AbstractPool;
import com.tcdng.unify.core.database.AbstractDataSource;
import com.tcdng.unify.core.database.NativeQuery;
import com.tcdng.unify.core.security.Authentication;
import com.tcdng.unify.core.util.SqlUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Abstract SQL data source.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractSqlDataSource extends AbstractDataSource implements SqlDataSource {

    @Configurable
    private String driver;

    @Configurable
    private String connectionUrl;

    @Configurable(resolve = false)
    private Authentication passwordAuthentication;

    @Configurable
    private String appSchema;

    @Configurable
    private String username;

    @Configurable(hidden = true)
    private String password;

    @Configurable("10000")
    private long getConnectionTimeout;

    @Configurable("64")
    private int maxConnections;

    @Configurable("1")
    private int minConnections;

    @Configurable("false")
    private boolean shutdownOnTerminate;

    private SqlConnectionPool sqlConnectionPool;

    public String getDriver() {
        return driver;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    @Override
	public List<SqlEntityInfo> getSqlEntityInfos() throws UnifyException {
		return getDialect().getSqlEntityInfos();
	}

	@Override
    public String getAppSchema() throws UnifyException {
        if(StringUtils.isBlank(appSchema)) {
            return getDialect().getDefaultSchema();
        }
        
        return appSchema;
    }

    @Override
    public List<String> getSchemaList() throws UnifyException {
        Connection connection = getConnection();
        ResultSet rs = null;
        try {
            List<String> schemaList = new ArrayList<String>();
            rs = connection.getMetaData().getSchemas();
            while (rs.next()) {
                schemaList.add(rs.getString("TABLE_SCHEM"));
            }
            return schemaList;
        } catch (SQLException e) {
            throwOperationErrorException(e);
        } finally {
            SqlUtils.close(rs);
            restoreConnection(connection);
        }
        return Collections.emptyList();
    }

    @Override
    public Map<String, SqlTableInfo> getTableMap(String schemaName, SqlTableType sqlTableType) throws UnifyException {
        if (schemaName != null) {
            Map<String, SqlTableInfo> map = new LinkedHashMap<String, SqlTableInfo>();
            for (SqlTableInfo sqlTableInfo : getTableList(schemaName, sqlTableType)) {
                map.put(sqlTableInfo.getTableName(), sqlTableInfo);
            }
            return map;
        }
        return Collections.emptyMap();
    }

    @Override
    public List<SqlTableInfo> getTableList(String schemaName, SqlTableType sqlTableType) throws UnifyException {
        if (schemaName != null) {
            Connection connection = getConnection();
            ResultSet rs = null;
            try {
                List<SqlTableInfo> tableInfoList = new ArrayList<SqlTableInfo>();
                String[] tableType = new String[] { SqlTableType.TABLE.code(), SqlTableType.VIEW.code() };
                if (sqlTableType != null) {
                    tableType = new String[] { sqlTableType.code() };
                }
                rs = connection.getMetaData().getTables(null, schemaName, null, tableType);
                while (rs.next()) {
                    String type = rs.getString("TABLE_TYPE");
                    String tableName = rs.getString("TABLE_NAME");
                    tableInfoList.add(new SqlTableInfo(SqlTableType.fromCode(type), tableName));
                }
                return tableInfoList;
            } catch (SQLException e) {
                throwOperationErrorException(e);
            } finally {
                SqlUtils.close(rs);
                restoreConnection(connection);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<SqlColumnInfo> getColumnList(String schemaName, String tableName) throws UnifyException {
        if (schemaName != null && tableName != null) {
            Connection connection = getConnection();
            ResultSet rs = null;
            try {
                List<SqlColumnInfo> columnInfoList = new ArrayList<SqlColumnInfo>();
                rs = connection.getMetaData().getColumns(null, schemaName, tableName, null);
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    int sqlType = rs.getInt("DATA_TYPE");
                    if (SqlUtils.isSupportedSqlType(sqlType)) {
                        Class<?> type = SqlUtils.getJavaType(sqlType);
                        String defaultVal = rs.getString("COLUMN_DEF");
                        if (defaultVal != null) {
                            defaultVal = defaultVal.trim();
                        }

                        String decimalDigitsStr = rs.getString("DECIMAL_DIGITS");
                        int decimalDigits = decimalDigitsStr == null ? 0 : Integer.valueOf(decimalDigitsStr);
                        columnInfoList.add(new SqlColumnInfo(type, rs.getString("TYPE_NAME"), columnName, defaultVal,
                                sqlType, rs.getInt("COLUMN_SIZE"), decimalDigits,
                                "YES".equals(rs.getString("IS_NULLABLE"))));
                    } else {
                        logDebug(
                                "Column [{0}] of type [{1}] skipped when obtaining column list for table [{2}] in schema [{3}].",
                                columnName, sqlType, tableName, schemaName);
                    }
                }
                return columnInfoList;
            } catch (SQLException e) {
                throwOperationErrorException(e);
            } finally {
                SqlUtils.close(rs);
                restoreConnection(connection);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public Map<String, SqlColumnInfo> getColumnMap(String schemaName, String tableName) throws UnifyException {
        List<SqlColumnInfo> list = getColumnList(schemaName, tableName);
        if (!list.isEmpty()) {
            Map<String, SqlColumnInfo> map = new LinkedHashMap<String, SqlColumnInfo>();
            for (SqlColumnInfo sqlColumnInfo : list) {
                map.put(sqlColumnInfo.getColumnName(), sqlColumnInfo);
            }
            return map;
        }

        return Collections.emptyMap();
    }

    @Override
    public Map<String, SqlColumnInfo> getColumnMapLowerCase(String schemaName, String tableName) throws UnifyException {
        List<SqlColumnInfo> list = getColumnList(schemaName, tableName);
        if (!list.isEmpty()) {
            Map<String, SqlColumnInfo> map = new LinkedHashMap<String, SqlColumnInfo>();
            for (SqlColumnInfo sqlColumnInfo : list) {
                map.put(sqlColumnInfo.getColumnName().toLowerCase(), sqlColumnInfo);
            }
            return map;
        }

        return Collections.emptyMap();
    }

    @Override
    public Set<String> getColumns(String schemaName, String tableName) throws UnifyException {
        if (schemaName != null && tableName != null) {
            Connection connection = getConnection();
            ResultSet rs = null;
            try {
                Set<String> columnNames = new LinkedHashSet<String>();
                rs = connection.getMetaData().getColumns(null, schemaName, tableName, null);
                if (getDialect().isAllObjectsInLowerCase()) {
                    while (rs.next()) {
                        columnNames.add(rs.getString("COLUMN_NAME").toLowerCase());
                    }
                } else {
                    while (rs.next()) {
                        columnNames.add(rs.getString("COLUMN_NAME").toUpperCase());
                    }
                }

                return columnNames;
            } catch (SQLException e) {
                throwOperationErrorException(e);
            } finally {
                SqlUtils.close(rs);
                restoreConnection(connection);
            }
        }

        return Collections.emptySet();
    }

    @Override
    public int testNativeQuery(NativeQuery query) throws UnifyException {
        String nativeSql = getDialect().generateNativeQuery(query);
        return testNativeQuery(nativeSql);
    }

    @Override
    public int testNativeQuery(String nativeSql) throws UnifyException {
        logDebug("Testing native query [{0}]...", nativeSql);
        Connection connection = getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(nativeSql);
            rs = pstmt.executeQuery();
            while (rs.next());
            return rs.getRow();
        } catch (SQLException e) {
            throwOperationErrorException(e);
        } finally {
            SqlUtils.close(rs);
            SqlUtils.close(pstmt);
            restoreConnection(connection);
        }
        logDebug("Native query [{0}] successfully tested.", nativeSql);
        return 0;
    }

    @Override
    public int testNativeUpdate(String nativeSql) throws UnifyException {
        logDebug("Testing uptate query [{0}]...", nativeSql);
        Connection connection = getConnection();
        PreparedStatement pstmt = null;
        int c = 0;
        try {
            pstmt = connection.prepareStatement(nativeSql);
            c = pstmt.executeUpdate();
            connection.rollback();
        } catch (SQLException e) {
            throwOperationErrorException(e);
        } finally {
            SqlUtils.close(pstmt);
            restoreConnection(connection);
        }
        logDebug("Native update [{0}] successfully tested.", nativeSql);
        return c;
    }

    @Override
    public List<Object[]> getRows(NativeQuery query) throws UnifyException {
        String nativeSql = getDialect().generateNativeQuery(query);
        List<Object[]> resultList = new ArrayList<Object[]>();
        Connection connection = getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(nativeSql);
            rs = pstmt.executeQuery();

            int columns = query.columns();
            while (rs.next()) {
                Object[] item = new Object[columns];
                for (int i = 0; i < columns; i++) {
                    item[i] = rs.getObject(i + 1);
                }

                resultList.add(item);
            }
        } catch (SQLException e) {
            throwOperationErrorException(e);
        } finally {
            SqlUtils.close(rs);
            SqlUtils.close(pstmt);
            restoreConnection(connection);
        }
        return resultList;
    }

    @Override
    public SqlDataSourceDialect getDialect() throws UnifyException {
        return (SqlDataSourceDialect) super.getDialect();
    }

    @Override
    public boolean testConnection() throws UnifyException {
        sqlConnectionPool.returnObject(sqlConnectionPool.borrowObject());
        return true;
    }

    @Override
    public Connection getConnection() throws UnifyException {
        return sqlConnectionPool.borrowObject();
    }

    @Override
    public boolean restoreConnection(Connection connection) throws UnifyException {
        return sqlConnectionPool.returnObject(connection);
    }

    @Override
    public boolean restoreConnection(Object connection) throws UnifyException {
        return sqlConnectionPool.returnObject((Connection) connection);
    }

    @Override
    public int getAvailableConnections() throws UnifyException {
        return sqlConnectionPool.available();
    }

    protected void setGetConnectionTimeout(long getConnectionTimeout) {
        this.getConnectionTimeout = getConnectionTimeout;
    }

    protected void setDriver(String driver) {
        this.driver = driver;
    }

    protected void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    protected void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    protected void setMinConnections(int minConnections) {
        this.minConnections = minConnections;
    }

    protected void setAppSchema(String appSchema) {
        this.appSchema = appSchema;
    }

    protected void setShutdownOnTerminate(boolean shutdownOnTerminate) {
        this.shutdownOnTerminate = shutdownOnTerminate;
    }

    protected void setPasswordAuthentication(Authentication passwordAuthentication) {
        this.passwordAuthentication = passwordAuthentication;
    }

    @Override
    protected void onInitialize() throws UnifyException {
        logInfo("Initializing datasource [{0}]...", getName());
        super.onInitialize();
        if (driver != null) {
            doInitConnectionPool();
        }
        logInfo("Initialization of datasource [{0}] completed.", getName());
    }

    @Override
    protected void onTerminate() throws UnifyException {
        logInfo("Terminating datasource [{0}]...", getName());
        if (shutdownOnTerminate) {
            SqlShutdownHook sqlShutdownHook = getDialect().getShutdownHook();
            if (sqlShutdownHook != null) {
                Connection connection = getConnection();
                try {
                    sqlShutdownHook.commandShutdown(connection);
                } finally {
                    restoreConnection(connection);
                }
            }
        }

        if (sqlConnectionPool != null) {
            sqlConnectionPool.terminate();
        }

        super.onTerminate();
        logInfo("Datasource [{0}] terminated.", getName());
    }

    protected void doInitConnectionPool() throws UnifyException {
        try {
            logInfo("Setting up connection pool for [{0}]...", getName());
            Class.forName(driver);
            sqlConnectionPool = createSqlConnectionPool();
            sqlConnectionPool.initialize();
        } catch (ClassNotFoundException e) {
            throw new UnifyException(UnifyCoreErrorConstants.DATASOURCE_MISSING_DRIVER, getName(), driver);
        }
    }

    protected SqlConnectionPool getSqlConnectionPool() {
        return sqlConnectionPool;
    }

    protected Authentication getPasswordAuthentication() {
        return passwordAuthentication;
    }

    protected String getUsername() {
        return username;
    }

    protected String getPassword() {
        return password;
    }

    protected long getGetConnectionTimeout() {
        return getConnectionTimeout;
    }

    protected int getMaxConnections() {
        return maxConnections;
    }

    protected int getMinConnections() {
        return minConnections;
    }

    protected boolean isShutdownOnTerminate() {
        return shutdownOnTerminate;
    }

	private SqlConnectionPool createSqlConnectionPool() throws UnifyException {
		final String prefix = "unify-" + getUnifyComponentContext().getName();
		String xConnectionUrl = System.getenv(prefix + "-url");
		String xUsername = System.getenv(prefix + "-username");
		String xPassword = System.getenv(prefix + "-password");

		if (xConnectionUrl == null) {
			xConnectionUrl = connectionUrl;
		}

		if (xUsername == null) {
			xUsername = !StringUtils.isBlank(username) ? username
					: (passwordAuthentication != null ? passwordAuthentication.getUsername() : null);
		}

		if (xPassword == null) {
			xPassword = !StringUtils.isBlank(password) ? password
					: (passwordAuthentication != null ? passwordAuthentication.getPassword() : null);
		}

		return new SqlConnectionPool(xConnectionUrl, xUsername, xPassword, getConnectionTimeout, minConnections,
				maxConnections);
	}

    protected class SqlConnectionPool extends AbstractPool<Connection> {

        private String connectionURL;

        private String username;

        private String password;

        private String testSql;

        public SqlConnectionPool(String connectionURL, String username, String password, long getTimeout,
                int minObjects, int maxObjects) {
            super(getTimeout, minObjects, maxObjects, true);
            this.connectionURL = connectionURL;
            this.username = username;
            this.password = password;
        }

        @Override
        public void initialize() throws UnifyException {
            testSql = getDialect().generateTestSql();
            super.initialize();
        }

        @Override
        protected Connection createObject(Object... params) throws Exception {
            Connection connection = null;
            if (username != null) {
                connection = DriverManager.getConnection(connectionURL, username, password);
            } else {
                connection = DriverManager.getConnection(connectionURL);
            }
            connection.setAutoCommit(false);
            return connection;
        }

        @Override
        protected void onGetObject(Connection connection, Object... params) throws Exception {
            if (connection.isClosed()) {
                throw new UnifyException(UnifyCoreErrorConstants.DATASOURCE_BAD_CONNECTION, getName());
            }

            // Full connection test
            PreparedStatement pStmt = null;
            ResultSet rs = null;
            try {
                pStmt = connection.prepareStatement(testSql);
                rs = pStmt.executeQuery();
            } finally {
                SqlUtils.close(rs);
                SqlUtils.close(pStmt);
            }
        }

        @Override
        protected void destroyObject(Connection connection) {
            try {
                connection.rollback();
                logDebug("Destroyed connection...");
            } catch (Exception e) {
            } finally {
                SqlUtils.close(connection);
            }
        }
    }
}
