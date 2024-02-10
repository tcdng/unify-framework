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
package com.tcdng.unify.core.constant;

import com.tcdng.unify.common.annotation.StaticList;
import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.annotation.Tooling;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Y-offset type list.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Tooling(description = "Y-Offset Type List")
@StaticList(name = "yoffsettypelist", description = "$m{staticlist.yoffsettypelist}")
public enum YOffsetType implements EnumConst {
    TOP("T"),
    BOTTOM("B");

    private final String code;

    private YOffsetType(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return TOP.code;
    }

    public boolean isTop() {
    	return TOP.equals(this);
    }

    public boolean isBottom() {
    	return BOTTOM.equals(this);
    }

    public static YOffsetType fromCode(String code) {
        return EnumUtils.fromCode(YOffsetType.class, code);
    }

    public static YOffsetType fromName(String name) {
        return EnumUtils.fromName(YOffsetType.class, name);
    }
}
