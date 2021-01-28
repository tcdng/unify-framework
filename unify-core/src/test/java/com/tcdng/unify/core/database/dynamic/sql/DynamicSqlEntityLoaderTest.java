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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.annotation.EntityType;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.database.DatabaseTransactionManager;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.database.dynamic.DynamicEntityInfo;
import com.tcdng.unify.core.database.sql.SqlDatabase;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Dynamic SQL entity loader test.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DynamicSqlEntityLoaderTest extends AbstractUnifyComponentTest {

    private DatabaseTransactionManager tm;

    private SqlDatabase db;

    private DynamicSqlEntityLoader dseLoader;

    private DynamicEntityInfo dynamicEntityInfo;

    private DynamicEntityInfo dynamicEntityInfoExt;

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadDynamicSqlEntity() throws Exception {
        Class<? extends Entity> clazz1 = dseLoader.loadDynamicSqlEntity(db, dynamicEntityInfo);
        assertNotNull(clazz1);
        assertEquals("com.tcdng.test.Equipment", clazz1.getName());
        Class<? extends Entity> clazz2 = (Class<? extends Entity>) ReflectUtils
                .classForName("com.tcdng.test.Equipment");
        assertSame(clazz1, clazz2);
    }

    @Test
    public void testLoadDynamicSqlEntityCreateInst() throws Exception {
        dseLoader.loadDynamicSqlEntity(db, dynamicEntityInfo);
        tm.beginTransaction();
        try {
            Date createDt = new Date();
            Entity inst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.Equipment");
            assertNotNull(inst);
            ReflectUtils.setBeanProperty(inst, "name", "5dPrinter");
            ReflectUtils.setBeanProperty(inst, "serialNo", "202004-0003");
            ReflectUtils.setBeanProperty(inst, "price", BigDecimal.valueOf(10.99));
            ReflectUtils.setBeanProperty(inst, "createDt", createDt);
            Long id = (Long) db.create(inst);
            assertNotNull(id);
            assertTrue(id > 0L);
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testLoadDynamicSqlEntityFindInstById() throws Exception {
        dseLoader.loadDynamicSqlEntity(db, dynamicEntityInfo);
        tm.beginTransaction();
        try {
            Date createDt = new Date();
            Entity inst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.Equipment");
            assertNotNull(inst);
            ReflectUtils.setBeanProperty(inst, "name", "6dPrinter");
            ReflectUtils.setBeanProperty(inst, "serialNo", "202004-8883");
            ReflectUtils.setBeanProperty(inst, "price", BigDecimal.valueOf(40.25));
            ReflectUtils.setBeanProperty(inst, "createDt", createDt);
            Long id = (Long) db.create(inst);
            
            Entity fInst = db.find(inst.getClass(), id);
            assertNotNull(fInst);
            assertEquals("6dPrinter", ReflectUtils.getBeanProperty(fInst, "name"));
            assertEquals("202004-8883", ReflectUtils.getBeanProperty(fInst, "serialNo"));
            assertEquals(BigDecimal.valueOf(40.25), ReflectUtils.getBeanProperty(fInst, "price"));
            assertEquals(createDt, ReflectUtils.getBeanProperty(fInst, "createDt"));
            assertNull(ReflectUtils.getBeanProperty(fInst, "expiryDt"));
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testLoadDynamicSqlEntityUpdateInstById() throws Exception {
        dseLoader.loadDynamicSqlEntity(db, dynamicEntityInfo);
        tm.beginTransaction();
        try {
            Date createDt = new Date();
            Entity inst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.Equipment");
            assertNotNull(inst);
            ReflectUtils.setBeanProperty(inst, "name", "6dPrinter");
            ReflectUtils.setBeanProperty(inst, "serialNo", "202004-8883");
            ReflectUtils.setBeanProperty(inst, "price", BigDecimal.valueOf(40.25));
            ReflectUtils.setBeanProperty(inst, "createDt", createDt);
            Long id = (Long) db.create(inst);
            
            Entity fInst = db.find(inst.getClass(), id);
            assertNotNull(fInst);
            ReflectUtils.setBeanProperty(fInst, "serialNo", "202004-7773");
            ReflectUtils.setBeanProperty(fInst, "price", BigDecimal.valueOf(90.62));
            db.updateByIdVersion(fInst);

            Entity ffInst = db.find(inst.getClass(), id);
            assertNotNull(ffInst);
            assertEquals("6dPrinter", ReflectUtils.getBeanProperty(ffInst, "name"));
            assertEquals("202004-7773", ReflectUtils.getBeanProperty(ffInst, "serialNo"));
            assertEquals(BigDecimal.valueOf(90.62), ReflectUtils.getBeanProperty(ffInst, "price"));
            assertEquals(createDt, ReflectUtils.getBeanProperty(ffInst, "createDt"));
            assertNull(ReflectUtils.getBeanProperty(ffInst, "expiryDt"));
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testLoadDynamicSqlEntityDeleteInstById() throws Exception {
        dseLoader.loadDynamicSqlEntity(db, dynamicEntityInfo);
        tm.beginTransaction();
        try {
            Date createDt = new Date();
            Entity inst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.Equipment");
            assertNotNull(inst);
            ReflectUtils.setBeanProperty(inst, "name", "20dPrinter");
            ReflectUtils.setBeanProperty(inst, "serialNo", "202004-8883");
            ReflectUtils.setBeanProperty(inst, "price", BigDecimal.valueOf(40.25));
            ReflectUtils.setBeanProperty(inst, "createDt", createDt);
            Long id = (Long) db.create(inst);
            
            Entity fInst = db.find(inst.getClass(), id);
            assertNotNull(fInst);
            db.delete(inst.getClass(), id);
            assertEquals(0, db.countAll(Query.of(inst.getClass()).addEquals("name", "20dPrinter")));
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadDynamicSqlEntityExt() throws Exception {
        Class<? extends Entity> clazz1 = dseLoader.loadDynamicSqlEntity(db, dynamicEntityInfoExt);
        assertNotNull(clazz1);
        assertEquals("com.tcdng.test.EquipmentExt", clazz1.getName());
        Class<? extends Entity> clazz2 = (Class<? extends Entity>) ReflectUtils
                .classForName("com.tcdng.test.EquipmentExt");
        assertSame(clazz1, clazz2);
    }

    @Test
    public void testLoadDynamicSqlEntityExtCreateInst() throws Exception {
        dseLoader.loadDynamicSqlEntity(db, dynamicEntityInfoExt);
        tm.beginTransaction();
        try {
            Date createDt = new Date();
            Entity inst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.EquipmentExt");
            assertNotNull(inst);
            ReflectUtils.setBeanProperty(inst, "name", "5dPrinter");
            ReflectUtils.setBeanProperty(inst, "serialNo", "202004-0003");
            ReflectUtils.setBeanProperty(inst, "price", BigDecimal.valueOf(10.99));
            ReflectUtils.setBeanProperty(inst, "createDt", createDt);
            Long id = (Long) db.create(inst);
            assertNotNull(id);
            assertTrue(id > 0L);
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testLoadDynamicSqlEntityExtFindInstById() throws Exception {
        dseLoader.loadDynamicSqlEntity(db, dynamicEntityInfoExt);
        tm.beginTransaction();
        try {
            Date createDt = new Date();
            Entity inst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.EquipmentExt");
            assertNotNull(inst);
            ReflectUtils.setBeanProperty(inst, "name", "6dPrinter");
            ReflectUtils.setBeanProperty(inst, "serialNo", "202004-8883");
            ReflectUtils.setBeanProperty(inst, "price", BigDecimal.valueOf(40.25));
            ReflectUtils.setBeanProperty(inst, "createDt", createDt);
            Long id = (Long) db.create(inst);
            
            Entity fInst = db.find(inst.getClass(), id);
            assertNotNull(fInst);
            assertEquals("6dPrinter", ReflectUtils.getBeanProperty(fInst, "name"));
            assertEquals("202004-8883", ReflectUtils.getBeanProperty(fInst, "serialNo"));
            assertEquals(BigDecimal.valueOf(40.25), ReflectUtils.getBeanProperty(fInst, "price"));
            assertEquals(createDt, ReflectUtils.getBeanProperty(fInst, "createDt"));
            assertNull(ReflectUtils.getBeanProperty(fInst, "expiryDt"));
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testLoadDynamicSqlEntityExtUpdateInstById() throws Exception {
        dseLoader.loadDynamicSqlEntity(db, dynamicEntityInfoExt);
        tm.beginTransaction();
        try {
            Date createDt = new Date();
            Entity inst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.EquipmentExt");
            assertNotNull(inst);
            ReflectUtils.setBeanProperty(inst, "name", "6dPrinter");
            ReflectUtils.setBeanProperty(inst, "serialNo", "202004-8883");
            ReflectUtils.setBeanProperty(inst, "price", BigDecimal.valueOf(40.25));
            ReflectUtils.setBeanProperty(inst, "createDt", createDt);
            Long id = (Long) db.create(inst);
            
            Entity fInst = db.find(inst.getClass(), id);
            assertNotNull(fInst);
            ReflectUtils.setBeanProperty(fInst, "serialNo", "202004-7773");
            ReflectUtils.setBeanProperty(fInst, "price", BigDecimal.valueOf(90.62));
            db.updateByIdVersion(fInst);

            Entity ffInst = db.find(inst.getClass(), id);
            assertNotNull(ffInst);
            assertEquals("6dPrinter", ReflectUtils.getBeanProperty(ffInst, "name"));
            assertEquals("202004-7773", ReflectUtils.getBeanProperty(ffInst, "serialNo"));
            assertEquals(BigDecimal.valueOf(90.62), ReflectUtils.getBeanProperty(ffInst, "price"));
            assertEquals(createDt, ReflectUtils.getBeanProperty(ffInst, "createDt"));
            assertNull(ReflectUtils.getBeanProperty(ffInst, "expiryDt"));
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testLoadDynamicSqlEntityExtDeleteInstById() throws Exception {
        dseLoader.loadDynamicSqlEntity(db, dynamicEntityInfoExt);
        tm.beginTransaction();
        try {
            Date createDt = new Date();
            Entity inst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.EquipmentExt");
            assertNotNull(inst);
            ReflectUtils.setBeanProperty(inst, "name", "20dPrinter");
            ReflectUtils.setBeanProperty(inst, "serialNo", "202004-8883");
            ReflectUtils.setBeanProperty(inst, "price", BigDecimal.valueOf(40.25));
            ReflectUtils.setBeanProperty(inst, "createDt", createDt);
            Long id = (Long) db.create(inst);
            
            Entity fInst = db.find(inst.getClass(), id);
            assertNotNull(fInst);
            db.delete(inst.getClass(), id);
            assertEquals(0, db.countAll(Query.of(inst.getClass()).addEquals("name", "20dPrinter")));
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Override
    protected void onSetup() throws Exception {
        tm = (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);
        db = (SqlDatabase) getComponent(ApplicationComponents.APPLICATION_DATABASE);
        dseLoader = (DynamicSqlEntityLoader) getComponent(ApplicationComponents.APPLICATION_DYNAMICSQLENTITYLOADER);
        dynamicEntityInfo = DynamicEntityInfo.newBuilder(EntityType.TABLE, "com.tcdng.test.Equipment")
                .tableName("EQUIPMENT").version(1L)
                .addField(DataType.STRING, "EQUIPMENT_NM", "name", 32, 0, 0, false)
                .addField(DataType.STRING, "SERIAL_NO", "serialNo", 0, 0, 0, false)
                .addField(DataType.DECIMAL, "PRICE", "price", 0, 18, 2, false)
                .addField(DataType.DATE, "EXPIRY_DT", "expiryDt", 0, 0, 0, true)
                .addField(DataType.TIMESTAMP_UTC, "CREATE_DT", "createDt", 0, 0, 0, false).build();
        dynamicEntityInfoExt = DynamicEntityInfo.newBuilder(EntityType.TABLE_EXT, "com.tcdng.test.EquipmentExt")
                .baseClassName(Equipment.class.getName()).version(1L)
                .addField(DataType.DECIMAL, "PRICE", "price", 0, 18, 2, false)
                .addField(DataType.DATE, "EXPIRY_DT", "expiryDt", 0, 0, 0, true)
                .addField(DataType.TIMESTAMP_UTC, "CREATE_DT", "createDt", 0, 0, 0, false).build();
    }

    @Override
    protected void onTearDown() throws Exception {

    }
}
