/*
 * Copyright (c) 2018-2025 The Code Department.
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
package com.tcdng.unify.core.criterion;

import com.tcdng.unify.common.annotation.StaticList;
import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Restriction type enumeration.
 * 
 * @author The Code Department
 * @since 4.1
 */
@StaticList(name = "restrictiontypelist", description="$m{staticlist.restrictiontypelist}")
public enum RestrictionType implements EnumConst {
    EQUALS("EQ", true),
    IEQUALS("IEQ", true),
    NOT_EQUALS("NEQ", true),
    INOT_EQUALS("INQ", true),
    LESS_THAN("LT", true),
    LESS_OR_EQUAL("LTE", true),
    GREATER("GT", true),
    GREATER_OR_EQUAL("GTE", true),
    BETWEEN("BT", false),
    NOT_BETWEEN("NBT", false),
    AMONGST("IN", false),
    NOT_AMONGST("NIN", false),
    LIKE("LK", true),
    ILIKE("ILK", true),
    NOT_LIKE("NLK", true),
    BEGINS_WITH("BW", true),
    IBEGINS_WITH("IBW", true),
    NOT_BEGIN_WITH("NBW", true),
    ENDS_WITH("EW", true),
    IENDS_WITH("IEW", true),
    NOT_END_WITH("NEW", true),
    IS_NULL("NL", false),
    IS_NOT_NULL("NNL", false),
    AND("AND", false),
    OR("OR", false);

    private final String code;

    private final boolean singleParam;

    private RestrictionType(String code, boolean singleParam) {
        this.code = code;
        this.singleParam = singleParam;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultCode() {
        return EQUALS.code;
    }

    public boolean isAmongst() {
        return AMONGST.equals(this) || NOT_AMONGST.equals(this);
    }

    public boolean isCompound() {
        return AND.equals(this) || OR.equals(this);
    }

    public boolean isRange() {
        return BETWEEN.equals(this) || NOT_BETWEEN.equals(this);
    }

    public boolean isSingleParam() {
        return singleParam;
    }

    public boolean isZeroParams() {
        return IS_NULL.equals(this) || IS_NOT_NULL.equals(this);
    }

    public static RestrictionType fromCode(String code) {
        return EnumUtils.fromCode(RestrictionType.class, code);
    }

    public static RestrictionType fromName(String name) {
        return EnumUtils.fromName(RestrictionType.class, name);
    }
}
