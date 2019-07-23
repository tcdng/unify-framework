/*
 * Copyright 2018-2019 The Code Department.
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.database.DataSourceManager;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Default implementation of an SQL data source manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_DATASOURCEMANAGER)
public class SqlDataSourceManagerImpl extends AbstractUnifyComponent implements DataSourceManager {

    @Configurable("true")
    private boolean forceForeignConstraints;

    @Configurable("false")
    private boolean formatSql;

    private FactoryMap<String, List<Class<?>>> datasourceEntityTypeMap;

    public SqlDataSourceManagerImpl() {
        datasourceEntityTypeMap = new FactoryMap<String, List<Class<?>>>() {
            @Override
            protected List<Class<?>> create(String datasource, Object... params) throws Exception {
                SqlDataSource sqlDataSource = (SqlDataSource) getComponent(datasource);
                List<Class<?>> entityTypeList = new ArrayList<Class<?>>();
                for (Class<?> entityClass : sqlDataSource.getEntityTypes()) {
                    logDebug("Building dependency list for entity type {0}", entityClass);
                    buildDependencyList(sqlDataSource, entityTypeList, entityClass);
                }
                return entityTypeList;
            }
        };
    }

    @Override
    public void initDataSource(String datasource) throws UnifyException {
        SqlDataSource sqlDataSource = (SqlDataSource) getComponent(datasource);
        Connection connection = (Connection) sqlDataSource.getConnection();
        PreparedStatement pstmt = null;
        try {
            for (SqlStatement sqlStatement : sqlDataSource.getDialect().prepareDataSourceInitStatements()) {
                pstmt = connection.prepareStatement(sqlStatement.getSql());
                pstmt.executeUpdate();
                SqlUtils.close(pstmt);
            }
        } catch (SQLException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.DATASOURCEMANAGER_MANAGE_SCHEMA_ERROR, datasource);
        } finally {
            SqlUtils.close(pstmt);
            sqlDataSource.restoreConnection(connection);
        }
    }

    @Override
    public void manageDataSource(String datasource) throws UnifyException {
        SqlDataSource sqlDataSource = (SqlDataSource) getComponent(datasource);
        Connection connection = (Connection) sqlDataSource.getConnection();
        try {
            logInfo("Scanning datasource {0} schema...", datasource);
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            for (Class<?> entityClass : datasourceEntityTypeMap.get(datasource)) {
                logDebug("Managing schema elements for entity type {0}...", entityClass);
                manageEntitySchemaElements(databaseMetaData, sqlDataSource, entityClass);
            }
        } catch (SQLException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.DATASOURCEMANAGER_MANAGE_SCHEMA_ERROR, datasource);
        } finally {
            sqlDataSource.restoreConnection(connection);
        }
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    private void manageEntitySchemaElements(DatabaseMetaData databaseMetaData, SqlDataSource sqlDataSource,
            Class<?> entityClass) throws UnifyException {
        SqlDataSourceDialect sqlDataSourceDialect = sqlDataSource.getDialect();
        SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(entityClass);

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = databaseMetaData.getConnection();
            // Manage table
            String appSchema = sqlDataSource.getAppSchema();
            List<String> tableUpdateSql = new ArrayList<String>();
            rs = databaseMetaData.getTables(null, appSchema, sqlEntityInfo.getTableName(), null);
            if (rs.next()) {
                // Table exists. Check for updates
                String tableType = rs.getString("TABLE_TYPE");
                if ("TABLE".equalsIgnoreCase(tableType)) {
                    Map<String, SqlColumnInfo> columnMap =
                            sqlDataSource.getColumnMap(appSchema, sqlEntityInfo.getTableName());
                    List<String> columnUpdateSql =
                            generateColumnUpdateSql(sqlDataSourceDialect, sqlEntityInfo, columnMap);
                    tableUpdateSql.addAll(columnUpdateSql);
                } else {
                    throw new UnifyException(UnifyCoreErrorConstants.DATASOURCEMANAGER_UNABLE_TO_UPDATE_TABLE,
                            sqlDataSource.getName(), sqlEntityInfo.getTableName(), tableType);
                }
            } else {
                logInfo("Creating datasource table {0}...", sqlEntityInfo.getTableName());

                // Create table with constraints and indexes
                tableUpdateSql.add(sqlDataSourceDialect.generateCreateTableSql(sqlEntityInfo, formatSql));

                if (forceForeignConstraints && sqlEntityInfo.isForeignKeys()) {
                    for (SqlForeignKeySchemaInfo sqlForeignKeyInfo : sqlEntityInfo.getForeignKeyList()) {
                        if (!sqlEntityInfo.getFieldInfo(sqlForeignKeyInfo.getFieldName()).isIgnoreFkConstraint()) {
                            tableUpdateSql.add(sqlDataSourceDialect.generateAddForeignKeyConstraintSql(sqlEntityInfo,
                                    sqlForeignKeyInfo, formatSql));
                        }
                    }
                }

                if (sqlEntityInfo.isUniqueConstraints()) {
                    for (SqlUniqueConstraintSchemaInfo sqlUniqueConstraintInfo : sqlEntityInfo.getUniqueConstraintList()
                            .values()) {
                        tableUpdateSql.add(sqlDataSourceDialect.generateAddUniqueConstraintSql(sqlEntityInfo,
                                sqlUniqueConstraintInfo, formatSql));
                    }
                }

                if (sqlEntityInfo.isIndexes()) {
                    for (SqlIndexSchemaInfo sqlIndexInfo : sqlEntityInfo.getIndexList().values()) {
                        tableUpdateSql.add(
                                sqlDataSourceDialect.generateCreateIndexSql(sqlEntityInfo, sqlIndexInfo, formatSql));
                    }
                }
            }
            SqlUtils.close(rs);

            boolean isTableNewOrAltered = !tableUpdateSql.isEmpty();

            // Manage view
            List<String> viewUpdateSQL = new ArrayList<String>();
            if (sqlEntityInfo.isViewable()) {
                boolean isDropView = false;
                rs = databaseMetaData.getTables(null, appSchema, sqlEntityInfo.getViewName(), null);
                if (rs.next()) {
                    isDropView = isTableNewOrAltered;
                    String tableType = rs.getString("TABLE_TYPE");
                    if (!"VIEW".equalsIgnoreCase(tableType)) {
                        throw new UnifyException(UnifyCoreErrorConstants.DATASOURCEMANAGER_UNABLE_TO_UPDATE_TABLE,
                                sqlDataSource.getName(), sqlEntityInfo.getViewName(), tableType);
                    }

                    if (!isDropView) {
                        // Check is list-only fields have changed
                        isDropView = !matchViewColumns(sqlEntityInfo,
                                sqlDataSource.getColumns(appSchema, sqlEntityInfo.getViewName()));
                    }
                } else {
                    // Force creation of view
                    isTableNewOrAltered = true;
                }
                SqlUtils.close(rs);

                if (isTableNewOrAltered || isDropView) {
                    // Check if we have to drop view first
                    if (isDropView) {
                        viewUpdateSQL.add(sqlDataSourceDialect.generateDropViewSql(sqlEntityInfo));
                    }

                    // Create view
                    viewUpdateSQL.add(sqlDataSourceDialect.generateCreateViewSql(sqlEntityInfo, formatSql));
                }
            }

            // Apply updates
            tableUpdateSql.addAll(viewUpdateSQL);
            for (String sql : tableUpdateSql) {
                logDebug("Executing managed datasource script {0}...", sql);
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
            throw new UnifyException(e, UnifyCoreErrorConstants.DATASOURCEMANAGER_MANAGE_SCHEMA_ERROR,
                    sqlDataSource.getName());
        } finally {
            SqlUtils.close(rs);
            SqlUtils.close(pstmt);
        }
    }

    private boolean matchViewColumns(SqlEntityInfo sqlEntityInfo, Set<String> columnNames) {
        for (SqlFieldInfo sqlfieldInfo : sqlEntityInfo.getListFieldInfos()) {
            if (!columnNames.contains(sqlfieldInfo.getColumnName())) {
                return false;
            }
        }

        return true;
    }

    private List<String> generateColumnUpdateSql(SqlDataSourceDialect sqlDataSourceDialect, SqlEntityInfo sqlEntityInfo,
            Map<String, SqlColumnInfo> columnInfos) throws UnifyException {
        List<String> columnUpdateSqlList = new ArrayList<String>();
        for (SqlFieldInfo sqlfieldInfo : sqlEntityInfo.getFieldInfos()) {
            SqlColumnInfo sqlColumnInfo = columnInfos.remove(sqlfieldInfo.getColumnName());
            if (sqlColumnInfo == null) {
                // New column
                columnUpdateSqlList.add(sqlDataSourceDialect.generateAddColumn(sqlEntityInfo, sqlfieldInfo, formatSql));
            } else {
                SqlColumnAlterInfo columnAlterInfo =
                        checkSqlColumnAltered(sqlDataSourceDialect, sqlfieldInfo, sqlColumnInfo);
                if (columnAlterInfo.isAltered()) {
                    // Alter column
                    columnUpdateSqlList.addAll(sqlDataSourceDialect.generateAlterColumn(sqlEntityInfo, sqlfieldInfo,
                            columnAlterInfo, formatSql));
                }
            }
        }

        // Make abandoned columns nullable
        for (SqlColumnInfo sqlColumnInfo : columnInfos.values()) {
            if (!sqlColumnInfo.isNullable()) {
                // Alter column nullable
                columnUpdateSqlList
                        .add(sqlDataSourceDialect.generateAlterColumnNull(sqlEntityInfo, sqlColumnInfo, formatSql));
            }
        }

        return columnUpdateSqlList;
    }

    private SqlColumnAlterInfo checkSqlColumnAltered(SqlDataSourceDialect sqlDataSourceDialect,
            SqlFieldInfo sqlfieldInfo, SqlColumnInfo columnInfo) throws UnifyException {
        boolean nullableChange = columnInfo.isNullable() != sqlfieldInfo.isNullable();

        boolean defaultChange = !(((StringUtils.isBlank(columnInfo.getDefaultVal()) || columnInfo.getDefaultVal()
                .equals(sqlDataSourceDialect.getSqlTypePolicy(sqlfieldInfo.getColumnType()).getAltDefault()))
                && StringUtils.isBlank(sqlfieldInfo.getDefaultVal()))
                || (columnInfo.getDefaultVal() != null
                        && columnInfo.getDefaultVal().equals(sqlfieldInfo.getDefaultVal())));

        SqlDataTypePolicy sqlDataTypePolicy = sqlDataSourceDialect.getSqlTypePolicy(sqlfieldInfo.getColumnType());
        boolean typeChange = columnInfo.getSqlType() != sqlDataTypePolicy.getSqlType();

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

        return new SqlColumnAlterInfo(nullableChange, defaultChange, typeChange, lenChange);
    }

    private void buildDependencyList(SqlDataSource sqlDataSource, List<Class<?>> entityTypeList, Class<?> entityClass)
            throws UnifyException {
        SqlDataSourceDialect sqlDataSourceDialect = sqlDataSource.getDialect();
        SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(entityClass);

        for (SqlFieldInfo sqlFieldInfo : sqlEntityInfo.getFieldInfos()) {
            if (sqlFieldInfo.isForeignKey()) {
                SqlEntityInfo fkSqlEntityInfo = sqlFieldInfo.getForeignEntityInfo();
                buildDependencyList(sqlDataSource, entityTypeList, fkSqlEntityInfo.getKeyClass());
            }
        }

        if (!entityTypeList.contains(entityClass)) {
            entityTypeList.add(entityClass);
        }
    }
}
