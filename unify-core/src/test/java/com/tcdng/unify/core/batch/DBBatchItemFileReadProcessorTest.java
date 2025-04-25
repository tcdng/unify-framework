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
package com.tcdng.unify.core.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.Database;
import com.tcdng.unify.core.database.DatabaseTransactionManager;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Batch item reader unit tests.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class DBBatchItemFileReadProcessorTest extends AbstractUnifyComponentTest {

    private Database db;

    private DatabaseTransactionManager tm;

    @SuppressWarnings("unchecked")
    @Test
    public void testBatchItemProcessing() throws Exception {
        // Setup parameters
        BatchFileReadConfig batchFileReadConfig = BatchFileReaderTestUtils.createSampleFixedLengthBatchConfig(null, true, false);
        byte[] file = IOUtils.createInMemoryTextFile("0123456789Abel Turner         NGN0000000020000",
                "6758495839Bamanga Tukur       NGN0000000052000");

        // Perform batch file processing and do some assertions
        BatchFileReadProcessor processor = (BatchFileReadProcessor) getComponent("test-batchfileprocessor-a");
        List<Object> result = (List<Object>) processor.process(batchFileReadConfig, file);
        assertNotNull(result);
        assertEquals(2, result.size());

        // Assert persistent records are created
        List<TestBatchItemRecordA> batchItemList = null;
        tm.beginTransaction();
        try {
            batchItemList = db.findAll(
                    Query.of(TestBatchItemRecordA.class).addAmongst("id", result).addOrder("id"));
        } finally {
            tm.endTransaction();
        }

        assertEquals(2, batchItemList.size());

        TestBatchItemRecordA record = batchItemList.get(0);
        assertEquals("0123456789", record.getAccountNo());
        assertEquals("Abel Turner", record.getBeneficiary());
        assertEquals("NGN", record.getCurrency());
        assertEquals(Double.valueOf(20000), record.getAmount());

        record = batchItemList.get(1);
        assertEquals("6758495839", record.getAccountNo());
        assertEquals("Bamanga Tukur", record.getBeneficiary());
        assertEquals("NGN", record.getCurrency());
        assertEquals(Double.valueOf(52000), record.getAmount());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBatchItemProcessingSkipExisting() throws Exception {
        // Setup parameters
        BatchFileReadConfig batchFileReadConfig = BatchFileReaderTestUtils.createSampleFixedLengthBatchConfig(null, true, false);
        byte[] file = IOUtils.createInMemoryTextFile("0123456789Abel Turner         NGN0000000020000",
                "6758495839Bamanga Tukur       NGN0000000052000", "6758495839William Thomas TutteNGN0000000040000");

        // Perform batch file processing and do some assertions
        BatchFileReadProcessor processor = (BatchFileReadProcessor) getComponent("test-batchfileprocessor-a");
        List<Object> result = (List<Object>) processor.process(batchFileReadConfig, file);
        assertNotNull(result);
        assertEquals(2, result.size());

        // Assert persistent records are created
        List<TestBatchItemRecordA> batchItemList = null;
        tm.beginTransaction();
        try {
            batchItemList = db.findAll(
                    Query.of(TestBatchItemRecordA.class).addAmongst("id", result).addOrder("id"));
        } finally {
            tm.endTransaction();
        }

        assertEquals(2, batchItemList.size());

        TestBatchItemRecordA record = batchItemList.get(0);
        assertEquals("0123456789", record.getAccountNo());
        assertEquals("Abel Turner", record.getBeneficiary());
        assertEquals("NGN", record.getCurrency());
        assertEquals(Double.valueOf(20000), record.getAmount());

        record = batchItemList.get(1);
        assertEquals("6758495839", record.getAccountNo());
        assertEquals("Bamanga Tukur", record.getBeneficiary());
        assertEquals("NGN", record.getCurrency());
        assertEquals(Double.valueOf(52000), record.getAmount());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBatchItemProcessingUpdateExisting() throws Exception {
        // Setup parameters
        BatchFileReadConfig batchFileReadConfig = BatchFileReaderTestUtils.createSampleFixedLengthBatchConfig(ConstraintAction.UPDATE, true, false);
        byte[] file = IOUtils.createInMemoryTextFile("0123456789Abel Turner         NGN0000000020000",
                "6758495839Bamanga Tukur       NGN0000000052000", "6758495839William Thomas TutteGBP0000000040000");

        // Perform batch file processing and do some assertions
        BatchFileReadProcessor processor = (BatchFileReadProcessor) getComponent("test-batchfileprocessor-a");
        List<Object> result = (List<Object>) processor.process(batchFileReadConfig, file);
        assertNotNull(result);
        assertEquals(2, result.size());

        // Assert persistent records are created
        List<TestBatchItemRecordA> batchItemList = null;
        tm.beginTransaction();
        try {
            batchItemList = db.findAll(
                    Query.of(TestBatchItemRecordA.class).addAmongst("id", result).addOrder("id"));
        } finally {
            tm.endTransaction();
        }

        assertEquals(2, batchItemList.size());

        TestBatchItemRecordA record = batchItemList.get(0);
        assertEquals("0123456789", record.getAccountNo());
        assertEquals("Abel Turner", record.getBeneficiary());
        assertEquals("NGN", record.getCurrency());
        assertEquals(Double.valueOf(20000), record.getAmount());

        record = batchItemList.get(1); // Entity 2 has been updated with record
                                       // 3
        assertEquals("6758495839", record.getAccountNo());
        assertEquals("William Thomas Tutte", record.getBeneficiary());
        assertEquals("GBP", record.getCurrency());
        assertEquals(Double.valueOf(40000), record.getAmount());
    }

    @Test(expected = UnifyException.class)
    public void testBatchItemProcessingFailOnExisting() throws Exception {
        // Setup parameters
        BatchFileReadConfig batchFileReadConfig = BatchFileReaderTestUtils.createSampleFixedLengthBatchConfig(ConstraintAction.FAIL, true, false);
        byte[] file = IOUtils.createInMemoryTextFile("0123456789Abel Turner         NGN0000000020000",
                "6758495839Bamanga Tukur       NGN0000000052000", "6758495839William Thomas TutteGBP0000000040000");

        // Perform batch file processing and do some assertions
        BatchFileReadProcessor processor = (BatchFileReadProcessor) getComponent("test-batchfileprocessor-a");
        processor.process(batchFileReadConfig, file);
    }

    @Override
    protected void onSetup() throws Exception {
        db = (Database) getComponent(ApplicationComponents.APPLICATION_DATABASE);
        tm = (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    protected void onTearDown() throws Exception {
        deleteAll(TestBatchItemRecordA.class, TestBatchItemRecordB.class, TestBatchRecordB.class);
    }
}
