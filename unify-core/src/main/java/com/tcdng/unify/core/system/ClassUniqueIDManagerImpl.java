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
package com.tcdng.unify.core.system;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.tcdng.unify.common.annotation.ColumnType;
import com.tcdng.unify.common.constants.ApplicationCommonConstants;
import com.tcdng.unify.common.constants.DefaultColumnPositionConstants;
import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.constant.QueryAgainst;
import com.tcdng.unify.core.database.AbstractEntity;
import com.tcdng.unify.core.database.sql.NameSqlDataSourceSchema;
import com.tcdng.unify.core.database.sql.SqlDataSource;
import com.tcdng.unify.core.database.sql.SqlEntityInfo;
import com.tcdng.unify.core.database.sql.SqlFieldDimensions;
import com.tcdng.unify.core.database.sql.SqlFieldInfo;
import com.tcdng.unify.core.util.GetterSetterInfo;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.core.util.SqlUtils;

/**
 * Default class unique ID manager implementation.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_CLASSUNIQUEIDMANAGER)
public class ClassUniqueIDManagerImpl extends AbstractUnifyComponent implements ClassUniqueIDManager {

	private final Map<String, Long> uniqueIdsByClass;

	private SqlEntityInfo sqlEntityInfo;

	public ClassUniqueIDManagerImpl() {
		this.uniqueIdsByClass = new HashMap<String, Long>();
	}

	@Override
	public boolean ensureClassUniqueIDTable() throws UnifyException {
		logDebug("Ensuring class unique ID table...");
		SqlDataSource sqlDataSource = getComponent(SqlDataSource.class,
				ApplicationCommonConstants.APPLICATION_DATASOURCE);
		Connection connection = (Connection) sqlDataSource.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			final String tableName = sqlDataSource.getDialect().isAllObjectsInLowerCase()
					? ClassUniqueIDTableNameConstants.CLASSUNIQUEID_TABLE_NAME.toLowerCase()
					: ClassUniqueIDTableNameConstants.CLASSUNIQUEID_TABLE_NAME;
			logDebug("Detecting class unique ID table [{0}]...", tableName);
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			rs = databaseMetaData.getTables(null, sqlDataSource.getAppSchema(), tableName, null);
			if (rs.next()) {
				logDebug("Class unique ID table [{0}] detected.", tableName);
			} else {
				logDebug("Class unique ID table [{0}] not found. Attempting to create one...", tableName);
				final SqlEntityInfo _sqlEntityInfo = getClassUniqueIDEntityInfo();
				String sql = sqlDataSource.getDialect().generateCreateTableSql(_sqlEntityInfo, PrintFormat.PRETTY);
				logDebug("Executing script [{0}]...", sql);
				pstmt = connection.prepareStatement(sql);
				pstmt.executeUpdate();
				connection.commit();
				logDebug("Class unique ID table [{0}] successfully created.", tableName);
				return true;
			}
		} catch (SQLException e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.SQLSCHEMAMANAGER_MANAGE_SCHEMA_ERROR,
					sqlDataSource.getPreferredName());
		} finally {
			SqlUtils.close(rs);
			SqlUtils.close(pstmt);
			sqlDataSource.restoreConnection(connection);
		}

		return false;
	}

	@Override
	public Long getClassUniqueID(Class<?> clazz) throws UnifyException {
		final String className = clazz.getName();
		logDebug("Fetching class [{0}] unique ID ...", className);
		Long uniqueId = uniqueIdsByClass.get(className);
		if (uniqueId == null) {
			synchronized (this) {
				uniqueId = uniqueIdsByClass.get(className);
				if (uniqueId == null) {
					SqlDataSource sqlDataSource = getComponent(SqlDataSource.class,
							ApplicationCommonConstants.APPLICATION_DATASOURCE);
					Connection connection = (Connection) sqlDataSource.getConnection();
					PreparedStatement pstmt = null;
					ResultSet rs = null;
					try {
						final SqlEntityInfo _sqlEntityInfo = getClassUniqueIDEntityInfo();
						StringBuilder sb1 = new StringBuilder();
						sb1.append(sqlDataSource.getDialect().generateFindRecordSql(_sqlEntityInfo, QueryAgainst.TABLE))
								.append(" WHERE ")
								.append(_sqlEntityInfo.getFieldInfo("className").getPreferredColumnName())
								.append(" = ?");
						String sql1 = sb1.toString();
						logDebug("Executing script [{0}]...", sql1);
						pstmt = connection.prepareStatement(sql1);
						pstmt.setString(1, className);
						rs = pstmt.executeQuery();
						if (rs.next()) {
							uniqueId = rs.getLong(ClassUniqueIDTableNameConstants.CLASSUNIQUEID_ID);
						} else {
							SqlUtils.close(pstmt);
							StringBuilder sb2 = new StringBuilder();
							sb2.append("INSERT INTO ").append(_sqlEntityInfo.getSchemaTableName()).append(" (")
									.append(ClassUniqueIDTableNameConstants.CLASSUNIQUEID_CLASS_NAME)
									.append(") VALUES (?)");
							String sql2 = sb2.toString();
							logDebug("Executing script [{0}]...", sql2);
							pstmt = connection.prepareStatement(sql2);
							pstmt.setString(1, className);
							int count = pstmt.executeUpdate();
							if (count > 0) {
								SqlUtils.close(rs);
								SqlUtils.close(pstmt);
								pstmt = connection.prepareStatement(sql1);
								pstmt.setString(1, className);
								rs = pstmt.executeQuery();
								if (rs.next()) {
									uniqueId = rs.getLong(ClassUniqueIDTableNameConstants.CLASSUNIQUEID_ID);
								}
							}
						}
					} catch (SQLException e) {
						throw new UnifyException(e, UnifyCoreErrorConstants.SQLSCHEMAMANAGER_MANAGE_SCHEMA_ERROR,
								sqlDataSource.getPreferredName());
					} finally {
						SqlUtils.close(rs);
						SqlUtils.close(pstmt);
						sqlDataSource.restoreConnection(connection);
					}

					uniqueIdsByClass.put(className, uniqueId);
				}
			}
		}

		logDebug("Unique ID [{0}] fetched for class [{1}].", uniqueId, className);
		return uniqueId;
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	public static class ClassUniqueID extends AbstractEntity {

		private Long id;

		private String className;

		@Override
		public String getDescription() {
			return className;
		}

		@Override
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

	}

	private SqlEntityInfo getClassUniqueIDEntityInfo() throws UnifyException {
		if (sqlEntityInfo == null) {
			synchronized (this) {
				if (sqlEntityInfo == null) {
					SqlDataSource sqlDataSource = getComponent(SqlDataSource.class,
							ApplicationCommonConstants.APPLICATION_DATASOURCE);
					String tableName = sqlDataSource.getDialect().isAllObjectsInLowerCase()
							? ClassUniqueIDTableNameConstants.CLASSUNIQUEID_TABLE_NAME.toLowerCase()
							: ClassUniqueIDTableNameConstants.CLASSUNIQUEID_TABLE_NAME;
					;
					final String preferredTableName = sqlDataSource.getDialect().getPreferredName(tableName);
					final String schema = (String) getComponentConfig(NameSqlDataSourceSchema.class,
							ApplicationComponents.APPLICATION_DATASOURCE).getSettings().getSettingValue("appSchema");
					final String schemaTableName = SqlUtils.generateFullSchemaElementName(schema, preferredTableName);

					SqlFieldDimensions sqlFieldDimensions = new SqlFieldDimensions(0, 20, 0);
					Map<String, SqlFieldInfo> propertyInfoMap = new LinkedHashMap<String, SqlFieldInfo>();
					GetterSetterInfo getterSetterInfo = ReflectUtils.getGetterSetterInfo(ClassUniqueID.class, "id");
					final boolean isForeignKey = false;
					final boolean isIgnoreFkConstraint = false;
					final boolean isListOnly = false;
					final boolean isNullable = false;
					final boolean isFosterParentType = false;
					final boolean isFosterParentId = false;
					final boolean isCategoryColumn = false;
					final boolean isTenantId = false;
					final String mapped = null;
					final SqlFieldInfo idFieldInfo = new SqlFieldInfo(DefaultColumnPositionConstants.ID_POSITION,
							ColumnType.LONG, null, null, null, "id", ClassUniqueIDTableNameConstants.CLASSUNIQUEID_ID,
							sqlDataSource.getDialect()
									.getPreferredName(ClassUniqueIDTableNameConstants.CLASSUNIQUEID_ID),
							null, null, true, isForeignKey, isListOnly, isIgnoreFkConstraint, null, sqlFieldDimensions,
							isNullable, isFosterParentType, isFosterParentId, isCategoryColumn, isTenantId, mapped,
							null, ReflectUtils.getField(ClassUniqueID.class, "id"), getterSetterInfo.getGetter(),
							getterSetterInfo.getSetter(), sqlDataSource.getDialect().isAllObjectsInLowerCase());
					idFieldInfo.setAutoIncrement(true);

					sqlFieldDimensions = new SqlFieldDimensions(128, -1, -1);
					getterSetterInfo = ReflectUtils.getGetterSetterInfo(ClassUniqueID.class, "className");
					final SqlFieldInfo descFieldInfo = new SqlFieldInfo(DefaultColumnPositionConstants.COLUMN_POSITION,
							ColumnType.STRING, null, null, null, "className",
							ClassUniqueIDTableNameConstants.CLASSUNIQUEID_CLASS_NAME,
							sqlDataSource.getDialect()
									.getPreferredName(ClassUniqueIDTableNameConstants.CLASSUNIQUEID_CLASS_NAME),
							null, null, false, isForeignKey, isListOnly, isIgnoreFkConstraint, null, sqlFieldDimensions,
							isNullable, isFosterParentType, isFosterParentId, isCategoryColumn, isTenantId, mapped,
							null, ReflectUtils.getField(ClassUniqueID.class, "className"), getterSetterInfo.getGetter(),
							getterSetterInfo.getSetter(), sqlDataSource.getDialect().isAllObjectsInLowerCase());

					propertyInfoMap.put(idFieldInfo.getName(), idFieldInfo);
					propertyInfoMap.put(descFieldInfo.getName(), descFieldInfo);

					String tableAlias = "CUID0";
					if (sqlDataSource.getDialect().isAllObjectsInLowerCase()) {
						tableName = tableName.toLowerCase();
					}

					sqlEntityInfo = new SqlEntityInfo(null, ClassUniqueID.class, null, null, null, null, schema, tableName,
							preferredTableName, schemaTableName, tableAlias, tableName, preferredTableName,
							schemaTableName, idFieldInfo, null, null, null, null, null, propertyInfoMap, null, null,
							null, null, null, null, null, null, sqlDataSource.getDialect().isAllObjectsInLowerCase(),
							true);
				}
			}
		}

		return sqlEntityInfo;
	}

}
