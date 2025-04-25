/*
 * Copyright 2018-2025 The Code Department.
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

import com.tcdng.unify.core.database.sql.AbstractSqlDataTypePolicy;

/**
 * Float data type SQL policy.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class FloatPolicy extends AbstractSqlDataTypePolicy {

    @Override
    public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
        sb.append(" FLOAT");
    }

    @Override
    public void executeSetPreparedStatement(Object pstmt, int index, Object data, long utcOffset) throws Exception {
        if (data == null) {
            ((PreparedStatement) pstmt).setNull(index, Types.FLOAT);
        } else {
            ((PreparedStatement) pstmt).setFloat(index, (Float) data);
        }
    }

    @Override
    public Object executeGetResult(Object rs, Class<?> type, String column, long utcOffset) throws Exception {
        Object object = ((ResultSet) rs).getFloat(column);
        if (((ResultSet) rs).wasNull()) {
            return null;
        }
        return object;
    }

    @Override
    public Object executeGetResult(Object rs, Class<?> type, int index, long utcOffset) throws Exception {
        Object object = ((ResultSet) rs).getFloat(index);
        if (((ResultSet) rs).wasNull()) {
            return null;
        }
        return object;
    }

    @Override
    public void executeRegisterOutParameter(Object cstmt, int index) throws Exception {
        ((CallableStatement) cstmt).registerOutParameter(index, Types.FLOAT);
    }

    @Override
    public Object executeGetOutput(Object cstmt, Class<?> type, int index, long utcOffset) throws Exception {
        Object object = ((CallableStatement) cstmt).getFloat(index);
        if (((CallableStatement) cstmt).wasNull()) {
            return null;
        }
        return object;
    }

    @Override
    public String getAltDefault(Class<?> fieldType) {
        return "0.0";
    }

    @Override
	public String getTypeName() {
		return "FLOAT";
	}

    @Override
    public int getSqlType() {
        return Types.FLOAT;
    }

    @Override
    public boolean isFixedLength() {
        return true;
    }

}
