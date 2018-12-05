/*
 * Copyright 2014 The Code Department
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
import com.tcdng.unify.core.constant.PadDirection;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.data.ValueStoreFactory;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Fixed length batch file reader tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class FixedLengthBatchFileReaderTest extends AbstractUnifyComponentTest {

	@Test
	public void testOpenBatchFileReader() throws Exception {
		BatchFileReader reader = null;
		try {
			BatchFileConfig fileBulkConfig = BatchFileReaderTestUtils.createSampleFixedLengthBatchConfig(true);
			byte[][] fileObject = new byte[1][];
			fileObject[0] = IOUtils.createInMemoryTextFile("0123456789Abel Turner         NGN0000000020000",
					"6758495839Bamanga Tukur       NGN0000000052000");
			reader = (BatchFileReader) this.getComponent("fixedlength-batchfilereader");
			reader.open(null, fileBulkConfig, fileObject);
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
			BatchFileConfig fileBulkConfig = BatchFileReaderTestUtils.createSampleFixedLengthBatchConfig(true);
			byte[][] fileObject = new byte[1][];
			fileObject[0] = IOUtils.createInMemoryTextFile();
			reader = (BatchFileReader) this.getComponent("fixedlength-batchfilereader");
			reader.open(null, fileBulkConfig, fileObject);

			boolean read = reader.readNextRecord(this.getValueStore(new TestBatchItemRecordB()));
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
			BatchFileConfig fileBulkConfig = BatchFileReaderTestUtils.createSampleFixedLengthBatchConfig(true);
			byte[][] fileObject = new byte[1][];
			fileObject[0] = IOUtils.createInMemoryTextFile("0123456789Abel Turner         NGN0000000020000",
					"6758495839Bamanga Tukur       NGN0000000052000");
			reader = (BatchFileReader) this.getComponent("fixedlength-batchfilereader");
			reader.open(null, fileBulkConfig, fileObject);

			TestBatchItemRecordB batchItemRecord = new TestBatchItemRecordB();
			ValueStore store = this.getValueStore(batchItemRecord);
			boolean read = reader.readNextRecord(store);
			assertTrue(read);
			assertEquals("0123456789", batchItemRecord.getAccountNo());
			assertEquals("Abel Turner", batchItemRecord.getBeneficiary());
			assertEquals("NGN", batchItemRecord.getCurrency());
			assertEquals(Double.valueOf(20000), batchItemRecord.getAmount());
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
			BatchFileConfig fileBulkConfig = BatchFileReaderTestUtils.createSampleFixedLengthBatchConfig(false);
			byte[][] fileObject = new byte[1][];
			fileObject[0] = IOUtils.createInMemoryTextFile("0123456789Abel Turner         NGN0000000020000",
					"6758495839Bamanga Tukur       NGN0000000052000");
			reader = (BatchFileReader) this.getComponent("fixedlength-batchfilereader");
			reader.open(null, fileBulkConfig, fileObject);

			TestBatchItemRecordB batchItemRecord = new TestBatchItemRecordB();
			ValueStore store = this.getValueStore(batchItemRecord);
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
			BatchFileConfig fileBulkConfig = BatchFileReaderTestUtils.createSampleFixedLengthBatchConfig(true);
			byte[][] fileObject = new byte[1][];
			fileObject[0] = IOUtils.createInMemoryTextFile("0123456789Abel Turner         NGN0000000020000",
					"6758495839Bamanga Tukur       NGN0000000052000");
			reader = (BatchFileReader) this.getComponent("fixedlength-batchfilereader");
			reader.open(null, fileBulkConfig, fileObject);

			TestBatchItemRecordB batchItemRecord = new TestBatchItemRecordB();
			ValueStore store = this.getValueStore(batchItemRecord);
			boolean read = reader.readNextRecord(store);
			assertTrue(read);
			assertEquals("0123456789", batchItemRecord.getAccountNo());
			assertEquals("Abel Turner", batchItemRecord.getBeneficiary());
			assertEquals("NGN", batchItemRecord.getCurrency());
			assertEquals(Double.valueOf(20000), batchItemRecord.getAmount());

			read = reader.readNextRecord(store);
			assertTrue(read);
			assertEquals("6758495839", batchItemRecord.getAccountNo());
			assertEquals("Bamanga Tukur", batchItemRecord.getBeneficiary());
			assertEquals("NGN", batchItemRecord.getCurrency());
			assertEquals(Double.valueOf(52000), batchItemRecord.getAmount());

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
			BatchFileConfig fileBulkConfig = BatchFileReaderTestUtils.createSampleFixedLengthBatchConfig(true);
			fileBulkConfig.addFieldConfig("amount", null, "!centformat", PadDirection.LEFT, 13, false, false, true,
					'0');

			byte[][] fileObject = new byte[1][];
			fileObject[0] = IOUtils.createInMemoryTextFile("0123456789Abel Turner         NGN0000000020000",
					"6758495839Bamanga Tukur       NGN0000000052043");
			reader = (BatchFileReader) this.getComponent("fixedlength-batchfilereader");
			reader.open(null, fileBulkConfig, fileObject);

			TestBatchItemRecordB batchItemRecord = new TestBatchItemRecordB();
			ValueStore store = this.getValueStore(batchItemRecord);
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
			BatchFileConfig fileBulkConfig = BatchFileReaderTestUtils.createSampleFixedLengthBatchConfig(true);
			byte[][] fileObject = new byte[1][];
			fileObject[0] = IOUtils.createInMemoryTextFile("0123456789Abel Turner         NGN0000000020000",
					"6758495839Bamanga Tukur       NGN0000000052000");
			reader = (BatchFileReader) this.getComponent("fixedlength-batchfilereader");
			reader.open(null, fileBulkConfig, fileObject);

			TestBatchItemRecordB batchItemRecord = new TestBatchItemRecordB();
			ValueStore store = this.getValueStore(batchItemRecord);
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

	private ValueStore getValueStore(Object record) throws Exception {
		return ((ValueStoreFactory) this.getComponent(ApplicationComponents.APPLICATION_VALUESTOREFACTORY))
				.getValueStore(record, 0);
	}
}
