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
    
    EQUALS("EQ", RestrictionType.EQUALS, "condition.equals.label", "condition.equals.symbol", false),
    NOT_EQUALS("NEQ", RestrictionType.NOT_EQUALS, "condition.notequals.label", "condition.notequals.symbol", false),
    LESS_THAN("LT", RestrictionType.LESS_THAN, "condition.lessthan.label", "condition.lessthan.symbol", false),
    LESS_OR_EQUAL("LTE", RestrictionType.LESS_OR_EQUAL, "condition.lessorequal.label", "condition.lessorequal.symbol", false),
    GREATER_THAN("GT", RestrictionType.GREATER, "condition.greaterthan.label", "condition.greaterthan.symbol", false),
    GREATER_OR_EQUAL("GTE", RestrictionType.GREATER_OR_EQUAL, "condition.greaterorequal.label", "condition.greaterorequal.symbol", false),
    BETWEEN("BT", RestrictionType.BETWEEN, "condition.between.label", "condition.between.symbol", false),
    NOT_BETWEEN("NBT", RestrictionType.NOT_BETWEEN, "condition.notbetween.label", "condition.notbetween.symbol", false),
    AMONGST("IN", RestrictionType.AMONGST, "condition.amongst.label", "condition.amongst.symbol", false),
    NOT_AMONGST("NIN", RestrictionType.NOT_AMONGST, "condition.notamongst.label", "condition.notamongst.symbol", false),
    LIKE("LK", RestrictionType.LIKE, "condition.like.label", "condition.like.symbol", false),
    NOT_LIKE("NLK", RestrictionType.NOT_LIKE, "condition.notlike.label", "condition.notlike.symbol", false),
    BEGINS_WITH("BW", RestrictionType.BEGINS_WITH, "condition.beginswith.label", "condition.beginswith.symbol", false),
    NOT_BEGIN_WITH("NBW", RestrictionType.NOT_BEGIN_WITH, "condition.notbeginswith.label", "condition.notbeginswith.symbol", false),
    ENDS_WITH("EW", RestrictionType.ENDS_WITH, "condition.endswith.label", "condition.endswith.symbol", false),
    NOT_END_WITH("NEW", RestrictionType.NOT_END_WITH, "condition.notendswith.label", "condition.notendswith.symbol", false),
    IS_NULL("NL", RestrictionType.IS_NULL, "condition.isnull.label", "condition.isnull.symbol", false),
    IS_NOT_NULL("NNL", RestrictionType.IS_NOT_NULL, "condition.isnotnull.label", "condition.isnotnull.symbol", false),

    EQUALS_FIELD("EQF", RestrictionType.EQUALS, "condition.equalsfield.label", "condition.equalsfield.symbol", true),
    NOT_EQUALS_FIELD("NEQF", RestrictionType.NOT_EQUALS, "condition.notequalsfield.label", "condition.notequalsfield.symbol", true),
    LESS_THAN_FIELD("LTF", RestrictionType.LESS_THAN, "condition.lessthanfield.label", "condition.lessthanfield.symbol", true),
    LESS_OR_EQUAL_FIELD("LTEF", RestrictionType.LESS_OR_EQUAL, "condition.lessorequalfield.label", "condition.lessorequalfield.symbol", true),
    GREATER_THAN_FIELD("GTF", RestrictionType.GREATER, "condition.greaterthanfield.label", "condition.greaterthanfield.symbol", true),
    GREATER_OR_EQUAL_FIELD("GTEF", RestrictionType.GREATER_OR_EQUAL, "condition.greaterorequalfield.label", "condition.greaterorequalfield.symbol", true),
    BETWEEN_FIELD("BTF", RestrictionType.BETWEEN, "condition.betweenfield.label", "condition.betweenfield.symbol", true),
    NOT_BETWEEN_FIELD("NBTF", RestrictionType.NOT_BETWEEN, "condition.notbetweenfield.label", "condition.notbetweenfield.symbol", true),
    LIKE_FIELD("LKF", RestrictionType.LIKE, "condition.likefield.label", "condition.likefield.symbol", true),
    NOT_LIKE_FIELD("NLKF", RestrictionType.NOT_LIKE, "condition.notlikefield.label", "condition.notlikefield.symbol", true),
    BEGINS_WITH_FIELD("BWF", RestrictionType.BEGINS_WITH, "condition.beginswithfield.label", "condition.beginswithfield.symbol", true),
    NOT_BEGIN_WITH_FIELD("NBWF", RestrictionType.NOT_BEGIN_WITH, "condition.notbeginswithfield.label", "condition.notbeginswithfield.symbol", true),
    ENDS_WITH_FIELD("EWF", RestrictionType.ENDS_WITH, "condition.endswithfield.label", "condition.endswithfield.symbol", true),
    NOT_END_WITH_FIELD("NEWF", RestrictionType.NOT_END_WITH, "condition.notendswithfield.label", "condition.notendswithfield.symbol", true),
    
    AND("AND", RestrictionType.AND, "condition.and.label", "condition.and.symbol", false),
    OR("OR", RestrictionType.OR, "condition.or.label", "condition.or.symbol", false);

    private final String code;

    private final RestrictionType type;

    private final String labelKey;

    private final String symbolKey;

    private final boolean fieldVal;

    private FilterConditionType(String code, RestrictionType type, String labelKey, String symbolKey,
            boolean fieldVal) {
        this.code = code;
        this.type = type;
        this.labelKey = labelKey;
        this.symbolKey = symbolKey;
        this.fieldVal = fieldVal;
    }

    @Override
    public String defaultCode() {
        return EQUALS.code;
    }

    @Override
    public String code() {
        return code;
    }

    public RestrictionType restrictionType() {
        return type;
    }

    public String labelKey() {
        return labelKey;
    }

    public String symbolKey() {
        return symbolKey;
    }

    public boolean isFieldVal() {
        return fieldVal;
    }

    public boolean isCollection() {
        return type.isCollection();
    }

    public boolean isCompound() {
        return type.isCompound();
    }

    public boolean isRange() {
        return type.isRange();
    }

    public boolean isSingleParam() {
        return type.isSingleParam();
    }

    public boolean isZeroParams() {
        return type.isZeroParams();
    }

    public static FilterConditionType fromCode(String code) {
        return EnumUtils.fromCode(FilterConditionType.class, code);
    }

    public static FilterConditionType fromName(String name) {
        return EnumUtils.fromName(FilterConditionType.class, name);
    }
}
