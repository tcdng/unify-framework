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

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.business.BusinessLogicInput;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.database.Database;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Convenient base class for database batch item file read processor.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractDBBatchItemFileReadProcessor<T extends BatchItemRecord>
        extends AbstractBatchFileReadProcessor implements DBBatchFileReadProcessor {

    private Class<T> batchItemClass;

    public AbstractDBBatchItemFileReadProcessor(Class<T> batchItemClass) {
        this.batchItemClass = batchItemClass;
    }

    @Override
    protected Object doProcessBatchFile(BusinessLogicInput input, BatchFileConfig batchFileConfig,
            BatchFileReader reader) throws UnifyException {
        TaskMonitor tm = input.getTaskMonitor();
        tm.addMessage("Processing batch file...");
        List<Object> idList = new ArrayList<Object>();
        String[] updateFields = null;
        ConstraintAction action = batchFileConfig.getOnConstraint();
        if (ConstraintAction.UPDATE.equals(action)) {
            List<String> updateList = new ArrayList<String>();
            for (BatchFileFieldConfig bfc : batchFileConfig.getFieldConfigs()) {
                if (bfc.isUpdateOnConstraint()) {
                    updateList.add(bfc.getFieldName());
                }
            }
            updateFields = updateList.toArray(new String[updateList.size()]);
        }

        Database db = getDatabase(input);
        T batchItem = ReflectUtils.newInstance(batchItemClass);
        ValueStore itemStore = getValueStoreFactory().getValueStore(batchItem, 0);
        int createCount = 0;
        int updateCount = 0;
        int skipCount = 0;
        while (reader.readNextRecord(itemStore)) {
            T constraint = db.findConstraint(batchItem);
            if (constraint == null) {
                // No constraint. Just create item.
                preBatchItemCreate(input, batchItem);
                Object id = db.create(batchItem);
                idList.add(id);
                postBatchItemCreate(input, batchItem);
                createCount++;
            } else {
                // Constraining record found. Take action.
                switch (action) {
                    case FAIL:
                        throw new UnifyException(UnifyCoreErrorConstants.BATCH_FILE_READER_RECORD_EXISTS, constraint,
                                batchItem);
                    case UPDATE:
                        ReflectUtils.shallowBeanCopy(constraint, batchItem, updateFields);
                        db.updateByIdVersion(constraint);
                        updateCount++;
                        break;
                    case SKIP:
                    default:
                        // Skip. Do nothing with batch item
                        skipCount++;
                        break;
                }
            }
        }

        tm.addMessage("Batch file processing completed.");
        tm.addMessage("Summary: createCount = " + createCount + ", updateCount = " + updateCount + ", skipCount = "
                + skipCount);

        return idList;
    }

    protected void preBatchItemCreate(BusinessLogicInput input, T batchItem) throws UnifyException {

    }

    protected void postBatchItemCreate(BusinessLogicInput input, T batchItem) throws UnifyException {

    }

}
