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
package com.tcdng.unify.core.database;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.database.sql.SqlJoinType;
import com.tcdng.unify.core.operation.Operator;

/**
 * A native query object.
 * 
 * @author Lateef
 * @since 1.0
 */
public class NativeQuery {

	private List<Column> columnList;

	private List<Join> joinList;

	private List<Filter> filterList;

	private String schemaName;

	private String tableName;

	private int offset;

	private int limit;

	private boolean distinct;

	public NativeQuery() {
		this.columnList = new ArrayList<Column>();
		this.joinList = new ArrayList<Join>();
		this.filterList = new ArrayList<Filter>();
		this.limit = 0;
	}

	public NativeQuery schemaName(String schemaName) {
		this.schemaName = schemaName;
		return this;
	}

	public NativeQuery tableName(String tableName) {
		this.tableName = tableName;
		return this;
	}

	public NativeQuery addColumn(String tableName, String columnName) {
		columnList.add(new Column(tableName, columnName));
		return this;
	}

	public NativeQuery addJoin(SqlJoinType type, String tableA, String columnA, String tableB, String columnB) {
		joinList.add(new Join(type, tableA, columnA, tableB, columnB));
		return this;
	}

	public NativeQuery addFilter(Operator op, String tableName, String columnName, Object param1, Object param2) {
		filterList.add(new Filter(op, tableName, columnName, param1, param2));
		return this;
	}

	public NativeQuery limit(int limit) {
		this.limit = limit;
		return this;
	}

	public NativeQuery offset(int offset) {
		this.offset = offset;
		return this;
	}

	public NativeQuery distinct(boolean distinct) {
		this.distinct = distinct;
		return this;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public String getTableName() {
		return tableName;
	}

	public List<Column> getColumnList() {
		return columnList;
	}

	public List<Join> getJoinList() {
		return joinList;
	}

	public List<Filter> getFilterList() {
		return filterList;
	}

	public int getLimit() {
		return limit;
	}

	public int getOffset() {
		return offset;
	}

	public int columns() {
		return this.columnList.size();
	}

	public boolean isDistinct() {
		return distinct;
	}

	public boolean isJoin() {
		return !joinList.isEmpty();
	}

	public boolean isFilter() {
		return !filterList.isEmpty();
	}

	public class Column {

		private String tableName;

		private String columnName;

		public Column(String tableName, String columnName) {
			this.tableName = tableName;
			this.columnName = columnName;
		}

		public String getTableName() {
			return tableName;
		}

		public String getColumnName() {
			return columnName;
		}
	}

	public class Join {

		private SqlJoinType type;

		private String tableA;

		private String columnA;

		private String tableB;

		private String columnB;

		public Join(SqlJoinType type, String tableA, String columnA, String tableB, String columnB) {
			this.type = type;
			this.tableA = tableA;
			this.columnA = columnA;
			this.tableB = tableB;
			this.columnB = columnB;
		}

		public SqlJoinType getType() {
			return type;
		}

		public String getTableA() {
			return tableA;
		}

		public String getColumnA() {
			return columnA;
		}

		public String getTableB() {
			return tableB;
		}

		public String getColumnB() {
			return columnB;
		}

	}

	public class Filter {

		private Operator op;

		private String tableName;

		private String columnName;

		private Object param1;

		private Object param2;

		public Filter(Operator op, String tableName, String columnName, Object param1, Object param2) {
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
}
