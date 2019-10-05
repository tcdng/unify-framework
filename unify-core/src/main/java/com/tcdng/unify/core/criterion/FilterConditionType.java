/*
 * Copyright 2018-2019 The Code Department.
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

import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Filter condition type enumeration.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@StaticList("filterconditiontypelist")
public enum FilterConditionType implements EnumConst {

    EQUALS("EQ", RestrictionType.EQUALS),
    NOT_EQUAL("NEQ", RestrictionType.NOT_EQUAL),
    GREATER("GT", RestrictionType.GREATER),
    GREATER_OR_EQUAL("GTE", RestrictionType.GREATER_OR_EQUAL),
    LESS("LT", RestrictionType.LESS_THAN),
    LESS_OR_EQUAL("LTE", RestrictionType.LESS_OR_EQUAL),
    BETWEEN("BT", RestrictionType.BETWEEN),
    NOT_BETWEEN("NBT", RestrictionType.NOT_BETWEEN),
    BEGIN_WITH("BW", RestrictionType.BEGINS_WITH),
    END_WITH("EW", RestrictionType.ENDS_WITH),
    LIKE("LK", RestrictionType.LIKE),
    NOT_BEGIN_WITH("NBW", RestrictionType.NOT_BEGIN_WITH),
    NOT_END_WITH("NEW", RestrictionType.NOT_END_WITH),
    NOT_LIKE("NLK", RestrictionType.NOT_LIKE),
    IS_NULL("NL", RestrictionType.IS_NULL),
    IS_NOT_NULL("NNL", RestrictionType.IS_NOT_NULL);

    private final String code;

    private final RestrictionType restrictionType;

    private FilterConditionType(String code, RestrictionType restrictionType) {
        this.code = code;
        this.restrictionType = restrictionType;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultCode() {
        return EQUALS.code;
    }

    public RestrictionType restrictionType() {
        return restrictionType;
    }

    public boolean isRange() {
        return BETWEEN.equals(this) || NOT_BETWEEN.equals(this);
    }

    public boolean isZeroParams() {
        return IS_NULL.equals(this) || IS_NOT_NULL.equals(this);
    }

    public static FilterConditionType fromCode(String code) {
        return EnumUtils.fromCode(FilterConditionType.class, code);
    }

    public static FilterConditionType fromName(String name) {
        return EnumUtils.fromName(FilterConditionType.class, name);
    }
}
