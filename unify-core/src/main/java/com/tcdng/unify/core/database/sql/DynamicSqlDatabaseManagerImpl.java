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

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.Setting;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.FactoryMap;

/**
 * Default dynamic SQL database manager implementation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASEMANAGER)
public class DynamicSqlDatabaseManagerImpl extends AbstractUnifyComponent implements DynamicSqlDatabaseManager {

    private FactoryMap<String, DynamicSqlDatabase> dynamicSqlDatabases;

    public DynamicSqlDatabaseManagerImpl() {
        dynamicSqlDatabases = new FactoryMap<String, DynamicSqlDatabase>() {

            @Override
            protected DynamicSqlDatabase create(String dataSourceName, Object... params) throws Exception {
                // We put this here so we don have to do this check every time foe valid data source names.
                if (ApplicationComponents.APPLICATION_DATASOURCE.equals(dataSourceName)) {
                    throw new UnifyException(
                            UnifyCoreErrorConstants.DYNAMICDATABASE_ATTEMPT_TO_USE_APPLICATIONDATASOURCE);
                }

                return (DynamicSqlDatabase) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASE,
                        new Setting("dataSourceName", dataSourceName));
            }
        };
    }

    @Override
    public DynamicSqlDatabase getDynamicSqlDatabase(String dataSourceName) throws UnifyException {
        return dynamicSqlDatabases.get(dataSourceName);
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

}
