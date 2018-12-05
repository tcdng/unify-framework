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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.SqlDialectConstants;
import com.tcdng.unify.core.database.sql.AbstractSqlDataSourceDialect;
import com.tcdng.unify.core.database.sql.SqlEntitySchemaInfo;
import com.tcdng.unify.core.database.sql.SqlFieldSchemaInfo;
import com.tcdng.unify.core.database.sql.SqlUniqueConstraintSchemaInfo;
import com.tcdng.unify.core.database.sql.policy.BlobPolicy;

/**
 * MySQL SQL dialect.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(name = SqlDialectConstants.MYSQL, description = "$m{sqldialect.mysqldb}")
public class MySqlDialect extends AbstractSqlDataSourceDialect {

	public MySqlDialect() {
		super(true); // Append NULL on table create
	}

	@Override
	public String generateTestSql() throws UnifyException {
		return "SELECT 1";
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
	public String generateDropUniqueConstraintSql(SqlEntitySchemaInfo sqlRecordSchemaInfo,
			SqlUniqueConstraintSchemaInfo sqlUniqueConstraintInfo, boolean format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		String tableName = sqlRecordSchemaInfo.getTable();
		sb.append("ALTER TABLE ").append(tableName);
		if (format) {
			sb.append(this.getLineSeparator());
		} else {
			sb.append(" ");
		}
		sb.append("DROP INDEX ").append(tableName).append("_").append(sqlUniqueConstraintInfo.getName().toUpperCase())
				.append("UK");
		return sb.toString();
	}

	@Override
	public String generateRenameColumn(SqlEntitySchemaInfo sqlRecordSchemaInfo, SqlFieldSchemaInfo sqlFieldSchemaInfo,
			SqlFieldSchemaInfo oldSqlFieldSchemaInfo, boolean format) throws UnifyException {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ").append(sqlRecordSchemaInfo.getTable());
		if (format) {
			sb.append(this.getLineSeparator());
		} else {
			sb.append(' ');
		}
		sb.append("CHANGE COLUMN ").append(oldSqlFieldSchemaInfo.getColumn()).append(" ");
		this.appendCreateTableColumnSQL(sb, sqlFieldSchemaInfo);
		return sb.toString();
	}

	@Override
	protected void onInitialize() throws UnifyException {
		super.onInitialize();

		this.setDataTypePolicy(ColumnType.BLOB, new MySqlBlobPolicy());
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
}

class MySqlBlobPolicy extends BlobPolicy {

	@Override
	public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
		sb.append("MEDIUMBLOB");
	}
}