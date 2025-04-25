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

/**
 * Dynamic SQl data source configuration.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DynamicSqlDataSourceConfig {

    private String preferredName;

    private String name;

    private String dialect;

    private String driver;

    private String connectionUrl;

    private String dbSchema;

    private String dbUsername;

    private String dbPassword;

    private int maxConnection;

    private boolean shutdownOnTerminate;

    private boolean manageSchema;
    
    public DynamicSqlDataSourceConfig(String name, String dialect, String driver, String connectionUrl, String dbSchema,
            String dbUsername, String dbPassword, int maxConnection, boolean shutdownOnTerminate) {
        this(null, name, dialect, driver, connectionUrl, dbSchema, dbUsername, dbPassword, maxConnection,
                shutdownOnTerminate);
    }

    public DynamicSqlDataSourceConfig(String preferredName, String name, String dialect, String driver,
            String connectionUrl, String dbSchema, String dbUsername, String dbPassword, int maxConnection,
            boolean shutdownOnTerminate) {
        this.preferredName = preferredName;
        this.name = name;
        this.dialect = dialect;
        this.driver = driver;
        this.connectionUrl = connectionUrl;
        this.dbSchema = dbSchema;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.maxConnection = maxConnection;
        this.shutdownOnTerminate = shutdownOnTerminate;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public String getName() {
        return name;
    }

    public String getDialect() {
        return dialect;
    }

    public String getDriver() {
        return driver;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public String getDbSchema() {
        return dbSchema;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public int getMaxConnection() {
        return maxConnection;
    }

    public boolean isShutdownOnTerminate() {
        return shutdownOnTerminate;
    }

    public boolean isManageSchema() {
        return manageSchema;
    }

    public void setManageSchema(boolean manageSchema) {
        this.manageSchema = manageSchema;
    }
}
