/*
 * Copyright 2018-2024 The Code Department.
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

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.criterion.Restriction;

/**
 * SQL generation policy.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface SqlCriteriaPolicy {
    /**
     * Generates a native prepared statement SQL with parameter information based on
     * supplied restriction.
     * 
     * @param sql
     *            generated native SQL are appended to this buffer
     * @param parameterInfoList
     *            parameter information is added to this list
     * @param sqlEntityInfo
     *            the record type information object
     * @param restriction
     *            the restriction object
     * @throws UnifyException
     *             if an error occurs
     */
    void generatePreparedStatementCriteria(StringBuilder sql, List<SqlParameter> parameterInfoList,
            SqlEntityInfo sqlEntityInfo, Restriction restriction) throws UnifyException;

    /**
     * Translates a restriction object to native SQL and appends to supplied string
     * buffer..
     * 
     * @param sql
     *            the buffer to write to
     * @param sqlEntityInfo
     *            the record info
     * @param restriction
     *            the restriction
     * @throws UnifyException
     *             if an error occurs
     */
    void translate(StringBuilder sql, SqlEntityInfo sqlEntityInfo, Restriction restriction) throws UnifyException;

    /**
     * Translates a criteria to native SQL and appends to supplied string buffer..
     * 
     * @param sql
     *            the buffer to write to
     * @param tableName
     *            the table name
     * @param columnName
     *            the column name
     * @param param1
     *            the first parameter
     * @param param2
     *            the second parameter
     * @throws UnifyException
     *             if an error occurs
     */
    void translate(StringBuilder sql, String tableName, String columnName, Object param1, Object param2)
            throws UnifyException;
}
