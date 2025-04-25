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
package com.tcdng.unify.core.database.dynamic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.database.dynamic.sql.DynamicSqlDataSourceConfig;
import com.tcdng.unify.core.database.dynamic.sql.DynamicSqlDataSourceManager;
import com.tcdng.unify.core.database.dynamic.sql.DynamicSqlDatabase;
import com.tcdng.unify.core.database.dynamic.sql.DynamicSqlDatabaseManager;
import com.tcdng.unify.core.system.entities.SingleVersionBlob;

/**
 * Dynamic SQL database manager test.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DynamicSqlDatabaseManagerTest extends AbstractUnifyComponentTest {

    private static final String TEST_CONFIG = "test-config";

    private DynamicSqlDataSourceManager dsm;

    private DynamicSqlDatabaseManager dbm;

    @Test
    public void testGetDynamicSqlDatabase() throws Exception {
        DynamicSqlDatabase db = dbm.getDynamicSqlDatabase(TEST_CONFIG);
        assertNotNull(db);
        assertEquals(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCE, db.getDataSourceName());
    }

    @Override
    protected void onSetup() throws Exception {
        // Configure data source
        dsm = (DynamicSqlDataSourceManager) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER);
        dsm.configure(new DynamicSqlDataSourceConfig(TEST_CONFIG, "hsqldb-dialect", "org.hsqldb.jdbcDriver",
                "jdbc:hsqldb:mem:dyntest", null, null, null, 2, true));
        // Set database manager
        dbm = (DynamicSqlDatabaseManager) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASEMANAGER);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onTearDown() throws Exception {
        // De-configure data source
        DynamicSqlDataSourceManager dynamicSqlDataSourceManager = (DynamicSqlDataSourceManager) getComponent(
                ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER);
        if (dynamicSqlDataSourceManager.isConfigured(TEST_CONFIG)) {
            dynamicSqlDataSourceManager.terminateAll();
        }
        deleteAll(SingleVersionBlob.class);
    }
}
