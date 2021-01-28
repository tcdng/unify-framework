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

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.dynamic.DynamicEntityInfo;
import com.tcdng.unify.core.database.sql.SqlDataSource;
import com.tcdng.unify.core.database.sql.SqlDatabase;
import com.tcdng.unify.core.database.sql.SqlSchemaManager;
import com.tcdng.unify.core.database.sql.SqlSchemaManagerOptions;
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
    
    @Configurable
    private RuntimeJavaClassManager runtimeJavaClassManager;

    @Configurable
    private SqlSchemaManager sqlSchemaManager;
    
    @SuppressWarnings({ "unchecked" })
    @Override
    public Class<? extends Entity> loadDynamicSqlEntity(SqlDatabase db, DynamicEntityInfo dynamicEntityInfo)
            throws UnifyException {
        // Create entity class
        String src = DynamicEntityUtils.generateEntityJavaClassSource(dynamicEntityInfo);
        Class<? extends Entity> entityClass = (Class<? extends Entity>) runtimeJavaClassManager
                .compileAndLoadJavaClass(dynamicEntityInfo.getClassName(), src);
        
        // Register in data source
        SqlDataSource sqlDataSource = (SqlDataSource) db.getDataSource();
        sqlDataSource.getDialect().createSqlEntityInfo(entityClass);
        
        // Manage schema
        SqlSchemaManagerOptions options = new SqlSchemaManagerOptions();
        sqlSchemaManager.manageTableSchema(sqlDataSource, options, entityClass);
        sqlSchemaManager.manageViewSchema(sqlDataSource, options, entityClass);
        return entityClass;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

}
