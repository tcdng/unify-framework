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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;

/**
 * Fixed length batch file reader.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component(name = "fixedlength-batchfilereader", description = "$m{batchfilereader.fixedlength}")
public class FixedLengthBatchFileReader extends AbstractMultiLineTextFileRecordReader {

    @Override
    protected String[] parseEntry(String entry) throws UnifyException {
        String[] result = new String[getBatchFileConfig().getFieldConfigList().size()];
        logDebug("Parsing fixed length [{0}] in line number = {1}", entry, getEntryCounter());
        int index = 0;
        int beginIndex = 0;
        for (BatchFileFieldConfig fieldConfig : getBatchFileConfig().getFieldConfigList()) {
            int endIndex = beginIndex + fieldConfig.getLength();
            result[index] = entry.substring(beginIndex, endIndex);

            if (fieldConfig.isTrim()) {
                result[index] = result[index].trim();
            }

            beginIndex = endIndex;
            index++;
        }
        return result;
    }
}
