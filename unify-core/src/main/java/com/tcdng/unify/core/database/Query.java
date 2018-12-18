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
package com.tcdng.unify.core.database;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.tcdng.unify.core.constant.OrderType;
import com.tcdng.unify.core.operation.Amongst;
import com.tcdng.unify.core.operation.AndBuilder;
import com.tcdng.unify.core.operation.Between;
import com.tcdng.unify.core.operation.Criteria;
import com.tcdng.unify.core.operation.CriteriaBuilder;
import com.tcdng.unify.core.operation.Equal;
import com.tcdng.unify.core.operation.Greater;
import com.tcdng.unify.core.operation.GreaterOrEqual;
import com.tcdng.unify.core.operation.IsNotNull;
import com.tcdng.unify.core.operation.IsNull;
import com.tcdng.unify.core.operation.Less;
import com.tcdng.unify.core.operation.LessOrEqual;
import com.tcdng.unify.core.operation.Like;
import com.tcdng.unify.core.operation.LikeBegin;
import com.tcdng.unify.core.operation.LikeEnd;
import com.tcdng.unify.core.operation.NotAmongst;
import com.tcdng.unify.core.operation.NotBetween;
import com.tcdng.unify.core.operation.NotEqual;
import com.tcdng.unify.core.operation.NotLike;
import com.tcdng.unify.core.operation.NotLikeBegin;
import com.tcdng.unify.core.operation.NotLikeEnd;
import com.tcdng.unify.core.operation.Order;
import com.tcdng.unify.core.operation.Select;

/**
 * Query object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class Query<T extends Entity> implements Cloneable {

    private Class<T> entityClass;

    private AndBuilder andBuilder;

    private Select select;

    private Order order;

    private String maxProperty;

    private String minProperty;

    private int offset;

    private int limit;

    private boolean ignoreEmptyCriteria;

    private boolean applyAppQueryLimit;

    private boolean mustMatch;

    public Query(Class<T> entityClass) {
        this(entityClass, false);
    }

    public Query(Class<T> entityClass, boolean applyAppQueryLimit) {
        this.entityClass = entityClass;
        andBuilder = new AndBuilder();
        this.applyAppQueryLimit = applyAppQueryLimit;
        mustMatch = true;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public Query<T> amongst(String field, Collection<? extends Object> values) {
        andBuilder.and(new Amongst(field, values));
        return this;
    }

    public Query<T> between(String field, Object lowerValue, Object upperValue) {
        andBuilder.and(new Between(field, lowerValue, upperValue));
        return this;
    }

    public Query<T> equals(String field, Object value) {
        andBuilder.and(new Equal(field, value));
        return this;
    }

    public Query<T> greater(String field, Object value) {
        andBuilder.and(new Greater(field, value));
        return this;
    }

    public Query<T> greaterEqual(String field, Object value) {
        andBuilder.and(new GreaterOrEqual(field, value));
        return this;
    }

    public Query<T> isNotNull(String field) {
        andBuilder.and(new IsNotNull(field));
        return this;
    }

    public Query<T> isNull(String field) {
        andBuilder.and(new IsNull(field));
        return this;
    }

    public Query<T> less(String field, Object value) {
        andBuilder.and(new Less(field, value));
        return this;
    }

    public Query<T> lessEqual(String field, Object value) {
        andBuilder.and(new LessOrEqual(field, value));
        return this;
    }

    public Query<T> like(String field, String value) {
        andBuilder.and(new Like(field, value));
        return this;
    }

    public Query<T> likeBegin(String field, String value) {
        andBuilder.and(new LikeBegin(field, value));
        return this;
    }

    public Query<T> likeEnd(String field, String value) {
        andBuilder.and(new LikeEnd(field, value));
        return this;
    }

    public Query<T> notAmongst(String field, Collection<? extends Object> values) {
        andBuilder.and(new NotAmongst(field, values));
        return this;
    }

    public Query<T> notBetween(String field, Object lowerValue, Object upperValue) {
        andBuilder.and(new NotBetween(field, lowerValue, upperValue));
        return this;
    }

    public Query<T> notEqual(String field, Object value) {
        andBuilder.and(new NotEqual(field, value));
        return this;
    }

    public Query<T> notLike(String field, String value) {
        andBuilder.and(new NotLike(field, value));
        return this;
    }

    public Query<T> notLikeBegin(String field, String value) {
        andBuilder.and(new NotLikeBegin(field, value));
        return this;
    }

    public Query<T> notLikeEnd(String field, String value) {
        andBuilder.and(new NotLikeEnd(field, value));
        return this;
    }

    public Query<T> add(CriteriaBuilder criteriaBuilder) {
        andBuilder.and(criteriaBuilder);
        return this;
    }

    public Query<T> add(Criteria criteria) {
        andBuilder.and(criteria);
        return this;
    }

    public Query<T> select(String field) {
        innerGetSelect().add(field);
        return this;
    }

    public Query<T> select(String... fields) {
        for (String field : fields) {
            innerGetSelect().add(field);
        }
        return this;
    }

    public Query<T> order(String field) {
        getOrder().add(field);
        return this;
    }

    public Query<T> order(String... fields) {
        for (String field : fields) {
            getOrder().add(field);
        }
        return this;
    }

    public Query<T> order(OrderType type, String field) {
        getOrder().add(field, type);
        return this;
    }

    public Query<T> order(OrderType type, String... fields) {
        for (String field : fields) {
            getOrder().add(field, type);
        }
        return this;
    }

    public Query<T> max(String field) {
        maxProperty = field;
        return this;
    }

    public String getMaxProperty() {
        return maxProperty;
    }

    public boolean isMax() {
        return maxProperty != null;
    }

    public Query<T> min(String field) {
        minProperty = field;
        return this;
    }

    public String getMinProperty() {
        return minProperty;
    }

    public boolean isMin() {
        return minProperty != null;
    }

    public boolean isMinMax() {
        return minProperty != null || maxProperty != null;
    }

    public Criteria getCriteria() {
        return andBuilder.getCriteria();
    }

    public Set<String> getFields() {
        Set<String> fields = new HashSet<String>();
        if (andBuilder.getCriteria() != null) {
            andBuilder.getCriteria().getFields(fields);
        }
        return fields;
    }

    public Select getSelect() {
        return select;
    }

    public void setSelect(Select select) {
        this.select = select;
    }

    public Order getOrder() {
        if (order == null) {
            order = new Order();
        }
        return order;
    }

    public boolean isOrder() {
        return order != null;
    }

    public int getOffset() {
        return offset;
    }

    public Query<T> offset(int offset) {
        this.offset = offset;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public Query<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public boolean isLimit() {
        return limit > 0;
    }

    public boolean isOffset() {
        return offset > 0;
    }

    public boolean isEmptyCriteria() {
        return andBuilder.isEmpty();
    }

    public boolean isIgnoreEmptyCriteria() {
        return ignoreEmptyCriteria;
    }

    public Query<T> ignoreEmptyCriteria(boolean ignoreEmptyCriteria) {
        this.ignoreEmptyCriteria = ignoreEmptyCriteria;
        return this;
    }

    public boolean isApplyAppQueryLimit() {
        return applyAppQueryLimit;
    }

    public Query<T> applyAppQueryLimit(boolean applyAppQueryLimit) {
        this.applyAppQueryLimit = applyAppQueryLimit;
        return this;
    }

    public boolean isMustMatch() {
        return mustMatch;
    }

    public Query<T> mustMatch(boolean mustMatch) {
        this.mustMatch = mustMatch;
        return this;
    }

    public boolean isDistinct() {
        return innerGetSelect().isDistinct();
    }

    public Query<T> distinct(boolean distinct) {
        innerGetSelect().setDistinct(distinct);
        return this;
    }

    public Query<T> clear() {
        andBuilder.clear();
        if (select != null) {
            select.clear();
        }

        if (order != null) {
            order.clear();
        }

        offset = 0;
        limit = 0;
        ignoreEmptyCriteria = false;
        applyAppQueryLimit = false;
        return this;
    }

    public void clearCriteria() {
        andBuilder.clear();
    }

    public void clearSelect() {
        if (select != null) {
            select.clear();
        }
    }

    public void clearOrder() {
        if (order != null) {
            order.clear();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Query<T> copy() {
        Query<T> query = new Query(entityClass, applyAppQueryLimit);
        query.andBuilder = andBuilder;
        query.select = select;
        query.order = order;
        query.offset = offset;
        query.limit = limit;
        query.ignoreEmptyCriteria = ignoreEmptyCriteria;
        query.mustMatch = mustMatch;
        return query;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Query<T> copyNoCriteria() {
        Query<T> query = new Query(entityClass, applyAppQueryLimit);
        query.select = select;
        query.order = order;
        query.offset = offset;
        query.limit = limit;
        query.ignoreEmptyCriteria = ignoreEmptyCriteria;
        query.mustMatch = mustMatch;
        return query;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Query<T> copyNoAll() {
        return new Query(entityClass, applyAppQueryLimit);
    }

    private Select innerGetSelect() {
        if (select == null) {
            select = new Select();
        }
        return select;
    }
}
