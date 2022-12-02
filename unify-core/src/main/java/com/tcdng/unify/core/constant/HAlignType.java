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

import com.tcdng.unify.convert.annotation.StaticList;
import com.tcdng.unify.convert.constants.EnumConst;
import com.tcdng.unify.core.annotation.Tooling;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Horizontal alignment types.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Tooling(description = "Horizontal Alignment")
@StaticList(name = "horizontalalignlist", description = "$m{staticlist.horizontalalignlist}")
public enum HAlignType implements EnumConst {
    LEFT("L", "haleft"),
    CENTER("C", "hacenter"),
    RIGHT("R", "haright"),
    JUSTIFIED("J", "hajustified");

    private final String code;

    private final String styleClass;

    private HAlignType(String code, String styleClass) {
        this.code = code;
        this.styleClass = styleClass;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return LEFT.code;
    }

    public String styleClass() {
        return this.styleClass;
    }

    public static HAlignType fromCode(String code) {
        return EnumUtils.fromCode(HAlignType.class, code);
    }

    public static HAlignType fromName(String name) {
        return EnumUtils.fromName(HAlignType.class, name);
    }
}
