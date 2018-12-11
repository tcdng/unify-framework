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
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.business.AbstractBusinessLogicUnit;
import com.tcdng.unify.core.business.BusinessLogicInput;
import com.tcdng.unify.core.business.BusinessLogicOutput;

/**
 * Convenient base class for batch file read processor.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractBatchFileReadProcessor extends AbstractBusinessLogicUnit
		implements BatchFileReadProcessor {

	@Configurable(ApplicationComponents.APPLICATION_BATCHFILEREADERWRITERFACTORY)
	private BatchFileReaderWriterFactory batchFileRwFactory;

	@Override
	public void execute(BusinessLogicInput input, BusinessLogicOutput output) throws UnifyException {
		Object result = null;
		BatchFileConfig batchFileConfig = input.getParameter(BatchFileConfig.class,
				BatchFileReadProcessorInputConstants.BATCHFILECONFIG);
		Object[] fileObject = input.getParameter(Object[].class, BatchFileReadProcessorInputConstants.FILEOBJECTS);
		BatchFileReader reader = batchFileRwFactory.getBatchFileReader(input, batchFileConfig, fileObject);
		try {
			result = doProcessBatchFile(input, batchFileConfig, reader);
		} finally {
			batchFileRwFactory.disposeBatchRecordReader(reader);
		}

		output.setResult(BatchFileReadProcessorOutputConstants.BATCHFILEREADRESULT, result);
	}

	protected abstract Object doProcessBatchFile(BusinessLogicInput input, BatchFileConfig batchFileConfig,
			BatchFileReader reader) throws UnifyException;
}
