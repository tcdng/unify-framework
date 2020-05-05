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

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.list.AbstractListCommand;

/**
 * Abstract base class for dynamic SQL data source list command.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractDynamicSqlDataSourceListCommand
        extends AbstractListCommand<AbstractDynamicSqlDataSourceListCommand.DynamicSqlParams> {

    @Configurable(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER)
    private DynamicSqlDataSourceManager dsManager;

    public AbstractDynamicSqlDataSourceListCommand() {
        super(DynamicSqlParams.class);
    }

    protected DynamicSqlDataSourceManager getDsManager() {
        return dsManager;
    }

    public class DynamicSqlParams {
        private String configName;
        private String schemaName;
        private String tableName;

        public DynamicSqlParams(String configName, String schemaName, String tableName) {
            super();
            this.configName = configName;
            this.schemaName = schemaName;
            this.tableName = tableName;
        }

        public String getConfigName() {
            return configName;
        }

        public String getSchemaName() {
            return schemaName;
        }

        public String getTableName() {
            return tableName;
        }

    }
}
