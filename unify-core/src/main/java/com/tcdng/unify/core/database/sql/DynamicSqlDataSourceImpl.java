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

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Singleton;
import com.tcdng.unify.core.database.DataSourceDialect;

/**
 * Dynamic SQL data source implementation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Singleton(false)
@Component(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCE)
public class DynamicSqlDataSourceImpl extends AbstractSqlDataSource implements DynamicSqlDataSource {

    @Override
    public void configure(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig) throws UnifyException {
        if (isConfigured()) {
            throw new UnifyException(UnifyCoreErrorConstants.DYNAMIC_DATASOURCE_ALREADY_CONFIGURED,
                    dynamicSqlDataSourceConfig.getName());
        }
        innerConfigure(dynamicSqlDataSourceConfig);
    }

    @Override
    public boolean reconfigure(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig) throws UnifyException {
        if (isConfigured()) {
            // Get old pool
            SqlConnectionPool oldConnectionPool = getSqlConnectionPool();

            // Create new pool
            innerConfigure(dynamicSqlDataSourceConfig);

            // Terminate old pool
            if (oldConnectionPool != null) {
                oldConnectionPool.terminate();
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean isConfigured() throws UnifyException {
        return getDriver() != null;
    }

    private void innerConfigure(DynamicSqlDataSourceConfig dataSourceConfig) throws UnifyException {
        setDialect((DataSourceDialect) getComponent(dataSourceConfig.getDialect()));
        setDriver(dataSourceConfig.getDriver());
        setAppSchema(dataSourceConfig.getSchema());
        setConnectionUrl(dataSourceConfig.getConnectionUrl());
        setUsername(dataSourceConfig.getDbUsername());
        setPassword(dataSourceConfig.getDbPassword());
        setMaxConnections(dataSourceConfig.getMaxConnection());
        setShutdownOnTerminate(dataSourceConfig.isShutdownOnTerminate());
        doInitConnectionPool();
    }
}
