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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.database.DataSourceManagerOptions;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.util.SqlUtils;

/**
 * Convenient abstract base class for SQL data source managers.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractSqlDataSourceManager extends AbstractUnifyComponent implements SqlDataSourceManager {

	@Configurable
	private SqlSchemaManager sqlSchemaManager;

	private List<String> delayedDataSourceList;
	
	private boolean delayedInit;
	
	public AbstractSqlDataSourceManager() {
		this.delayedDataSourceList = new ArrayList<String>();
	}

	@Override
	public void initDataSource(String dataSourceName, DataSourceManagerOptions options) throws UnifyException {
		SqlDataSource sqlDataSource = getSqlDataSource(dataSourceName);
		if (sqlDataSource.isInitDelayed()) {
			delayedDataSourceList.add(dataSourceName);
		} else {
			Connection connection = (Connection) sqlDataSource.getConnection();
			PreparedStatement pstmt = null;
			try {
				buildSqlEntityFactoryInformation(dataSourceName, sqlDataSource);
				for (SqlStatement sqlStatement : sqlDataSource.getDialect().prepareDataSourceInitStatements()) {
					pstmt = connection.prepareStatement(sqlStatement.getSql());
					pstmt.executeUpdate();
					SqlUtils.close(pstmt);
				}
			} catch (SQLException e) {
				throw new UnifyException(e, UnifyCoreErrorConstants.SQLSCHEMAMANAGER_MANAGE_SCHEMA_ERROR,
						dataSourceName);
			} finally {
				SqlUtils.close(pstmt);
				sqlDataSource.restoreConnection(connection);
			}
		}
	}

	@Override
	public void manageDataSource(String dataSourceName, DataSourceManagerOptions options) throws UnifyException {
		SqlDataSource sqlDataSource = getSqlDataSource(dataSourceName);
		if (!sqlDataSource.isReadOnly()) {
			SqlSchemaManagerOptions _options = new SqlSchemaManagerOptions(options);
			List<Class<?>> tableList = getTableEntities(dataSourceName);
			List<Class<? extends Entity>> viewList = new ArrayList<Class<? extends Entity>>(SqlUtils.getEntityClassList(tableList));
			viewList.addAll(getViewOnlyEntities(dataSourceName));
			if (sqlDataSource.getDialect().isReconstructViewsOnTableSchemaUpdate()) {
				sqlSchemaManager.dropViewSchema(sqlDataSource, _options, viewList);
			}

			sqlSchemaManager.manageTableSchema(sqlDataSource, _options, tableList);			
			sqlSchemaManager.manageViewSchema(sqlDataSource, _options, viewList);
		}
	}

	@Override
	public void initDelayedDataSource() throws UnifyException {
		if (!delayedInit) {
			synchronized(this) {
				if (!delayedInit) {
					for (String dataSourceName: delayedDataSourceList) {
						SqlDataSource sqlDataSource = getSqlDataSource(dataSourceName);
						buildSqlEntityFactoryInformation(dataSourceName, sqlDataSource);
					}
					delayedInit = true;
				}
			}
		}		
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	protected abstract SqlDataSource getSqlDataSource(String dataSourceName) throws UnifyException;

	protected List<Class<?>> getTableEntityTypes(String dataSourceName, SqlDataSource sqlDataSource)
			throws UnifyException {
		return sqlDataSource.getTableEntityTypes();
	}

	protected List<Class<? extends Entity>> getViewEntityTypes(String dataSourceName, SqlDataSource sqlDataSource)
			throws UnifyException {
		return sqlDataSource.getViewEntityTypes();
	}

	private void buildSqlEntityFactoryInformation(String dataSourceName, SqlDataSource sqlDataSource)
			throws UnifyException {
		logDebug("Building SQL information for data source [{0}]...", dataSourceName);
		SqlDataSourceDialect sqlDataSourceDialect = sqlDataSource.getDialect();

		List<Class<?>> tableEntityTypes = getTableEntityTypes(dataSourceName, sqlDataSource);
		logDebug("Constructing SQL information for [{0}] table types...", tableEntityTypes.size());
		for (Class<?> entityClass : tableEntityTypes) {
			sqlDataSourceDialect.createSqlEntityInfo(entityClass);
		}

		List<Class<? extends Entity>> viewEntityTypes = getViewEntityTypes(dataSourceName, sqlDataSource);
		logDebug("Constructing SQL information for [{0}] view types...", viewEntityTypes.size());
		for (Class<?> entityClass : viewEntityTypes) {
			sqlDataSourceDialect.createSqlEntityInfo(entityClass);
		}
	}

	private List<Class<?>> getTableEntities(String dataSourceName) throws UnifyException {
		SqlDataSource sqlDataSource = getSqlDataSource(dataSourceName);
		return sqlSchemaManager.buildParentDependencyList(sqlDataSource, getTableEntityTypes(dataSourceName, sqlDataSource));
	}
	
	private List<Class<? extends Entity>> getViewOnlyEntities(String dataSourceName) throws UnifyException {
		SqlDataSource sqlDataSource = getSqlDataSource(dataSourceName);
		return getViewEntityTypes(dataSourceName, sqlDataSource);
	}
}
