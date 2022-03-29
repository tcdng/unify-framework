/*
 * Copyright 2018-2022 The Code Department.
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCorePropertyConstants;

/**
 * Database foster parent with child list CRUD tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DatabaseFosterParentCRUDTest extends AbstractUnifyComponentTest {

    private DatabaseTransactionManager tm;

    private Database db;

    @Test
    public void testUpdateRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long loanApplicationId = (Long) db.create(loanApplication);

            loanApplication.setAmount(BigDecimal.valueOf(40.2).setScale(2));
            loanApplication.setFileList(Arrays.asList(new FileAttachment("photo", "Photograph")));
            db.updateById(loanApplication);

            LoanApplication fetchedLoanApplication = db.find(LoanApplication.class, loanApplicationId);
            assertNotNull(fetchedLoanApplication);
            assertEquals("weeklyLoanApplication", fetchedLoanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(40.2).setScale(2), fetchedLoanApplication.getAmount());

            List<FileAttachment> fileList = fetchedLoanApplication.getFileList();
            assertNotNull(fileList);
            assertEquals(1, fileList.size());
            assertEquals("photo", fileList.get(0).getCode());
            assertEquals("Photograph", fileList.get(0).getDescription());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateLeanRecordByIdWithBlankChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long loanApplicationId = (Long) db.create(loanApplication);

            loanApplication.setAmount(BigDecimal.valueOf(40.2).setScale(2));
            loanApplication.setFileList(null); // blank
            db.updateLeanById(loanApplication);

            LoanApplication fetchedLoanApplication = db.find(LoanApplication.class, loanApplicationId);
            assertNotNull(fetchedLoanApplication);
            assertEquals("weeklyLoanApplication", fetchedLoanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(40.2).setScale(2), fetchedLoanApplication.getAmount());

            List<FileAttachment> fileList = fetchedLoanApplication.getFileList();
            assertNotNull(fileList);
            assertEquals(2, fileList.size());
            assertEquals("incoporationCert", fileList.get(0).getCode());
            assertEquals("Incorporation Certificate", fileList.get(0).getDescription());
            assertEquals("tinCert", fileList.get(1).getCode());
            assertEquals("TIN Certificate", fileList.get(1).getDescription());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateLeanRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long loanApplicationId = (Long) db.create(loanApplication);

            loanApplication.setAmount(BigDecimal.valueOf(40.2).setScale(2));
            loanApplication.setFileList(Arrays.asList(new FileAttachment("photo", "Photograph")));
            db.updateLeanById(loanApplication);

            LoanApplication fetchedLoanApplication = db.find(LoanApplication.class, loanApplicationId);
            assertNotNull(fetchedLoanApplication);
            assertEquals("weeklyLoanApplication", fetchedLoanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(40.2).setScale(2), fetchedLoanApplication.getAmount());

            List<FileAttachment> fileList = fetchedLoanApplication.getFileList();
            assertNotNull(fileList);
            assertEquals(2, fileList.size());
            assertEquals("incoporationCert", fileList.get(0).getCode());
            assertEquals("Incorporation Certificate", fileList.get(0).getDescription());
            assertEquals("tinCert", fileList.get(1).getCode());
            assertEquals("TIN Certificate", fileList.get(1).getDescription());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateRecordByIdVersionWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long loanApplicationId = (Long) db.create(loanApplication);

            loanApplication.setAmount(BigDecimal.valueOf(40.2).setScale(2));
            loanApplication.setFileList(Arrays.asList(new FileAttachment("photo", "Photograph")));
            db.updateByIdVersion(loanApplication);

            LoanApplication fetchedLoanApplication = db.find(LoanApplication.class, loanApplicationId);
            assertNotNull(fetchedLoanApplication);
            assertEquals("weeklyLoanApplication", fetchedLoanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(40.2).setScale(2), fetchedLoanApplication.getAmount());

            List<FileAttachment> fileList = fetchedLoanApplication.getFileList();
            assertNotNull(fileList);
            assertEquals(1, fileList.size());
            assertEquals("photo", fileList.get(0).getCode());
            assertEquals("Photograph", fileList.get(0).getDescription());
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
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long loanApplicationId = (Long) db.create(loanApplication);

            loanApplication.setAmount(BigDecimal.valueOf(40.2).setScale(2));
            loanApplication.setFileList(null); // Blank child list
            db.updateLeanByIdVersion(loanApplication);

            LoanApplication fetchedLoanApplication = db.find(LoanApplication.class, loanApplicationId);
            assertNotNull(fetchedLoanApplication);
            assertEquals("weeklyLoanApplication", fetchedLoanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(40.2).setScale(2), fetchedLoanApplication.getAmount());

            List<FileAttachment> fileList = fetchedLoanApplication.getFileList();
            assertNotNull(fileList);
            assertEquals(2, fileList.size());
            assertEquals("incoporationCert", fileList.get(0).getCode());
            assertEquals("Incorporation Certificate", fileList.get(0).getDescription());
            assertEquals("tinCert", fileList.get(1).getCode());
            assertEquals("TIN Certificate", fileList.get(1).getDescription());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testUpdateLeanRecordByIdVersionWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long loanApplicationId = (Long) db.create(loanApplication);

            loanApplication.setAmount(BigDecimal.valueOf(40.2).setScale(2));
            loanApplication.setFileList(Arrays.asList(new FileAttachment("photo", "Photograph")));
            db.updateLeanByIdVersion(loanApplication);

            LoanApplication fetchedLoanApplication = db.find(LoanApplication.class, loanApplicationId);
            assertNotNull(fetchedLoanApplication);
            assertEquals("weeklyLoanApplication", fetchedLoanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(40.2).setScale(2), fetchedLoanApplication.getAmount());

            List<FileAttachment> fileList = fetchedLoanApplication.getFileList();
            assertNotNull(fileList);
            assertEquals(2, fileList.size());
            assertEquals("incoporationCert", fileList.get(0).getCode());
            assertEquals("Incorporation Certificate", fileList.get(0).getDescription());
            assertEquals("tinCert", fileList.get(1).getCode());
            assertEquals("TIN Certificate", fileList.get(1).getDescription());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecordWithNoChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            Long id = (Long) db.create(loanApplication);
            assertNotNull(id);
            assertEquals(id, loanApplication.getId());

            int count = db.countAll(new FileAttachmentQuery().ignoreEmptyCriteria(true));
            assertEquals(0, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecordWithNullChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.setFileList(null);
            Long id = (Long) db.create(loanApplication);
            assertNotNull(id);
            assertEquals(id, loanApplication.getId());

            int count = db.countAll(new FileAttachmentQuery().ignoreEmptyCriteria(true));
            assertEquals(0, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateRecordWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            FileAttachment rpStart = new FileAttachment("incoporationCert", "Incorporation Certificate");
            FileAttachment rpEnd = new FileAttachment("tinCert", "TIN Certificate");
            loanApplication.addAttachment(rpStart).addAttachment(rpEnd);

            Long id = (Long) db.create(loanApplication);
            assertNotNull(id);
            assertEquals(id, loanApplication.getId());
            assertEquals(id, rpStart.getOwnerId());
            assertEquals(id, rpEnd.getOwnerId());
            assertEquals("LOANAPPLICATION", rpStart.getOwnerType());
            assertEquals("LOANAPPLICATION", rpEnd.getOwnerType());

            int count = db.countAll(new FileAttachmentQuery().ignoreEmptyCriteria(true));
            assertEquals(2, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testCreateMultipleRecordsWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long id1 = (Long) db.create(loanApplication);

            loanApplication = new LoanApplication("salaryLoanApplication", BigDecimal.valueOf(70.32));
            loanApplication.addAttachment(new FileAttachment("birthCert", "Birth Certificate"));
            Long id2 = (Long) db.create(loanApplication);

            int count = db.countAll(new FileAttachmentQuery().ownerId(id1).ownerType("LOANAPPLICATION"));
            assertEquals(2, count);

            count = db.countAll(new FileAttachmentQuery().ownerId(id2).ownerType("LOANAPPLICATION"));
            assertEquals(1, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindLeanRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long id = (Long) db.create(loanApplication);

            LoanApplication foundLoanApplication = db.findLean(LoanApplication.class, id);
            assertNotNull(foundLoanApplication);
            assertEquals("weeklyLoanApplication", foundLoanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(25.5).setScale(2), foundLoanApplication.getAmount());

            assertNull(foundLoanApplication.getFileList());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long id = (Long) db.create(loanApplication);

            LoanApplication foundLoanApplication = db.find(LoanApplication.class, id);
            assertNotNull(foundLoanApplication);
            assertEquals("weeklyLoanApplication", foundLoanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(25.5).setScale(2), foundLoanApplication.getAmount());

            assertNotNull(foundLoanApplication.getFileList());
            assertEquals(2, foundLoanApplication.getFileList().size());

            FileAttachment fileAttach = foundLoanApplication.getFileList().get(0);
            assertEquals("incoporationCert", fileAttach.getCode());
            assertEquals("Incorporation Certificate", fileAttach.getDescription());

            fileAttach = foundLoanApplication.getFileList().get(1);
            assertEquals("tinCert", fileAttach.getCode());
            assertEquals("TIN Certificate", fileAttach.getDescription());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindLeanRecordByCriteriaWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long id = (Long) db.create(loanApplication);

            LoanApplication foundLoanApplication = db.findLean(new LoanApplicationQuery().addEquals("id", id));
            assertNotNull(foundLoanApplication);
            assertEquals("weeklyLoanApplication", foundLoanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(25.5).setScale(2), foundLoanApplication.getAmount());

            assertNull(foundLoanApplication.getFileList());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByCriteriaWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long id = (Long) db.create(loanApplication);

            LoanApplication foundLoanApplication = db.find(new LoanApplicationQuery().addEquals("id", id));
            assertNotNull(foundLoanApplication);
            assertEquals("weeklyLoanApplication", foundLoanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(25.5).setScale(2), foundLoanApplication.getAmount());

            assertNotNull(foundLoanApplication.getFileList());
            assertEquals(2, foundLoanApplication.getFileList().size());

            FileAttachment fileAttach = foundLoanApplication.getFileList().get(0);
            assertEquals("incoporationCert", fileAttach.getCode());
            assertEquals("Incorporation Certificate", fileAttach.getDescription());

            fileAttach = foundLoanApplication.getFileList().get(1);
            assertEquals("tinCert", fileAttach.getCode());
            assertEquals("TIN Certificate", fileAttach.getDescription());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindRecordByCriteriaWithSelectChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long id = (Long) db.create(loanApplication);

            LoanApplication foundLoanApplication = db
                    .find(new LoanApplicationQuery().addEquals("id", id).addSelect("applicationName", "fileList"));
            assertNotNull(foundLoanApplication);
            assertEquals("weeklyLoanApplication", foundLoanApplication.getApplicationName());
            assertNull(foundLoanApplication.getAmount());

            assertNotNull(foundLoanApplication.getFileList());
            assertEquals(2, foundLoanApplication.getFileList().size());

            FileAttachment fileAttach = foundLoanApplication.getFileList().get(0);
            assertEquals("incoporationCert", fileAttach.getCode());
            assertEquals("Incorporation Certificate", fileAttach.getDescription());

            fileAttach = foundLoanApplication.getFileList().get(1);
            assertEquals("tinCert", fileAttach.getCode());
            assertEquals("TIN Certificate", fileAttach.getDescription());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllRecordsWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            db.create(loanApplication);

            List<LoanApplication> list = db.findAll(new LoanApplicationQuery().ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals("weeklyLoanApplication", list.get(0).getApplicationName());
            assertEquals(BigDecimal.valueOf(25.5).setScale(2), list.get(0).getAmount());

            assertNull(list.get(0).getFileList());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindAllWithChildrenWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            db.create(loanApplication);

            List<LoanApplication> list = db.findAllWithChildren(new LoanApplicationQuery().ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals("weeklyLoanApplication", list.get(0).getApplicationName());
            assertEquals(BigDecimal.valueOf(25.5).setScale(2), list.get(0).getAmount());

            List<FileAttachment> parameterList = list.get(0).getFileList();
            assertNotNull(parameterList);
            assertEquals(2, parameterList.size());
            FileAttachment rp = parameterList.get(0);
            assertNotNull(rp);
            assertEquals("incoporationCert", rp.getCode());
            assertEquals("Incorporation Certificate", rp.getDescription());

            rp = parameterList.get(1);
            assertNotNull(rp);
            assertEquals("tinCert", rp.getCode());
            assertEquals("TIN Certificate", rp.getDescription());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testFindChildren() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long id = (Long) db.create(loanApplication);

            LoanApplication foundLoanApplication = db.findLean(new LoanApplicationQuery().addEquals("id", id));
            assertNotNull(foundLoanApplication);
            assertEquals("weeklyLoanApplication", foundLoanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(25.5).setScale(2), foundLoanApplication.getAmount());

            assertNull(foundLoanApplication.getFileList());

            db.findChildren(foundLoanApplication);
            assertEquals("weeklyLoanApplication", foundLoanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(25.5).setScale(2), foundLoanApplication.getAmount());
            assertNotNull(foundLoanApplication.getFileList());
            assertEquals(2, foundLoanApplication.getFileList().size());

            FileAttachment fileAttach = foundLoanApplication.getFileList().get(0);
            assertEquals("incoporationCert", fileAttach.getCode());
            assertEquals("Incorporation Certificate", fileAttach.getDescription());

            fileAttach = foundLoanApplication.getFileList().get(1);
            assertEquals("tinCert", fileAttach.getCode());
            assertEquals("TIN Certificate", fileAttach.getDescription());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListLeanRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long id = (Long) db.create(loanApplication);

            LoanApplication foundLoanApplication = db.listLean(LoanApplication.class, id);
            assertNotNull(foundLoanApplication);
            assertEquals("weeklyLoanApplication", foundLoanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(25.5).setScale(2), foundLoanApplication.getAmount());

            assertNull(foundLoanApplication.getFileList());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long id = (Long) db.create(loanApplication);

            LoanApplication foundLoanApplication = db.list(LoanApplication.class, id);
            assertNotNull(foundLoanApplication);
            assertEquals("weeklyLoanApplication", foundLoanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(25.5).setScale(2), foundLoanApplication.getAmount());

            assertNotNull(foundLoanApplication.getFileList());
            assertEquals(2, foundLoanApplication.getFileList().size());

            FileAttachment fileAttach = foundLoanApplication.getFileList().get(0);
            assertEquals("incoporationCert", fileAttach.getCode());
            assertEquals("Incorporation Certificate", fileAttach.getDescription());

            fileAttach = foundLoanApplication.getFileList().get(1);
            assertEquals("tinCert", fileAttach.getCode());
            assertEquals("TIN Certificate", fileAttach.getDescription());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListLeanRecordByCriteriaWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long id = (Long) db.create(loanApplication);

            LoanApplication foundLoanApplication = db.listLean(new LoanApplicationQuery().addEquals("id", id));
            assertNotNull(foundLoanApplication);
            assertEquals("weeklyLoanApplication", foundLoanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(25.5).setScale(2), foundLoanApplication.getAmount());

            assertNull(foundLoanApplication.getFileList());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByCriteriaWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long id = (Long) db.create(loanApplication);

            LoanApplication foundLoanApplication = db.list(new LoanApplicationQuery().addEquals("id", id));
            assertNotNull(foundLoanApplication);
            assertEquals("weeklyLoanApplication", foundLoanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(25.5).setScale(2), foundLoanApplication.getAmount());

            assertNotNull(foundLoanApplication.getFileList());
            assertEquals(2, foundLoanApplication.getFileList().size());

            FileAttachment fileAttach = foundLoanApplication.getFileList().get(0);
            assertEquals("incoporationCert", fileAttach.getCode());
            assertEquals("Incorporation Certificate", fileAttach.getDescription());

            fileAttach = foundLoanApplication.getFileList().get(1);
            assertEquals("tinCert", fileAttach.getCode());
            assertEquals("TIN Certificate", fileAttach.getDescription());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListRecordByCriteriaWithSelectChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long id = (Long) db.create(loanApplication);

            LoanApplication foundLoanApplication = db
                    .list(new LoanApplicationQuery().addEquals("id", id).addSelect("fileList"));
            assertNotNull(foundLoanApplication);
            assertNull(foundLoanApplication.getApplicationName());
            assertNull(foundLoanApplication.getAmount());

            assertNotNull(foundLoanApplication.getFileList());
            assertEquals(2, foundLoanApplication.getFileList().size());

            FileAttachment fileAttach = foundLoanApplication.getFileList().get(0);
            assertEquals("incoporationCert", fileAttach.getCode());
            assertEquals("Incorporation Certificate", fileAttach.getDescription());

            fileAttach = foundLoanApplication.getFileList().get(1);
            assertEquals("tinCert", fileAttach.getCode());
            assertEquals("TIN Certificate", fileAttach.getDescription());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllRecordsWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            db.create(loanApplication);

            List<LoanApplication> list = db.listAll(new LoanApplicationQuery().ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals("weeklyLoanApplication", list.get(0).getApplicationName());
            assertEquals(BigDecimal.valueOf(25.5).setScale(2), list.get(0).getAmount());

            assertNull(list.get(0).getFileList());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListAllWithChildrenWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            db.create(loanApplication);

            List<LoanApplication> list = db.listAllWithChildren(new LoanApplicationQuery().ignoreEmptyCriteria(true));
            assertNotNull(list);
            assertEquals("weeklyLoanApplication", list.get(0).getApplicationName());
            assertEquals(BigDecimal.valueOf(25.5).setScale(2), list.get(0).getAmount());

            List<FileAttachment> parameterList = list.get(0).getFileList();
            assertNotNull(parameterList);
            assertEquals(2, parameterList.size());
            FileAttachment rp = parameterList.get(0);
            assertNotNull(rp);
            assertEquals("incoporationCert", rp.getCode());
            assertEquals("Incorporation Certificate", rp.getDescription());

            rp = parameterList.get(1);
            assertNotNull(rp);
            assertEquals("tinCert", rp.getCode());
            assertEquals("TIN Certificate", rp.getDescription());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testListChildren() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long id = (Long) db.create(loanApplication);

            LoanApplication foundLoanApplication = db.findLean(new LoanApplicationQuery().addEquals("id", id));
            assertNotNull(foundLoanApplication);
            assertEquals("weeklyLoanApplication", foundLoanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(25.5).setScale(2), foundLoanApplication.getAmount());

            assertNull(foundLoanApplication.getFileList());

            db.listChildren(foundLoanApplication);
            assertEquals("weeklyLoanApplication", foundLoanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(25.5).setScale(2), foundLoanApplication.getAmount());
            assertNotNull(foundLoanApplication.getFileList());
            assertEquals(2, foundLoanApplication.getFileList().size());

            FileAttachment fileAttach = foundLoanApplication.getFileList().get(0);
            assertEquals("incoporationCert", fileAttach.getCode());
            assertEquals("Incorporation Certificate", fileAttach.getDescription());

            fileAttach = foundLoanApplication.getFileList().get(1);
            assertEquals("tinCert", fileAttach.getCode());
            assertEquals("TIN Certificate", fileAttach.getDescription());
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdWithNoChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.setFileList(new ArrayList<FileAttachment>());
            Long id = (Long) db.create(loanApplication);
            db.delete(LoanApplication.class, id);
            assertEquals(0, db.countAll(new LoanApplicationQuery().addEquals("id", id)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdWithNullChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.setFileList(null);
            Long id = (Long) db.create(loanApplication);
            db.delete(LoanApplication.class, id);
            assertEquals(0, db.countAll(new LoanApplicationQuery().addEquals("id", id)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"))
                    .addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long id = (Long) db.create(loanApplication);
            db.delete(LoanApplication.class, id);
            assertEquals(0, db.countAll(new LoanApplicationQuery().addEquals("id", id)));
            assertEquals(0, db.countAll(new FileAttachmentQuery().ignoreEmptyCriteria(true)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteRecordByIdVersionWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"));
            loanApplication.addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long id = (Long) db.create(loanApplication);
            loanApplication = db.find(LoanApplication.class, id);
            db.deleteByIdVersion(loanApplication);
            assertEquals(0, db.countAll(new LoanApplicationQuery().addEquals("id", id)));
            assertEquals(0, db.countAll(new FileAttachmentQuery().ignoreEmptyCriteria(true)));
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteAllRecordWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"));
            loanApplication.addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            db.create(loanApplication);
            db.deleteAll(new LoanApplicationQuery().ignoreEmptyCriteria(true));

            int count = db.countAll(new LoanApplicationQuery().ignoreEmptyCriteria(true));
            assertEquals(0, count);

            count = db.countAll(new FileAttachmentQuery().ignoreEmptyCriteria(true));
            assertEquals(0, count);
        } finally {
            tm.endTransaction();
        }
    }

    @Test
    public void testDeleteSingleFromMultipleRecordsWithChildList() throws Exception {
        tm.beginTransaction();
        try {
            LoanApplication loanApplication = new LoanApplication("weeklyLoanApplication", BigDecimal.valueOf(25.5).setScale(2));
            loanApplication.addAttachment(new FileAttachment("incoporationCert", "Incorporation Certificate"));
            loanApplication.addAttachment(new FileAttachment("tinCert", "TIN Certificate"));
            Long id1 = (Long) db.create(loanApplication);

            loanApplication = new LoanApplication("salaryLoanApplication", BigDecimal.valueOf(70.32));
            loanApplication.addAttachment(new FileAttachment("birthCert", "Birth Certificate"));
            Long id2 = (Long) db.create(loanApplication);

            db.deleteAll(new LoanApplicationQuery().addEquals("id", id1));

            int count = db.countAll(new LoanApplicationQuery().ignoreEmptyCriteria(true));
            assertEquals(1, count);

            loanApplication = db.find(LoanApplication.class, id2);
            assertNotNull(loanApplication);
            assertEquals("salaryLoanApplication", loanApplication.getApplicationName());
            assertEquals(BigDecimal.valueOf(70.32), loanApplication.getAmount());
            assertEquals(1, loanApplication.getFileList().size());
            assertEquals("birthCert", loanApplication.getFileList().get(0).getCode());
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
        deleteAll(LoanApplication.class, FileAttachment.class);
    }
}
