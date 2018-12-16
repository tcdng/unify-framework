/*
 * Copyright 2018 The Code Department
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.business.BusinessLogicInput;
import com.tcdng.unify.core.constant.PadDirection;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.data.ValueStoreFactory;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Delimited fields batch file reader tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class DelimitedBatchFileReaderTest extends AbstractUnifyComponentTest {

    @Test
    public void testOpenBatchFileReader() throws Exception {
        BatchFileReader reader = null;
        try {
            BatchFileConfig batchFileConfig = BatchFileReaderTestUtils.createSampleDelimitedFileBatchConfig(true);
            byte[][] fileObject = new byte[1][];
            fileObject[0] = IOUtils.createInMemoryTextFile("0123456789,Abel Turner,NGN,200.00",
                    "6758495839,Bamanga Tukur,NGN,520.00");
            reader = (BatchFileReader) getComponent("delimited-batchfilereader");
            reader.open(getBusinessLogicInput(), batchFileConfig, fileObject);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Test
    public void testReadEmptyFile() throws Exception {
        BatchFileReader reader = null;
        try {
            BatchFileConfig batchFileConfig = BatchFileReaderTestUtils.createSampleDelimitedFileBatchConfig(true);
            byte[][] fileObject = new byte[1][];
            fileObject[0] = IOUtils.createInMemoryTextFile();
            reader = (BatchFileReader) getComponent("delimited-batchfilereader");
            reader.open(getBusinessLogicInput(), batchFileConfig, fileObject);

            boolean read = reader.readNextRecord(getValueStore(new TestBatchItemRecordB()));
            assertFalse(read);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Test
    public void testReadNextRecordWithTrimmingOn() throws Exception {
        BatchFileReader reader = null;
        try {
            BatchFileConfig batchFileConfig = BatchFileReaderTestUtils.createSampleDelimitedFileBatchConfig(true); // Trimming
            // on
            byte[][] fileObject = new byte[1][];
            fileObject[0] = IOUtils.createInMemoryTextFile("0123456789,Abel Turner,NGN,200.00",
                    "6758495839,Bamanga Tukur,NGN,520.00");
            reader = (BatchFileReader) getComponent("delimited-batchfilereader");
            reader.open(getBusinessLogicInput(), batchFileConfig, fileObject);

            TestBatchItemRecordB batchItemRecord = new TestBatchItemRecordB();
            ValueStore store = getValueStore(batchItemRecord);
            boolean read = reader.readNextRecord(store);
            assertTrue(read);
            assertEquals("0123456789", batchItemRecord.getAccountNo());
            assertEquals("Abel Turner", batchItemRecord.getBeneficiary());
            assertEquals("NGN", batchItemRecord.getCurrency());
            assertEquals(Double.valueOf(200.00), batchItemRecord.getAmount());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Test
    public void testReadNextRecordWithTrimmingOff() throws Exception {
        BatchFileReader reader = null;
        try {
            BatchFileConfig batchFileConfig = BatchFileReaderTestUtils.createSampleDelimitedFileBatchConfig(false); // Trimming
            // off
            byte[][] fileObject = new byte[1][];
            fileObject[0] = IOUtils.createInMemoryTextFile("0123456789,Abel Turner         ,NGN,20000",
                    "6758495839,Bamanga Tukur       ,NGN,52000");
            reader = (BatchFileReader) getComponent("delimited-batchfilereader");
            reader.open(getBusinessLogicInput(), batchFileConfig, fileObject);

            TestBatchItemRecordB batchItemRecord = new TestBatchItemRecordB();
            ValueStore store = getValueStore(batchItemRecord);
            boolean read = reader.readNextRecord(store);
            assertTrue(read);
            assertEquals("0123456789", batchItemRecord.getAccountNo());
            assertEquals("Abel Turner         ", batchItemRecord.getBeneficiary());
            assertEquals("NGN", batchItemRecord.getCurrency());
            assertEquals(Double.valueOf(20000), batchItemRecord.getAmount());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Test
    public void testReadNextRecordMultiple() throws Exception {
        BatchFileReader reader = null;
        try {
            BatchFileConfig batchFileConfig = BatchFileReaderTestUtils.createSampleDelimitedFileBatchConfig(true); // Trimming
            // on
            byte[][] fileObject = new byte[1][];
            fileObject[0] = IOUtils.createInMemoryTextFile("0123456789,Abel Turner,NGN,200.00",
                    "6758495839,Bamanga Tukur,NGN,520.00");
            reader = (BatchFileReader) getComponent("delimited-batchfilereader");
            reader.open(getBusinessLogicInput(), batchFileConfig, fileObject);

            TestBatchItemRecordB batchItemRecord = new TestBatchItemRecordB();
            ValueStore store = getValueStore(batchItemRecord);
            boolean read = reader.readNextRecord(store);
            assertTrue(read);
            assertEquals("0123456789", batchItemRecord.getAccountNo());
            assertEquals("Abel Turner", batchItemRecord.getBeneficiary());
            assertEquals("NGN", batchItemRecord.getCurrency());
            assertEquals(Double.valueOf(200.00), batchItemRecord.getAmount());

            read = reader.readNextRecord(store);
            assertTrue(read);
            assertEquals("6758495839", batchItemRecord.getAccountNo());
            assertEquals("Bamanga Tukur", batchItemRecord.getBeneficiary());
            assertEquals("NGN", batchItemRecord.getCurrency());
            assertEquals(Double.valueOf(520.00), batchItemRecord.getAmount());

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Test
    public void testReadNextRecordMultipleWithQuotedValues() throws Exception {
        BatchFileReader reader = null;
        try {
            BatchFileConfig batchFileConfig = BatchFileReaderTestUtils.createSampleDelimitedFileBatchConfig(true);
            byte[][] fileObject = new byte[1][];
            fileObject[0] = IOUtils.createInMemoryTextFile("\"0123456789\",\"Abel Turner\",\"NGN\",\"200.00\"",
                    "6758495839,\"Bamanga Tukur, Kano\",NGN,520.00");
            reader = (BatchFileReader) getComponent("delimited-batchfilereader");
            reader.open(getBusinessLogicInput(), batchFileConfig, fileObject);

            TestBatchItemRecordB batchItemRecord = new TestBatchItemRecordB();
            ValueStore store = getValueStore(batchItemRecord);
            boolean read = reader.readNextRecord(store);
            assertTrue(read);
            assertEquals("0123456789", batchItemRecord.getAccountNo());
            assertEquals("Abel Turner", batchItemRecord.getBeneficiary());
            assertEquals("NGN", batchItemRecord.getCurrency());
            assertEquals(Double.valueOf(200.00), batchItemRecord.getAmount());

            read = reader.readNextRecord(store);
            assertTrue(read);
            assertEquals("6758495839", batchItemRecord.getAccountNo());
            assertEquals("Bamanga Tukur, Kano", batchItemRecord.getBeneficiary());
            assertEquals("NGN", batchItemRecord.getCurrency());
            assertEquals(Double.valueOf(520.00), batchItemRecord.getAmount());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Test
    public void testReadNextRecordWithFormatter() throws Exception {
        BatchFileReader reader = null;
        try {
            BatchFileConfig batchFileConfig = BatchFileReaderTestUtils.createSampleDelimitedFileBatchConfig(true);
            batchFileConfig.addFieldConfig("amount", null, "!centformat", PadDirection.LEFT, 13, false, false, true,
                    '0');
            byte[][] fileObject = new byte[1][];
            fileObject[0] = IOUtils.createInMemoryTextFile("0123456789,Abel Turner,NGN,200000",
                    "6758495839,Bamanga Tukur,NGN,52043");
            reader = (BatchFileReader) getComponent("delimited-batchfilereader");
            reader.open(getBusinessLogicInput(), batchFileConfig, fileObject);

            TestBatchItemRecordB batchItemRecord = new TestBatchItemRecordB();
            ValueStore store = getValueStore(batchItemRecord);
            boolean read = reader.readNextRecord(store);
            assertTrue(read);
            assertEquals("0123456789", batchItemRecord.getAccountNo());
            assertEquals("Abel Turner", batchItemRecord.getBeneficiary());
            assertEquals("NGN", batchItemRecord.getCurrency());
            assertEquals(Double.valueOf(2000.00), batchItemRecord.getAmount());

            read = reader.readNextRecord(store);
            assertTrue(read);
            assertEquals("6758495839", batchItemRecord.getAccountNo());
            assertEquals("Bamanga Tukur", batchItemRecord.getBeneficiary());
            assertEquals("NGN", batchItemRecord.getCurrency());
            assertEquals(Double.valueOf(520.43), batchItemRecord.getAmount());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Test
    public void testReadNextRecordTillEof() throws Exception {
        BatchFileReader reader = null;
        try {
            BatchFileConfig batchFileConfig = BatchFileReaderTestUtils.createSampleDelimitedFileBatchConfig(true);
            byte[][] fileObject = new byte[1][];
            fileObject[0] = IOUtils.createInMemoryTextFile("0123456789,Abel Turner,NGN,200000",
                    "6758495839,Bamanga Tukur,NGN,52043");
            reader = (BatchFileReader) getComponent("delimited-batchfilereader");
            reader.open(getBusinessLogicInput(), batchFileConfig, fileObject);

            TestBatchItemRecordB batchItemRecord = new TestBatchItemRecordB();
            ValueStore store = getValueStore(batchItemRecord);
            boolean read = reader.readNextRecord(store);
            assertTrue(read);
            read = reader.readNextRecord(store);
            assertTrue(read);
            read = reader.readNextRecord(store);
            assertFalse(read);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }

    private BusinessLogicInput getBusinessLogicInput() {
        BusinessLogicInput input = new BusinessLogicInput();
        input.setParameter(DelimitedBatchFileReaderInputConstants.FIELDDELIMITER, FieldDelimiterType.COMMA);
        return input;
    }

    private ValueStore getValueStore(Object record) throws Exception {
        return ((ValueStoreFactory) getComponent(ApplicationComponents.APPLICATION_VALUESTOREFACTORY))
                .getValueStore(record, 0);
    }
}
