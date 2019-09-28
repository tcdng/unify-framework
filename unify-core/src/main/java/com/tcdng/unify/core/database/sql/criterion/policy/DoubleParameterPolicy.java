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
package com.tcdng.unify.core.database.sql.criterion.policy;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.criterion.DoubleValueRestriction;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.database.sql.AbstractSqlCriteriaPolicy;
import com.tcdng.unify.core.database.sql.SqlDataSourceDialect;
import com.tcdng.unify.core.database.sql.SqlEntityInfo;
import com.tcdng.unify.core.database.sql.SqlFieldInfo;
import com.tcdng.unify.core.database.sql.SqlParameter;
import com.tcdng.unify.core.transform.Transformer;

/**
 * Base double parameter operator policy.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class DoubleParameterPolicy extends AbstractSqlCriteriaPolicy {

    public DoubleParameterPolicy(String opSql, final SqlDataSourceDialect sqlDataSourceDialect) {
        super(opSql, sqlDataSourceDialect);
    }

    @Override
    public void translate(StringBuilder sql, SqlEntityInfo sqlEntityInfo, Restriction restriction) throws UnifyException {
        DoubleValueRestriction dvc = (DoubleValueRestriction) restriction;
        String columnName = dvc.getPropertyName();
        if (sqlEntityInfo != null) {
            columnName = sqlEntityInfo.getListFieldInfo(dvc.getPropertyName()).getPreferredColumnName();
        }
        translate(sql, sqlEntityInfo.getTableAlias(), columnName, dvc.getFirstValue(), dvc.getSecondValue());
    }

    @Override
    public void translate(StringBuilder sql, String tableName, String columnName, Object param1, Object param2)
            throws UnifyException {
        sql.append("(");
        sql.append(tableName).append('.').append(columnName).append(opSql).append(getSqlStringValue(param1))
                .append(" AND ").append(getSqlStringValue(param2));
        sql.append(")");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void generatePreparedStatementCriteria(StringBuilder sql, final List<SqlParameter> parameterInfoList,
            SqlEntityInfo sqlEntityInfo, final Restriction restriction) throws UnifyException {
        DoubleValueRestriction dvc = (DoubleValueRestriction) restriction;
        SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getListFieldInfo(dvc.getPropertyName());
        sql.append("(");
        sql.append(sqlFieldInfo.getPreferredColumnName()).append(opSql).append("? AND ?");
        sql.append(")");
        Object value1 = convertType(sqlFieldInfo, dvc.getFirstValue());
        Object value2 = convertType(sqlFieldInfo, dvc.getSecondValue());
        if (sqlFieldInfo.isTransformed()) {
            Transformer<Object, Object> transformer = (Transformer<Object, Object>) sqlFieldInfo.getTransformer();
            parameterInfoList.add(new SqlParameter(getSqlTypePolicy(sqlFieldInfo.getColumnType()),
                    transformer.forwardTransform(value1)));
            parameterInfoList.add(new SqlParameter(getSqlTypePolicy(sqlFieldInfo.getColumnType()),
                    transformer.forwardTransform(value2)));
        } else {
            parameterInfoList.add(new SqlParameter(getSqlTypePolicy(sqlFieldInfo.getColumnType()), value1));
            parameterInfoList.add(new SqlParameter(getSqlTypePolicy(sqlFieldInfo.getColumnType()), value2));
        }
    }
}
