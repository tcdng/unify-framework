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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.sql.SqlDataSource;

/**
 * Dynamic SQL data source.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface DynamicSqlDataSource extends SqlDataSource {

    /**
     * Configures data source using supplied configuration.
     * 
     * @param dynamicSqlDataSourceConfig
     *            the configuration to use
     * @throws UnifyException
     *             if data source is already configured. If an error occurs
     */
    void configure(DynamicSqlDataSourceConfig dynamicSqlDataSourceConfig) throws UnifyException;

    /**
     * Returns true if data source is configured.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isConfigured() throws UnifyException;
}
