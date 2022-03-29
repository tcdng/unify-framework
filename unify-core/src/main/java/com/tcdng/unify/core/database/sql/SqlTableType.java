/*
 * Copyright 2018-2022 The Code Department.
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

import com.tcdng.unify.convert.constants.EnumConst;
import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Supported SQL table type enumeration.
 * 
 * @author The Code Department
 * @since 1.0
 */
@StaticList(name = "sqltabletypelist", description="$m{staticlist.sqltabletypelist}")
public enum SqlTableType implements EnumConst {

    TABLE("TABLE", false), VIEW("VIEW", true);

    private final String code;

    private final boolean view;

    private SqlTableType(String code, boolean view) {
        this.code = code;
        this.view = view;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return TABLE.code;
    }

    public boolean isView() {
        return view;
    }

    public static SqlTableType fromCode(String code) {
        return EnumUtils.fromCode(SqlTableType.class, code);
    }

    public static SqlTableType fromName(String name) {
        return EnumUtils.fromName(SqlTableType.class, name);
    }
}
