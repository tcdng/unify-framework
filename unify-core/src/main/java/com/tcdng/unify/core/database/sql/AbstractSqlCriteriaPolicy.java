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
package com.tcdng.unify.core.database.sql;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.operation.Criteria;
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

	/**
	 * Returns the SQL criteria policy for supplied criteria's operator.
	 * 
	 * @param criteria
	 *            the criteria object
	 * @return the criteria policy
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected SqlCriteriaPolicy getOperatorPolicy(Criteria criteria) throws UnifyException {
		return sqlDataSourceDialect.getSqlCriteriaPolicy(criteria.getOperator());
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
	 * Converts a value to its SQL string equivalent.
	 * 
	 * @param value
	 *            the value to convert
	 * @return the converted value
	 * @throws UnifyException
	 *             if an error occurs
	 */
	protected String getSqlStringValue(Object value) throws UnifyException {
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
		if (postOp != null && !postOp.getClass().equals(sqlFieldInfo.getFieldClass())) {
			return DataUtils.convert(sqlFieldInfo.getFieldClass(), postOp, null);
		}

		return postOp;
	}
}
