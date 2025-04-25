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

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.business.GenericService;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Convenient base class for database batch item file read processor.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractDBBatchItemFileReadProcessor<T extends BatchItemRecord>
        extends AbstractBatchFileReadProcessor implements DBBatchFileReadProcessor {

    @Configurable
    private GenericService genericService;

    private Class<T> batchItemClass;

    public AbstractDBBatchItemFileReadProcessor(Class<T> batchItemClass) {
        this.batchItemClass = batchItemClass;
    }

    @Override
    protected Object doProcess(BatchFileReadConfig batchFileConfig, BatchFileReader reader) throws UnifyException {
        List<Object> ids = new ArrayList<Object>();
        List<String> updateList = null;
        ConstraintAction action = batchFileConfig.getOnConstraint();
        if (ConstraintAction.UPDATE.equals(action)) {
            updateList = new ArrayList<String>();
            for (BatchFileFieldConfig bfc : batchFileConfig.getFieldConfigList()) {
                if (bfc.isUpdateOnConstraint()) {
                    updateList.add(bfc.getBeanFieldName());
                }
            }
        }

        T batchItem = ReflectUtils.newInstance(batchItemClass);
        ValueStore itemStore = getValueStore(batchItem);
        while (reader.readNextRecord(itemStore)) {
            T constraint = genericService.findConstraint(batchItem);
            if (constraint == null) {
                // No constraint. Just create item.
                preBatchItemCreate(batchFileConfig, batchItem);
                Object id = genericService.create(batchItem);
                ids.add(id);
                postBatchItemCreate(batchFileConfig, batchItem);
            } else {
                // Constraining record found. Take action.
                switch (action) {
                    case FAIL:
                        throw new UnifyException(UnifyCoreErrorConstants.BATCH_FILE_READER_RECORD_EXISTS, constraint,
                                batchItem);
                    case UPDATE:
                        ReflectUtils.shallowBeanCopy(constraint, batchItem, updateList);
                        genericService.update(constraint);
                        break;
                    case SKIP:
                    default:
                        break;
                }
            }
        }

        return ids;
    }

    protected void preBatchItemCreate(BatchFileReadConfig batchFileReadConfig, T batchItem) throws UnifyException {

    }

    protected void postBatchItemCreate(BatchFileReadConfig batchFileReadConfig, T batchItem) throws UnifyException {

    }

}
