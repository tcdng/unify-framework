/*
 * Copyright 2018-2025 The Code Department.
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
import com.tcdng.unify.core.Setting;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.FactoryMap;

/**
 * Default dynamic SQL database manager implementation.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASEMANAGER)
public class DynamicSqlDatabaseManagerImpl extends AbstractUnifyComponent implements DynamicSqlDatabaseManager {

    private FactoryMap<String, DynamicSqlDatabase> dynamicSqlDatabases;

    public DynamicSqlDatabaseManagerImpl() {
        dynamicSqlDatabases = new FactoryMap<String, DynamicSqlDatabase>() {

            @Override
            protected DynamicSqlDatabase create(String dataSourceConfigName, Object... params) throws Exception {
                return (DynamicSqlDatabase) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASE,
                        new Setting("dataSourceConfigName", dataSourceConfigName));
            }
        };
    }

    @Override
    public DynamicSqlDatabase getDynamicSqlDatabase(String dataSourceConfigName) throws UnifyException {
        return dynamicSqlDatabases.get(dataSourceConfigName);
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

}
