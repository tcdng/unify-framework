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
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import com.tcdng.unify.core.database.sql.SqlDataTypePolicy;

/**
 * Time data type SQL policy.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TimePolicy implements SqlDataTypePolicy {

	@Override
	public void appendTypeSql(StringBuilder sb, int length, int precision, int scale) {
		sb.append("TIMESTAMP");
	}

	@Override
	public void executeSetPreparedStatement(Object pstmt, int index, Object data) throws Exception {
		if (data == null) {
			((PreparedStatement) pstmt).setNull(index, Types.TIMESTAMP);
		} else {
			((PreparedStatement) pstmt).setTimestamp(index, new Timestamp(((Date) data).getTime()));
		}
	}

	@Override
	public Object executeGetResult(Object rs, Class<?> type, String column) throws Exception {
		Timestamp timestamp = ((ResultSet) rs).getTimestamp(column);
		if (timestamp != null) {
			return new Date(timestamp.getTime());
		}
		return null;
	}

	@Override
	public Object executeGetResult(Object rs, Class<?> type, int index) throws Exception {
		Timestamp timestamp = ((ResultSet) rs).getTimestamp(index);
		if (timestamp != null) {
			return new Date(timestamp.getTime());
		}
		return null;
	}
}
