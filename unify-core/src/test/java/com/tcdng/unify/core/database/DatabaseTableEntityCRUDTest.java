/*
 * Copyright 2018-2019 The Code Department.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.BooleanType;
import com.tcdng.unify.core.constant.Gender;
import com.tcdng.unify.core.constant.OrderType;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.criterion.Update;
import com.tcdng.unify.core.data.Aggregate;
import com.tcdng.unify.core.data.AggregateType;
import com.tcdng.unify.core.util.CalendarUtils;

/**
 * Database table entity CRUD tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DatabaseTableEntityCRUDTest extends AbstractUnifyComponentTest {

    private Office parklaneOffice = new Office("24, Parklane Apapa", "+2348888888", 20);

    private Office warehouseOffice = new Office("38, Warehouse Road Apapa", "+2345555555", 35);

    private DatabaseTransactionManager tm;

    private Database db;
    
    @Test
    public void testAggregateSingle() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("orange", "orange", 15.00));

            Aggregate<?> aggregate =
                    db.aggregate(AggregateType.SUM, new FruitQuery().addSelect("price").ignoreEmptyCriteria(true));
            assertNotNull(aggregate);
            assertEquals(4, aggregate.getCount());
            assertEquals(140.00, aggregate.getValue());

            aggregate = db.aggregate(AggregateType.AVERAGE, new FruitQuery().addLike("name", "apple").addSelect("price"));
            assertNotNull(aggregate);
            assertEquals(2, aggregate.getCount());
            assertEquals(40.00, aggregate.getValue());

            aggregate = db.aggregate(AggregateType.MAXIMUM, new FruitQuery().addSelect("price").ignoreEmptyCriteria(true));
            assertNotNull(aggregate);
            assertEquals(4, aggregate.getCount());
            assertEquals(60.00, aggregate.getValue());

            aggregate = db.aggregate(AggregateType.MINIMUM, new FruitQuery().addLike("name", "apple").addSelect("price"));
            assertNotNull(aggregate);
            assertEquals(2, aggregate.getCount());
            assertEquals(20.00, aggregate.getValue());
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testAggregateMultiple() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00, 25));
            db.create(new Fruit("pineapple", "cyan", 60.00, 3));
            db.create(new Fruit("banana", "yellow", 45.00, 45));
            db.create(new Fruit("orange", "orange", 15.00, 11));

            // Sum
            List<Aggregate<?>> list = db.aggregateMany(AggregateType.SUM,
                    new FruitQuery().addSelect("price", "quantity").ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals(2, list.size());

            Aggregate<?> priceAggregate = list.get(0);
            Aggregate<?> qtyAggregate = list.get(1);
            assertNotNull(priceAggregate);
            assertNotNull(qtyAggregate);
            assertEquals("price", priceAggregate.getFieldName());
            assertEquals(4, priceAggregate.getCount());
            assertEquals(140.00, priceAggregate.getValue());
            assertEquals("quantity", qtyAggregate.getFieldName());
            assertEquals(4, qtyAggregate.getCount());
            assertEquals(84, qtyAggregate.getValue());

            // Average
            list = db.aggregateMany(AggregateType.AVERAGE,
                    new FruitQuery().addLike("name", "apple").addSelect("quantity", "price"));
            assertNotNull(list);
            assertEquals(2, list.size());

            qtyAggregate = list.get(0);
            priceAggregate = list.get(1);
            assertNotNull(qtyAggregate);
            assertNotNull(priceAggregate);
            assertEquals("quantity", qtyAggregate.getFieldName());
            assertEquals(2, qtyAggregate.getCount());
            assertEquals(14, qtyAggregate.getValue());
            assertEquals("price", priceAggregate.getFieldName());
            assertEquals(2, priceAggregate.getCount());
            assertEquals(40.00, priceAggregate.getValue());

            // Maximum
            list = db.aggregateMany(AggregateType.MAXIMUM,
                    new FruitQuery().addSelect("price", "quantity").ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals(2, list.size());

            priceAggregate = list.get(0);
            qtyAggregate = list.get(1);
            assertNotNull(priceAggregate);
            assertNotNull(qtyAggregate);
            assertEquals("price", priceAggregate.getFieldName());
            assertEquals(4, priceAggregate.getCount());
            assertEquals(60.00, priceAggregate.getValue());
            assertEquals("quantity", qtyAggregate.getFieldName());
            assertEquals(4, qtyAggregate.getCount());
            assertEquals(45, qtyAggregate.getValue());

            // Minimum
            list = db.aggregateMany(AggregateType.MINIMUM,
                    new FruitQuery().addLike("name", "apple").addSelect("quantity", "price"));
            assertNotNull(list);
            assertEquals(2, list.size());

            qtyAggregate = list.get(0);
            priceAggregate = list.get(1);
            assertNotNull(qtyAggregate);
            assertNotNull(priceAggregate);
            assertEquals("quantity", qtyAggregate.getFieldName());
            assertEquals(2, qtyAggregate.getCount());
            assertEquals(3, qtyAggregate.getValue());
            assertEquals("price", priceAggregate.getFieldName());
            assertEquals(2, priceAggregate.getCount());
            assertEquals(20.00, priceAggregate.getValue());
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testAggregateCountDistinct() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00, 25));
            db.create(new Fruit("grape", "red", 25.00, 11));
            db.create(new Fruit("pineapple", "cyan", 60.00, 3));
            db.create(new Fruit("banana", "yellow", 45.00, 45));
            db.create(new Fruit("orange", "orange", 15.00, 11));

            // Count
            Aggregate<?> countAggregate =
                    db.aggregate(AggregateType.COUNT, new FruitQuery().addSelect("color").addLessThanEqual("price", 45.00));
            assertNotNull(countAggregate);
            assertEquals("color", countAggregate.getFieldName());
            assertEquals(4, countAggregate.getCount());
            assertEquals("4", countAggregate.getValue());

            // Count with distinct
            countAggregate = db.aggregate(AggregateType.COUNT,
                    new FruitQuery().addSelect("color").addLessThanEqual("price", 45.00).setDistinct(true));
            assertNotNull(countAggregate);
            assertEquals("color", countAggregate.getFieldName());
            assertEquals(4, countAggregate.getCount());
            assertEquals("3", countAggregate.getValue());

        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCountRecord() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("orange", "orange", 15.00));
            assertEquals(4, db.countAll(new FruitQuery().ignoreEmptyCriteria(true)));
            assertEquals(2, db.countAll(new FruitQuery().addLessThanEqual("price", 20.00)));
            assertEquals(1, db.countAll(new FruitQuery().addBeginsWith("name", "ban")));
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testGetMinValueEmptyCriteria() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("orange", "orange", 15.00));
            assertEquals(Double.valueOf(15.00),
                    db.min(Double.class, "price", new FruitQuery().ignoreEmptyCriteria(true)));
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testGetMinValueWithCriteria() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("orange", "orange", 15.00));
            assertEquals(Double.valueOf(20.00), db.min(Double.class, "price", new FruitQuery().addLike("name", "app")));
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testGetMinValueNoMatchRecords() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("orange", "orange", 15.00));
            assertNull(db.min(Double.class, "price", new FruitQuery().addEquals("name", "tangerine")));
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testGetMaxValueEmptyCriteria() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("orange", "orange", 15.00));
            assertEquals(Double.valueOf(60.00),
                    db.max(Double.class, "price", new FruitQuery().ignoreEmptyCriteria(true)));
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testGetMaxValueWithCriteria() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("orange", "orange", 15.00));
            assertEquals(Double.valueOf(45.00), db.max(Double.class, "price", new FruitQuery().addNotLike("name", "app")));
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testGetMaxValueNoMatchRecords() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("orange", "orange", 15.00));
            assertNull(db.max(Double.class, "price", new FruitQuery().addEquals("name", "tangerine")));
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecord() throws Exception {
        tm.beginTransaction();
        try {
            Fruit fruitToCreate = new Fruit("apple", "red", 20.00);
            Long id = (Long) db.create(fruitToCreate);
            assertNotNull(id);
            assertEquals(id, fruitToCreate.getId());

            Fruit createdFruit = db.find(Fruit.class, id);
            assertEquals(fruitToCreate, createdFruit);
            assertFalse(createdFruit.equals(new Fruit("banana", "yellow", 45.00)));

            Office bingoOffice = new Office("24, Parklane Apapa", "+2348888888", 20);
            bingoOffice.setWorkDays(new String[] { "mon", "tue", "wed" });

        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecordWithArrayProperty() throws Exception {
        tm.beginTransaction();
        try {
            Office bingoOffice = new Office("24, Parklane Apapa", "+2348888888", 20);
            bingoOffice.setWorkDays(new String[] { "mon", "tue", "wed" });
            Long id = (Long) db.create(bingoOffice);
            assertNotNull(id);
            assertEquals(id, bingoOffice.getId());

            Office createdOffice = db.find(Office.class, id);
            assertEquals(bingoOffice.getTelephone(), createdOffice.getTelephone());
            assertEquals(bingoOffice.getAddress(), createdOffice.getAddress());
            String[] originalDays = bingoOffice.getWorkDays();
            String[] createdDays = createdOffice.getWorkDays();
            assertNotNull(originalDays);
            assertNotNull(createdDays);
            assertEquals(originalDays.length, createdDays.length);
            assertEquals(originalDays[0], createdDays[0]);
            assertEquals(originalDays[1], createdDays[1]);
            assertEquals(originalDays[2], createdDays[2]);
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testPersistenceAfterTransaction() throws Exception {
        Fruit apple = new Fruit("apple", "red", 20.00);
        Fruit banana = new Fruit("banana", "yellow", 45.00);
        tm.beginTransaction();
        try {
            db.create(apple);
            db.create(banana);
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }

        tm.beginTransaction();
        try {
            List<Fruit> fruitList = db.findAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("price"));
            assertEquals(2, fruitList.size());
            assertEquals(apple, fruitList.get(0));
            assertEquals(banana, fruitList.get(1));
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testRollback() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            tm.setRollback();
        } finally {
            tm.endTransaction();
        }

        tm.beginTransaction();
        try {
            assertEquals(0, db.countAll(new FruitQuery().ignoreEmptyCriteria(true)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testRollbackToSavepoint() throws Exception {
        Fruit apple = new Fruit("apple", "red", 20.00);
        tm.beginTransaction();
        try {
            db.create(apple);
            tm.setSavePoint();

            db.create(new Fruit("banana", "yellow", 45.00));
            tm.rollbackToSavePoint();
        } finally {
            tm.endTransaction();
        }

        tm.beginTransaction();
        try {
            List<Fruit> fruitList = db.findAll(new FruitQuery().ignoreEmptyCriteria(true));
            assertEquals(1, fruitList.size());
            assertEquals(apple, fruitList.get(0));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordById() throws Exception {
        tm.beginTransaction();
        try {
            Long id = (Long) db.create(new Fruit("apple", "red", 20.00));
            assertEquals(1, db.countAll(new FruitQuery().addEquals("id", id)));
            db.delete(Fruit.class, id);
            assertEquals(0, db.countAll(new FruitQuery().addEquals("id", id)));
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testDeleteRecordByIdWithInvalidId() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.delete(Fruit.class, 20L);
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdVersion() throws Exception {
        tm.beginTransaction();
        try {
            Long id = (Long) db.create(new Fruit("apple", "red", 20.00));
            Fruit fruitData = db.find(Fruit.class, id);
            db.deleteByIdVersion(fruitData);
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testDeleteRecordByIdVersionWithWrongVersion() throws Exception {
        tm.beginTransaction();
        try {
            Long id = (Long) db.create(new Fruit("apple", "red", 20.00));
            Fruit fruitData = db.find(Fruit.class, id);
            fruitData.setVersion(20L);
            db.deleteByIdVersion(fruitData);
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteAllRecord() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            Fruit pineapple = new Fruit("pineapple", "cyan", 60.00);
            db.create(pineapple);
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("orange", "orange", 15.00));
            assertEquals(3, db.deleteAll(new FruitQuery().addNotEqual("color", "cyan")));
            List<Fruit> testFruitList = db.findAll(new FruitQuery().ignoreEmptyCriteria(true));
            assertEquals(1, testFruitList.size());
            assertEquals(pineapple, testFruitList.get(0));
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteAllRecordWithOrder() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            Fruit pineapple = new Fruit("pineapple", "cyan", 60.00);
            db.create(pineapple);
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("orange", "orange", 15.00));
            assertEquals(3, db.deleteAll(new FruitQuery().addNotEqual("color", "cyan")));
            // Order should be ignored
            List<Fruit> testFruitList = db.findAll(new FruitQuery().addOrder("color").ignoreEmptyCriteria(true));
            assertEquals(1, testFruitList.size());
            assertEquals(pineapple, testFruitList.get(0));
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteAllRecordWithListOnlyCriteria() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Paul Horowitz", 72, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            Author susan = new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, warehouseOfficeId);
            db.create(susan);

            // Delete all parklane authors with list only property (telephone)
            assertEquals(2, db.deleteAll(new AuthorQuery().addEquals("officeTelephone", "+2348888888")));

            // Only Susan should be left
            List<Author> testAuthorList = db.findAll(new AuthorQuery().ignoreEmptyCriteria(true));
            assertEquals(1, testAuthorList.size());
            assertEquals(susan, testAuthorList.get(0));
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteAllRecordsWithLimit() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("banana", "yellow", 35.00));
            db.create(new Fruit("orange", "orange", 24.20));
            db.create(new Fruit("mango", "green", 52.00));
            db.create(new Fruit("pineapple", "green", 63.00));
            db.create(new Fruit("peach", "peach", 11.50));
            db.create(new Fruit("pear", "green", 42.50));
            db.create(new Fruit("avocado", "purple", 99.20));
            db.create(new Fruit("tampico", "red", 4.50));
            db.create(new Fruit("grape", "yellow", 4.50));
            db.create(new Fruit("strawberry", "red", 4.50));
            db.deleteAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("name").setLimit(9));
            List<Fruit> testFruitList = db.findAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("id"));
            assertEquals(2, testFruitList.size());
            assertEquals("tampico", testFruitList.get(0).getName());
            assertEquals("strawberry", testFruitList.get(1).getName());
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteAllRecordsWithOffset() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("banana", "yellow", 35.00));
            db.create(new Fruit("orange", "orange", 24.20));
            db.create(new Fruit("mango", "green", 52.00));
            db.create(new Fruit("pineapple", "green", 63.00));
            db.create(new Fruit("peach", "peach", 11.50));
            db.create(new Fruit("pear", "green", 42.50));
            db.create(new Fruit("avocado", "purple", 99.20));
            db.create(new Fruit("tampico", "red", 4.50));
            db.create(new Fruit("grape", "yellow", 4.50));
            db.create(new Fruit("strawberry", "red", 4.50));
            db.deleteAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("name").setOffset(3));
            List<Fruit> testFruitList = db.findAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("id"));
            assertEquals(3, testFruitList.size());
            assertEquals("apple", testFruitList.get(0).getName());
            assertEquals("banana", testFruitList.get(1).getName());
            assertEquals("avocado", testFruitList.get(2).getName());
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteAllRecordsWithOffsetLimit() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("banana", "yellow", 35.00));
            db.create(new Fruit("orange", "orange", 24.20));
            db.create(new Fruit("mango", "green", 52.00));
            db.create(new Fruit("pineapple", "green", 63.00));
            db.create(new Fruit("peach", "peach", 11.50));
            db.create(new Fruit("pear", "green", 42.50));
            db.create(new Fruit("avocado", "purple", 99.20));
            db.create(new Fruit("tampico", "red", 4.50));
            db.create(new Fruit("grape", "yellow", 4.50));
            db.create(new Fruit("strawberry", "red", 4.50));
            db.deleteAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("id").setOffset(6).setLimit(3));
            List<Fruit> testFruitList = db.findAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("id"));
            assertEquals(8, testFruitList.size());
            assertEquals("apple", testFruitList.get(0).getName());
            assertEquals("banana", testFruitList.get(1).getName());
            assertEquals("orange", testFruitList.get(2).getName());
            assertEquals("mango", testFruitList.get(3).getName());
            assertEquals("pineapple", testFruitList.get(4).getName());
            assertEquals("peach", testFruitList.get(5).getName());
            assertEquals("grape", testFruitList.get(6).getName());
            assertEquals("strawberry", testFruitList.get(7).getName());
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testOnDeleteCascade() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Paul Horowitz", 72, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            Author susan = new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, warehouseOfficeId);
            db.create(susan);

            // Delete all parklane office. A cascade should delete authors in office
            db.deleteById(parklaneOffice);

            // Only Warehouse office and Susan should be left
            List<Office> officeList = db.findAll(new OfficeQuery().ignoreEmptyCriteria(true));
            assertNotNull(officeList.size());
            assertEquals(1, officeList.size());
            assertEquals(warehouseOffice, officeList.get(0));

            List<Author> testAuthorList = db.findAll(new AuthorQuery().ignoreEmptyCriteria(true));
            assertNotNull(testAuthorList.size());
            assertEquals(1, testAuthorList.size());
            assertEquals(susan, testAuthorList.get(0));
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordById() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            Long id = (Long) db.create(apple);
            Fruit foundApple = db.find(Fruit.class, id);
            assertEquals(apple, foundApple);
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testFindRecordByIdWithInvalidId() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.find(Fruit.class, 20L);
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByIdVersion() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            Long id = (Long) db.create(apple);
            Fruit foundApple = db.find(Fruit.class, id, 1L);
            assertEquals(apple, foundApple);
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testFindRecordByIdVersionWithWrongVersion() throws Exception {
        tm.beginTransaction();
        try {
            Long id = (Long) db.create(new Fruit("apple", "red", 20.00));
            db.find(Fruit.class, id, 20L);
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordConstraint() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));

            // There should be no constraint for creating fruit with a different
            // name
            Fruit constraint = db.findConstraint(new Fruit("pineapple", "cyan", 60.00));
            assertNull(constraint);
            db.create(new Fruit("pineapple", "cyan", 60.00));

            // Constraint should exist for fruit with same name
            constraint = db.findConstraint(new Fruit("apple", "green", 32.00));
            assertNotNull(constraint);
            assertEquals("apple", constraint.getName());
            assertEquals("red", constraint.getColor());
            assertEquals(Double.valueOf(20.00), constraint.getPrice());
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            Fruit banana = new Fruit("banana", "yellow", 45.00);
            db.create(banana);
            Fruit foundFruit = db.find(new FruitQuery().addEquals("color", "yellow"));
            assertEquals(banana, foundFruit);
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByMin() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            Fruit foundFruit = db.find(new FruitQuery().addLike("name", "app").setMin("price"));
            assertEquals(apple, foundFruit);
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByMinNoCriteria() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            Fruit foundFruit = db.find(new FruitQuery().setMin("price"));
            assertEquals(apple, foundFruit);
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testFindRecordByMinMultipleResult() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            db.create(new Fruit("pineapple", "cyan", 20.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            Fruit foundFruit = db.find(new FruitQuery().addLike("name", "app").setMin("price"));
            assertEquals(apple, foundFruit);
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testFindRecordByMinNoCriteriaMultipleResult() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 20.00));
            Fruit foundFruit = db.find(new FruitQuery().setMin("price"));
            assertEquals(apple, foundFruit);
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByMax() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            Fruit pineapple = new Fruit("pineapple", "cyan", 60.00);
            db.create(pineapple);
            db.create(new Fruit("banana", "yellow", 45.00));
            Fruit foundFruit = db.find(new FruitQuery().addLike("name", "app").setMax("price"));
            assertEquals(pineapple, foundFruit);
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByMaxNoCriteria() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            Fruit pineapple = new Fruit("pineapple", "cyan", 60.00);
            db.create(pineapple);
            db.create(new Fruit("banana", "yellow", 45.00));
            Fruit foundFruit = db.find(new FruitQuery().setMax("price"));
            assertEquals(pineapple, foundFruit);
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testFindRecordByMaxMultipleResult() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 60.00));
            Fruit pineapple = new Fruit("pineapple", "cyan", 60.00);
            db.create(pineapple);
            db.create(new Fruit("banana", "yellow", 45.00));
            Fruit foundFruit = db.find(new FruitQuery().addLike("name", "app").setMax("price"));
            assertEquals(pineapple, foundFruit);
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testFindRecordByMaxNoCriteriaMultipleResult() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            Fruit pineapple = new Fruit("pineapple", "cyan", 60.00);
            db.create(pineapple);
            db.create(new Fruit("banana", "yellow", 60.00));
            Fruit foundFruit = db.find(new FruitQuery().setMax("price"));
            assertEquals(pineapple, foundFruit);
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByCriteriaWithSelect() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            Fruit foundFruit = db.find(new FruitQuery().addEquals("color", "cyan").addSelect("name", "price"));
            assertNotNull(foundFruit);
            assertEquals("pineapple", foundFruit.getName());
            assertNull(foundFruit.getColor());
            assertEquals(Double.valueOf(60.00), foundFruit.getPrice());
            assertNull(foundFruit.getQuantity());
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testFindRecordByCriteriaWithMultipleResult() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.find(new FruitQuery().addLike("name", "apple"));
        } catch(Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByCriteriaWithNoResult() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            assertNull(db.find(new FruitQuery().addLike("name", "zing")));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecords() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            Fruit orange = new Fruit("orange", "orange", 15.00);
            db.create(orange);
            List<Fruit> testFruitList = db.findAll(new FruitQuery().addLessThanEqual("price", 20.00).addOrder("price"));
            assertEquals(2, testFruitList.size());
            assertEquals(orange, testFruitList.get(0));
            assertEquals(apple, testFruitList.get(1));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsWithOrder() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            Fruit orange = new Fruit("orange", "orange", 15.00);
            db.create(orange);
            List<Fruit> testFruitList =
                    db.findAll(new FruitQuery().addLessThanEqual("price", 20.00).addOrder(OrderType.DESCENDING, "price"));
            assertEquals(2, testFruitList.size());
            assertEquals(apple, testFruitList.get(0));
            assertEquals(orange, testFruitList.get(1));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsWithSelect() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            Fruit orange = new Fruit("orange", "orange", 15.00);
            db.create(orange);
            List<Fruit> testFruitList =
                    db.findAll(new FruitQuery().addLessThanEqual("price", 20.00).addOrder("price").addSelect("name", "quantity"));
            assertEquals(2, testFruitList.size());

            Fruit foundFruit = testFruitList.get(0);
            assertNotNull(foundFruit);
            assertEquals("orange", foundFruit.getName());
            assertNull(foundFruit.getColor());
            assertNull(foundFruit.getPrice());
            assertEquals(Integer.valueOf(0), foundFruit.getQuantity());

            foundFruit = testFruitList.get(1);
            assertNotNull(foundFruit);
            assertEquals("apple", foundFruit.getName());
            assertNull(foundFruit.getColor());
            assertNull(foundFruit.getPrice());
            assertEquals(Integer.valueOf(0), foundFruit.getQuantity());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllMapRecords() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            Fruit orange = new Fruit("orange", "orange", 15.00);
            db.create(orange);
            Map<String, Fruit> testFruitMap =
                    db.findAllMap(String.class, "name", new FruitQuery().addLessThanEqual("price", 20.00).addOrder("price"));
            assertEquals(2, testFruitMap.size());
            assertEquals(orange, testFruitMap.get("orange"));
            assertEquals(apple, testFruitMap.get("apple"));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsWithAmongstSingle() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            Fruit orange = new Fruit("orange", "orange", 15.00);
            db.create(orange);
            List<Fruit> testFruitList = db.findAll(new FruitQuery().addAmongst("name", Arrays.asList("orange")));
            assertEquals(1, testFruitList.size());
            assertEquals(orange, testFruitList.get(0));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsWithAmongstMultiple() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            Fruit orange = new Fruit("orange", "orange", 15.00);
            db.create(orange);
            List<Fruit> testFruitList =
                    db.findAll(new FruitQuery().addAmongst("name", Arrays.asList("apple", "orange")).addOrder("price"));
            assertEquals(2, testFruitList.size());
            assertEquals(orange, testFruitList.get(0));
            assertEquals(apple, testFruitList.get(1));
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testFindAllRecordsWithAmongstNone() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            Fruit orange = new Fruit("orange", "orange", 15.00);
            db.create(orange);
            List<Fruit> testFruitList = db.findAll(new FruitQuery().addAmongst("name", Arrays.asList()));
            assertEquals(1, testFruitList.size());
            assertEquals(orange, testFruitList.get(0));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsWithCriteriaQueryLimit() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("banana", "yellow", 35.00));
            db.create(new Fruit("orange", "orange", 24.20));
            db.create(new Fruit("mango", "green", 52.00));
            db.create(new Fruit("pineapple", "green", 63.00));
            db.create(new Fruit("peach", "peach", 11.50));
            List<Fruit> testFruitList = db.findAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("id").setLimit(4));
            assertEquals(4, testFruitList.size());
            assertEquals("apple", testFruitList.get(0).getName());
            assertEquals("banana", testFruitList.get(1).getName());
            assertEquals("orange", testFruitList.get(2).getName());
            assertEquals("mango", testFruitList.get(3).getName());

            testFruitList = db.findAll(new FruitQuery().addGreaterThan("price", 20.0).addOrder("id").setLimit(2));
            assertEquals(2, testFruitList.size());
            assertEquals("banana", testFruitList.get(0).getName());
            assertEquals("orange", testFruitList.get(1).getName());

        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsWithApplicationQueryLimit() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("banana", "yellow", 35.00));
            db.create(new Fruit("orange", "orange", 24.20));
            db.create(new Fruit("mango", "green", 52.00));
            db.create(new Fruit("pineapple", "green", 63.00));
            db.create(new Fruit("peach", "peach", 11.50));
            db.create(new Fruit("pear", "green", 42.50));
            db.create(new Fruit("avocado", "purple", 99.20));
            db.create(new Fruit("grape", "yellow", 4.50));
            db.create(new Fruit("strawberry", "red", 4.50));
            List<Fruit> testFruitList =
                    db.findAll(new FruitQuery().ignoreEmptyCriteria(true).applyAppQueryLimit(true).addOrder("id"));
            assertEquals(8, testFruitList.size());
            assertEquals("apple", testFruitList.get(0).getName());
            assertEquals("banana", testFruitList.get(1).getName());
            assertEquals("orange", testFruitList.get(2).getName());
            assertEquals("mango", testFruitList.get(3).getName());
            assertEquals("pineapple", testFruitList.get(4).getName());
            assertEquals("peach", testFruitList.get(5).getName());
            assertEquals("pear", testFruitList.get(6).getName());
            assertEquals("avocado", testFruitList.get(7).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsWithNoQueryLimit() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("banana", "yellow", 35.00));
            db.create(new Fruit("orange", "orange", 24.20));
            db.create(new Fruit("mango", "green", 52.00));
            db.create(new Fruit("pineapple", "green", 63.00));
            db.create(new Fruit("peach", "peach", 11.50));
            db.create(new Fruit("pear", "green", 42.50));
            db.create(new Fruit("avocado", "purple", 99.20));
            db.create(new Fruit("grape", "yellow", 4.50));
            db.create(new Fruit("strawberry", "red", 4.50));
            List<Fruit> testFruitList = db.findAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("id"));
            assertEquals(10, testFruitList.size());
            assertEquals("apple", testFruitList.get(0).getName());
            assertEquals("banana", testFruitList.get(1).getName());
            assertEquals("orange", testFruitList.get(2).getName());
            assertEquals("mango", testFruitList.get(3).getName());
            assertEquals("pineapple", testFruitList.get(4).getName());
            assertEquals("peach", testFruitList.get(5).getName());
            assertEquals("pear", testFruitList.get(6).getName());
            assertEquals("avocado", testFruitList.get(7).getName());
            assertEquals("grape", testFruitList.get(8).getName());
            assertEquals("strawberry", testFruitList.get(9).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsWithQueryBlankCopy() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("banana", "yellow", 35.00));
            db.create(new Fruit("orange", "orange", 24.20));
            db.create(new Fruit("mango", "green", 52.00));
            db.create(new Fruit("pineapple", "green", 63.00));
            db.create(new Fruit("peach", "peach", 11.50));
            db.create(new Fruit("pear", "green", 42.50));
            db.create(new Fruit("avocado", "purple", 99.20));
            db.create(new Fruit("grape", "yellow", 4.50));
            db.create(new Fruit("strawberry", "red", 4.50));
            
            FruitQuery query = (FruitQuery) new FruitQuery().ignoreEmptyCriteria(true);
            Restriction restriction = query.getRestrictions();
            Query<Fruit> copyQuery =
                    query.copyNoCriteria().addRestriction(restriction).addLike("name", "apple").addOrder("name");
            
            List<Fruit> testFruitList = db.findAll(copyQuery);
            
            assertEquals(2, testFruitList.size());
            assertEquals("apple", testFruitList.get(0).getName());
            assertEquals("pineapple", testFruitList.get(1).getName());
        } finally {
            tm.endTransaction();
        }
    }
    
    @Test
    public void testListRecordById() throws Exception {
        tm.beginTransaction();
        try {
            Long testOfficeId = (Long) db.create(parklaneOffice);
            Author author = new Author("Paul Horowitz", 72, Gender.MALE, BooleanType.FALSE, testOfficeId);

            Long id = (Long) db.create(author);
            Author foundAuthor = db.list(Author.class, id);

            assertTrue(!foundAuthor.equals(author));
            assertEquals("Paul Horowitz", foundAuthor.getName());
            assertEquals(Integer.valueOf(72), foundAuthor.getAge());
            assertEquals(Gender.MALE, foundAuthor.getGender());
            assertEquals(testOfficeId, foundAuthor.getOfficeId());
            assertEquals("24, Parklane Apapa", foundAuthor.getOfficeAddress());
            assertEquals("+2348888888", foundAuthor.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testPopulateListOnly() throws Exception {
        tm.beginTransaction();
        try {
            Long officeId = (Long) db.create(parklaneOffice);
            Author author = new Author();
            author.setOfficeId(officeId);
            author.setRetired(BooleanType.TRUE);

            db.populateListOnly(author);
            assertEquals("24, Parklane Apapa", author.getOfficeAddress());
            assertEquals("+2348888888", author.getOfficeTelephone());
            assertEquals("True", author.getRetiredDesc());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testPopulateListOnlyNullFk() throws Exception {
        tm.beginTransaction();
        try {
            Author author = new Author();
            author.setOfficeId(null);
            author.setRetired(null);

            db.populateListOnly(author);
            assertNull(author.getOfficeAddress());
            assertNull(author.getOfficeTelephone());
            assertNull(author.getRetiredDesc());
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testListRecordByIdWithInvalidId() throws Exception {
        tm.beginTransaction();
        try {
            Long testOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Winfield Hill", 75, Gender.MALE, BooleanType.FALSE, testOfficeId));
            db.list(Author.class, 20L);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByIdVersion() throws Exception {
        tm.beginTransaction();
        try {
            Long testOfficeId = (Long) db.create(warehouseOffice);
            Author author = new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, testOfficeId);

            Long id = (Long) db.create(author);
            Author foundAuthor = db.list(Author.class, id, 1L);

            assertTrue(!foundAuthor.equals(author));
            assertEquals("Brian Bramer", foundAuthor.getName());
            assertEquals(Integer.valueOf(50), foundAuthor.getAge());
            assertEquals(Gender.MALE, foundAuthor.getGender());
            assertEquals(testOfficeId, foundAuthor.getOfficeId());
            assertEquals("38, Warehouse Road Apapa", foundAuthor.getOfficeAddress());
            assertEquals("+2345555555", foundAuthor.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testListRecordByIdVersionWithWrongVersion() throws Exception {
        tm.beginTransaction();
        try {
            Long testOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Susan Bramer", 48, Gender.FEMALE, BooleanType.FALSE, testOfficeId));
            db.list(Author.class, testOfficeId, 20L);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            Long testOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, testOfficeId));
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.TRUE, testOfficeId));

            Author foundAuthor = db.list(new AuthorQuery().addEquals("age", 45));
            assertEquals("Susan Bramer", foundAuthor.getName());
            assertEquals(Integer.valueOf(45), foundAuthor.getAge());
            assertEquals(Gender.FEMALE, foundAuthor.getGender());
            assertEquals(BooleanType.TRUE, foundAuthor.getRetired());
            assertEquals("True", foundAuthor.getRetiredDesc());
            assertEquals(testOfficeId, foundAuthor.getOfficeId());
            assertEquals("38, Warehouse Road Apapa", foundAuthor.getOfficeAddress());
            assertEquals("+2345555555", foundAuthor.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByCriteriaWithSelect() throws Exception {
        tm.beginTransaction();
        try {
            Long testOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, testOfficeId));
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.TRUE, testOfficeId));

            Author foundAuthor = db.list(new AuthorQuery().addEquals("age", 45).addSelect("name", "gender", "retiredDesc",
                    "officeId", "officeAddress"));
            assertEquals("Susan Bramer", foundAuthor.getName());
            assertEquals(Gender.FEMALE, foundAuthor.getGender());
            assertNull(foundAuthor.getRetired());
            assertEquals("True", foundAuthor.getRetiredDesc());
            assertEquals(testOfficeId, foundAuthor.getOfficeId());
            assertEquals("38, Warehouse Road Apapa", foundAuthor.getOfficeAddress());
            assertNull(foundAuthor.getOfficeTelephone());
            assertNull(foundAuthor.getAge());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByCriteriaWithListOnlyCriteria() throws Exception {
        tm.beginTransaction();
        try {
            Long testOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, testOfficeId));
            testOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, testOfficeId));

            Author foundAuthor = db.list(new AuthorQuery().addEquals("officeTelephone", "+2345555555"));
            assertEquals("Susan Bramer", foundAuthor.getName());
            assertEquals(Integer.valueOf(45), foundAuthor.getAge());
            assertEquals(Gender.FEMALE, foundAuthor.getGender());
            assertEquals(BooleanType.FALSE, foundAuthor.getRetired());
            assertEquals("False", foundAuthor.getRetiredDesc());
            assertEquals(testOfficeId, foundAuthor.getOfficeId());
            assertEquals("38, Warehouse Road Apapa", foundAuthor.getOfficeAddress());
            assertEquals("+2345555555", foundAuthor.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testListRecordByCriteriaWithMultipleResult() throws Exception {
        tm.beginTransaction();
        try {
            Long testOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, testOfficeId));
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, testOfficeId));
            db.create(new Author("Winfield Hill", 75, Gender.MALE, BooleanType.FALSE, testOfficeId));
            db.list(new AuthorQuery().addLessThan("age", 60));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByCriteriaWithNoResult() throws Exception {
        tm.beginTransaction();
        try {
            Long testOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, testOfficeId));
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, testOfficeId));
            db.create(new Author("Winfield Hill", 75, Gender.MALE, BooleanType.FALSE, testOfficeId));
            assertNull(db.list(new AuthorQuery().addGreaterThan("age", 100)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecords() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Winfield Hill", 75, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));

            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.TRUE, warehouseOfficeId));

            // Pick authors with age less or equals 50 and order by name
            List<Author> testAuthorList = db.listAll(new AuthorQuery().addLessThanEqual("age", 50).addOrder("name"));
            assertEquals(2, testAuthorList.size());

            // Should pick the Bramers with different offices
            Author foundAuthor = testAuthorList.get(0);
            assertEquals("Brian Bramer", foundAuthor.getName());
            assertEquals(Integer.valueOf(50), foundAuthor.getAge());
            assertEquals(Gender.MALE, foundAuthor.getGender());
            assertEquals(BooleanType.FALSE, foundAuthor.getRetired());
            assertEquals("False", foundAuthor.getRetiredDesc());
            assertEquals(parklaneOfficeId, foundAuthor.getOfficeId());
            assertEquals("24, Parklane Apapa", foundAuthor.getOfficeAddress());
            assertEquals("+2348888888", foundAuthor.getOfficeTelephone());

            foundAuthor = testAuthorList.get(1);
            assertEquals("Susan Bramer", foundAuthor.getName());
            assertEquals(Integer.valueOf(45), foundAuthor.getAge());
            assertEquals(Gender.FEMALE, foundAuthor.getGender());
            assertEquals(BooleanType.TRUE, foundAuthor.getRetired());
            assertEquals("True", foundAuthor.getRetiredDesc());
            assertEquals(warehouseOfficeId, foundAuthor.getOfficeId());
            assertEquals("38, Warehouse Road Apapa", foundAuthor.getOfficeAddress());
            assertEquals("+2345555555", foundAuthor.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecordsWithOrder() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Winfield Hill", 75, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));

            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.TRUE, warehouseOfficeId));

            // Pick authors with age less or equals 50 and order by name
            List<Author> testAuthorList =
                    db.listAll(new AuthorQuery().addLessThanEqual("age", 50).addOrder(OrderType.DESCENDING, "name"));
            assertEquals(2, testAuthorList.size());

            // Should pick the Bramers with different offices
            Author foundAuthor = testAuthorList.get(0);
            assertEquals("Susan Bramer", foundAuthor.getName());
            assertEquals(Integer.valueOf(45), foundAuthor.getAge());
            assertEquals(Gender.FEMALE, foundAuthor.getGender());
            assertEquals(BooleanType.TRUE, foundAuthor.getRetired());
            assertEquals("True", foundAuthor.getRetiredDesc());
            assertEquals(warehouseOfficeId, foundAuthor.getOfficeId());
            assertEquals("38, Warehouse Road Apapa", foundAuthor.getOfficeAddress());
            assertEquals("+2345555555", foundAuthor.getOfficeTelephone());

            foundAuthor = testAuthorList.get(1);
            assertEquals("Brian Bramer", foundAuthor.getName());
            assertEquals(Integer.valueOf(50), foundAuthor.getAge());
            assertEquals(Gender.MALE, foundAuthor.getGender());
            assertEquals(BooleanType.FALSE, foundAuthor.getRetired());
            assertEquals("False", foundAuthor.getRetiredDesc());
            assertEquals(parklaneOfficeId, foundAuthor.getOfficeId());
            assertEquals("24, Parklane Apapa", foundAuthor.getOfficeAddress());
            assertEquals("+2348888888", foundAuthor.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecordsWithSelect() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Winfield Hill", 75, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));

            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.TRUE, warehouseOfficeId));

            // Pick authors with age less or equals 50 and order by name
            List<Author> testAuthorList = db.listAll(new AuthorQuery().addLessThanEqual("age", 50).addOrder("name").addSelect("age",
                    "retired", "gender", "officeTelephone"));
            assertEquals(2, testAuthorList.size());

            // Should pick the Bramers with different offices
            Author foundAuthor = testAuthorList.get(0);
            assertNull(foundAuthor.getName());
            assertEquals(Integer.valueOf(50), foundAuthor.getAge());
            assertEquals(Gender.MALE, foundAuthor.getGender());
            assertEquals(BooleanType.FALSE, foundAuthor.getRetired());
            assertNull(foundAuthor.getRetiredDesc());
            assertNull(foundAuthor.getOfficeId());
            assertNull(foundAuthor.getOfficeAddress());
            assertEquals("+2348888888", foundAuthor.getOfficeTelephone());

            foundAuthor = testAuthorList.get(1);
            assertNull(foundAuthor.getName());
            assertEquals(Integer.valueOf(45), foundAuthor.getAge());
            assertEquals(Gender.FEMALE, foundAuthor.getGender());
            assertEquals(BooleanType.TRUE, foundAuthor.getRetired());
            assertNull(foundAuthor.getRetiredDesc());
            assertNull(foundAuthor.getOfficeId());
            assertNull(foundAuthor.getOfficeAddress());
            assertEquals("+2345555555", foundAuthor.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllMapRecords() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Winfield Hill", 75, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.TRUE, parklaneOfficeId));

            // Pick authors with age less or equals 50 and order by name
            Map<String, Author> testAuthorMap =
                    db.listAllMap(String.class, "name", new AuthorQuery().addLessThanEqual("age", 50).addOrder("name"));
            assertNotNull(testAuthorMap);
            assertEquals(2, testAuthorMap.size());

            // Should pick the Bramers
            Author foundAuthor = testAuthorMap.get("Brian Bramer");
            assertEquals("Brian Bramer", foundAuthor.getName());
            assertEquals(Integer.valueOf(50), foundAuthor.getAge());
            assertEquals(Gender.MALE, foundAuthor.getGender());
            assertEquals(BooleanType.FALSE, foundAuthor.getRetired());
            assertEquals("False", foundAuthor.getRetiredDesc());
            assertEquals(parklaneOfficeId, foundAuthor.getOfficeId());
            assertEquals("24, Parklane Apapa", foundAuthor.getOfficeAddress());
            assertEquals("+2348888888", foundAuthor.getOfficeTelephone());

            foundAuthor = testAuthorMap.get("Susan Bramer");
            assertEquals("Susan Bramer", foundAuthor.getName());
            assertEquals(Integer.valueOf(45), foundAuthor.getAge());
            assertEquals(Gender.FEMALE, foundAuthor.getGender());
            assertEquals(BooleanType.TRUE, foundAuthor.getRetired());
            assertEquals("True", foundAuthor.getRetiredDesc());
            assertEquals(parklaneOfficeId, foundAuthor.getOfficeId());
            assertEquals("24, Parklane Apapa", foundAuthor.getOfficeAddress());
            assertEquals("+2348888888", foundAuthor.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecordsWithCriteriaQueryLimit() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("banana", "yellow", 35.00));
            db.create(new Fruit("orange", "orange", 24.20));
            db.create(new Fruit("mango", "green", 52.00));
            db.create(new Fruit("pineapple", "green", 63.00));
            db.create(new Fruit("peach", "peach", 11.50));
            List<Fruit> testFruitList = db.listAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("id").setLimit(4));
            assertEquals(4, testFruitList.size());
            assertEquals("apple", testFruitList.get(0).getName());
            assertEquals("banana", testFruitList.get(1).getName());
            assertEquals("orange", testFruitList.get(2).getName());
            assertEquals("mango", testFruitList.get(3).getName());

            testFruitList = db.findAll(new FruitQuery().addGreaterThan("price", 20.0).addOrder("id").setLimit(2));
            assertEquals(2, testFruitList.size());
            assertEquals("banana", testFruitList.get(0).getName());
            assertEquals("orange", testFruitList.get(1).getName());

        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecordsWithApplicationQueryLimit() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("banana", "yellow", 35.00));
            db.create(new Fruit("orange", "orange", 24.20));
            db.create(new Fruit("mango", "green", 52.00));
            db.create(new Fruit("pineapple", "green", 63.00));
            db.create(new Fruit("peach", "peach", 11.50));
            db.create(new Fruit("pear", "green", 42.50));
            db.create(new Fruit("avocado", "purple", 99.20));
            db.create(new Fruit("grape", "yellow", 4.50));
            db.create(new Fruit("strawberry", "red", 4.50));
            List<Fruit> testFruitList =
                    db.listAll(new FruitQuery().ignoreEmptyCriteria(true).applyAppQueryLimit(true).addOrder("id"));
            assertEquals(8, testFruitList.size());
            assertEquals("apple", testFruitList.get(0).getName());
            assertEquals("banana", testFruitList.get(1).getName());
            assertEquals("orange", testFruitList.get(2).getName());
            assertEquals("mango", testFruitList.get(3).getName());
            assertEquals("pineapple", testFruitList.get(4).getName());
            assertEquals("peach", testFruitList.get(5).getName());
            assertEquals("pear", testFruitList.get(6).getName());
            assertEquals("avocado", testFruitList.get(7).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecordsWithNoQueryLimit() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("banana", "yellow", 35.00));
            db.create(new Fruit("orange", "orange", 24.20));
            db.create(new Fruit("mango", "green", 52.00));
            db.create(new Fruit("pineapple", "green", 63.00));
            db.create(new Fruit("peach", "peach", 11.50));
            db.create(new Fruit("pear", "green", 42.50));
            db.create(new Fruit("avocado", "purple", 99.20));
            db.create(new Fruit("grape", "yellow", 4.50));
            db.create(new Fruit("strawberry", "red", 4.50));
            List<Fruit> testFruitList = db.listAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("id"));
            assertEquals(10, testFruitList.size());
            assertEquals("apple", testFruitList.get(0).getName());
            assertEquals("banana", testFruitList.get(1).getName());
            assertEquals("orange", testFruitList.get(2).getName());
            assertEquals("mango", testFruitList.get(3).getName());
            assertEquals("pineapple", testFruitList.get(4).getName());
            assertEquals("peach", testFruitList.get(5).getName());
            assertEquals("pear", testFruitList.get(6).getName());
            assertEquals("avocado", testFruitList.get(7).getName());
            assertEquals("grape", testFruitList.get(8).getName());
            assertEquals("strawberry", testFruitList.get(9).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordWithListOnlyCriteria() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Winfield Hill", 75, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));

            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, warehouseOfficeId));

            // Pick authors with parklane telephone (view-only) and order by
            // name
            List<Author> testAuthorList =
                    db.findAll(new AuthorQuery().addEquals("officeTelephone", "+2348888888").addOrder("name"));
            assertEquals(2, testAuthorList.size());

            // Should pick the Brian and Winfield with blank list-only properties
            Author foundAuthor = testAuthorList.get(0);
            assertEquals("Brian Bramer", foundAuthor.getName());
            assertEquals(Integer.valueOf(50), foundAuthor.getAge());
            assertEquals(Gender.MALE, foundAuthor.getGender());
            assertEquals(BooleanType.FALSE, foundAuthor.getRetired());
            assertEquals(parklaneOfficeId, foundAuthor.getOfficeId());
            assertNull(foundAuthor.getRetiredDesc());
            assertNull(foundAuthor.getOfficeAddress());
            assertNull(foundAuthor.getOfficeTelephone());

            foundAuthor = testAuthorList.get(1);
            assertEquals("Winfield Hill", foundAuthor.getName());
            assertEquals(Integer.valueOf(75), foundAuthor.getAge());
            assertEquals(Gender.MALE, foundAuthor.getGender());
            assertEquals(BooleanType.FALSE, foundAuthor.getRetired());
            assertEquals(parklaneOfficeId, foundAuthor.getOfficeId());
            assertNull(foundAuthor.getRetiredDesc());
            assertNull(foundAuthor.getOfficeAddress());
            assertNull(foundAuthor.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecordWithListOnlyCriteria() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Winfield Hill", 75, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));

            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, warehouseOfficeId));

            // Pick authors with parklane telephone (view-only) and order by
            // name
            List<Author> testAuthorList =
                    db.listAll(new AuthorQuery().addEquals("officeTelephone", "+2348888888").addOrder("name"));
            assertEquals(2, testAuthorList.size());

            // Should pick the Brian and Winfield with different offices
            Author foundAuthor = testAuthorList.get(0);
            assertEquals("Brian Bramer", foundAuthor.getName());
            assertEquals(Integer.valueOf(50), foundAuthor.getAge());
            assertEquals(Gender.MALE, foundAuthor.getGender());
            assertEquals(BooleanType.FALSE, foundAuthor.getRetired());
            assertEquals("False", foundAuthor.getRetiredDesc());
            assertEquals(parklaneOfficeId, foundAuthor.getOfficeId());
            assertEquals("24, Parklane Apapa", foundAuthor.getOfficeAddress());
            assertEquals("+2348888888", foundAuthor.getOfficeTelephone());

            foundAuthor = testAuthorList.get(1);
            assertEquals("Winfield Hill", foundAuthor.getName());
            assertEquals(Integer.valueOf(75), foundAuthor.getAge());
            assertEquals(Gender.MALE, foundAuthor.getGender());
            assertEquals(BooleanType.FALSE, foundAuthor.getRetired());
            assertEquals("False", foundAuthor.getRetiredDesc());
            assertEquals(parklaneOfficeId, foundAuthor.getOfficeId());
            assertEquals("24, Parklane Apapa", foundAuthor.getOfficeAddress());
            assertEquals("+2348888888", foundAuthor.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateRecordById() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            apple.setColor("green");
            apple.setPrice(50.00);
            assertEquals(1, db.updateById(apple));

            Fruit foundFruit = db.find(Fruit.class, apple.getId());
            assertEquals(apple, foundFruit);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate")).addParameter(new ReportParameter("endDate"));
            Long reportId = (Long) db.create(report);

            report.setDescription("New Weekly Report");
            report.setParameters(Arrays.asList(new ReportParameter("resolutionDate")));
            db.updateById(report);

            Report fetchedReport = db.find(Report.class, reportId);
            assertNotNull(fetchedReport);
            assertEquals("weeklyReport", fetchedReport.getName());
            assertEquals("New Weekly Report", fetchedReport.getDescription());

            List<ReportParameter> reportParamList = fetchedReport.getParameters();
            assertNotNull(reportParamList);
            assertEquals(1, reportParamList.size());
            assertEquals("resolutionDate", reportParamList.get(0).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateLeanRecordById() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            apple.setColor("green");
            apple.setPrice(50.00);
            assertEquals(1, db.updateLeanById(apple));

            Fruit foundFruit = db.find(Fruit.class, apple.getId());
            assertEquals(apple, foundFruit);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateLeanRecordByIdWithBlankChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate")).addParameter(new ReportParameter("endDate"));
            Long reportId = (Long) db.create(report);

            report.setDescription("New Weekly Report");
            report.setParameters(null); // blank
            db.updateLeanById(report);

            Report fetchedReport = db.find(Report.class, reportId);
            assertNotNull(fetchedReport);
            assertEquals("weeklyReport", fetchedReport.getName());
            assertEquals("New Weekly Report", fetchedReport.getDescription());

            List<ReportParameter> reportParamList = fetchedReport.getParameters();
            assertNotNull(reportParamList);
            assertEquals(2, reportParamList.size());
            assertEquals("startDate", reportParamList.get(0).getName());
            assertEquals("endDate", reportParamList.get(1).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateLeanRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate")).addParameter(new ReportParameter("endDate"));
            Long reportId = (Long) db.create(report);

            report.setDescription("New Weekly Report");
            report.setParameters(Arrays.asList(new ReportParameter("resolutionDate")));
            db.updateLeanById(report);

            Report fetchedReport = db.find(Report.class, reportId);
            assertNotNull(fetchedReport);
            assertEquals("weeklyReport", fetchedReport.getName());
            assertEquals("New Weekly Report", fetchedReport.getDescription());

            List<ReportParameter> reportParamList = fetchedReport.getParameters();
            assertNotNull(reportParamList);
            assertEquals(2, reportParamList.size());
            assertEquals("startDate", reportParamList.get(0).getName());
            assertEquals("endDate", reportParamList.get(1).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateRecordByIdUsingUpdate() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            Long id = (Long) db.create(apple);
            assertEquals(1, db.updateById(Fruit.class, id, new Update().add("color", "green")));

            Fruit foundFruit = db.find(Fruit.class, apple.getId());
            assertEquals("apple", foundFruit.getName());
            assertEquals("green", foundFruit.getColor());
            assertEquals(Double.valueOf(20.00), foundFruit.getPrice());
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testUpdateRecordByIdWithInvalidId() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            apple.setId(10L); // Change ID
            apple.setColor("green");
            apple.setPrice(50.00);
            db.updateById(apple);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateRecordByIdVersion() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            apple.setColor("green");
            apple.setPrice(50.00);
            assertEquals(1, db.updateByIdVersion(apple));

            Fruit foundFruit = db.find(Fruit.class, apple.getId());
            assertEquals(apple, foundFruit);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateRecordByIdVersionWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate")).addParameter(new ReportParameter("endDate"));
            Long reportId = (Long) db.create(report);

            report.setDescription("New Weekly Report");
            report.setParameters(Arrays.asList(new ReportParameter("resolutionDate")));
            db.updateByIdVersion(report);

            Report fetchedReport = db.find(Report.class, reportId);
            assertNotNull(fetchedReport);
            assertEquals("weeklyReport", fetchedReport.getName());
            assertEquals("New Weekly Report", fetchedReport.getDescription());

            List<ReportParameter> reportParamList = fetchedReport.getParameters();
            assertNotNull(reportParamList);
            assertEquals(1, reportParamList.size());
            assertEquals("resolutionDate", reportParamList.get(0).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateLeanRecordByIdVersion() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            apple.setColor("green");
            apple.setPrice(50.00);
            assertEquals(1, db.updateLeanByIdVersion(apple));

            Fruit foundFruit = db.find(Fruit.class, apple.getId());
            assertEquals(apple, foundFruit);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateLeanRecordByIdVersionWithBlankChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate")).addParameter(new ReportParameter("endDate"));
            Long reportId = (Long) db.create(report);

            report.setDescription("New Weekly Report");
            report.setParameters(null); // Blank child list
            db.updateLeanByIdVersion(report);

            Report fetchedReport = db.find(Report.class, reportId);
            assertNotNull(fetchedReport);
            assertEquals("weeklyReport", fetchedReport.getName());
            assertEquals("New Weekly Report", fetchedReport.getDescription());

            List<ReportParameter> reportParamList = fetchedReport.getParameters();
            assertNotNull(reportParamList);
            assertEquals(2, reportParamList.size());
            assertEquals("startDate", reportParamList.get(0).getName());
            assertEquals("endDate", reportParamList.get(1).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateLeanRecordByIdVersionWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate")).addParameter(new ReportParameter("endDate"));
            Long reportId = (Long) db.create(report);

            report.setDescription("New Weekly Report");
            report.setParameters(Arrays.asList(new ReportParameter("resolutionDate")));
            db.updateLeanByIdVersion(report);

            Report fetchedReport = db.find(Report.class, reportId);
            assertNotNull(fetchedReport);
            assertEquals("weeklyReport", fetchedReport.getName());
            assertEquals("New Weekly Report", fetchedReport.getDescription());

            List<ReportParameter> reportParamList = fetchedReport.getParameters();
            assertNotNull(reportParamList);
            assertEquals(2, reportParamList.size());
            assertEquals("startDate", reportParamList.get(0).getName());
            assertEquals("endDate", reportParamList.get(1).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testUpdateRecordByIdVersionWithWrongVersion() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            apple.setColor("green");
            apple.setPrice(50.00);
            apple.setVersion(10L); // Change Version
            db.updateByIdVersion(apple);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateAllRecord() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("orange", "orange", 15.00));
            assertEquals(3, db.updateAll(new FruitQuery().addLessThanEqual("price", 45.00), new Update().add("price", 30.00)));
            List<Fruit> testFruitList = db.findAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("name"));
            assertEquals(4, testFruitList.size());

            assertEquals("apple", testFruitList.get(0).getName());
            assertEquals(Double.valueOf(30.00), testFruitList.get(0).getPrice());
            assertEquals("banana", testFruitList.get(1).getName());
            assertEquals(Double.valueOf(30.00), testFruitList.get(1).getPrice());
            assertEquals("orange", testFruitList.get(2).getName());
            assertEquals(Double.valueOf(30.00), testFruitList.get(2).getPrice());
            assertEquals("pineapple", testFruitList.get(3).getName());
            assertEquals(Double.valueOf(60.00), testFruitList.get(3).getPrice());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateAllRecordWithOrder() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("orange", "orange", 15.00));
            assertEquals(3, db.updateAll(new FruitQuery().addLessThanEqual("price", 45.00).addOrder("id"),
                    new Update().add("price", 30.00)));
            List<Fruit> testFruitList = db.findAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("name"));
            assertEquals(4, testFruitList.size());

            assertEquals("apple", testFruitList.get(0).getName());
            assertEquals(Double.valueOf(30.00), testFruitList.get(0).getPrice());
            assertEquals("banana", testFruitList.get(1).getName());
            assertEquals(Double.valueOf(30.00), testFruitList.get(1).getPrice());
            assertEquals("orange", testFruitList.get(2).getName());
            assertEquals(Double.valueOf(30.00), testFruitList.get(2).getPrice());
            assertEquals("pineapple", testFruitList.get(3).getName());
            assertEquals(Double.valueOf(60.00), testFruitList.get(3).getPrice());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateAllRecordWithListOnlyCriteria() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Paul Horowitz", 72, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            Author susan = new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, warehouseOfficeId);
            db.create(susan);

            // Update the age of all parklane authors with list only property
            // (telephone) to 100
            assertEquals(2, db.updateAll(new AuthorQuery().addEquals("officeTelephone", "+2348888888"),
                    new Update().add("age", 100)));

            List<Author> testAuthorList = db.findAll(new AuthorQuery().ignoreEmptyCriteria(true).addOrder("age", "name"));
            assertEquals(3, testAuthorList.size());

            // Susan's age should be first and remain same at 45
            assertEquals("Susan Bramer", testAuthorList.get(0).getName());
            assertEquals(Integer.valueOf(45), testAuthorList.get(0).getAge());
            // The Brian and Paul with ages updated to 100
            assertEquals("Brian Bramer", testAuthorList.get(1).getName());
            assertEquals(Integer.valueOf(100), testAuthorList.get(1).getAge());
            assertEquals("Paul Horowitz", testAuthorList.get(2).getName());
            assertEquals(Integer.valueOf(100), testAuthorList.get(2).getAge());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateAllRecordsWithLimit() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("banana", "yellow", 35.00));
            db.create(new Fruit("orange", "orange", 24.20));
            db.create(new Fruit("mango", "green", 52.00));
            db.create(new Fruit("pineapple", "green", 63.00));
            db.create(new Fruit("peach", "peach", 11.50));
            db.create(new Fruit("pear", "green", 42.50));
            db.create(new Fruit("avocado", "purple", 99.20));
            db.create(new Fruit("grape", "yellow", 4.50));
            db.create(new Fruit("strawberry", "red", 4.50));
            db.updateAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("name").setLimit(9),
                    new Update().add("price", 99.99));
            List<Fruit> testFruitList = db.findAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("id"));
            assertEquals(10, testFruitList.size());
            assertEquals("apple", testFruitList.get(0).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(0).getPrice());
            assertEquals("banana", testFruitList.get(1).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(1).getPrice());
            assertEquals("orange", testFruitList.get(2).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(2).getPrice());
            assertEquals("mango", testFruitList.get(3).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(3).getPrice());
            assertEquals("pineapple", testFruitList.get(4).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(4).getPrice());
            assertEquals("peach", testFruitList.get(5).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(5).getPrice());
            assertEquals("pear", testFruitList.get(6).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(6).getPrice());
            assertEquals("avocado", testFruitList.get(7).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(7).getPrice());
            assertEquals("grape", testFruitList.get(8).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(8).getPrice());
            assertEquals("strawberry", testFruitList.get(9).getName());
            assertEquals(Double.valueOf(4.50), testFruitList.get(9).getPrice());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateAllRecordsWithOffset() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("banana", "yellow", 35.00));
            db.create(new Fruit("orange", "orange", 24.20));
            db.create(new Fruit("mango", "green", 52.00));
            db.create(new Fruit("pineapple", "green", 63.00));
            db.create(new Fruit("peach", "peach", 11.50));
            db.create(new Fruit("pear", "green", 42.50));
            db.create(new Fruit("avocado", "purple", 99.20));
            db.create(new Fruit("grape", "yellow", 4.50));
            db.create(new Fruit("strawberry", "red", 4.50));
            db.updateAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("name").setOffset(3),
                    new Update().add("price", 99.99));
            List<Fruit> testFruitList = db.findAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("id"));
            assertEquals(10, testFruitList.size());
            assertEquals("apple", testFruitList.get(0).getName());
            assertEquals(Double.valueOf(20.00), testFruitList.get(0).getPrice());
            assertEquals("banana", testFruitList.get(1).getName());
            assertEquals(Double.valueOf(35.00), testFruitList.get(1).getPrice());
            assertEquals("orange", testFruitList.get(2).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(2).getPrice());
            assertEquals("mango", testFruitList.get(3).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(3).getPrice());
            assertEquals("pineapple", testFruitList.get(4).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(4).getPrice());
            assertEquals("peach", testFruitList.get(5).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(5).getPrice());
            assertEquals("pear", testFruitList.get(6).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(6).getPrice());
            assertEquals("avocado", testFruitList.get(7).getName());
            assertEquals(Double.valueOf(99.20), testFruitList.get(7).getPrice());
            assertEquals("grape", testFruitList.get(8).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(8).getPrice());
            assertEquals("strawberry", testFruitList.get(9).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(9).getPrice());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateAllRecordsWithOffsetLimit() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("banana", "yellow", 35.00));
            db.create(new Fruit("orange", "orange", 24.20));
            db.create(new Fruit("mango", "green", 52.00));
            db.create(new Fruit("pineapple", "green", 63.00));
            db.create(new Fruit("peach", "peach", 11.50));
            db.create(new Fruit("pear", "green", 42.50));
            db.create(new Fruit("avocado", "purple", 99.20));
            db.create(new Fruit("grape", "yellow", 4.50));
            db.create(new Fruit("strawberry", "red", 4.50));
            db.updateAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("name").setOffset(6).setLimit(3),
                    new Update().add("price", 99.99));
            List<Fruit> testFruitList = db.findAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("id"));
            assertEquals(10, testFruitList.size());
            assertEquals("apple", testFruitList.get(0).getName());
            assertEquals(Double.valueOf(20.00), testFruitList.get(0).getPrice());
            assertEquals("banana", testFruitList.get(1).getName());
            assertEquals(Double.valueOf(35.00), testFruitList.get(1).getPrice());
            assertEquals("orange", testFruitList.get(2).getName());
            assertEquals(Double.valueOf(24.20), testFruitList.get(2).getPrice());
            assertEquals("mango", testFruitList.get(3).getName());
            assertEquals(Double.valueOf(52.00), testFruitList.get(3).getPrice());
            assertEquals("pineapple", testFruitList.get(4).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(4).getPrice());
            assertEquals("peach", testFruitList.get(5).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(5).getPrice());
            assertEquals("pear", testFruitList.get(6).getName());
            assertEquals(Double.valueOf(99.99), testFruitList.get(6).getPrice());
            assertEquals("avocado", testFruitList.get(7).getName());
            assertEquals(Double.valueOf(99.20), testFruitList.get(7).getPrice());
            assertEquals("grape", testFruitList.get(8).getName());
            assertEquals(Double.valueOf(4.50), testFruitList.get(8).getPrice());
            assertEquals("strawberry", testFruitList.get(9).getName());
            assertEquals(Double.valueOf(4.50), testFruitList.get(9).getPrice());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecordWithOnewayTransformation() throws Exception {
        tm.beginTransaction();
        try {
            Date createDt = new Date();
            User user = new User("tiger", "scott", createDt);
            Long id = (Long) db.create(user);
            assertNotNull(id);
            assertEquals(id, user.getId());

            User createdUser = db.find(User.class, id);
            assertEquals("tiger", createdUser.getName());
            assertEquals(user.getName(), createdUser.getName());
            assertNotNull(createdUser.getPassword());
            assertFalse(user.getPassword().equals(createdUser.getPassword()));
            assertFalse(createDt.equals(createdUser.getCreateDt()));
            assertTrue(CalendarUtils.getMidnightDate(createDt).equals(createdUser.getCreateDt()));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordWithOnewayTransformationByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            User user = new User("tiger", "scott");
            db.create(user);

            List<User> userList = db.findAll(new UserQuery().addEquals("password", "scott"));
            assertEquals(1, userList.size());

            User fetchedUser = userList.get(0);
            assertEquals("tiger", fetchedUser.getName());
            assertNotNull(fetchedUser.getPassword());
            assertFalse("scott".equals(fetchedUser.getPassword()));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordWithOnewayTransformationByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            User user = new User("tiger", "scott");
            db.create(user);

            List<User> userList = db.listAll(new UserQuery().addEquals("password", "scott"));
            assertEquals(1, userList.size());

            User fetchedUser = userList.get(0);
            assertEquals("tiger", fetchedUser.getName());
            assertNotNull(fetchedUser.getPassword());
            assertFalse("scott".equals(fetchedUser.getPassword()));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateRecordWithOnewayTransformation() throws Exception {
        tm.beginTransaction();
        try {
            User user = new User("tiger", "scott");
            Long id = (Long) db.create(user);

            User fetchedUser = db.find(User.class, id);
            fetchedUser.setPassword("perry");
            db.updateById(fetchedUser);

            User refetchedUser = db.find(User.class, id);
            assertEquals(fetchedUser.getName(), refetchedUser.getName());
            assertFalse(fetchedUser.getPassword().equals(refetchedUser.getPassword()));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateRecordWithOnewayTransformationByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            User user = new User("tiger", "scott");
            Long id = (Long) db.create(user);
            User fetchedUser = db.find(User.class, id);

            db.updateAll(new UserQuery().addEquals("id", id), new Update().add("password", "tyler"));

            User refetchedUser = db.find(User.class, id);

            assertEquals(fetchedUser.getName(), refetchedUser.getName());
            assertFalse(fetchedUser.getPassword().equals(refetchedUser.getPassword()));
            assertFalse("tyler".equals(refetchedUser.getPassword()));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordWithOnewayTransformationByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            User user1 = new User("tiger", "scott");
            User user2 = new User("arnold", "brown");
            db.create(user1);
            db.create(user2);

            int deleteCount = db.deleteAll(new UserQuery().addEquals("password", "brown"));
            assertEquals(1, deleteCount);

            List<User> userList = db.findAll(new UserQuery().ignoreEmptyCriteria(true));
            assertEquals(1, userList.size());

            User fetchedUser = userList.get(0);
            assertEquals("tiger", fetchedUser.getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecordWithTwowayTransformation() throws Exception {
        tm.beginTransaction();
        try {
            ServerConfig serverConfig = new ServerConfig("alan", "greenspan");
            Long id = (Long) db.create(serverConfig);
            assertNotNull(id);
            assertEquals(id, serverConfig.getId());

            ServerConfig createdServerConfig = db.find(ServerConfig.class, id);
            assertEquals("alan", createdServerConfig.getName());
            assertEquals(serverConfig.getName(), createdServerConfig.getName());
            assertNotNull(createdServerConfig.getPassword());
            assertEquals("greenspan", createdServerConfig.getPassword());
            assertEquals(serverConfig.getPassword(), createdServerConfig.getPassword());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordWithTwowayTransformationByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            ServerConfig serverConfig = new ServerConfig("alan", "greenspan");
            db.create(serverConfig);

            List<ServerConfig> serverConfigList = db.findAll(new ServerConfigQuery().addEquals("password", "greenspan"));
            assertEquals(1, serverConfigList.size());

            ServerConfig fetchedServerConfig = serverConfigList.get(0);
            assertEquals("alan", fetchedServerConfig.getName());
            assertNotNull(fetchedServerConfig.getPassword());
            assertEquals("greenspan", fetchedServerConfig.getPassword());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateRecordWithTwowayTransformation() throws Exception {
        tm.beginTransaction();
        try {
            ServerConfig serverConfig = new ServerConfig("allan", "greenspan");
            Long id = (Long) db.create(serverConfig);

            ServerConfig fetchedServerConfig = db.find(ServerConfig.class, id);
            fetchedServerConfig.setPassword("kerry");
            db.updateById(fetchedServerConfig);

            ServerConfig refetchedServerConfig = db.find(ServerConfig.class, id);
            assertEquals(fetchedServerConfig.getName(), refetchedServerConfig.getName());
            assertEquals(fetchedServerConfig.getPassword(), refetchedServerConfig.getPassword());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateRecordWithTwowayTransformationByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            ServerConfig serverConfig = new ServerConfig("alan", "greenspan");
            Long id = (Long) db.create(serverConfig);
            ServerConfig fetchedServerConfig = db.find(ServerConfig.class, id);

            db.updateAll(new ServerConfigQuery().addEquals("id", id), new Update().add("password", "bluebridge"));

            ServerConfig refetchedServerConfig = db.find(ServerConfig.class, id);

            assertEquals(fetchedServerConfig.getName(), refetchedServerConfig.getName());
            assertFalse(fetchedServerConfig.getPassword().equals(refetchedServerConfig.getPassword()));
            assertEquals("bluebridge", refetchedServerConfig.getPassword());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordWithTwowayTransformationByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            ServerConfig serverConfig1 = new ServerConfig("alan", "greenspan");
            ServerConfig serverConfig2 = new ServerConfig("tom", "jones");
            db.create(serverConfig1);
            db.create(serverConfig2);

            int deleteCount = db.deleteAll(new ServerConfigQuery().addEquals("password", "jones"));
            assertEquals(1, deleteCount);

            List<ServerConfig> serverConfigList = db.findAll(new ServerConfigQuery().ignoreEmptyCriteria(true));
            assertEquals(1, serverConfigList.size());

            ServerConfig fetchedServerConfig = serverConfigList.get(0);
            assertEquals("alan", fetchedServerConfig.getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecordWithNoChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            Long id = (Long) db.create(report);
            assertNotNull(id);
            assertEquals(id, report.getId());

            int count = db.countAll(new ReportParameterQuery().ignoreEmptyCriteria(true));
            assertEquals(0, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecordWithNullChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setParameters(null);
            Long id = (Long) db.create(report);
            assertNotNull(id);
            assertEquals(id, report.getId());

            int count = db.countAll(new ReportParameterQuery().ignoreEmptyCriteria(true));
            assertEquals(0, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecordWithChild() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            ReportForm reportForm = new ReportForm("sampleEditor");
            report.setReportForm(reportForm);

            Long id = (Long) db.create(report);
            assertNotNull(id);
            assertEquals(id, report.getId());
            assertEquals(id, reportForm.getReportId());

            int count = db.countAll(new ReportFormQuery().ignoreEmptyCriteria(true));
            assertEquals(1, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecordWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            ReportParameter rpStart = new ReportParameter("startDate");
            ReportParameter rpEnd = new ReportParameter("endDate");
            report.addParameter(rpStart).addParameter(rpEnd);

            Long id = (Long) db.create(report);
            assertNotNull(id);
            assertEquals(id, report.getId());
            assertEquals(id, rpStart.getReportId());
            assertEquals(id, rpEnd.getReportId());

            int count = db.countAll(new ReportParameterQuery().ignoreEmptyCriteria(true));
            assertEquals(2, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecordWithDeepChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            ReportParameter rpStart = new ReportParameter("startDate");
            ReportParameter rpEnd = new ReportParameter("endDate");
            report.addParameter(rpStart).addParameter(rpEnd);

            ReportParameterOptions rpo10 = new ReportParameterOptions("upperLimit");
            ReportParameterOptions rpo11 = new ReportParameterOptions("lowerLimit");
            rpStart.addOption(rpo10).addOption(rpo11);

            ReportParameterOptions rpo20 = new ReportParameterOptions("title");
            rpEnd.addOption(rpo20);

            Long id = (Long) db.create(report);
            assertNotNull(id);
            assertEquals(id, report.getId());
            assertEquals(id, rpStart.getReportId());
            assertEquals(id, rpEnd.getReportId());

            assertEquals(rpStart.getId(), rpo10.getReportParameterId());
            assertEquals(rpStart.getId(), rpo11.getReportParameterId());
            assertEquals(rpEnd.getId(), rpo20.getReportParameterId());

            int count = db.countAll(new ReportParameterQuery().ignoreEmptyCriteria(true));
            assertEquals(2, count);

            count = db.countAll(new ReportParameterOptionsQuery().reportParameterId(rpStart.getId()));
            assertEquals(2, count);

            count = db.countAll(new ReportParameterOptionsQuery().reportParameterId(rpEnd.getId()));
            assertEquals(1, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateMultipleRecordsWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate", BooleanType.FALSE))
                    .addParameter(new ReportParameter("endDate", BooleanType.TRUE));
            Long id1 = (Long) db.create(report);

            report = new Report("salaryReport", "Salary Report");
            report.addParameter(new ReportParameter("staffNo", BooleanType.FALSE));
            Long id2 = (Long) db.create(report);

            int count = db.countAll(new ReportParameterQuery().reportId(id1));
            assertEquals(2, count);

            count = db.countAll(new ReportParameterQuery().reportId(id2));
            assertEquals(1, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateMultipleRecordsWithDeepChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            ReportParameter rpStart = new ReportParameter("startDate");
            ReportParameter rpEnd = new ReportParameter("endDate");
            report.addParameter(rpStart).addParameter(rpEnd);

            ReportParameterOptions rpo10 = new ReportParameterOptions("upperLimit");
            ReportParameterOptions rpo11 = new ReportParameterOptions("lowerLimit");
            rpStart.addOption(rpo10).addOption(rpo11);
            Long id1 = (Long) db.create(report);

            report = new Report("salaryReport", "Salary Report");
            ReportParameter rpStaff = new ReportParameter("staffNo");
            report.addParameter(rpStaff);
            ReportParameterOptions rpo20 = new ReportParameterOptions("title");
            rpStaff.addOption(rpo20);
            Long id2 = (Long) db.create(report);

            int count = db.countAll(new ReportParameterQuery().reportId(id1));
            assertEquals(2, count);

            count = db.countAll(new ReportParameterQuery().reportId(id2));
            assertEquals(1, count);

            count = db.countAll(new ReportParameterOptionsQuery().reportParameterId(rpStart.getId()));
            assertEquals(2, count);

            count = db.countAll(new ReportParameterOptionsQuery().reportParameterId(rpEnd.getId()));
            assertEquals(0, count);

            count = db.countAll(new ReportParameterOptionsQuery().reportParameterId(rpStaff.getId()));
            assertEquals(1, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindLeanRecordByIdWithChild() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setReportForm(new ReportForm("greenEditor"));
            Long id = (Long) db.create(report);

            Report foundReport = db.findLean(Report.class, id);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNull(foundReport.getReportForm());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindLeanRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate", BooleanType.FALSE))
                    .addParameter(new ReportParameter("endDate", BooleanType.TRUE));
            Long id = (Long) db.create(report);

            Report foundReport = db.findLean(Report.class, id);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNull(foundReport.getParameters());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByIdWithChild() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setReportForm(new ReportForm("greenEditor"));
            Long id = (Long) db.create(report);

            Report foundReport = db.find(Report.class, id);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNotNull(foundReport.getReportForm());
            assertEquals("greenEditor", foundReport.getReportForm().getEditor());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate", BooleanType.FALSE))
                    .addParameter(new ReportParameter("endDate", BooleanType.TRUE));
            Long id = (Long) db.create(report);

            Report foundReport = db.find(Report.class, id);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());

            ReportParameter rParam = foundReport.getParameters().get(0);
            assertEquals("startDate", rParam.getName());
            assertEquals(BooleanType.FALSE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());

            rParam = foundReport.getParameters().get(1);
            assertEquals("endDate", rParam.getName());
            assertEquals(BooleanType.TRUE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindLeanRecordByCriteriaWithChild() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setReportForm(new ReportForm("beanEditor"));
            Long id = (Long) db.create(report);

            Report foundReport = db.findLean(new ReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNull(foundReport.getReportForm());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindLeanRecordByCriteriaWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate", BooleanType.FALSE))
                    .addParameter(new ReportParameter("endDate", BooleanType.TRUE));
            Long id = (Long) db.create(report);

            Report foundReport = db.findLean(new ReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNull(foundReport.getParameters());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByCriteriaWithChild() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setReportForm(new ReportForm("beanEditor"));
            Long id = (Long) db.create(report);

            Report foundReport = db.find(new ReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNotNull(foundReport.getReportForm());
            assertEquals("beanEditor", foundReport.getReportForm().getEditor());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByCriteriaWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate", BooleanType.FALSE))
                    .addParameter(new ReportParameter("endDate", BooleanType.TRUE));
            Long id = (Long) db.create(report);

            Report foundReport = db.find(new ReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());

            ReportParameter rParam = foundReport.getParameters().get(0);
            assertEquals("startDate", rParam.getName());
            assertEquals(BooleanType.FALSE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());

            rParam = foundReport.getParameters().get(1);
            assertEquals("endDate", rParam.getName());
            assertEquals(BooleanType.TRUE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByCriteriaWithSelectChild() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setReportForm(new ReportForm("beanEditor"));
            Long id = (Long) db.create(report);

            Report foundReport = db.find(new ReportQuery().addEquals("id", id).addSelect("name", "reportForm"));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertNull(foundReport.getDescription());

            assertNotNull(foundReport.getReportForm());
            assertEquals("beanEditor", foundReport.getReportForm().getEditor());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByCriteriaWithSelectChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate", BooleanType.FALSE))
                    .addParameter(new ReportParameter("endDate", BooleanType.TRUE));
            Long id = (Long) db.create(report);

            Report foundReport = db.find(new ReportQuery().addEquals("id", id).addSelect("name", "parameters"));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertNull(foundReport.getDescription());

            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());

            ReportParameter rParam = foundReport.getParameters().get(0);
            assertEquals("startDate", rParam.getName());
            assertEquals(BooleanType.FALSE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());

            rParam = foundReport.getParameters().get(1);
            assertEquals("endDate", rParam.getName());
            assertEquals(BooleanType.TRUE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByIdWithDeepChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            ReportParameter rpStart = new ReportParameter("startDate", BooleanType.FALSE);
            ReportParameter rpEnd = new ReportParameter("endDate", BooleanType.TRUE);
            report.addParameter(rpStart).addParameter(rpEnd);
            ReportParameterOptions rpo10 = new ReportParameterOptions("upperLimit");
            ReportParameterOptions rpo11 = new ReportParameterOptions("lowerLimit");
            rpStart.addOption(rpo10).addOption(rpo11);

            ReportParameterOptions rpo20 = new ReportParameterOptions("title");
            rpEnd.addOption(rpo20);
            Long id = (Long) db.create(report);

            Report foundReport = db.find(Report.class, id);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());

            ReportParameter rParam = foundReport.getParameters().get(0);
            assertEquals("startDate", rParam.getName());
            assertEquals(BooleanType.FALSE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());
            List<ReportParameterOptions> rOptions = rParam.getOptions();
            assertNotNull(rOptions);
            assertEquals(2, rOptions.size());
            ReportParameterOptions rpo = rOptions.get(0);
            assertNotNull(rpo);
            assertEquals("upperLimit", rpo.getName());
            rpo = rOptions.get(1);
            assertNotNull(rpo);
            assertEquals("lowerLimit", rpo.getName());

            rParam = foundReport.getParameters().get(1);
            assertEquals("endDate", rParam.getName());
            assertEquals(BooleanType.TRUE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());
            rOptions = rParam.getOptions();
            assertNotNull(rOptions);
            assertEquals(1, rOptions.size());
            rpo = rOptions.get(0);
            assertNotNull(rpo);
            assertEquals("title", rpo.getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByCriteriaWithDeepChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            ReportParameter rpStart = new ReportParameter("startDate", BooleanType.FALSE);
            ReportParameter rpEnd = new ReportParameter("endDate", BooleanType.TRUE);
            report.addParameter(rpStart).addParameter(rpEnd);
            ReportParameterOptions rpo10 = new ReportParameterOptions("upperLimit");
            ReportParameterOptions rpo11 = new ReportParameterOptions("lowerLimit");
            rpStart.addOption(rpo10).addOption(rpo11);

            ReportParameterOptions rpo20 = new ReportParameterOptions("title");
            rpEnd.addOption(rpo20);
            Long id = (Long) db.create(report);

            Report foundReport = db.find(new ReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());

            ReportParameter rParam = foundReport.getParameters().get(0);
            assertEquals("startDate", rParam.getName());
            assertEquals(BooleanType.FALSE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());
            List<ReportParameterOptions> rOptions = rParam.getOptions();
            assertNotNull(rOptions);
            assertEquals(2, rOptions.size());
            ReportParameterOptions rpo = rOptions.get(0);
            assertNotNull(rpo);
            assertEquals("upperLimit", rpo.getName());
            rpo = rOptions.get(1);
            assertNotNull(rpo);
            assertEquals("lowerLimit", rpo.getName());

            rParam = foundReport.getParameters().get(1);
            assertEquals("endDate", rParam.getName());
            assertEquals(BooleanType.TRUE, rParam.getScheduled());
            assertNull(rParam.getReportDesc());
            assertNull(rParam.getScheduledDesc());
            rOptions = rParam.getOptions();
            assertNotNull(rOptions);
            assertEquals(1, rOptions.size());
            rpo = rOptions.get(0);
            assertNotNull(rpo);
            assertEquals("title", rpo.getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsWithChild() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setReportForm(new ReportForm("editor10"));
            db.create(report);

            List<Report> list = db.findAll(new ReportQuery().ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals("weeklyReport", list.get(0).getName());
            assertEquals("Weekly Report", list.get(0).getDescription());

            assertNull(list.get(0).getReportForm());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate")).addParameter(new ReportParameter("endDate"));
            db.create(report);

            List<Report> list = db.findAll(new ReportQuery().ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals("weeklyReport", list.get(0).getName());
            assertEquals("Weekly Report", list.get(0).getDescription());

            assertNull(list.get(0).getParameters());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsWithDeepChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            ReportParameter rpStart = new ReportParameter("startDate", BooleanType.FALSE);
            ReportParameterOptions rpo10 = new ReportParameterOptions("upperLimit");
            ReportParameterOptions rpo11 = new ReportParameterOptions("lowerLimit");
            rpStart.addOption(rpo10).addOption(rpo11);

            report.addParameter(rpStart).addParameter(new ReportParameter("endDate"));
            db.create(report);

            List<Report> list = db.findAll(new ReportQuery().ignoreEmptyCriteria(true));

            assertNotNull(list);
            assertEquals("weeklyReport", list.get(0).getName());
            assertEquals("Weekly Report", list.get(0).getDescription());

            assertNull(list.get(0).getParameters());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListLeanRecordByIdWithChild() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setReportForm(new ReportForm("cyanEditor"));
            Long id = (Long) db.create(report);

            Report foundReport = db.listLean(Report.class, id);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNull(foundReport.getReportForm());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListLeanRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate")).addParameter(new ReportParameter("endDate"));
            Long id = (Long) db.create(report);

            Report foundReport = db.listLean(Report.class, id);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNull(foundReport.getParameters());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByIdWithChild() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setReportForm(new ReportForm("cyanEditor"));
            Long id = (Long) db.create(report);

            Report foundReport = db.list(Report.class, id);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNotNull(foundReport.getReportForm());
            assertEquals("cyanEditor", foundReport.getReportForm().getEditor());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate")).addParameter(new ReportParameter("endDate"));
            Long id = (Long) db.create(report);

            Report foundReport = db.list(Report.class, id);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());
            assertEquals("startDate", foundReport.getParameters().get(0).getName());
            assertEquals("endDate", foundReport.getParameters().get(1).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListLeanRecordByCriteriaWithChild() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setReportForm(new ReportForm("grayEditor"));
            Long id = (Long) db.create(report);

            Report foundReport = db.listLean(new ReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNull(foundReport.getReportForm());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListLeanRecordByCriteriaWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate")).addParameter(new ReportParameter("endDate"));
            Long id = (Long) db.create(report);

            Report foundReport = db.listLean(new ReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNull(foundReport.getParameters());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByCriteriaWithChild() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setReportForm(new ReportForm("grayEditor"));
            Long id = (Long) db.create(report);

            Report foundReport = db.list(new ReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNotNull(foundReport.getReportForm());
            assertEquals("grayEditor", foundReport.getReportForm().getEditor());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByCriteriaWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate")).addParameter(new ReportParameter("endDate"));
            Long id = (Long) db.create(report);

            Report foundReport = db.list(new ReportQuery().addEquals("id", id));
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());
            assertEquals("startDate", foundReport.getParameters().get(0).getName());
            assertEquals("endDate", foundReport.getParameters().get(1).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByCriteriaWithSelectChild() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setReportForm(new ReportForm("grayEditor"));
            Long id = (Long) db.create(report);

            Report foundReport = db.list(new ReportQuery().addEquals("id", id).addSelect("description", "reportForm"));
            assertNotNull(foundReport);
            assertNull(foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNotNull(foundReport.getReportForm());
            assertEquals("grayEditor", foundReport.getReportForm().getEditor());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByCriteriaWithSelectChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate")).addParameter(new ReportParameter("endDate"));
            Long id = (Long) db.create(report);

            Report foundReport = db.list(new ReportQuery().addEquals("id", id).addSelect("parameters"));
            assertNotNull(foundReport);
            assertNull(foundReport.getName());
            assertNull(foundReport.getDescription());

            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());
            assertEquals("startDate", foundReport.getParameters().get(0).getName());
            assertEquals("endDate", foundReport.getParameters().get(1).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByIdWithDeepChildList() throws Exception {
        tm.beginTransaction();
        try {

            Report report = new Report("weeklyReport", "Weekly Report");
            ReportParameter rpStart = new ReportParameter("startDate", BooleanType.FALSE);
            ReportParameter rpEnd = new ReportParameter("endDate", BooleanType.TRUE);
            report.addParameter(rpStart).addParameter(rpEnd);
            ReportParameterOptions rpo10 = new ReportParameterOptions("upperLimit");
            ReportParameterOptions rpo11 = new ReportParameterOptions("lowerLimit");
            rpStart.addOption(rpo10).addOption(rpo11);

            ReportParameterOptions rpo20 = new ReportParameterOptions("title");
            rpEnd.addOption(rpo20);
            Long id = (Long) db.create(report);

            Report foundReport = db.list(Report.class, id);

            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());

            ReportParameter rParam = foundReport.getParameters().get(0);
            assertEquals("startDate", rParam.getName());
            assertEquals(BooleanType.FALSE, rParam.getScheduled());
            assertEquals("Weekly Report", rParam.getReportDesc());
            assertEquals("False", rParam.getScheduledDesc());
            List<ReportParameterOptions> rOptions = rParam.getOptions();
            assertNotNull(rOptions);
            assertEquals(2, rOptions.size());
            ReportParameterOptions rpo = rOptions.get(0);
            assertNotNull(rpo);
            assertEquals("upperLimit", rpo.getName());
            rpo = rOptions.get(1);
            assertNotNull(rpo);
            assertEquals("lowerLimit", rpo.getName());

            rParam = foundReport.getParameters().get(1);
            assertEquals("endDate", rParam.getName());
            assertEquals(BooleanType.TRUE, rParam.getScheduled());
            assertEquals("Weekly Report", rParam.getReportDesc());
            assertEquals("True", rParam.getScheduledDesc());
            rOptions = rParam.getOptions();
            assertNotNull(rOptions);
            assertEquals(1, rOptions.size());
            rpo = rOptions.get(0);
            assertNotNull(rpo);
            assertEquals("title", rpo.getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByCriteriaWithDeepChildList() throws Exception {
        tm.beginTransaction();
        try {

            Report report = new Report("weeklyReport", "Weekly Report");
            ReportParameter rpStart = new ReportParameter("startDate", BooleanType.FALSE);
            ReportParameter rpEnd = new ReportParameter("endDate", BooleanType.TRUE);
            report.addParameter(rpStart).addParameter(rpEnd);
            ReportParameterOptions rpo10 = new ReportParameterOptions("upperLimit");
            ReportParameterOptions rpo11 = new ReportParameterOptions("lowerLimit");
            rpStart.addOption(rpo10).addOption(rpo11);

            ReportParameterOptions rpo20 = new ReportParameterOptions("title");
            rpEnd.addOption(rpo20);
            Long id = (Long) db.create(report);

            Report foundReport = db.list(new ReportQuery().addEquals("id", id));

            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());

            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());

            ReportParameter rParam = foundReport.getParameters().get(0);
            assertEquals("startDate", rParam.getName());
            assertEquals(BooleanType.FALSE, rParam.getScheduled());
            assertEquals("Weekly Report", rParam.getReportDesc());
            assertEquals("False", rParam.getScheduledDesc());
            List<ReportParameterOptions> rOptions = rParam.getOptions();
            assertNotNull(rOptions);
            assertEquals(2, rOptions.size());
            ReportParameterOptions rpo = rOptions.get(0);
            assertNotNull(rpo);
            assertEquals("upperLimit", rpo.getName());
            rpo = rOptions.get(1);
            assertNotNull(rpo);
            assertEquals("lowerLimit", rpo.getName());

            rParam = foundReport.getParameters().get(1);
            assertEquals("endDate", rParam.getName());
            assertEquals(BooleanType.TRUE, rParam.getScheduled());
            assertEquals("Weekly Report", rParam.getReportDesc());
            assertEquals("True", rParam.getScheduledDesc());
            rOptions = rParam.getOptions();
            assertNotNull(rOptions);
            assertEquals(1, rOptions.size());
            rpo = rOptions.get(0);
            assertNotNull(rpo);
            assertEquals("title", rpo.getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecordsWithChild() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setReportForm(new ReportForm("blueEditor"));
            db.create(report);

            List<Report> list = db.listAll(new ReportQuery().ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals("weeklyReport", list.get(0).getName());
            assertEquals("Weekly Report", list.get(0).getDescription());

            assertNull(list.get(0).getReportForm());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecordsWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate")).addParameter(new ReportParameter("endDate"));
            db.create(report);

            List<Report> list = db.listAll(new ReportQuery().ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals("weeklyReport", list.get(0).getName());
            assertEquals("Weekly Report", list.get(0).getDescription());

            assertNull(list.get(0).getParameters());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecordsWithDeepChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            ReportParameter rpStart = new ReportParameter("startDate", BooleanType.FALSE);
            ReportParameterOptions rpo10 = new ReportParameterOptions("upperLimit");
            ReportParameterOptions rpo11 = new ReportParameterOptions("lowerLimit");
            rpStart.addOption(rpo10).addOption(rpo11);
            report.addParameter(rpStart).addParameter(new ReportParameter("endDate"));
            db.create(report);

            List<Report> list = db.listAll(new ReportQuery().ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals("weeklyReport", list.get(0).getName());
            assertEquals("Weekly Report", list.get(0).getDescription());

            assertNull(list.get(0).getParameters());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateSingleFromMultipleRecordsWithDeepChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            ReportParameter rpStart = new ReportParameter("startDate");
            ReportParameter rpEnd = new ReportParameter("endDate");
            report.addParameter(rpStart).addParameter(rpEnd);

            ReportParameterOptions rpo10 = new ReportParameterOptions("upperLimit");
            ReportParameterOptions rpo11 = new ReportParameterOptions("lowerLimit");
            rpStart.addOption(rpo10).addOption(rpo11);
            Long id1 = (Long) db.create(report);

            report = new Report("salaryReport", "Salary Report");
            ReportParameter rpStaff = new ReportParameter("staffNo");
            report.addParameter(rpStaff);
            ReportParameterOptions rpo20 = new ReportParameterOptions("title");
            rpStaff.addOption(rpo20);
            Long id2 = (Long) db.create(report);

            Report foundReport = db.find(Report.class, id2);
            foundReport.setDescription("New Salary Report");
            foundReport.setParameters(null);
            foundReport.addParameter(new ReportParameter("Age").addOption(new ReportParameterOptions("One"))
                    .addOption(new ReportParameterOptions("Two")));
            foundReport.addParameter(new ReportParameter("Height").addOption(new ReportParameterOptions("Three")));

            db.updateById(foundReport);

            // This should be unchanged
            foundReport = db.find(Report.class, id1);
            assertNotNull(foundReport);
            assertEquals("weeklyReport", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());
            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());
            ReportParameter rParam = foundReport.getParameters().get(0);
            assertEquals("startDate", rParam.getName());
            List<ReportParameterOptions> rOptions = rParam.getOptions();
            assertNotNull(rOptions);
            assertEquals(2, rOptions.size());
            ReportParameterOptions rpo = rOptions.get(0);
            assertNotNull(rpo);
            assertEquals("upperLimit", rpo.getName());
            rpo = rOptions.get(1);
            assertNotNull(rpo);
            assertEquals("lowerLimit", rpo.getName());

            rParam = foundReport.getParameters().get(1);
            assertEquals("endDate", rParam.getName());
            rOptions = rParam.getOptions();
            assertNotNull(rOptions);
            assertEquals(0, rOptions.size());

            // This should be updated
            foundReport = db.find(Report.class, id2);
            assertNotNull(foundReport);
            assertEquals("salaryReport", foundReport.getName());
            assertEquals("New Salary Report", foundReport.getDescription());
            assertNotNull(foundReport.getParameters());
            assertEquals(2, foundReport.getParameters().size());
            rParam = foundReport.getParameters().get(0);
            assertEquals("Age", rParam.getName());
            rOptions = rParam.getOptions();
            assertNotNull(rOptions);
            assertEquals(2, rOptions.size());
            rpo = rOptions.get(0);
            assertNotNull(rpo);
            assertEquals("One", rpo.getName());
            rpo = rOptions.get(1);
            assertNotNull(rpo);
            assertEquals("Two", rpo.getName());

            rParam = foundReport.getParameters().get(1);
            assertEquals("Height", rParam.getName());
            rOptions = rParam.getOptions();
            assertNotNull(rOptions);
            assertEquals(1, rOptions.size());
            rpo = rOptions.get(0);
            assertNotNull(rpo);
            assertEquals("Three", rpo.getName());

        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdWithNoChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setParameters(new ArrayList<ReportParameter>());
            Long id = (Long) db.create(report);
            db.delete(Report.class, id);
            assertEquals(0, db.countAll(new ReportQuery().addEquals("id", id)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdWithNullChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setParameters(null);
            Long id = (Long) db.create(report);
            db.delete(Report.class, id);
            assertEquals(0, db.countAll(new ReportQuery().addEquals("id", id)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdWithChild() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setReportForm(new ReportForm("sampleEditor"));
            Long id = (Long) db.create(report);
            db.delete(Report.class, id);
            assertEquals(0, db.countAll(new ReportQuery().addEquals("id", id)));
            assertEquals(0, db.countAll(new ReportFormQuery().ignoreEmptyCriteria(true)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate")).addParameter(new ReportParameter("endDate"));
            Long id = (Long) db.create(report);
            db.delete(Report.class, id);
            assertEquals(0, db.countAll(new ReportQuery().addEquals("id", id)));
            assertEquals(0, db.countAll(new ReportParameterQuery().ignoreEmptyCriteria(true)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdWithDeepChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            ReportParameter rpStart = new ReportParameter("startDate", BooleanType.FALSE);
            ReportParameterOptions rpo10 = new ReportParameterOptions("upperLimit");
            ReportParameterOptions rpo11 = new ReportParameterOptions("lowerLimit");
            rpStart.addOption(rpo10).addOption(rpo11);
            report.addParameter(rpStart).addParameter(new ReportParameter("endDate"));
            Long id = (Long) db.create(report);

            db.delete(Report.class, id);

            assertEquals(0, db.countAll(new ReportQuery().addEquals("id", id)));
            assertEquals(0, db.countAll(new ReportParameterQuery().ignoreEmptyCriteria(true)));
            assertEquals(0, db.countAll(new ReportParameterOptionsQuery().ignoreEmptyCriteria(true)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdVersionWithChild() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setReportForm(new ReportForm("sampleEditor"));
            Long id = (Long) db.create(report);
            report = db.find(Report.class, id);
            db.deleteByIdVersion(report);
            assertEquals(0, db.countAll(new ReportQuery().addEquals("id", id)));
            assertEquals(0, db.countAll(new ReportFormQuery().ignoreEmptyCriteria(true)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdVersionWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate"));
            report.addParameter(new ReportParameter("endDate"));
            Long id = (Long) db.create(report);
            report = db.find(Report.class, id);
            db.deleteByIdVersion(report);
            assertEquals(0, db.countAll(new ReportQuery().addEquals("id", id)));
            assertEquals(0, db.countAll(new ReportParameterQuery().ignoreEmptyCriteria(true)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteAllRecordWithChild() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            ReportForm reportForm = new ReportForm("sampleEditor");
            report.setReportForm(reportForm);
            db.create(report);
            db.deleteAll(new ReportQuery().ignoreEmptyCriteria(true));

            int count = db.countAll(new ReportQuery().ignoreEmptyCriteria(true));
            assertEquals(0, count);

            count = db.countAll(new ReportFormQuery().ignoreEmptyCriteria(true));
            assertEquals(0, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteAllRecordWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate"));
            report.addParameter(new ReportParameter("endDate"));
            db.create(report);
            db.deleteAll(new ReportQuery().ignoreEmptyCriteria(true));

            int count = db.countAll(new ReportQuery().ignoreEmptyCriteria(true));
            assertEquals(0, count);

            count = db.countAll(new ReportParameterQuery().ignoreEmptyCriteria(true));
            assertEquals(0, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteAllRecordWithDeepChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            ReportParameter rpStart = new ReportParameter("startDate", BooleanType.FALSE);
            ReportParameter rpEnd = new ReportParameter("endDate", BooleanType.TRUE);
            report.addParameter(rpStart).addParameter(rpEnd);
            ReportParameterOptions rpo10 = new ReportParameterOptions("upperLimit");
            ReportParameterOptions rpo11 = new ReportParameterOptions("lowerLimit");
            rpStart.addOption(rpo10).addOption(rpo11);

            ReportParameterOptions rpo20 = new ReportParameterOptions("title");
            rpEnd.addOption(rpo20);
            db.create(report);

            db.deleteAll(new ReportQuery().ignoreEmptyCriteria(true));

            assertEquals(0, db.countAll(new ReportQuery().ignoreEmptyCriteria(true)));
            assertEquals(0, db.countAll(new ReportParameterQuery().ignoreEmptyCriteria(true)));
            assertEquals(0, db.countAll(new ReportParameterOptionsQuery().ignoreEmptyCriteria(true)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteSingleFromMultipleRecordsWithChild() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setReportForm(new ReportForm("editor1"));
            Long id1 = (Long) db.create(report);

            report = new Report("salaryReport", "Salary Report");
            report.setReportForm(new ReportForm("editor2"));
            Long id2 = (Long) db.create(report);

            db.deleteAll(new ReportQuery().addEquals("id", id1));

            int count = db.countAll(new ReportQuery().ignoreEmptyCriteria(true));
            assertEquals(1, count);

            report = db.find(Report.class, id2);
            assertNotNull(report);
            assertEquals("salaryReport", report.getName());
            assertEquals("Salary Report", report.getDescription());
            assertNotNull(report.getReportForm());
            assertEquals("editor2", report.getReportForm().getEditor());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteSingleFromMultipleRecordsWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.addParameter(new ReportParameter("startDate"));
            report.addParameter(new ReportParameter("endDate"));
            Long id1 = (Long) db.create(report);

            report = new Report("salaryReport", "Salary Report");
            report.addParameter(new ReportParameter("staffNo"));
            Long id2 = (Long) db.create(report);

            db.deleteAll(new ReportQuery().addEquals("id", id1));

            int count = db.countAll(new ReportQuery().ignoreEmptyCriteria(true));
            assertEquals(1, count);

            report = db.find(Report.class, id2);
            assertNotNull(report);
            assertEquals("salaryReport", report.getName());
            assertEquals("Salary Report", report.getDescription());
            assertEquals(1, report.getParameters().size());
            assertEquals("staffNo", report.getParameters().get(0).getName());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteSingleFromMultipleRecordsWithDeepChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            ReportParameter rpStart = new ReportParameter("startDate");
            ReportParameter rpEnd = new ReportParameter("endDate");
            report.addParameter(rpStart).addParameter(rpEnd);

            ReportParameterOptions rpo10 = new ReportParameterOptions("upperLimit");
            ReportParameterOptions rpo11 = new ReportParameterOptions("lowerLimit");
            rpStart.addOption(rpo10).addOption(rpo11);
            Long id1 = (Long) db.create(report);

            report = new Report("salaryReport", "Salary Report");
            ReportParameter rpStaff = new ReportParameter("staffNo");
            report.addParameter(rpStaff);
            ReportParameterOptions rpo20 = new ReportParameterOptions("title");
            rpStaff.addOption(rpo20);
            Long id2 = (Long) db.create(report);

            db.deleteAll(new ReportQuery().addEquals("id", id1));

            int count = db.countAll(new ReportQuery().ignoreEmptyCriteria(true));
            assertEquals(1, count);

            report = db.find(Report.class, id2);
            assertNotNull(report);
            assertEquals("salaryReport", report.getName());
            assertEquals("Salary Report", report.getDescription());
            assertEquals(1, report.getParameters().size());
            assertEquals("staffNo", report.getParameters().get(0).getName());

            List<ReportParameterOptions> rOptions = report.getParameters().get(0).getOptions();
            assertNotNull(rOptions);
            assertEquals(1, rOptions.size());
            ReportParameterOptions rpo = rOptions.get(0);
            assertNotNull(rpo);
            assertEquals("title", rpo.getName());

        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testValue() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00, 25));
            db.create(new Fruit("pineapple", "cyan", 60.00, 3));
            db.create(new Fruit("banana", "yellow", 45.00, 45));
            db.create(new Fruit("orange", "orange", 15.00, 11));

            String color = db.value(String.class, "color", new FruitQuery().addEquals("name", "apple"));
            assertEquals("red", color);

            Double price = db.value(Double.class, "price", new FruitQuery().addEquals("name", "banana"));
            assertEquals(Double.valueOf(45.00), price);
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testListValueMultipleResult() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00, 25));
            db.create(new Fruit("pineapple", "cyan", 60.00, 3));
            db.create(new Fruit("banana", "yellow", 45.00, 45));
            db.create(new Fruit("orange", "orange", 15.00, 11));

            db.value(String.class, "color", new FruitQuery().addLike("name", "apple"));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testValueList() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00, 25));
            db.create(new Fruit("pineapple", "cyan", 60.00, 3));
            db.create(new Fruit("banana", "yellow", 45.00, 45));
            db.create(new Fruit("orange", "orange", 15.00, 11));

            List<String> colorList = db.valueList(String.class, "color", new FruitQuery().addLike("name", "apple"));
            assertEquals(2, colorList.size());
            assertTrue(colorList.contains("red"));
            assertTrue(colorList.contains("cyan"));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testValueSet() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00, 25));
            db.create(new Fruit("pineapple", "cyan", 60.00, 3));
            db.create(new Fruit("banana", "yellow", 45.00, 45));
            db.create(new Fruit("orange", "orange", 15.00, 11));

            Set<String> nameSet =
                    db.valueSet(String.class, "name", new FruitQuery().addGreaterThan("price", Double.valueOf(15.00)));
            assertEquals(3, nameSet.size());
            assertTrue(nameSet.contains("apple"));
            assertTrue(nameSet.contains("pineapple"));
            assertTrue(nameSet.contains("banana"));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testValueMap() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00, 25));
            db.create(new Fruit("pineapple", "cyan", 60.00, 3));
            db.create(new Fruit("banana", "yellow", 45.00, 45));
            db.create(new Fruit("orange", "orange", 15.00, 11));

            Map<String, Double> priceByFruit = db.valueMap(String.class, "name", Double.class, "price",
                    new FruitQuery().addGreaterThan("price", Double.valueOf(15.00)));
            assertEquals(3, priceByFruit.size());
            assertEquals(Double.valueOf(20.00), priceByFruit.get("apple"));
            assertEquals(Double.valueOf(60.00), priceByFruit.get("pineapple"));
            assertEquals(Double.valueOf(45.00), priceByFruit.get("banana"));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testValueListMap() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00, 25));
            db.create(new Fruit("pineapple", "red", 60.00, 3));
            db.create(new Fruit("banana", "yellow", 45.00, 45));
            db.create(new Fruit("orange", "red", 15.00, 11));

            Map<String, List<String>> fruitsByColor = db.valueListMap(String.class, "color", String.class, "name",
                    new FruitQuery().addOrder("name").ignoreEmptyCriteria(true));
            assertNotNull(fruitsByColor);
            assertEquals(2, fruitsByColor.size());

            List<String> redFruits = fruitsByColor.get("red");
            assertNotNull(redFruits);
            assertEquals(3, redFruits.size());
            assertTrue(redFruits.contains("apple"));
            assertTrue(redFruits.contains("pineapple"));
            assertTrue(redFruits.contains("orange"));

            List<String> yellowFruits = fruitsByColor.get("yellow");
            assertNotNull(yellowFruits);
            assertEquals(1, yellowFruits.size());
            assertTrue(yellowFruits.contains("banana"));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsMin() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 20.00);
            db.create(apple);
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("orange", "orange", 25.00));
            Fruit starApple = new Fruit("starapple", "purple", 20.00);
            db.create(starApple);
            List<Fruit> testFruitList = db.findAll(new FruitQuery().addLike("name", "apple").setMin("price").addOrder("name"));
            assertEquals(2, testFruitList.size());
            assertEquals(apple, testFruitList.get(0));
            assertEquals(starApple, testFruitList.get(1));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsMinNoCriteria() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("pineapple", "cyan", 60.00));
            Fruit banana = new Fruit("banana", "yellow", 15.00);
            db.create(banana);
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("orange", "orange", 25.00));
            db.create(new Fruit("starapple", "purple", 20.00));
            List<Fruit> testFruitList = db.findAll(new FruitQuery().setMin("price"));
            assertEquals(1, testFruitList.size());
            assertEquals(banana, testFruitList.get(0));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecordsMin() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Winfield Hill", 75, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));

            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.TRUE, warehouseOfficeId));

            // Pick authors with age less or equals 50 and order by name
            List<Author> testAuthorList =
                    db.listAll(new AuthorQuery().addLessThanEqual("age", 50).setMin("officeSize").addOrder("name"));
            assertEquals(1, testAuthorList.size());

            // Should pick the Brian brammer because office has smaller size
            Author foundAuthor = testAuthorList.get(0);
            assertEquals("Brian Bramer", foundAuthor.getName());
            assertEquals(Integer.valueOf(50), foundAuthor.getAge());
            assertEquals(Gender.MALE, foundAuthor.getGender());
            assertEquals(BooleanType.FALSE, foundAuthor.getRetired());
            assertEquals("False", foundAuthor.getRetiredDesc());
            assertEquals(parklaneOfficeId, foundAuthor.getOfficeId());
            assertEquals("24, Parklane Apapa", foundAuthor.getOfficeAddress());
            assertEquals("+2348888888", foundAuthor.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecordsMinNoCriteria() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Winfield Hill", 75, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));

            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.TRUE, warehouseOfficeId));

            // Pick authors with smallest office size
            List<Author> testAuthorList = db.listAll(new AuthorQuery().setMin("officeSize").addOrder("name"));
            assertEquals(2, testAuthorList.size());

            // Should pick the authors in parklane
            Author foundAuthor = testAuthorList.get(0);
            assertEquals("Brian Bramer", foundAuthor.getName());
            assertEquals(Integer.valueOf(50), foundAuthor.getAge());
            assertEquals(Gender.MALE, foundAuthor.getGender());
            assertEquals(BooleanType.FALSE, foundAuthor.getRetired());
            assertEquals("False", foundAuthor.getRetiredDesc());
            assertEquals(parklaneOfficeId, foundAuthor.getOfficeId());
            assertEquals("24, Parklane Apapa", foundAuthor.getOfficeAddress());
            assertEquals("+2348888888", foundAuthor.getOfficeTelephone());

            foundAuthor = testAuthorList.get(1);
            assertEquals("Winfield Hill", foundAuthor.getName());
            assertEquals(Integer.valueOf(75), foundAuthor.getAge());
            assertEquals(Gender.MALE, foundAuthor.getGender());
            assertEquals(BooleanType.FALSE, foundAuthor.getRetired());
            assertEquals("False", foundAuthor.getRetiredDesc());
            assertEquals(parklaneOfficeId, foundAuthor.getOfficeId());
            assertEquals("24, Parklane Apapa", foundAuthor.getOfficeAddress());
            assertEquals("+2348888888", foundAuthor.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsMax() throws Exception {
        tm.beginTransaction();
        try {
            Fruit apple = new Fruit("apple", "red", 75.00);
            db.create(apple);
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("orange", "orange", 25.00));
            Fruit starApple = new Fruit("starapple", "purple", 20.00);
            db.create(starApple);
            List<Fruit> testFruitList = db.findAll(new FruitQuery().addLike("name", "apple").setMax("price").addOrder("name"));
            assertEquals(1, testFruitList.size());
            assertEquals(apple, testFruitList.get(0));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsMaxNoCriteria() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("pineapple", "cyan", 60.00));
            Fruit banana = new Fruit("banana", "yellow", 60.20);
            db.create(banana);
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("orange", "orange", 25.00));
            Fruit starApple = new Fruit("starapple", "purple", 60.20);
            db.create(starApple);
            List<Fruit> testFruitList = db.findAll(new FruitQuery().setMax("price").addOrder("name"));
            assertEquals(2, testFruitList.size());
            assertEquals(banana, testFruitList.get(0));
            assertEquals(starApple, testFruitList.get(1));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecordsMax() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Winfield Hill", 75, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));

            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.TRUE, warehouseOfficeId));

            // Pick authors with age less or equals 50 and order by name
            List<Author> testAuthorList =
                    db.listAll(new AuthorQuery().addEquals("gender", Gender.MALE).setMax("age").addOrder("name"));
            assertEquals(1, testAuthorList.size());

            // Should pick the Winfield Hill because is male with max age
            Author foundAuthor = testAuthorList.get(0);
            assertEquals("Winfield Hill", foundAuthor.getName());
            assertEquals(Integer.valueOf(75), foundAuthor.getAge());
            assertEquals(Gender.MALE, foundAuthor.getGender());
            assertEquals(BooleanType.FALSE, foundAuthor.getRetired());
            assertEquals("False", foundAuthor.getRetiredDesc());
            assertEquals(parklaneOfficeId, foundAuthor.getOfficeId());
            assertEquals("24, Parklane Apapa", foundAuthor.getOfficeAddress());
            assertEquals("+2348888888", foundAuthor.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecordsMaxNoCriteria() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Winfield Hill", 75, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));

            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.TRUE, warehouseOfficeId));

            // Pick authors with largest office size
            List<Author> testAuthorList = db.listAll(new AuthorQuery().setMax("officeSize").addOrder("name"));
            assertEquals(1, testAuthorList.size());

            // Should pick the authors in warehouse
            Author foundAuthor = testAuthorList.get(0);
            assertEquals("Susan Bramer", foundAuthor.getName());
            assertEquals(Integer.valueOf(45), foundAuthor.getAge());
            assertEquals(Gender.FEMALE, foundAuthor.getGender());
            assertEquals(BooleanType.TRUE, foundAuthor.getRetired());
            assertEquals("True", foundAuthor.getRetiredDesc());
            assertEquals(warehouseOfficeId, foundAuthor.getOfficeId());
            assertEquals("38, Warehouse Road Apapa", foundAuthor.getOfficeAddress());
            assertEquals("+2345555555", foundAuthor.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Override
    protected void doAddSettingsAndDependencies() throws Exception {
        addContainerSetting(UnifyCorePropertyConstants.APPLICATION_QUERY_LIMIT, 8);
    }

    @Override
    protected void onSetup() throws Exception {
        tm = (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);
        db = (Database) getComponent(ApplicationComponents.APPLICATION_DATABASE);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    protected void onTearDown() throws Exception {
        deleteAll(User.class, ServerConfig.class, Author.class, Office.class, Fruit.class, ReportForm.class,
                ReportParameterOptions.class, ReportParameter.class, Report.class);
    }
}
