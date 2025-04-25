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

import com.tcdng.unify.core.criterion.RestrictionType;

/**
 * SQl view restriction information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class SqlViewRestrictionInfo {

    private SqlViewColumnInfo columnInfo;

    private RestrictionType restrictionType;

    private Object param1;

    private Object param2;

    public SqlViewRestrictionInfo(SqlViewColumnInfo columnInfo, RestrictionType restrictionType, Object param1,
            Object param2) {
        this.columnInfo = columnInfo;
        this.restrictionType = restrictionType;
        this.param1 = param1;
        this.param2 = param2;
    }

    public String getTableAlias() {
        return columnInfo.getTableAlias();
    }

    public String getColumnName() {
        return columnInfo.getColumnName();
    }

    public RestrictionType getRestrictionType() {
        return restrictionType;
    }

    public Object getParam1() {
        return param1;
    }

    public Object getParam2() {
        return param2;
    }
}
