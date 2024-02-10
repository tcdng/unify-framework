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

/**
 * Interface with policy methods for an SQL data type.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface SqlDataTypePolicy {

    /**
     * Gets the policy alternative default SQL string.
     * 
     * @param fieldType the field data type.
     * 
     * @return the alternative default
     */
    String getAltDefault(Class<?> fieldType);

    /**
     * Gets the type name.
     * 
     * @return the type name
     */
    String getTypeName();

    /**
     * Gets the java SQL data type.
     * 
     * @return the java SQL type value
     */
    int getSqlType();

    /**
     * Checks if policy type is for fixed length type.
     * 
     * @return a true value if for fixed length otherwise false
     */
    boolean isFixedLength();

    /**
     * Appends a type SQL to string buffer.
     * 
     * @param sb
     *            the string buffer
     * @param length
     *            the data length
     * @param precision
     *            the number precision
     * @param scale
     *            the number scale
     */
    void appendTypeSql(StringBuilder sb, int length, int precision, int scale);

    /**
     * Appends default SQL
     * 
     * @param sb
     *            the builder to append to
     * @param fieldType
     *            the field type
     * @param defaultVal
     *            the optional default value. If null or blank, Alternative default
     *            values are used.
     */
    void appendDefaultSql(StringBuilder sb, Class<?> fieldType, String defaultVal);

    /**
     * Appends default value
     * 
     * @param sb
     *            the builder to append to
     * @param fieldType
     *            the field type
     * @param defaultVal
     *            the optional default value. If null or blank, Alternative default
     *            values are used.
     */
    void appendDefaultVal(StringBuilder sb, Class<?> fieldType, String defaultVal);

    /**
     * Executes the setter of a prepared statement.
     * 
     * @param pstmt
     *            the prepared statement
     * @param index
     *            the data index
     * @param data
     *            the data to set
     * @param utcOffset
     *            UTC offset for timestamp type
     * @throws Exception
     *             if an error occurs
     */
    void executeSetPreparedStatement(Object pstmt, int index, Object data, long utcOffset) throws Exception;

    /**
     * Executes the register output parameter of a callable statement.
     * 
     * @param cstmt
     *            the callable statement
     * @param index
     *            the data index
     * @throws Exception
     *             if an error occurs
     */
    void executeRegisterOutParameter(Object cstmt, int index) throws Exception;

    /**
     * Executes result set getter using supplied column name.
     * 
     * @param rs
     *            the result set
     * @param type
     *            the result type
     * @param column
     *            tthe result column
     * @param utcOffset
     *            UTC offset for timestamp type
     * @return the result value
     * @throws Exception
     *             if an error occurs
     */
    Object executeGetResult(Object rs, Class<?> type, String column, long utcOffset) throws Exception;

    /**
     * Executes result set getter using supplied column index.
     * 
     * @param rs
     *            the result set
     * @param type
     *            the result type
     * @param index
     *            the result column index
     * @param utcOffset
     *            UTC offset for timestamp type
     * @return the result value
     * @throws Exception
     *             if an error occurs
     */
    Object executeGetResult(Object rs, Class<?> type, int index, long utcOffset) throws Exception;

    /**
     * Executes callable statement output using supplied column index.
     * 
     * @param cstmt
     *            the callable statement
     * @param type
     *            the result type
     * @param index
     *            the result column index
     * @param utcOffset
     *            UTC offset for timestamp type
     * @return the result value
     * @throws Exception
     *             if an error occurs
     */
    Object executeGetOutput(Object cstmt, Class<?> type, int index, long utcOffset) throws Exception;
}
