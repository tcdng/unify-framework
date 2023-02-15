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
 * Vertical alignment types.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Tooling(description = "Horizontal Alignment")
@StaticList(name = "verticalalignlist", description = "$m{staticlist.verticalalignlist}")
public enum VAlignType implements EnumConst {
    TOP("T"), MIDDLE("M"), BOTTOM("B");

    private final String code;

    private VAlignType(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return MIDDLE.code;
    }

    public static VAlignType fromCode(String code) {
        return EnumUtils.fromCode(VAlignType.class, code);
    }

    public static VAlignType fromName(String name) {
        return EnumUtils.fromName(VAlignType.class, name);
    }
}
