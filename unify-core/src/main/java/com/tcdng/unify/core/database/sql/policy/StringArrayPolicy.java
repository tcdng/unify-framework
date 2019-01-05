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
package com.tcdng.unify.core.database.sql.policy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import com.tcdng.unify.core.database.sql.SqlDataTypePolicy;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * String array data type SQL policy.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class StringArrayPolicy implements SqlDataTypePolicy {

    private Class<?> arrayClass;

    public StringArrayPolicy() {
        this(String[].class);
    }

    public StringArrayPolicy(Class<?> arrayClass) {
        this.arrayClass = arrayClass;
    }

    @Override
    public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
        if (length <= 0) {
            length = 256;
        }
        sb.append("VARCHAR(").append(length).append(')');
    }

    @Override
    public void appendSpecifyDefaultValueSql(StringBuilder sb, Class<?> type, String defaultVal) {
        if (StringUtils.isBlank(defaultVal)) {
            sb.append(" DEFAULT '").append(defaultVal).append("'");
        }
    }

    @Override
    public void executeSetPreparedStatement(Object pstmt, int index, Object data) throws Exception {
        if (data == null) {
            ((PreparedStatement) pstmt).setNull(index, Types.VARCHAR);
        } else {
            ((PreparedStatement) pstmt).setString(index, getString(data));
        }
    }

    @Override
    public Object executeGetResult(Object rs, Class<?> type, String column) throws Exception {
        String string = ((ResultSet) rs).getString(column);
        if (string != null) {
            return getResult(string);
        }
        return null;
    }

    @Override
    public Object executeGetResult(Object rs, Class<?> type, int index) throws Exception {
        String string = ((ResultSet) rs).getString(index);
        if (string != null) {
            return getResult(string);
        }
        return null;
    }

    private String getString(Object data) throws Exception {
        return StringUtils.buildCommaSeparatedString(DataUtils.convert(String[].class, data, null), false);
    }

    private Object getResult(String data) throws Exception {
        return DataUtils.convert(arrayClass, StringUtils.getCommaSeparatedValues(data), null);
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
