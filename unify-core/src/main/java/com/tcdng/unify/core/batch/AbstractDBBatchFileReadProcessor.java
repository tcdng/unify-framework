/*
 * Copyright 2018-2025 The Code Department.
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
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.business.GenericService;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Convenient base class for database batch file read processor.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractDBBatchFileReadProcessor<T extends BatchRecord, U extends BatchItemRecord>
        extends AbstractBatchFileReadProcessor implements DBBatchFileReadProcessor {

    @Configurable
    private GenericService genericService;

    private Class<T> batchClass;

    private Class<U> batchItemClass;

    public AbstractDBBatchFileReadProcessor(Class<T> batchClass, Class<U> batchItemClass) {
        this.batchClass = batchClass;
        this.batchItemClass = batchItemClass;
    }

    @Override
    protected Object doProcess(BatchFileReadConfig batchFileReadConfig, BatchFileReader reader) throws UnifyException {
        Map<String, T> batchMap = new HashMap<String, T>();
        // Start batch creation. May result in multiple batches
        // based on finding records belonging to more than one category.
        U batchItem = ReflectUtils.newInstance(batchItemClass);
        ValueStore itemStore = getValueStore(batchItem);
        while (reader.readNextRecord(itemStore)) {
            Object batchId = null;
            String category = getBatchCategory(batchItem);
            T batch = batchMap.get(category);
            if (batch == null) {
                batch = ReflectUtils.newInstance(batchClass);
                preBatchCreate(batchFileReadConfig, batch, batchItem);
                batch.setCategory(category);
                batchId = genericService.create(batch);
                batchMap.put(category, batch);
            } else {
                preBatchUpdate(batchFileReadConfig, batch, batchItem);
                batch.setCategory(category);
                genericService.update(batch);
                batchId = batch.getId();
            }

            batchItem.setBatchId(batchId);
            preBatchItemCreate(batchFileReadConfig, batchItem);
            genericService.create(batchItem);
            postBatchItemCreate(batchFileReadConfig, batchItem);
        }

        for (T batch : batchMap.values()) {
            postBatchCreate(batchFileReadConfig, batch);
        }
        
        return batchMap;
    }

    protected void preBatchItemCreate(BatchFileReadConfig batchFileReadConfig, U batchItem) throws UnifyException {

    }

    protected void postBatchItemCreate(BatchFileReadConfig batchFileReadConfig, U batchItem) throws UnifyException {

    }

    protected abstract String getBatchCategory(U batchItem) throws UnifyException;

    protected abstract void preBatchCreate(BatchFileReadConfig batchFileReadConfig, T batch, U batchItem)
            throws UnifyException;

    protected abstract void preBatchUpdate(BatchFileReadConfig batchFileReadConfig, T batch, U batchItem)
            throws UnifyException;

    protected abstract void postBatchCreate(BatchFileReadConfig batchFileReadConfig, T batch) throws UnifyException;
}
