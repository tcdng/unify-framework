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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.tcdng.unify.core.constant.PadDirection;

/**
 * Batch file configuration.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class BatchFileConfig {

	private String reader;

	private String writer;

	private Map<String, BatchFileFieldConfig> fieldConfigList;

	private ConstraintAction onConstraint;

	private boolean skipFirstRecord;

	public BatchFileConfig() {
		this(false);
	}

	public BatchFileConfig(boolean skipFirstRecord) {
		fieldConfigList = new LinkedHashMap<String, BatchFileFieldConfig>();
		skipFirstRecord = false;
		onConstraint = ConstraintAction.SKIP;
		this.skipFirstRecord = skipFirstRecord;
	}

	public BatchFileConfig addFieldConfig(String fieldName, String readerFieldName, String formatter,
			PadDirection padDirection, int length, boolean trim, boolean pad, boolean updateOnConstraint,
			Character padChar) {
		fieldConfigList.put(fieldName, new BatchFileFieldConfig(fieldName, readerFieldName, formatter,
				padDirection, length, trim, pad, updateOnConstraint, padChar));
		return this;
	}

	public BatchFileConfig addFieldConfig(String fieldName, int length, boolean trim) {
		fieldConfigList.put(fieldName,
				new BatchFileFieldConfig(fieldName, null, null, null, length, trim, false, true, ' '));
		return this;
	}

	public ConstraintAction getOnConstraint() {
		return onConstraint;
	}

	public void setOnConstraint(ConstraintAction onConstraint) {
		this.onConstraint = onConstraint;
	}

	public String getReader() {
		return reader;
	}

	public void setReader(String reader) {
		this.reader = reader;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public Collection<BatchFileFieldConfig> getFieldConfigs() {
		return fieldConfigList.values();
	}

	public boolean isSkipFirstRecord() {
		return skipFirstRecord;
	}
}
