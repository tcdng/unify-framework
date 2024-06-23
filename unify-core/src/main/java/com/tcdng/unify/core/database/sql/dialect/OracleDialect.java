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
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.tcdng.unify.core.database.StaticReference;
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
import com.tcdng.unify.core.database.sql.data.policy.EnumConstPolicy;
import com.tcdng.unify.core.database.sql.data.policy.IntegerPolicy;
import com.tcdng.unify.core.database.sql.data.policy.LongPolicy;
import com.tcdng.unify.core.database.sql.data.policy.ShortPolicy;
import com.tcdng.unify.core.database.sql.data.policy.StringPolicy;
import com.tcdng.unify.core.database.sql.data.policy.TimestampPolicy;
import com.tcdng.unify.core.database.sql.data.policy.TimestampUTCPolicy;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Oracle SQL dialect.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(name = SqlDialectNameConstants.ORACLE, description = "$m{sqldialect.oracledb}")
public class OracleDialect extends AbstractSqlDataSourceDialect {

	private static final OracleDataSourceDialectPolicies sqlDataSourceDialectPolicies = new OracleDataSourceDialectPolicies();

	private static final List<String> RESERVED_IDENTIFIERS = Arrays.asList("CLUSTER", "COLUMN", "COMMENT", "COMPRESS",
			"DATE", "DESC", "SHARE", "ONLINE", "OFFLINE", "USER");

	static {
		Map<ColumnType, SqlDataTypePolicy> tempMap1 = new EnumMap<ColumnType, SqlDataTypePolicy>(ColumnType.class);
		populateDefaultSqlDataTypePolicies(tempMap1);
		tempMap1.put(ColumnType.DATE, new OracleDatePolicy());
		tempMap1.put(ColumnType.TIMESTAMP, new OracleTimestampPolicy());
		tempMap1.put(ColumnType.TIMESTAMP_UTC, new OracleTimestampUTCPolicy());
		tempMap1.put(ColumnType.BLOB, new OracleBlobPolicy());
		tempMap1.put(ColumnType.CLOB, new OracleClobPolicy());
		tempMap1.put(ColumnType.LONG, new OracleLongPolicy());
		tempMap1.put(ColumnType.INTEGER, new OracleIntegerPolicy());
		tempMap1.put(ColumnType.SHORT, new OracleShortPolicy());
		tempMap1.put(ColumnType.STRING, new OracleStringPolicy());
		tempMap1.put(ColumnType.ENUMCONST, new OracleEnumConstPolicy());

		Map<RestrictionType, SqlCriteriaPolicy> tempMap2 = new EnumMap<RestrictionType, SqlCriteriaPolicy>(
				RestrictionType.class);
		populateDefaultSqlCriteriaPolicies(sqlDataSourceDialectPolicies, tempMap2);

		sqlDataSourceDialectPolicies.setSqlDataTypePolicies(DataUtils.unmodifiableMap(tempMap1));
		sqlDataSourceDialectPolicies.setSqlCriteriaPolicies(DataUtils.unmodifiableMap(tempMap2));
	}

	public OracleDialect() {
		super(RESERVED_IDENTIFIERS, false); // useCallableFunctionMode
	}

	@Override
	public String getDefaultSchema() {
		return null;
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
		return "SELECT 1 FROM DUAL";
	}

	@Override
	public String generateUTCTimestampSql() throws UnifyException {
		return "SELECT SYS_EXTRACT_UTC(SYSTIMESTAMP) FROM DUAL";
	}

	@Override
	public String getSqlBlobType() {
		return "oracle.jdbc.OracleBlob";
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
	protected void appendAutoIncrementPrimaryKey(StringBuilder sb) {
		sb.append(" GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL");
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
		appendColumnAndTypeSql(sb, sqlFieldSchemaInfo, sqlColumnAlterInfo);
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
		return false;
	}

	@Override
	public boolean isGeneratesIndexesOnCreateTable() {
		return false;
	}

	@Override
	protected void appendTimestampTruncation(StringBuilder sql, SqlFieldInfo sqlFieldInfo,
			TimeSeriesType timeSeriesType, boolean merge) throws UnifyException {
		if (merge) {
			sql.append("TO_CHAR(").append(sqlFieldInfo.getPreferredColumnName()).append(", '");
			switch (timeSeriesType) {
			case DAY_OF_WEEK:
				sql.append("D"); // 1- 7
				break;
			case DAY:
			case DAY_OF_MONTH:
				sql.append("DD"); // 1 - 31
				break;
			case DAY_OF_YEAR:
				sql.append("DDD"); // 1 - 366
				break;
			case HOUR:
				sql.append("HH24"); // 0 - 23
				break;
			case MONTH:
				sql.append("MM"); // 01 - 12
				break;
			case WEEK:
				sql.append("WW"); // 1 - 53
				break;
			case YEAR:
				sql.append("YYYY"); // 1 - 9999
				break;
			default:
				break;
			}
			sql.append("')");
		} else {
			sql.append("TRUNC(").append(sqlFieldInfo.getPreferredColumnName()).append(", '");
			switch (timeSeriesType) {
			case DAY:
			case DAY_OF_WEEK:
			case DAY_OF_MONTH:
			case DAY_OF_YEAR:
				sql.append("DD");
				break;
			case HOUR:
				sql.append("HH");
				break;
			case MONTH:
				sql.append("MM");
				break;
			case WEEK:
				sql.append("WW");
				break;
			case YEAR:
				sql.append("YY");
				break;
			default:
				break;
			}
			sql.append("')");
		}
	}

	@Override
	protected void appendTimestampTruncationGroupBy(StringBuilder sql, SqlFieldInfo sqlFieldInfo,
			TimeSeriesType timeSeriesType, boolean merge) throws UnifyException {
		appendTimestampTruncation(sql, sqlFieldInfo, timeSeriesType, merge);
	}

	@Override
	protected boolean appendLimitOffsetInfixClause(StringBuilder sql, int offset, int limit) throws UnifyException {
		return false;
	}

	@Override
	protected boolean appendWhereLimitOffsetSuffixClause(StringBuilder sql, int offset, int limit, boolean append)
			throws UnifyException {
		if (offset > 0) {
			throw new UnifyException(UnifyCoreErrorConstants.QUERY_RESULT_OFFSET_NOT_SUPPORTED);
		}

		if (limit > 0) {
			if (append) {
				sql.append(" AND ROWNUM <= ").append(limit);
			} else {
				sql.append(" WHERE ROWNUM <= ").append(limit);
			}
			return true;
		}
		return false;
	}

	@Override
	protected boolean appendLimitOffsetSuffixClause(StringBuilder sql, int offset, int limit, boolean append)
			throws UnifyException {
		return false;
	}

	@Override
	protected SqlDataSourceDialectPolicies getSqlDataSourceDialectPolicies() {
		return sqlDataSourceDialectPolicies;
	}

	private static class OracleDataSourceDialectPolicies extends AbstractSqlDataSourceDialectPolicies {

		public OracleDataSourceDialectPolicies() {
			timestampFormat = new SimpleDateFormat(
					"('TO_TIMESTAMP'(''yyyy-MM-dd HH:mm:ss'', '''yyyy-MM-dd HH24:mi:ss'''))");
		}

		public void setSqlDataTypePolicies(Map<ColumnType, SqlDataTypePolicy> sqlDataTypePolicies) {
			this.sqlDataTypePolicies = sqlDataTypePolicies;
		}

		public void setSqlCriteriaPolicies(Map<RestrictionType, SqlCriteriaPolicy> sqlCriteriaPolicies) {
			this.sqlCriteriaPolicies = sqlCriteriaPolicies;
		}

		@Override
		public int getMaxClauseValues() {
			return 1000;
		}

		@Override
		protected ColumnType dialectSwapColumnType(ColumnType columnType, int length) {
			return columnType.isString() && length > 4000 ? ColumnType.CLOB : columnType;
		}

		@Override
		protected String concat(String... expressions) {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			boolean appSym = false;
			for (String expression : expressions) {
				if (appSym) {
					sb.append(" || ");
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

class OracleEnumConstPolicy extends EnumConstPolicy {

	@Override
	public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
		if (length <= 0) {
			length = StaticReference.CODE_LENGTH;
		}
		sb.append(" VARCHAR2(").append(length).append(')');
	}

	@Override
	public String getTypeName() {
		return "VARCHAR2";
	}
}

class OracleStringPolicy extends StringPolicy {

	@Override
	public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
		if (length <= 0) {
			length = DEFAULT_LENGTH;
		}
		sb.append(" VARCHAR2(").append(length).append(')');
	}

	@Override
	public String getTypeName() {
		return "VARCHAR2";
	}
}

class OracleDatePolicy extends DatePolicy {

	@Override
	public String getAltDefault(Class<?> fieldType) {
		return "CURRENT_TIMESTAMP";
	}

}

class OracleTimestampPolicy extends TimestampPolicy {

	@Override
	public String getAltDefault(Class<?> fieldType) {
		return "CURRENT_TIMESTAMP";
	}

	@Override
	public String getTypeName() {
		return "TIMESTAMP(6)";
	}

}

class OracleTimestampUTCPolicy extends TimestampUTCPolicy {

	@Override
	public String getAltDefault(Class<?> fieldType) {
		return "CURRENT_TIMESTAMP";
	}

	@Override
	public String getTypeName() {
		return "TIMESTAMP(6)";
	}

}

class OracleLongPolicy extends LongPolicy {

	@Override
	public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
		sb.append(" NUMBER(").append(precision).append(")");
	}

	@Override
	public String getTypeName() {
		return "NUMBER";
	}

	@Override
	public int getSqlType() {
		return Types.DECIMAL;
	}

	@Override
	public boolean isFixedLength() {
		return false;
	}
}

class OracleIntegerPolicy extends IntegerPolicy {

	@Override
	public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
		sb.append(" NUMBER(").append(precision).append(")");
	}

	@Override
	public String getTypeName() {
		return "NUMBER";
	}

	@Override
	public int getSqlType() {
		return Types.DECIMAL;
	}

	@Override
	public boolean isFixedLength() {
		return false;
	}
}

class OracleShortPolicy extends ShortPolicy {

	@Override
	public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
		sb.append(" NUMBER(").append(precision).append(")");
	}

	@Override
	public String getTypeName() {
		return "NUMBER";
	}

	@Override
	public int getSqlType() {
		return Types.DECIMAL;
	}

	@Override
	public boolean isFixedLength() {
		return false;
	}
}

class OracleBlobPolicy extends BlobPolicy {

	@Override
	public void executeSetPreparedStatement(Object pstmt, int index, Object data, long timeZoneRawOffset)
			throws Exception {
		if (data == null || ((byte[]) data).length == 0) {
			((PreparedStatement) pstmt).setNull(index, Types.BLOB);
		} else {
			((PreparedStatement) pstmt).setBinaryStream(index, new ByteArrayInputStream((byte[]) data),
					((byte[]) data).length);
		}
	}

}

class OracleClobPolicy extends ClobPolicy {

	@Override
	public void executeSetPreparedStatement(Object pstmt, int index, Object data, long timeZoneRawOffset)
			throws Exception {
		if (data == null || ((String) data).isEmpty()) {
			((PreparedStatement) pstmt).setNull(index, Types.CLOB);
		} else {
			((PreparedStatement) pstmt).setCharacterStream(index, new StringReader((String) data),
					((String) data).length());
		}
	}

}