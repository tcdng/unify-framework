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
package com.tcdng.unify.core.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.tcdng.unify.core.criterion.Amongst;
import com.tcdng.unify.core.criterion.BeginsWith;
import com.tcdng.unify.core.criterion.Equals;
import com.tcdng.unify.core.criterion.Greater;
import com.tcdng.unify.core.criterion.Less;
import com.tcdng.unify.core.criterion.LessOrEqual;
import com.tcdng.unify.core.criterion.Like;
import com.tcdng.unify.core.criterion.NotEquals;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.criterion.Update;

/**
 * Database table entity pre-query tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DatabaseTableEntityPreQueryTest extends AbstractUnifyComponentTest {

    private Office parklaneOffice = new Office("24, Parklane Apapa", "+2348888888", 20);

    private Office warehouseOffice = new Office("38, Warehouse Road Apapa", "+2345555555", 35);

    private DatabaseTransactionManager tm;

    private Database db;

    private TestEntityPolicy testEntityPolicy;

    @Test
    public void testCountRecord() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("orange", "orange", 15.00));
            assertEquals(4, db.countAll(new FruitQuery().ignoreEmptyCriteria(true)));

            testEntityPolicy.setRestriction(new LessOrEqual("price", 20.00));
            assertEquals(2, db.countAll(new FruitQuery()));

            testEntityPolicy.setRestriction(new BeginsWith("name", "ban"));
            assertEquals(1, db.countAll(new FruitQuery()));
        } catch (Exception e) {
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

            testEntityPolicy.setRestriction(new Like("name", "app"));
            assertEquals(Double.valueOf(20.00), db.min(Double.class, "price", new FruitQuery()));
        } catch (Exception e) {
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

            testEntityPolicy.setRestriction(new Equals("name", "tangerine"));
            assertNull(db.min(Double.class, "price", new FruitQuery()));
        } catch (Exception e) {
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

            testEntityPolicy.setRestriction(new Like("name", "app"));
            assertEquals(Double.valueOf(60.00), db.max(Double.class, "price", new FruitQuery()));
        } catch (Exception e) {
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

            testEntityPolicy.setRestriction(new Equals("name", "tangerine"));
            assertNull(db.max(Double.class, "price", new FruitQuery()));
        } catch (Exception e) {
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

            testEntityPolicy.setRestriction(new NotEquals("color", "cyan"));
            assertEquals(3, db.deleteAll(new FruitQuery()));

            testEntityPolicy.clearRestriction();
            List<Fruit> testFruitList = db.findAll(new FruitQuery().ignoreEmptyCriteria(true));
            assertEquals(1, testFruitList.size());
            assertEquals(pineapple, testFruitList.get(0));
        } catch (Exception e) {
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

            testEntityPolicy.setRestriction(new NotEquals("color", "cyan"));
            assertEquals(3, db.deleteAll(new FruitQuery()));

            // Order should be ignored
            testEntityPolicy.clearRestriction();
            List<Fruit> testFruitList = db.findAll(new FruitQuery().addOrder("color").ignoreEmptyCriteria(true));
            assertEquals(1, testFruitList.size());
            assertEquals(pineapple, testFruitList.get(0));
        } catch (Exception e) {
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
            testEntityPolicy.setRestriction(new Equals("officeTelephone", "+2348888888"));
            assertEquals(2, db.deleteAll(new AuthorQuery()));

            // Only Susan should be left
            testEntityPolicy.clearRestriction();
            List<Author> testAuthorList = db.findAll(new AuthorQuery().ignoreEmptyCriteria(true));
            assertEquals(1, testAuthorList.size());
            assertEquals(susan, testAuthorList.get(0));
        } catch (Exception e) {
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

            testEntityPolicy.setRestriction(new Equals("color", "yellow"));
            Fruit foundFruit = db.find(new FruitQuery());
            assertEquals(banana, foundFruit);
        } catch (Exception e) {
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

            testEntityPolicy.setRestriction(new Like("name", "app"));
            Fruit foundFruit = db.find(new FruitQuery().setMin("price"));
            assertEquals(apple, foundFruit);
        } catch (Exception e) {
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

            testEntityPolicy.setRestriction(new Like("name", "app"));
            Fruit foundFruit = db.find(new FruitQuery().setMin("price"));
            assertEquals(apple, foundFruit);
        } catch (Exception e) {
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

            testEntityPolicy.setRestriction(new Like("name", "app"));
            Fruit foundFruit = db.find(new FruitQuery().setMax("price"));
            assertEquals(pineapple, foundFruit);
        } catch (Exception e) {
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

            testEntityPolicy.setRestriction(new Like("name", "app"));
            Fruit foundFruit = db.find(new FruitQuery().setMax("price"));
            assertEquals(pineapple, foundFruit);
        } catch (Exception e) {
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

            testEntityPolicy.setRestriction(new Equals("color", "cyan"));
            Fruit foundFruit = db.find(new FruitQuery().addSelect("name", "price"));
            assertNotNull(foundFruit);
            assertEquals("pineapple", foundFruit.getName());
            assertNull(foundFruit.getColor());
            assertEquals(Double.valueOf(60.00), foundFruit.getPrice());
            assertNull(foundFruit.getQuantity());
        } catch (Exception e) {
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

            testEntityPolicy.setRestriction(new Like("name", "apple"));
            db.find(new FruitQuery());
        } catch (Exception e) {
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

            testEntityPolicy.setRestriction(new Like("name", "zing"));
            assertNull(db.find(new FruitQuery()));
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

            testEntityPolicy.setRestriction(new LessOrEqual("price", 20.00));
            List<Fruit> testFruitList = db.findAll(new FruitQuery().addOrder("price"));
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

            testEntityPolicy.setRestriction(new LessOrEqual("price", 20.00));
            List<Fruit> testFruitList = db.findAll(new FruitQuery().addOrder(OrderType.DESCENDING, "price"));
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

            testEntityPolicy.setRestriction(new LessOrEqual("price", 20.00));
            List<Fruit> testFruitList = db.findAll(new FruitQuery().addOrder("price").addSelect("name", "quantity"));
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

            testEntityPolicy.setRestriction(new LessOrEqual("price", 20.00));
            Map<String, Fruit> testFruitMap = db.findAllMap(String.class, "name", new FruitQuery().addOrder("price"));
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

            testEntityPolicy.setRestriction(new Amongst("name", Arrays.asList("orange")));
            List<Fruit> testFruitList = db.findAll(new FruitQuery());
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

            testEntityPolicy.setRestriction(new Amongst("name", Arrays.asList("apple", "orange")));
            List<Fruit> testFruitList = db.findAll(new FruitQuery().addOrder("price"));
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

            testEntityPolicy.setRestriction(new Amongst("name", Arrays.asList()));
            List<Fruit> testFruitList = db.findAll(new FruitQuery());
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
            List<Fruit> testFruitList =
                    db.findAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("id").setLimit(4));
            assertEquals(4, testFruitList.size());
            assertEquals("apple", testFruitList.get(0).getName());
            assertEquals("banana", testFruitList.get(1).getName());
            assertEquals("orange", testFruitList.get(2).getName());
            assertEquals("mango", testFruitList.get(3).getName());

            testEntityPolicy.setRestriction(new Greater("price", 20.0));
            testFruitList = db.findAll(new FruitQuery().addOrder("id").setLimit(2));
            assertEquals(2, testFruitList.size());
            assertEquals("banana", testFruitList.get(0).getName());
            assertEquals("orange", testFruitList.get(1).getName());

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

            testEntityPolicy.setRestriction(new Like("name", "apple"));
            Query<Fruit> copyQuery = query.copyNoCriteria().addRestriction(restriction).addOrder("name");

            List<Fruit> testFruitList = db.findAll(copyQuery);

            assertEquals(2, testFruitList.size());
            assertEquals("apple", testFruitList.get(0).getName());
            assertEquals("pineapple", testFruitList.get(1).getName());
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

            testEntityPolicy.setRestriction(new Equals("age", 45));
            Author foundAuthor = db.list(new AuthorQuery());
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

            testEntityPolicy.setRestriction(new Equals("age", 45));
            Author foundAuthor =
                    db.list(new AuthorQuery().addSelect("name", "gender", "retiredDesc", "officeId", "officeAddress"));
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

            testEntityPolicy.setRestriction(new Equals("officeTelephone", "+2345555555"));
            Author foundAuthor = db.list(new AuthorQuery());
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

            testEntityPolicy.setRestriction(new Less("age", 60));
            db.list(new AuthorQuery());
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

            testEntityPolicy.setRestriction(new Greater("age", 100));
            assertNull(db.list(new AuthorQuery()));
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
            testEntityPolicy.setRestriction(new LessOrEqual("age", 50));
            List<Author> testAuthorList = db.listAll(new AuthorQuery().addOrder("name"));
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
            testEntityPolicy.setRestriction(new LessOrEqual("age", 50));
            List<Author> testAuthorList = db.listAll(new AuthorQuery().addOrder(OrderType.DESCENDING, "name"));
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
            testEntityPolicy.setRestriction(new LessOrEqual("age", 50));
            List<Author> testAuthorList = db.listAll(
                    new AuthorQuery().addOrder("name").addSelect("age", "retired", "gender", "officeTelephone"));
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
            testEntityPolicy.setRestriction(new LessOrEqual("age", 50));
            Map<String, Author> testAuthorMap = db.listAllMap(String.class, "name", new AuthorQuery().addOrder("name"));
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
            List<Fruit> testFruitList =
                    db.listAll(new FruitQuery().ignoreEmptyCriteria(true).addOrder("id").setLimit(4));
            assertEquals(4, testFruitList.size());
            assertEquals("apple", testFruitList.get(0).getName());
            assertEquals("banana", testFruitList.get(1).getName());
            assertEquals("orange", testFruitList.get(2).getName());
            assertEquals("mango", testFruitList.get(3).getName());

            testEntityPolicy.setRestriction(new Greater("price", 20.0));
            testFruitList = db.findAll(new FruitQuery().addOrder("id").setLimit(2));
            assertEquals(2, testFruitList.size());
            assertEquals("banana", testFruitList.get(0).getName());
            assertEquals("orange", testFruitList.get(1).getName());

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
            testEntityPolicy.setRestriction(new Equals("officeTelephone", "+2348888888"));
            List<Author> testAuthorList = db.listAll(new AuthorQuery().addOrder("name"));
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
    public void testUpdateAllRecord() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("orange", "orange", 15.00));

            testEntityPolicy.setRestriction(new LessOrEqual("price", 45.00));
            assertEquals(3, db.updateAll(new FruitQuery(), new Update().add("price", 30.00)));

            testEntityPolicy.clearRestriction();
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

            testEntityPolicy.setRestriction(new LessOrEqual("price", 45.00));
            assertEquals(3, db.updateAll(new FruitQuery().addLessThanEqual("price", 45.00).addOrder("id"),
                    new Update().add("price", 30.00)));

            testEntityPolicy.clearRestriction();
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
            testEntityPolicy.setRestriction(new Equals("officeTelephone", "+2348888888"));
            assertEquals(2, db.updateAll(new AuthorQuery(), new Update().add("age", 100)));

            testEntityPolicy.clearRestriction();
            List<Author> testAuthorList =
                    db.findAll(new AuthorQuery().ignoreEmptyCriteria(true).addOrder("age", "name"));
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
    public void testFindRecordWithOnewayTransformationByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            User user = new User("tiger", "scott");
            db.create(user);

            testEntityPolicy.setRestriction(new Equals("password", "scott"));
            List<User> userList = db.findAll(new UserQuery());
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

            testEntityPolicy.setRestriction(new Equals("password", "scott"));
            List<User> userList = db.listAll(new UserQuery());
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
    public void testUpdateRecordWithOnewayTransformationByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            User user = new User("tiger", "scott");
            Long id = (Long) db.create(user);
            User fetchedUser = db.find(User.class, id);

            testEntityPolicy.setRestriction(new Equals("id", id));
            db.updateAll(new UserQuery(), new Update().add("password", "tyler"));

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

            testEntityPolicy.setRestriction(new Equals("password", "brown"));
            int deleteCount = db.deleteAll(new UserQuery());
            assertEquals(1, deleteCount);

            testEntityPolicy.clearRestriction();
            List<User> userList = db.findAll(new UserQuery().ignoreEmptyCriteria(true));
            assertEquals(1, userList.size());

            User fetchedUser = userList.get(0);
            assertEquals("tiger", fetchedUser.getName());
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

            testEntityPolicy.setRestriction(new Equals("password", "greenspan"));
            List<ServerConfig> serverConfigList = db.findAll(new ServerConfigQuery());
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
    public void testUpdateRecordWithTwowayTransformationByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            ServerConfig serverConfig = new ServerConfig("alan", "greenspan");
            Long id = (Long) db.create(serverConfig);
            ServerConfig fetchedServerConfig = db.find(ServerConfig.class, id);

            testEntityPolicy.setRestriction(new Equals("id", id));
            db.updateAll(new ServerConfigQuery(), new Update().add("password", "bluebridge"));

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

            testEntityPolicy.setRestriction(new Equals("password", "jones"));
            int deleteCount = db.deleteAll(new ServerConfigQuery());
            assertEquals(1, deleteCount);

            testEntityPolicy.clearRestriction();
            List<ServerConfig> serverConfigList = db.findAll(new ServerConfigQuery().ignoreEmptyCriteria(true));
            assertEquals(1, serverConfigList.size());

            ServerConfig fetchedServerConfig = serverConfigList.get(0);
            assertEquals("alan", fetchedServerConfig.getName());
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

            testEntityPolicy.setRestriction(new Equals("id", id));
            Report foundReport = db.findLean(new ReportQuery());
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

            testEntityPolicy.setRestriction(new Equals("id", id));
            Report foundReport = db.find(new ReportQuery());
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

            testEntityPolicy.setRestriction(new Equals("id", id));
            Report foundReport = db.find(new ReportQuery());
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

            testEntityPolicy.setRestriction(new Equals("id", id));
            Report foundReport = db.find(new ReportQuery().addSelect("name", "reportForm"));
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

            testEntityPolicy.setRestriction(new Equals("id", id));
            Report foundReport = db.find(new ReportQuery().addSelect("name", "parameters"));
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

            testEntityPolicy.setRestriction(new Equals("id", id));
            Report foundReport = db.find(new ReportQuery());
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
    public void testListLeanRecordByCriteriaWithChild() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setReportForm(new ReportForm("grayEditor"));
            Long id = (Long) db.create(report);

            testEntityPolicy.setRestriction(new Equals("id", id));
            Report foundReport = db.listLean(new ReportQuery());
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

            testEntityPolicy.setRestriction(new Equals("id", id));
            Report foundReport = db.listLean(new ReportQuery());
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

            testEntityPolicy.setRestriction(new Equals("id", id));
            Report foundReport = db.list(new ReportQuery());
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

            testEntityPolicy.setRestriction(new Equals("id", id));
            Report foundReport = db.list(new ReportQuery());
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

            testEntityPolicy.setRestriction(new Equals("id", id));
            Report foundReport = db.list(new ReportQuery().addSelect("description", "reportForm"));
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

            testEntityPolicy.setRestriction(new Equals("id", id));
            Report foundReport = db.list(new ReportQuery().addSelect("parameters"));
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

            testEntityPolicy.setRestriction(new Equals("id", id));
            Report foundReport = db.list(new ReportQuery());

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
    public void testDeleteRecordByIdWithNoChildList() throws Exception {
        tm.beginTransaction();
        try {
            Report report = new Report("weeklyReport", "Weekly Report");
            report.setParameters(new ArrayList<ReportParameter>());
            Long id = (Long) db.create(report);
            db.delete(Report.class, id);

            testEntityPolicy.setRestriction(new Equals("id", id));
            assertEquals(0, db.countAll(new ReportQuery()));
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

            testEntityPolicy.setRestriction(new Equals("id", id));
            assertEquals(0, db.countAll(new ReportQuery()));
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

            testEntityPolicy.setRestriction(new Equals("id", id));
            assertEquals(0, db.countAll(new ReportQuery()));

            testEntityPolicy.clearRestriction();
            assertEquals(0, db.countAll(new ReportFormQuery().ignoreEmptyCriteria(true)));
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

            testEntityPolicy.setRestriction(new Equals("name", "apple"));
            String color = db.value(String.class, "color", new FruitQuery());
            assertEquals("red", color);

            testEntityPolicy.setRestriction(new Equals("name", "banana"));
            Double price = db.value(Double.class, "price", new FruitQuery());
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

            testEntityPolicy.setRestriction(new Like("name", "apple"));
            db.value(String.class, "color", new FruitQuery());
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

            testEntityPolicy.setRestriction(new Like("name", "apple"));
            List<String> colorList = db.valueList(String.class, "color", new FruitQuery());
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

            testEntityPolicy.setRestriction(new Greater("price", Double.valueOf(15.00)));
            Set<String> nameSet = db.valueSet(String.class, "name", new FruitQuery());
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

            testEntityPolicy.setRestriction(new Greater("price", Double.valueOf(15.00)));
            Map<String, Double> priceByFruit =
                    db.valueMap(String.class, "name", Double.class, "price", new FruitQuery());
            assertEquals(3, priceByFruit.size());
            assertEquals(Double.valueOf(20.00), priceByFruit.get("apple"));
            assertEquals(Double.valueOf(60.00), priceByFruit.get("pineapple"));
            assertEquals(Double.valueOf(45.00), priceByFruit.get("banana"));
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

            testEntityPolicy.setRestriction(new Like("name", "apple"));
            List<Fruit> testFruitList = db.findAll(new FruitQuery().setMin("price").addOrder("name"));
            assertEquals(2, testFruitList.size());
            assertEquals(apple, testFruitList.get(0));
            assertEquals(starApple, testFruitList.get(1));
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
            testEntityPolicy.setRestriction(new LessOrEqual("age", 50));
            List<Author> testAuthorList = db.listAll(new AuthorQuery().setMin("officeSize").addOrder("name"));
            assertEquals(1, testAuthorList.size());

            // Should pick the Brian Brammer because office has smaller size
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

            testEntityPolicy.setRestriction(new Like("name", "apple"));
            List<Fruit> testFruitList = db.findAll(new FruitQuery().setMax("price").addOrder("name"));
            assertEquals(1, testFruitList.size());
            assertEquals(apple, testFruitList.get(0));
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
            testEntityPolicy.setRestriction(new Equals("gender", Gender.MALE));
            List<Author> testAuthorList = db.listAll(new AuthorQuery().setMax("age").addOrder("name"));
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

    @Override
    protected void doAddSettingsAndDependencies() throws Exception {
        addContainerSetting(UnifyCorePropertyConstants.APPLICATION_QUERY_LIMIT, 8);
    }

    @Override
    protected void onSetup() throws Exception {
        tm = (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);
        db = (Database) getComponent(ApplicationComponents.APPLICATION_DATABASE);
        testEntityPolicy = (TestEntityPolicy) getComponent("testversionedentity-policy");
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    protected void onTearDown() throws Exception {
        testEntityPolicy.clearRestriction();
        deleteAll(User.class, ServerConfig.class, Author.class, Office.class, Fruit.class, ReportForm.class,
                ReportParameterOptions.class, ReportParameter.class, Report.class);
    }
}
