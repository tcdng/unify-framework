/*
 * Copyright 2018-2019 The Code Department.
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
import java.util.Collections;
import java.util.List;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.SqlDialectConstants;
import com.tcdng.unify.core.database.sql.AbstractSqlDataSourceDialect;
import com.tcdng.unify.core.database.sql.SqlColumnAlterInfo;
import com.tcdng.unify.core.database.sql.SqlColumnInfo;
import com.tcdng.unify.core.database.sql.SqlDataTypePolicy;
import com.tcdng.unify.core.database.sql.SqlEntitySchemaInfo;
import com.tcdng.unify.core.database.sql.SqlFieldSchemaInfo;
import com.tcdng.unify.core.database.sql.policy.BlobPolicy;
import com.tcdng.unify.core.database.sql.policy.ClobPolicy;
import com.tcdng.unify.core.database.sql.policy.IntegerPolicy;
import com.tcdng.unify.core.database.sql.policy.LongPolicy;
import com.tcdng.unify.core.database.sql.policy.ShortPolicy;
import com.tcdng.unify.core.util.StringUtils;

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
    public String generateUTCTimestampSql() throws UnifyException {
        return "SELECT SYS_EXTRACT_UTC(SYSTIMESTAMP) FROM DUAL";
    }

    @Override
    public int getMaxClauseValues() {
        return 1000;
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
            sb.append("MODIFY ");
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
        sb.append("MODIFY ").append(sqlColumnInfo.getColumnName());
        appendTypeSql(sb, sqlColumnInfo);
        sb.append(" NULL");
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
        setDataTypePolicy(ColumnType.INTEGER, new OracleIntegerPolicy());
        setDataTypePolicy(ColumnType.SHORT, new OracleShortPolicy());

        setTimestampFormat(
                new SimpleDateFormat("('TO_TIMESTAMP'(''yyyy-MM-dd HH:mm:ss'', '''yyyy-MM-dd HH24:mi:ss'''))"));
    }
}

class OracleLongPolicy extends LongPolicy {

    @Override
    public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
        sb.append(" NUMBER(").append(precision).append(")");
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
    public void executeSetPreparedStatement(Object pstmt, int index, Object data, long timeZoneRawOffset) throws Exception {
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
    public void executeSetPreparedStatement(Object pstmt, int index, Object data, long timeZoneRawOffset) throws Exception {
        if (data == null || ((String) data).isEmpty()) {
            ((PreparedStatement) pstmt).setNull(index, Types.CLOB);
        } else {
            ((PreparedStatement) pstmt).setCharacterStream(index, new StringReader((String) data),
                    ((String) data).length());
        }
    }

}