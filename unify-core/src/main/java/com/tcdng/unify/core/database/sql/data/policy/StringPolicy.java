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
package com.tcdng.unify.core.database.sql.data.policy;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import com.tcdng.unify.convert.constants.EnumConst;
import com.tcdng.unify.core.database.sql.AbstractSqlDataTypePolicy;

/**
 * String data type SQL policy.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class StringPolicy extends AbstractSqlDataTypePolicy {

    public static final int DEFAULT_LENGTH = 32;

    @Override
    public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
        if (length <= 0) {
            length = DEFAULT_LENGTH;
        }
        sb.append(" VARCHAR(").append(length).append(')');
    }

    @Override
    public void executeSetPreparedStatement(Object pstmt, int index, Object data, long utcOffset) throws Exception {
        if (data == null) {
            ((PreparedStatement) pstmt).setNull(index, Types.VARCHAR);
        } else {
            if (data instanceof EnumConst) {
                ((PreparedStatement) pstmt).setString(index, ((EnumConst) data).code());
            } else {
                ((PreparedStatement) pstmt).setString(index, (String) data);
            }
        }
    }

    @Override
    public Object executeGetResult(Object rs, Class<?> type, String column, long utcOffset) throws Exception {
        return ((ResultSet) rs).getString(column);
    }

    @Override
    public Object executeGetResult(Object rs, Class<?> type, int index, long utcOffset) throws Exception {
        return ((ResultSet) rs).getString(index);
    }

    @Override
    public void executeRegisterOutParameter(Object cstmt, int index) throws Exception {
        ((CallableStatement) cstmt).registerOutParameter(index, Types.VARCHAR);
    }

    @Override
    public Object executeGetOutput(Object cstmt, Class<?> type, int index, long utcOffset) throws Exception {
        return ((CallableStatement) cstmt).getString(index);
    }

    @Override
    public String getAltDefault(Class<?> fieldType) {
        return "' '";
    }

    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public boolean isFixedLength() {
        return false;
    }

}
