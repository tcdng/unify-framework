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
import com.tcdng.unify.core.criterion.CompoundRestriction;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.database.sql.AbstractSqlCriteriaPolicy;
import com.tcdng.unify.core.database.sql.SqlCriteriaPolicy;
import com.tcdng.unify.core.database.sql.SqlDataSourceDialect;
import com.tcdng.unify.core.database.sql.SqlEntityInfo;
import com.tcdng.unify.core.database.sql.SqlParameter;

/**
 * Base compound operator policy.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class CompoundPolicy extends AbstractSqlCriteriaPolicy {

    public CompoundPolicy(String opSql, final SqlDataSourceDialect sqlDataSourceDialect) {
        super(opSql, sqlDataSourceDialect);
    }

    @Override
    public void translate(StringBuilder sql, SqlEntityInfo sqlEntityInfo, Restriction criteria) throws UnifyException {
        CompoundRestriction cc = (CompoundRestriction) criteria;
        List<Restriction> restrictionList = cc.getRestrictionList();
        if (restrictionList != null && !restrictionList.isEmpty()) {
            int size = restrictionList.size();
            boolean useBrackets =  size > 1;
            if(useBrackets) {
                sql.append("(");
            }
            
            int i = 0;
            Restriction restriction = restrictionList.get(i++);
            SqlCriteriaPolicy sqlCriteriaPolicy = getOperatorPolicy(restriction);
            sqlCriteriaPolicy.translate(sql, sqlEntityInfo, restriction);
            while(i < size) {
                sql.append(opSql);
                restriction = restrictionList.get(i++);
                sqlCriteriaPolicy = getOperatorPolicy(restriction);
                sqlCriteriaPolicy.translate(sql, sqlEntityInfo, restriction);
            }

            if(useBrackets) {
                sql.append(")");
            }
        }
    }

    @Override
    public void translate(StringBuilder sql, String tableName, String columnName, Object param1, Object param2)
            throws UnifyException {
        // TODO unsupported
    }

    @Override
    public void generatePreparedStatementCriteria(StringBuilder sql, final List<SqlParameter> parameterInfoList,
            SqlEntityInfo sqlEntityInfo, final Restriction criteria) throws UnifyException {
        CompoundRestriction cc = (CompoundRestriction) criteria;
        List<Restriction> restrictionList = cc.getRestrictionList();
        if (restrictionList != null && !restrictionList.isEmpty()) {
            int size = restrictionList.size();
            boolean useBrackets =  size > 1;
            if(useBrackets) {
                sql.append("(");
            }
            
            int i = 0;
            Restriction restriction = restrictionList.get(i++);
            SqlCriteriaPolicy sqlCriteriaPolicy = getOperatorPolicy(restriction);
            sqlCriteriaPolicy.generatePreparedStatementCriteria(sql, parameterInfoList, sqlEntityInfo, restriction);
            while(i < size) {
                sql.append(opSql);
                restriction = restrictionList.get(i++);
                sqlCriteriaPolicy = getOperatorPolicy(restriction);
                sqlCriteriaPolicy.generatePreparedStatementCriteria(sql, parameterInfoList, sqlEntityInfo, restriction);
            }

            if(useBrackets) {
                sql.append(")");
            }
        }
    }

}
