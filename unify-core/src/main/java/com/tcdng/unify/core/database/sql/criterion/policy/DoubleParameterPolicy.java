/*
 * Copyright 2018-2022 The Code Department.
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
import com.tcdng.unify.core.criterion.DoubleParamRestriction;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.criterion.RestrictionField;
import com.tcdng.unify.core.database.sql.AbstractSqlCriteriaPolicy;
import com.tcdng.unify.core.database.sql.SqlDataSourceDialectPolicies;
import com.tcdng.unify.core.database.sql.SqlEntityInfo;
import com.tcdng.unify.core.database.sql.SqlFieldInfo;
import com.tcdng.unify.core.database.sql.SqlParameter;
import com.tcdng.unify.core.transform.Transformer;

/**
 * Base double parameter operator policy.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class DoubleParameterPolicy extends AbstractSqlCriteriaPolicy {

    public DoubleParameterPolicy(String opSql, SqlDataSourceDialectPolicies rootPolicies) {
        super(opSql, rootPolicies);
    }

    @Override
    public void translate(StringBuilder sql, SqlEntityInfo sqlEntityInfo, Restriction restriction)
            throws UnifyException {
        DoubleParamRestriction dvc = (DoubleParamRestriction) restriction;
        String columnName = dvc.getFieldName();
        if (sqlEntityInfo != null) {
            columnName = sqlEntityInfo.getListFieldInfo(dvc.getFieldName()).getPreferredColumnName();
        }

        final String tableName = sqlEntityInfo.getTableAlias();
        final Object val1 = dvc.getFirstParam();
        final Object val2 = dvc.getSecondParam();
        final boolean val1IsField = val1 instanceof RestrictionField;
        final boolean val2IsField = val2 instanceof RestrictionField;
        if (val1IsField || val2IsField) {
            sql.append("(");
            sql.append(tableName).append('.').append(columnName).append(opSql);
            if (val1IsField) {
                sql.append(tableName).append('.').append(
                        sqlEntityInfo.getListFieldInfo(((RestrictionField) val1).getName()).getPreferredColumnName());
            } else {
                sql.append(getNativeSqlParam(resolveParam(null, val1)));
            }

            sql.append(" AND ");
            if (val2IsField) {
                sql.append(tableName).append('.').append(
                        sqlEntityInfo.getListFieldInfo(((RestrictionField) val2).getName()).getPreferredColumnName());
            } else {
                sql.append(getNativeSqlParam(resolveParam(null, val2)));
            }

            sql.append(")");
            return;
        }

        translate(sql, tableName, columnName, val1, val2);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void generatePreparedStatementCriteria(StringBuilder sql, List<SqlParameter> parameterInfoList,
            SqlEntityInfo sqlEntityInfo, Restriction restriction) throws UnifyException {
        DoubleParamRestriction dvc = (DoubleParamRestriction) restriction;
        final Object val1 = dvc.getFirstParam();
        final Object val2 = dvc.getSecondParam();
        final boolean val1IsField = val1 instanceof RestrictionField;
        final boolean val2IsField = val2 instanceof RestrictionField;

        SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getListFieldInfo(dvc.getFieldName());
        sql.append("(");
        sql.append(sqlFieldInfo.getPreferredColumnName()).append(opSql);
        if (val1IsField) {
            sql.append(sqlEntityInfo.getListFieldInfo(((RestrictionField) val1).getName()).getPreferredColumnName());
        } else {
            sql.append('?');
        }

        sql.append(" AND ");
        if (val2IsField) {
            sql.append(sqlEntityInfo.getListFieldInfo(((RestrictionField) val2).getName()).getPreferredColumnName());
        } else {
            sql.append('?');
        }

        sql.append(")");

        if (sqlFieldInfo.isTransformed()) {
            Transformer<Object, Object> transformer = (Transformer<Object, Object>) sqlFieldInfo.getTransformer();
            if (!val1IsField) {
                parameterInfoList.add(new SqlParameter(getSqlTypePolicy(sqlFieldInfo.getColumnType()),
                        transformer.forwardTransform(convertType(sqlFieldInfo, val1))));
            }
            if (!val2IsField) {
                parameterInfoList.add(new SqlParameter(getSqlTypePolicy(sqlFieldInfo.getColumnType()),
                        transformer.forwardTransform(convertType(sqlFieldInfo, val2))));
            }
        } else {
            if (!val1IsField) {
                parameterInfoList.add(new SqlParameter(getSqlTypePolicy(sqlFieldInfo.getColumnType()),
                        convertType(sqlFieldInfo, val1)));
            }

            if (!val2IsField) {
                parameterInfoList.add(new SqlParameter(getSqlTypePolicy(sqlFieldInfo.getColumnType()),
                        convertType(sqlFieldInfo, val2)));
            }
        }
    }

    @Override
    protected void doTranslate(StringBuilder sql, String tableName, String columnName, Object param1, Object param2)
            throws UnifyException {
        sql.append("(");
        sql.append(tableName).append('.').append(columnName).append(opSql).append(getNativeSqlParam(param1))
                .append(" AND ").append(getNativeSqlParam(param2));
        sql.append(")");
    }
}
