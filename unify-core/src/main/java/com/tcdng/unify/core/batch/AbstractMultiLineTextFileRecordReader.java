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

import java.io.BufferedReader;
import java.io.IOException;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.business.BusinessLogicInput;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Convenient base class for multi-line text file record reader.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractMultiLineTextFileRecordReader extends AbstractBatchFileReader {

    private BatchFileConfig batchFileConfig;

    private BufferedReader reader;

    private int entryCounter;

    @Override
    public void open(BusinessLogicInput input, BatchFileConfig configuration, Object[] file) throws UnifyException {
        batchFileConfig = configuration;
        reader = IOUtils.detectAndOpenBufferedReader(file[0]);
        if (configuration.isSkipFirstRecord()) {
            nextEntry();
        }
    }

    @Override
    public void close() {
        IOUtils.close(reader);
        reader = null;
    }

    @Override
    public boolean readNextRecord(ValueStore recordStore) throws UnifyException {
        String[] splitRecord = readNextRecord();
        if (splitRecord != null) {
            int index = 0;
            for (BatchFileFieldConfig fieldConfig : batchFileConfig.getFieldConfigs()) {
                Formatter<?> formatter = null;
                if (fieldConfig.isFormatter()) {
                    formatter = getApplicationLocaleFormatter(fieldConfig.getFormatter());
                }

                recordStore.store(fieldConfig.getFieldName(), splitRecord[index++], formatter);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean skipNextRecord() throws UnifyException {
        return readNextRecord() != null;
    }

    protected BatchFileConfig getBatchFileConfig() {
        return batchFileConfig;
    }

    protected int getEntryCounter() {
        return entryCounter;
    }

    protected abstract String[] parseEntry(String entry) throws UnifyException;

    private String[] readNextRecord() throws UnifyException {
        String line = nextEntry();
        if (line != null) {
            entryCounter++;
            return parseEntry(line);
        }

        return null;
    }

    private String nextEntry() throws UnifyException {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throwOperationErrorException(e);
        }
        return null;
    }
}
