/*
 * Copyright 2018-2020 The Code Department.
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

package com.tcdng.unify.web.ui.widget.control;

import com.tcdng.unify.convert.constants.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Date field type.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public enum DateFieldType implements EnumConst {

    STANDARD("STN"),
    FUTURE("FTR"),
    PAST("PST"),
    YEAR_END("YND");

    private final String code;
    
    private DateFieldType(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return STANDARD.code;
    }

    public boolean supportsYear() {
        return !YEAR_END.equals(this);
    }
    
    public static DateFieldType fromCode(String code) {
        return EnumUtils.fromCode(DateFieldType.class, code);
    }

    public static DateFieldType fromName(String name) {
        return EnumUtils.fromName(DateFieldType.class, name);
    }
}
