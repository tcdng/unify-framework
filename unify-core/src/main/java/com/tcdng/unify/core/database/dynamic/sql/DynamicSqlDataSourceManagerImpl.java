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
package com.tcdng.unify.core.database.dynamic.sql;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.constant.ForceConstraints;
import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.database.DataSourceEntityContext;
import com.tcdng.unify.core.database.DataSourceEntityListProvider;
import com.tcdng.unify.core.database.DataSourceManagerContext;
import com.tcdng.unify.core.database.DataSourceManagerOptions;
import com.tcdng.unify.core.database.NativeQuery;
import com.tcdng.unify.core.database.sql.AbstractSqlDataSourceManager;
import com.tcdng.unify.core.database.sql.SqlColumnInfo;
import com.tcdng.unify.core.database.sql.SqlDataSource;
import com.tcdng.unify.core.database.sql.SqlTableInfo;
import com.tcdng.unify.core.database.sql.SqlTableType;

/**
 * Default implementation of dynamic SQL data source manager.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER)
public class DynamicSqlDataSourceManagerImpl extends AbstractSqlDataSourceManager
        implements DynamicSqlDataSourceManager {

	@Configurable(ApplicationComponents.APPLICATION_DATASOURCE_ENTITYLIST_PROVIDER)
	private DataSourceEntityListProvider entityListProvider;

	private FactoryMap<String, DynamicSqlDataSource> dynamicSqlDataSourceMap;

    public DynamicSqlDataSourceManagerImpl() {
        dynamicSqlDataSourceMap = new FactoryMap<String, DynamicSqlDataSource>() {
            @Override
            protected DynamicSqlDataSource create(String key, Object... params) throws Exception {
                return getNewDynamicSqlDataSource((DynamicSqlDataSourceConfig) params[0]);
            }
        };
    }

    @Override
    public synchronized void configure(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig) throws UnifyException {
        if (dynamicSqlDataSourceMap.isKey(dynamicSqlDataSourceConfig.getName())) {
            throw new UnifyException(UnifyCoreErrorConstants.DYNAMIC_DATASOURCE_ALREADY_CONFIGURED,
                    dynamicSqlDataSourceConfig.getName());
        }

        createAndInitDynamicSqlDataSource(dynamicSqlDataSourceConfig);
    }

    @Override
    public synchronized boolean reconfigure(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig)
            throws UnifyException {
        if (dynamicSqlDataSourceMap.remove(dynamicSqlDataSourceConfig.getName()) != null) {
            createAndInitDynamicSqlDataSource(dynamicSqlDataSourceConfig);
            return true;
        }

        return false;
    }

    @Override
    public boolean testConfiguration(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig) throws UnifyException {
        DynamicSqlDataSource dynamicSqlDataSource = getNewDynamicSqlDataSource(dynamicSqlDataSourceConfig);
        try {
            return dynamicSqlDataSource.testConnection();
        } finally {
            dynamicSqlDataSource.terminate();
        }
    }

    @Override
    public int testNativeQuery(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig, NativeQuery query)
            throws UnifyException {
        DynamicSqlDataSource dynamicSqlDataSource = getNewDynamicSqlDataSource(dynamicSqlDataSourceConfig);
        try {
            return dynamicSqlDataSource.testNativeQuery(query);
        } finally {
            dynamicSqlDataSource.terminate();
        }
    }

    @Override
    public int testNativeQuery(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig, String nativeSql)
            throws UnifyException {
        DynamicSqlDataSource dynamicSqlDataSource = getNewDynamicSqlDataSource(dynamicSqlDataSourceConfig);
        try {
            return dynamicSqlDataSource.testNativeQuery(nativeSql);
        } finally {
            dynamicSqlDataSource.terminate();
        }
    }

    @Override
    public int testNativeUpdate(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig, String updateSql)
            throws UnifyException {
        DynamicSqlDataSource dynamicSqlDataSource = getNewDynamicSqlDataSource(dynamicSqlDataSourceConfig);
        try {
            return dynamicSqlDataSource.testNativeUpdate(updateSql);
        } finally {
            dynamicSqlDataSource.terminate();
        }
    }

    @Override
    public boolean isConfigured(String dataSourceConfigName) throws UnifyException {
        if (dynamicSqlDataSourceMap.isKey(dataSourceConfigName)) {
            return dynamicSqlDataSourceMap.get(dataSourceConfigName).isConfigured();
        }
        return false;
    }

    @Override
    public int getDataSourceCount() throws UnifyException {
        return dynamicSqlDataSourceMap.size();
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
    public Connection getConnection(String dataSourceConfigName) throws UnifyException {
        return getDynamicSqlDataSource(dataSourceConfigName).getConnection();
    }

    @Override
    public boolean restoreConnection(String dataSourceConfigName, Connection connection) throws UnifyException {
        return getDynamicSqlDataSource(dataSourceConfigName).restoreConnection(connection);
    }

    @Override
    public void terminateConfiguration(String dataSourceConfigName) throws UnifyException {
        SqlDataSource sqlDataSource = getDynamicSqlDataSource(dataSourceConfigName);
        try {
            sqlDataSource.terminate();
        } finally {
            dynamicSqlDataSourceMap.remove(dataSourceConfigName);
        }
    }

    @Override
    public void terminateAll() throws UnifyException {
        for (String dataSourceConfigName : new ArrayList<String>(dynamicSqlDataSourceMap.keySet())) {
            terminateConfiguration(dataSourceConfigName);
        }
    }

    @Override
    protected SqlDataSource getSqlDataSource(String dataSourceName) throws UnifyException {
        return getDynamicSqlDataSource(dataSourceName);
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {
        terminateAll();
    }

    private SqlDataSource getDynamicSqlDataSource(String dataSourceConfigName) throws UnifyException {
    	if (isComponent(dataSourceConfigName)) {
    		return getComponent(SqlDataSource.class, dataSourceConfigName);
    	}
    	
        if (!dynamicSqlDataSourceMap.isKey(dataSourceConfigName)) {
            throw new UnifyException(UnifyCoreErrorConstants.DYNAMIC_DATASOURCE_IS_UNKNOWN, dataSourceConfigName);
        }

        return dynamicSqlDataSourceMap.get(dataSourceConfigName);
    }

	private void createAndInitDynamicSqlDataSource(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig)
			throws UnifyException {
		dynamicSqlDataSourceMap.get(dynamicSqlDataSourceConfig.getName(), dynamicSqlDataSourceConfig);

		final DataSourceEntityContext entityCtx = entityListProvider
				.getDataSourceEntityContext(Arrays.asList(dynamicSqlDataSourceConfig.getPreferredName()));
		entityCtx.addDataSourceAlias(dynamicSqlDataSourceConfig.getPreferredName(),
				dynamicSqlDataSourceConfig.getName());
		final DataSourceManagerContext ctx = new DataSourceManagerContext(entityCtx,
				new DataSourceManagerOptions(PrintFormat.NONE,
						ForceConstraints.fromBoolean(!getContainerSetting(boolean.class,
								UnifyCorePropertyConstants.APPLICATION_FOREIGNKEY_EASE, false))));
		initDataSource(ctx, dynamicSqlDataSourceConfig.getName());
		if (dynamicSqlDataSourceConfig.isManageSchema()) {
			manageDataSource(ctx, dynamicSqlDataSourceConfig.getName());
		}
	}

    private DynamicSqlDataSource getNewDynamicSqlDataSource(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig)
            throws UnifyException {
        DynamicSqlDataSource dynamicSqlDataSource =
                (DynamicSqlDataSource) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCE);
        dynamicSqlDataSource.configure(dynamicSqlDataSourceConfig);
        return dynamicSqlDataSource;
    }
}
