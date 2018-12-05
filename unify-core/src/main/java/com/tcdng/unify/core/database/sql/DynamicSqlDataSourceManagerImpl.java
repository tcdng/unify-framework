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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.database.DataSource;
import com.tcdng.unify.core.database.NativeQuery;

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
		this.dynamicSqlDataSoureMap = new FactoryMap<String, DynamicSqlDataSource>() {
			@Override
			protected DynamicSqlDataSource create(String key, Object... params) throws Exception {
				return newDynamicSqlDataSource((DynamicSqlDataSourceConfig) params[0]);
			}
		};
	}

	@Override
	public void configure(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig) throws UnifyException {
		String configName = dynamicSqlDataSourceConfig.getName();
		if (this.dynamicSqlDataSoureMap.isKey(configName)) {
			throw new UnifyException(UnifyCoreErrorConstants.DYNAMIC_DATASOURCE_ALREADY_CONFIGURED, configName);
		}

		this.dynamicSqlDataSoureMap.get(configName, dynamicSqlDataSourceConfig);
	}

	@Override
	public boolean reconfigure(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig) throws UnifyException {
		String configName = dynamicSqlDataSourceConfig.getName();
		if (this.dynamicSqlDataSoureMap.isKey(configName)) {
			this.dynamicSqlDataSoureMap.get(configName).reconfigure(dynamicSqlDataSourceConfig);
			return true;
		}

		return false;
	}

	@Override
	public boolean testConfiguration(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig) throws UnifyException {
		DynamicSqlDataSource dsds = this.newDynamicSqlDataSource(dynamicSqlDataSourceConfig);
		try {
			return dsds.testConnection();
		} finally {
			dsds.terminate();
		}
	}

	@Override
	public int testNativeQuery(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig, NativeQuery query)
			throws UnifyException {
		DynamicSqlDataSource dsds = this.newDynamicSqlDataSource(dynamicSqlDataSourceConfig);
		try {
			return dsds.testNativeQuery(query);
		} finally {
			dsds.terminate();
		}
	}

	@Override
	public int testNativeQuery(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig, String nativeSql)
			throws UnifyException {
		DynamicSqlDataSource dsds = this.newDynamicSqlDataSource(dynamicSqlDataSourceConfig);
		try {
			return dsds.testNativeQuery(nativeSql);
		} finally {
			dsds.terminate();
		}
	}

	@Override
	public int testNativeUpdate(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig, String updateSql)
			throws UnifyException {
		DynamicSqlDataSource dsds = this.newDynamicSqlDataSource(dynamicSqlDataSourceConfig);
		try {
			return dsds.testNativeUpdate(updateSql);
		} finally {
			dsds.terminate();
		}
	}

	@Override
	public boolean isConfigured(String configName) throws UnifyException {
		if (this.dynamicSqlDataSoureMap.isKey(configName)) {
			return this.dynamicSqlDataSoureMap.get(configName).isConfigured();
		}
		return false;
	}

	@Override
	public int getDataSourceCount() throws UnifyException {
		return this.dynamicSqlDataSoureMap.size();
	}

	@Override
	public List<String> getSchemas(String configName) throws UnifyException {
		return this.getDynamicSqlDataSource(configName).getSchemaList();
	}

	@Override
	public List<SqlTableInfo> getTables(String configName, String schemaName, SqlTableType sqlTableType)
			throws UnifyException {
		return this.getDynamicSqlDataSource(configName).getTableList(schemaName, sqlTableType);
	}

	@Override
	public List<SqlColumnInfo> getColumns(String configName, String schemaName, String tableName)
			throws UnifyException {
		return this.getDynamicSqlDataSource(configName).getColumnList(schemaName, tableName);
	}

	@Override
	public List<Object[]> getRows(String configName, NativeQuery query) throws UnifyException {
		return this.getDynamicSqlDataSource(configName).getRows(query);
	}

	@Override
	public DataSource getDataSource(String configName) throws UnifyException {
		return this.getDynamicSqlDataSource(configName);
	}

	@Override
	public Connection getConnection(String configName) throws UnifyException {
		return this.getDynamicSqlDataSource(configName).getConnection();
	}

	@Override
	public boolean restoreConnection(String configName, Connection connection) throws UnifyException {
		return this.getDynamicSqlDataSource(configName).restoreConnection(connection);
	}

	@Override
	public void terminateConfiguration(String configName) throws UnifyException {
		DynamicSqlDataSource dsds = this.getDynamicSqlDataSource(configName);
		try {
			dsds.terminate();
		} finally {
			this.dynamicSqlDataSoureMap.remove(configName);
		}
	}

	@Override
	public void terminateAll() throws UnifyException {
		for (String configName : new ArrayList<String>(this.dynamicSqlDataSoureMap.keySet())) {
			this.terminateConfiguration(configName);
		}
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {
		this.terminateAll();
	}

	private DynamicSqlDataSource getDynamicSqlDataSource(String configName) throws UnifyException {
		if (!this.dynamicSqlDataSoureMap.isKey(configName)) {
			throw new UnifyException(UnifyCoreErrorConstants.DYNAMIC_DATASOURCE_IS_UNKNOWN, configName);
		}

		return this.dynamicSqlDataSoureMap.get(configName);
	}

	private DynamicSqlDataSource newDynamicSqlDataSource(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig)
			throws UnifyException {
		DynamicSqlDataSource dsds = (DynamicSqlDataSource) this
				.getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCE);
		dsds.configure(dynamicSqlDataSourceConfig);
		return dsds;
	}
}
