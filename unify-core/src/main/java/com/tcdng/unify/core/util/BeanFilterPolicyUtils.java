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

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import com.tcdng.unify.core.criterion.FilterConditionType;
import com.tcdng.unify.core.filter.BeanFilterPolicy;
import com.tcdng.unify.core.filter.policy.AmongstPolicy;
import com.tcdng.unify.core.filter.policy.AndPolicy;
import com.tcdng.unify.core.filter.policy.BeginsWithFieldPolicy;
import com.tcdng.unify.core.filter.policy.BeginsWithPolicy;
import com.tcdng.unify.core.filter.policy.BetweenFieldPolicy;
import com.tcdng.unify.core.filter.policy.BetweenPolicy;
import com.tcdng.unify.core.filter.policy.EndsWithFieldPolicy;
import com.tcdng.unify.core.filter.policy.EndsWithPolicy;
import com.tcdng.unify.core.filter.policy.EqualsFieldPolicy;
import com.tcdng.unify.core.filter.policy.EqualsPolicy;
import com.tcdng.unify.core.filter.policy.GreaterFieldPolicy;
import com.tcdng.unify.core.filter.policy.GreaterOrEqualFieldPolicy;
import com.tcdng.unify.core.filter.policy.GreaterOrEqualPolicy;
import com.tcdng.unify.core.filter.policy.GreaterPolicy;
import com.tcdng.unify.core.filter.policy.IsNotNullPolicy;
import com.tcdng.unify.core.filter.policy.IsNullPolicy;
import com.tcdng.unify.core.filter.policy.LessFieldPolicy;
import com.tcdng.unify.core.filter.policy.LessOrEqualFieldPolicy;
import com.tcdng.unify.core.filter.policy.LessOrEqualPolicy;
import com.tcdng.unify.core.filter.policy.LessPolicy;
import com.tcdng.unify.core.filter.policy.LikeFieldPolicy;
import com.tcdng.unify.core.filter.policy.LikePolicy;
import com.tcdng.unify.core.filter.policy.NotAmongstPolicy;
import com.tcdng.unify.core.filter.policy.NotBeginWithFieldPolicy;
import com.tcdng.unify.core.filter.policy.NotBeginWithPolicy;
import com.tcdng.unify.core.filter.policy.NotBetweenFieldPolicy;
import com.tcdng.unify.core.filter.policy.NotBetweenPolicy;
import com.tcdng.unify.core.filter.policy.NotEndWithFieldPolicy;
import com.tcdng.unify.core.filter.policy.NotEndWithPolicy;
import com.tcdng.unify.core.filter.policy.NotEqualsFieldPolicy;
import com.tcdng.unify.core.filter.policy.NotEqualsPolicy;
import com.tcdng.unify.core.filter.policy.NotLikeFieldPolicy;
import com.tcdng.unify.core.filter.policy.NotLikePolicy;
import com.tcdng.unify.core.filter.policy.OrPolicy;

/**
 * Bean filter policy utilites.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class BeanFilterPolicyUtils {

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
        policies.put(FilterConditionType.EQUALS_FIELD, new EqualsFieldPolicy());
        policies.put(FilterConditionType.NOT_EQUALS_FIELD, new NotEqualsFieldPolicy());
        policies.put(FilterConditionType.LESS_THAN_FIELD, new LessFieldPolicy());
        policies.put(FilterConditionType.LESS_OR_EQUAL_FIELD, new LessOrEqualFieldPolicy());
        policies.put(FilterConditionType.GREATER_THAN_FIELD, new GreaterFieldPolicy());
        policies.put(FilterConditionType.GREATER_OR_EQUAL_FIELD, new GreaterOrEqualFieldPolicy());
        policies.put(FilterConditionType.BETWEEN_FIELD, new BetweenFieldPolicy());
        policies.put(FilterConditionType.NOT_BETWEEN_FIELD, new NotBetweenFieldPolicy());
        policies.put(FilterConditionType.LIKE_FIELD, new LikeFieldPolicy());
        policies.put(FilterConditionType.NOT_LIKE_FIELD, new NotLikeFieldPolicy());
        policies.put(FilterConditionType.BEGINS_WITH_FIELD, new BeginsWithFieldPolicy());
        policies.put(FilterConditionType.NOT_BEGIN_WITH_FIELD, new NotBeginWithFieldPolicy());
        policies.put(FilterConditionType.ENDS_WITH_FIELD, new EndsWithFieldPolicy());
        policies.put(FilterConditionType.NOT_END_WITH_FIELD, new NotEndWithFieldPolicy());
        policies.put(FilterConditionType.AND, new AndPolicy());
        policies.put(FilterConditionType.OR, new OrPolicy());
        filterPolicies = Collections.unmodifiableMap(policies);
    }

    private BeanFilterPolicyUtils() {
        
    }
    
    public static BeanFilterPolicy getBeanFilterPolicy(FilterConditionType type) {
        return filterPolicies.get(type);
    }
}
