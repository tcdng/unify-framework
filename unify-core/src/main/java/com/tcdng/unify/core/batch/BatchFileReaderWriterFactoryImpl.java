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

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.business.BusinessLogicInput;

/**
 * Default batch file reader/writer factory implementation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_BATCHFILEREADERWRITERFACTORY)
public class BatchFileReaderWriterFactoryImpl extends AbstractBatchFileReaderWriterFactory {

	@Override
	public BatchFileReader getBatchFileReader(BusinessLogicInput input, BatchFileConfig batchFileConfig,
			Object[] fileObject) throws UnifyException {
		BatchFileReader reader = (BatchFileReader) getComponent(batchFileConfig.getReader());
		reader.open(input, batchFileConfig, fileObject);
		return reader;
	}

	@Override
	public void disposeBatchRecordReader(BatchFileReader reader) {
		reader.close();
	}

}
