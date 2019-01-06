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
package com.tcdng.unify.core.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.TestTaskMonitor;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.business.BusinessLogicInput;
import com.tcdng.unify.core.business.BusinessLogicOutput;
import com.tcdng.unify.core.business.BusinessLogicUnit;
import com.tcdng.unify.core.database.Database;
import com.tcdng.unify.core.database.DatabaseTransactionManager;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Batch item reader unit tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DBBatchItemFileReadProcessorTest extends AbstractUnifyComponentTest {

    private Database db;

    private DatabaseTransactionManager tm;

    @SuppressWarnings("unchecked")
    @Test
    public void testBatchItemProcessing() throws Exception {
        // Setup parameters
        BusinessLogicInput input = new BusinessLogicInput(new TestTaskMonitor(), db.getName());
        BatchFileConfig fileBulkConfig = BatchFileReaderTestUtils.createSampleFixedLengthBatchConfig(true);
        byte[][] fileObject = new byte[1][];
        fileObject[0] = IOUtils.createInMemoryTextFile("0123456789Abel Turner         NGN0000000020000",
                "6758495839Bamanga Tukur       NGN0000000052000");
        input.setParameter(BatchFileReadProcessorInputConstants.BATCHFILECONFIG, fileBulkConfig);
        input.setParameter(BatchFileReadProcessorInputConstants.FILEOBJECTS, fileObject);

        // Perform batch file processing and do some assertions
        BusinessLogicOutput output = new BusinessLogicOutput();
        BusinessLogicUnit blu = (BusinessLogicUnit) getComponent("test-batchfileprocessor-a");
        tm.beginTransaction();
        try {
            blu.execute(input, output);
        } finally {
            tm.endTransaction();
        }

        List<Object> result = output.getResult(List.class, BatchFileReadProcessorOutputConstants.BATCHFILEREADRESULT);
        assertNotNull(result);
        assertEquals(2, result.size());

        // Assert persistent records are created
        List<TestBatchItemRecordA> batchItemList = null;
        tm.beginTransaction();
        try {
            batchItemList = db.findAll(
                    new Query<TestBatchItemRecordA>(TestBatchItemRecordA.class).amongst("id", result).order("id"));
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
        BusinessLogicInput input = new BusinessLogicInput(new TestTaskMonitor(), db.getName());
        BatchFileConfig fileBulkConfig = BatchFileReaderTestUtils.createSampleFixedLengthBatchConfig(true);
        byte[][] fileObject = new byte[1][];
        fileObject[0] = IOUtils.createInMemoryTextFile("0123456789Abel Turner         NGN0000000020000",
                "6758495839Bamanga Tukur       NGN0000000052000", "6758495839William Thomas TutteNGN0000000040000");
        input.setParameter(BatchFileReadProcessorInputConstants.BATCHFILECONFIG, fileBulkConfig);
        input.setParameter(BatchFileReadProcessorInputConstants.FILEOBJECTS, fileObject);

        // Perform batch file processing and do some assertions
        BusinessLogicOutput output = new BusinessLogicOutput();
        BusinessLogicUnit blu = (BusinessLogicUnit) getComponent("test-batchfileprocessor-a");
        tm.beginTransaction();
        try {
            blu.execute(input, output);
        } finally {
            tm.endTransaction();
        }

        List<Object> result = output.getResult(List.class, BatchFileReadProcessorOutputConstants.BATCHFILEREADRESULT);
        assertNotNull(result);
        assertEquals(2, result.size());

        // Assert persistent records are created
        List<TestBatchItemRecordA> batchItemList = null;
        tm.beginTransaction();
        try {
            batchItemList = db.findAll(
                    new Query<TestBatchItemRecordA>(TestBatchItemRecordA.class).amongst("id", result).order("id"));
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
        BusinessLogicInput input = new BusinessLogicInput(new TestTaskMonitor(), db.getName());
        BatchFileConfig fileBulkConfig = BatchFileReaderTestUtils.createSampleFixedLengthBatchConfig(true);
        fileBulkConfig.setOnConstraint(ConstraintAction.UPDATE);
        byte[][] fileObject = new byte[1][];
        fileObject[0] = IOUtils.createInMemoryTextFile("0123456789Abel Turner         NGN0000000020000",
                "6758495839Bamanga Tukur       NGN0000000052000", "6758495839William Thomas TutteGBP0000000040000");
        input.setParameter(BatchFileReadProcessorInputConstants.BATCHFILECONFIG, fileBulkConfig);
        input.setParameter(BatchFileReadProcessorInputConstants.FILEOBJECTS, fileObject);

        // Perform batch file processing and do some assertions
        BusinessLogicOutput output = new BusinessLogicOutput();
        BusinessLogicUnit blu = (BusinessLogicUnit) getComponent("test-batchfileprocessor-a");
        tm.beginTransaction();
        try {
            blu.execute(input, output);
        } finally {
            tm.endTransaction();
        }

        List<Object> result =
                (List<Object>) output.getResult(List.class, BatchFileReadProcessorOutputConstants.BATCHFILEREADRESULT);
        assertNotNull(result);
        assertEquals(2, result.size());

        // Assert persistent records are created
        List<TestBatchItemRecordA> batchItemList = null;
        tm.beginTransaction();
        try {
            batchItemList = db.findAll(
                    new Query<TestBatchItemRecordA>(TestBatchItemRecordA.class).amongst("id", result).order("id"));
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
        BusinessLogicInput input = new BusinessLogicInput(new TestTaskMonitor(), db.getName());
        BatchFileConfig fileBulkConfig = BatchFileReaderTestUtils.createSampleFixedLengthBatchConfig(true);
        fileBulkConfig.setOnConstraint(ConstraintAction.FAIL);
        byte[][] fileObject = new byte[1][];
        fileObject[0] = IOUtils.createInMemoryTextFile("0123456789Abel Turner         NGN0000000020000",
                "6758495839Bamanga Tukur       NGN0000000052000", "6758495839William Thomas TutteGBP0000000040000");
        input.setParameter(BatchFileReadProcessorInputConstants.BATCHFILECONFIG, fileBulkConfig);
        input.setParameter(BatchFileReadProcessorInputConstants.FILEOBJECTS, fileObject);

        // Perform batch file processing and do some assertions
        BusinessLogicUnit blu = (BusinessLogicUnit) getComponent("test-batchfileprocessor-a");
        tm.beginTransaction();
        try {
            blu.execute(input, new BusinessLogicOutput());
        } finally {
            tm.endTransaction();
        }
    }

    @Override
    protected void onSetup() throws Exception {
        db = (Database) getComponent(ApplicationComponents.APPLICATION_DATABASE);
        tm = (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASE);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    protected void onTearDown() throws Exception {
        deleteAll(TestBatchItemRecordA.class, TestBatchItemRecordB.class, TestBatchRecordB.class);
    }
}
