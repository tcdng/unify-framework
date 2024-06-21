/*
  * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.core.database.sql.dialect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.constant.TimeSeriesType;
import com.tcdng.unify.core.criterion.RestrictionType;
import com.tcdng.unify.core.database.sql.AbstractSqlDataSourceDialect;
import com.tcdng.unify.core.database.sql.AbstractSqlDataSourceDialectPolicies;
import com.tcdng.unify.core.database.sql.SqlColumnAlterInfo;
import com.tcdng.unify.core.database.sql.SqlColumnInfo;
import com.tcdng.unify.core.database.sql.SqlCriteriaPolicy;
import com.tcdng.unify.core.database.sql.SqlDataSourceDialectPolicies;
import com.tcdng.unify.core.database.sql.SqlDataTypePolicy;
import com.tcdng.unify.core.database.sql.SqlDialectNameConstants;
import com.tcdng.unify.core.database.sql.SqlEntitySchemaInfo;
import com.tcdng.unify.core.database.sql.SqlFieldInfo;
import com.tcdng.unify.core.database.sql.SqlFieldSchemaInfo;
import com.tcdng.unify.core.database.sql.SqlIndexSchemaInfo;
import com.tcdng.unify.core.database.sql.SqlUniqueConstraintSchemaInfo;
import com.tcdng.unify.core.database.sql.data.policy.BlobPolicy;
import com.tcdng.unify.core.database.sql.data.policy.ClobPolicy;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * MySQL SQL dialect.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(name = SqlDialectNameConstants.MYSQL, description = "$m{sqldialect.mysqldb}")
public class MySqlDialect extends AbstractSqlDataSourceDialect {

	private static final MySqlDataSourceDialectPolicies sqlDataSourceDialectPolicies = new MySqlDataSourceDialectPolicies();

	static {
		Map<ColumnType, SqlDataTypePolicy> tempMap1 = new EnumMap<ColumnType, SqlDataTypePolicy>(ColumnType.class);
		populateDefaultSqlDataTypePolicies(tempMap1);
		tempMap1.put(ColumnType.BLOB, new MySqlBlobPolicy());
		tempMap1.put(ColumnType.CLOB, new MySqlClobPolicy());

		Map<RestrictionType, SqlCriteriaPolicy> tempMap2 = new EnumMap<RestrictionType, SqlCriteriaPolicy>(
				RestrictionType.class);
		populateDefaultSqlCriteriaPolicies(sqlDataSourceDialectPolicies, tempMap2);

		sqlDataSourceDialectPolicies.setSqlDataTypePolicies(DataUtils.unmodifiableMap(tempMap1));
		sqlDataSourceDialectPolicies.setSqlCriteriaPolicies(DataUtils.unmodifiableMap(tempMap2));
	}

	public MySqlDialect() {
		super(Collections.emptyList(), true);
	}

	@Override
	public String getDefaultSchema() {
		return null;
	}

	@Override
	public String generateTestSql() throws UnifyException {
		return "SELECT 1";
	}

	@Override
	public String generateUTCTimestampSql() throws UnifyException {
		return "SELECT UTC_TIMESTAMP";
	}

	@Override
	public String generateDropForeignKeyConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, String dbForeignKeyName,
			PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		String tableName = sqlEntitySchemaInfo.getSchemaTableName();
		sb.append("ALTER TABLE ").append(tableName);
		if (format.isPretty()) {
			sb.append(getLineSeparator());
		} else {
			sb.append(" ");
		}

		sb.append("DROP FOREIGN KEY ").append(dbForeignKeyName);
		return sb.toString();
	}

	@Override
	public String generateInlineUniqueConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
			SqlUniqueConstraintSchemaInfo sqlUniqueConstraintInfo, PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("UNIQUE ").append(sqlUniqueConstraintInfo.getName()).append(" (");
		boolean appendSym = false;
		for (String fieldName : sqlUniqueConstraintInfo.getFieldNameList()) {
			if (appendSym)
				sb.append(',');
			else
				appendSym = true;

			SqlFieldSchemaInfo sqlFieldInfo = sqlEntitySchemaInfo.getFieldInfo(fieldName);
			sb.append(sqlFieldInfo.getPreferredColumnName());
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String generateDropUniqueConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
			String dbUniqueConstraintName, PrintFormat format) throws UnifyException {
		return generateDropIndexSql(sqlEntitySchemaInfo, dbUniqueConstraintName, format);
	}

	@Override
	public String generateInlineIndexSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlIndexSchemaInfo sqlIndexInfo,
			PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("INDEX ").append(sqlIndexInfo.getName()).append(" (");
		boolean appendSym = false;
		for (String fieldName : sqlIndexInfo.getFieldNameList()) {
			if (appendSym)
				sb.append(',');
			else
				appendSym = true;

			SqlFieldSchemaInfo sqlFieldInfo = sqlEntitySchemaInfo.getFieldInfo(fieldName);
			sb.append(sqlFieldInfo.getPreferredColumnName());
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String generateDropIndexSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, String dbIndexName, PrintFormat format)
			throws UnifyException {
		StringBuilder sb = new StringBuilder();
		String tableName = sqlEntitySchemaInfo.getSchemaTableName();
		sb.append("ALTER TABLE ").append(tableName);
		if (format.isPretty()) {
			sb.append(getLineSeparator());
		} else {
			sb.append(" ");
		}

		sb.append("DROP INDEX ").append(dbIndexName);
		return sb.toString();
	}

	@Override
	public String generateRenameColumn(String tableName, String oldColumnName, SqlFieldSchemaInfo newSqlFieldInfo,
			PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ").append(tableName);
		if (format.isPretty()) {
			sb.append(getLineSeparator());
		} else {
			sb.append(' ');
		}
		sb.append("CHANGE COLUMN ").append(oldColumnName).append(' ');
		appendColumnAndTypeSql(sb, newSqlFieldInfo.getPreferredColumnName(), newSqlFieldInfo, true);
		return sb.toString();
	}

	@Override
	protected void appendTimestampTruncation(StringBuilder sql, SqlFieldInfo sqlFieldInfo,
			TimeSeriesType timeSeriesType, boolean merge) throws UnifyException {
		final String columnName = sqlFieldInfo.getPreferredColumnName();
		if (merge) {
			boolean inc = false;
			sql.append("LPAD(");
			int len = 1;
			switch (timeSeriesType) {
			case DAY_OF_WEEK:
				sql.append("DAYOFWEEK("); // 1- 7
				break;
			case DAY:
			case DAY_OF_MONTH:
				sql.append("DAYOFMONTH("); // 1 - 31
				len = 2;
				break;
			case DAY_OF_YEAR:
				sql.append("DAYOFYEAR("); // 1 - 366
				len = 3;
				break;
			case HOUR:
				sql.append("HOUR("); // 0 - 23
				len = 2;
				break;
			case MONTH:
				sql.append("MONTH("); // 1 - 12
				len = 2;
				break;
			case WEEK:
				sql.append("WEEK("); // 0 - 53
				inc = true;
				len = 2;
				break;
			case YEAR:
				sql.append("YEAR("); // 1000 - 9999
				len = 4;
				break;
			default:
				break;
			}

			sql.append(columnName).append(")");
			if (inc) {
				sql.append(" + 1");
			}
			
			sql.append(",");
			sql.append(len);
			sql.append(",'0')");
		} else {
			switch (timeSeriesType) {
			case DAY:
			case DAY_OF_WEEK:
			case DAY_OF_MONTH:
			case DAY_OF_YEAR:
				sql.append("CAST(STR_TO_DATE(DATE_FORMAT(").append(columnName)
						.append(", '%Y-%m-%d'), '%Y-%m-%d') AS DATETIME)");
				break;
			case HOUR:
				sql.append("CAST(STR_TO_DATE(DATE_FORMAT(").append(columnName)
						.append(", '%Y-%m-%d %H'), '%Y-%m-%d %H') AS DATETIME)");
				break;
			case MONTH:
				sql.append("CAST(STR_TO_DATE(DATE_FORMAT(").append(columnName)
						.append(", '%Y-%m-01'), '%Y-%m-%d') AS DATETIME)");
				break;
			case WEEK:
				sql.append("CAST(DATE(").append(columnName).append(" - INTERVAL (DAYOFWEEK(").append(columnName)
						.append(") - 1) DAY) AS DATETIME)");
				break;
			case YEAR:
				sql.append("CAST(STR_TO_DATE(DATE_FORMAT(").append(columnName)
						.append(", '%Y-01-01'), '%Y-%m-%d') AS DATETIME)");
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void appendTimestampTruncationGroupBy(StringBuilder sql, SqlFieldInfo sqlFieldInfo,
			TimeSeriesType timeSeriesType, boolean merge) throws UnifyException {
		sql.append(TRUNC_COLUMN_ALIAS);
	}

	@Override
	protected List<String> doGenerateAlterColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo,
			SqlFieldSchemaInfo sqlFieldSchemaInfo, SqlColumnAlterInfo sqlColumnAlterInfo, PrintFormat format)
			throws UnifyException {
		List<String> sqlList = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		SqlDataTypePolicy sqlDataTypePolicy = getSqlTypePolicy(sqlFieldSchemaInfo.getColumnType(),
				sqlFieldSchemaInfo.getLength());

		if (sqlColumnAlterInfo.isNullableChange()) {
			if (!sqlFieldSchemaInfo.isNullable()) {
				sb.append("UPDATE ").append(sqlEntitySchemaInfo.getSchemaTableName()).append(" SET ")
						.append(sqlFieldSchemaInfo.getPreferredColumnName()).append(" = ");
				sqlDataTypePolicy.appendDefaultVal(sb, sqlFieldSchemaInfo.getFieldType(),
						sqlFieldSchemaInfo.getDefaultVal());
				sb.append(" WHERE ").append(sqlFieldSchemaInfo.getPreferredColumnName()).append(" IS NULL");
				sqlList.add(sb.toString());
				StringUtils.truncate(sb);
			}
		}

		sb.append("ALTER TABLE ").append(sqlEntitySchemaInfo.getSchemaTableName());
		if (format.isPretty()) {
			sb.append(getLineSeparator());
		} else {
			sb.append(' ');
		}
		sb.append("MODIFY ");
		appendColumnAndTypeSql(sb, sqlFieldSchemaInfo.getPreferredColumnName(), sqlFieldSchemaInfo, true);
		sqlList.add(sb.toString());
		StringUtils.truncate(sb);
		return sqlList;
	}

	@Override
	public String generateAlterColumnNull(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlColumnInfo sqlColumnInfo,
			PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ").append(sqlEntitySchemaInfo.getSchemaTableName());
		if (format.isPretty()) {
			sb.append(getLineSeparator());
		} else {
			sb.append(' ');
		}
		sb.append("MODIFY ").append(sqlColumnInfo.getColumnName());
		appendTypeSql(sb, sqlColumnInfo);
		sb.append(" NULL");
		return sb.toString();
	}

	@Override
	public boolean isGeneratesUniqueConstraintsOnCreateTable() {
		return true;
	}

	@Override
	public boolean isGeneratesIndexesOnCreateTable() {
		return true;
	}

	@Override
	protected SqlDataSourceDialectPolicies getSqlDataSourceDialectPolicies() {
		return sqlDataSourceDialectPolicies;
	}

	@Override
	protected boolean appendLimitOffsetInfixClause(StringBuilder sql, int offset, int limit) throws UnifyException {
		return false;
	}

	@Override
	protected boolean appendWhereLimitOffsetSuffixClause(StringBuilder sql, int offset, int limit, boolean append)
			throws UnifyException {
		return false;
	}

	@Override
	protected boolean appendLimitOffsetSuffixClause(StringBuilder sql, int offset, int limit, boolean append)
			throws UnifyException {
		boolean isAppend = false;
		if (limit > 0) {
			sql.append(" LIMIT ").append(limit);
			isAppend = true;
		}

		if (offset > 0) {
			sql.append(" OFFSET ").append(offset);
			isAppend = true;
		}

		return isAppend;
	}

	private static class MySqlDataSourceDialectPolicies extends AbstractSqlDataSourceDialectPolicies {

		public void setSqlDataTypePolicies(Map<ColumnType, SqlDataTypePolicy> sqlDataTypePolicies) {
			this.sqlDataTypePolicies = sqlDataTypePolicies;
		}

		public void setSqlCriteriaPolicies(Map<RestrictionType, SqlCriteriaPolicy> sqlCriteriaPolicies) {
			this.sqlCriteriaPolicies = sqlCriteriaPolicies;
		}

		@Override
		protected ColumnType dialectSwapColumnType(ColumnType columnType, int length) {
			return columnType.isString() && length > 8000 ? ColumnType.CLOB: columnType;
		}

		@Override
		public int getMaxClauseValues() {
			return -1;
		}

		protected String concat(String... expressions) {
			StringBuilder sb = new StringBuilder();
			sb.append("CONCAT(");
			boolean appSym = false;
			for (String expression : expressions) {
				if (appSym) {
					sb.append(", ");
				} else {
					appSym = true;
				}

				sb.append(expression);
			}
			sb.append(")");
			return sb.toString();
		}
	}
}

class MySqlBlobPolicy extends BlobPolicy {

	@Override
	public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
		sb.append(" MEDIUMBLOB");
	}

	@Override
	public String getTypeName() {
		return "MEDIUMBLOB";
	}
}

class MySqlClobPolicy extends ClobPolicy {

	@Override
	public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
		sb.append(" MEDIUMTEXT");
	}

	@Override
	public String getTypeName() {
		return "MEDIUMTEXT";
	}
}