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

/**
 * Batch file reader utilities for tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class BatchFileReaderTestUtils {

    private BatchFileReaderTestUtils() {

    }

    public static BatchFileConfig createSampleFixedLengthBatchConfig(boolean trim) throws Exception {
        BatchFileConfig fileBulkConfig = new BatchFileConfig();
        fileBulkConfig.setReader("fixedlength-batchfilereader");
        fileBulkConfig.addFieldConfig("accountNo", 10, trim);
        fileBulkConfig.addFieldConfig("beneficiary", 20, trim);
        fileBulkConfig.addFieldConfig("currency", 3, trim);
        fileBulkConfig.addFieldConfig("amount", 13, trim);
        return fileBulkConfig;
    }

    public static BatchFileConfig createSampleDelimitedFileBatchConfig(boolean trim) throws Exception {
        BatchFileConfig fileBulkConfig = new BatchFileConfig();
        fileBulkConfig.setReader("delimited-batchfilereader");
        fileBulkConfig.addFieldConfig("accountNo", 10, trim);
        fileBulkConfig.addFieldConfig("beneficiary", 20, trim);
        fileBulkConfig.addFieldConfig("currency", 3, trim);
        fileBulkConfig.addFieldConfig("amount", 13, trim);
        return fileBulkConfig;
    }
}
