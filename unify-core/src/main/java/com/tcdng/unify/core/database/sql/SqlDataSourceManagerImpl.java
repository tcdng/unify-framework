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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.database.DataSourceManager;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.SqlUtils;
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
    public void manageDataSource(String dataSourceName) throws UnifyException {
        SqlDataSource sqlDataSource = (SqlDataSource) getComponent(dataSourceName);
        Connection connection = (Connection) sqlDataSource.getConnection();
        try {
            logInfo("Scanning datasource {0} schema...", dataSourceName);
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            for (Class<?> entityClass : getTableEntities(dataSourceName)) {
                logDebug("Managing schema elements for table entity type {0}...", entityClass);
                manageTableEntitySchemaElements(databaseMetaData, sqlDataSource, entityClass);
            }

            for (Class<? extends Entity> entityClass : getViewEntities(dataSourceName)) {
                logDebug("Managing schema elements for view entity type {0}...", entityClass);
                manageViewEntitySchemaElements(databaseMetaData, sqlDataSource, entityClass);
            }
        } catch (SQLException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.DATASOURCEMANAGER_MANAGE_SCHEMA_ERROR, dataSourceName);
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

    private void manageTableEntitySchemaElements(DatabaseMetaData databaseMetaData, SqlDataSource sqlDataSource,
            Class<?> entityClass) throws UnifyException {
        SqlDataSourceDialect sqlDataSourceDialect = sqlDataSource.getDialect();
        SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(entityClass);

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = databaseMetaData.getConnection();
            // Manage entity table
            String appSchema = sqlDataSource.getAppSchema();
            List<String> tableUpdateSql = new ArrayList<String>();
            rs = databaseMetaData.getTables(null, appSchema, sqlEntityInfo.getTableName(), null);
            if (rs.next()) {
                // Table exists. Check for updates
                String tableType = rs.getString("TABLE_TYPE");
                if ("TABLE".equalsIgnoreCase(tableType)) {
                    Map<String, SqlColumnInfo> columnMap =
                            sqlDataSource.getColumnMap(appSchema, sqlEntityInfo.getTableName());
                    List<String> columnUpdateSql = detectColumnUpdates(sqlDataSourceDialect, sqlEntityInfo, columnMap);
                    tableUpdateSql.addAll(columnUpdateSql);
                } else {
                    throw new UnifyException(UnifyCoreErrorConstants.DATASOURCEMANAGER_UNABLE_TO_UPDATE_TABLE,
                            sqlDataSource.getName(), sqlEntityInfo.getTableName(), tableType);
                }

                SqlUtils.close(rs);

                Map<String, TableConstraint> managedTableConstraints = new HashMap<String, TableConstraint>();
                // Fetch foreign keys
                rs = databaseMetaData.getImportedKeys(null, appSchema, sqlEntityInfo.getTableName());
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
                rs = databaseMetaData.getIndexInfo(null, appSchema, sqlEntityInfo.getTableName(), false, false);
                while (rs.next()) {
                    String idxName = SqlUtils.resolveConstraintName(rs.getString("INDEX_NAME"),
                            sqlDataSourceDialect.isAllObjectsInLowerCase());
                    String idxColumnName = rs.getString("COLUMN_NAME");
                    if (StringUtils.isNotBlank(idxName) && StringUtils.isNotBlank(idxColumnName)) {
                        boolean unique = false;
                        if (sqlDataSourceDialect.isAllObjectsInLowerCase()) {
                            unique = idxName.indexOf(SqlUtils.IDENTIFIER_SUFFIX_UNIQUECONSTRAINT_LOWERCASE) > 0;
                        } else {
                            unique = idxName.indexOf(SqlUtils.IDENTIFIER_SUFFIX_UNIQUECONSTRAINT) > 0;
                        }

                        TableConstraint tConst = managedTableConstraints.get(idxName);
                        if (tConst == null) {
                            tConst = new TableConstraint(idxName, null, unique);
                            managedTableConstraints.put(idxName, tConst);
                        }

                        tConst.addColumn(idxColumnName);
                    }
                }

                List<String> createUpdateConstraintSql = new ArrayList<String>();
                // Detect foreign constraint changes
                if (forceForeignConstraints && sqlEntityInfo.isForeignKeys()) {
                    for (SqlForeignKeySchemaInfo sqlForeignKeyInfo : sqlEntityInfo.getForeignKeyList()) {
                        if (!sqlEntityInfo.getFieldInfo(sqlForeignKeyInfo.getFieldName()).isIgnoreFkConstraint()) {
                            SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getFieldInfo(sqlForeignKeyInfo.getFieldName());
                            String fkConstName = sqlFieldInfo.getConstraint();
                            TableConstraint fkConst = managedTableConstraints.get(fkConstName);
                            boolean update = true;
                            if (fkConst != null) {
                                // Check if foreign key matches database constraint
                                if (fkConst.isForeignKey() && fkConst.getColumns().size() == 1
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
                                createUpdateConstraintSql.add(sqlDataSourceDialect.generateAddForeignKeyConstraintSql(
                                        sqlEntityInfo, sqlForeignKeyInfo, formatSql));
                            }
                        }
                    }
                }

                // Detect unique constraint changes
                if (sqlEntityInfo.isUniqueConstraints()) {
                    for (SqlUniqueConstraintSchemaInfo sqlUniqueConstraintInfo : sqlEntityInfo.getUniqueConstraintList()
                            .values()) {
                        TableConstraint ucConst = managedTableConstraints.get(sqlUniqueConstraintInfo.getName());
                        boolean update = true;
                        if (ucConst != null) {
                            // Check if unique constant matches database constraint
                            if (ucConst.isUniqueConst() && matchIndexAllColumns(sqlEntityInfo,
                                    sqlUniqueConstraintInfo.getFieldNameList(), ucConst.getColumns())) {
                                // Perfect match. Remove from pending list and no need for update
                                managedTableConstraints.remove(sqlUniqueConstraintInfo.getName());
                                update = false;
                            }
                        }

                        if (update) {
                            createUpdateConstraintSql.add(sqlDataSourceDialect
                                    .generateAddUniqueConstraintSql(sqlEntityInfo, sqlUniqueConstraintInfo, formatSql));
                        }
                    }
                }

                // Detect index changes
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
                                    sqlIndexInfo, formatSql));
                        }
                    }
                }

                // Add drops first
                for (TableConstraint tConst : managedTableConstraints.values()) {
                    if (tConst.isForeignKey()) {
                        tableUpdateSql.add(sqlDataSourceDialect.generateDropForeignKeyConstraintSql(sqlEntityInfo,
                                tConst.getName(), formatSql));
                    } else if (tConst.isUniqueConst()) {
                        tableUpdateSql.add(sqlDataSourceDialect.generateDropUniqueConstraintSql(sqlEntityInfo,
                                tConst.getName(), formatSql));
                    } else {
                        tableUpdateSql.add(
                                sqlDataSourceDialect.generateDropIndexSql(sqlEntityInfo, tConst.getName(), formatSql));
                    }
                }

                // Then create changes
                tableUpdateSql.addAll(createUpdateConstraintSql);
            } else {
                logInfo("Creating datasource table {0}...", sqlEntityInfo.getTableName());

                // Create table
                tableUpdateSql.add(sqlDataSourceDialect.generateCreateTableSql(sqlEntityInfo, formatSql));

                // Create constraints and indexes
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

            // Manage entity view
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

    private boolean matchIndexAllColumns(SqlEntityInfo sqlEntityInfo, List<String> fieldNameList, Set<String> columns)
            throws UnifyException {
        if (fieldNameList.size() == columns.size()) {
            for (String fieldName : fieldNameList) {
                if (!columns.contains(sqlEntityInfo.getFieldInfo(fieldName).getColumnName())) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }

    private void manageViewEntitySchemaElements(DatabaseMetaData databaseMetaData, SqlDataSource sqlDataSource,
            Class<? extends Entity> entityClass) throws UnifyException {
        SqlDataSourceDialect sqlDataSourceDialect = sqlDataSource.getDialect();
        SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.getSqlEntityInfo(entityClass);

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = databaseMetaData.getConnection();
            String appSchema = sqlDataSource.getAppSchema();

            // Manage entity view
            List<String> viewUpdateSQL = new ArrayList<String>();
            if (sqlEntityInfo.isViewOnly()) {
                boolean isViewNew = false;
                boolean isDropView = false;
                rs = databaseMetaData.getTables(null, appSchema, sqlEntityInfo.getViewName(), null);
                if (rs.next()) {
                    String tableType = rs.getString("TABLE_TYPE");
                    if (!"VIEW".equalsIgnoreCase(tableType)) {
                        throw new UnifyException(UnifyCoreErrorConstants.DATASOURCEMANAGER_UNABLE_TO_UPDATE_TABLE,
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
                    viewUpdateSQL.add(sqlDataSourceDialect.generateCreateViewSql(sqlEntityInfo, formatSql));
                }
            }

            // Apply updates
            for (String sql : viewUpdateSQL) {
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

    private List<Class<?>> getTableEntities(String dataSourceName) throws UnifyException {
        SqlDataSource sqlDataSource = (SqlDataSource) getComponent(dataSourceName);
        List<Class<?>> entityTypeList = new ArrayList<Class<?>>();
        for (Class<?> entityClass : sqlDataSource.getTableEntityTypes()) {
            logDebug("Building dependency list for entity type {0}", entityClass);
            buildDependencyList(sqlDataSource, entityTypeList, entityClass);
        }
        return entityTypeList;
    }

    private List<Class<? extends Entity>> getViewEntities(String dataSourceName) throws UnifyException {
        SqlDataSource sqlDataSource = (SqlDataSource) getComponent(dataSourceName);
        return sqlDataSource.getViewEntityTypes();
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

    private boolean matchViewColumns(SqlEntityInfo sqlEntityInfo, Set<String> columnNames) {
        for (SqlFieldInfo sqlfieldInfo : sqlEntityInfo.getListFieldInfos()) {
            if (!columnNames.contains(sqlfieldInfo.getColumnName())) {
                return false;
            }
        }

        return true;
    }

    private List<String> detectColumnUpdates(SqlDataSourceDialect sqlDataSourceDialect, SqlEntityInfo sqlEntityInfo,
            Map<String, SqlColumnInfo> columnInfos) throws UnifyException {
        List<String> columnUpdateSql = new ArrayList<String>();
        for (SqlFieldInfo sqlfieldInfo : sqlEntityInfo.getFieldInfos()) {
            SqlColumnInfo sqlColumnInfo = columnInfos.remove(sqlfieldInfo.getColumnName());
            if (sqlColumnInfo == null) {
                // New column
                columnUpdateSql.add(sqlDataSourceDialect.generateAddColumn(sqlEntityInfo, sqlfieldInfo, formatSql));
            } else {
                SqlColumnAlterInfo columnAlterInfo =
                        checkSqlColumnAltered(sqlDataSourceDialect, sqlfieldInfo, sqlColumnInfo);
                if (columnAlterInfo.isAltered()) {
                    // Alter column
                    columnUpdateSql.addAll(sqlDataSourceDialect.generateAlterColumn(sqlEntityInfo, sqlfieldInfo,
                            columnAlterInfo, formatSql));
                }
            }
        }

        // Make abandoned columns nullable
        for (SqlColumnInfo sqlColumnInfo : columnInfos.values()) {
            if (!sqlColumnInfo.isNullable()) {
                // Alter column nullable
                columnUpdateSql
                        .add(sqlDataSourceDialect.generateAlterColumnNull(sqlEntityInfo, sqlColumnInfo, formatSql));
            }
        }

        return columnUpdateSql;
    }

    private SqlColumnAlterInfo checkSqlColumnAltered(SqlDataSourceDialect sqlDataSourceDialect,
            SqlFieldInfo sqlfieldInfo, SqlColumnInfo columnInfo) throws UnifyException {
        boolean nullableChange = columnInfo.isNullable() != sqlfieldInfo.isNullable();
        if (nullableChange) {
            logDebug("Nullable Change: columnInfo.isNullable() = {0}, sqlfieldInfo.isNullable() = {1}...",
                    columnInfo.isNullable(), sqlfieldInfo.isNullable());
        }

        SqlDataTypePolicy sqlDataTypePolicy = sqlDataSourceDialect.getSqlTypePolicy(sqlfieldInfo.getColumnType());
        boolean defaultChange = !DataUtils.equals(columnInfo.getDefaultVal(), sqlfieldInfo.getDefaultVal());
        if (defaultChange && StringUtils.isBlank(sqlfieldInfo.getDefaultVal())) {
            if (columnInfo.getDefaultVal().equals(sqlDataTypePolicy.getAltDefault(sqlfieldInfo.getFieldType()))) {
                defaultChange = false;
            }
        }

        if (defaultChange) {
            logDebug(
                    "Default Change: columnInfo.getDefaultVal() = {0}, sqlfieldInfo.getDefaultVal() = {1}, sqlDataTypePolicy.getAltDefault() = {2}...",
                    columnInfo.getDefaultVal(), sqlfieldInfo.getDefaultVal(),
                    sqlDataTypePolicy.getAltDefault(sqlfieldInfo.getFieldType()));
        }

        boolean typeChange = columnInfo.getSqlType() != sqlDataTypePolicy.getSqlType();
        if (typeChange) {
            logDebug("Type Change: columnInfo.getSqlType() = {0}, sqlDataTypePolicy.getSqlType() = {1}...",
                    columnInfo.getSqlType(), sqlDataTypePolicy.getSqlType());
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

    private class TableConstraint {

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

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{name = ").append(name);
            sb.append(", tableName = ").append(tableName);
            sb.append(", unique = ").append(unique);
            sb.append(", columns = ").append(columns).append("}");
            return sb.toString();
        }
    }
}
