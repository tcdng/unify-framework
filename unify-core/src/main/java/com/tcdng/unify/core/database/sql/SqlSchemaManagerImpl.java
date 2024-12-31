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
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.common.annotation.StaticList;
import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.ForceConstraints;
import com.tcdng.unify.core.constant.LocaleType;
import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.util.SqlUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Default implementation of SQL schema manager.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_SQLSCHEMAMANAGER)
public class SqlSchemaManagerImpl extends AbstractSqlSchemaManager {

	private static final Map<String, Set<String>> swappableValueSet;

	private boolean sqlDebugging;

	static {
		swappableValueSet = new HashMap<String, Set<String>>();
		swappableValueSet.put("0", new HashSet<String>(Arrays.asList("(0)")));
	}

	@Override
	public void registerSqlEntityClasses(SqlDataSource sqlDataSource, List<Class<? extends Entity>> entityClassList)
			throws UnifyException {
		logInfo("Registering [{0}] entity classes...", entityClassList.size());
		SqlDataSourceDialect sqlDataSourceDialect = sqlDataSource.getDialect();
		for (Class<?> entityClass : entityClassList) {
			sqlDataSourceDialect.createSqlEntityInfo(entityClass);
		}

		// TODO Unregister older copies

		logInfo("Registration of entity classes completed successfully.");
	}

	@Override
	public void updateSchema(SqlDataSource sqlDataSource, List<Class<?>> schemaChangedClassList) throws UnifyException {
		logInfo("Updating schema information for [{0}] managed classes in [{1}] datasource ...",
				schemaChangedClassList.size(), sqlDataSource.getName());
		SqlSchemaManagerOptions options = new SqlSchemaManagerOptions(PrintFormat.NONE, ForceConstraints.fromBoolean(
				!getContainerSetting(boolean.class, UnifyCorePropertyConstants.APPLICATION_FOREIGNKEY_EASE, false)));
		
		List<Class<? extends Entity>> viewList = buildDependencyDynamicViewList(sqlDataSource, schemaChangedClassList);

		if (sqlDataSource.getDialect().isReconstructViewsOnTableSchemaUpdate()) {
			dropViewSchema(sqlDataSource, options, viewList);
		}

		manageTableSchema(sqlDataSource, options, schemaChangedClassList);
		manageViewSchema(sqlDataSource, options, viewList);
		logInfo("Schema information update for managed classes completed successfully.");
	}

	@Override
	public void manageTableSchema(SqlDataSource sqlDataSource, SqlSchemaManagerOptions options,
			List<Class<?>> entityClasses) throws UnifyException {
		Connection connection = (Connection) sqlDataSource.getConnection();
		try {
			logInfo("Scanning datasource {0} for table schema...", sqlDataSource.getName());
			logInfo("Managing schema elements for [{0}] table entities...", entityClasses.size());
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			SqlSchemaManagerContext ctx = new SqlSchemaManagerContext();
			for (Class<?> entityClass : entityClasses) {
				Map<String, TableConstraint> managedTableConstraints = fetchManagedTableConstraints(databaseMetaData,
						sqlDataSource, entityClass);
				manageTableSchema(ctx, databaseMetaData, sqlDataSource, entityClass, managedTableConstraints, options);
			}

			applyDeferredTableSchemaSql(ctx, databaseMetaData, sqlDataSource);
			logInfo("Table schema elements management completed for [{0}] table entities...", entityClasses.size());
		} catch (SQLException e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.SQLSCHEMAMANAGER_MANAGE_SCHEMA_ERROR,
					sqlDataSource.getPreferredName());
		} finally {
			sqlDataSource.restoreConnection(connection);
		}
	}

	@Override
	public void manageViewSchema(SqlDataSource sqlDataSource, SqlSchemaManagerOptions options,
			List<Class<? extends Entity>> entityClasses) throws UnifyException {
		Connection connection = (Connection) sqlDataSource.getConnection();
		try {
			logInfo("Scanning datasource {0} view schema...", sqlDataSource.getName());
			logInfo("Managing schema elements for [{0}] view entities...", entityClasses.size());
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			for (Class<? extends Entity> entityClass : entityClasses) {
				manageViewSchema(databaseMetaData, sqlDataSource, entityClass, options);
			}
			logInfo("View schema elements management completed for [{0}] view entities...", entityClasses.size());
		} catch (SQLException e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.SQLSCHEMAMANAGER_MANAGE_SCHEMA_ERROR,
					sqlDataSource.getPreferredName());
		} finally {
			sqlDataSource.restoreConnection(connection);
		}
	}

	@Override
	public void dropViewSchema(SqlDataSource sqlDataSource, SqlSchemaManagerOptions options,
			List<Class<? extends Entity>> entityClasses) throws UnifyException {
		Connection connection = (Connection) sqlDataSource.getConnection();
		try {
			logInfo("Scanning datasource {0} drop view schema...", sqlDataSource.getName());
			logInfo("Dropping schema elements for [{0}] view entities...", entityClasses.size());
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			for (Class<? extends Entity> entityClass : entityClasses) {
				dropViewSchema(databaseMetaData, sqlDataSource, entityClass, options);
			}
			logInfo("Drop view schema elements deletion completed for [{0}] view entities...", entityClasses.size());
		} catch (SQLException e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.SQLSCHEMAMANAGER_MANAGE_SCHEMA_ERROR,
					sqlDataSource.getPreferredName());
		} finally {
			sqlDataSource.restoreConnection(connection);
		}
	}

	@Override
	public List<Class<?>> buildParentDependencyList(SqlDataSource sqlDataSource, List<Class<?>> entityClasses)
			throws UnifyException {
		logInfo("Building parent dependency list for [{0}] entities...", entityClasses.size());
		List<Class<?>> resultList = new ArrayList<Class<?>>();
		for (Class<?> entityClass : entityClasses) {
			buildParentDependencyList(sqlDataSource, resultList, entityClass);
		}

		logInfo("Parent dependency list resolved to [{0}] entities...", resultList.size());
		return resultList;
	}

	@Override
	public List<Class<?>> buildChildDependencyList(SqlDataSource sqlDataSource, List<Class<?>> entityClasses)
			throws UnifyException {
		logInfo("Building child dependency list for [{0}] entities...", entityClasses.size());
		List<Class<?>> resultList = new ArrayList<Class<?>>();
		for (Class<?> entityClass : entityClasses) {
			buildChildDependencyList(sqlDataSource, resultList, entityClass);
		}

		logInfo("Child dependency list resolved to [{0}] entities...", resultList.size());
		return resultList;
	}

	@Override
	protected void onInitialize() throws UnifyException {
		sqlDebugging = getContainerSetting(boolean.class, UnifyCorePropertyConstants.APPLICATION_SQL_DEBUGGING, false);
	}

	private List<Class<? extends Entity>> buildDependencyDynamicViewList(SqlDataSource sqlDataSource,
			List<Class<?>> entityClasses) throws UnifyException {
		logInfo("Building dependency view list for [{0}] entities...", entityClasses.size());
		List<Class<? extends Entity>> viewList = SqlUtils
				.getDynamicEntityClassList(buildChildDependencyList(sqlDataSource, entityClasses));
		logInfo("[{0}] dependency views resolved.", viewList.size());
		return viewList;
	}

	private void buildParentDependencyList(SqlDataSource sqlDataSource, List<Class<?>> entityTypeList,
			Class<?> entityClass) throws UnifyException {
		SqlDataSourceDialect sqlDataSourceDialect = sqlDataSource.getDialect();
		SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.findSqlEntityInfo(entityClass);

		for (SqlFieldInfo sqlFieldInfo : sqlEntityInfo.getManagedFieldInfos()) {
			if (sqlFieldInfo.isForeignKey()) {
				SqlEntityInfo fkSqlEntityInfo = sqlFieldInfo.getForeignEntityInfo();
				if (!sqlEntityInfo.getTableName().equals(fkSqlEntityInfo.getTableName())) {
					buildParentDependencyList(sqlDataSource, entityTypeList, fkSqlEntityInfo.getKeyClass());
				}
			}
		}

		if (!entityTypeList.contains(entityClass)) {
			entityTypeList.add(entityClass);
		}
	}

	private void buildChildDependencyList(SqlDataSource sqlDataSource, List<Class<?>> entityTypeList,
			Class<?> entityClass) throws UnifyException {
		SqlDataSourceDialect sqlDataSourceDialect = sqlDataSource.getDialect();
		SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.findSqlEntityInfo(entityClass);
		List<SqlEntityInfo> sqlEntityInfos = sqlDataSourceDialect.findAllChildSqlEntityInfos(entityClass);

		for (SqlEntityInfo _sqlEntityInfo : sqlEntityInfos) {
			if (!sqlEntityInfo.getTableName().equals(_sqlEntityInfo.getTableName())) {
				buildChildDependencyList(sqlDataSource, entityTypeList, _sqlEntityInfo.getEntityClass());
			}
		}

		if (!entityTypeList.contains(entityClass)) {
			entityTypeList.add(entityClass);
		}
	}

	private void manageTableSchema(SqlSchemaManagerContext ctx, DatabaseMetaData databaseMetaData,
			SqlDataSource sqlDataSource, Class<?> entityClass, Map<String, TableConstraint> managedTableConstraints,
			SqlSchemaManagerOptions options) throws UnifyException {
		SqlDataSourceDialect sqlDataSourceDialect = sqlDataSource.getDialect();
		SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.findSqlEntityInfo(entityClass);
		if (sqlEntityInfo.isSchemaAlreadyManaged()) {
			return;
		}

		logDebug("Managing entity class [{0}]...", entityClass.getName());
		final PrintFormat printFormat = options.getPrintFormat();
		final ForceConstraints forceConstraints = options.getForceConstraints();

		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			connection = databaseMetaData.getConnection();
			// Manage entity table
			logDebug("Managing entity table [{0}]...", sqlEntityInfo.getTableName());
			String schema = sqlEntityInfo.getSchema();
			if (StringUtils.isBlank(schema)) {
				schema = sqlDataSource.getAppSchema();
			}

			List<String> tableUpdateSql = new ArrayList<String>();
			rs = databaseMetaData.getTables(null, schema, sqlEntityInfo.getTableName(), null);
			if (rs.next()) {
				// Table exists. Check for updates
				List<String> alterTableColumnsSql = Collections.emptyList();
				String tableType = rs.getString("TABLE_TYPE");
				if ("TABLE".equalsIgnoreCase(tableType)) {
					Map<String, SqlColumnInfo> columnMap = sqlDataSource.getColumnMapLowerCase(schema,
							sqlEntityInfo.getTableName());
					alterTableColumnsSql = getColumnUpdates(sqlDataSourceDialect, sqlEntityInfo, columnMap,
							printFormat);
				} else {
					throw new UnifyException(UnifyCoreErrorConstants.SQLSCHEMAMANAGER_UNABLE_TO_UPDATE_TABLE,
							sqlDataSource.getName(), sqlEntityInfo.getTableName(), tableType);
				}

				SqlUtils.close(rs);

				List<String> dropConstraintSql = new ArrayList<String>();
				List<String> createUpdateConstraintSql = new ArrayList<String>();
				if (!alterTableColumnsSql.isEmpty()) {
					// Drop all constraints and indexes
					logDebug("Dropping all constraints and indexes...");
					dropConstraintSql.addAll(generateDropConstraints(sqlDataSourceDialect, sqlEntityInfo,
							managedTableConstraints.values(), printFormat));

					// Recreate all constraints and indexes
					logDebug("Recreating all constraints and indexes...");
					if (!sqlEntityInfo.isExtended()) {
						for (SqlForeignKeySchemaInfo sqlForeignKeyInfo : sqlEntityInfo.getManagedForeignKeyList()) {
							ctx.addDeferredFkConstraintSql(sqlDataSourceDialect
									.generateAddForeignKeyConstraintSql(sqlEntityInfo, sqlForeignKeyInfo, printFormat));
						}
					}

					for (SqlUniqueConstraintSchemaInfo sqlUniqueConstraintInfo : sqlEntityInfo.getUniqueConstraintList()
							.values()) {
						if (!sqlUniqueConstraintInfo.isWithConditionList()) {
							createUpdateConstraintSql.add(sqlDataSourceDialect.generateAddUniqueConstraintSql(
									sqlEntityInfo, sqlUniqueConstraintInfo, printFormat));
						}
					}

					for (SqlIndexSchemaInfo sqlIndexInfo : sqlEntityInfo.getIndexList().values()) {
						createUpdateConstraintSql.add(
								sqlDataSourceDialect.generateCreateIndexSql(sqlEntityInfo, sqlIndexInfo, printFormat));
					}
				} else {
					// Detect foreign constraint changes
					logDebug("Detecting foreign constraint changes...");
					if (forceConstraints.isTrue() && sqlEntityInfo.isManagedForeignKeys()) {
						if (!sqlEntityInfo.isExtended()) {
							for (SqlForeignKeySchemaInfo sqlForeignKeyInfo : sqlEntityInfo.getManagedForeignKeyList()) {
								if (!sqlEntityInfo.getManagedFieldInfo(sqlForeignKeyInfo.getFieldName())
										.isIgnoreFkConstraint()) {
									SqlFieldInfo sqlFieldInfo = sqlEntityInfo
											.getManagedFieldInfo(sqlForeignKeyInfo.getFieldName());
									String fkConstName = sqlFieldInfo.getConstraint();
									TableConstraint fkConst = managedTableConstraints.get(fkConstName);
									boolean update = true;
									if (fkConst != null) {
										// Check if foreign key matches database constraint
										if (fkConst.isForeignKey() /* && fkConst.getColumns().size() == 1 */
												&& fkConst.getTableName()
														.equals(sqlFieldInfo.getForeignEntityInfo().getTableName())
												&& fkConst.getColumns()
														.contains(sqlFieldInfo.getForeignFieldInfo().getColumnName())) {
											// Perfect match. Remove from pending list and no need for update
											managedTableConstraints.remove(fkConstName);
											update = false;
										}
									}

									if (update) {
										ctx.addDeferredFkConstraintSql(
												sqlDataSourceDialect.generateAddForeignKeyConstraintSql(sqlEntityInfo,
														sqlForeignKeyInfo, printFormat));
									}
								}
							}
						}
					}

					// Detect unique constraint changes
					logDebug("Detecting unique constraint changes...");
					if (sqlEntityInfo.isUniqueConstraints()) {
						for (SqlUniqueConstraintSchemaInfo sqlUniqueConstraintInfo : sqlEntityInfo
								.getUniqueConstraintList().values()) {
							TableConstraint ucConst = managedTableConstraints.get(sqlUniqueConstraintInfo.getName());
							boolean update = true;
							if (ucConst != null) {
								// Check if unique constant matches database constraint
								if (ucConst.isUniqueConst() && !sqlUniqueConstraintInfo.isWithConditionList()
										&& matchIndexAllColumns(sqlEntityInfo,
												sqlUniqueConstraintInfo.getFieldNameList(), ucConst.getColumns())) {
									// Perfect match. Remove from pending list and no need for update
									managedTableConstraints.remove(sqlUniqueConstraintInfo.getName());
									update = false;
								}
							}

							if (update) {
								createUpdateConstraintSql.add(sqlDataSourceDialect.generateAddUniqueConstraintSql(
										sqlEntityInfo, sqlUniqueConstraintInfo, printFormat));
							}
						}
					}

					// Detect index changes
					logDebug("Detecting index changes...");
					if (sqlEntityInfo.isIndexes()) {
						for (SqlIndexSchemaInfo sqlIndexInfo : sqlEntityInfo.getIndexList().values()) {
							TableConstraint idxConst = managedTableConstraints.get(sqlIndexInfo.getName());
							boolean update = true;
							if (idxConst != null) {
								// Check if index matches database constraint
								if (idxConst.isIndex() && matchIndexAllColumns(sqlEntityInfo,
										sqlIndexInfo.getFieldNameList(), idxConst.getColumns())) {
									// Perfect match. Remove from pending list and no need for update
									managedTableConstraints.remove(sqlIndexInfo.getName());
									update = false;
								}
							}

							if (update) {
								createUpdateConstraintSql.add(sqlDataSourceDialect.generateCreateIndexSql(sqlEntityInfo,
										sqlIndexInfo, printFormat));
							}
						}
					}

					// Drop unused constraints and indexes
					logDebug("Dropping unused constraints and indexes...");
					dropConstraintSql.addAll(generateDropConstraints(sqlDataSourceDialect, sqlEntityInfo,
							managedTableConstraints.values(), printFormat));
				}

				tableUpdateSql.addAll(dropConstraintSql);
				tableUpdateSql.addAll(alterTableColumnsSql);
				tableUpdateSql.addAll(createUpdateConstraintSql);
			} else {
				logInfo("Creating datasource table {0}...", sqlEntityInfo.getTableName());

				// Create table
				tableUpdateSql.add(sqlDataSourceDialect.generateCreateTableSql(sqlEntityInfo, printFormat));

				// Create constraints and indexes
				if (forceConstraints.isTrue() && sqlEntityInfo.isManagedForeignKeys()) {
					if (!sqlEntityInfo.isExtended()) {
						for (SqlForeignKeySchemaInfo sqlForeignKeyInfo : sqlEntityInfo.getManagedForeignKeyList()) {
							if (!sqlEntityInfo.getManagedFieldInfo(sqlForeignKeyInfo.getFieldName())
									.isIgnoreFkConstraint()) {
								ctx.addDeferredFkConstraintSql(sqlDataSourceDialect.generateAddForeignKeyConstraintSql(
										sqlEntityInfo, sqlForeignKeyInfo, printFormat));
							}
						}
					}
				}

				if (sqlEntityInfo.isUniqueConstraints()
						&& !sqlDataSourceDialect.isGeneratesUniqueConstraintsOnCreateTable()) {
					for (SqlUniqueConstraintSchemaInfo sqlUniqueConstraintInfo : sqlEntityInfo.getUniqueConstraintList()
							.values()) {
						tableUpdateSql.add(sqlDataSourceDialect.generateAddUniqueConstraintSql(sqlEntityInfo,
								sqlUniqueConstraintInfo, printFormat));
					}
				}

				if (sqlEntityInfo.isIndexes() && !sqlDataSourceDialect.isGeneratesIndexesOnCreateTable()) {
					for (SqlIndexSchemaInfo sqlIndexInfo : sqlEntityInfo.getIndexList().values()) {
						tableUpdateSql.add(
								sqlDataSourceDialect.generateCreateIndexSql(sqlEntityInfo, sqlIndexInfo, printFormat));
					}
				}
			}
			SqlUtils.close(rs);

			// Apply updates
			for (String sql : tableUpdateSql) {
				if (sqlDebugging) {
					logDebug("Executing SQL update [{0}]...", sql);
				}

				pstmt = connection.prepareStatement(sql);
				pstmt.executeUpdate();
				SqlUtils.close(pstmt);

				if (sqlDebugging) {
					logDebug("Completed executing SQL update [{0}]...", sql);
				}
			}
			connection.commit();

			// Update static reference data
			logDebug("Updating static reference data...");
			if (EnumConst.class.isAssignableFrom(entityClass)) {
				StaticList sla = entityClass.getAnnotation(StaticList.class);
				if (sla != null) {
					Map<String, Listable> map = getListMap(LocaleType.APPLICATION, sla.name());
					for (Map.Entry<String, Listable> entry : map.entrySet()) {
						final String code = entry.getKey();
						final String description = entry.getValue().getListDescription();

						final SqlFieldInfo codeFieldInfo = sqlEntityInfo.getFieldInfo("code");
						final SqlFieldInfo descFieldInfo = sqlEntityInfo.getFieldInfo("description");
						pstmt = connection
								.prepareStatement("SELECT COUNT(*) FROM " + sqlEntityInfo.getPreferredTableName()
										+ " WHERE " + codeFieldInfo.getPreferredColumnName() + " = ?");
						pstmt.setString(1, code);
						rs = pstmt.executeQuery();
						rs.next();
						final int count = rs.getInt(1);
						SqlUtils.close(pstmt);
						SqlUtils.close(rs);

						if (count == 0) {
							pstmt = connection.prepareStatement("INSERT INTO " + sqlEntityInfo.getPreferredTableName()
									+ " (" + codeFieldInfo.getPreferredColumnName() + ", "
									+ descFieldInfo.getPreferredColumnName() + ") VALUES (?, ?)");
							pstmt.setString(1, code);
							pstmt.setString(2, description);
							pstmt.executeUpdate();
							SqlUtils.close(pstmt);
						} else {
							pstmt = connection.prepareStatement("UPDATE " + sqlEntityInfo.getPreferredTableName()
									+ " SET " + descFieldInfo.getPreferredColumnName() + " = ? WHERE "
									+ codeFieldInfo.getPreferredColumnName() + " = ?");
							pstmt.setString(1, description);
							pstmt.setString(2, code);
							pstmt.executeUpdate();
							SqlUtils.close(pstmt);
						}
					}
				}
			}
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
			}
			throw new UnifyException(e, UnifyCoreErrorConstants.SQLSCHEMAMANAGER_MANAGE_SCHEMA_ERROR,
					sqlDataSource.getName());
		} finally {
			SqlUtils.close(rs);
			SqlUtils.close(pstmt);
			sqlEntityInfo.setSchemaAlreadyManaged();
		}
	}

	private void applyDeferredTableSchemaSql(SqlSchemaManagerContext ctx, DatabaseMetaData databaseMetaData,
			SqlDataSource sqlDataSource) throws UnifyException {
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			connection = databaseMetaData.getConnection();
			// Execute deferred scripts
			logDebug("Executing deferred SQL scripts...");

			// Apply FK constraints
			logDebug("Applying foreign key SQL scripts...");
			for (String sql : ctx.getDeferredFkConstraintSql()) {
				if (sqlDebugging) {
					logDebug("Executing SQL update [{0}]...", sql);
				}

				pstmt = connection.prepareStatement(sql);
				pstmt.executeUpdate();
				SqlUtils.close(pstmt);

				if (sqlDebugging) {
					logDebug("Completed executing SQL update [{0}]...", sql);
				}
			}
			connection.commit();
			logDebug("Application of foreign key SQL scripts successfully completed.");
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
			}
			throw new UnifyException(e, UnifyCoreErrorConstants.SQLSCHEMAMANAGER_MANAGE_SCHEMA_ERROR,
					sqlDataSource.getName());
		} finally {
			SqlUtils.close(rs);
			SqlUtils.close(pstmt);
		}
	}

	private Map<String, TableConstraint> fetchManagedTableConstraints(DatabaseMetaData databaseMetaData,
			SqlDataSource sqlDataSource, Class<?> entityClass) throws UnifyException {
		SqlDataSourceDialect sqlDataSourceDialect = sqlDataSource.getDialect();
		SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.findSqlEntityInfo(entityClass);

		Map<String, TableConstraint> managedTableConstraints = new LinkedHashMap<String, TableConstraint>();
		ResultSet rs = null;
		try {
			String schema = sqlEntityInfo.getSchema();
			if (StringUtils.isBlank(schema)) {
				schema = sqlDataSource.getAppSchema();
			}
			//
			try (ResultSet _rs = databaseMetaData.getTables(null, schema, sqlEntityInfo.getTableName(), null)) {
				if (_rs.next()) {
					// Fetch foreign keys
					rs = databaseMetaData.getImportedKeys(null, schema, sqlEntityInfo.getTableName());
					while (rs.next()) {
						String fkName = SqlUtils.resolveConstraintName(rs.getString("FK_NAME"),
								sqlDataSourceDialect.isAllObjectsInLowerCase());
						String pkTableName = rs.getString("PKTABLE_NAME");
						String pkColumnName = rs.getString("PKCOLUMN_NAME");
						if (StringUtils.isNotBlank(fkName) && StringUtils.isNotBlank(pkTableName)
								&& StringUtils.isNotBlank(pkColumnName)) {
							TableConstraint tConst = managedTableConstraints.get(fkName);
							if (tConst == null) {
								tConst = new TableConstraint(fkName, pkTableName, false);
								managedTableConstraints.put(fkName, tConst);
							}

							tConst.addColumn(pkColumnName);
						}
					}

					SqlUtils.close(rs);
					// Fetch indexes
					rs = databaseMetaData.getIndexInfo(null, schema, sqlEntityInfo.getTableName(), false, false);
					while (rs.next()) {
						String idxName = SqlUtils.resolveConstraintName(rs.getString("INDEX_NAME"),
								sqlDataSourceDialect.isAllObjectsInLowerCase());
						String idxColumnName = rs.getString("COLUMN_NAME");
						if (StringUtils.isNotBlank(idxName) && StringUtils.isNotBlank(idxColumnName)) {
							boolean unique = SqlUtils.isUniqueConstraintName(idxName);
							TableConstraint tConst = managedTableConstraints.get(idxName);
							if (tConst == null) {
								tConst = new TableConstraint(idxName, null, unique);
								managedTableConstraints.put(idxName, tConst);
							}

							tConst.addColumn(idxColumnName);
						}
					}
				}
			}
		} catch (SQLException e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.SQLSCHEMAMANAGER_MANAGE_SCHEMA_ERROR,
					sqlDataSource.getName(), entityClass);
		} finally {
			SqlUtils.close(rs);
		}

		return managedTableConstraints;
	}

	private List<String> generateDropConstraints(SqlDataSourceDialect sqlDataSourceDialect, SqlEntityInfo sqlEntityInfo,
			Collection<TableConstraint> constraints, PrintFormat printFormat) throws UnifyException {
		List<String> dropSql = new ArrayList<String>();
		for (TableConstraint tConst : constraints) {
			if (tConst.isForeignKey()) {
				dropSql.add(sqlDataSourceDialect.generateDropForeignKeyConstraintSql(sqlEntityInfo, tConst.getName(),
						printFormat));
			} else if (tConst.isUniqueConst()) {
				dropSql.add(sqlDataSourceDialect.generateDropUniqueConstraintSql(sqlEntityInfo, tConst.getName(),
						printFormat));
			} else {
				dropSql.add(sqlDataSourceDialect.generateDropIndexSql(sqlEntityInfo, tConst.getName(), printFormat));
			}
		}

		return dropSql;
	}

	private void manageViewSchema(DatabaseMetaData databaseMetaData, SqlDataSource sqlDataSource,
			Class<? extends Entity> entityClass, SqlSchemaManagerOptions options) throws UnifyException {
		SqlDataSourceDialect sqlDataSourceDialect = sqlDataSource.getDialect();
		SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.findSqlEntityInfo(entityClass);
		final PrintFormat printFormat = options.getPrintFormat();

		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			connection = databaseMetaData.getConnection();
			String appSchema = sqlDataSource.getAppSchema();

			// Manage entity view
			List<String> viewUpdateSQL = new ArrayList<String>();
			if (sqlEntityInfo.isViewable() || sqlEntityInfo.isViewOnly()) {
				boolean isViewNew = false;
				boolean isDropView = false;
				rs = databaseMetaData.getTables(null, appSchema, sqlEntityInfo.getViewName(), null);
				if (rs.next()) {
					String tableType = rs.getString("TABLE_TYPE");
					if (!"VIEW".equalsIgnoreCase(tableType)) {
						throw new UnifyException(UnifyCoreErrorConstants.SQLSCHEMAMANAGER_UNABLE_TO_UPDATE_TABLE,
								sqlDataSource.getName(), sqlEntityInfo.getViewName(), tableType);
					}

					// Check is list-only fields have changed
					isDropView = !matchViewColumns(sqlEntityInfo,
							sqlDataSource.getColumns(appSchema, sqlEntityInfo.getViewName()));
				} else {
					// Force creation of view
					isViewNew = true;
				}
				SqlUtils.close(rs);

				if (isViewNew || isDropView) {
					// Check if we have to drop view first
					if (isDropView) {
						viewUpdateSQL.add(sqlDataSourceDialect.generateDropViewSql(sqlEntityInfo));
					}

					// Create view
					viewUpdateSQL.add(sqlDataSourceDialect.generateCreateViewSql(sqlEntityInfo, printFormat));
				}
			}

			// Apply updates
			for (String sql : viewUpdateSQL) {
				pstmt = connection.prepareStatement(sql);
				pstmt.executeUpdate();
				SqlUtils.close(pstmt);
			}
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
			}
			throw new UnifyException(e, UnifyCoreErrorConstants.SQLSCHEMAMANAGER_MANAGE_SCHEMA_ERROR,
					sqlDataSource.getName());
		} finally {
			SqlUtils.close(rs);
			SqlUtils.close(pstmt);
		}
	}

	private void dropViewSchema(DatabaseMetaData databaseMetaData, SqlDataSource sqlDataSource,
			Class<? extends Entity> entityClass, SqlSchemaManagerOptions options) throws UnifyException {
		SqlDataSourceDialect sqlDataSourceDialect = sqlDataSource.getDialect();
		SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.findSqlEntityInfo(entityClass);

		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			connection = databaseMetaData.getConnection();
			String appSchema = sqlDataSource.getAppSchema();

			// Drop entity view
			String dropViewSQL = null;
			if (sqlEntityInfo.isViewable()) {
				boolean isDropView = false;
				rs = databaseMetaData.getTables(null, appSchema, sqlEntityInfo.getViewName(), null);
				if (rs.next()) {
					String tableType = rs.getString("TABLE_TYPE");
					if (!"VIEW".equalsIgnoreCase(tableType)) {
						throw new UnifyException(UnifyCoreErrorConstants.SQLSCHEMAMANAGER_UNABLE_TO_UPDATE_TABLE,
								sqlDataSource.getName(), sqlEntityInfo.getViewName(), tableType);
					}

					isDropView = true;
				}

				SqlUtils.close(rs);

				if (isDropView) {
					dropViewSQL = sqlDataSourceDialect.generateDropViewSql(sqlEntityInfo);
				}
			}

			// Apply updates
			if (dropViewSQL != null) {
				pstmt = connection.prepareStatement(dropViewSQL);
				pstmt.executeUpdate();
				SqlUtils.close(pstmt);
			}
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
			}
			throw new UnifyException(e, UnifyCoreErrorConstants.SQLSCHEMAMANAGER_MANAGE_SCHEMA_ERROR,
					sqlDataSource.getName());
		} finally {
			SqlUtils.close(rs);
			SqlUtils.close(pstmt);
		}
	}

	private boolean matchIndexAllColumns(SqlEntityInfo sqlEntityInfo, List<String> fieldNameList, Set<String> columns)
			throws UnifyException {
		if (fieldNameList.size() == columns.size()) {
			for (String fieldName : fieldNameList) {
				if (!columns.contains(sqlEntityInfo.getManagedFieldInfo(fieldName).getColumnName())) {
					return false;
				}
			}

			return true;
		}
		return false;
	}

	private boolean matchViewColumns(SqlEntityInfo sqlEntityInfo, Set<String> columnNames) {
		for (SqlFieldInfo sqlfieldInfo : sqlEntityInfo.getManagedListFieldInfos()) {
			if (!columnNames.contains(sqlfieldInfo.getColumnName())) {
				return false;
			}
		}

		return true;
	}

	private List<String> getColumnUpdates(SqlDataSourceDialect sqlDataSourceDialect, SqlEntityInfo sqlEntityInfo,
			Map<String, SqlColumnInfo> columnInfos, PrintFormat printFormat) throws UnifyException {
		List<String> columnUpdateSql = new ArrayList<String>();
		logDebug("Getting column updates for [{0}]...", sqlEntityInfo.getTableName());
		logDebug("Checking [{0}] fields...", sqlEntityInfo.getManagedFieldInfos().size());
		for (SqlFieldInfo sqlfieldInfo : sqlEntityInfo.getManagedFieldInfos()) {
			SqlColumnInfo sqlColumnInfo = columnInfos.remove(sqlfieldInfo.getColumnName().toLowerCase());
			if (sqlColumnInfo == null) {
				// New column
				logDebug("New column/field detected [{0}]...", sqlfieldInfo);
				columnUpdateSql.add(sqlDataSourceDialect.generateAddColumn(sqlEntityInfo, sqlfieldInfo, printFormat));
			} else {
				SqlColumnAlterInfo columnAlterInfo = checkSqlColumnAltered(sqlDataSourceDialect, sqlfieldInfo,
						sqlColumnInfo);
				if (columnAlterInfo.isAltered()) {
					// Alter column
					logDebug("Column alteration of column/field [{0}] detected [{1}]...", sqlfieldInfo,
							columnAlterInfo);
					columnUpdateSql.addAll(sqlDataSourceDialect.generateAlterColumn(sqlEntityInfo, sqlfieldInfo,
							columnAlterInfo, printFormat));
				}
			}
		}

		// Make abandoned columns nullable
		for (SqlColumnInfo sqlColumnInfo : columnInfos.values()) {
			if (!sqlColumnInfo.isNullable()) {
				// Alter column nullable
				logDebug("Non-nullable unused field detected [{0}]...", sqlColumnInfo);
				columnUpdateSql
						.add(sqlDataSourceDialect.generateAlterColumnNull(sqlEntityInfo, sqlColumnInfo, printFormat));
			}
		}

		return columnUpdateSql;
	}

	private SqlColumnAlterInfo checkSqlColumnAltered(SqlDataSourceDialect sqlDataSourceDialect,
			SqlFieldInfo sqlfieldInfo, SqlColumnInfo columnInfo) throws UnifyException {
		final boolean nullableChange = columnInfo.isNullable() != sqlfieldInfo.isNullable();
		if (nullableChange) {
			logDebug("Nullable Change: columnInfo.isNullable() = {0}, sqlfieldInfo.isNullable() = {1}...",
					columnInfo.isNullable(), sqlfieldInfo.isNullable());
		}

		SqlDataTypePolicy sqlDataTypePolicy = sqlDataSourceDialect.getSqlTypePolicy(sqlfieldInfo.getColumnType(),
				sqlfieldInfo.getLength());
		boolean defaultChange = !sqlDataSourceDialect.matchColumnDefault(columnInfo.getDefaultVal(),
				sqlfieldInfo.getDefaultVal())
				&& !isSwappableValues(sqlfieldInfo.getDefaultVal(), columnInfo.getDefaultVal());
		if (defaultChange && StringUtils.isBlank(sqlfieldInfo.getDefaultVal())) {
			if (StringUtils.isBlank(columnInfo.getDefaultVal()) || sqlDataSourceDialect.matchColumnDefault(
					columnInfo.getDefaultVal(), sqlDataTypePolicy.getAltDefault(sqlfieldInfo.getFieldType()))) {
				defaultChange = false;
			}
		}

		if (defaultChange) {
			logDebug(
					"Default Change: fieldName = {0}, column = {1}, columnInfo.getDefaultVal() = {2}, sqlfieldInfo.getDefaultVal() = {3}, sqlDataTypePolicy.getAltDefault() = {4}...",
					sqlfieldInfo.getName(), sqlfieldInfo.getColumnName(), columnInfo.getDefaultVal(),
					sqlfieldInfo.getDefaultVal(), sqlDataTypePolicy.getAltDefault(sqlfieldInfo.getFieldType()));
		}

		final boolean typeChange = !sqlDataTypePolicy.getTypeName().equals(columnInfo.getTypeName().toUpperCase());
		if (typeChange) {
			logDebug("Type Change: columnInfo.getTypeName() = {0}, sqlDataTypePolicy.getTypeName()= {1}...",
					columnInfo.getTypeName(), sqlDataTypePolicy.getTypeName());
		}

		boolean lenChange = false;
		if (!sqlDataTypePolicy.isFixedLength()) {
			if (sqlfieldInfo.getLength() > 0) {
				if (sqlfieldInfo.getLength() != columnInfo.getSize()) {
					lenChange = true;
				}
			}

			if (sqlfieldInfo.getPrecision() > 0) {
				if (sqlfieldInfo.getPrecision() != columnInfo.getSize()) {
					lenChange = true;
				}
			}

			if (sqlfieldInfo.getScale() > 0) {
				if (sqlfieldInfo.getScale() != columnInfo.getDecimalDigits()) {
					lenChange = true;
				}
			}
		}

		if (lenChange) {
			logDebug(
					"Length Change: columnInfo.getSize() = {0}, columnInfo.getDecimalDigits() = {1}, sqlfieldInfo.getLength() = {2}, sqlfieldInfo.getPrecision() = {3}, sqlfieldInfo.getScale() = {4}...",
					columnInfo.getSize(), columnInfo.getDecimalDigits(), sqlfieldInfo.getLength(),
					sqlfieldInfo.getPrecision(), sqlfieldInfo.getScale());
		}

		return new SqlColumnAlterInfo(nullableChange, defaultChange, typeChange, lenChange);
	}

	private boolean isSwappableValues(String origin, String alternative) {
		if (origin == null && "0".equals(alternative)) {
			return true;
		}

		Set<String> set = swappableValueSet.get(origin);
		return set != null && set.contains(alternative);
	}

	protected class TableConstraint {

		private String name;

		private String tableName;

		private Set<String> columns;

		private boolean unique;

		public TableConstraint(String name, String tableName, boolean unique) {
			this.columns = new HashSet<String>();
			this.name = name;
			this.tableName = tableName;
			this.unique = unique;
		}

		public void addColumn(String columnName) {
			columns.add(columnName);
		}

		public String getName() {
			return name;
		}

		public String getTableName() {
			return tableName;
		}

		public Set<String> getColumns() {
			return columns;
		}

		public boolean isForeignKey() {
			return tableName != null;
		}

		public boolean isUniqueConst() {
			return tableName == null && unique;
		}

		public boolean isIndex() {
			return tableName == null && !unique;
		}
	}

}
