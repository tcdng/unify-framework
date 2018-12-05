/*
 * Copyright 2014 The Code Department
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

import com.tcdng.unify.core.operation.Operator;

/**
 * A report filter.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ReportColumnFilter {

	private Operator op;

	private String tableName;

	private String columnName;

	private Object param1;

	private Object param2;

	public ReportColumnFilter(Operator op, String tableName, String columnName, Object param1, Object param2) {
		this.op = op;
		this.tableName = tableName;
		this.columnName = columnName;
		this.param1 = param1;
		this.param2 = param2;
	}

	public Operator getOp() {
		return op;
	}

	public String getTableName() {
		return tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public Object getParam1() {
		return param1;
	}

	public Object getParam2() {
		return param2;
	}

}
