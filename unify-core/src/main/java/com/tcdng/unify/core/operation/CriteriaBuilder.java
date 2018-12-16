/*
 * Copyright 2018 The Code Department
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
package com.tcdng.unify.core.operation;

import java.util.List;

/**
 * Builder class used to construct multiple condition criteria.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class CriteriaBuilder implements Cloneable {

    protected Criteria criteria;

    public Criteria getCriteria() {
        return criteria;
    }

    public CriteriaBuilder amongst(String field, List<? extends Object> values) {
        return add(new Amongst(field, values));
    }

    public CriteriaBuilder between(String field, Object lowerValue, Object upperValue) {
        return add(new Between(field, lowerValue, upperValue));
    }

    public CriteriaBuilder equal(String field, Object value) {
        return add(new Equal(field, value));
    }

    public CriteriaBuilder greater(String field, Object value) {
        return add(new Greater(field, value));
    }

    public CriteriaBuilder greaterEqual(String field, Object value) {
        return add(new GreaterOrEqual(field, value));
    }

    public CriteriaBuilder isNotNull(String field) {
        return add(new IsNotNull(field));
    }

    public CriteriaBuilder isNull(String field) {
        return add(new IsNull(field));
    }

    public CriteriaBuilder less(String field, Object value) {
        return add(new Less(field, value));
    }

    public CriteriaBuilder lessEqual(String field, Object value) {
        return add(new LessOrEqual(field, value));
    }

    public CriteriaBuilder like(String field, String value) {
        return add(new Like(field, value));
    }

    public CriteriaBuilder likeBegin(String field, String value) {
        return add(new LikeBegin(field, value));
    }

    public CriteriaBuilder likeEnd(String field, String value) {
        return add(new LikeEnd(field, value));
    }

    public CriteriaBuilder notAmongst(String field, List<? extends Object> values) {
        return add(new NotAmongst(field, values));
    }

    public CriteriaBuilder notBetween(String field, Object lowerValue, Object upperValue) {
        return add(new NotBetween(field, lowerValue, upperValue));
    }

    public CriteriaBuilder notEqual(String field, Object value) {
        return add(new NotEqual(field, value));
    }

    public CriteriaBuilder notLike(String field, String value) {
        return add(new NotLike(field, value));
    }

    public CriteriaBuilder notLikeBegin(String field, String value) {
        return add(new NotLikeBegin(field, value));
    }

    public CriteriaBuilder notLikeEnd(String field, String value) {
        return add(new NotLikeEnd(field, value));
    }

    public void clear() {
        criteria = null;
    }

    public boolean isEmpty() {
        return criteria == null;
    }

    public CriteriaBuilder copy() {
        try {
            return (CriteriaBuilder) clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        CriteriaBuilder clone = (CriteriaBuilder) super.clone();
        if (criteria != null) {
            clone.criteria = (Criteria) criteria.clone();
        }
        return clone;
    }

    protected CriteriaBuilder add(CriteriaBuilder criteriaBuilder) {
        return add(criteriaBuilder.getCriteria());
    }

    protected abstract CriteriaBuilder add(Criteria criteria);
}
