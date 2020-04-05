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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.database.DatabaseTransactionManager;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.dynamic.DynamicEntityInfo;
import com.tcdng.unify.core.system.entities.SingleVersionBlob;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Dynamic SQL database manager test.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DynamicSqlDatabaseManagerTest extends AbstractUnifyComponentTest {

    private static final String TEST_CONFIG = "test-config.PUBLIC";

    private DynamicSqlDataSourceManager dsm;

    private DynamicSqlDatabaseManager dbm;

    private DatabaseTransactionManager tm;

    @Test
    public void testGetDynamicSqlDatabase() throws Exception {
        DynamicSqlDatabase db = dbm.getDynamicSqlDatabase(TEST_CONFIG);
        assertNotNull(db);
        assertEquals(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCE, db.getDataSourceName());
    }

    @Test
    public void testDynamicEntityCreateRecord() throws Exception {
        tm.beginTransaction();
        try {
            DynamicSqlDatabase db = dbm.getDynamicSqlDatabase(TEST_CONFIG);
            Class<? extends Entity> entityClass = db.getDynamicEntityClass("com.tcdng.test.Equipment");
            Entity inst =  ReflectUtils.newInstance(entityClass);
            ReflectUtils.setBeanProperty(inst, "name", "3dPrinter");
            ReflectUtils.setBeanProperty(inst, "serialNo", "202004-0001");
            ReflectUtils.setBeanProperty(inst, "price", BigDecimal.valueOf(2.99));
            ReflectUtils.setBeanProperty(inst, "createDt", new Date());
            Long id = (Long) db.create(inst);
            assertNotNull(id);
            assertEquals(id, ReflectUtils.getBeanProperty(inst, "id"));
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Override
    protected void onSetup() throws Exception {
        // Configure data source
        dsm = (DynamicSqlDataSourceManager) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCEMANAGER);
        dsm.configure(new DynamicSqlDataSourceConfig(TEST_CONFIG, "hsqldb-dialect", "org.hsqldb.jdbcDriver",
                "jdbc:hsqldb:mem:dyntest", null, null, 2, true));
        DynamicEntityInfo dynamicEntityInfo =
                DynamicEntityInfo.newBuilder().tableName("EQUIPMENT").className("com.tcdng.test.Equipment").version(1L)
                        .addField(DataType.STRING, "EQUIPMENT_NM", "name", 32, 0, 0, false)
                        .addField(DataType.STRING, "SERIAL_NO", "serialNo", 0, 0, 0, false)
                        .addField(DataType.DECIMAL, "PRICE", "price", 0, 18, 2, false)
                        .addField(DataType.DATE, "EXPIRY_DT", "expiryDt", 0, 0, 0, true)
                        .addField(DataType.TIMESTAMP_UTC, "CREATE_DT", "createDt", 0, 0, 0, false).build();
        dsm.createOrUpdateDataSourceDynamicEntitySchemaObjects(TEST_CONFIG, Arrays.asList(dynamicEntityInfo));

        // Set database manager
        dbm = (DynamicSqlDatabaseManager) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLDATABASEMANAGER);

        // Set transaction manager
        tm = (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);
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
