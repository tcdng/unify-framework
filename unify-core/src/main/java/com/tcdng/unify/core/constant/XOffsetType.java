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
 * X-offset type list.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Tooling(description = "X-Offset Type List")
@StaticList(name = "xoffsettypelist", description = "$m{staticlist.xoffsettypelist}")
public enum XOffsetType implements EnumConst {
    LEFT("L"),
    RIGHT("R");

    private final String code;

    private XOffsetType(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return LEFT.code;
    }

    public boolean isLeft() {
    	return LEFT.equals(this);
    }

    public boolean isRight() {
    	return RIGHT.equals(this);
    }
    
    public static XOffsetType fromCode(String code) {
        return EnumUtils.fromCode(XOffsetType.class, code);
    }

    public static XOffsetType fromName(String name) {
        return EnumUtils.fromName(XOffsetType.class, name);
    }
}
