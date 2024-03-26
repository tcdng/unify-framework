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

package com.tcdng.unify.core.database.dynamic.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.annotation.DynamicEntityType;
import com.tcdng.unify.core.annotation.DynamicFieldType;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.constant.OrderType;
import com.tcdng.unify.core.database.DatabaseTransactionManager;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.database.dynamic.DynamicEntityInfo;
import com.tcdng.unify.core.database.sql.SqlDatabase;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Dynamic SQL entity loader test.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DynamicSqlEntityLoaderTest extends AbstractUnifyComponentTest {

    private DatabaseTransactionManager tm;

    private SqlDatabase db;

    private DynamicSqlEntityLoader dseLoader;

    private DynamicEntityInfo dynamicEntityInfo;

    private DynamicEntityInfo dynamicEntityInfoExt;
    
    private DynamicEntityInfo authorDynamicEntityInfo;
    
    private DynamicEntityInfo bookDynamicEntityInfo;

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadDynamicSqlEntity() throws Exception {
        Class<? extends Entity> clazz1 = dseLoader.loadDynamicSqlEntity(dynamicEntityInfo);
        assertNotNull(clazz1);
        assertEquals("com.tcdng.test.Equipment", clazz1.getName());
        Class<? extends Entity> clazz2 = (Class<? extends Entity>) ReflectUtils
                .classForName("com.tcdng.test.Equipment");
        assertSame(clazz1, clazz2);
    }

    @Test
    public void testLoadDynamicSqlEntityCreateInst() throws Exception {
        dseLoader.loadDynamicSqlEntity(dynamicEntityInfo);
        tm.beginTransaction();
        try {
            Date createDt = new Date();
            Entity inst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.Equipment");
            assertNotNull(inst);
            ReflectUtils.setBeanProperty(inst, "name", "5dPrinter");
            ReflectUtils.setBeanProperty(inst, "serialNo", "202004-0003");
            ReflectUtils.setBeanProperty(inst, "price", BigDecimal.valueOf(10.99));
            ReflectUtils.setBeanProperty(inst, "createDt", createDt);
            ReflectUtils.setBeanProperty(inst, "order", OrderType.ASCENDING);
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
        dseLoader.loadDynamicSqlEntity(dynamicEntityInfo);
        tm.beginTransaction();
        try {
            Date createDt = new Date();
            Entity inst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.Equipment");
            assertNotNull(inst);
            ReflectUtils.setBeanProperty(inst, "name", "6dPrinter");
            ReflectUtils.setBeanProperty(inst, "serialNo", "202004-8883");
            ReflectUtils.setBeanProperty(inst, "price", BigDecimal.valueOf(40.25));
            ReflectUtils.setBeanProperty(inst, "createDt", createDt);
            ReflectUtils.setBeanProperty(inst, "order", OrderType.DESCENDING);
            Long id = (Long) db.create(inst);

            Entity fInst = db.find(inst.getClass(), id);
            assertNotNull(fInst);
            assertEquals("6dPrinter", ReflectUtils.getBeanProperty(fInst, "name"));
            assertEquals("202004-8883", ReflectUtils.getBeanProperty(fInst, "serialNo"));
            assertEquals(BigDecimal.valueOf(40.25), ReflectUtils.getBeanProperty(fInst, "price"));
            assertEquals(createDt, ReflectUtils.getBeanProperty(fInst, "createDt"));
            assertNull(ReflectUtils.getBeanProperty(fInst, "expiryDt"));
            assertEquals("6dPrinter", fInst.getDescription());
            assertEquals(OrderType.DESCENDING, ReflectUtils.getBeanProperty(fInst, "order"));
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testLoadDynamicSqlEntityUpdateInstById() throws Exception {
        dseLoader.loadDynamicSqlEntity(dynamicEntityInfo);
        tm.beginTransaction();
        try {
            Date createDt = new Date();
            Entity inst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.Equipment");
            assertNotNull(inst);
            ReflectUtils.setBeanProperty(inst, "name", "6dPrinter");
            ReflectUtils.setBeanProperty(inst, "serialNo", "202004-8883");
            ReflectUtils.setBeanProperty(inst, "price", BigDecimal.valueOf(40.25));
            ReflectUtils.setBeanProperty(inst, "createDt", createDt);
            ReflectUtils.setBeanProperty(inst, "order", OrderType.DESCENDING);
            Long id = (Long) db.create(inst);

            Entity fInst = db.find(inst.getClass(), id);
            assertNotNull(fInst);
            ReflectUtils.setBeanProperty(fInst, "serialNo", "202004-7773");
            ReflectUtils.setBeanProperty(fInst, "price", BigDecimal.valueOf(90.62));
            ReflectUtils.setBeanProperty(fInst, "order", OrderType.ASCENDING);
            db.updateByIdVersion(fInst);

            Entity ffInst = db.find(inst.getClass(), id);
            assertNotNull(ffInst);
            assertEquals("6dPrinter", ReflectUtils.getBeanProperty(ffInst, "name"));
            assertEquals("202004-7773", ReflectUtils.getBeanProperty(ffInst, "serialNo"));
            assertEquals(BigDecimal.valueOf(90.62), ReflectUtils.getBeanProperty(ffInst, "price"));
            assertEquals(createDt, ReflectUtils.getBeanProperty(ffInst, "createDt"));
            assertNull(ReflectUtils.getBeanProperty(ffInst, "expiryDt"));
            assertEquals(OrderType.ASCENDING, ReflectUtils.getBeanProperty(ffInst, "order"));
            assertEquals("6dPrinter", fInst.getDescription());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testLoadDynamicSqlEntityDeleteInstById() throws Exception {
        dseLoader.loadDynamicSqlEntity(dynamicEntityInfo);
        tm.beginTransaction();
        try {
            Date createDt = new Date();
            Entity inst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.Equipment");
            assertNotNull(inst);
            ReflectUtils.setBeanProperty(inst, "name", "20dPrinter");
            ReflectUtils.setBeanProperty(inst, "serialNo", "202004-8883");
            ReflectUtils.setBeanProperty(inst, "price", BigDecimal.valueOf(40.25));
            ReflectUtils.setBeanProperty(inst, "createDt", createDt);
            ReflectUtils.setBeanProperty(inst, "order", OrderType.ASCENDING);
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
        Class<? extends Entity> clazz1 = dseLoader.loadDynamicSqlEntity(dynamicEntityInfoExt);
        assertNotNull(clazz1);
        assertEquals("com.tcdng.test.EquipmentExt", clazz1.getName());
        Class<? extends Entity> clazz2 = (Class<? extends Entity>) ReflectUtils
                .classForName("com.tcdng.test.EquipmentExt");
        assertSame(clazz1, clazz2);
    }

    @Test
    public void testLoadDynamicSqlEntityExtCreateInst() throws Exception {
        dseLoader.loadDynamicSqlEntity(dynamicEntityInfoExt);
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
        dseLoader.loadDynamicSqlEntity(dynamicEntityInfoExt);
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
            assertEquals("6dPrinter 202004-8883 40.25", fInst.getDescription());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testLoadDynamicSqlEntityExtUpdateInstById() throws Exception {
        dseLoader.loadDynamicSqlEntity(dynamicEntityInfoExt);
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
            assertEquals("6dPrinter 202004-7773 90.62", fInst.getDescription());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testLoadDynamicSqlEntityExtDeleteInstById() throws Exception {
        dseLoader.loadDynamicSqlEntity(dynamicEntityInfoExt);
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

    @Test
    public void testLoadDynamicSqlEntityCircular() throws Exception {
        List<Class<? extends Entity>> classList = dseLoader.loadDynamicSqlEntities(
                Arrays.asList(authorDynamicEntityInfo, bookDynamicEntityInfo));
        assertNotNull(classList);
        assertEquals(2, classList.size());
        Class<? extends Entity> authorClazz = (Class<? extends Entity>) classList.get(0);
        assertEquals("com.tcdng.test.Author", authorClazz.getName());
        Class<? extends Entity> bookClazz = (Class<? extends Entity>) classList.get(1);
        assertEquals("com.tcdng.test.Book", bookClazz.getName());
    }

    @Test
    public void testLoadDynamicSqlEntityCircularCreateInst() throws Exception {
        dseLoader.loadDynamicSqlEntities(
                Arrays.asList(authorDynamicEntityInfo, bookDynamicEntityInfo));

        tm.beginTransaction();
        try {
            Entity authorInst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.Author");
            ReflectUtils.setBeanProperty(authorInst, "name", "John Doe");
            Entity book1Inst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.Book");
            Entity book2Inst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.Book");
            ReflectUtils.setBeanProperty(book1Inst, "title", "Finding Jane");
            ReflectUtils.setBeanProperty(book2Inst, "title", "Attack the Bean");
            ReflectUtils.setBeanProperty(authorInst, "bookList", Arrays.asList(book1Inst, book2Inst));
            Long id = (Long) db.create(authorInst);
            assertNotNull(id);
            assertTrue(id > 0L);
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
        
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLoadDynamicSqlEntityCircularFindInst() throws Exception {
        dseLoader.loadDynamicSqlEntities(
                Arrays.asList(authorDynamicEntityInfo, bookDynamicEntityInfo));

        tm.beginTransaction();
        try {
            Entity authorInst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.Author");
            ReflectUtils.setBeanProperty(authorInst, "name", "John Doe");
            Entity book1Inst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.Book");
            Entity book2Inst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.Book");
            ReflectUtils.setBeanProperty(book1Inst, "title", "Finding Jane");
            ReflectUtils.setBeanProperty(book2Inst, "title", "Attack the Bean");
            ReflectUtils.setBeanProperty(authorInst, "bookList", Arrays.asList(book1Inst, book2Inst));
            Long id = (Long) db.create(authorInst);
            
            Entity findAuthorInst = db.list(authorInst.getClass(), id);
            assertNotNull(findAuthorInst);
            assertEquals("John Doe", ReflectUtils.getBeanProperty(findAuthorInst, "name"));
            List<? extends Entity> list = (List<? extends Entity>) ReflectUtils.getBeanProperty(findAuthorInst, "bookList");
            assertNotNull(list);
            assertEquals(2, list.size());
            
            Entity findBook1Inst = list.get(0);
            assertNotNull(findBook1Inst);
            assertEquals(id, ReflectUtils.getBeanProperty(findBook1Inst, "authorId"));
            assertEquals("Finding Jane", ReflectUtils.getBeanProperty(findBook1Inst, "title"));
            assertEquals("John Doe", ReflectUtils.getBeanProperty(findBook1Inst, "authorName"));
            
            Entity findBook2Inst = list.get(1);
            assertNotNull(findBook2Inst);
            assertEquals(id, ReflectUtils.getBeanProperty(findBook2Inst, "authorId"));
            assertEquals("Attack the Bean", ReflectUtils.getBeanProperty(findBook2Inst, "title"));
            assertEquals("John Doe", ReflectUtils.getBeanProperty(findBook2Inst, "authorName"));
            
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
        
    }

    @Test
    public void testLoadDynamicSqlEntityCircularDeleteInst() throws Exception {
        dseLoader.loadDynamicSqlEntities(
                Arrays.asList(authorDynamicEntityInfo, bookDynamicEntityInfo));

        tm.beginTransaction();
        try {
            Entity authorInst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.Author");
            ReflectUtils.setBeanProperty(authorInst, "name", "John Doe");
            Entity book1Inst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.Book");
            Entity book2Inst = ReflectUtils.newInstance(Entity.class, "com.tcdng.test.Book");
            ReflectUtils.setBeanProperty(book1Inst, "title", "Finding Jane");
            ReflectUtils.setBeanProperty(book2Inst, "title", "Attack the Bean");
            ReflectUtils.setBeanProperty(authorInst, "bookList", Arrays.asList(book1Inst, book2Inst));
            Long id = (Long) db.create(authorInst);
            
            int bookCount = db.countAll(Query.of(book1Inst.getClass()).addEquals("authorId", id));
            assertEquals(2, bookCount);
            
            db.delete(authorInst.getClass(), id);
            
            bookCount = db.countAll(Query.of(book1Inst.getClass()).addEquals("authorId", id));
            assertEquals(0, bookCount);
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
		final String mapped = null;
		final String defaultVal = null;
		dynamicEntityInfo = DynamicEntityInfo
				.newBuilder(DynamicEntityType.TABLE, "com.tcdng.test.Equipment", DynamicEntityInfo.ManagedType.MANAGED)
				.tableName("EQUIPMENT").version(1L)
				.addField(DynamicFieldType.GENERATION, DataType.STRING, "EQUIPMENT_NM", "name", mapped, defaultVal, 32, 0, 0,
						false, true)
				.addField(DynamicFieldType.GENERATION, DataType.STRING, "SERIAL_NO", "serialNo", mapped, "00000000", 0, 0, 0,
						false, false)
				.addField(DynamicFieldType.GENERATION, DataType.DECIMAL, "PRICE", "price", mapped, defaultVal, 0, 18, 2, false,
						false)
				.addField(DynamicFieldType.GENERATION, DataType.DATE, "EXPIRY_DT", "expiryDt", mapped, defaultVal, 0, 0, 0,
						true, false)
				.addField(DynamicFieldType.GENERATION, DataType.TIMESTAMP_UTC, "CREATE_DT", "createDt", mapped, defaultVal, 0,
						0, 0, false, false)
				.addField(DynamicFieldType.GENERATION, OrderType.class.getName(), "EQUIPMENT_ORDER", "order", mapped,
						defaultVal, false, false)
				.build();
		
		dynamicEntityInfoExt = DynamicEntityInfo
				.newBuilder(DynamicEntityType.TABLE_EXT, "com.tcdng.test.EquipmentExt",
						DynamicEntityInfo.ManagedType.MANAGED)
				.baseClassName(Equipment.class.getName()).version(1L)
				.addField(DynamicFieldType.INFO_ONLY, DataType.STRING, "EQUIPMENT_NM", "name", mapped, defaultVal, 32, 0, 0,
						false, true)
				.addField(DynamicFieldType.INFO_ONLY, DataType.STRING, "SERIAL_NO", "serialNo", mapped, defaultVal, 0, 0, 0,
						false, true)
				.addField(DynamicFieldType.GENERATION, DataType.DECIMAL, "PRICE", "price", mapped, defaultVal, 0, 18, 2, false,
						true)
				.addField(DynamicFieldType.GENERATION, DataType.DATE, "EXPIRY_DT", "expiryDt", mapped, defaultVal, 0, 0, 0,
						true, false)
				.addField(DynamicFieldType.GENERATION, DataType.TIMESTAMP_UTC, "CREATE_DT", "createDt", mapped, defaultVal, 0,
						0, 0, false, false)
				.build();

		DynamicEntityInfo.Builder adeib = DynamicEntityInfo
				.newBuilder(DynamicEntityType.TABLE, "com.tcdng.test.Author", DynamicEntityInfo.ManagedType.MANAGED)
				.tableName("TAUTHOR").version(1L);
		authorDynamicEntityInfo = adeib.prefetch();
		DynamicEntityInfo.Builder bdeib = DynamicEntityInfo
				.newBuilder(DynamicEntityType.TABLE, "com.tcdng.test.Book", DynamicEntityInfo.ManagedType.MANAGED)
				.tableName("TBOOK").version(1L);
		bookDynamicEntityInfo = bdeib.prefetch();

		adeib.addField(DynamicFieldType.INFO_ONLY, DataType.LONG, "PASCAL_ID", "id", null, null, 0, 0, 0, false, false)
				.addField(DynamicFieldType.GENERATION, DataType.STRING, "TAUTHOR_NM", "name", mapped, defaultVal, 32, 0, 0,
						false, true)
				.addForeignKeyField(DynamicFieldType.GENERATION, DynamicEntityInfo.SELF_REFERENCE, "PARENT_AUTHOR_ID",
						"parentAuthorId", defaultVal, true)
				.addListOnlyField(DynamicFieldType.GENERATION, null, "parentAuthorName", "parentAuthorId", "name",
						false)
				.addChildListField(DynamicFieldType.GENERATION, bookDynamicEntityInfo, "bookList", true).build();

		bdeib.addForeignKeyField(DynamicFieldType.GENERATION, authorDynamicEntityInfo, null, "authorId", "1", false)
				.addField(DynamicFieldType.GENERATION, DataType.STRING, "TITLE", "title", mapped, defaultVal, 32, 0, 0, false,
						true)
				.addListOnlyField(DynamicFieldType.GENERATION, null, "authorName", "authorId", "name", false).build();
		
		dynamicEntityInfo.finalizeResolution();
		dynamicEntityInfoExt.finalizeResolution();
		authorDynamicEntityInfo.finalizeResolution();
		bookDynamicEntityInfo.finalizeResolution();
	}

    @Override
    protected void onTearDown() throws Exception {

    }
}
