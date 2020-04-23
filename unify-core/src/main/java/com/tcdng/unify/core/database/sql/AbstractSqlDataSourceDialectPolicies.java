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

package com.tcdng.unify.core.database.sql;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.criterion.RestrictionType;
import com.tcdng.unify.core.util.SqlUtils;

/**
 * Convenient abstract base class for SQL data source dialect policies.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractSqlDataSourceDialectPolicies implements SqlDataSourceDialectPolicies {

    private static final String TIMESTAMP_FORMAT = "''yyyy-MM-dd HH:mm:ss''";

    protected DateFormat timestampFormat;

    protected Map<ColumnType, SqlDataTypePolicy> sqlDataTypePolicies;

    protected Map<RestrictionType, SqlCriteriaPolicy> sqlCriteriaPolicies;

    public AbstractSqlDataSourceDialectPolicies() {
        new SimpleDateFormat(TIMESTAMP_FORMAT);
    }

    @Override
    public Map<ColumnType, SqlDataTypePolicy> getSqlDataTypePolicies() {
        return sqlDataTypePolicies;
    }

    @Override
    public Map<RestrictionType, SqlCriteriaPolicy> getSqlCriteriaPolicies() {
        return sqlCriteriaPolicies;
    }

    @Override
    public SqlDataTypePolicy getSqlTypePolicy(ColumnType columnType) {
        return sqlDataTypePolicies.get(columnType);
    }

    @Override
    public SqlCriteriaPolicy getSqlCriteriaPolicy(RestrictionType restrictionType) {
        return sqlCriteriaPolicies.get(restrictionType);
    }
    
    public int getMaxClauseValues() {
        return 0;
    }
    
    public String translateToNativeSqlParam(Object param) throws UnifyException {
        if (param instanceof String) {
            return "\'" + param + "\'";
        }

        if (param instanceof Date) {
            return timestampFormat.format((Date) param);
        }

        if (param instanceof Boolean) {
            return "\'" + SqlUtils.getString((Boolean) param) + "\'";
        }

        return String.valueOf(param);
    }

    @Override
    public String generateLikeParameter(SqlLikeType type, Object param) throws UnifyException {
        String paramStr = null;
        if (param instanceof SqlViewColumnInfo) {
            SqlViewColumnInfo sqlViewColumnInfo = (SqlViewColumnInfo) param;
            paramStr = sqlViewColumnInfo.getTableAlias() + "." + sqlViewColumnInfo.getColumnName();
        } else {
            paramStr = String.valueOf(param);
        }

        if (type.equals(SqlLikeType.BEGINS_WITH)) {
            return paramStr + "%";
        } else if (type.equals(SqlLikeType.ENDS_WITH)) {
            return "%" + paramStr;
        }

        return "%" + paramStr + "%";
    }

}
