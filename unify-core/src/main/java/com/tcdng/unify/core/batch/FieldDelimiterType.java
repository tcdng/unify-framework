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
package com.tcdng.unify.core.batch;

import com.tcdng.unify.common.annotation.StaticList;
import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Field delimiter type.
 * 
 * @author The Code Department
 * @since 1.0
 */
@StaticList(name = "fielddelimiterlist", description="$m{staticlist.fielddelimiterlist}")
public enum FieldDelimiterType implements EnumConst {

    COMMA("C", ','), TAB("T", '\t');

    private final String code;

    private final char ch;

    private FieldDelimiterType(String code, char ch) {
        this.code = code;
        this.ch = ch;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return COMMA.code;
    }

    public char getCharacter() {
        return ch;
    }

    public static FieldDelimiterType fromCode(String code) {
        return EnumUtils.fromCode(FieldDelimiterType.class, code);
    }

    public static FieldDelimiterType fromName(String name) {
        return EnumUtils.fromName(FieldDelimiterType.class, name);
    }
}
