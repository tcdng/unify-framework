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

import java.sql.Types;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
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
import com.tcdng.unify.core.database.sql.data.policy.BlobPolicy;
import com.tcdng.unify.core.database.sql.data.policy.ClobPolicy;
import com.tcdng.unify.core.database.sql.data.policy.DatePolicy;
import com.tcdng.unify.core.database.sql.data.policy.TimestampPolicy;
import com.tcdng.unify.core.database.sql.data.policy.TimestampUTCPolicy;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * MS SQL dialect.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(name = SqlDialectNameConstants.MSSQL, description = "$m{sqldialect.mssqldb}")
public class MsSqlDialect extends AbstractSqlDataSourceDialect {

	private static final MsSqlDataSourceDialectPolicies sqlDataSourceDialectPolicies = new MsSqlDataSourceDialectPolicies();

	static {
		Map<ColumnType, SqlDataTypePolicy> tempMap1 = new EnumMap<ColumnType, SqlDataTypePolicy>(ColumnType.class);
		populateDefaultSqlDataTypePolicies(tempMap1);
		tempMap1.put(ColumnType.TIMESTAMP_UTC, new MsSqlTimestampUTCPolicy());
		tempMap1.put(ColumnType.TIMESTAMP, new MsSqlTimestampPolicy());
		tempMap1.put(ColumnType.DATE, new MsSqlDatePolicy());
		tempMap1.put(ColumnType.BLOB, new MsSqlBlobPolicy());
		tempMap1.put(ColumnType.CLOB, new MsSqlClobPolicy());

		Map<RestrictionType, SqlCriteriaPolicy> tempMap2 = new EnumMap<RestrictionType, SqlCriteriaPolicy>(
				RestrictionType.class);
		populateDefaultSqlCriteriaPolicies(sqlDataSourceDialectPolicies, tempMap2);

		sqlDataSourceDialectPolicies.setSqlDataTypePolicies(DataUtils.unmodifiableMap(tempMap1));
		sqlDataSourceDialectPolicies.setSqlCriteriaPolicies(DataUtils.unmodifiableMap(tempMap2));
	}

	public MsSqlDialect() {
		super(false); // useCallableFunctionMode
	}

	@Override
	public String getDefaultSchema() {
		return "dbo";
	}

	@Override
	public String generateTestSql() throws UnifyException {
		return "SELECT CURRENT_TIMESTAMP";
	}

	@Override
	public String generateUTCTimestampSql() throws UnifyException {
		return "SELECT GETUTCDATE()";
	}

	@Override
	public boolean matchColumnDefault(String nativeVal, String defaultVal) throws UnifyException {
		if (super.matchColumnDefault(nativeVal, defaultVal)) {
			return true;
		}

		if (nativeVal != null && defaultVal != null) {
			if (nativeVal.charAt(0) == '(') {
				int last = nativeVal.length() - 1;
				if (nativeVal.charAt(last) == ')') {
					return nativeVal.substring(1, last).equals(defaultVal);
				}
			}
		}

		return false;
	}

	@Override
	protected void appendTimestampTruncation(StringBuilder sql, SqlFieldInfo sqlFieldInfo,
			TimeSeriesType timeSeriesType, boolean merge) throws UnifyException {
		if (merge) {
			sql.append("FORMAT(DATEPART(");
			String fmt = "'0'";
			switch (timeSeriesType) {
			case DAY_OF_WEEK:
				sql.append("weekday"); // 1- 7
				break;
			case DAY:
			case DAY_OF_MONTH:
				sql.append("day"); // 1 - 31
				fmt = "'00'";
				break;
			case DAY_OF_YEAR:
				sql.append("dayofyear"); // 1 - 366
				fmt = "'000'";
				break;
			case HOUR:
				sql.append("hour"); // 0 - 23
				fmt = "'00'";
				break;
			case MONTH:
				sql.append("month"); // 1 - 12
				fmt = "'00'";
				break;
			case WEEK:
				sql.append("week"); // 1 - 54
				fmt = "'00'";
				break;
			case YEAR:
				sql.append("year"); // 1 - 9999
				fmt = "'0000'";
				break;
			default:
				break;
			}
			sql.append(", ").append(sqlFieldInfo.getPreferredColumnName()).append("),");
			sql.append(fmt);
			sql.append(")");
		} else {
			sql.append("DATEPART(");
			switch (timeSeriesType) {
			case DAY:
			case DAY_OF_WEEK:
			case DAY_OF_MONTH:
			case DAY_OF_YEAR:
				sql.append("day");
				break;
			case HOUR:
				sql.append("hour");
				break;
			case MONTH:
				sql.append("month");
				break;
			case WEEK:
				sql.append("week");
				break;
			case YEAR:
				sql.append("year");
				break;
			default:
				break;
			}
			sql.append(", ").append(sqlFieldInfo.getPreferredColumnName()).append(")");
		}
	}

	@Override
	protected void appendTimestampTruncationGroupBy(StringBuilder sql, SqlFieldInfo sqlFieldInfo,
			TimeSeriesType timeSeriesType, boolean merge) throws UnifyException {
		sql.append(TRUNC_COLUMN_ALIAS);
	}

	@Override
	protected boolean appendLimitOffsetInfixClause(StringBuilder sql, int offset, int limit) throws UnifyException {
		if (offset > 0) {
			throw new UnifyException(UnifyCoreErrorConstants.QUERY_RESULT_OFFSET_NOT_SUPPORTED);
		}

		if (limit > 0) {
			sql.append(" TOP ").append(limit);
			return true;
		}
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
		return false;
	}

	@Override
	public String generateRenameTable(SqlEntitySchemaInfo sqlRecordSchemaInfo,
			SqlEntitySchemaInfo oldSqlRecordSchemaInfo, PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("sp_RENAME '").append(oldSqlRecordSchemaInfo.getSchemaTableName()).append('.')
				.append(sqlRecordSchemaInfo.getSchemaTableName()).append("'");
		return sb.toString();
	}

	@Override
	public String generateAddColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo, String columnName,
			SqlFieldSchemaInfo sqlFieldSchemaInfo, PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ").append(sqlEntitySchemaInfo.getSchemaTableName());
		if (format.isPretty()) {
			sb.append(getLineSeparator());
		} else {
			sb.append(' ');
		}
		sb.append("ADD ");
		appendColumnAndTypeSql(sb, columnName, sqlFieldSchemaInfo, true);
		return sb.toString();
	}

	@Override
	protected List<String> doGenerateAlterColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo,
			SqlFieldSchemaInfo sqlFieldSchemaInfo, SqlColumnAlterInfo sqlColumnAlterInfo, PrintFormat format)
			throws UnifyException {
		List<String> sqlList = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		SqlDataTypePolicy sqlDataTypePolicy = getSqlTypePolicy(sqlFieldSchemaInfo.getColumnType());

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

		sb.append("ALTER COLUMN ");
		appendColumnAndTypeSql(sb, sqlFieldSchemaInfo);
		sqlList.add(sb.toString());
		StringUtils.truncate(sb);

		if (sqlFieldSchemaInfo.isWithDefaultVal()) {
			sb.append("ALTER TABLE ").append(sqlEntitySchemaInfo.getSchemaTableName());
			if (format.isPretty()) {
				sb.append(getLineSeparator());
			} else {
				sb.append(' ');
			}

			sb.append("ADD ");
			sqlDataTypePolicy.appendDefaultSql(sb, sqlFieldSchemaInfo.getFieldType(),
					sqlFieldSchemaInfo.getDefaultVal());
			sb.append(" FOR ");
			sb.append(sqlFieldSchemaInfo.getPreferredColumnName());
			sqlList.add(sb.toString());
			StringUtils.truncate(sb);
		}

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
		sb.append("ALTER COLUMN ");
		sb.append(sqlColumnInfo.getColumnName());
		appendTypeSql(sb, sqlColumnInfo);
		sb.append(" NULL");
		return sb.toString();
	}

	@Override
	public String generateRenameColumn(String tableName, String oldColumnName, SqlFieldSchemaInfo newSqlFieldInfo,
			PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("sp_RENAME '").append(tableName).append('.').append(oldColumnName).append("', '");
		sb.append(newSqlFieldInfo.getPreferredColumnName()).append("' , 'COLUMN'");
		return sb.toString();
	}

	@Override
	public String generateDropColumn(SqlEntitySchemaInfo sqlRecordSchemaInfo, SqlFieldSchemaInfo sqlFieldSchemaInfo,
			PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ").append(sqlRecordSchemaInfo.getSchemaTableName());
		if (format.isPretty()) {
			sb.append(getLineSeparator());
		} else {
			sb.append(' ');
		}
		sb.append("DROP COLUMN ").append(sqlFieldSchemaInfo.getPreferredColumnName());
		return sb.toString();
	}

	@Override
	public boolean isGeneratesUniqueConstraintsOnCreateTable() {
		return false;
	}

	@Override
	public boolean isGeneratesIndexesOnCreateTable() {
		return false;
	}

	@Override
	protected void onInitialize() throws UnifyException {
		super.onInitialize();
		includeNoPrecisionType("INT");
	}

	@Override
	protected SqlDataSourceDialectPolicies getSqlDataSourceDialectPolicies() {
		return sqlDataSourceDialectPolicies;
	}

	private static class MsSqlDataSourceDialectPolicies extends AbstractSqlDataSourceDialectPolicies {

		public void setSqlDataTypePolicies(Map<ColumnType, SqlDataTypePolicy> sqlDataTypePolicies) {
			this.sqlDataTypePolicies = sqlDataTypePolicies;
		}

		public void setSqlCriteriaPolicies(Map<RestrictionType, SqlCriteriaPolicy> sqlCriteriaPolicies) {
			this.sqlCriteriaPolicies = sqlCriteriaPolicies;
		}

		@Override
		public int getMaxClauseValues() {
			return -1;
		}

		@Override
		protected String concat(String... expressions) {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			boolean appSym = false;
			for (String expression : expressions) {
				if (appSym) {
					sb.append(" + ");
				} else {
					appSym = true;
				}

				sb.append(expression);
			}
			sb.append(")");
			return sb.toString();
		}
	}

	private void appendColumnAndTypeSql(StringBuilder sb, SqlFieldSchemaInfo sqlFieldSchemaInfo) throws UnifyException {
		SqlDataTypePolicy sqlDataTypePolicy = getSqlTypePolicy(sqlFieldSchemaInfo.getColumnType());
		sb.append(sqlFieldSchemaInfo.getPreferredColumnName());
		sqlDataTypePolicy.appendTypeSql(sb, sqlFieldSchemaInfo.getLength(), sqlFieldSchemaInfo.getPrecision(),
				sqlFieldSchemaInfo.getScale());

		if (sqlFieldSchemaInfo.isPrimaryKey()) {
			sb.append(" PRIMARY KEY NOT NULL");
		} else {
			if (sqlFieldSchemaInfo.isWithDefaultVal()) {
				sqlDataTypePolicy.appendDefaultSql(sb, sqlFieldSchemaInfo.getFieldType(),
						sqlFieldSchemaInfo.getDefaultVal());
			}

			if (sqlFieldSchemaInfo.isNullable()) {
				sb.append(" NULL");
			} else {
				sb.append(" NOT NULL");
			}
		}
	}

}

class MsSqlTimestampUTCPolicy extends TimestampUTCPolicy {

	@Override
	public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
		sb.append(" DATETIME");
	}

	@Override
	public String getTypeName() {
		return "DATETIME";
	}

	@Override
	public int getSqlType() {
		return Types.TIMESTAMP;
	}

}

class MsSqlTimestampPolicy extends TimestampPolicy {

	@Override
	public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
		sb.append(" DATETIME");
	}

	@Override
	public String getTypeName() {
		return "DATETIME";
	}

	@Override
	public int getSqlType() {
		return Types.TIMESTAMP;
	}

}

class MsSqlDatePolicy extends DatePolicy {

	@Override
	public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
		sb.append(" DATETIME");
	}

	@Override
	public String getTypeName() {
		return "DATETIME";
	}

	@Override
	public int getSqlType() {
		return Types.TIMESTAMP;
	}

}

class MsSqlBlobPolicy extends BlobPolicy {

	@Override
	public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
		sb.append(" VARBINARY(MAX)");
	}

	@Override
	public String getTypeName() {
		return "VARBINARY";
	}

	@Override
	public int getSqlType() {
		return Types.LONGVARBINARY;
	}

}

class MsSqlClobPolicy extends ClobPolicy {

	@Override
	public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
		sb.append(" VARCHAR(MAX)");
	}

	@Override
	public String getTypeName() {
		return "VARCHAR";
	}

	@Override
	public int getSqlType() {
		return Types.LONGVARCHAR;
	}

}