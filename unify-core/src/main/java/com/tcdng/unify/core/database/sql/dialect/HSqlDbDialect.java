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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.SqlDialectConstants;
import com.tcdng.unify.core.database.sql.AbstractSqlDataSourceDialect;
import com.tcdng.unify.core.database.sql.SqlColumnInfo;
import com.tcdng.unify.core.database.sql.SqlEntitySchemaInfo;
import com.tcdng.unify.core.database.sql.SqlFieldSchemaInfo;
import com.tcdng.unify.core.database.sql.SqlShutdownHook;
import com.tcdng.unify.core.database.sql.SqlUtils;

/**
 * HSQLDB SQL dialect.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(name = SqlDialectConstants.HSQLDB, description = "$m{sqldialect.hsqldb}")
public class HSqlDbDialect extends AbstractSqlDataSourceDialect {

    private SqlShutdownHook sqlShutdownHook = new HSqlDbShutdownHook();

    @Override
    public int getMaxClauseValues() {
        return -1;
    }

    @Override
    public String generateTestSql() throws UnifyException {
        return "VALUES CURRENT_TIMESTAMP";
    }

    @Override
    public String generateUTCTimestampSql() throws UnifyException {
        return "VALUES TIMESTAMPADD(SQL_TSI_MINUTE, -EXTRACT(TIMEZONE_MINUTE FROM CURRENT_TIMESTAMP), TIMESTAMPADD(SQL_TSI_HOUR, -EXTRACT(TIMEZONE_HOUR FROM CURRENT_TIMESTAMP), CURRENT_TIMESTAMP))";
    }

    @Override
    public SqlShutdownHook getShutdownHook() throws UnifyException {
        return sqlShutdownHook;
    }

    @Override
    public String generateAddColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlFieldSchemaInfo sqlFieldSchemaInfo,
            boolean format) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(sqlEntitySchemaInfo.getTable());
        if (format) {
            sb.append(getLineSeparator());
        } else {
            sb.append(' ');
        }
        sb.append("ADD COLUMN ");
        appendCreateTableColumnSQL(sb, sqlFieldSchemaInfo);
        return sb.toString();
    }

    @Override
    public String generateAlterColumnNull(SqlEntitySchemaInfo sqlEntitySchemaInfo, SqlColumnInfo sqlColumnInfo,
            boolean format) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(sqlEntitySchemaInfo.getTable());
        if (format) {
            sb.append(getLineSeparator());
        } else {
            sb.append(' ');
        }
        sb.append("ALTER COLUMN ").append(sqlColumnInfo.getColumnName()).append(" SET NULL");
        return sb.toString();
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

    private class HSqlDbShutdownHook implements SqlShutdownHook {

        @Override
        public void commandShutdown(Connection connection) throws UnifyException {
            Statement st = null;
            try {
                st = connection.createStatement();
                st.execute("SHUTDOWN");
            } catch (SQLException e) {
                throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, getName());
            } finally {
                SqlUtils.close(st);
            }
        }
    }
}
