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

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.util.ValueStoreUtils;

/**
 * Convenient base class for batch file read processor.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractBatchFileReadProcessor extends AbstractUnifyComponent
        implements BatchFileReadProcessor {

    @Override
    public Object process(BatchFileReadConfig batchFileReadConfig, Object... file) throws UnifyException {
        BatchFileReader batchFileReader = (BatchFileReader) getComponent(batchFileReadConfig.getReaderName());
        try {
            batchFileReader.open(batchFileReadConfig, file);
            return doProcess(batchFileReadConfig, batchFileReader);
        } finally {
            batchFileReader.close();
        }
    }

    @Override
    protected void onInitialize() throws UnifyException {
        
    }

    @Override
    protected void onTerminate() throws UnifyException {
        
    }

    protected ValueStore getValueStore(Object bean) throws UnifyException {
        return ValueStoreUtils.getValueStore(bean, null, 0);
    }
    
    protected abstract Object doProcess(BatchFileReadConfig batchFileReadConfig, BatchFileReader batchFileReader)
            throws UnifyException;
}
