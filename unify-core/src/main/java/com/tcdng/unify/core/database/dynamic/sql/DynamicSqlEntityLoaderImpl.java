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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
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
 * @author Lateef Ojulari
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

    @SuppressWarnings("unchecked")
    @Override
    public List<Class<? extends Entity>> loadDynamicSqlEntities(SqlDatabase db,
            List<DynamicEntityInfo> dynamicEntityInfoList) throws UnifyException {
        List<JavaClassSource> sourceList = new ArrayList<JavaClassSource>();
        for (DynamicEntityInfo dynamicEntityInfo : dynamicEntityInfoList) {
            logDebug("Generating source file for entity class [{0}]...", dynamicEntityInfo.getClassName());
            JavaClassSource source = new JavaClassSource(dynamicEntityInfo.getClassName(),
                    DynamicEntityUtils.generateEntityJavaClassSource(dynamicEntityInfo));
            sourceList.add(source);
        }

        logDebug("Compiling and loading [{0}] entity classes...", sourceList.size());
        List<Class<?>> classList = runtimeJavaClassManager.compileAndLoadJavaClasses(sourceList);

        SqlDataSource sqlDataSource = (SqlDataSource) db.getDataSource();
        SqlDataSourceDialect sqlDataSourceDialect = sqlDataSource.getDialect();
        for (Class<?> entityClass : classList) {
            sqlDataSourceDialect.createSqlEntityInfo(entityClass);
        }

        List<Class<?>> seqList = sqlSchemaManager.buildDependencyList(sqlDataSource, classList);
        List<Class<?>> tableList = new ArrayList<Class<?>>();
        List<Class<? extends Entity>> resultList = new ArrayList<Class<? extends Entity>>();
        for (Class<?> clazz : seqList) {
            if (classList.contains(clazz)) {
                tableList.add(clazz);
                resultList.add((Class<? extends Entity>) clazz);
            }
        }

        beginClusterLock(DYNAMICSQLENTITYLOADER_LOCK);
        try {
            // Manage schema
            logDebug("Managing schema for entity classes...");
            SqlSchemaManagerOptions options = new SqlSchemaManagerOptions();
            sqlSchemaManager.manageTableSchema(sqlDataSource, options, tableList);
            sqlSchemaManager.manageViewSchema(sqlDataSource, options, resultList);
        } finally {
            endClusterLock(DYNAMICSQLENTITYLOADER_LOCK);
        }

        return resultList;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

}
