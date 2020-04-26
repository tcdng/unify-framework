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
import java.util.Stack;

/**
 * Criteria builder.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class CriteriaBuilder {

    private Stack<CompoundRestriction> buildStack;

    private CompoundRestriction rootCondition;

    private CriteriaBuilder() {
        buildStack = new Stack<CompoundRestriction>();
    }

    public CriteriaBuilder beginAnd() {
        beginCompound(new And());
        return this;
    }

    public CriteriaBuilder beginOr() {
        beginCompound(new Or());
        return this;
    }

    public CriteriaBuilder endCompound() {
        if (buildStack.isEmpty()) {
            throw new RuntimeException("Compound condition is not started.");
        }

        CompoundRestriction cr = buildStack.peek();
        if (cr.getRestrictionList().isEmpty()) {
            throw new RuntimeException("Compound conditon must have at least one subcondition.");
        }

        buildStack.pop();
        if (buildStack.isEmpty()) {
            rootCondition = cr;
        } else {
            buildStack.get(buildStack.size() - 1).getRestrictionList().add(cr);
        }
        
        return this;
    }

    public CriteriaBuilder addAmongst(String fieldName, Collection<?> values) {
        addSimpleCondition(new Amongst(fieldName, values));
        return this;
    }

    public CriteriaBuilder addBetween(String fieldName, Object lowerValue, Object upperValue) {
        addSimpleCondition(new Between(fieldName, lowerValue, upperValue));
        return this;
    }

    public CriteriaBuilder addEquals(String fieldName, Object value) {
        addSimpleCondition(new Equals(fieldName, value));
        return this;
    }

    public CriteriaBuilder addEqualsField(String fieldNameA, String fieldNameB) {
        addSimpleCondition(new EqualsField(fieldNameA, fieldNameB));
        return this;
    }

    public CriteriaBuilder addGreaterThan(String fieldName, Object value) {
        addSimpleCondition(new Greater(fieldName, value));
        return this;
    }

    public CriteriaBuilder addGreaterThanField(String fieldNameA, String fieldNameB) {
        addSimpleCondition(new GreaterField(fieldNameA, fieldNameB));
        return this;
    }

    public CriteriaBuilder addGreaterThanEqual(String fieldName, Object value) {
        addSimpleCondition(new GreaterOrEqual(fieldName, value));
        return this;
    }

    public CriteriaBuilder addGreaterThanEqualField(String fieldNameA, String fieldNameB) {
        addSimpleCondition(new GreaterOrEqualField(fieldNameA, fieldNameB));
        return this;
    }

    public CriteriaBuilder addIsNotNull(String fieldName) {
        addSimpleCondition(new IsNotNull(fieldName));
        return this;
    }

    public CriteriaBuilder addIsNull(String fieldName) {
        addSimpleCondition(new IsNull(fieldName));
        return this;
    }

    public CriteriaBuilder addLessThan(String fieldName, Object value) {
        addSimpleCondition(new Less(fieldName, value));
        return this;
    }

    public CriteriaBuilder addLessThanField(String fieldNameA, String fieldNameB) {
        addSimpleCondition(new LessField(fieldNameA, fieldNameB));
        return this;
    }

    public CriteriaBuilder addLessThanEqual(String fieldName, Object value) {
        addSimpleCondition(new LessOrEqual(fieldName, value));
        return this;
    }

    public CriteriaBuilder addLessThanEqualField(String fieldNameA, String fieldNameB) {
        addSimpleCondition(new LessOrEqualField(fieldNameA, fieldNameB));
        return this;
    }

    public CriteriaBuilder addLike(String fieldName, String value) {
        addSimpleCondition(new Like(fieldName, value));
        return this;
    }

    public CriteriaBuilder addLikeField(String fieldNameA, String fieldNameB) {
        addSimpleCondition(new LikeField(fieldNameA, fieldNameB));
        return this;
    }

    public CriteriaBuilder addBeginsWith(String fieldName, String value) {
        addSimpleCondition(new BeginsWith(fieldName, value));
        return this;
    }

    public CriteriaBuilder addBeginsWithField(String fieldNameA, String fieldNameB) {
        addSimpleCondition(new BeginsWithField(fieldNameA, fieldNameB));
        return this;
    }

    public CriteriaBuilder addEndsWith(String fieldName, String value) {
        addSimpleCondition(new EndsWith(fieldName, value));
        return this;
    }

    public CriteriaBuilder addEndsWithField(String fieldNameA, String fieldNameB) {
        addSimpleCondition(new EndsWithField(fieldNameA, fieldNameB));
        return this;
    }

    public CriteriaBuilder addNotAmongst(String fieldName, Collection<Object> values) {
        addSimpleCondition(new NotAmongst(fieldName, values));
        return this;
    }

    public CriteriaBuilder addNotBetween(String fieldName, Object lowerValue, Object upperValue) {
        addSimpleCondition(new NotBetween(fieldName, lowerValue, upperValue));
        return this;
    }

    public CriteriaBuilder addNotEquals(String fieldName, Object value) {
        addSimpleCondition(new NotEquals(fieldName, value));
        return this;
    }

    public CriteriaBuilder addNotEqualsField(String fieldNameA, String fieldNameB) {
        addSimpleCondition(new NotEqualsField(fieldNameA, fieldNameB));
        return this;
    }

    public CriteriaBuilder addNotLike(String fieldName, String value) {
        addSimpleCondition(new NotLike(fieldName, value));
        return this;
    }

    public CriteriaBuilder addNotLikeField(String fieldNameA, String fieldNameB) {
        addSimpleCondition(new NotLikeField(fieldNameA, fieldNameB));
        return this;
    }

    public CriteriaBuilder addNotBeginWith(String fieldName, String value) {
        addSimpleCondition(new NotBeginWith(fieldName, value));
        return this;
    }

    public CriteriaBuilder addNotBeginWithField(String fieldNameA, String fieldNameB) {
        addSimpleCondition(new NotBeginWithField(fieldNameA, fieldNameB));
        return this;
    }

    public CriteriaBuilder addNotEndWith(String fieldName, String value) {
        addSimpleCondition(new NotEndWith(fieldName, value));
        return this;
    }

    public CriteriaBuilder addNotEndWithField(String fieldNameA, String fieldNameB) {
        addSimpleCondition(new NotEndWithField(fieldNameA, fieldNameB));
        return this;
    }

    public CompoundRestriction build() {
        if (rootCondition == null) {
            throw new RuntimeException("Root condition is not established.");
        }

        return rootCondition;
    }

    private void beginCompound(CompoundRestriction compoundRestriction) {
        if (rootCondition != null) {
            throw new RuntimeException("Root condition is already established.");
        }

        buildStack.push(compoundRestriction);
    }


    private CriteriaBuilder addSimpleCondition(SimpleRestriction sr) {
        if (rootCondition != null) {
            throw new RuntimeException("Root condition already established.");
        }

        if (buildStack.isEmpty()) {
            throw new RuntimeException("Compound condition not started.");
        }

        buildStack.peek().getRestrictionList().add(sr);
        return this;
    }
}
