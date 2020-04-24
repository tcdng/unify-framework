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
package com.tcdng.unify.core.database.sql.criterion.policy;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.criterion.RestrictionField;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.criterion.SingleValueRestriction;
import com.tcdng.unify.core.database.sql.AbstractSqlCriteriaPolicy;
import com.tcdng.unify.core.database.sql.SqlDataSourceDialectPolicies;
import com.tcdng.unify.core.database.sql.SqlEntityInfo;
import com.tcdng.unify.core.database.sql.SqlFieldInfo;
import com.tcdng.unify.core.database.sql.SqlParameter;
import com.tcdng.unify.core.transform.Transformer;

/**
 * Base single parameter operator policy.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class SingleParameterPolicy extends AbstractSqlCriteriaPolicy {

    public SingleParameterPolicy(String opSql, SqlDataSourceDialectPolicies rootPolicies) {
        super(opSql, rootPolicies);
    }

    @Override
    public void translate(StringBuilder sql, SqlEntityInfo sqlEntityInfo, Restriction restriction)
            throws UnifyException {
        SingleValueRestriction svc = (SingleValueRestriction) restriction;
        String columnName = svc.getFieldName();
        if (sqlEntityInfo != null) {
            columnName = sqlEntityInfo.getListFieldInfo(svc.getFieldName()).getPreferredColumnName();
        }

        final String tableName = sqlEntityInfo.getTableAlias();
        final Object val = svc.getValue();
        if (val instanceof RestrictionField) {
            sql.append(tableName).append('.').append(columnName).append(opSql).append(
                    resolveParam(tableName, sqlEntityInfo.getListFieldInfo(((RestrictionField) val).getName())));
            return;
        }

        translate(sql, tableName, columnName, val, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void generatePreparedStatementCriteria(StringBuilder sql, List<SqlParameter> parameterInfoList,
            SqlEntityInfo sqlEntityInfo, Restriction restriction) throws UnifyException {
        SingleValueRestriction svc = (SingleValueRestriction) restriction;
        SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getListFieldInfo(svc.getFieldName());
        final Object val = svc.getValue();
        if (val instanceof RestrictionField) {
            sql.append(sqlFieldInfo.getPreferredColumnName()).append(opSql).append(resolveParam(
                    null, sqlEntityInfo.getListFieldInfo(((RestrictionField) val).getName())));
            return;
        }

        sql.append(sqlFieldInfo.getPreferredColumnName()).append(opSql).append("?");
        if (sqlFieldInfo.isTransformed()) {
            parameterInfoList.add(new SqlParameter(getSqlTypePolicy(sqlFieldInfo.getColumnType()), resolveParam(null,
                    ((Transformer<Object, Object>) sqlFieldInfo.getTransformer()).forwardTransform(val))));
        } else {
            Object postOp = convertType(sqlFieldInfo, resolveParam(null, val));
            parameterInfoList.add(new SqlParameter(getSqlTypePolicy(sqlFieldInfo.getColumnType()), postOp));
        }
    }

    @Override
    protected void doTranslate(StringBuilder sql, String tableName, String columnName, Object param1, Object param2)
            throws UnifyException {
        sql.append(tableName).append('.').append(columnName).append(opSql).append(getNativeSqlParam(param1));
    }
}
