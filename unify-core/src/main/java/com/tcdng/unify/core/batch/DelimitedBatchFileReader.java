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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Parameter;
import com.tcdng.unify.core.annotation.Parameters;
import com.tcdng.unify.core.business.BusinessLogicInput;

/**
 * Delimited batch file reader.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(name = "delimited-batchfilereader", description = "$m{batchfilereader.delimited}")
@Parameters({
		@Parameter(name = DelimitedBatchFileReaderInputConstants.FIELDDELIMITER, description = "$m{batchfilereader.delimited.fielddelimiter}", editor = "!ui-select list:$s{fielddelimiterlist} blankOption:$s{}", mandatory = true) })
public class DelimitedBatchFileReader extends AbstractMultiLineTextFileRecordReader {

	private FieldDelimiterType fieldDelimiterType;

	@Override
	public void open(BusinessLogicInput input, BatchFileConfig configuration, Object[] file) throws UnifyException {
		super.open(input, configuration, file);
		fieldDelimiterType = input.getParameter(FieldDelimiterType.class,
				DelimitedBatchFileReaderInputConstants.FIELDDELIMITER);
	}

	@Override
	protected String[] parseEntry(String entry) throws UnifyException {
		String[] result = new String[getBatchFileConfig().getFieldConfigs().size()];
		logDebug("Parsing delimited [{0}] in line number = {1}, delimiter = [{2}]", entry, getEntryCounter(),
				fieldDelimiterType);
		int index = 0;
		int beginIndex = 0;
		char ch = fieldDelimiterType.getCharacter();
		for (BatchFileFieldConfig fieldConfig : getBatchFileConfig().getFieldConfigs()) {
			int endIndex = 0;
			if (entry.charAt(beginIndex) == '"') {
				// Take care of quoted value
				int actBeginIndex = beginIndex + 1;
				int actEndIndex = entry.indexOf('"', actBeginIndex);
				while (actEndIndex > 0 && entry.charAt(actEndIndex - 1) == '\\') {
					actEndIndex = entry.indexOf('"', actEndIndex + 1);
				}
				result[index] = entry.substring(actBeginIndex, actEndIndex).replaceAll("\\\\\"", "\"");
				endIndex = actEndIndex + 1;
			} else {
				// Normal value with no quotes
				endIndex = entry.indexOf(ch, beginIndex);
				if (endIndex < 0) {
					endIndex = entry.length();
				}
				result[index] = entry.substring(beginIndex, endIndex);
			}

			if (fieldConfig.isTrim()) {
				result[index] = result[index].trim();
			}

			beginIndex = endIndex + 1;
			index++;
		}
		return result;
	}
}
