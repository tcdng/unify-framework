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

import com.tcdng.unify.common.annotation.StaticList;
import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Pass fail constants.
 * 
 * @author The Code Department
 * @version 1.0
 */
@StaticList(name = "passfaillist", description="$m{staticlist.passfaillist}")
public enum PassFailType implements EnumConst {

    PASS("Y"), FAIL("N");

    private final String code;

    private PassFailType(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultCode() {
        return FAIL.code;
    }

    public static PassFailType fromCode(String code) {
        return EnumUtils.fromCode(PassFailType.class, code);
    }

    public static PassFailType fromName(String name) {
        return EnumUtils.fromName(PassFailType.class, name);
    }
}
