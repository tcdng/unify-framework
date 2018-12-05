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
package com.tcdng.unify.core.database.sql.dialect;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.SqlDialectConstants;
import com.tcdng.unify.core.database.sql.AbstractSqlDataSourceDialect;
import com.tcdng.unify.core.database.sql.SqlEntitySchemaInfo;
import com.tcdng.unify.core.database.sql.SqlFieldSchemaInfo;

/**
 * MS SQL dialect.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(name = SqlDialectConstants.MSSQL, description = "$m{sqldialect.mssqldb}")
public class MsSqlDialect extends AbstractSqlDataSourceDialect {

	@Override
	public String generateTestSql() throws UnifyException {
		return "SELECT CURRENT_TIMESTAMP";
	}

	@Override
	public String generateNowSql() throws UnifyException {
		return "SELECT CURRENT_TIMESTAMP";
	}

	@Override
	public int getMaxClauseValues() {
		return -1;
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
			SqlEntitySchemaInfo oldSqlRecordSchemaInfo, boolean format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("sp_RENAME '").append(oldSqlRecordSchemaInfo.getTable()).append('.')
				.append(sqlRecordSchemaInfo.getTable()).append("'");
		return sb.toString();
	}

	@Override
	public String generateAlterColumn(SqlEntitySchemaInfo sqlRecordSchemaInfo, SqlFieldSchemaInfo sqlFieldSchemaInfo,
			boolean format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ").append(sqlRecordSchemaInfo.getTable());
		if (format) {
			sb.append(this.getLineSeparator());
		} else {
			sb.append(' ');
		}
		sb.append("ALTER COLUMN ");
		this.appendCreateTableColumnSQL(sb, sqlFieldSchemaInfo);
		return sb.toString();
	}

	@Override
	public String generateRenameColumn(SqlEntitySchemaInfo sqlRecordSchemaInfo, SqlFieldSchemaInfo sqlFieldSchemaInfo,
			SqlFieldSchemaInfo oldSqlFieldSchemaInfo, boolean format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("sp_RENAME '").append(sqlRecordSchemaInfo.getTable()).append('.')
				.append(oldSqlFieldSchemaInfo.getColumn()).append("', '");
		sb.append(sqlFieldSchemaInfo.getColumn()).append("' , 'COLUMN'");
		return sb.toString();
	}

	@Override
	public String generateDropColumn(SqlEntitySchemaInfo sqlRecordSchemaInfo, SqlFieldSchemaInfo sqlFieldSchemaInfo,
			boolean format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ").append(sqlRecordSchemaInfo.getTable());
		if (format) {
			sb.append(this.getLineSeparator());
		} else {
			sb.append(' ');
		}
		sb.append("DROP COLUMN ").append(sqlFieldSchemaInfo.getColumn());
		return sb.toString();
	}
}
