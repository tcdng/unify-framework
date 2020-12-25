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

import java.util.Collection;

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

    EQUALS("EQ", RestrictionType.EQUALS, "condition.equals.label", "condition.equals.symbol", false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addEquals(fieldName, paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new Equals(fieldName, paramA);
        }
    },
    EQUALS_LINGUAL("EQG", RestrictionType.EQUALS, "condition.equalslingual.label", "condition.equals.symbol", false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {

        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return null;
        }
    },
    NOT_EQUALS("NEQ", RestrictionType.NOT_EQUALS, "condition.notequals.label", "condition.notequals.symbol", false,
            false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotEquals(fieldName, paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotEquals(fieldName, paramA);
        }
    },
    LESS_THAN("LT", RestrictionType.LESS_THAN, "condition.lessthan.label", "condition.lessthan.symbol", false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addLessThan(fieldName, paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new Less(fieldName, paramA);
        }
    },
    LESS_OR_EQUAL("LTE", RestrictionType.LESS_OR_EQUAL, "condition.lessorequal.label", "condition.lessorequal.symbol",
            false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addLessThanEqual(fieldName, paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new LessOrEqual(fieldName, paramA);
        }
    },
    GREATER_THAN("GT", RestrictionType.GREATER, "condition.greaterthan.label", "condition.greaterthan.symbol", false,
            false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addGreaterThan(fieldName, paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new Greater(fieldName, paramA);
        }
    },
    GREATER_OR_EQUAL("GTE", RestrictionType.GREATER_OR_EQUAL, "condition.greaterorequal.label",
            "condition.greaterorequal.symbol", false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addGreaterThanEqual(fieldName, paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new GreaterOrEqual(fieldName, paramA);
        }
    },
    BETWEEN("BT", RestrictionType.BETWEEN, "condition.between.label", "condition.between.symbol", false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addBetween(fieldName, paramA, paramB);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new Between(fieldName, paramA, paramB);
        }
    },
    NOT_BETWEEN("NBT", RestrictionType.NOT_BETWEEN, "condition.notbetween.label", "condition.notbetween.symbol", false,
            false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotBetween(fieldName, paramA, paramB);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotBetween(fieldName, paramA, paramB);
        }
    },
    AMONGST("IN", RestrictionType.AMONGST, "condition.amongst.label", "condition.amongst.symbol", false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addAmongst(fieldName, (Collection<?>) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new Amongst(fieldName, (Collection<?>) paramA);
        }
    },
    NOT_AMONGST("NIN", RestrictionType.NOT_AMONGST, "condition.notamongst.label", "condition.notamongst.symbol", false,
            false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotAmongst(fieldName, (Collection<?>) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotAmongst(fieldName, (Collection<?>) paramA);
        }
    },
    LIKE("LK", RestrictionType.LIKE, "condition.like.label", "condition.like.symbol", false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addLike(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new Like(fieldName, (String) paramA);
        }
    },
    NOT_LIKE("NLK", RestrictionType.NOT_LIKE, "condition.notlike.label", "condition.notlike.symbol", false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotLike(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotLike(fieldName, (String) paramA);
        }
    },
    BEGINS_WITH("BW", RestrictionType.BEGINS_WITH, "condition.beginswith.label", "condition.beginswith.symbol", false,
            false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addBeginsWith(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new BeginsWith(fieldName, (String) paramA);
        }
    },
    NOT_BEGIN_WITH("NBW", RestrictionType.NOT_BEGIN_WITH, "condition.notbeginswith.label",
            "condition.notbeginswith.symbol", false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotBeginWith(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotBeginWith(fieldName, (String) paramA);
        }
    },
    ENDS_WITH("EW", RestrictionType.ENDS_WITH, "condition.endswith.label", "condition.endswith.symbol", false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addEndsWith(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new EndsWith(fieldName, (String) paramA);
        }
    },
    NOT_END_WITH("NEW", RestrictionType.NOT_END_WITH, "condition.notendswith.label", "condition.notendswith.symbol",
            false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotEndWith(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotEndWith(fieldName, (String) paramA);
        }
    },
    IS_NULL("NL", RestrictionType.IS_NULL, "condition.isnull.label", "condition.isnull.symbol", false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addIsNull(fieldName);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new IsNull(fieldName);
        }
    },
    IS_NOT_NULL("NNL", RestrictionType.IS_NOT_NULL, "condition.isnotnull.label", "condition.isnotnull.symbol", false,
            false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addIsNotNull(fieldName);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new IsNotNull(fieldName);
        }
    },

    EQUALS_FIELD("EQF", RestrictionType.EQUALS, "condition.equalsfield.label", "condition.equalsfield.symbol", true,
            false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addEqualsField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new EqualsField(fieldName, (String) paramA);
        }
    },
    NOT_EQUALS_FIELD("NEQF", RestrictionType.NOT_EQUALS, "condition.notequalsfield.label",
            "condition.notequalsfield.symbol", true, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotEqualsField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotEqualsField(fieldName, (String) paramA);
        }
    },
    LESS_THAN_FIELD("LTF", RestrictionType.LESS_THAN, "condition.lessthanfield.label", "condition.lessthanfield.symbol",
            true, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addLessThanField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new LessField(fieldName, (String) paramA);
        }
    },
    LESS_OR_EQUAL_FIELD("LTEF", RestrictionType.LESS_OR_EQUAL, "condition.lessorequalfield.label",
            "condition.lessorequalfield.symbol", true, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addLessThanEqualField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new LessOrEqualField(fieldName, (String) paramA);
        }
    },
    GREATER_THAN_FIELD("GTF", RestrictionType.GREATER, "condition.greaterthanfield.label",
            "condition.greaterthanfield.symbol", true, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addGreaterThanField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new GreaterField(fieldName, (String) paramA);
        }
    },
    GREATER_OR_EQUAL_FIELD("GTEF", RestrictionType.GREATER_OR_EQUAL, "condition.greaterorequalfield.label",
            "condition.greaterorequalfield.symbol", true, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addGreaterThanEqualField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new GreaterOrEqualField(fieldName, (String) paramA);
        }
    },
    BETWEEN_FIELD("BTF", RestrictionType.BETWEEN, "condition.betweenfield.label", "condition.betweenfield.symbol", true,
            false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addBetweenField(fieldName, (String) paramA, (String) paramB);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new BetweenField(fieldName, (String) paramA, (String) paramB);
        }
    },
    NOT_BETWEEN_FIELD("NBTF", RestrictionType.NOT_BETWEEN, "condition.notbetweenfield.label",
            "condition.notbetweenfield.symbol", true, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotBetweenField(fieldName, (String) paramA, (String) paramB);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotBetweenField(fieldName, (String) paramA, (String) paramB);
        }
    },
    LIKE_FIELD("LKF", RestrictionType.LIKE, "condition.likefield.label", "condition.likefield.symbol", true, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addLikeField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new LikeField(fieldName, (String) paramA);
        }
    },
    NOT_LIKE_FIELD("NLKF", RestrictionType.NOT_LIKE, "condition.notlikefield.label", "condition.notlikefield.symbol",
            true, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotLikeField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotLikeField(fieldName, (String) paramA);
        }
    },
    BEGINS_WITH_FIELD("BWF", RestrictionType.BEGINS_WITH, "condition.beginswithfield.label",
            "condition.beginswithfield.symbol", true, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addBeginsWithField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new BeginsWithField(fieldName, (String) paramA);
        }
    },
    NOT_BEGIN_WITH_FIELD("NBWF", RestrictionType.NOT_BEGIN_WITH, "condition.notbeginswithfield.label",
            "condition.notbeginswithfield.symbol", true, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotBeginWithField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotBeginWithField(fieldName, (String) paramA);
        }
    },
    ENDS_WITH_FIELD("EWF", RestrictionType.ENDS_WITH, "condition.endswithfield.label", "condition.endswithfield.symbol",
            true, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addEndsWithField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new EndsWithField(fieldName, (String) paramA);
        }
    },
    NOT_END_WITH_FIELD("NEWF", RestrictionType.NOT_END_WITH, "condition.notendswithfield.label",
            "condition.notendswithfield.symbol", true, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotEndWithField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotEndWithField(fieldName, (String) paramA);
        }
    },

    EQUALS_COLLECTION("EQC", RestrictionType.EQUALS, "condition.equalscollection.label",
            "condition.equalscollection.symbol", false, true) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addCollectionSizeEquals(fieldName, (Integer) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new EqualsCollection(fieldName, (Integer) paramA);
        }
    },
    NOT_EQUALS_COLLECTION("NEQC", RestrictionType.NOT_EQUALS, "condition.notequalscollection.label",
            "condition.notequalscollection.symbol", false, true) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addCollectionSizeNotEquals(fieldName, (Integer) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotEqualsCollection(fieldName, (Integer) paramA);
        }
    },
    LESS_THAN_COLLECTION("LTC", RestrictionType.LESS_THAN, "condition.lessthancollection.label",
            "condition.lessthancollection.symbol", false, true) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addCollectionSizeLessThan(fieldName, (Integer) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new LessCollection(fieldName, (Integer) paramA);
        }
    },
    LESS_OR_EQUAL_COLLECTION("LTEC", RestrictionType.LESS_OR_EQUAL, "condition.lessorequalcollection.label",
            "condition.lessorequalcollection.symbol", false, true) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addCollectionSizeLessThanEqual(fieldName, (Integer) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new LessOrEqualCollection(fieldName, (Integer) paramA);
        }
    },
    GREATER_THAN_COLLECTION("GTC", RestrictionType.GREATER, "condition.greaterthancollection.label",
            "condition.greaterthancollection.symbol", false, true) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addCollectionSizeGreaterThan(fieldName, (Integer) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new GreaterCollection(fieldName, (Integer) paramA);
        }
    },
    GREATER_OR_EQUAL_COLLECTION("GTEC", RestrictionType.GREATER_OR_EQUAL, "condition.greaterorequalcollection.label",
            "condition.greaterorequalcollection.symbol", false, true) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addCollectionSizeGreaterThanEqual(fieldName, (Integer) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new GreaterOrEqualCollection(fieldName, (Integer) paramA);
        }
    },

    AND("AND", RestrictionType.AND, "condition.and.label", "condition.and.symbol", false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {

        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return null;
        }
    },
    OR("OR", RestrictionType.OR, "condition.or.label", "condition.or.symbol", false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {

        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return null;
        }
    };

    private final String code;

    private final RestrictionType type;

    private final String labelKey;

    private final String symbolKey;

    private final boolean fieldVal;

    private final boolean collection;

    private FilterConditionType(String code, RestrictionType type, String labelKey, String symbolKey, boolean fieldVal,
            boolean collection) {
        this.code = code;
        this.type = type;
        this.labelKey = labelKey;
        this.symbolKey = symbolKey;
        this.fieldVal = fieldVal;
        this.collection = collection;
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
        return collection;
    }

    public boolean isAmongst() {
        return type.isAmongst();
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

    public boolean isLingual() {
        return EQUALS_LINGUAL.equals(this);
    }
    
    public abstract void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB);

    public abstract Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB);

    public static FilterConditionType fromCode(String code) {
        return EnumUtils.fromCode(FilterConditionType.class, code);
    }

    public static FilterConditionType fromName(String name) {
        return EnumUtils.fromName(FilterConditionType.class, name);
    }
}
