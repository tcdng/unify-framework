/*
 * Copyright 2018 The Code Department
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
package com.tcdng.unify.core.database;

import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Configuration;
import com.tcdng.unify.core.annotation.Singleton;
import com.tcdng.unify.core.database.sql.SqlDataSourceImpl;

/**
 * Test data source. Uses HSQLDB.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Singleton(true)
@Component("test-sqldatasource")
@Configuration({ @Configurable(property = "driver", value = "org.hsqldb.jdbcDriver"),
        @Configurable(property = "connectionUrl", value = "jdbc:hsqldb:mem:test"),
        @Configurable(property = "appSchema", value = "PUBLIC"),
        @Configurable(property = "getConnectionTimeout", value = "1000"),
        @Configurable(property = "dialect", value = "hsqldb-dialect"),
        @Configurable(property = "shutdownOnTerminate", value = "true") })
public class TestSqlDataSource extends SqlDataSourceImpl {

}
