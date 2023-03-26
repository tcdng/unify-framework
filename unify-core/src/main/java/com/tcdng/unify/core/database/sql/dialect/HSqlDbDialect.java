/*
 * Copyright 2018-2023 The Code Department.
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
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyOperationException;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.PrintFormat;
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
import com.tcdng.unify.core.database.sql.SqlFieldSchemaInfo;
import com.tcdng.unify.core.database.sql.SqlShutdownHook;
import com.tcdng.unify.core.database.sql.data.policy.BooleanPolicy;
import com.tcdng.unify.core.database.sql.data.policy.CharacterPolicy;
import com.tcdng.unify.core.database.sql.data.policy.DatePolicy;
import com.tcdng.unify.core.database.sql.data.policy.DoublePolicy;
import com.tcdng.unify.core.database.sql.data.policy.IntegerPolicy;
import com.tcdng.unify.core.database.sql.data.policy.TimestampPolicy;
import com.tcdng.unify.core.database.sql.data.policy.TimestampUTCPolicy;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.SqlUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * HSQLDB SQL dialect.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(name = SqlDialectNameConstants.HSQLDB, description = "$m{sqldialect.hsqldb}")
public class HSqlDbDialect extends AbstractSqlDataSourceDialect {

    private static final HsqlDbDataSourceDialectPolicies sqlDataSourceDialectPolicies =
            new HsqlDbDataSourceDialectPolicies();

    static {
        Map<ColumnType, SqlDataTypePolicy> tempMap1 = new EnumMap<ColumnType, SqlDataTypePolicy>(ColumnType.class);
        populateDefaultSqlDataTypePolicies(tempMap1);
        tempMap1.put(ColumnType.BOOLEAN, new HSqlBooleanPolicy());
        tempMap1.put(ColumnType.CHARACTER, new HSqlCharacterPolicy());
        tempMap1.put(ColumnType.DOUBLE, new HSqlDoublePolicy());
        tempMap1.put(ColumnType.INTEGER, new HSqlIntegerPolicy());

        tempMap1.put(ColumnType.DATE, new HSqlDbDatePolicy());
        tempMap1.put(ColumnType.TIMESTAMP, new HSqlDbTimestampPolicy());
        tempMap1.put(ColumnType.TIMESTAMP_UTC, new HSqlDbTimestampUTCPolicy());

        Map<RestrictionType, SqlCriteriaPolicy> tempMap2 = new EnumMap<RestrictionType, SqlCriteriaPolicy>(RestrictionType.class);
        populateDefaultSqlCriteriaPolicies(sqlDataSourceDialectPolicies, tempMap2);

        sqlDataSourceDialectPolicies.setSqlDataTypePolicies(DataUtils.unmodifiableMap(tempMap1));
        sqlDataSourceDialectPolicies.setSqlCriteriaPolicies(DataUtils.unmodifiableMap(tempMap2));
    }

    private SqlShutdownHook sqlShutdownHook = new HSqlDbShutdownHook();

    public HSqlDbDialect() {
        super(true); // useCallableFunctionMode
    }

    @Override
    public String getDefaultSchema() {
        return "PUBLIC";
    }

    @Override
    public boolean matchColumnDefault(String nativeVal, String defaultVal) throws UnifyException {
        if(super.matchColumnDefault(nativeVal, defaultVal)) {
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
    protected List<String> doGenerateAlterColumn(SqlEntitySchemaInfo sqlEntitySchemaInfo,
            SqlFieldSchemaInfo sqlFieldSchemaInfo, SqlColumnAlterInfo sqlColumnAlterInfo, PrintFormat format)
            throws UnifyException {
        List<String> sqlList = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        SqlDataTypePolicy sqlDataTypePolicy = getSqlTypePolicy(sqlFieldSchemaInfo.getColumnType());
        if (sqlColumnAlterInfo.isDataChange()) {
            sb.append("ALTER TABLE ").append(sqlEntitySchemaInfo.getSchemaTableName());
            if (format.isPretty()) {
                sb.append(getLineSeparator());
            } else {
                sb.append(' ');
            }
            sb.append("ALTER COLUMN ").append(sqlFieldSchemaInfo.getPreferredColumnName());
            sqlDataTypePolicy.appendTypeSql(sb, sqlFieldSchemaInfo.getLength(), sqlFieldSchemaInfo.getPrecision(),
                    sqlFieldSchemaInfo.getScale());
            sqlList.add(sb.toString());
            StringUtils.truncate(sb);
        }

        if (sqlColumnAlterInfo.isDefaultChange()) {
            sb.append("ALTER TABLE ").append(sqlEntitySchemaInfo.getSchemaTableName());
            if (format.isPretty()) {
                sb.append(getLineSeparator());
            } else {
                sb.append(' ');
            }
            sb.append("ALTER COLUMN ").append(sqlFieldSchemaInfo.getPreferredColumnName()).append(" SET");
            if (!sqlFieldSchemaInfo.isNullable() || sqlFieldSchemaInfo.isWithDefaultVal()) {
                sqlDataTypePolicy.appendDefaultSql(sb, sqlFieldSchemaInfo.getFieldType(),
                        sqlFieldSchemaInfo.getDefaultVal());
            } else {
                sb.append(" DEFAULT NULL");
            }

            sqlList.add(sb.toString());
            StringUtils.truncate(sb);
        }

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

            sb.append("ALTER TABLE ").append(sqlEntitySchemaInfo.getSchemaTableName());
            if (format.isPretty()) {
                sb.append(getLineSeparator());
            } else {
                sb.append(' ');
            }
            sb.append("ALTER COLUMN ").append(sqlFieldSchemaInfo.getPreferredColumnName());
            if (sqlFieldSchemaInfo.isNullable()) {
                sb.append(" SET NULL");
            } else {
                sb.append(" SET NOT NULL");
            }
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
        sb.append("ALTER COLUMN ").append(sqlColumnInfo.getColumnName()).append(" SET NULL");
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

    private static class HsqlDbDataSourceDialectPolicies extends AbstractSqlDataSourceDialectPolicies {

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

    private class HSqlDbShutdownHook implements SqlShutdownHook {

        @Override
        public void commandShutdown(Connection connection) throws UnifyException {
            Statement st = null;
            try {
                st = connection.createStatement();
                st.execute("SHUTDOWN");
            } catch (SQLException e) {
                throw new UnifyOperationException(e, getName());
            } finally {
                SqlUtils.close(st);
            }
        }
    }
}


class HSqlBooleanPolicy extends BooleanPolicy {

    @Override
	public String getTypeName() {
		return "CHARACTER";
	}
}

class HSqlCharacterPolicy extends CharacterPolicy {

    @Override
	public String getTypeName() {
		return "CHARACTER";
	}
}

class HSqlDoublePolicy extends DoublePolicy {

    @Override
	public String getTypeName() {
		return "DOUBLE";
	}
}

class HSqlIntegerPolicy extends IntegerPolicy {

    @Override
	public String getTypeName() {
		return "INTEGER";
	}
}

class HSqlDbDatePolicy extends DatePolicy {

	@Override
	public String getAltDefault(Class<?> fieldType) {
        return "CURRENT_DATE";
	}
	
}

class HSqlDbTimestampPolicy extends TimestampPolicy {

	@Override
	public String getAltDefault(Class<?> fieldType) {
        return "CURRENT_TIMESTAMP";
	}
	
}

class HSqlDbTimestampUTCPolicy extends TimestampUTCPolicy {

	@Override
	public String getAltDefault(Class<?> fieldType) {
        return "CURRENT_TIMESTAMP";
	}
	
}