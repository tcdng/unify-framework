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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.BooleanType;
import com.tcdng.unify.core.constant.Gender;
import com.tcdng.unify.core.criterion.Update;

/**
 * Database view entity CRUD tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DatabaseViewEntityCRUDTest extends AbstractUnifyComponentTest {

    private Office parklaneOffice = new Office("24, Parklane Apapa", "+2348888888", 20);

    private Office warehouseOffice = new Office("38, Warehouse Road Apapa", "+2345555555", 35);

    private DatabaseTransactionManager tm;

    private Database db;

    @Test
    public void testCountRecord() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Paul Horowitz", 72, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            Author susan = new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, warehouseOfficeId);
            db.create(susan);
            assertEquals(3, db.countAll(new AuthorViewQuery().ignoreEmptyCriteria(true)));
            assertEquals(2, db.countAll(new AuthorViewQuery().addEquals("authorOfficeId", parklaneOfficeId)));
            assertEquals(2, db.countAll(new AuthorViewQuery().addEndsWith("authorName", "Bramer")));
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testCreateRecord() throws Exception {
        tm.beginTransaction();
        try {
            AuthorView authorView = new AuthorView();
            db.create(authorView);

        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testDeleteRecordById() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            Long authorId =
                    (Long) db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.delete(AuthorView.class, authorId);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordById() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            Long authorId = (Long) db
                    .create(new Author("Susan Bramer", 48, Gender.FEMALE, BooleanType.FALSE, parklaneOfficeId));

            AuthorView authorView = db.find(AuthorView.class, authorId);
            assertNotNull(authorView);
            assertEquals(authorId, authorView.getAuthorId());
            assertEquals(parklaneOfficeId, authorView.getAuthorOfficeId());
            assertEquals(Integer.valueOf(48), authorView.getAuthorAge());
            assertEquals(Gender.FEMALE, authorView.getAuthorGender());
            assertEquals("Susan Bramer", authorView.getAuthorName());
            assertEquals("24, Parklane Apapa", authorView.getOfficeAddress());
            assertEquals(Integer.valueOf(20), authorView.getOfficeSize());
            assertEquals("+2348888888", authorView.getOfficeTelephone());

        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testFindRecordByIdWithInvalidId() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Susan Bramer", 48, Gender.FEMALE, BooleanType.FALSE, parklaneOfficeId));

            db.find(AuthorView.class, 20L);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Paul Horowitz", 72, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            Long authorId = (Long) db
                    .create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, warehouseOfficeId));

            AuthorView authorView = db.find(new AuthorViewQuery().addEquals("authorGender", Gender.FEMALE));
            assertNotNull(authorView);
            assertEquals(authorId, authorView.getAuthorId());
            assertEquals(warehouseOfficeId, authorView.getAuthorOfficeId());
            assertEquals(Integer.valueOf(45), authorView.getAuthorAge());
            assertEquals(Gender.FEMALE, authorView.getAuthorGender());
            assertEquals("Susan Bramer", authorView.getAuthorName());
            assertEquals("38, Warehouse Road Apapa", authorView.getOfficeAddress());
            assertEquals(Integer.valueOf(35), authorView.getOfficeSize());
            assertEquals("+2345555555", authorView.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testFindRecordByCriteriaWithMultipleResult() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Paul Horowitz", 72, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, warehouseOfficeId));
            db.find(new AuthorViewQuery().addLike("authorName", "Bramer"));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByCriteriaWithNoResult() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Paul Horowitz", 72, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            assertNull(db.find(new AuthorViewQuery().addEquals("authorGender", Gender.FEMALE)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecords() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            Long authorId1 =
                    (Long) db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            Long authorId2 = (Long) db
                    .create(new Author("Paul Horowitz", 72, Gender.MALE, BooleanType.FALSE, warehouseOfficeId));
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, warehouseOfficeId));

            List<AuthorView> authorViewList =
                    db.findAll(new AuthorViewQuery().addEquals("authorGender", Gender.MALE).addOrder("authorId"));
            assertNotNull(authorViewList);
            assertEquals(2, authorViewList.size());

            AuthorView authorView = authorViewList.get(0);
            assertNotNull(authorView);
            assertEquals(authorId1, authorView.getAuthorId());
            assertEquals(parklaneOfficeId, authorView.getAuthorOfficeId());
            assertEquals(Integer.valueOf(50), authorView.getAuthorAge());
            assertEquals(Gender.MALE, authorView.getAuthorGender());
            assertEquals("Brian Bramer", authorView.getAuthorName());
            assertEquals("24, Parklane Apapa", authorView.getOfficeAddress());
            assertEquals(Integer.valueOf(20), authorView.getOfficeSize());
            assertEquals("+2348888888", authorView.getOfficeTelephone());

            authorView = authorViewList.get(1);
            assertNotNull(authorView);
            assertEquals(authorId2, authorView.getAuthorId());
            assertEquals(warehouseOfficeId, authorView.getAuthorOfficeId());
            assertEquals(Integer.valueOf(72), authorView.getAuthorAge());
            assertEquals(Gender.MALE, authorView.getAuthorGender());
            assertEquals("Paul Horowitz", authorView.getAuthorName());
            assertEquals("38, Warehouse Road Apapa", authorView.getOfficeAddress());
            assertEquals(Integer.valueOf(35), authorView.getOfficeSize());
            assertEquals("+2345555555", authorView.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsWithSelect() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            Long authorId1 =
                    (Long) db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            Long authorId2 = (Long) db
                    .create(new Author("Paul Horowitz", 72, Gender.MALE, BooleanType.FALSE, warehouseOfficeId));
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, warehouseOfficeId));

            List<AuthorView> authorViewList = db.findAll(new AuthorViewQuery().addEquals("authorGender", Gender.MALE)
                    .addOrder("authorId").addSelect("authorName", "officeAddress"));
            assertNotNull(authorViewList);
            assertEquals(2, authorViewList.size());

            AuthorView authorView = authorViewList.get(0);
            assertNotNull(authorView);
            assertEquals(authorId1, authorView.getAuthorId());
            assertNull(authorView.getAuthorOfficeId());
            assertNull(authorView.getAuthorAge());
            assertNull(authorView.getAuthorGender());
            assertEquals("Brian Bramer", authorView.getAuthorName());
            assertEquals("24, Parklane Apapa", authorView.getOfficeAddress());
            assertNull(authorView.getOfficeSize());
            assertNull(authorView.getOfficeTelephone());

            authorView = authorViewList.get(1);
            assertNotNull(authorView);
            assertEquals(authorId2, authorView.getAuthorId());
            assertNull(authorView.getAuthorOfficeId());
            assertNull(authorView.getAuthorAge());
            assertNull(authorView.getAuthorGender());
            assertEquals("Paul Horowitz", authorView.getAuthorName());
            assertEquals("38, Warehouse Road Apapa", authorView.getOfficeAddress());
            assertNull(authorView.getOfficeSize());
            assertNull(authorView.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllMapRecords() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            Long authorId2 = (Long) db
                    .create(new Author("Paul Horowitz", 72, Gender.MALE, BooleanType.FALSE, warehouseOfficeId));
            Long authorId3 = (Long) db
                    .create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, warehouseOfficeId));

            Map<String, AuthorView> authorViewMap = db.findAllMap(String.class, "authorName",
                    new AuthorViewQuery().addEquals("authorOfficeId", warehouseOfficeId));
            assertEquals(2, authorViewMap.size());

            AuthorView authorView = authorViewMap.get("Susan Bramer");
            assertNotNull(authorView);
            assertEquals(authorId3, authorView.getAuthorId());
            assertEquals(warehouseOfficeId, authorView.getAuthorOfficeId());
            assertEquals(Integer.valueOf(45), authorView.getAuthorAge());
            assertEquals(Gender.FEMALE, authorView.getAuthorGender());
            assertEquals("Susan Bramer", authorView.getAuthorName());
            assertEquals("38, Warehouse Road Apapa", authorView.getOfficeAddress());
            assertEquals(Integer.valueOf(35), authorView.getOfficeSize());
            assertEquals("+2345555555", authorView.getOfficeTelephone());

            authorView = authorViewMap.get("Paul Horowitz");
            assertNotNull(authorView);
            assertEquals(authorId2, authorView.getAuthorId());
            assertEquals(warehouseOfficeId, authorView.getAuthorOfficeId());
            assertEquals(Integer.valueOf(72), authorView.getAuthorAge());
            assertEquals(Gender.MALE, authorView.getAuthorGender());
            assertEquals("Paul Horowitz", authorView.getAuthorName());
            assertEquals("38, Warehouse Road Apapa", authorView.getOfficeAddress());
            assertEquals(Integer.valueOf(35), authorView.getOfficeSize());
            assertEquals("+2345555555", authorView.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordById() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            Long authorId = (Long) db
                    .create(new Author("Susan Bramer", 48, Gender.FEMALE, BooleanType.FALSE, parklaneOfficeId));

            AuthorView authorView = db.list(AuthorView.class, authorId);
            assertNotNull(authorView);
            assertEquals(authorId, authorView.getAuthorId());
            assertEquals(parklaneOfficeId, authorView.getAuthorOfficeId());
            assertEquals(Integer.valueOf(48), authorView.getAuthorAge());
            assertEquals(Gender.FEMALE, authorView.getAuthorGender());
            assertEquals("Susan Bramer", authorView.getAuthorName());
            assertEquals("24, Parklane Apapa", authorView.getOfficeAddress());
            assertEquals(Integer.valueOf(20), authorView.getOfficeSize());
            assertEquals("+2348888888", authorView.getOfficeTelephone());

        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testListRecordByIdWithInvalidId() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Susan Bramer", 48, Gender.FEMALE, BooleanType.FALSE, parklaneOfficeId));

            db.list(AuthorView.class, 20L);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByCriteria() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Paul Horowitz", 72, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            Long authorId = (Long) db
                    .create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, warehouseOfficeId));

            AuthorView authorView = db.list(new AuthorViewQuery().addEquals("authorGender", Gender.FEMALE));
            assertNotNull(authorView);
            assertEquals(authorId, authorView.getAuthorId());
            assertEquals(warehouseOfficeId, authorView.getAuthorOfficeId());
            assertEquals(Integer.valueOf(45), authorView.getAuthorAge());
            assertEquals(Gender.FEMALE, authorView.getAuthorGender());
            assertEquals("Susan Bramer", authorView.getAuthorName());
            assertEquals("38, Warehouse Road Apapa", authorView.getOfficeAddress());
            assertEquals(Integer.valueOf(35), authorView.getOfficeSize());
            assertEquals("+2345555555", authorView.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testListRecordByCriteriaWithMultipleResult() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Paul Horowitz", 72, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, warehouseOfficeId));
            db.list(new AuthorViewQuery().addLike("authorName", "Bramer"));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByCriteriaWithNoResult() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Paul Horowitz", 72, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            assertNull(db.list(new AuthorViewQuery().addEquals("authorGender", Gender.FEMALE)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecords() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            Long authorId1 =
                    (Long) db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            Long authorId2 = (Long) db
                    .create(new Author("Paul Horowitz", 72, Gender.MALE, BooleanType.FALSE, warehouseOfficeId));
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, warehouseOfficeId));

            List<AuthorView> authorViewList =
                    db.listAll(new AuthorViewQuery().addEquals("authorGender", Gender.MALE).addOrder("authorId"));
            assertNotNull(authorViewList);
            assertEquals(2, authorViewList.size());

            AuthorView authorView = authorViewList.get(0);
            assertNotNull(authorView);
            assertEquals(authorId1, authorView.getAuthorId());
            assertEquals(parklaneOfficeId, authorView.getAuthorOfficeId());
            assertEquals(Integer.valueOf(50), authorView.getAuthorAge());
            assertEquals(Gender.MALE, authorView.getAuthorGender());
            assertEquals("Brian Bramer", authorView.getAuthorName());
            assertEquals("24, Parklane Apapa", authorView.getOfficeAddress());
            assertEquals(Integer.valueOf(20), authorView.getOfficeSize());
            assertEquals("+2348888888", authorView.getOfficeTelephone());

            authorView = authorViewList.get(1);
            assertNotNull(authorView);
            assertEquals(authorId2, authorView.getAuthorId());
            assertEquals(warehouseOfficeId, authorView.getAuthorOfficeId());
            assertEquals(Integer.valueOf(72), authorView.getAuthorAge());
            assertEquals(Gender.MALE, authorView.getAuthorGender());
            assertEquals("Paul Horowitz", authorView.getAuthorName());
            assertEquals("38, Warehouse Road Apapa", authorView.getOfficeAddress());
            assertEquals(Integer.valueOf(35), authorView.getOfficeSize());
            assertEquals("+2345555555", authorView.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecordsWithSelect() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            Long authorId1 =
                    (Long) db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            Long authorId2 = (Long) db
                    .create(new Author("Paul Horowitz", 72, Gender.MALE, BooleanType.FALSE, warehouseOfficeId));
            db.create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, warehouseOfficeId));

            List<AuthorView> authorViewList = db.listAll(new AuthorViewQuery().addEquals("authorGender", Gender.MALE)
                    .addOrder("authorId").addSelect("authorName", "officeAddress"));
            assertNotNull(authorViewList);
            assertEquals(2, authorViewList.size());

            AuthorView authorView = authorViewList.get(0);
            assertNotNull(authorView);
            assertEquals(authorId1, authorView.getAuthorId());
            assertNull(authorView.getAuthorOfficeId());
            assertNull(authorView.getAuthorAge());
            assertNull(authorView.getAuthorGender());
            assertEquals("Brian Bramer", authorView.getAuthorName());
            assertEquals("24, Parklane Apapa", authorView.getOfficeAddress());
            assertNull(authorView.getOfficeSize());
            assertNull(authorView.getOfficeTelephone());

            authorView = authorViewList.get(1);
            assertNotNull(authorView);
            assertEquals(authorId2, authorView.getAuthorId());
            assertNull(authorView.getAuthorOfficeId());
            assertNull(authorView.getAuthorAge());
            assertNull(authorView.getAuthorGender());
            assertEquals("Paul Horowitz", authorView.getAuthorName());
            assertEquals("38, Warehouse Road Apapa", authorView.getOfficeAddress());
            assertNull(authorView.getOfficeSize());
            assertNull(authorView.getOfficeTelephone());
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
            Long warehouseOfficeId = (Long) db.create(warehouseOffice);
            Long authorId2 = (Long) db
                    .create(new Author("Paul Horowitz", 72, Gender.MALE, BooleanType.FALSE, warehouseOfficeId));
            Long authorId3 = (Long) db
                    .create(new Author("Susan Bramer", 45, Gender.FEMALE, BooleanType.FALSE, warehouseOfficeId));

            Map<String, AuthorView> authorViewMap = db.listAllMap(String.class, "authorName",
                    new AuthorViewQuery().addEquals("authorOfficeId", warehouseOfficeId));
            assertEquals(2, authorViewMap.size());

            AuthorView authorView = authorViewMap.get("Susan Bramer");
            assertNotNull(authorView);
            assertEquals(authorId3, authorView.getAuthorId());
            assertEquals(warehouseOfficeId, authorView.getAuthorOfficeId());
            assertEquals(Integer.valueOf(45), authorView.getAuthorAge());
            assertEquals(Gender.FEMALE, authorView.getAuthorGender());
            assertEquals("Susan Bramer", authorView.getAuthorName());
            assertEquals("38, Warehouse Road Apapa", authorView.getOfficeAddress());
            assertEquals(Integer.valueOf(35), authorView.getOfficeSize());
            assertEquals("+2345555555", authorView.getOfficeTelephone());

            authorView = authorViewMap.get("Paul Horowitz");
            assertNotNull(authorView);
            assertEquals(authorId2, authorView.getAuthorId());
            assertEquals(warehouseOfficeId, authorView.getAuthorOfficeId());
            assertEquals(Integer.valueOf(72), authorView.getAuthorAge());
            assertEquals(Gender.MALE, authorView.getAuthorGender());
            assertEquals("Paul Horowitz", authorView.getAuthorName());
            assertEquals("38, Warehouse Road Apapa", authorView.getOfficeAddress());
            assertEquals(Integer.valueOf(35), authorView.getOfficeSize());
            assertEquals("+2345555555", authorView.getOfficeTelephone());
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testUpdateRecordById() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            Long authorId = (Long) db
                    .create(new Author("Susan Bramer", 48, Gender.FEMALE, BooleanType.FALSE, parklaneOfficeId));

            AuthorView authorView = db.find(AuthorView.class, authorId);
            db.updateById(authorView);
        } finally {
            tm.endTransaction();
        }
    }

    @Test(expected = UnifyException.class)
    public void testUpdateAllRecord() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Susan Bramer", 48, Gender.FEMALE, BooleanType.FALSE, parklaneOfficeId));
            db.updateAll(new AuthorViewQuery().addEquals("authorGender", Gender.MALE), new Update().add("authorAge", 52));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testValue() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Susan Bramer", 48, Gender.FEMALE, BooleanType.FALSE, parklaneOfficeId));

            Gender gender = db.value(Gender.class, "authorGender", new AuthorViewQuery().addEquals("authorAge", 48));
            assertEquals(Gender.FEMALE, gender);

            String name =
                    db.value(String.class, "authorName", new AuthorViewQuery().addEquals("authorGender", Gender.MALE));
            assertEquals("Brian Bramer", name);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testValueList() throws Exception {
        tm.beginTransaction();
        try {
            Long parklaneOfficeId = (Long) db.create(parklaneOffice);
            db.create(new Author("Brian Bramer", 50, Gender.MALE, BooleanType.FALSE, parklaneOfficeId));
            db.create(new Author("Susan Bramer", 48, Gender.FEMALE, BooleanType.FALSE, parklaneOfficeId));

            List<String> nameList =
                    db.valueList(String.class, "authorName", new AuthorViewQuery().addEquals("authorOfficeId", parklaneOfficeId));
            assertNotNull(nameList);
            assertEquals(2, nameList.size());
            assertTrue(nameList.contains("Susan Bramer"));
            assertTrue(nameList.contains("Brian Bramer"));
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
        deleteAll(Author.class, Office.class);
    }
}
