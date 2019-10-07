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
package com.tcdng.unify.core.database.sql;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.criterion.RestrictionType;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Base abstract SQL criteria policy.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractSqlCriteriaPolicy implements SqlCriteriaPolicy {

    protected SqlDataSourceDialect sqlDataSourceDialect;

    protected String opSql;

    public AbstractSqlCriteriaPolicy(String opSql, SqlDataSourceDialect sqlDataSourceDialect) {
        this.sqlDataSourceDialect = sqlDataSourceDialect;
        this.opSql = opSql;
    }

    @Override
    public void translate(StringBuilder sql, String tableName, String columnName, Object param1, Object param2)
            throws UnifyException {
        doTranslate(sql, tableName, columnName, resolveParam(param1), resolveParam(param2));
    }

    /**
     * Returns the SQL criteria policy for supplied restriction.
     * 
     * @param criteria
     *            the criteria object
     * @return the criteria policy
     * @throws UnifyException
     *             if an error occurs
     */
    protected SqlCriteriaPolicy getOperatorPolicy(Restriction restriction) throws UnifyException {
        return sqlDataSourceDialect.getSqlCriteriaPolicy(restriction.getType());
    }

    /**
     * Returns the SQL criteria policy for supplied restriction type.
     * 
     * @param type
     *            the restriction type
     * @return the criteris policy
     * @throws UnifyException
     *             if an error occurs
     */
    protected SqlCriteriaPolicy getOperatorPolicy(RestrictionType type) throws UnifyException {
        return sqlDataSourceDialect.getSqlCriteriaPolicy(type);
    }

    /**
     * Returns SQL data type policy for column type.
     * 
     * @param columnType
     *            the column type
     * @throws UnifyException
     *             if column type is not supported
     */
    protected SqlDataTypePolicy getSqlTypePolicy(ColumnType columnType) throws UnifyException {
        return sqlDataSourceDialect.getSqlTypePolicy(columnType);
    }

    /**
     * Converts a value to its native SQL string equivalent.
     * 
     * @param value
     *            the value to convert
     * @return the converted value
     * @throws UnifyException
     *             if an error occurs
     */
    protected String getNativeSqlStringValue(Object value) throws UnifyException {
        return sqlDataSourceDialect.translateValue(value);
    }

    /**
     * Returns the minimum clause values.
     */
    protected int maximumClauseValues() {
        return sqlDataSourceDialect.getMaxClauseValues();
    }

    /**
     * Converts value to field type if necessary.
     * 
     * @param sqlFieldInfo
     *            the field information
     * @param postOp
     *            the value
     * @return the converted value
     * @throws UnifyException
     *             if an error occurs
     */
    protected Object convertType(SqlFieldInfo sqlFieldInfo, Object postOp) throws UnifyException {
        if (postOp != null && !postOp.getClass().equals(sqlFieldInfo.getFieldType())) {
            return DataUtils.convert(sqlFieldInfo.getFieldType(), postOp, null);
        }

        return postOp;
    }

    /**
     * Resolves parameter.
     * 
     * @param param
     *            the parameter to resolve
     * @return the resolved parameter
     * @throws UnifyException
     *             if an error occurs
     */
    protected Object resolveParam(Object param) throws UnifyException {
        if (param instanceof SqlViewColumnInfo) {
            SqlViewColumnInfo sqlViewColumnInfo = (SqlViewColumnInfo) param;
            return sqlViewColumnInfo.getTableAlias() + "." + sqlViewColumnInfo.getColumnName();
        }
        
        return param;
    }

    protected abstract void doTranslate(StringBuilder sql, String tableName, String columnName, Object param1,
            Object param2) throws UnifyException;

    protected SqlDataSourceDialect getSqlDataSourceDialect() {
        return sqlDataSourceDialect;
    }
}
