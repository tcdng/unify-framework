/*
 * Copyright 2018-2024 The Code Department.
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

import com.tcdng.unify.common.annotation.StaticList;
import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Supported SQL join type enumeration.
 * 
 * @author The Code Department
 * @since 1.0
 */
@StaticList(name = "sqljointypelist", description="$m{staticlist.sqljointypelist}")
public enum SqlJoinType implements EnumConst {

    INNER("INNER", "INNER JOIN"), LEFT("LEFT", "LEFT JOIN"), RIGHT("RIGHT", "RIGHT JOIN"), FULL("FULL", "FULL JOIN");

    private final String code;

    private final String sql;

    private SqlJoinType(String code, String sql) {
        this.code = code;
        this.sql = sql;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return LEFT.code;
    }

    public String sql() {
        return sql;
    }

    public static SqlJoinType fromCode(String code) {
        return EnumUtils.fromCode(SqlJoinType.class, code);
    }

    public static SqlJoinType fromName(String name) {
        return EnumUtils.fromName(SqlJoinType.class, name);
    }
}