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
package com.tcdng.unify.core.database.sql;

import com.tcdng.unify.core.data.Listable;

/**
 * SQL table column information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class SqlColumnInfo implements Listable {

	private Class<?> javaType;

	private String columnName;

	private int sqlType;

	private int size;

	private int decimalDigits;

	private boolean nullable;

	public SqlColumnInfo(Class<?> javaType, String name, int sqlType, int size, int decimalDigits, boolean nullable) {
		this.javaType = javaType;
		this.columnName = name;
		this.sqlType = sqlType;
		this.size = size;
		this.decimalDigits = decimalDigits;
		this.nullable = nullable;
	}

	@Override
	public String getListKey() {
		return columnName;
	}

	@Override
	public String getListDescription() {
		return columnName;
	}

	public Class<?> getJavaType() {
		return javaType;
	}

	public String getColumnName() {
		return columnName;
	}

	public int getSqlType() {
		return sqlType;
	}

	public int getSize() {
		return size;
	}

	public int getDecimalDigits() {
		return decimalDigits;
	}

	public boolean isNullable() {
		return nullable;
	}
}
