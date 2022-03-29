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
package com.tcdng.unify.core.batch;

import com.tcdng.unify.convert.constants.EnumConst;
import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Duplicate batch or batch item on constraint action.
 * 
 * @author The Code Department
 * @since 1.0
 */
@StaticList(name = "constraintactionlist", description="$m{staticlist.constraintactionlist}")
public enum ConstraintAction implements EnumConst {

    UPDATE("U"), SKIP("S"), FAIL("F");

    private final String code;

    private ConstraintAction(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return SKIP.code;
    }

    public static ConstraintAction fromCode(String code) {
        return EnumUtils.fromCode(ConstraintAction.class, code);
    }

    public static ConstraintAction fromName(String name) {
        return EnumUtils.fromName(ConstraintAction.class, name);
    }
}
