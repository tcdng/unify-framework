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
package com.tcdng.unify.common.util;

import com.tcdng.unify.common.annotation.StaticList;
import com.tcdng.unify.common.constants.EnumConst;

/**
 * String token type.
 * 
 * @author The Code Department
 * @since 1.0
 */
@StaticList(name = "stringtokentypelist", description="$m{staticlist.stringtokentypelist}")
public enum StringTokenType implements EnumConst {
	TEXT("TXT"),
	NEWLINE("NLN"),
	PARAM("PRM"),
	FORMATTED_PARAM("FPR"),
	PROCESS_PARAM("PPR"),
	GENERATOR_PARAM("GPR");

    private final String code;

    private StringTokenType(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultCode() {
        return TEXT.code;
    }

	public boolean isText() {
        return TEXT.equals(this);
    }

	public boolean isNewline() {
        return NEWLINE.equals(this);
    }

	public boolean isParam() {
        return PARAM.equals(this) || FORMATTED_PARAM.equals(this) || PROCESS_PARAM.equals(this) || GENERATOR_PARAM.equals(this);
    }

    public boolean isFormattedParam() {
        return FORMATTED_PARAM.equals(this);
    }

    public boolean isProcessParam() {
        return PROCESS_PARAM.equals(this);
    }

    public boolean isGeneratorParam() {
        return GENERATOR_PARAM.equals(this);
    }

    public static StringTokenType fromCode(String code) {
        return EnumUtils.fromCode(StringTokenType.class, code);
    }

    public static StringTokenType fromName(String name) {
        return EnumUtils.fromName(StringTokenType.class, name);
    }
}
