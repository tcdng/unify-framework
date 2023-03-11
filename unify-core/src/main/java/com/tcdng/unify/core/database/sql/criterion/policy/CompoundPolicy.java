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
package com.tcdng.unify.core.database.sql.criterion.policy;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.criterion.CompoundRestriction;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.database.NativeQuery;
import com.tcdng.unify.core.database.sql.AbstractSqlCriteriaPolicy;
import com.tcdng.unify.core.database.sql.SqlCriteriaPolicy;
import com.tcdng.unify.core.database.sql.SqlDataSourceDialectPolicies;
import com.tcdng.unify.core.database.sql.SqlEntityInfo;
import com.tcdng.unify.core.database.sql.SqlParameter;
import com.tcdng.unify.core.database.sql.SqlTableNativeAliasGenerator;

/**
 * Base compound operator policy.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class CompoundPolicy extends AbstractSqlCriteriaPolicy {

    public CompoundPolicy(String opSql, SqlDataSourceDialectPolicies rootPolicies) {
        super(opSql, rootPolicies);
    }

    @Override
    public void translate(StringBuilder sql, SqlEntityInfo sqlEntityInfo, Restriction restriction)
            throws UnifyException {
        CompoundRestriction cc = (CompoundRestriction) restriction;
        List<Restriction> restrictionList = cc.getRestrictionList();
        if (restrictionList != null && !restrictionList.isEmpty()) {
            int size = restrictionList.size();
            boolean useBrackets = size > 1;
            if (useBrackets) {
                sql.append("(");
            }

            int i = 0;
            Restriction subRestriction = restrictionList.get(i++);
            SqlCriteriaPolicy sqlCriteriaPolicy = getOperatorPolicy(subRestriction);
            sqlCriteriaPolicy.translate(sql, sqlEntityInfo, subRestriction);
            while (i < size) {
                sql.append(opSql);
                subRestriction = restrictionList.get(i++);
                sqlCriteriaPolicy = getOperatorPolicy(subRestriction);
                sqlCriteriaPolicy.translate(sql, sqlEntityInfo, subRestriction);
            }

            if (useBrackets) {
                sql.append(")");
            }
        }
    }

    @Override
    public void generatePreparedStatementCriteria(StringBuilder sql, List<SqlParameter> parameterInfoList,
            SqlEntityInfo sqlEntityInfo, Restriction restriction) throws UnifyException {
        CompoundRestriction cc = (CompoundRestriction) restriction;
        List<Restriction> restrictionList = cc.getRestrictionList();
        if (restrictionList != null && !restrictionList.isEmpty()) {
            int size = restrictionList.size();
            boolean useBrackets = size > 1;
            if (useBrackets) {
                sql.append("(");
            }

            int i = 0;
            Restriction subRestriction = restrictionList.get(i++);
            SqlCriteriaPolicy sqlCriteriaPolicy = getOperatorPolicy(subRestriction);
            sqlCriteriaPolicy.generatePreparedStatementCriteria(sql, parameterInfoList, sqlEntityInfo, subRestriction);
            while (i < size) {
                sql.append(opSql);
                subRestriction = restrictionList.get(i++);
                sqlCriteriaPolicy = getOperatorPolicy(subRestriction);
                sqlCriteriaPolicy.generatePreparedStatementCriteria(sql, parameterInfoList, sqlEntityInfo,
                        subRestriction);
            }

            if (useBrackets) {
                sql.append(")");
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doTranslate(StringBuilder sql, String tableName, String columnName, Object param1, Object param2)
            throws UnifyException {
        SqlTableNativeAliasGenerator aliasGenerator = (SqlTableNativeAliasGenerator) param1;
        List<NativeQuery.Filter> subFilterList = (List<NativeQuery.Filter>) param2;
        if (subFilterList != null && !subFilterList.isEmpty()) {
            int size = subFilterList.size();
            boolean useBrackets = size > 1;
            if (useBrackets) {
                sql.append("(");
            }

            int i = 0;
            NativeQuery.Filter subFilter = subFilterList.get(i++);
            translate(sql, aliasGenerator, subFilter);
            while (i < size) {
                sql.append(opSql);
                subFilter = subFilterList.get(i++);
                translate(sql, aliasGenerator, subFilter);
            }

            if (useBrackets) {
                sql.append(")");
            }
        }
    }

    private void translate(StringBuilder sql, SqlTableNativeAliasGenerator aliasGenerator, NativeQuery.Filter filter)
            throws UnifyException {
        if (filter.isCompound()) {
            getOperatorPolicy(filter.getOp()).translate(sql, null, null, aliasGenerator, filter.getSubFilterList());
        } else {
            getOperatorPolicy(filter.getOp()).translate(sql, aliasGenerator.getTableNativeAlias(filter.getTableName()),
                    filter.getColumnName(), filter.getParam1(), filter.getParam2());
        }
    }

}
