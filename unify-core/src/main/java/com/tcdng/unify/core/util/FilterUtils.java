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

package com.tcdng.unify.core.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.criterion.FilterConditionType;
import com.tcdng.unify.core.filter.BeanFilterPolicy;
import com.tcdng.unify.core.filter.policy.AmongstPolicy;
import com.tcdng.unify.core.filter.policy.AndPolicy;
import com.tcdng.unify.core.filter.policy.BeginsWithPolicy;
import com.tcdng.unify.core.filter.policy.BetweenPolicy;
import com.tcdng.unify.core.filter.policy.EndsWithPolicy;
import com.tcdng.unify.core.filter.policy.EqualsCollectionPolicy;
import com.tcdng.unify.core.filter.policy.EqualsPolicy;
import com.tcdng.unify.core.filter.policy.GreaterCollectionPolicy;
import com.tcdng.unify.core.filter.policy.GreaterOrEqualCollectionPolicy;
import com.tcdng.unify.core.filter.policy.GreaterOrEqualPolicy;
import com.tcdng.unify.core.filter.policy.GreaterPolicy;
import com.tcdng.unify.core.filter.policy.IsNotNullPolicy;
import com.tcdng.unify.core.filter.policy.IsNullPolicy;
import com.tcdng.unify.core.filter.policy.LessCollectionPolicy;
import com.tcdng.unify.core.filter.policy.LessOrEqualCollectionPolicy;
import com.tcdng.unify.core.filter.policy.LessOrEqualPolicy;
import com.tcdng.unify.core.filter.policy.LessPolicy;
import com.tcdng.unify.core.filter.policy.LikePolicy;
import com.tcdng.unify.core.filter.policy.NotAmongstPolicy;
import com.tcdng.unify.core.filter.policy.NotBeginWithPolicy;
import com.tcdng.unify.core.filter.policy.NotBetweenPolicy;
import com.tcdng.unify.core.filter.policy.NotEndWithPolicy;
import com.tcdng.unify.core.filter.policy.NotEqualsCollectionPolicy;
import com.tcdng.unify.core.filter.policy.NotEqualsPolicy;
import com.tcdng.unify.core.filter.policy.NotLikePolicy;
import com.tcdng.unify.core.filter.policy.OrPolicy;

/**
 * Filter utilities.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class FilterUtils {

    private static final Map<Class<?>, String> classToFilterConditionSelectorMap;

    static {
        Map<Class<?>, String> map = new HashMap<Class<?>, String>();
        map.put(boolean.class, "!booleanconditionlist");
        map.put(Boolean.class, "!booleanconditionlist");
        map.put(char.class, "!stringconditionlist");
        map.put(Character.class, "!stringconditionlist");
        map.put(short.class, "!numberconditionlist");
        map.put(Short.class, "!numberconditionlist");
        map.put(int.class, "!numberconditionlist");
        map.put(Integer.class, "!numberconditionlist");
        map.put(long.class, "!numberconditionlist");
        map.put(Long.class, "!numberconditionlist");
        map.put(float.class, "!numberconditionlist");
        map.put(Float.class, "!numberconditionlist");
        map.put(double.class, "!numberconditionlist");
        map.put(Double.class, "!numberconditionlist");
        map.put(BigDecimal.class, "!numberconditionlist");
        map.put(Date.class, "!numberconditionlist");
        map.put(String.class, "!stringconditionlist");
        classToFilterConditionSelectorMap = Collections.unmodifiableMap(map);
    }

    private static final Set<FilterConditionType> booleanConditionTypes = Collections.unmodifiableSet(new LinkedHashSet<FilterConditionType>(
           Arrays.asList(FilterConditionType.EQUALS,
                    FilterConditionType.IS_NULL,
                    FilterConditionType.IS_NOT_NULL,
                    FilterConditionType.NOT_EQUALS,
                    FilterConditionType.EQUALS_FIELD,
                    FilterConditionType.NOT_EQUALS_FIELD)));
    
    private static final Set<FilterConditionType> numberConditionTypes = Collections.unmodifiableSet(new LinkedHashSet<FilterConditionType>(
            Arrays.asList(FilterConditionType.EQUALS,
                    FilterConditionType.GREATER_THAN,
                    FilterConditionType.GREATER_OR_EQUAL,
                    FilterConditionType.LESS_THAN,
                    FilterConditionType.LESS_OR_EQUAL,
                    FilterConditionType.BETWEEN,
                    FilterConditionType.AMONGST,
                    FilterConditionType.IS_NULL,
                    FilterConditionType.IS_NOT_NULL,
                    FilterConditionType.NOT_EQUALS,
                    FilterConditionType.NOT_BETWEEN,
                    FilterConditionType.NOT_AMONGST,
                    FilterConditionType.EQUALS_FIELD,
                    FilterConditionType.GREATER_THAN_FIELD,
                    FilterConditionType.GREATER_OR_EQUAL_FIELD,
                    FilterConditionType.LESS_THAN_FIELD,
                    FilterConditionType.LESS_OR_EQUAL_FIELD,
                    FilterConditionType.BETWEEN_FIELD,
                    FilterConditionType.NOT_EQUALS_FIELD,
                    FilterConditionType.NOT_BETWEEN_FIELD)));
     
    private static final Set<FilterConditionType> stringConditionTypes = Collections.unmodifiableSet(new LinkedHashSet<FilterConditionType>(
            Arrays.asList(FilterConditionType.EQUALS,
                    FilterConditionType.BEGINS_WITH,
                    FilterConditionType.ENDS_WITH,
                    FilterConditionType.LIKE,
                    FilterConditionType.AMONGST,
                    FilterConditionType.IS_NULL,
                    FilterConditionType.IS_NOT_NULL,
                    FilterConditionType.NOT_EQUALS,
                    FilterConditionType.NOT_BEGIN_WITH,
                    FilterConditionType.NOT_END_WITH,
                    FilterConditionType.NOT_LIKE,
                    FilterConditionType.NOT_AMONGST,
                    FilterConditionType.EQUALS_FIELD,
                    FilterConditionType.BEGINS_WITH_FIELD,
                    FilterConditionType.ENDS_WITH_FIELD,
                    FilterConditionType.LIKE_FIELD,
                    FilterConditionType.NOT_EQUALS_FIELD,
                    FilterConditionType.NOT_BEGIN_WITH_FIELD,
                    FilterConditionType.NOT_END_WITH_FIELD,
                    FilterConditionType.NOT_LIKE_FIELD)));

    private static final Set<FilterConditionType> enumConstConditionTypes = Collections.unmodifiableSet(new LinkedHashSet<FilterConditionType>(
           Arrays.asList(FilterConditionType.EQUALS,
                   FilterConditionType.AMONGST,
                   FilterConditionType.IS_NULL,
                   FilterConditionType.IS_NOT_NULL,
                   FilterConditionType.NOT_EQUALS,
                   FilterConditionType.NOT_AMONGST)));
    
    private static final Set<FilterConditionType> collectionConditionTypes = Collections.unmodifiableSet(new LinkedHashSet<FilterConditionType>(
            Arrays.asList(FilterConditionType.EQUALS_COLLECTION,
                    FilterConditionType.GREATER_THAN_COLLECTION,
                    FilterConditionType.GREATER_OR_EQUAL_COLLECTION,
                    FilterConditionType.LESS_THAN_COLLECTION,
                    FilterConditionType.LESS_OR_EQUAL_COLLECTION,
                    FilterConditionType.IS_NULL,
                    FilterConditionType.IS_NOT_NULL,
                    FilterConditionType.NOT_EQUALS_COLLECTION)));
    
    private static final Set<FilterConditionType> objectConditionTypes = Collections.unmodifiableSet(new LinkedHashSet<FilterConditionType>(
            Arrays.asList(FilterConditionType.IS_NULL,
                    FilterConditionType.IS_NOT_NULL)));

    private static final Map<Class<?>, Set<FilterConditionType>> supportedConditionMap;

    static {
        Map<Class<?>, Set<FilterConditionType>> map = new HashMap<Class<?>, Set<FilterConditionType>>();
        map.put(boolean.class, booleanConditionTypes);
        map.put(Boolean.class, booleanConditionTypes);
        map.put(char.class, stringConditionTypes);
        map.put(Character.class, stringConditionTypes);
        map.put(short.class, numberConditionTypes);
        map.put(Short.class, numberConditionTypes);
        map.put(int.class, numberConditionTypes);
        map.put(Integer.class, numberConditionTypes);
        map.put(long.class, numberConditionTypes);
        map.put(Long.class, numberConditionTypes);
        map.put(float.class, numberConditionTypes);
        map.put(Float.class, numberConditionTypes);
        map.put(double.class, numberConditionTypes);
        map.put(Double.class, numberConditionTypes);
        map.put(BigDecimal.class, numberConditionTypes);
        map.put(Date.class, numberConditionTypes);
        map.put(String.class, stringConditionTypes);
        supportedConditionMap = Collections.unmodifiableMap(map);
    }

    private static final Map<FilterConditionType, BeanFilterPolicy> filterPolicies;

    static {
        Map<FilterConditionType, BeanFilterPolicy> policies =
                new EnumMap<FilterConditionType, BeanFilterPolicy>(FilterConditionType.class);
        policies.put(FilterConditionType.EQUALS, new EqualsPolicy());
        policies.put(FilterConditionType.NOT_EQUALS, new NotEqualsPolicy());
        policies.put(FilterConditionType.LESS_THAN, new LessPolicy());
        policies.put(FilterConditionType.LESS_OR_EQUAL, new LessOrEqualPolicy());
        policies.put(FilterConditionType.GREATER_THAN, new GreaterPolicy());
        policies.put(FilterConditionType.GREATER_OR_EQUAL, new GreaterOrEqualPolicy());
        policies.put(FilterConditionType.BETWEEN, new BetweenPolicy());
        policies.put(FilterConditionType.NOT_BETWEEN, new NotBetweenPolicy());
        policies.put(FilterConditionType.AMONGST, new AmongstPolicy());
        policies.put(FilterConditionType.NOT_AMONGST, new NotAmongstPolicy());
        policies.put(FilterConditionType.LIKE, new LikePolicy());
        policies.put(FilterConditionType.NOT_LIKE, new NotLikePolicy());
        policies.put(FilterConditionType.BEGINS_WITH, new BeginsWithPolicy());
        policies.put(FilterConditionType.NOT_BEGIN_WITH, new NotBeginWithPolicy());
        policies.put(FilterConditionType.ENDS_WITH, new EndsWithPolicy());
        policies.put(FilterConditionType.NOT_END_WITH, new NotEndWithPolicy());
        policies.put(FilterConditionType.IS_NULL, new IsNullPolicy());
        policies.put(FilterConditionType.IS_NOT_NULL, new IsNotNullPolicy());
        policies.put(FilterConditionType.EQUALS_FIELD, new EqualsPolicy());
        policies.put(FilterConditionType.NOT_EQUALS_FIELD, new NotEqualsPolicy());
        policies.put(FilterConditionType.LESS_THAN_FIELD, new LessPolicy());
        policies.put(FilterConditionType.LESS_OR_EQUAL_FIELD, new LessOrEqualPolicy());
        policies.put(FilterConditionType.GREATER_THAN_FIELD, new GreaterPolicy());
        policies.put(FilterConditionType.GREATER_OR_EQUAL_FIELD, new GreaterOrEqualPolicy());
        policies.put(FilterConditionType.BETWEEN_FIELD, new BetweenPolicy());
        policies.put(FilterConditionType.NOT_BETWEEN_FIELD, new NotBetweenPolicy());
        policies.put(FilterConditionType.LIKE_FIELD, new LikePolicy());
        policies.put(FilterConditionType.NOT_LIKE_FIELD, new NotLikePolicy());
        policies.put(FilterConditionType.BEGINS_WITH_FIELD, new BeginsWithPolicy());
        policies.put(FilterConditionType.NOT_BEGIN_WITH_FIELD, new NotBeginWithPolicy());
        policies.put(FilterConditionType.ENDS_WITH_FIELD, new EndsWithPolicy());
        policies.put(FilterConditionType.NOT_END_WITH_FIELD, new NotEndWithPolicy());
        policies.put(FilterConditionType.EQUALS_COLLECTION, new EqualsCollectionPolicy());
        policies.put(FilterConditionType.NOT_EQUALS_COLLECTION, new NotEqualsCollectionPolicy());
        policies.put(FilterConditionType.LESS_THAN_COLLECTION, new LessCollectionPolicy());
        policies.put(FilterConditionType.LESS_OR_EQUAL_COLLECTION, new LessOrEqualCollectionPolicy());
        policies.put(FilterConditionType.GREATER_THAN_COLLECTION, new GreaterCollectionPolicy());
        policies.put(FilterConditionType.GREATER_OR_EQUAL_COLLECTION, new GreaterOrEqualCollectionPolicy());
        policies.put(FilterConditionType.AND, new AndPolicy());
        policies.put(FilterConditionType.OR, new OrPolicy());
        filterPolicies = Collections.unmodifiableMap(policies);
    }

    private FilterUtils() {
        
    }
    
    public static String getFilterConditionTypeSelectDescriptior(Class<?> fieldType) {
        String descriptor = classToFilterConditionSelectorMap.get(fieldType);
        if (descriptor != null) {
            return descriptor;
        }

        if (EnumConst.class.isAssignableFrom(fieldType)) {
            return "!enumconstconditionlist";
        }

        if (fieldType.isArray() || Collection.class.isAssignableFrom(fieldType)) {
            return "!collectionconditionlist";
        }

        return "!objectconditionlist";
    }

    public static Set<FilterConditionType> getSupportedFilterConditionTypes(Class<?> fieldType) {
        Set<FilterConditionType> supported = supportedConditionMap.get(fieldType);
        if (supported != null) {
            return supported;
        }

        if (EnumConst.class.isAssignableFrom(fieldType)) {
            return enumConstConditionTypes;
        }

        if (fieldType.isArray() || Collection.class.isAssignableFrom(fieldType)) {
            return collectionConditionTypes;
        }

        return objectConditionTypes;
    }

    public static boolean isFilterConditionSupportedForType(Class<?> fieldType, FilterConditionType type) {
        Set<FilterConditionType> supported = FilterUtils.getSupportedFilterConditionTypes(fieldType);
        return supported.contains(type);
    }
    
    public static BeanFilterPolicy getBeanFilterPolicy(FilterConditionType type) {
        return filterPolicies.get(type);
    }
}
