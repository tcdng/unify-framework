/*
 * Copyright 2018-2022 The Code Department.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.constant.ForceConstraints;
import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.dynamic.DynamicEntityInfo;
import com.tcdng.unify.core.database.sql.SqlDataSource;
import com.tcdng.unify.core.database.sql.SqlDataSourceDialect;
import com.tcdng.unify.core.database.sql.SqlDatabase;
import com.tcdng.unify.core.database.sql.SqlSchemaManager;
import com.tcdng.unify.core.database.sql.SqlSchemaManagerOptions;
import com.tcdng.unify.core.runtime.JavaClassSource;
import com.tcdng.unify.core.runtime.RuntimeJavaClassManager;
import com.tcdng.unify.core.util.DynamicEntityUtils;

/**
 * Default implementation of dynamic SQL entity loader.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_DYNAMICSQLENTITYLOADER)
public class DynamicSqlEntityLoaderImpl extends AbstractUnifyComponent implements DynamicSqlEntityLoader {

    private static final String DYNAMICSQLENTITYLOADER_LOCK = "dynamicsqlentityloader-lock";

    @Configurable
    private RuntimeJavaClassManager runtimeJavaClassManager;

    @Configurable
    private SqlSchemaManager sqlSchemaManager;

    @Override
    public Class<? extends Entity> loadDynamicSqlEntity(SqlDatabase db, DynamicEntityInfo dynamicEntityInfo)
            throws UnifyException {
        return this.loadDynamicSqlEntities(db, Arrays.asList(dynamicEntityInfo)).get(0);
    }

    public void setRuntimeJavaClassManager(RuntimeJavaClassManager runtimeJavaClassManager) {
        this.runtimeJavaClassManager = runtimeJavaClassManager;
    }

    public void setSqlSchemaManager(SqlSchemaManager sqlSchemaManager) {
        this.sqlSchemaManager = sqlSchemaManager;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Class<? extends Entity>> loadDynamicSqlEntities(SqlDatabase db,
            List<DynamicEntityInfo> dynamicEntityInfoList) throws UnifyException {
        logDebug("Generating source files for [{0}] entity classes...", dynamicEntityInfoList.size());
        List<JavaClassSource> sourceList = new ArrayList<JavaClassSource>();
        for (DynamicEntityInfo dynamicEntityInfo : dynamicEntityInfoList) {
            JavaClassSource source = new JavaClassSource(dynamicEntityInfo.getClassName(),
                    DynamicEntityUtils.generateEntityJavaClassSource(dynamicEntityInfo));
            sourceList.add(source);
        }
        logDebug("Source files successfully generated for [{0}] entity classes...", dynamicEntityInfoList.size());

        logDebug("Compiling and loading [{0}] entity classes...", sourceList.size());
        List<Class<? extends Entity>> classList = runtimeJavaClassManager.compileAndLoadJavaClasses(Entity.class,
                sourceList);

        final int len = dynamicEntityInfoList.size();
        List<Class<?>> managedClassList = new ArrayList<Class<?>>();
        for (int i = 0; i < len; i++) {
            if (dynamicEntityInfoList.get(i).isManaged()) {
                managedClassList.add(classList.get(i));
            }
        }

        logDebug("Creating entity class information for [{0}] managed classes ...", managedClassList.size());
        SqlDataSource sqlDataSource = (SqlDataSource) db.getDataSource();
        SqlDataSourceDialect sqlDataSourceDialect = sqlDataSource.getDialect();
        for (Class<?> entityClass : managedClassList) {
            sqlDataSourceDialect.createSqlEntityInfo(entityClass);
        }

        List<Class<?>> dependencyList = sqlSchemaManager.buildDependencyList(sqlDataSource, managedClassList);
        List<Class<?>> tableList = new ArrayList<Class<?>>();
        List<Class<? extends Entity>> viewList = new ArrayList<Class<? extends Entity>>();
        for (Class<?> clazz : dependencyList) {
            viewList.add((Class<? extends Entity>) clazz);
            if (managedClassList.contains(clazz)) {
                tableList.add(clazz);
            }
        }

        beginClusterLock(DYNAMICSQLENTITYLOADER_LOCK);
        try {
            SqlSchemaManagerOptions options = new SqlSchemaManagerOptions(PrintFormat.NONE,
                    ForceConstraints.fromBoolean(!getContainerSetting(boolean.class,
                            UnifyCorePropertyConstants.APPLICATION_FOREIGNKEY_EASE, false)));
            // TODO Check table or view change
            // boolean schemaChanged = sqlSchemaManager.detectTableSchemaChange(sqlDataSource, options, tableList);
            logDebug("Managing schema for [{0}] entity classes...", tableList.size());
            if (sqlDataSource.getDialect().isReconstructViewsOnTableSchemaUpdate()) {
                sqlSchemaManager.dropViewSchema(sqlDataSource, options, tableList);
            }

            sqlSchemaManager.manageTableSchema(sqlDataSource, options, tableList);
            sqlSchemaManager.manageViewSchema(sqlDataSource, options, viewList);
        } finally {
            endClusterLock(DYNAMICSQLENTITYLOADER_LOCK);
        }

        return classList;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

}
