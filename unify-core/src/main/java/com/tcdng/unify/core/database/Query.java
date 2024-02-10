/*
 * Copyright 2018-2024 The Code Department.
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
import com.tcdng.unify.core.criterion.Amongst;
import com.tcdng.unify.core.criterion.And;
import com.tcdng.unify.core.criterion.BeginsWith;
import com.tcdng.unify.core.criterion.BeginsWithField;
import com.tcdng.unify.core.criterion.Between;
import com.tcdng.unify.core.criterion.BetweenField;
import com.tcdng.unify.core.criterion.CompoundRestriction;
import com.tcdng.unify.core.criterion.EndsWith;
import com.tcdng.unify.core.criterion.EndsWithField;
import com.tcdng.unify.core.criterion.Equals;
import com.tcdng.unify.core.criterion.EqualsField;
import com.tcdng.unify.core.criterion.Greater;
import com.tcdng.unify.core.criterion.GreaterField;
import com.tcdng.unify.core.criterion.GreaterOrEqual;
import com.tcdng.unify.core.criterion.GreaterOrEqualField;
import com.tcdng.unify.core.criterion.GroupBy;
import com.tcdng.unify.core.criterion.IBeginsWith;
import com.tcdng.unify.core.criterion.IBeginsWithField;
import com.tcdng.unify.core.criterion.IEndsWith;
import com.tcdng.unify.core.criterion.IEndsWithField;
import com.tcdng.unify.core.criterion.IEquals;
import com.tcdng.unify.core.criterion.ILike;
import com.tcdng.unify.core.criterion.ILikeField;
import com.tcdng.unify.core.criterion.INotEquals;
import com.tcdng.unify.core.criterion.IsNotNull;
import com.tcdng.unify.core.criterion.IsNull;
import com.tcdng.unify.core.criterion.Less;
import com.tcdng.unify.core.criterion.LessField;
import com.tcdng.unify.core.criterion.LessOrEqual;
import com.tcdng.unify.core.criterion.LessOrEqualField;
import com.tcdng.unify.core.criterion.Like;
import com.tcdng.unify.core.criterion.LikeField;
import com.tcdng.unify.core.criterion.NotAmongst;
import com.tcdng.unify.core.criterion.NotBeginWith;
import com.tcdng.unify.core.criterion.NotBeginWithField;
import com.tcdng.unify.core.criterion.NotBetween;
import com.tcdng.unify.core.criterion.NotEndWith;
import com.tcdng.unify.core.criterion.NotEndWithField;
import com.tcdng.unify.core.criterion.NotEquals;
import com.tcdng.unify.core.criterion.NotEqualsField;
import com.tcdng.unify.core.criterion.NotLike;
import com.tcdng.unify.core.criterion.NotLikeField;
import com.tcdng.unify.core.criterion.Or;
import com.tcdng.unify.core.criterion.Order;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.criterion.Select;

/**
 * Query object.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class Query<T extends Entity> implements Cloneable {

    private Class<T> entityClass;

    private CompoundRestriction restrictions;

    private Select select;

    private GroupBy groupBy;

    private Order order;

    private String maxProperty;

    private String minProperty;

    private int offset;

    private int limit;

    private boolean ignoreTenancy;

    private boolean ignoreEmptyCriteria;

    private boolean applyAppQueryLimit;

    private boolean mustMatch;

    public Query(Class<T> entityClass) {
        this(entityClass, false);
    }

    public Query(Class<T> entityClass, boolean applyAppQueryLimit) {
        this(entityClass, new And(), false);
    }

    public Query(Class<T> entityClass, CompoundRestriction restrictions, boolean applyAppQueryLimit) {
        this.entityClass = entityClass;
        this.restrictions = restrictions;
        this.applyAppQueryLimit = applyAppQueryLimit;
        mustMatch = true;
    }

    public static <U extends Entity> Query<U> of(Class<U> entityClass) {
        return new Query<U>(entityClass);
    }

    public static <U extends Entity> Query<U> of(Class<U> entityClass, boolean applyAppQueryLimit) {
        return new Query<U>(entityClass, applyAppQueryLimit);
    }

    public static <U extends Entity> Query<U> ofDefaultingToAnd(Class<U> entityClass, Restriction restriction) {
        return ofDefaultingToAnd(entityClass, restriction, false);
    }

    public static <U extends Entity> Query<U> ofDefaultingToAnd(Class<U> entityClass, Restriction restriction,
            boolean applyAppQueryLimit) {
        return new Query<U>(entityClass, new And().add(restriction), applyAppQueryLimit);
    }

    public static <U extends Entity> Query<U> ofDefaultingToOr(Class<U> entityClass, Restriction restriction) {
        return ofDefaultingToOr(entityClass, restriction, false);
    }

    public static <U extends Entity> Query<U> ofDefaultingToOr(Class<U> entityClass, Restriction restriction,
            boolean applyAppQueryLimit) {
        return new Query<U>(entityClass, new Or().add(restriction), applyAppQueryLimit);
    }

    public static <U extends Entity> Query<U> of(Class<U> entityClass, CompoundRestriction restrictions) {
        return new Query<U>(entityClass, restrictions, false);
    }

    public static <U extends Entity> Query<U> of(Class<U> entityClass, CompoundRestriction restrictions,
            boolean applyAppQueryLimit) {
        return new Query<U>(entityClass, restrictions, applyAppQueryLimit);
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public Query<T> addAmongst(String field, Collection<? extends Object> values) {
        restrictions.add(new Amongst(field, values));
        return this;
    }

    public Query<T> addBetween(String field, Object lowerValue, Object upperValue) {
        restrictions.add(new Between(field, lowerValue, upperValue));
        return this;
    }

    public Query<T> addBetweenField(String field, String lowerField, String upperField) {
        restrictions.add(new BetweenField(field, lowerField, upperField));
        return this;
    }

    public Query<T> addEquals(String field, Object value) {
        restrictions.add(new Equals(field, value));
        return this;
    }

    public Query<T> addIEquals(String field, String value) {
        restrictions.add(new IEquals(field, value));
        return this;
    }

    public Query<T> addEqualsField(String field1, String field2) {
        restrictions.add(new EqualsField(field1, field2));
        return this;
    }

    public Query<T> addGreaterThan(String field, Object value) {
        restrictions.add(new Greater(field, value));
        return this;
    }

    public Query<T> addGreaterThanField(String field1, String field2) {
        restrictions.add(new GreaterField(field1, field2));
        return this;
    }

    public Query<T> addGreaterThanEqual(String field, Object value) {
        restrictions.add(new GreaterOrEqual(field, value));
        return this;
    }

    public Query<T> addGreaterThanEqualField(String field1, String field2) {
        restrictions.add(new GreaterOrEqualField(field1, field2));
        return this;
    }

    public Query<T> addIsNotNull(String field) {
        restrictions.add(new IsNotNull(field));
        return this;
    }

    public Query<T> addIsNull(String field) {
        restrictions.add(new IsNull(field));
        return this;
    }

    public Query<T> addLessThan(String field, Object value) {
        restrictions.add(new Less(field, value));
        return this;
    }

    public Query<T> addLessThanField(String field1, String field2) {
        restrictions.add(new LessField(field1, field2));
        return this;
    }

    public Query<T> addLessThanEqual(String field, Object value) {
        restrictions.add(new LessOrEqual(field, value));
        return this;
    }

    public Query<T> addLessThanEqualField(String field1, String field2) {
        restrictions.add(new LessOrEqualField(field1, field2));
        return this;
    }

    public Query<T> addLike(String field, String value) {
        restrictions.add(new Like(field, value));
        return this;
    }

    public Query<T> addLikeField(String field1, String field2) {
        restrictions.add(new LikeField(field1, field2));
        return this;
    }

    public Query<T> addILike(String field, String value) {
        restrictions.add(new ILike(field, value));
        return this;
    }

    public Query<T> addILikeField(String field1, String field2) {
        restrictions.add(new ILikeField(field1, field2));
        return this;
    }

    public Query<T> addBeginsWith(String field, String value) {
        restrictions.add(new BeginsWith(field, value));
        return this;
    }

    public Query<T> addBeginsWithField(String field1, String field2) {
        restrictions.add(new BeginsWithField(field1, field2));
        return this;
    }

    public Query<T> addIBeginsWith(String field, String value) {
        restrictions.add(new IBeginsWith(field, value));
        return this;
    }

    public Query<T> addIBeginsWithField(String field1, String field2) {
        restrictions.add(new IBeginsWithField(field1, field2));
        return this;
    }

    public Query<T> addEndsWith(String field, String value) {
        restrictions.add(new EndsWith(field, value));
        return this;
    }

    public Query<T> addEndsWithField(String field1, String field2) {
        restrictions.add(new EndsWithField(field1, field2));
        return this;
    }

    public Query<T> addIEndsWith(String field, String value) {
        restrictions.add(new IEndsWith(field, value));
        return this;
    }

    public Query<T> addIEndsWithField(String field1, String field2) {
        restrictions.add(new IEndsWithField(field1, field2));
        return this;
    }

    public Query<T> addNotAmongst(String field, Collection<? extends Object> values) {
        restrictions.add(new NotAmongst(field, values));
        return this;
    }

    public Query<T> addNotBetween(String field, Object lowerValue, Object upperValue) {
        restrictions.add(new NotBetween(field, lowerValue, upperValue));
        return this;
    }

    public Query<T> addNotEquals(String field, Object value) {
        restrictions.add(new NotEquals(field, value));
        return this;
    }

    public Query<T> addINotEquals(String field, String value) {
        restrictions.add(new INotEquals(field, value));
        return this;
    }

    public Query<T> addNotEqualsField(String field1, String field2) {
        restrictions.add(new NotEqualsField(field1, field2));
        return this;
    }

    public Query<T> addNotLike(String field, String value) {
        restrictions.add(new NotLike(field, value));
        return this;
    }

    public Query<T> addNotLikeField(String field1, String field2) {
        restrictions.add(new NotLikeField(field1, field2));
        return this;
    }

    public Query<T> addNotBeginWith(String field, String value) {
        restrictions.add(new NotBeginWith(field, value));
        return this;
    }

    public Query<T> addNotBeginWithField(String field1, String field2) {
        restrictions.add(new NotBeginWithField(field1, field2));
        return this;
    }

    public Query<T> addNotEndWith(String field, String value) {
        restrictions.add(new NotEndWith(field, value));
        return this;
    }

    public Query<T> addNotEndWithField(String field1, String field2) {
        restrictions.add(new NotEndWithField(field1, field2));
        return this;
    }

    public Query<T> addRestriction(Restriction restriction) {
        if (!restriction.isEmpty()) {
            restrictions.add(restriction);
        }

        return this;
    }

    public Query<T> addSelect(String field) {
        innerGetSelect().add(field);
        return this;
    }

    public Query<T> addSelect(String... fields) {
        for (String field : fields) {
            innerGetSelect().add(field);
        }
        return this;
    }

    public Query<T> addGroupBy(String field) {
        innerGetGroupBy().add(field);
        return this;
    }

    public Query<T> addGroupBy(String... fields) {
        for (String field : fields) {
            innerGetGroupBy().add(field);
        }
        return this;
    }

    public Query<T> addOrder(String field) {
        getOrder().add(field);
        return this;
    }

    public Query<T> addOrder(String... fields) {
        for (String field : fields) {
            getOrder().add(field);
        }
        return this;
    }

    public Query<T> addOrder(OrderType type, String field) {
        getOrder().add(field, type);
        return this;
    }

    public Query<T> addOrder(OrderType type, String... fields) {
        for (String field : fields) {
            getOrder().add(field, type);
        }
        return this;
    }

    public Query<T> setMax(String field) {
        maxProperty = field;
        return this;
    }

    public String getMaxProperty() {
        return maxProperty;
    }

    public boolean isMax() {
        return maxProperty != null;
    }

    public Query<T> setMin(String field) {
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

    public CompoundRestriction getRestrictions() {
        return restrictions;
    }

    public Set<String> getRestrictedFields() {
        Set<String> restrictedFields = new HashSet<String>();
        restrictions.writeRestrictedFields(restrictedFields);
        return restrictedFields;
    }
    
    public boolean isRestrictedField(String fieldName) {
        return restrictions.isRestrictedField(fieldName);
    }

    public boolean isInclusiveRestrictedField(String fieldName) {
        return restrictions.isInclusiveRestrictedField(fieldName);
    }

    public Select getSelect() {
        return select;
    }

    public Query<T>  setSelect(Select select) {
        this.select = select;
        return this;
    }

    public boolean isSelect() {
        return select != null && !select.isEmpty();
    }

    public GroupBy getGroupBy() {
        return groupBy;
    }

    public Query<T> setGroupBy(GroupBy groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    public boolean isGroupBy() {
        return groupBy != null && !groupBy.isEmpty();
    }

    public Order getOrder() {
        if (order == null) {
            order = new Order();
        }
        return order;
    }

    public Query<T>  setOrder(Order order) {
        this.order = order;
        return this;
    }

    public boolean isOrder() {
        return order != null && order.isParts();
    }

    public int getOffset() {
        return offset;
    }

    public Query<T> setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public Query<T> setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public boolean isLimit() {
        return limit > 0;
    }

    public boolean isOffset() {
        return offset > 0;
    }

    public boolean isPagination() {
        return offset >= 0 || limit > 0;
    }

    public boolean isEmptyCriteria() {
        return restrictions.isEmpty();
    }

    public boolean isIgnoreEmptyCriteria() {
        return ignoreEmptyCriteria;
    }

    public Query<T> ignoreEmptyCriteria(boolean ignoreEmptyCriteria) {
        this.ignoreEmptyCriteria = ignoreEmptyCriteria;
        return this;
    }

    public boolean isIgnoreTenancy() {
        return ignoreTenancy;
    }

    public Query<T> ignoreTenancy(boolean ignoreTenancy) {
        this.ignoreTenancy = ignoreTenancy;
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

    public Query<T> setDistinct(boolean distinct) {
        innerGetSelect().setDistinct(distinct);
        return this;
    }

    public boolean replaceAll(String propertyName, Object val) {
        return restrictions.replaceAll(propertyName, val);
    }

    public boolean replaceAll(String propertyName, Object val1, Object val2) {
        return restrictions.replaceAll(propertyName, val1, val2);
    }

    public boolean replaceAll(String propertyName, Collection<Object> val) {
        return restrictions.replaceAll(propertyName, val);
    }

    public Query<T> clear() {
        restrictions.clear();
        if (select != null) {
            select.clear();
        }

        if (groupBy != null) {
            groupBy.clear();
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
        restrictions.clear();
    }

    public void clearSelect() {
        if (select != null) {
            select.clear();
        }
    }

    public void clearGroupBy() {
        if (groupBy != null) {
            groupBy.clear();
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
        query.restrictions = restrictions;
        query.select = select;
        query.groupBy = groupBy;
        query.order = order;
        query.offset = offset;
        query.limit = limit;
        query.ignoreEmptyCriteria = ignoreEmptyCriteria;
        query.ignoreTenancy = ignoreTenancy;
        query.mustMatch = mustMatch;
        return query;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Query<T> copyNoCriteria() {
        Query<T> query = new Query(entityClass, applyAppQueryLimit);
        query.select = select;
        query.groupBy = groupBy;
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

    private GroupBy innerGetGroupBy() {
        if (groupBy == null) {
            groupBy = new GroupBy();
        }
        return groupBy;
    }
}
