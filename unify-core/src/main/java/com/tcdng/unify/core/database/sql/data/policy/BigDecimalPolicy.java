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
package com.tcdng.unify.core.database.sql.data.policy;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import com.tcdng.unify.core.database.sql.AbstractSqlDataTypePolicy;

/**
 * Big decimal data type SQL policy.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class BigDecimalPolicy extends AbstractSqlDataTypePolicy {

    @Override
    public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
        sb.append(" DECIMAL(").append(precision).append(',').append(scale).append(')');
    }

    @Override
    public void executeSetPreparedStatement(Object pstmt, int index, Object data, long utcOffset) throws Exception {
        if (data == null) {
            ((PreparedStatement) pstmt).setNull(index, Types.DECIMAL);
        } else {
            ((PreparedStatement) pstmt).setBigDecimal(index, (BigDecimal) data);
        }
    }

    @Override
    public Object executeGetResult(Object rs, Class<?> type, String column, long utcOffset) throws Exception {
        return ((ResultSet) rs).getBigDecimal(column);
    }

    @Override
    public Object executeGetResult(Object rs, Class<?> type, int index, long utcOffset) throws Exception {
        return ((ResultSet) rs).getBigDecimal(index);
    }

    @Override
    public void executeRegisterOutParameter(Object cstmt, int index) throws Exception {
        ((CallableStatement) cstmt).registerOutParameter(index, Types.DECIMAL);
    }

    @Override
    public Object executeGetOutput(Object cstmt, Class<?> type, int index, long utcOffset) throws Exception {
        return ((CallableStatement) cstmt).getBigDecimal(index);
    }

    @Override
    public String getAltDefault() {
        return "0.0";
    }

    @Override
    public int getSqlType() {
        return Types.DECIMAL;
    }

    @Override
    public boolean isFixedLength() {
        return false;
    }
}
