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

import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.database.StaticReference;
import com.tcdng.unify.core.database.sql.SqlDataTypePolicy;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * String data type SQL policy.
 * 
 * @author Lateef Ojulari
 * @version 1.0
 */
public class EnumConstPolicy implements SqlDataTypePolicy {

	@Override
	public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
		if (length <= 0) {
			length = StaticReference.CODE_LENGTH;
		}
		sb.append("VARCHAR(").append(length).append(')');
	}

	@Override
	public void executeSetPreparedStatement(Object pstmt, int index, Object data) throws Exception {
		if (data == null) {
			((PreparedStatement) pstmt).setNull(index, Types.VARCHAR);
		} else {
			((PreparedStatement) pstmt).setString(index, ((EnumConst) data).code());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object executeGetResult(Object rs, Class<?> type, String column) throws Exception {
		Object object = ((ResultSet) rs).getString(column);
		if (((ResultSet) rs).wasNull()) {
			return null;
		}
		return EnumUtils.fromCode((Class<? extends EnumConst>) type, (String) object);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object executeGetResult(Object rs, Class<?> type, int index) throws Exception {
		Object object = ((ResultSet) rs).getString(index);
		if (((ResultSet) rs).wasNull()) {
			return null;
		}
		return EnumUtils.fromCode((Class<? extends EnumConst>) type, (String) object);
	}

}
