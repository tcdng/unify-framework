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

import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.business.BusinessLogicInput;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.database.Database;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Convenient base class for database batch file read processor.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractDBBatchFileReadProcessor<T extends BatchRecord, U extends BatchItemRecord>
        extends AbstractBatchFileReadProcessor implements DBBatchFileReadProcessor {

    private Class<T> batchClass;

    private Class<U> batchItemClass;

    public AbstractDBBatchFileReadProcessor(Class<T> batchClass, Class<U> batchItemClass) {
        this.batchClass = batchClass;
        this.batchItemClass = batchItemClass;
    }

    @Override
    protected Object doProcessBatchFile(BusinessLogicInput input, BatchFileConfig batchFileConfig,
            BatchFileReader reader) throws UnifyException {
        TaskMonitor tm = input.getTaskMonitor();
        tm.addMessage("Processing batch file...");
        Map<String, T> batchMap = new HashMap<String, T>();
        // Start batch creation. May result in multiple batches
        // based on finding records belonging to more than one category.
        Database db = getDatabase(input);
        U batchItem = ReflectUtils.newInstance(batchItemClass);
        ValueStore itemStore = getValueStoreFactory().getValueStore(batchItem, 0);
        while (reader.readNextRecord(itemStore)) {
            Object batchId = null;
            String category = getBatchCategory(batchItem);
            T batch = batchMap.get(category);
            if (batch == null) {
                batch = ReflectUtils.newInstance(batchClass);
                preBatchCreate(input, batch, batchItem);
                batch.setCategory(category);
                batchId = db.create(batch);
                batchMap.put(category, batch);
            } else {
                preBatchUpdate(input, batch, batchItem);
                batch.setCategory(category);
                db.updateByIdVersion(batch);
                batchId = batch.getId();
            }

            batchItem.setBatchId(batchId);
            preBatchItemCreate(input, batchItem);
            db.create(batchItem);
            postBatchItemCreate(input, batchItem);
        }

        for (T batch : batchMap.values()) {
            postBatchCreate(input, batch);
        }

        tm.addMessage("Batch file processing completed.");
        tm.addMessage("Summary: batchCount = " + batchMap.size());
        return batchMap;
    }

    protected void preBatchItemCreate(BusinessLogicInput input, U batchItem) throws UnifyException {

    }

    protected void postBatchItemCreate(BusinessLogicInput input, U batchItem) throws UnifyException {

    }

    protected abstract String getBatchCategory(U batchItem) throws UnifyException;

    protected abstract void preBatchCreate(BusinessLogicInput input, T batch, U batchItem) throws UnifyException;

    protected abstract void preBatchUpdate(BusinessLogicInput input, T batch, U batchItem) throws UnifyException;

    protected abstract void postBatchCreate(BusinessLogicInput input, T batch) throws UnifyException;
}
