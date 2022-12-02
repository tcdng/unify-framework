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
package com.tcdng.unify.core.constant;

import com.tcdng.unify.common.annotation.StaticList;
import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.annotation.Tooling;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Constant definitions for requirement type.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Tooling(description = "Requirement Type")
@StaticList(name = "requirementtypelist", description="$m{staticlist.requirementtypelist}")
public enum RequirementType implements EnumConst {

    MANDATORY("M"), OPTIONAL("O"), NONE("N");

    private final String code;

    private RequirementType(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return NONE.code;
    }

    public static RequirementType fromCode(String code) {
        return EnumUtils.fromCode(RequirementType.class, code);
    }

    public static RequirementType fromName(String name) {
        return EnumUtils.fromName(RequirementType.class, name);
    }
}
