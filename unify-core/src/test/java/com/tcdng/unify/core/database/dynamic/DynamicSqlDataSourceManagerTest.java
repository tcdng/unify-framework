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
package com.tcdng.unify.core.database.dynamic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.database.dynamic.DynamicEntityInfo;
import com.tcdng.unify.core.database.dynamic.sql.DynamicSqlDataSourceConfig;
import com.tcdng.unify.core.database.dynamic.sql.DynamicSqlDataSourceManager;
import com.tcdng.unify.core.system.entities.SingleVersionBlob;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Dynamic SQL data source manager test.
 * 
 * @author Lateef Ojulari
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

    @Test(expected = UnifyException.class)
    public void testCreateOrUpdateDataSourceDynamicEntitySchemaObjectsUnknownConfig() throws Exception {
        dsm.createOrUpdateDataSourceDynamicEntitySchemaObjects("someConfig", new ArrayList<DynamicEntityInfo>());
    }

    @Test
    public void testCreateOrUpdateDataSourceDynamicEntitySchemaObjectsEmptyList() throws Exception {
        dsm.configure(getConfig());
        dsm.createOrUpdateDataSourceDynamicEntitySchemaObjects(TEST_CONFIG, new ArrayList<DynamicEntityInfo>());
    }

    @Test
    public void testCreateOrUpdateDataSourceDynamicEntitySchemaObjectsSingleSimpleEntity() throws Exception {
        dsm.configure(getConfig());
        DynamicEntityInfo dynamicEntityInfo =
                DynamicEntityInfo.newBuilder().tableName("EQUIPMENT").className("com.tcdng.test.Equipment").version(1L)
                        .addField(DataType.STRING, "EQUIPMENT_NM", "name", 32, 0, 0, false)
                        .addField(DataType.STRING, "SERIAL_NO", "serialNo", 0, 0, 0, false)
                        .addField(DataType.DECIMAL, "PRICE", "price", 0, 18, 2, false)
                        .addField(DataType.DATE, "EXPIRY_DT", "expiryDt", 0, 0, 0, false)
                        .addField(DataType.TIMESTAMP_UTC, "CREATE_DT", "createDt", 0, 0, 0, false)
                        .build();
        dsm.createOrUpdateDataSourceDynamicEntitySchemaObjects(TEST_CONFIG, Arrays.asList(dynamicEntityInfo));
        Class<?> entityClass1 = dsm.getDataSourceDynamicEntityClass(TEST_CONFIG, "com.tcdng.test.Equipment");
        assertNotNull(entityClass1);
        Class<?> entityClass2 = dsm.getDataSourceDynamicEntityClass(TEST_CONFIG, "com.tcdng.test.Equipment");
        assertNotNull(entityClass2);
        assertTrue(entityClass1 == entityClass2);
    }

    @Test
    public void testCreateOrUpdateDataSourceDynamicEntitySchemaObjectsSingleSimpleEntityNewSchema() throws Exception {
        dsm.configure(getConfig());
        DynamicEntityInfo dynamicEntityInfo =
                DynamicEntityInfo.newBuilder().tableName("EQUIPMENT").className("com.tcdng.test.Equipment").version(1L)
                        .addField(DataType.STRING, "EQUIPMENT_NM", "name", 32, 0, 0, false)
                        .addField(DataType.STRING, "SERIAL_NO", "serialNo", 0, 0, 0, false)
                        .addField(DataType.DECIMAL, "PRICE", "price", 0, 18, 2, false)
                        .addField(DataType.DATE, "EXPIRY_DT", "expiryDt", 0, 0, 0, false)
                        .addField(DataType.TIMESTAMP_UTC, "CREATE_DT", "createDt", 0, 0, 0, false)
                        .build();
        dsm.createOrUpdateDataSourceDynamicEntitySchemaObjects(TEST_CONFIG, Arrays.asList(dynamicEntityInfo));
        Class<?> entityClass1 = dsm.getDataSourceDynamicEntityClass(TEST_CONFIG, "com.tcdng.test.Equipment");
        assertNotNull(entityClass1);
        List<String> beanFieldList = ReflectUtils.getBeanCompliantFieldNames(entityClass1);
        assertTrue(beanFieldList.contains("name"));
        assertTrue(beanFieldList.contains("serialNo"));
        assertTrue(beanFieldList.contains("price"));
        assertTrue(beanFieldList.contains("expiryDt"));
        assertTrue(beanFieldList.contains("createDt"));
        assertFalse(beanFieldList.contains("active"));
        
        dynamicEntityInfo =
                DynamicEntityInfo.newBuilder().tableName("EQUIPMENT").className("com.tcdng.test.Equipment").version(2L)
                        .addField(DataType.STRING, "EQUIPMENT_NM", "name", 64, 0, 0, false)
                        .addField(DataType.STRING, "SERIAL_NO", "serialNo", 0, 0, 0, false)
                        .addField(DataType.DECIMAL, "PRICE", "price", 0, 18, 2, false)
                        .addField(DataType.DATE, "EXPIRY_DT", "expiryDt", 0, 0, 0, true)
                        .addField(DataType.TIMESTAMP_UTC, "CREATE_DT", "createDt", 0, 0, 0, false)
                        .addField(DataType.BOOLEAN, "ACTIVE_FG", "active", 0, 0, 0, false)
                        .build();
        dsm.createOrUpdateDataSourceDynamicEntitySchemaObjects(TEST_CONFIG, Arrays.asList(dynamicEntityInfo));        
        Class<?> entityClass2 = dsm.getDataSourceDynamicEntityClass(TEST_CONFIG, "com.tcdng.test.Equipment");
        assertNotNull(entityClass2);
        assertFalse(entityClass1 == entityClass2);
        beanFieldList = ReflectUtils.getBeanCompliantFieldNames(entityClass2);
        assertTrue(beanFieldList.contains("name"));
        assertTrue(beanFieldList.contains("serialNo"));
        assertTrue(beanFieldList.contains("price"));
        assertTrue(beanFieldList.contains("expiryDt"));
        assertTrue(beanFieldList.contains("createDt"));
        assertTrue(beanFieldList.contains("active"));
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
