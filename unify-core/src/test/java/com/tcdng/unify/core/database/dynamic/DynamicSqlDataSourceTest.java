/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.core.database.dynamic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.dynamic.sql.DynamicSqlDataSource;
import com.tcdng.unify.core.database.dynamic.sql.DynamicSqlDataSourceConfig;

/**
 * Dynamic SQL data source test.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DynamicSqlDataSourceTest extends AbstractUnifyComponentTest {

    @Test
    public void testDataSourceNotSingleton() throws Exception {
        DynamicSqlDataSource dsds1 =
                (DynamicSqlDataSource) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCE);
        DynamicSqlDataSource dsds2 =
                (DynamicSqlDataSource) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCE);
        assertFalse(dsds1 == dsds2);
    }

    @Test
    public void testNotConfiguredOnCreate() throws Exception {
        DynamicSqlDataSource dsds =
                (DynamicSqlDataSource) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCE);
        assertFalse(dsds.isConfigured());
    }

    @Test
    public void testConfigure() throws Exception {
        DynamicSqlDataSource dsds =
                (DynamicSqlDataSource) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCE);
        try {
            DynamicSqlDataSourceConfig config = getConfig(0);
            dsds.configure(config);
            assertTrue(dsds.isConfigured());
        } finally {
            dsds.terminate();
        }
    }

    @Test(expected = UnifyException.class)
    public void testMultipleConfigure() throws Exception {
        DynamicSqlDataSource dsds =
                (DynamicSqlDataSource) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCE);
        try {
            DynamicSqlDataSourceConfig config = getConfig(0);
            dsds.configure(config);
            dsds.configure(config);
        } finally {
            dsds.terminate();
        }
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }

    private DynamicSqlDataSourceConfig getConfig(int schemaIndex) {
        return new DynamicSqlDataSourceConfig("test-config", "hsqldb-dialect", "org.hsqldb.jdbcDriver",
                "jdbc:hsqldb:mem:dyntest" + schemaIndex, null, null, null, 2, true);
    }
}
