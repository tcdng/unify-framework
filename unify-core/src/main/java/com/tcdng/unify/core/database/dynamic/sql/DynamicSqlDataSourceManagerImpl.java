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
package com.tcdng.unify.core.database.dynamic.sql;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.EntityType;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.database.DataSourceManagerOptions;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.NativeQuery;
import com.tcdng.unify.core.database.dynamic.DynamicEntityInfo;
import com.tcdng.unify.core.database.dynamic.DynamicFieldInfo;
import com.tcdng.unify.core.database.dynamic.DynamicForeignKeyFieldInfo;
import com.tcdng.unify.core.database.sql.AbstractSqlDataSourceManager;
import com.tcdng.unify.core.database.sql.SqlColumnInfo;
import com.tcdng.unify.core.database.sql.SqlDataSource;
import com.tcdng.unify.core.database.sql.SqlDataSourceDialect;
import com.tcdng.unify.core.database.sql.SqlTableInfo;
import com.tcdng.unify.core.database.sql.SqlTableType;
import com.tcdng.unify.core.runtime.RuntimeJavaClassManager;
import com.tcdng.unify.core.runtime.StringJavaClassSource;
import com.tcdng.unify.core.util.DynamicEntityUtils;
import com.tcdng.unify.core.util.LockUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Default implementation of dynamic SQL data source manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER)
public class DynamicSqlDataSourceManagerImpl extends AbstractSqlDataSourceManager
        implements DynamicSqlDataSourceManager {

    private static final String DYNAMICSQLDATASOURCEMNGR_APPLICATION = "app::dynSqlDsMngr";

    @Configurable
    private RuntimeJavaClassManager runtimeJavaClassManager;

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
    public Class<?> getDataSourceDynamicEntityClass(String dataSourceConfigName, String className)
            throws UnifyException {
        final String runtimeClassTblCategory =
                getDataSourceRuntimeClassCategory(dataSourceConfigName, EntityType.TABLE);
        if (runtimeJavaClassManager.getSavedJavaClassVersion(runtimeClassTblCategory, className) > 0) {
            return runtimeJavaClassManager.getSavedJavaClass(runtimeClassTblCategory, className);
        }

        return runtimeJavaClassManager.getSavedJavaClass(
                getDataSourceRuntimeClassCategory(dataSourceConfigName, EntityType.TABLE_EXT), className);
    }

    @Override
    public void createOrUpdateDataSourceDynamicEntitySchemaObjects(String dataSourceConfigName,
            List<DynamicEntityInfo> dynamicEntityInfoList) throws UnifyException {
        SqlDataSourceDialect sqlDataSourceDialect = getDynamicSqlDataSource(dataSourceConfigName).getDialect();
        String lockName = getDataSourceLockObject(dataSourceConfigName);
        beginClusterLock(lockName);
        try {
            // Compile classes first
            Map<String, UpdateInfo> updateEntityClassList = new LinkedHashMap<String, UpdateInfo>();
            for (DynamicEntityInfo dynamicEntityInfo : dynamicEntityInfoList) {
                generateAndCompileNewJavaClass(dataSourceConfigName, dynamicEntityInfo, updateEntityClassList);
            }

            // Construct entity information
            if (!updateEntityClassList.isEmpty()) {
                for (Map.Entry<String, UpdateInfo> entry : updateEntityClassList.entrySet()) {
                    UpdateInfo updateInfo = entry.getValue();
                    if (updateInfo.getOldEntityClass() != null) {
                        sqlDataSourceDialect.removeSqlEntityInfo(updateInfo.getOldEntityClass());
                    }

                    sqlDataSourceDialect.createSqlEntityInfo(
                            runtimeJavaClassManager.getSavedJavaClass(updateInfo.getCategory(), entry.getKey()));
                }

                manageDataSource(dataSourceConfigName, new DataSourceManagerOptions());
            }
        } finally {
            endClusterLock(lockName);
        }
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
    protected List<Class<?>> getTableEntityTypes(String dataSourceName, SqlDataSource sqlDataSource)
            throws UnifyException {
        List<Class<?>> entityTypeList = super.getTableEntityTypes(dataSourceName, sqlDataSource);
        entityTypeList.addAll(getRuntimeClasses(dataSourceName, EntityType.TABLE));
        return entityTypeList;
    }

    @Override
    protected List<Class<?>> getTableExtensionEntityTypes(String dataSourceName, SqlDataSource sqlDataSource)
            throws UnifyException {
        List<Class<?>> entityTypeList = super.getTableExtensionEntityTypes(dataSourceName, sqlDataSource);
        entityTypeList.addAll(getRuntimeClasses(dataSourceName, EntityType.TABLE_EXT));
        return entityTypeList;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<Class<? extends Entity>> getViewEntityTypes(String dataSourceName, SqlDataSource sqlDataSource)
            throws UnifyException {
        List<Class<? extends Entity>> entityTypeList = super.getViewEntityTypes(dataSourceName, sqlDataSource);
        for (Class<?> entityClass : getRuntimeClasses(dataSourceName, EntityType.VIEW)) {
            entityTypeList.add((Class<? extends Entity>) entityClass);
        }
        return entityTypeList;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {
        terminateAll();
    }

    private class UpdateInfo {

        private String category;

        private Class<?> oldEntityClass;

        public UpdateInfo(String category, Class<?> oldEntityClass) {
            this.category = category;
            this.oldEntityClass = oldEntityClass;
        }

        public String getCategory() {
            return category;
        }

        public Class<?> getOldEntityClass() {
            return oldEntityClass;
        }
    }

    private List<Class<?>> getRuntimeClasses(String dataSourceName, EntityType entityType) throws UnifyException {
        return runtimeJavaClassManager
                .getSavedJavaClasses(getDataSourceRuntimeClassCategory(dataSourceName, entityType));
    }

    private void generateAndCompileNewJavaClass(String dataSourceConfigName, DynamicEntityInfo dynamicEntityInfo,
            Map<String, UpdateInfo> updateEntityClassList) throws UnifyException {
        final String runtimeClassCategory = getDataSourceRuntimeClassCategory(dataSourceConfigName, dynamicEntityInfo.getType());
        final String className = dynamicEntityInfo.getClassName();
        final long newVersion = dynamicEntityInfo.getVersion();
        final long currentVersion = runtimeJavaClassManager.getSavedJavaClassVersion(runtimeClassCategory, className);
        if (currentVersion < newVersion) {
            Class<?> oldImplClass = null;
            if (currentVersion > 0) {
                oldImplClass = runtimeJavaClassManager.getSavedJavaClass(runtimeClassCategory, className);
            }

            // Compile and save parent entities first
            for (DynamicFieldInfo dynamicFieldInfo : dynamicEntityInfo.getFieldInfos()) {
                if (dynamicFieldInfo.getFieldType().isForeignKey()) {
                    generateAndCompileNewJavaClass(dataSourceConfigName,
                            ((DynamicForeignKeyFieldInfo) dynamicFieldInfo).getParentDynamicEntityInfo(),
                            updateEntityClassList);
                }
            }

            // Compile and save
            String src = DynamicEntityUtils.generateEntityJavaClassSource(dynamicEntityInfo);
            if (runtimeJavaClassManager.compileAndSaveJavaClass(runtimeClassCategory,
                    new StringJavaClassSource(className, src, newVersion))) {
                if (!updateEntityClassList.containsKey(className)) {
                    updateEntityClassList.put(className, new UpdateInfo(runtimeClassCategory, oldImplClass));
                }
            }
        }
    }

    private String getDataSourceLockObject(String runtimeClassCategory) throws UnifyException {
        return LockUtils.getStringLockObject(runtimeClassCategory);
    }

    private String getDataSourceRuntimeClassCategory(String dataSourceName, EntityType entityType) {
        return StringUtils.dotify(DYNAMICSQLDATASOURCEMNGR_APPLICATION, dataSourceName, entityType);
    }

    private DynamicSqlDataSource getDynamicSqlDataSource(String dataSourceConfigName) throws UnifyException {
        if (!dynamicSqlDataSourceMap.isKey(dataSourceConfigName)) {
            throw new UnifyException(UnifyCoreErrorConstants.DYNAMIC_DATASOURCE_IS_UNKNOWN, dataSourceConfigName);
        }

        return dynamicSqlDataSourceMap.get(dataSourceConfigName);
    }

    private void createAndInitDynamicSqlDataSource(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig)
            throws UnifyException {
        dynamicSqlDataSourceMap.get(dynamicSqlDataSourceConfig.getName(), dynamicSqlDataSourceConfig);
        DataSourceManagerOptions options = new DataSourceManagerOptions();
        initDataSource(dynamicSqlDataSourceConfig.getName(), options);
        manageDataSource(dynamicSqlDataSourceConfig.getName(), options);
    }

    private DynamicSqlDataSource getNewDynamicSqlDataSource(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig)
            throws UnifyException {
        DynamicSqlDataSource dynamicSqlDataSource =
                (DynamicSqlDataSource) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCE);
        dynamicSqlDataSource.configure(dynamicSqlDataSourceConfig);
        return dynamicSqlDataSource;
    }
}
