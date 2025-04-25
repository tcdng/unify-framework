/*
 * Copyright (c) 2018-2025 The Code Department.
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
package com.tcdng.unify.core.database;

import java.util.Date;

import com.tcdng.unify.common.annotation.ColumnType;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Native update.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class NativeUpdate {

	private String updateSql;

	private NativeParam[] params;

	public NativeUpdate(String updateSql) {
		this.updateSql = updateSql;
		this.params = new NativeParam[StringUtils.getCharOccurrences(updateSql, '?')];
	}

	public NativeUpdate setParam(int index, boolean param) {
		return _setParam(index, ColumnType.CHARACTER, param ? 'Y' : 'N');
	}

	public NativeUpdate setParam(int index, char param) {
		return _setParam(index, ColumnType.CHARACTER, param);
	}

	public NativeUpdate setParam(int index, long param) {
		return _setParam(index, ColumnType.LONG, param);
	}

	public NativeUpdate setParam(int index, double param) {
		return _setParam(index, ColumnType.DOUBLE, param);
	}

	public NativeUpdate setParam(int index, Date param) {
		return _setParam(index, ColumnType.TIMESTAMP, param);
	}

	public NativeUpdate setParam(int index, String param) {
		return _setParam(index, ColumnType.STRING, param);
	}

	public String getUpdateSql() {
		return updateSql;
	}

	public NativeParam[] getParams() {
		return params;
	}

	public boolean isWithParams() {
		return params.length > 0;
	}
	
	private NativeUpdate _setParam(int index, ColumnType type, Object param) {
		if (index >= params.length) {
			throw new IllegalArgumentException(
					"Index " + index + " is out of bounds of parameter length of " + params.length);
		}

		params[index] = new NativeParam(type, param);
		return this;
	}
}
