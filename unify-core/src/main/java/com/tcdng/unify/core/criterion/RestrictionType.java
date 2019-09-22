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
 * Restriction type enumeration.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@StaticList("restrictiontypelist")
public enum RestrictionType implements EnumConst {
    EQUAL("EQ"),
    NOT_EQUAL("NEQ"),
    LESS_THAN("LT"),
    LESS_OR_EQUAL("LTE"),
    GREATER("GT"),
    GREATER_OR_EQUAL("GTE"),
    BETWEEN("BT"),
    NOT_BETWEEN("NBT"),
    AMONGST("IN"),
    NOT_AMONGST("NIN"),
    LIKE("LK"),
    NOT_LIKE("NLK"),
    BEGIN_WITH("BW"),
    NOT_BEGIN_WITH("NBW"),
    END_WITH("EW"),
    NOT_END_WITH("NEW"),
    IS_NULL("NL"),
    IS_NOT_NULL("NNL"),
    AND("AND"),
    OR("OR");

    private final String code;

    private RestrictionType(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultCode() {
        return EQUAL.code;
    }

    public boolean isCollection() {
        return AMONGST.equals(this) || NOT_AMONGST.equals(this);
    }

    public boolean isCompound() {
        return AND.equals(this) || OR.equals(this);
    }

    public boolean isRange() {
        return BETWEEN.equals(this) || NOT_BETWEEN.equals(this);
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
