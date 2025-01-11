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

import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.database.DataSourceManagerContext;
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
	public void initDataSource(DataSourceManagerContext ctx, String dataSourceName) throws UnifyException {
		SqlDataSource sqlDataSource = getSqlDataSource(dataSourceName);
		scanTableEntityTypes(ctx, dataSourceName, sqlDataSource);
		scanViewEntityTypes(ctx, dataSourceName, sqlDataSource);
		
		if (sqlDataSource.isInitDelayed()) {
			delayedDataSourceList.add(dataSourceName);
		} else {
			Connection connection = (Connection) sqlDataSource.getConnection();
			PreparedStatement pstmt = null;
			try {
				buildSqlEntityFactoryInformation(ctx, dataSourceName, sqlDataSource);
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
	public void manageDataSource(DataSourceManagerContext ctx, String dataSourceName) throws UnifyException {
		SqlDataSource sqlDataSource = getSqlDataSource(dataSourceName);
		if (sqlDataSource.isManaged() && !sqlDataSource.isReadOnly()) {
			SqlSchemaManagerOptions _options = new SqlSchemaManagerOptions(ctx.getOptions());
			List<Class<?>> tableList = getDependencyTableEntities(ctx, dataSourceName);
			List<Class<? extends Entity>> viewList = new ArrayList<Class<? extends Entity>>(SqlUtils.getEntityClassList(tableList));
			viewList.addAll(getDependencyViewOnlyEntities(ctx, dataSourceName));
			if (sqlDataSource.getDialect().isReconstructViewsOnTableSchemaUpdate()) {
				sqlSchemaManager.dropViewSchema(sqlDataSource, _options, viewList);
			}

			sqlSchemaManager.manageTableSchema(sqlDataSource, _options, tableList);			
			sqlSchemaManager.manageViewSchema(sqlDataSource, _options, viewList);
		}
	}

	@Override
	public void initDelayedDataSource(DataSourceManagerContext ctx) throws UnifyException {
		if (!delayedInit) {
			synchronized(this) {
				if (!delayedInit) {
					for (String dataSourceName: delayedDataSourceList) {
						SqlDataSource sqlDataSource = getSqlDataSource(dataSourceName);
						buildSqlEntityFactoryInformation(ctx, dataSourceName, sqlDataSource);
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

	private void scanTableEntityTypes(DataSourceManagerContext ctx, String dataSourceName, SqlDataSource sqlDataSource)
			throws UnifyException {
		for (Class<?> clazz : sqlDataSource.getTableEntityTypes()) {
			ctx.addTableEntityClass(dataSourceName, clazz);
		}
	}

	private void scanViewEntityTypes(DataSourceManagerContext ctx, String dataSourceName, SqlDataSource sqlDataSource)
			throws UnifyException {
		for (Class<? extends Entity> clazz : sqlDataSource.getViewEntityTypes()) {
			ctx.addViewEntityClass(dataSourceName, clazz);
		}
	}

	private void buildSqlEntityFactoryInformation(DataSourceManagerContext ctx, String dataSourceName,
			SqlDataSource sqlDataSource) throws UnifyException {
		logDebug("Building SQL information for data source [{0}]...", dataSourceName);
		SqlDataSourceDialect sqlDataSourceDialect = sqlDataSource.getDialect();

		List<Class<?>> tableEntityTypes = ctx.getTableEntityClasses(dataSourceName);
		logDebug("Constructing SQL information for [{0}] table types...", tableEntityTypes.size());
		for (Class<?> entityClass : tableEntityTypes) {
			sqlDataSourceDialect.createSqlEntityInfo(entityClass);
		}

		List<Class<? extends Entity>> viewEntityTypes = ctx.getViewEntityClasses(dataSourceName);
		logDebug("Constructing SQL information for [{0}] view types...", viewEntityTypes.size());
		for (Class<?> entityClass : viewEntityTypes) {
			sqlDataSourceDialect.createSqlEntityInfo(entityClass);
		}
	}

	private List<Class<?>> getDependencyTableEntities(DataSourceManagerContext ctx, String dataSourceName) throws UnifyException {
		SqlDataSource sqlDataSource = getSqlDataSource(dataSourceName);
		return sqlSchemaManager.buildParentDependencyList(sqlDataSource, ctx.getTableEntityClasses(dataSourceName));
	}
	
	private List<Class<? extends Entity>> getDependencyViewOnlyEntities(DataSourceManagerContext ctx, String dataSourceName) throws UnifyException {
		return ctx.getViewEntityClasses(dataSourceName);
	}
}
