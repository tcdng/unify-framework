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
package com.tcdng.unify.core.database.sql.dialect;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.text.SimpleDateFormat;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.SqlDialectConstants;
import com.tcdng.unify.core.database.sql.AbstractSqlDataSourceDialect;
import com.tcdng.unify.core.database.sql.SqlEntitySchemaInfo;
import com.tcdng.unify.core.database.sql.SqlFieldSchemaInfo;
import com.tcdng.unify.core.database.sql.policy.BlobPolicy;
import com.tcdng.unify.core.database.sql.policy.ClobPolicy;
import com.tcdng.unify.core.database.sql.policy.LongPolicy;

/**
 * Oracle SQL dialect.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(name = SqlDialectConstants.ORACLE, description = "$m{sqldialect.oracledb}")
public class OracleDialect extends AbstractSqlDataSourceDialect {

    @Override
    public String generateTestSql() throws UnifyException {
        return "SELECT 1 FROM DUAL";
    }

    @Override
    public String generateNowSql() throws UnifyException {
        return "SELECT LOCALTIMESTAMP FROM DUAL";
    }

    @Override
    public int getMaxClauseValues() {
        return 1000;
    }

    @Override
    public String generateDropColumn(SqlEntitySchemaInfo sqlRecordSchemaInfo, SqlFieldSchemaInfo sqlFieldSchemaInfo,
            boolean format) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(sqlRecordSchemaInfo.getTable());
        if (format) {
            sb.append(getLineSeparator());
        } else {
            sb.append(' ');
        }
        sb.append("DROP COLUMN ").append(sqlFieldSchemaInfo.getColumn());
        return sb.toString();
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
    protected void onInitialize() throws UnifyException {
        super.onInitialize();

        setDataTypePolicy(ColumnType.BLOB, new OracleBlobPolicy());
        setDataTypePolicy(ColumnType.CLOB, new OracleClobPolicy());
        setDataTypePolicy(ColumnType.LONG, new OracleLongPolicy());

        setTimestampFormat(
                new SimpleDateFormat("('TO_TIMESTAMP'(''yyyy-MM-dd HH:mm:ss'', '''yyyy-MM-dd HH24:mi:ss'''))"));
    }
}

class OracleLongPolicy extends LongPolicy {

    @Override
    public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
        sb.append("NUMBER(19)");
    }
}

class OracleBlobPolicy extends BlobPolicy {

    @Override
    public void executeSetPreparedStatement(Object pstmt, int index, Object data) throws Exception {
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
    public void executeSetPreparedStatement(Object pstmt, int index, Object data) throws Exception {
        if (data == null || ((String) data).isEmpty()) {
            ((PreparedStatement) pstmt).setNull(index, Types.CLOB);
        } else {
            ((PreparedStatement) pstmt).setCharacterStream(index, new StringReader((String) data),
                    ((String) data).length());
        }
    }

}