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

import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Dr/CR type constants.
 * 
 * @author The Code Department
 * @since 1.0
 */
public enum DrCrType implements EnumConst {

    OPTIONAL("OPT", -1),
    DEBIT("DBT", 0),
    CREDIT("CRD", 1);

    private final String code;

    private final int index;
    
    private DrCrType(String code, int index) {
        this.code = code;
        this.index = index;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return OPTIONAL.code;
    }
    
    public int index() {
        return index;
    }

    public static DrCrType fromCode(String code) {
        return EnumUtils.fromCode(DrCrType.class, code);
    }

    public static DrCrType fromName(String name) {
        return EnumUtils.fromName(DrCrType.class, name);
    }
}
