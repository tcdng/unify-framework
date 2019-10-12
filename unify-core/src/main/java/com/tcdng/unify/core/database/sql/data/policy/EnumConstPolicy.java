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

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.database.StaticReference;
import com.tcdng.unify.core.database.sql.AbstractSqlDataTypePolicy;
import com.tcdng.unify.core.util.EnumUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * String data type SQL policy.
 * 
 * @author Lateef Ojulari
 * @version 1.0
 */
public class EnumConstPolicy extends AbstractSqlDataTypePolicy {

    @Override
    public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
        if (length <= 0) {
            length = StaticReference.CODE_LENGTH;
        }
        sb.append(" VARCHAR(").append(length).append(')');
    }

    @SuppressWarnings("unchecked")
    @Override
    public void appendDefaultSql(StringBuilder sb, Class<?> type, String defaultVal) {
        EnumConst val= null;
        if (StringUtils.isNotBlank(defaultVal)) {
            val = EnumUtils.fromCode((Class<? extends EnumConst>) type, (String) defaultVal);
            if (val == null) {
                val = EnumUtils.fromName((Class<? extends EnumConst>) type, (String) defaultVal);
            }
        }
        
        if (val == null) {
            val = EnumUtils.getDefault((Class<? extends EnumConst>) type);
        }
        sb.append(" DEFAULT '").append(val.code()).append("'");
    }

    @Override
    public void executeSetPreparedStatement(Object pstmt, int index, Object data, long utcOffset) throws Exception {
        if (data == null) {
            ((PreparedStatement) pstmt).setNull(index, Types.VARCHAR);
        } else {
            ((PreparedStatement) pstmt).setString(index, ((EnumConst) data).code());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object executeGetResult(Object rs, Class<?> type, String column, long utcOffset) throws Exception {
        Object object = ((ResultSet) rs).getString(column);
        if (((ResultSet) rs).wasNull()) {
            return null;
        }
        return EnumUtils.fromCode((Class<? extends EnumConst>) type, (String) object);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object executeGetResult(Object rs, Class<?> type, int index, long utcOffset) throws Exception {
        Object object = ((ResultSet) rs).getString(index);
        if (((ResultSet) rs).wasNull()) {
            return null;
        }
        return EnumUtils.fromCode((Class<? extends EnumConst>) type, (String) object);
    }

    @Override
    public void executeRegisterOutParameter(Object cstmt, int index) throws Exception {
        ((CallableStatement) cstmt).registerOutParameter(index, Types.VARCHAR);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object executeGetOutput(Object cstmt, Class<?> type, int index, long utcOffset) throws Exception {
        Object object = ((CallableStatement) cstmt).getString(index);
        if (((CallableStatement) cstmt).wasNull()) {
            return null;
        }
        return EnumUtils.fromCode((Class<? extends EnumConst>) type, (String) object);
    }

    @Override
    public String getAltDefault() {
        return null;
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
