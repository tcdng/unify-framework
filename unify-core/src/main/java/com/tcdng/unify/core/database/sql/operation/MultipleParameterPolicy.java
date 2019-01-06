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
package com.tcdng.unify.core.database.sql.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.database.sql.AbstractSqlCriteriaPolicy;
import com.tcdng.unify.core.database.sql.SqlDataSourceDialect;
import com.tcdng.unify.core.database.sql.SqlEntityInfo;
import com.tcdng.unify.core.database.sql.SqlFieldInfo;
import com.tcdng.unify.core.database.sql.SqlParameter;
import com.tcdng.unify.core.operation.Criteria;
import com.tcdng.unify.core.transform.Transformer;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Base multiple parameter operator policy.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@SuppressWarnings("unchecked")
public abstract class MultipleParameterPolicy extends AbstractSqlCriteriaPolicy {

    private String multOpSql;

    public MultipleParameterPolicy(String opSql, final SqlDataSourceDialect sqlDataSourceDialect, String multOpSql) {
        super(opSql, sqlDataSourceDialect);
        this.multOpSql = multOpSql;
    }

    @Override
    public void translate(StringBuilder sql, SqlEntityInfo sqlEntityInfo, Criteria criteria) throws UnifyException {
        String preOp = (String) criteria.getPreOp();
        if (sqlEntityInfo != null) {
            preOp = sqlEntityInfo.getListFieldInfo(preOp).getColumn();
        }
        translate(sql, sqlEntityInfo.getTableAlias(), preOp, criteria.getPostOp(), null);
    }

    @Override
    public void translate(StringBuilder sql, String tableName, String columnName, Object param1, Object param2)
            throws UnifyException {
        Collection<? extends Object> values = (Collection<? extends Object>) param1;
        if (values == null || values.isEmpty()) {
            throw new UnifyException(UnifyCoreErrorConstants.RECORD_AT_LEAST_ONE_VALUE_EXPECTED, columnName);
        }

        sql.append("(");
        int[] blocks = DataUtils.splitToBlocks(values.size(), maximumClauseValues());
        int i = 0;
        int j = 0;
        int iLen = blocks[j];
        for (Object value : values) {
            if (i >= iLen) {
                sql.append(multOpSql);
                i = 0;
                iLen = blocks[++j];
            }

            if (i == 0) {
                sql.append(tableName).append('.').append(columnName).append(opSql).append("(");
            } else {
                sql.append(", ");
            }

            sql.append(getSqlStringValue(value));

            if ((++i) >= iLen) {
                sql.append(")");
            }
        }
        sql.append(")");
    }

    @Override
    public void generatePreparedStatementCriteria(StringBuilder sql, final List<SqlParameter> parameterInfoList,
            SqlEntityInfo sqlEntityInfo, final Criteria criteria) throws UnifyException {
        SqlFieldInfo sqlFieldInfo = sqlEntityInfo.getListFieldInfo((String) criteria.getPreOp());
        Collection<Object> values = (Collection<Object>) criteria.getPostOp();
        if (values == null || values.isEmpty()) {
            throw new UnifyException(UnifyCoreErrorConstants.RECORD_AT_LEAST_ONE_VALUE_EXPECTED,
                    sqlEntityInfo.getEntityClass());
        }
        if (sqlFieldInfo.isTransformed()) {
            Transformer<Object, Object> transformer = (Transformer<Object, Object>) sqlFieldInfo.getTransformer();
            Collection<?> origValues = (Collection<?>) criteria.getPostOp();
            values = new ArrayList<Object>();
            for (Object value : origValues) {
                values.add(transformer.forwardTransform(value));
            }
        }

        sql.append("(");
        int kLen = values.size();
        int[] blocks = DataUtils.splitToBlocks(kLen, maximumClauseValues());
        int i = 0;
        int j = 0;
        int iLen = blocks[j];
        for (int k = 0; k < kLen; k++) {
            if (i >= iLen) {
                sql.append(multOpSql);
                i = 0;
                iLen = blocks[++j];
            }

            if (i == 0) {
                sql.append(sqlFieldInfo.getColumn()).append(opSql).append("(");
            } else {
                sql.append(", ");
            }

            sql.append("?");

            if ((++i) >= iLen) {
                sql.append(")");
            }
        }
        sql.append(")");
        parameterInfoList.add(new SqlParameter(getSqlTypePolicy(sqlFieldInfo.getColumnType()), values, true));
    }

}
