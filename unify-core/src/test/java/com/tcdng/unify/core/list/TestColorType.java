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
package com.tcdng.unify.core.list;

import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 *
 * @author Lateef Ojulari
 * @version 1.0
 */
@StaticList("colorlist")
public enum TestColorType implements EnumConst {

    RED("red"), BLUE("blue"), PURPLE("purple");

    private final String code;

    private TestColorType(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return RED.code;
    }

    public static TestColorType fromCode(String code) {
        return EnumUtils.fromCode(TestColorType.class, code);
    }

    public static TestColorType fromName(String name) {
        return EnumUtils.fromName(TestColorType.class, name);
    }

}
