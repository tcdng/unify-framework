/*
 * Copyright (c) 2018-2025 The Code Department.
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Singleton;
import com.tcdng.unify.core.database.DataSource;
import com.tcdng.unify.core.database.sql.AbstractSqlDatabase;

/**
 * Convenient abstract base class for dynamic SQL databases.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Singleton(false)
public abstract class AbstractDynamicSqlDatabase extends AbstractSqlDatabase implements DynamicSqlDatabase {

    @Configurable
    private DynamicSqlDataSourceManager dynamicSqlDataSourceManager;

    @Configurable
    private String dataSourceConfigName;

    @Override
    public String getDataSourceName() throws UnifyException {
        return getDataSource().getName();
    }

    @Override
    public DataSource getDataSource() throws UnifyException {
        return dynamicSqlDataSourceManager.getDataSource(dataSourceConfigName);
    }

}
