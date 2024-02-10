/*
 * Copyright 2018-2024 The Code Department.
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

import com.tcdng.unify.core.constant.PadDirection;

/**
 * Batch file reader utilities for tests.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class BatchFileReaderTestUtils {

    private BatchFileReaderTestUtils() {

    }

    public static BatchFileReadConfig createSampleFixedLengthBatchConfig(ConstraintAction cAction, boolean trim,
            boolean formattedAmt) throws Exception {
        BatchFileReadConfig.Builder bb =
                BatchFileReadConfig.newBuilder().reader("fixedlength-batchfilereader").addFieldConfig("accountNo", 10, trim)
                        .addFieldConfig("beneficiary", 20, trim).addFieldConfig("currency", 3, trim);
        if (formattedAmt) {
            bb.addFieldConfig("amount", null, "!centformat", PadDirection.LEFT, 13, false, false, true, '0');
        } else {
            bb.addFieldConfig("amount", 13, trim);
        }

        if (cAction != null) {
            bb.onConstraint(cAction);
        }
        return bb.build();
    }

    public static BatchFileReadConfig createSampleDelimitedFileBatchConfig(ConstraintAction cAction, boolean trim,
            boolean formattedAmt) throws Exception {
        BatchFileReadConfig.Builder bb =
                BatchFileReadConfig.newBuilder().reader("delimited-batchfilereader").addFieldConfig("accountNo", 10, trim)
                        .addFieldConfig("beneficiary", 20, trim).addFieldConfig("currency", 3, trim);
        if (formattedAmt) {
            bb.addFieldConfig("amount", null, "!centformat", PadDirection.LEFT, 13, false, false, true, '0');
        } else {
            bb.addFieldConfig("amount", 13, trim);
        }

        if (cAction != null) {
            bb.onConstraint(cAction);
        }

        bb.addParam(DelimitedBatchFileReaderInputConstants.FIELDDELIMITER, FieldDelimiterType.COMMA);
        return bb.build();
    }
}
