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

package com.tcdng.unify.core.database.sql;

/**
 * SQL table column alter information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class SqlColumnAlterInfo {

    private boolean nullableChange;

    private boolean defaultChange;

    private boolean typeChange;

    private boolean lenChange;

    public SqlColumnAlterInfo(boolean nullableChange, boolean defaultChange, boolean typeChange, boolean lenChange) {
        this.nullableChange = nullableChange;
        this.defaultChange = defaultChange;
        this.typeChange = typeChange;
        this.lenChange = lenChange;
    }

    public boolean isNullableChange() {
        return nullableChange;
    }

    public boolean isDefaultChange() {
        return defaultChange;
    }

    public boolean isTypeChange() {
        return typeChange;
    }

    public boolean isLenChange() {
        return lenChange;
    }

    public boolean isDataChange() {
        return typeChange || lenChange;
    }
    
    public boolean isAltered() {
        return nullableChange || defaultChange || typeChange || lenChange;
    }

	@Override
	public String toString() {
		return "[nullableChange=" + nullableChange + ", defaultChange=" + defaultChange
				+ ", typeChange=" + typeChange + ", lenChange=" + lenChange + "]";
	}

}
