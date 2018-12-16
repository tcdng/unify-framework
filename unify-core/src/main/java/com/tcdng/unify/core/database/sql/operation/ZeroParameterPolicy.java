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
package com.tcdng.unify.core.database.sql.operation;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.sql.AbstractSqlCriteriaPolicy;
import com.tcdng.unify.core.database.sql.SqlDataSourceDialect;
import com.tcdng.unify.core.database.sql.SqlEntityInfo;
import com.tcdng.unify.core.database.sql.SqlFieldInfo;
import com.tcdng.unify.core.database.sql.SqlParameter;
import com.tcdng.unify.core.operation.Criteria;

/**
 * Base no parameter operator policy.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ZeroParameterPolicy extends AbstractSqlCriteriaPolicy {

    public ZeroParameterPolicy(String opSql, final SqlDataSourceDialect sqlDataSourceDialect) {
        super(opSql, sqlDataSourceDialect);
    }

    @Override
    public void translate(StringBuilder sql, SqlEntityInfo sqlEntityInfo, Criteria criteria) throws UnifyException {
        String preOp = (String) criteria.getPreOp();
        if (sqlEntityInfo != null) {
            preOp = sqlEntityInfo.getListFieldInfo(preOp).getColumn();
        }
        translate(sql, sqlEntityInfo.getTableAlias(), preOp, null, null);
    }

    @Override
    public void translate(StringBuilder sql, String tableName, String columnName, Object param1, Object param2)
            throws UnifyException {
        sql.append("(");
        sql.append(tableName).append('.').append(columnName).append(opSql);
        sql.append(")");
    }

    @Override
    public void generatePreparedStatementCriteria(StringBuilder sql, final List<SqlParameter> parameterInfoList,
            SqlEntityInfo sqlEntityInfo, final Criteria criteria) throws UnifyException {
        SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getListFieldInfo((String) criteria.getPreOp());
        sql.append("(");
        sql.append(sqlFieldInfo.getColumn()).append(opSql);
        sql.append(")");
    }

}
