/*
 * Copyright 2018-2025 The Code Department.
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

import com.tcdng.unify.common.annotation.StaticList;
import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Filter condition type enumeration.
 * 
 * @author The Code Department
 * @since 4.1
 */
@StaticList(name = "filterconditiontypelist", description = "$m{staticlist.filterconditiontypelist}")
public enum FilterConditionType implements EnumConst {

    EQUALS("EQ", RestrictionType.EQUALS, "condition.equals.label", "condition.equals.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addEquals(fieldName, paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new Equals(fieldName, paramA);
        }
    },
    IEQUALS("IEQ", RestrictionType.IEQUALS, "condition.iequals.label", "condition.iequals.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addIEquals(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new IEquals(fieldName, (String) paramA);
        }
    },
    EQUALS_LINGUAL("EQG", RestrictionType.EQUALS, "condition.equalslingual.label", "condition.equals.symbol", FilterParamType.IMMEDIATE, true, true) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {

        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return null;
        }
    },
    NOT_EQUALS("NEQ", RestrictionType.NOT_EQUALS, "condition.notequals.label", "condition.notequals.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotEquals(fieldName, paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotEquals(fieldName, paramA);
        }
    },
    NOT_EQUALS_LINGUAL("NEL", RestrictionType.NOT_EQUALS, "condition.notequalslingual.label", "condition.notequals.symbol", FilterParamType.IMMEDIATE, false, true) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotEquals(fieldName, paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotEquals(fieldName, paramA);
        }
    },
    INOT_EQUALS("INQ", RestrictionType.INOT_EQUALS, "condition.inotequals.label", "condition.inotequals.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addINotEquals(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new INotEquals(fieldName, (String) paramA);
        }
    },
    LESS_THAN("LT", RestrictionType.LESS_THAN, "condition.lessthan.label", "condition.lessthan.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addLessThan(fieldName, paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new Less(fieldName, paramA);
        }
    },
    LESS_THAN_LINGUAL("LTL", RestrictionType.LESS_THAN, "condition.lessthanlingual.label", "condition.lessthan.symbol", FilterParamType.IMMEDIATE, false, true) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addLessThan(fieldName, paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new Less(fieldName, paramA);
        }
    },
    LESS_OR_EQUAL("LTE", RestrictionType.LESS_OR_EQUAL, "condition.lessorequal.label", "condition.lessorequal.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addLessThanEqual(fieldName, paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new LessOrEqual(fieldName, paramA);
        }
    },
    LESS_OR_EQUAL_LINGUAL("LTL", RestrictionType.LESS_OR_EQUAL, "condition.lessorequallingual.label", "condition.lessorequal.symbol", FilterParamType.IMMEDIATE, false, true) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addLessThanEqual(fieldName, paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new LessOrEqual(fieldName, paramA);
        }
    },
    GREATER_THAN("GT", RestrictionType.GREATER, "condition.greaterthan.label", "condition.greaterthan.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addGreaterThan(fieldName, paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new Greater(fieldName, paramA);
        }
    },
    GREATER_THAN_LINGUAL("GTL", RestrictionType.GREATER, "condition.greaterthanlingual.label", "condition.greaterthan.symbol", FilterParamType.IMMEDIATE, false, true) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addGreaterThan(fieldName, paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new Greater(fieldName, paramA);
        }
    },
    GREATER_OR_EQUAL("GTE", RestrictionType.GREATER_OR_EQUAL, "condition.greaterorequal.label", "condition.greaterorequal.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addGreaterThanEqual(fieldName, paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new GreaterOrEqual(fieldName, paramA);
        }
    },
    GREATER_OR_EQUAL_LINGUAL("GTL", RestrictionType.GREATER_OR_EQUAL, "condition.greaterorequallingual.label", "condition.greaterorequal.symbol", FilterParamType.IMMEDIATE, false, true) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addGreaterThanEqual(fieldName, paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new GreaterOrEqual(fieldName, paramA);
        }
    },
    BETWEEN("BT", RestrictionType.BETWEEN, "condition.between.label", "condition.between.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addBetween(fieldName, paramA, paramB);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new Between(fieldName, paramA, paramB);
        }
    },
    BETWEEN_LINGUAL("BTL", RestrictionType.BETWEEN, "condition.betweenlingual.label", "condition.between.symbol", FilterParamType.IMMEDIATE, false, true) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addBetween(fieldName, paramA, paramB);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new Between(fieldName, paramA, paramB);
        }
    },
    NOT_BETWEEN("NBT", RestrictionType.NOT_BETWEEN, "condition.notbetween.label", "condition.notbetween.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotBetween(fieldName, paramA, paramB);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotBetween(fieldName, paramA, paramB);
        }
    },
    NOT_BETWEEN_LINGUAL("NBL", RestrictionType.NOT_BETWEEN, "condition.notbetweenlingual.label", "condition.notbetween.symbol", FilterParamType.IMMEDIATE, false, true) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotBetween(fieldName, paramA, paramB);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotBetween(fieldName, paramA, paramB);
        }
    },
    AMONGST("IN", RestrictionType.AMONGST, "condition.amongst.label", "condition.amongst.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addAmongst(fieldName, (Collection<?>) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new Amongst(fieldName, (Collection<?>) paramA);
        }
    },
    NOT_AMONGST("NIN", RestrictionType.NOT_AMONGST, "condition.notamongst.label", "condition.notamongst.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotAmongst(fieldName, (Collection<?>) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotAmongst(fieldName, (Collection<?>) paramA);
        }
    },
    LIKE("LK", RestrictionType.LIKE, "condition.like.label", "condition.like.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addLike(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new Like(fieldName, (String) paramA);
        }
    },
    ILIKE("ILK", RestrictionType.ILIKE, "condition.ilike.label", "condition.ilike.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addILike(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new ILike(fieldName, (String) paramA);
        }
    },
    NOT_LIKE("NLK", RestrictionType.NOT_LIKE, "condition.notlike.label", "condition.notlike.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotLike(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotLike(fieldName, (String) paramA);
        }
    },
    BEGINS_WITH("BW", RestrictionType.BEGINS_WITH, "condition.beginswith.label", "condition.beginswith.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addBeginsWith(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new BeginsWith(fieldName, (String) paramA);
        }
    },
    IBEGINS_WITH("IBW", RestrictionType.IBEGINS_WITH, "condition.ibeginswith.label", "condition.ibeginswith.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addIBeginsWith(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new IBeginsWith(fieldName, (String) paramA);
        }
    },
    NOT_BEGIN_WITH("NBW", RestrictionType.NOT_BEGIN_WITH, "condition.notbeginswith.label", "condition.notbeginswith.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotBeginWith(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotBeginWith(fieldName, (String) paramA);
        }
    },
    ENDS_WITH("EW", RestrictionType.ENDS_WITH, "condition.endswith.label", "condition.endswith.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addEndsWith(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new EndsWith(fieldName, (String) paramA);
        }
    },
    IENDS_WITH("IEW", RestrictionType.IENDS_WITH, "condition.iendswith.label", "condition.iendswith.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addIEndsWith(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new IEndsWith(fieldName, (String) paramA);
        }
    },
    NOT_END_WITH("NEW", RestrictionType.NOT_END_WITH, "condition.notendswith.label", "condition.notendswith.symbol", FilterParamType.IMMEDIATE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotEndWith(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotEndWith(fieldName, (String) paramA);
        }
    },
    IS_NULL("NL", RestrictionType.IS_NULL, "condition.isnull.label", "condition.isnull.symbol", FilterParamType.NONE, true, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addIsNull(fieldName);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new IsNull(fieldName);
        }
    },
    IS_NOT_NULL("NNL", RestrictionType.IS_NOT_NULL, "condition.isnotnull.label", "condition.isnotnull.symbol", FilterParamType.NONE, true, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addIsNotNull(fieldName);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new IsNotNull(fieldName);
        }
    },

    EQUALS_FIELD("EQF", RestrictionType.EQUALS, "condition.equalsfield.label", "condition.equalsfield.symbol", FilterParamType.FIELD, true, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addEqualsField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new EqualsField(fieldName, (String) paramA);
        }
    },
    NOT_EQUALS_FIELD("NEQF", RestrictionType.NOT_EQUALS, "condition.notequalsfield.label", "condition.notequalsfield.symbol", FilterParamType.FIELD, true, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotEqualsField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotEqualsField(fieldName, (String) paramA);
        }
    },

    EQUALS_SESSIONPARAM("EQS", RestrictionType.EQUALS, "condition.equalssessionparam.label", "condition.equalssessionparam.symbol", FilterParamType.SESSION_PARAMETER, true, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addEquals(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new Equals(fieldName, (String) paramA);
        }
    },
    NOT_EQUALS_SESSIONPARAM("NEQS", RestrictionType.NOT_EQUALS, "condition.notequalssessionparam.label", "condition.notequalssessionparam.symbol", FilterParamType.SESSION_PARAMETER, true, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotEquals(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotEquals(fieldName, (String) paramA);
        }
    },
    LESS_THAN_FIELD("LTF", RestrictionType.LESS_THAN, "condition.lessthanfield.label", "condition.lessthanfield.symbol", FilterParamType.FIELD, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addLessThanField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new LessField(fieldName, (String) paramA);
        }
    },
    LESS_OR_EQUAL_FIELD("LTEF", RestrictionType.LESS_OR_EQUAL, "condition.lessorequalfield.label", "condition.lessorequalfield.symbol", FilterParamType.FIELD, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addLessThanEqualField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new LessOrEqualField(fieldName, (String) paramA);
        }
    },
    GREATER_THAN_FIELD("GTF", RestrictionType.GREATER, "condition.greaterthanfield.label", "condition.greaterthanfield.symbol", FilterParamType.FIELD, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addGreaterThanField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new GreaterField(fieldName, (String) paramA);
        }
    },
    GREATER_OR_EQUAL_FIELD("GTEF", RestrictionType.GREATER_OR_EQUAL, "condition.greaterorequalfield.label", "condition.greaterorequalfield.symbol", FilterParamType.FIELD, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addGreaterThanEqualField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new GreaterOrEqualField(fieldName, (String) paramA);
        }
    },
    BETWEEN_FIELD("BTF", RestrictionType.BETWEEN, "condition.betweenfield.label", "condition.betweenfield.symbol", FilterParamType.FIELD, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addBetweenField(fieldName, (String) paramA, (String) paramB);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new BetweenField(fieldName, (String) paramA, (String) paramB);
        }
    },
    NOT_BETWEEN_FIELD("NBTF", RestrictionType.NOT_BETWEEN, "condition.notbetweenfield.label", "condition.notbetweenfield.symbol", FilterParamType.FIELD, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotBetweenField(fieldName, (String) paramA, (String) paramB);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotBetweenField(fieldName, (String) paramA, (String) paramB);
        }
    },
    LIKE_FIELD("LKF", RestrictionType.LIKE, "condition.likefield.label", "condition.likefield.symbol", FilterParamType.FIELD, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addLikeField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new LikeField(fieldName, (String) paramA);
        }
    },
    ILIKE_FIELD("ILKF", RestrictionType.ILIKE, "condition.ilikefield.label", "condition.ilikefield.symbol", FilterParamType.FIELD, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addILikeField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new ILikeField(fieldName, (String) paramA);
        }
    },
    NOT_LIKE_FIELD("NLKF", RestrictionType.NOT_LIKE, "condition.notlikefield.label", "condition.notlikefield.symbol", FilterParamType.FIELD, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotLikeField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotLikeField(fieldName, (String) paramA);
        }
    },
    BEGINS_WITH_FIELD("BWF", RestrictionType.BEGINS_WITH, "condition.beginswithfield.label", "condition.beginswithfield.symbol", FilterParamType.FIELD, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addBeginsWithField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new BeginsWithField(fieldName, (String) paramA);
        }
    },
    IBEGINS_WITH_FIELD("IBWF", RestrictionType.IBEGINS_WITH, "condition.ibeginswithfield.label", "condition.ibeginswithfield.symbol", FilterParamType.FIELD, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addIBeginsWithField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new IBeginsWithField(fieldName, (String) paramA);
        }
    },
    NOT_BEGIN_WITH_FIELD("NBWF", RestrictionType.NOT_BEGIN_WITH, "condition.notbeginswithfield.label", "condition.notbeginswithfield.symbol", FilterParamType.FIELD, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotBeginWithField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotBeginWithField(fieldName, (String) paramA);
        }
    },
    ENDS_WITH_FIELD("EWF", RestrictionType.ENDS_WITH, "condition.endswithfield.label", "condition.endswithfield.symbol", FilterParamType.FIELD, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addEndsWithField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new EndsWithField(fieldName, (String) paramA);
        }
    },
    IENDS_WITH_FIELD("IEWF", RestrictionType.IENDS_WITH, "condition.iendswithfield.label", "condition.iendswithfield.symbol", FilterParamType.FIELD, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addIEndsWithField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new IEndsWithField(fieldName, (String) paramA);
        }
    },
    NOT_END_WITH_FIELD("NEWF", RestrictionType.NOT_END_WITH, "condition.notendswithfield.label", "condition.notendswithfield.symbol", FilterParamType.FIELD, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addNotEndWithField(fieldName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotEndWithField(fieldName, (String) paramA);
        }
    },

    EQUALS_PARAM("EQP", RestrictionType.EQUALS, "condition.equalsparam.label", "condition.equalsparam.symbol", FilterParamType.PARAMETER, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String paramName, Object paramA, Object paramB) {
            cb.addEqualsParam(paramName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String paramName, Object paramA, Object paramB) {
            return new EqualsParam(paramName, (String) paramA);
        }
    },
    NOT_EQUALS_PARAM("NEQP", RestrictionType.NOT_EQUALS, "condition.notequalsparam.label", "condition.notequalsparam.symbol", FilterParamType.PARAMETER, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String paramName, Object paramA, Object paramB) {
            cb.addNotEqualsParam(paramName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String paramName, Object paramA, Object paramB) {
            return new NotEqualsParam(paramName, (String) paramA);
        }
    },
    LESS_THAN_PARAM("LTP", RestrictionType.LESS_THAN, "condition.lessthanparam.label", "condition.lessthanparam.symbol", FilterParamType.PARAMETER, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String paramName, Object paramA, Object paramB) {
            cb.addLessThanParam(paramName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String paramName, Object paramA, Object paramB) {
            return new LessParam(paramName, (String) paramA);
        }
    },
    LESS_OR_EQUAL_PARAM("LTEP", RestrictionType.LESS_OR_EQUAL, "condition.lessorequalparam.label", "condition.lessorequalparam.symbol", FilterParamType.PARAMETER, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String paramName, Object paramA, Object paramB) {
            cb.addLessThanEqualParam(paramName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String paramName, Object paramA, Object paramB) {
            return new LessOrEqualParam(paramName, (String) paramA);
        }
    },
    GREATER_THAN_PARAM("GTP", RestrictionType.GREATER, "condition.greaterthanparam.label", "condition.greaterthanparam.symbol", FilterParamType.PARAMETER, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String paramName, Object paramA, Object paramB) {
            cb.addGreaterThanParam(paramName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String paramName, Object paramA, Object paramB) {
            return new GreaterParam(paramName, (String) paramA);
        }
    },
    GREATER_OR_EQUAL_PARAM("GTEP", RestrictionType.GREATER_OR_EQUAL, "condition.greaterorequalparam.label", "condition.greaterorequalparam.symbol", FilterParamType.PARAMETER, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String paramName, Object paramA, Object paramB) {
            cb.addGreaterThanEqualParam(paramName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String paramName, Object paramA, Object paramB) {
            return new GreaterOrEqualParam(paramName, (String) paramA);
        }
    },
    BETWEEN_PARAM("BTP", RestrictionType.BETWEEN, "condition.betweenparam.label", "condition.betweenparam.symbol", FilterParamType.PARAMETER, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String paramName, Object paramA, Object paramB) {
            cb.addBetweenParam(paramName, (String) paramA, (String) paramB);
        }

        @Override
        public Restriction createSimpleCriteria(String paramName, Object paramA, Object paramB) {
            return new BetweenParam(paramName, (String) paramA, (String) paramB);
        }
    },
    NOT_BETWEEN_PARAM("NBTP", RestrictionType.NOT_BETWEEN, "condition.notbetweenparam.label", "condition.notbetweenparam.symbol", FilterParamType.PARAMETER, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String paramName, Object paramA, Object paramB) {
            cb.addNotBetweenParam(paramName, (String) paramA, (String) paramB);
        }

        @Override
        public Restriction createSimpleCriteria(String paramName, Object paramA, Object paramB) {
            return new NotBetweenParam(paramName, (String) paramA, (String) paramB);
        }
    },
    LIKE_PARAM("LKP", RestrictionType.LIKE, "condition.likeparam.label", "condition.likeparam.symbol", FilterParamType.PARAMETER, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String paramName, Object paramA, Object paramB) {
            cb.addLikeParam(paramName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String paramName, Object paramA, Object paramB) {
            return new LikeParam(paramName, (String) paramA);
        }
    },
    ILIKE_PARAM("ILKP", RestrictionType.ILIKE, "condition.ilikeparam.label", "condition.ilikeparam.symbol", FilterParamType.PARAMETER, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String paramName, Object paramA, Object paramB) {
            cb.addILikeParam(paramName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String paramName, Object paramA, Object paramB) {
            return new ILikeParam(paramName, (String) paramA);
        }
    },
    NOT_LIKE_PARAM("NLKP", RestrictionType.NOT_LIKE, "condition.notlikeparam.label", "condition.notlikeparam.symbol", FilterParamType.PARAMETER, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String paramName, Object paramA, Object paramB) {
            cb.addNotLikeParam(paramName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String paramName, Object paramA, Object paramB) {
            return new NotLikeParam(paramName, (String) paramA);
        }
    },
    BEGINS_WITH_PARAM("BWP", RestrictionType.BEGINS_WITH, "condition.beginswithparam.label", "condition.beginswithparam.symbol", FilterParamType.PARAMETER, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String paramName, Object paramA, Object paramB) {
            cb.addBeginsWithParam(paramName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String paramName, Object paramA, Object paramB) {
            return new BeginsWithParam(paramName, (String) paramA);
        }
    },
    IBEGINS_WITH_PARAM("IBWP", RestrictionType.IBEGINS_WITH, "condition.ibeginswithparam.label", "condition.ibeginswithparam.symbol", FilterParamType.PARAMETER, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String paramName, Object paramA, Object paramB) {
            cb.addIBeginsWithParam(paramName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String paramName, Object paramA, Object paramB) {
            return new IBeginsWithParam(paramName, (String) paramA);
        }
    },
    NOT_BEGIN_WITH_PARAM("NBWP", RestrictionType.NOT_BEGIN_WITH, "condition.notbeginswithparam.label", "condition.notbeginswithparam.symbol", FilterParamType.PARAMETER, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String paramName, Object paramA, Object paramB) {
            cb.addNotBeginWithParam(paramName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String paramName, Object paramA, Object paramB) {
            return new NotBeginWithParam(paramName, (String) paramA);
        }
    },
    ENDS_WITH_PARAM("EWP", RestrictionType.ENDS_WITH, "condition.endswithparam.label", "condition.endswithparam.symbol", FilterParamType.PARAMETER, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String paramName, Object paramA, Object paramB) {
            cb.addEndsWithParam(paramName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String paramName, Object paramA, Object paramB) {
            return new EndsWithParam(paramName, (String) paramA);
        }
    },
    IENDS_WITH_PARAM("IEWP", RestrictionType.IENDS_WITH, "condition.iendswithparam.label", "condition.iendswithparam.symbol", FilterParamType.PARAMETER, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String paramName, Object paramA, Object paramB) {
            cb.addIEndsWithParam(paramName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String paramName, Object paramA, Object paramB) {
            return new IEndsWithParam(paramName, (String) paramA);
        }
    },
    NOT_END_WITH_PARAM("NEWP", RestrictionType.NOT_END_WITH, "condition.notendswithparam.label", "condition.notendswithparam.symbol", FilterParamType.PARAMETER, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String paramName, Object paramA, Object paramB) {
            cb.addNotEndWithParam(paramName, (String) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String paramName, Object paramA, Object paramB) {
            return new NotEndWithParam(paramName, (String) paramA);
        }
    },

    EQUALS_COLLECTION("EQC", RestrictionType.EQUALS, "condition.equalscollection.label", "condition.equalscollection.symbol", FilterParamType.COLLECTION, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addCollectionSizeEquals(fieldName, (Integer) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new EqualsCollection(fieldName, (Integer) paramA);
        }
    },
    NOT_EQUALS_COLLECTION("NEQC", RestrictionType.NOT_EQUALS, "condition.notequalscollection.label", "condition.notequalscollection.symbol", FilterParamType.COLLECTION, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addCollectionSizeNotEquals(fieldName, (Integer) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new NotEqualsCollection(fieldName, (Integer) paramA);
        }
    },
    LESS_THAN_COLLECTION("LTC", RestrictionType.LESS_THAN, "condition.lessthancollection.label", "condition.lessthancollection.symbol", FilterParamType.COLLECTION, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addCollectionSizeLessThan(fieldName, (Integer) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new LessCollection(fieldName, (Integer) paramA);
        }
    },
    LESS_OR_EQUAL_COLLECTION("LTEC", RestrictionType.LESS_OR_EQUAL, "condition.lessorequalcollection.label", "condition.lessorequalcollection.symbol", FilterParamType.COLLECTION, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addCollectionSizeLessThanEqual(fieldName, (Integer) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new LessOrEqualCollection(fieldName, (Integer) paramA);
        }
    },
    GREATER_THAN_COLLECTION("GTC", RestrictionType.GREATER, "condition.greaterthancollection.label", "condition.greaterthancollection.symbol", FilterParamType.COLLECTION, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addCollectionSizeGreaterThan(fieldName, (Integer) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new GreaterCollection(fieldName, (Integer) paramA);
        }
    },
    GREATER_OR_EQUAL_COLLECTION("GTEC", RestrictionType.GREATER_OR_EQUAL, "condition.greaterorequalcollection.label", "condition.greaterorequalcollection.symbol", FilterParamType.COLLECTION, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {
            cb.addCollectionSizeGreaterThanEqual(fieldName, (Integer) paramA);
        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return new GreaterOrEqualCollection(fieldName, (Integer) paramA);
        }
    },

    AND("AND", RestrictionType.AND, "condition.and.label", "condition.and.symbol", FilterParamType.NONE, false, false) {
        @Override
        public void addSimpleCriteria(CriteriaBuilder cb, String fieldName, Object paramA, Object paramB) {

        }

        @Override
        public Restriction createSimpleCriteria(String fieldName, Object paramA, Object paramB) {
            return null;
        }
    },
    OR("OR", RestrictionType.OR, "condition.or.label", "condition.or.symbol", FilterParamType.NONE, false, false) {
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

    private final FilterParamType paramType;

    private final boolean supportRef;

    private final boolean lingual;

    private FilterConditionType(String code, RestrictionType type, String labelKey, String symbolKey,
            FilterParamType paramType, boolean supportRef, boolean lingual) {
        this.code = code;
        this.type = type;
        this.labelKey = labelKey;
        this.symbolKey = symbolKey;
        this.paramType = paramType;
        this.supportRef = supportRef;
        this.lingual = lingual;
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

    public FilterParamType paramType() {
        return paramType;
    }

    public boolean isNoVal() {
        return paramType.isNone();
    }

    public boolean isImmediateVal() {
        return paramType.isImmediate();
    }

    public boolean isFieldVal() {
        return paramType.isField();
    }

    public boolean isParameterVal() {
        return paramType.isParameter();
    }

    public boolean isSessionParameterVal() {
        return paramType.isSessionParameter();
    }

    public boolean isCollectionVal() {
        return paramType.isCollection();
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

    public boolean isSupportRef() {
        return supportRef;
    }

    public boolean isLingual() {
        return lingual;
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
