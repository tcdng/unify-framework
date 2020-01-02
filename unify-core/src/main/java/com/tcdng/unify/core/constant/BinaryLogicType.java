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

package com.tcdng.unify.core.constant;

import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Binary logic type constants.
 * 
 * @author Lateef Ojulari
 * @version 1.0
 */
@StaticList("binarylogictypelist")
public enum BinaryLogicType implements EnumConst {

    AND("AND"), OR("OR");

    private final String code;

    private BinaryLogicType(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultCode() {
        return AND.code;
    }

    public static BinaryLogicType fromCode(String code) {
        return EnumUtils.fromCode(BinaryLogicType.class, code);
    }

    public static BinaryLogicType fromName(String name) {
        return EnumUtils.fromName(BinaryLogicType.class, name);
    }
}
