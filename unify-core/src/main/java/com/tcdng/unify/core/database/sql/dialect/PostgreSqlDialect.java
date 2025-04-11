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

import java.io.ByteArrayInputStream;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.common.annotation.ColumnType;
import com.tcdng.unify.core.UnifyException;
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
import com.tcdng.unify.core.database.sql.data.policy.BigDecimalPolicy;
import com.tcdng.unify.core.database.sql.data.policy.BlobPolicy;
import com.tcdng.unify.core.database.sql.data.policy.BooleanPolicy;
import com.tcdng.unify.core.database.sql.data.policy.CharacterPolicy;
import com.tcdng.unify.core.database.sql.data.policy.ClobPolicy;
import com.tcdng.unify.core.database.sql.data.policy.DatePolicy;
import com.tcdng.unify.core.database.sql.data.policy.IntegerPolicy;
import com.tcdng.unify.core.database.sql.data.policy.LongPolicy;
import com.tcdng.unify.core.database.sql.data.policy.ShortPolicy;
import com.tcdng.unify.core.database.sql.data.policy.TimestampPolicy;
import com.tcdng.unify.core.database.sql.data.policy.TimestampUTCPolicy;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * PostgreSQL SQL dialect.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(name = SqlDialectNameConstants.POSTGRESQL, description = "$m{sqldialect.postgresdb}")
public class PostgreSqlDialect extends AbstractSqlDataSourceDialect {

	private static final PostgreSqlDataSourceDialectPolicies sqlDataSourceDialectPolicies = new PostgreSqlDataSourceDialectPolicies();

	static {
		Map<ColumnType, SqlDataTypePolicy> tempMap1 = new EnumMap<ColumnType, SqlDataTypePolicy>(ColumnType.class);
		populateDefaultSqlDataTypePolicies(tempMap1);
		tempMap1.put(ColumnType.BOOLEAN, new PostgreSqlBooleanPolicy());
		tempMap1.put(ColumnType.CHARACTER, new PostgreSqlCharacterPolicy());
		tempMap1.put(ColumnType.INTEGER, new PostgreSqlIntegerPolicy());
		tempMap1.put(ColumnType.LONG, new PostgreSqlLongPolicy());		
		tempMap1.put(ColumnType.SHORT, new PostgreSqlShortPolicy());		
		tempMap1.put(ColumnType.DECIMAL, new PostgreSqlBigDecimalPolicy());		
		tempMap1.put(ColumnType.DATE, new PostgreSqlDatePolicy());
		tempMap1.put(ColumnType.TIMESTAMP, new PostgreSqlTimestampPolicy());
		tempMap1.put(ColumnType.TIMESTAMP_UTC, new PostgreSqlTimestampUTCPolicy());
		tempMap1.put(ColumnType.BLOB, new PostgreSqlBlobPolicy());
		tempMap1.put(ColumnType.CLOB, new PostgreSqlClobPolicy());

		Map<RestrictionType, SqlCriteriaPolicy> tempMap2 = new EnumMap<RestrictionType, SqlCriteriaPolicy>(
				RestrictionType.class);
		populateDefaultSqlCriteriaPolicies(sqlDataSourceDialectPolicies, tempMap2);

		sqlDataSourceDialectPolicies.setSqlDataTypePolicies(DataUtils.unmodifiableMap(tempMap1));
		sqlDataSourceDialectPolicies.setSqlCriteriaPolicies(DataUtils.unmodifiableMap(tempMap2));
	}

	public PostgreSqlDialect() {
		super(Collections.emptyList(), true);
	}

	@Override
	public String getDefaultSchema() {
		return "public";
	}

	@Override
	public boolean matchColumnDefault(String nativeVal, String defaultVal) throws UnifyException {
		if (super.matchColumnDefault(nativeVal, defaultVal)) {
			return true;
		}

		if (nativeVal != null && defaultVal != null) {
			if (nativeVal.charAt(0) == '\'') {
				if (defaultVal.charAt(0) == '\'') {
					return nativeVal.startsWith(defaultVal);
				}

				return nativeVal.startsWith("'" + defaultVal + "'");
			}
		}

		return false;
	}

	@Override
	public String generateTestSql() throws UnifyException {
		return "SELECT 1";
	}

	@Override
	public String generateUTCTimestampSql() throws UnifyException {
		return "SELECT NOW() AT TIME ZONE 'utc'";
	}

	@Override
	public String generateGetCheckConstraintsSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, PrintFormat format)
			throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT conname FROM pg_constraint")
		.append(" JOIN pg_class ON conrelid = pg_class.oid")
		.append(" JOIN pg_namespace ON pg_class.relnamespace = pg_namespace.oid")
		.append(" WHERE contype = 'c'")
		.append(" AND relname = '").append(sqlEntitySchemaInfo.getSchemaTableName()).append("'")
		.append(" AND nspname = '").append(sqlEntitySchemaInfo.getSchema()).append("'");
		return sb.toString();
	}

	@Override
	public String generateDropCheckConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, String checkName,
			PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ").append(sqlEntitySchemaInfo.getSchemaTableName()).append(" DROP CONSTRAINT ")
				.append(checkName);
		return sb.toString();
	}

	@Override
	public String generateDropUniqueConstraintSql(SqlEntitySchemaInfo sqlEntitySchemaInfo,
			String dbUniqueConstraintName, PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		String tableName = sqlEntitySchemaInfo.getSchemaTableName();
		sb.append("ALTER TABLE ").append(tableName);
		if (format.isPretty()) {
			sb.append(getLineSeparator());
		} else {
			sb.append(" ");
		}

		sb.append("DROP CONSTRAINT ").append(dbUniqueConstraintName);
		return sb.toString();
	}

	@Override
	public String generateDropIndexSql(SqlEntitySchemaInfo sqlEntitySchemaInfo, String dbIndexName, PrintFormat format)
			throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("DROP INDEX ").append(dbIndexName);
		return sb.toString();
	}

	@Override
	public String generateRenameColumn(String tableName, String oldColumnName, SqlFieldSchemaInfo sqlFieldSchemaInfo,
			PrintFormat format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ").append(tableName);
		if (format.isPretty()) {
			sb.append(getLineSeparator());
		} else {
			sb.append(' ');
		}
		sb.append("RENAME COLUMN ").append(oldColumnName).append(" TO ")
				.append(sqlFieldSchemaInfo.getPreferredColumnName());
		return sb.toString();
	}

	@Override
	protected void appendAutoIncrementPrimaryKey(StringBuilder sb) {
		sb.append(" GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL");
	}

	@Override
	protected void appendTimestampTruncation(StringBuilder sql, SqlFieldInfo sqlFieldInfo,
			TimeSeriesType timeSeriesType, boolean merge) throws UnifyException {
		if (merge) {
			boolean inc = false;
			sql.append("LPAD((EXTRACT(");
			int len = 1;
			switch (timeSeriesType) {
			case DAY_OF_WEEK:
				sql.append("dow"); // 0- 6
				inc = true;
				break;
			case DAY:
			case DAY_OF_MONTH:
				sql.append("day"); // 1 - 31
				len = 2;
				break;
			case DAY_OF_YEAR:
				sql.append("doy"); // 1 - 366
				len = 3;
				break;
			case HOUR:
				sql.append("hour"); // 0 - 23
				len = 2;
				break;
			case MONTH:
				sql.append("month"); // 1 - 12
				len = 2;
				break;
			case WEEK:
				sql.append("week"); // 1 - 54
				len = 2;
				break;
			case YEAR:
				sql.append("year"); // 1 - 9999
				len = 4;
				break;
			default:
				break;
			}
			sql.append(" FROM ").append(sqlFieldInfo.getPreferredColumnName()).append(")");
			if (inc) {
				sql.append(" + 1");
			}
			
			sql.append(")::text,");
			sql.append(len);
			sql.append(",'0')");
		} else {
			sql.append("DATE_TRUNC('");
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
			sql.append("', ").append(sqlFieldInfo.getPreferredColumnName()).append(")");
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
				if (sqlFieldSchemaInfo.getColumnType().isBlob()) {
					sb.append("''::BYTEA");
				} else if (sqlFieldSchemaInfo.getColumnType().isClob()) {
					sb.append("''");
				} else {
					sqlDataTypePolicy.appendDefaultVal(sb, sqlFieldSchemaInfo.getFieldType(),
							sqlFieldSchemaInfo.getDefaultVal());
				}

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

		sb.append("ALTER COLUMN ").append(sqlFieldSchemaInfo.getColumnName());
		sb.append(" TYPE");
		sqlDataTypePolicy.appendTypeSql(sb, sqlFieldSchemaInfo.getLength(), sqlFieldSchemaInfo.getPrecision(),
				sqlFieldSchemaInfo.getScale());
		if (sqlFieldSchemaInfo.isNullable()) {
			sb.append(", ALTER COLUMN ").append(sqlFieldSchemaInfo.getColumnName());
			sb.append(" DROP NOT NULL");
		} else {
			sb.append(", ALTER COLUMN ").append(sqlFieldSchemaInfo.getColumnName());
			sb.append(" SET NOT NULL");
		}

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
		sb.append("ALTER COLUMN ").append(sqlColumnInfo.getColumnName());
		sb.append(" TYPE");
		appendTypeSql(sb, sqlColumnInfo);
		sb.append(", ALTER COLUMN ").append(sqlColumnInfo.getColumnName());
		sb.append(" DROP NOT NULL");
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
	public boolean isReconstructViewsOnTableSchemaUpdate() throws UnifyException {
		return true;
	}

	@Override
	protected void onInitialize() throws UnifyException {
		super.onInitialize();
		includeNoPrecisionType("INT8");
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

	private static class PostgreSqlDataSourceDialectPolicies extends AbstractSqlDataSourceDialectPolicies {

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
		protected ColumnType dialectSwapColumnType(ColumnType columnType, int length) {
			return columnType.isString() && length > 65535 ? ColumnType.CLOB: columnType;
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

class PostgreSqlBooleanPolicy extends BooleanPolicy {

	@Override
	public String getTypeName() {
		return "BPCHAR";
	}
}

class PostgreSqlCharacterPolicy extends CharacterPolicy {

	@Override
	public String getTypeName() {
		return "BPCHAR";
	}
}

class PostgreSqlBigDecimalPolicy extends BigDecimalPolicy {

	@Override
	public String getTypeName() {
		return "NUMERIC";
	}
}

class PostgreSqlShortPolicy extends ShortPolicy {

	@Override
	public String getTypeName() {
		return "INT2";
	}
}

class PostgreSqlIntegerPolicy extends IntegerPolicy {

	@Override
	public String getTypeName() {
		return "INT4";
	}
}

class PostgreSqlLongPolicy extends LongPolicy {

	@Override
	public String getTypeName() {
		return "INT8";
	}
}

class PostgreSqlDatePolicy extends DatePolicy {

	@Override
	public String getAltDefault(Class<?> fieldType) {
		return "CURRENT_TIMESTAMP";
	}

}

class PostgreSqlTimestampPolicy extends TimestampPolicy {

	@Override
	public String getAltDefault(Class<?> fieldType) {
		return "CURRENT_TIMESTAMP";
	}

}

class PostgreSqlTimestampUTCPolicy extends TimestampUTCPolicy {

	@Override
	public String getAltDefault(Class<?> fieldType) {
		return "CURRENT_TIMESTAMP";
	}

}

class PostgreSqlBlobPolicy extends BlobPolicy {

	@Override
	public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
		sb.append(" BYTEA");
	}

	@Override
	public String getTypeName() {
		return "BYTEA";
	}

	@Override
	public int getSqlType() {
		return Types.BINARY;
	}

	@Override
	public void executeSetPreparedStatement(Object pstmt, int index, Object data, long utcOffset) throws Exception {
		if (data == null) {
			((PreparedStatement) pstmt).setNull(index, Types.BINARY);
		} else {
			byte[] bArray = (byte[]) data;
			((PreparedStatement) pstmt).setBinaryStream(index, new ByteArrayInputStream(bArray), bArray.length);
		}
	}

	@Override
	public Object executeGetResult(Object rs, Class<?> type, String column, long utcOffset) throws Exception {
		return ((ResultSet) rs).getBytes(column);
	}

	@Override
	public Object executeGetResult(Object rs, Class<?> type, int index, long utcOffset) throws Exception {
		return ((ResultSet) rs).getBytes(index);
	}
}

class PostgreSqlClobPolicy extends ClobPolicy {

	@Override
	public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
		sb.append(" TEXT");
	}

	@Override
	public void executeSetPreparedStatement(Object pstmt, int index, Object data, long utcOffset) throws Exception {
		if (data == null) {
			((PreparedStatement) pstmt).setNull(index, Types.VARCHAR);
		} else {
			((PreparedStatement) pstmt).setString(index, (String) data);
		}
	}

	@Override
	public Object executeGetResult(Object rs, Class<?> type, String column, long utcOffset) throws Exception {
		return ((ResultSet) rs).getString(column);
	}

	@Override
	public Object executeGetResult(Object rs, Class<?> type, int index, long utcOffset) throws Exception {
		return ((ResultSet) rs).getString(index);
	}

	@Override
	public void executeRegisterOutParameter(Object cstmt, int index) throws Exception {
		((CallableStatement) cstmt).registerOutParameter(index, Types.VARCHAR);
	}

	@Override
	public Object executeGetOutput(Object cstmt, Class<?> type, int index, long utcOffset) throws Exception {
		return ((CallableStatement) cstmt).getString(index);
	}

	@Override
	public String getTypeName() {
		return "TEXT";
	}

	@Override
	public int getSqlType() {
		return Types.VARCHAR;
	}
}