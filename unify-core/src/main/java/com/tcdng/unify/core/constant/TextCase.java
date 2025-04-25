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
package com.tcdng.unify.core.constant;

import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Text case constants.
 * 
 * @author The Code Department
 * @since 1.0
 */
public enum TextCase implements EnumConst {

    LOWER("LC"),
    UPPER("UC"),
    CAMEL("CM"),
    CAPITAL("CP"),
    PATH("PT");

    private final String code;

    private TextCase(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return LOWER.code;
    }

    public static TextCase fromCode(String code) {
        return EnumUtils.fromCode(TextCase.class, code);
    }

    public static TextCase fromName(String name) {
        return EnumUtils.fromName(TextCase.class, name);
    }
}
