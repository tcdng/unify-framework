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

package com.tcdng.unify.core.database.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.constant.ForceConstraints;
import com.tcdng.unify.core.constant.LocaleType;
import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.SqlUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Default implementation of SQL schema manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_SQLSCHEMAMANAGER)
public class SqlSchemaManagerImpl extends AbstractSqlSchemaManager {

    private static final Map<String, Set<String>> swappableValueSet;

    static {
        swappableValueSet = new HashMap<String, Set<String>>();
        swappableValueSet.put("0", new HashSet<String>(Arrays.asList("(0)")));
    }

    @Override
    public void manageTableSchema(SqlDataSource sqlDataSource, SqlSchemaManagerOptions options,
            List<Class<?>> entityClasses) throws UnifyException {
        Connection connection = (Connection) sqlDataSource.getConnection();
        try {
            logInfo("Scanning datasource {0} schema...", sqlDataSource.getPreferredName());
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            for (Class<?> entityClass : entityClasses) {
                logDebug("Managing schema elements for table entity type {0}...", entityClass);
                manageTableSchema(databaseMetaData, sqlDataSource, entityClass, options);
            }
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
            logInfo("Scanning datasource {0} schema...", sqlDataSource.getPreferredName());
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            for (Class<? extends Entity> entityClass : entityClasses) {
                logDebug("Managing schema elements for view entity type {0}...", entityClass);
                manageViewSchema(databaseMetaData, sqlDataSource, entityClass, options);
            }
        } catch (SQLException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.SQLSCHEMAMANAGER_MANAGE_SCHEMA_ERROR,
                    sqlDataSource.getPreferredName());
        } finally {
            sqlDataSource.restoreConnection(connection);
        }
    }

    @Override
    public void dropViewSchema(SqlDataSource sqlDataSource, SqlSchemaManagerOptions options,
            List<Class<?>> entityClasses) throws UnifyException {
        Connection connection = (Connection) sqlDataSource.getConnection();
        try {
            logInfo("Scanning datasource {0} schema...", sqlDataSource.getPreferredName());
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            for (Class<?> entityClass : entityClasses) {
                logDebug("Dropping schema elements for view entity type {0}...", entityClass);
                dropViewSchema(databaseMetaData, sqlDataSource, entityClass, options);
            }
        } catch (SQLException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.SQLSCHEMAMANAGER_MANAGE_SCHEMA_ERROR,
                    sqlDataSource.getPreferredName());
        } finally {
            sqlDataSource.restoreConnection(connection);
        }
    }

    @Override
    public List<Class<?>> buildDependencyList(SqlDataSource sqlDataSource, List<Class<?>> entityClasses)
            throws UnifyException {
        List<Class<?>> resultList = new ArrayList<Class<?>>();
        for (Class<?> entityClass : entityClasses) {
            buildDependencyList(sqlDataSource, resultList, entityClass);
        }

        return resultList;
    }

    private void buildDependencyList(SqlDataSource sqlDataSource, List<Class<?>> entityTypeList, Class<?> entityClass)
            throws UnifyException {
        logDebug("Building dependency list for entity type [{0}]...", entityClass);
        SqlDataSourceDialect sqlDataSourceDialect = sqlDataSource.getDialect();
        SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.findSqlEntityInfo(entityClass);

        for (SqlFieldInfo sqlFieldInfo : sqlEntityInfo.getManagedFieldInfos()) {
            if (sqlFieldInfo.isForeignKey()) {
                SqlEntityInfo fkSqlEntityInfo = sqlFieldInfo.getForeignEntityInfo();
                if (!sqlEntityInfo.getTableName().equals(fkSqlEntityInfo.getTableName())) {
                    buildDependencyList(sqlDataSource, entityTypeList, fkSqlEntityInfo.getKeyClass());
                }
            }
        }

        if (!entityTypeList.contains(entityClass)) {
            entityTypeList.add(entityClass);
        }
    }

    private void manageTableSchema(DatabaseMetaData databaseMetaData, SqlDataSource sqlDataSource, Class<?> entityClass,
            SqlSchemaManagerOptions options) throws UnifyException {
        SqlDataSourceDialect sqlDataSourceDialect = sqlDataSource.getDialect();
        SqlEntityInfo sqlEntityInfo = sqlDataSourceDialect.findSqlEntityInfo(entityClass);
        if (sqlEntityInfo.isSchemaAlreadyManaged()) {
            return;
        }

        final PrintFormat printFormat = options.getPrintFormat();
        final ForceConstraints forceConstraints = options.getForceConstraints();

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = databaseMetaData.getConnection();
            // Manage entity table
            String schema = sqlEntityInfo.getSchema();
            if (StringUtils.isBlank(schema)) {
                schema = sqlDataSource.getAppSchema();
            }

            logDebug("Getting metadata for table [{0}] in schema [{1}]...", sqlEntityInfo.getTableName(), schema);
            List<String> tableUpdateSql = new ArrayList<String>();
            rs = databaseMetaData.getTables(null, schema, sqlEntityInfo.getTableName(), null);
            if (rs.next()) {
                // Table exists. Check for updates
                String tableType = rs.getString("TABLE_TYPE");
                if ("TABLE".equalsIgnoreCase(tableType)) {
                    Map<String, SqlColumnInfo> columnMap = sqlDataSource.getColumnMap(schema,
                            sqlEntityInfo.getTableName());
                    List<String> columnUpdateSql = detectColumnUpdates(sqlDataSourceDialect, sqlEntityInfo, columnMap,
                            printFormat);
                    tableUpdateSql.addAll(columnUpdateSql);
                } else {
                    throw new UnifyException(UnifyCoreErrorConstants.SQLSCHEMAMANAGER_UNABLE_TO_UPDATE_TABLE,
                            sqlDataSource.getName(), sqlEntityInfo.getTableName(), tableType);
                }

                SqlUtils.close(rs);

                Map<String, TableConstraint> managedTableConstraints = new HashMap<String, TableConstraint>();
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

                List<String> createUpdateConstraintSql = new ArrayList<String>();
                // Detect foreign constraint changes
                if (forceConstraints.isTrue() && sqlEntityInfo.isManagedForeignKeys()) {
                    for (SqlForeignKeySchemaInfo sqlForeignKeyInfo : sqlEntityInfo.getManagedForeignKeyList()) {
                        if (!sqlEntityInfo.getManagedFieldInfo(sqlForeignKeyInfo.getFieldName())
                                .isIgnoreFkConstraint()) {
                            SqlFieldInfo sqlFieldInfo = sqlEntityInfo
                                    .getManagedFieldInfo(sqlForeignKeyInfo.getFieldName());
                            String fkConstName = sqlFieldInfo.getConstraint();
                            TableConstraint fkConst = managedTableConstraints.get(fkConstName);
                            boolean update = true;
                            if (fkConst != null) {
                                logDebug(
                                        "Checking foreign key: fkConst = [{0}], entity.tableName = [{1}], field.columnName = [{2}]...",
                                        fkConst, sqlFieldInfo.getForeignEntityInfo().getTableName(),
                                        sqlFieldInfo.getForeignFieldInfo().getColumnName());
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
                                createUpdateConstraintSql.add(sqlDataSourceDialect.generateAddForeignKeyConstraintSql(
                                        sqlEntityInfo, sqlForeignKeyInfo, printFormat));
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
                            createUpdateConstraintSql.add(sqlDataSourceDialect.generateAddUniqueConstraintSql(
                                    sqlEntityInfo, sqlUniqueConstraintInfo, printFormat));
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
                                    sqlIndexInfo, printFormat));
                        }
                    }
                }

                // Add drops first
                for (TableConstraint tConst : managedTableConstraints.values()) {
                    if (tConst.isForeignKey()) {
                        tableUpdateSql.add(sqlDataSourceDialect.generateDropForeignKeyConstraintSql(sqlEntityInfo,
                                tConst.getName(), printFormat));
                    } else if (tConst.isUniqueConst()) {
                        tableUpdateSql.add(sqlDataSourceDialect.generateDropUniqueConstraintSql(sqlEntityInfo,
                                tConst.getName(), printFormat));
                    } else {
                        tableUpdateSql.add(sqlDataSourceDialect.generateDropIndexSql(sqlEntityInfo, tConst.getName(),
                                printFormat));
                    }
                }

                // Then create changes
                tableUpdateSql.addAll(createUpdateConstraintSql);
            } else {
                logInfo("Creating datasource table {0}...", sqlEntityInfo.getTableName());

                // Create table
                tableUpdateSql.add(sqlDataSourceDialect.generateCreateTableSql(sqlEntityInfo, printFormat));

                // Create constraints and indexes
                if (forceConstraints.isTrue() && sqlEntityInfo.isManagedForeignKeys()) {
                    for (SqlForeignKeySchemaInfo sqlForeignKeyInfo : sqlEntityInfo.getManagedForeignKeyList()) {
                        if (!sqlEntityInfo.getManagedFieldInfo(sqlForeignKeyInfo.getFieldName())
                                .isIgnoreFkConstraint()) {
                            tableUpdateSql.add(sqlDataSourceDialect.generateAddForeignKeyConstraintSql(sqlEntityInfo,
                                    sqlForeignKeyInfo, printFormat));
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

            boolean isTableNewOrAltered = !tableUpdateSql.isEmpty();

            // Manage entity view
            List<String> viewUpdateSQL = new ArrayList<String>();
            if (sqlEntityInfo.isViewable()) {
                boolean isDropView = false;
                rs = databaseMetaData.getTables(null, schema, sqlEntityInfo.getViewName(), null);
                if (rs.next()) {
                    isDropView = isTableNewOrAltered;
                    String tableType = rs.getString("TABLE_TYPE");
                    if (!"VIEW".equalsIgnoreCase(tableType)) {
                        throw new UnifyException(UnifyCoreErrorConstants.SQLSCHEMAMANAGER_UNABLE_TO_UPDATE_TABLE,
                                sqlDataSource.getName(), sqlEntityInfo.getViewName(), tableType);
                    }

                    if (!isDropView) {
                        // Check is list-only fields have changed
                        isDropView = !matchViewColumns(sqlEntityInfo,
                                sqlDataSource.getColumns(schema, sqlEntityInfo.getViewName()));
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
                    viewUpdateSQL.add(sqlDataSourceDialect.generateCreateViewSql(sqlEntityInfo, printFormat));
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

            // Update static reference data
            if (EnumConst.class.isAssignableFrom(entityClass)) {
                StaticList sla = entityClass.getAnnotation(StaticList.class);
                if (sla != null) {
                    logDebug("Updating static data for reference {0}...", sqlEntityInfo.getEnumConstClass());
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
                            logDebug("Insert static record with code = [{0}] and description = [{1}]...", code,
                                    description);
                            pstmt = connection.prepareStatement("INSERT INTO " + sqlEntityInfo.getPreferredTableName()
                                    + " (" + codeFieldInfo.getPreferredColumnName() + ", "
                                    + descFieldInfo.getPreferredColumnName() + ") VALUES (?, ?)");
                            pstmt.setString(1, code);
                            pstmt.setString(2, description);
                            pstmt.executeUpdate();
                            SqlUtils.close(pstmt);
                        } else {
                            logDebug("Updating description = [{0}] for static record with code = [{1}] ...",
                                    description, code);
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
            if (sqlEntityInfo.isViewOnly()) {
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
            throw new UnifyException(e, UnifyCoreErrorConstants.SQLSCHEMAMANAGER_MANAGE_SCHEMA_ERROR,
                    sqlDataSource.getName());
        } finally {
            SqlUtils.close(rs);
            SqlUtils.close(pstmt);
        }
    }

    private void dropViewSchema(DatabaseMetaData databaseMetaData, SqlDataSource sqlDataSource,
            Class<?> entityClass, SqlSchemaManagerOptions options) throws UnifyException {
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
                logDebug("Executing managed datasource script {0}...", dropViewSQL);
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

    private List<String> detectColumnUpdates(SqlDataSourceDialect sqlDataSourceDialect, SqlEntityInfo sqlEntityInfo,
            Map<String, SqlColumnInfo> columnInfos, PrintFormat printFormat) throws UnifyException {
        List<String> columnUpdateSql = new ArrayList<String>();
        for (SqlFieldInfo sqlfieldInfo : sqlEntityInfo.getManagedFieldInfos()) {
            SqlColumnInfo sqlColumnInfo = columnInfos.remove(sqlfieldInfo.getColumnName());
            if (sqlColumnInfo == null) {
                // New column
                columnUpdateSql.add(sqlDataSourceDialect.generateAddColumn(sqlEntityInfo, sqlfieldInfo, printFormat));
            } else {
                SqlColumnAlterInfo columnAlterInfo = checkSqlColumnAltered(sqlDataSourceDialect, sqlfieldInfo,
                        sqlColumnInfo);
                if (columnAlterInfo.isAltered()) {
                    // Alter column
                    columnUpdateSql.addAll(sqlDataSourceDialect.generateAlterColumn(sqlEntityInfo, sqlfieldInfo,
                            columnAlterInfo, printFormat));
                }
            }
        }

        // Make abandoned columns nullable
        for (SqlColumnInfo sqlColumnInfo : columnInfos.values()) {
            if (!sqlColumnInfo.isNullable()) {
                // Alter column nullable
                columnUpdateSql
                        .add(sqlDataSourceDialect.generateAlterColumnNull(sqlEntityInfo, sqlColumnInfo, printFormat));
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
        boolean defaultChange = !DataUtils.equals(columnInfo.getDefaultVal(), sqlfieldInfo.getDefaultVal())
                && !isSwappableValues(sqlfieldInfo.getDefaultVal(), columnInfo.getDefaultVal());
        if (defaultChange && StringUtils.isBlank(sqlfieldInfo.getDefaultVal())) {
            if (StringUtils.isBlank(columnInfo.getDefaultVal()) || columnInfo.getDefaultVal()
                    .equals(sqlDataTypePolicy.getAltDefault(sqlfieldInfo.getFieldType()))) {
                defaultChange = false;
            }
        }

        if (defaultChange) {
            logDebug(
                    "Default Change: columnInfo.getDefaultVal() = {0}, sqlfieldInfo.getDefaultVal() = {1}, sqlDataTypePolicy.getAltDefault() = {2}...",
                    columnInfo.getDefaultVal(), sqlfieldInfo.getDefaultVal(),
                    sqlDataTypePolicy.getAltDefault(sqlfieldInfo.getFieldType()));
        }

        boolean typeChange = !isSwappableSqlTypes(sqlDataTypePolicy.getSqlType(), columnInfo.getSqlType() );
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

    private boolean isSwappableSqlTypes(int srcType, int destType) {
        return (srcType == destType) || ((srcType == Types.DECIMAL) && (destType == Types.NUMERIC));
    }
    
    private boolean isSwappableValues(String origin, String alternative) {
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
