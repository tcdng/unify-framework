/*
 * Copyright 2018-2023 The Code Department.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.dynamic.sql.DynamicSqlDataSourceConfig;
import com.tcdng.unify.core.database.dynamic.sql.DynamicSqlDataSourceManager;
import com.tcdng.unify.core.system.entities.SingleVersionBlob;

/**
 * Dynamic SQL data source manager test.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DynamicSqlDataSourceManagerTest extends AbstractUnifyComponentTest {

    private static final String TEST_CONFIG = "test-config";

    private DynamicSqlDataSourceManager dsm;

    @Test
    public void testConfigureDataSource() throws Exception {
        dsm.configure(getConfig());
    }

    @Test(expected = UnifyException.class)
    public void testConfigureSameDataSourceMultiple() throws Exception {
        DynamicSqlDataSourceConfig dsConfig = getConfig();
        dsm.configure(dsConfig);
        dsm.configure(dsConfig);
    }

    @Test
    public void testIsDataSourceConfigured() throws Exception {
        dsm.configure(getConfig());
        assertTrue(dsm.isConfigured(TEST_CONFIG));
    }

    @Test
    public void testReconfigureDataSource() throws Exception {
        DynamicSqlDataSourceConfig dsConfig = getConfig();
        dsm.configure(dsConfig);
        assertTrue(dsm.reconfigure(dsConfig));
    }

    @Test
    public void testReconfigureNonManagedDataSource() throws Exception {
        assertFalse(dsm.reconfigure(getConfig()));
    }

    @Test
    public void testGetDataSourceCount() throws Exception {
        assertEquals(0, dsm.getDataSourceCount());

        dsm.configure(getConfig());
        assertEquals(1, dsm.getDataSourceCount());
    }

    @Test
    public void testTestDataSourceConfiguration() throws Exception {
        assertTrue(dsm.testConfiguration(getConfig()));
        assertEquals(0, dsm.getDataSourceCount());
    }

    @Test
    public void testGetAndRestoreConnection() throws Exception {
        dsm.configure(getConfig());

        Connection connection = dsm.getConnection(TEST_CONFIG);
        assertNotNull(connection);
        assertTrue(dsm.restoreConnection(TEST_CONFIG, connection));
    }

    @Test
    public void testTerminateConfiguration() throws Exception {
        dsm.configure(getConfig());
        assertEquals(1, dsm.getDataSourceCount());

        dsm.terminateConfiguration(TEST_CONFIG);
        assertEquals(0, dsm.getDataSourceCount());
    }

    @Test(expected = UnifyException.class)
    public void testTerminateUnknownConfiguration() throws Exception {
        dsm.terminateConfiguration(TEST_CONFIG);
    }

    @Override
    protected void onSetup() throws Exception {
        dsm = (DynamicSqlDataSourceManager) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onTearDown() throws Exception {
        dsm.terminateAll();
        deleteAll(SingleVersionBlob.class);
    }

    private DynamicSqlDataSourceConfig getConfig() {
        return new DynamicSqlDataSourceConfig(TEST_CONFIG, "hsqldb-dialect", "org.hsqldb.jdbcDriver",
                "jdbc:hsqldb:mem:dyntest", null, null, null, 2, true);
    }

}
