/*
 * Copyright 2018-2020 The Code Department.
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
import java.util.Collections;
import java.util.List;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.database.sql.AbstractSqlDataSourceDialect;
import com.tcdng.unify.core.database.sql.SqlColumnAlterInfo;
import com.tcdng.unify.core.database.sql.SqlColumnInfo;
import com.tcdng.unify.core.database.sql.SqlDataTypePolicy;
import com.tcdng.unify.core.database.sql.SqlDialectNameConstants;
import com.tcdng.unify.core.database.sql.SqlEntitySchemaInfo;
import com.tcdng.unify.core.database.sql.SqlFieldSchemaInfo;
import com.tcdng.unify.core.database.sql.data.policy.BlobPolicy;
import com.tcdng.unify.core.database.sql.data.policy.ClobPolicy;
import com.tcdng.unify.core.database.sql.data.policy.DatePolicy;
import com.tcdng.unify.core.database.sql.data.policy.TimestampPolicy;
import com.tcdng.unify.core.database.sql.data.policy.TimestampUTCPolicy;
import com.tcdng.unify.core.util.StringUtils;

/**
 * MS SQL dialect.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(name = SqlDialectNameConstants.MSSQL, description = "$m{sqldialect.mssqldb}")
public class MsSqlDialect extends AbstractSqlDataSourceDialect {

    public MsSqlDialect() {
        super(false); // useCallableFunctionMode
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
        sb.append("sp_RENAME '").append(oldSqlRecordSchemaInfo.getSchemaTableName()).append('.')
                .append(sqlRecordSchemaInfo.getSchemaTableName()).append("'");
        return sb.toString();
    }


    @Override
    public String generateAddColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlFieldSchemaInfo sqlFieldSchemaInfo,
            boolean format) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(sqlEntitySchemaInfo.getSchemaTableName());
        if (format) {
            sb.append(getLineSeparator());
        } else {
            sb.append(' ');
        }
        sb.append("ADD ");
        appendColumnAndTypeSql(sb, sqlFieldSchemaInfo, true);
        return sb.toString();
    }

    @Override
    public List<String> generateAlterColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo,
            SqlFieldSchemaInfo sqlFieldSchemaInfo, SqlColumnAlterInfo sqlColumnAlterInfo, boolean format)
            throws UnifyException {
        if (sqlColumnAlterInfo.isAltered()) {
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
            if (format) {
                sb.append(getLineSeparator());
            } else {
                sb.append(' ');
            }
            sb.append("ALTER COLUMN ");
            appendColumnAndTypeSql(sb, sqlFieldSchemaInfo, true);
            sqlList.add(sb.toString());
            StringUtils.truncate(sb);
            return sqlList;
        }

        return Collections.emptyList();
    }

    @Override
    public String generateAlterColumnNull(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlColumnInfo sqlColumnInfo,
            boolean format) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(sqlEntitySchemaInfo.getSchemaTableName());
        if (format) {
            sb.append(getLineSeparator());
        } else {
            sb.append(' ');
        }
        sb.append("ALTER COLUMN ");
        appendTypeSql(sb, sqlColumnInfo);
        sb.append(" NULL");
        return sb.toString();
    }

    @Override
    public String generateRenameColumn(SqlEntitySchemaInfo sqlRecordSchemaInfo, SqlFieldSchemaInfo sqlFieldSchemaInfo,
            SqlFieldSchemaInfo oldSqlFieldSchemaInfo, boolean format) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        sb.append("sp_RENAME '").append(sqlRecordSchemaInfo.getSchemaTableName()).append('.')
                .append(oldSqlFieldSchemaInfo.getPreferredColumnName()).append("', '");
        sb.append(sqlFieldSchemaInfo.getPreferredColumnName()).append("' , 'COLUMN'");
        return sb.toString();
    }

    @Override
    public String generateDropColumn(SqlEntitySchemaInfo sqlRecordSchemaInfo, SqlFieldSchemaInfo sqlFieldSchemaInfo,
            boolean format) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(sqlRecordSchemaInfo.getSchemaTableName());
        if (format) {
            sb.append(getLineSeparator());
        } else {
            sb.append(' ');
        }
        sb.append("DROP COLUMN ").append(sqlFieldSchemaInfo.getPreferredColumnName());
        return sb.toString();
    }

    @Override
    protected void onInitialize() throws UnifyException {
        super.onInitialize();

        setDataTypePolicy(ColumnType.TIMESTAMP_UTC, new MsSqlTimestampUTCPolicy());
        setDataTypePolicy(ColumnType.TIMESTAMP, new MsSqlTimestampPolicy());
        setDataTypePolicy(ColumnType.DATE, new MsSqlDatePolicy());
        setDataTypePolicy(ColumnType.BLOB, new MsSqlBlobPolicy());
        setDataTypePolicy(ColumnType.CLOB, new MsSqlClobPolicy());
    }
}

class MsSqlTimestampUTCPolicy extends TimestampUTCPolicy {

    @Override
    public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
        sb.append(" DATETIME");
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
    public int getSqlType() {
        return Types.LONGVARCHAR;
    }

}