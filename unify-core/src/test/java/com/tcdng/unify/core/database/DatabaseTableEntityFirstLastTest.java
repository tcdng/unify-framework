/*
 * Copyright (c) 2018-2025 The Code Department.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.constant.BooleanType;
import com.tcdng.unify.core.constant.Gender;

/**
 * Database table entity first-last records tests.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class DatabaseTableEntityFirstLastTest extends AbstractUnifyComponentTest {

    private Office warehouseOffice = new Office("38, Warehouse Road Apapa", "+2345555555", 35);

    private DatabaseTransactionManager tm;

    private Database db;

    @Test
    public void testFindFirstByCriteriaNoMatch() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            Fruit foundFruit = db.findFirst(new FruitQuery().addLike("name", "orange"));
            assertNull(foundFruit);
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindFirstByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            Fruit foundFruit = db.findFirst(new FruitQuery().addLike("name", "apple"));
            assertNotNull(foundFruit);
            assertEquals("apple", foundFruit.getName());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindLastByCriteriaNoMatch() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            Fruit foundFruit = db.findLast(new FruitQuery().addLike("name", "orange"));
            assertNull(foundFruit);
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindLastByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Fruit("apple", "red", 20.00));
            db.create(new Fruit("banana", "yellow", 45.00));
            db.create(new Fruit("pineapple", "cyan", 60.00));
            Fruit foundFruit = db.findLast(new FruitQuery().addLike("name", "apple"));
            assertNotNull(foundFruit);
            assertEquals("pineapple", foundFruit.getName());
        } catch (Exception e) {
            tm.setRollback();
            throw e;
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListFirstByCriteriaNoMatch() throws Exception {
        tm.beginTransaction();
        try {
            Long testOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, testOfficeId));
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.TRUE, testOfficeId));
            db.create(new Author("Tom Hanks", 45, Gender.MALE, BooleanType.TRUE, testOfficeId));

            Author foundAuthor = db.listFirst(new AuthorQuery().addEquals("age", 102));
            assertNull(foundAuthor);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListFirstByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            Long testOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, testOfficeId));
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.TRUE, testOfficeId));
            db.create(new Author("Tom Hanks", 45, Gender.MALE, BooleanType.TRUE, testOfficeId));

            Author foundAuthor = db.listFirst(new AuthorQuery().addEquals("age", 45));
            assertNotNull(foundAuthor);
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
    public void testListLastByCriteriaNoMatch() throws Exception {
        tm.beginTransaction();
        try {
            Long testOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, testOfficeId));
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.TRUE, testOfficeId));
            db.create(new Author("Tom Hanks", 45, Gender.MALE, BooleanType.TRUE, testOfficeId));

            Author foundAuthor = db.listLast(new AuthorQuery().addEquals("age", 450));
            assertNull(foundAuthor);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListLastByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            Long testOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, testOfficeId));
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.TRUE, testOfficeId));
            db.create(new Author("Tom Hanks", 45, Gender.MALE, BooleanType.TRUE, testOfficeId));

            Author foundAuthor = db.listLast(new AuthorQuery().addEquals("age", 45));
            assertNotNull(foundAuthor);
            assertEquals("Tom Hanks", foundAuthor.getName());
            assertEquals(Integer.valueOf(45), foundAuthor.getAge());
            assertEquals(Gender.MALE, foundAuthor.getGender());
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
    public void testFindLeanFirstByCriteriaWithChild() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Report("weeklyReportA10", "Weekly Report", new ReportForm("beanEditor")));
            db.create(new Report("weeklyReportB10", "Weekly Report", new ReportForm("beanEditor")));
            db.create(new Report("weeklyReportA11", "Weekly Report", new ReportForm("beanEditor")));
            db.create(new Report("weeklyReportB11", "Weekly Report", new ReportForm("beanEditor")));

            Report foundReport = db.findLeanFirst(new ReportQuery().addLike("name", "A1"));
            assertNotNull(foundReport);
            assertEquals("weeklyReportA10", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());
            assertNull(foundReport.getReportForm());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindLeanLastByCriteriaWithChild() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Report("weeklyReportA10", "Weekly Report", new ReportForm("beanEditor")));
            db.create(new Report("weeklyReportB10", "Weekly Report", new ReportForm("beanEditor")));
            db.create(new Report("weeklyReportA11", "Weekly Report", new ReportForm("beanEditor")));
            db.create(new Report("weeklyReportB11", "Weekly Report", new ReportForm("beanEditor")));

            Report foundReport = db.findLeanLast(new ReportQuery().addLike("name", "A1"));
            assertNotNull(foundReport);
            assertEquals("weeklyReportA11", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());
            assertNull(foundReport.getReportForm());
        } finally {
            tm.endTransaction();
        }
    }


    @Test
    public void testListLeanFirstByCriteriaWithChild() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Report("weeklyReportA10", "Weekly Report", new ReportForm("beanEditor")));
            db.create(new Report("weeklyReportB10", "Weekly Report", new ReportForm("beanEditor")));
            db.create(new Report("weeklyReportA11", "Weekly Report", new ReportForm("beanEditor")));
            db.create(new Report("weeklyReportB11", "Weekly Report", new ReportForm("beanEditor")));

            Report foundReport = db.listLeanFirst(new ReportQuery().addLike("name", "A1"));
            assertNotNull(foundReport);
            assertEquals("weeklyReportA10", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());
            assertNull(foundReport.getReportForm());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListLeanLastByCriteriaWithChild() throws Exception {
        tm.beginTransaction();
        try {
            db.create(new Report("weeklyReportA10", "Weekly Report", new ReportForm("beanEditor")));
            db.create(new Report("weeklyReportB10", "Weekly Report", new ReportForm("beanEditor")));
            db.create(new Report("weeklyReportA11", "Weekly Report", new ReportForm("beanEditor")));
            db.create(new Report("weeklyReportB11", "Weekly Report", new ReportForm("beanEditor")));

            Report foundReport = db.listLeanLast(new ReportQuery().addLike("name", "A1"));
            assertNotNull(foundReport);
            assertEquals("weeklyReportA11", foundReport.getName());
            assertEquals("Weekly Report", foundReport.getDescription());
            assertNull(foundReport.getReportForm());
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
