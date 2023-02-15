/*
 * Copyright 2018-2022 The Code Department.
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
package com.tcdng.unify.core.report;

import com.tcdng.unify.core.constant.Bold;
import com.tcdng.unify.core.constant.HAlignType;
import com.tcdng.unify.core.util.DataUtils;

/**
 * A report field.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class ReportField {

	private String name;

	private String className;

	private String sqlBlobTypeName;

	private String formatterUpl;

	private HAlignType horizontalAlignment;

	private Bold bold;

	protected ReportField(String name, String className, String sqlBlobTypeName, String formatterUpl,
			HAlignType horizontalAlignment, Bold bold) {
		this.name = name;
		this.className = className;
		this.sqlBlobTypeName = sqlBlobTypeName;
		this.formatterUpl = formatterUpl;
		this.horizontalAlignment = horizontalAlignment;
		this.bold = bold;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypeName() {
		return className;
	}

	public boolean isBold() {
		return bold != null && bold.isTrue();
	}

	public boolean isNumber() {
		return DataUtils.isNumberType(className);
	}

	public boolean isDate() {
		return "java.util.Date".equals(className);
	}

	public boolean isBlob() {
		return "[B".equals(className);
	}

	public HAlignType getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public String getSqlBlobTypeName() {
		return sqlBlobTypeName;
	}

	public String getFormatterUpl() {
		return formatterUpl;
	}

}
