/*
 * Copyright 2018-2020 The Code Department.
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
package com.tcdng.unify.core.database.sql.dynamic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.database.NativeQuery;
import com.tcdng.unify.core.database.dynamic.DynamicEntityInfo;
import com.tcdng.unify.core.database.sql.SqlColumnInfo;
import com.tcdng.unify.core.database.sql.SqlDataSource;
import com.tcdng.unify.core.database.sql.SqlTableInfo;
import com.tcdng.unify.core.database.sql.SqlTableType;

/**
 * Default implementation of dynamic SQL data source manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER)
public class DynamicSqlDataSourceManagerImpl extends AbstractUnifyComponent implements DynamicSqlDataSourceManager {

    private FactoryMap<String, DynamicSqlDataSource> dynamicSqlDataSoureMap;

    public DynamicSqlDataSourceManagerImpl() {
        dynamicSqlDataSoureMap = new FactoryMap<String, DynamicSqlDataSource>() {
            @Override
            protected DynamicSqlDataSource create(String key, Object... params) throws Exception {
                return newDynamicSqlDataSource((DynamicSqlDataSourceConfig) params[0]);
            }
        };
    }

    @Override
    public void configure(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig) throws UnifyException {
        String dataSourceConfigName = dynamicSqlDataSourceConfig.getName();
        if (dynamicSqlDataSoureMap.isKey(dataSourceConfigName)) {
            throw new UnifyException(UnifyCoreErrorConstants.DYNAMIC_DATASOURCE_ALREADY_CONFIGURED, dataSourceConfigName);
        }

        dynamicSqlDataSoureMap.get(dataSourceConfigName, dynamicSqlDataSourceConfig);
    }

    @Override
    public boolean reconfigure(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig) throws UnifyException {
        String dataSourceConfigName = dynamicSqlDataSourceConfig.getName();
        if (dynamicSqlDataSoureMap.isKey(dataSourceConfigName)) {
            dynamicSqlDataSoureMap.get(dataSourceConfigName).reconfigure(dynamicSqlDataSourceConfig);
            return true;
        }

        return false;
    }

    @Override
    public boolean testConfiguration(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig) throws UnifyException {
        DynamicSqlDataSource dynamicSqlDataSource = newDynamicSqlDataSource(dynamicSqlDataSourceConfig);
        try {
            return dynamicSqlDataSource.testConnection();
        } finally {
            dynamicSqlDataSource.terminate();
        }
    }

    @Override
    public int testNativeQuery(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig, NativeQuery query)
            throws UnifyException {
        DynamicSqlDataSource dynamicSqlDataSource = newDynamicSqlDataSource(dynamicSqlDataSourceConfig);
        try {
            return dynamicSqlDataSource.testNativeQuery(query);
        } finally {
            dynamicSqlDataSource.terminate();
        }
    }

    @Override
    public int testNativeQuery(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig, String nativeSql)
            throws UnifyException {
        DynamicSqlDataSource dynamicSqlDataSource = newDynamicSqlDataSource(dynamicSqlDataSourceConfig);
        try {
            return dynamicSqlDataSource.testNativeQuery(nativeSql);
        } finally {
            dynamicSqlDataSource.terminate();
        }
    }

    @Override
    public int testNativeUpdate(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig, String updateSql)
            throws UnifyException {
        DynamicSqlDataSource dynamicSqlDataSource = newDynamicSqlDataSource(dynamicSqlDataSourceConfig);
        try {
            return dynamicSqlDataSource.testNativeUpdate(updateSql);
        } finally {
            dynamicSqlDataSource.terminate();
        }
    }

    @Override
    public boolean isConfigured(String dataSourceConfigName) throws UnifyException {
        if (dynamicSqlDataSoureMap.isKey(dataSourceConfigName)) {
            return dynamicSqlDataSoureMap.get(dataSourceConfigName).isConfigured();
        }
        return false;
    }

    @Override
    public int getDataSourceCount() throws UnifyException {
        return dynamicSqlDataSoureMap.size();
    }

    @Override
    public List<String> getSchemas(String dataSourceConfigName) throws UnifyException {
        return getDynamicSqlDataSource(dataSourceConfigName).getSchemaList();
    }

    @Override
    public List<SqlTableInfo> getTables(String dataSourceConfigName, String schemaName, SqlTableType sqlTableType)
            throws UnifyException {
        return getDynamicSqlDataSource(dataSourceConfigName).getTableList(schemaName, sqlTableType);
    }

    @Override
    public List<SqlColumnInfo> getColumns(String dataSourceConfigName, String schemaName, String tableName)
            throws UnifyException {
        return getDynamicSqlDataSource(dataSourceConfigName).getColumnList(schemaName, tableName);
    }

    @Override
    public List<Object[]> getRows(String dataSourceConfigName, NativeQuery query) throws UnifyException {
        return getDynamicSqlDataSource(dataSourceConfigName).getRows(query);
    }

    @Override
    public SqlDataSource getDataSource(String dataSourceConfigName) throws UnifyException {
        return getDynamicSqlDataSource(dataSourceConfigName);
    }

    @Override
    public void createOrUpdateDynamicEntitySchemaObjects(String dataSourceConfigName,
            List<DynamicEntityInfo> dynamicEntityInfoList) throws UnifyException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Connection getConnection(String dataSourceConfigName) throws UnifyException {
        return getDynamicSqlDataSource(dataSourceConfigName).getConnection();
    }

    @Override
    public boolean restoreConnection(String dataSourceConfigName, Connection connection) throws UnifyException {
        return getDynamicSqlDataSource(dataSourceConfigName).restoreConnection(connection);
    }

    @Override
    public void terminateConfiguration(String dataSourceConfigName) throws UnifyException {
        DynamicSqlDataSource dynamicSqlDataSource = getDynamicSqlDataSource(dataSourceConfigName);
        try {
            dynamicSqlDataSource.terminate();
        } finally {
            dynamicSqlDataSoureMap.remove(dataSourceConfigName);
        }
    }

    @Override
    public void terminateAll() throws UnifyException {
        for (String dataSourceConfigName : new ArrayList<String>(dynamicSqlDataSoureMap.keySet())) {
            terminateConfiguration(dataSourceConfigName);
        }
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {
        terminateAll();
    }

    private DynamicSqlDataSource getDynamicSqlDataSource(String dataSourceConfigName) throws UnifyException {
        if (!dynamicSqlDataSoureMap.isKey(dataSourceConfigName)) {
            throw new UnifyException(UnifyCoreErrorConstants.DYNAMIC_DATASOURCE_IS_UNKNOWN, dataSourceConfigName);
        }

        return dynamicSqlDataSoureMap.get(dataSourceConfigName);
    }

    private DynamicSqlDataSource newDynamicSqlDataSource(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig)
            throws UnifyException {
        DynamicSqlDataSource dynamicSqlDataSource =
                (DynamicSqlDataSource) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCE);
        dynamicSqlDataSource.configure(dynamicSqlDataSourceConfig);
        return dynamicSqlDataSource;
    }
}
