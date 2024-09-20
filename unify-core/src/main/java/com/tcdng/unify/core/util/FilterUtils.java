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

import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.criterion.FilterConditionListType;
import com.tcdng.unify.core.criterion.FilterConditionType;
import com.tcdng.unify.core.filter.ObjectFilterPolicy;
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
import com.tcdng.unify.core.filter.policy.IBeginsWithPolicy;
import com.tcdng.unify.core.filter.policy.IEndsWithPolicy;
import com.tcdng.unify.core.filter.policy.IEqualsPolicy;
import com.tcdng.unify.core.filter.policy.ILikePolicy;
import com.tcdng.unify.core.filter.policy.INotEqualsPolicy;
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
 * @author The Code Department
 * @since 1.0
 */
public final class FilterUtils {

	private static final Map<FilterConditionListType, Map<Class<?>, String>> classToFilterConditionSelectorMap;

	static {
		Map<FilterConditionListType, Map<Class<?>, String>> mapAll = new EnumMap<FilterConditionListType, Map<Class<?>, String>>(
				FilterConditionListType.class);

		Map<Class<?>, String> map = new HashMap<Class<?>, String>();
		map.put(boolean.class, "booleanconditionlist");
		map.put(Boolean.class, "booleanconditionlist");
		map.put(char.class, "stringconditionlist");
		map.put(Character.class, "stringconditionlist");
		map.put(short.class, "numberconditionlist");
		map.put(Short.class, "numberconditionlist");
		map.put(int.class, "numberconditionlist");
		map.put(Integer.class, "numberconditionlist");
		map.put(long.class, "numberconditionlist");
		map.put(Long.class, "numberconditionlist");
		map.put(float.class, "numberconditionlist");
		map.put(Float.class, "numberconditionlist");
		map.put(double.class, "numberconditionlist");
		map.put(Double.class, "numberconditionlist");
		map.put(BigDecimal.class, "numberconditionlist");
		map.put(Date.class, "dateconditionlist");
		map.put(String.class, "stringconditionlist");
		mapAll.put(FilterConditionListType.IMMEDIATE_ONLY, Collections.unmodifiableMap(map));

		map = new HashMap<Class<?>, String>();
		map.put(boolean.class, "booleanfieldconditionlist");
		map.put(Boolean.class, "booleanfieldconditionlist");
		map.put(char.class, "stringfieldconditionlist");
		map.put(Character.class, "stringfieldconditionlist");
		map.put(short.class, "numberfieldconditionlist");
		map.put(Short.class, "numberfieldconditionlist");
		map.put(int.class, "numberfieldconditionlist");
		map.put(Integer.class, "numberfieldconditionlist");
		map.put(long.class, "numberfieldconditionlist");
		map.put(Long.class, "numberfieldconditionlist");
		map.put(float.class, "numberfieldconditionlist");
		map.put(Float.class, "numberfieldconditionlist");
		map.put(double.class, "numberfieldconditionlist");
		map.put(Double.class, "numberfieldconditionlist");
		map.put(BigDecimal.class, "numberfieldconditionlist");
		map.put(Date.class, "datefieldconditionlist");
		map.put(String.class, "stringfieldconditionlist");
		mapAll.put(FilterConditionListType.IMMEDIATE_FIELD, Collections.unmodifiableMap(map));

		map = new HashMap<Class<?>, String>();
		map.put(boolean.class, "booleanparamconditionlist");
		map.put(Boolean.class, "booleanparamconditionlist");
		map.put(char.class, "stringparamconditionlist");
		map.put(Character.class, "stringparamconditionlist");
		map.put(short.class, "numberparamconditionlist");
		map.put(Short.class, "numberparamconditionlist");
		map.put(int.class, "numberparamconditionlist");
		map.put(Integer.class, "numberparamconditionlist");
		map.put(long.class, "numberparamconditionlist");
		map.put(Long.class, "numberparamconditionlist");
		map.put(float.class, "numberparamconditionlist");
		map.put(Float.class, "numberparamconditionlist");
		map.put(double.class, "numberparamconditionlist");
		map.put(Double.class, "numberparamconditionlist");
		map.put(BigDecimal.class, "numberparamconditionlist");
		map.put(Date.class, "dateparamconditionlist");
		map.put(String.class, "stringparamconditionlist");
		mapAll.put(FilterConditionListType.IMMEDIATE_PARAM, Collections.unmodifiableMap(map));

		map = new HashMap<Class<?>, String>();
		map.put(String.class, "enumconditionlist");
		mapAll.put(FilterConditionListType.IMMEDIATE_ENUM_ONLY, Collections.unmodifiableMap(map));

		map = new HashMap<Class<?>, String>();
		map.put(String.class, "enumfieldconditionlist");
		mapAll.put(FilterConditionListType.IMMEDIATE_ENUM_FIELD, Collections.unmodifiableMap(map));

		map = new HashMap<Class<?>, String>();
		map.put(String.class, "enumparamconditionlist");
		mapAll.put(FilterConditionListType.IMMEDIATE_ENUM_PARAM, Collections.unmodifiableMap(map));

		classToFilterConditionSelectorMap = Collections.unmodifiableMap(mapAll);
	}

	private static final Set<FilterConditionType> booleanConditionTypes_imm = Collections
			.unmodifiableSet(new LinkedHashSet<FilterConditionType>(Arrays.asList(FilterConditionType.EQUALS,
					FilterConditionType.NOT_EQUALS, FilterConditionType.IS_NULL, FilterConditionType.IS_NOT_NULL)));

	private static final Set<FilterConditionType> booleanConditionTypes_field = Collections
			.unmodifiableSet(new LinkedHashSet<FilterConditionType>(Arrays.asList(FilterConditionType.EQUALS,
					FilterConditionType.NOT_EQUALS, FilterConditionType.IS_NULL, FilterConditionType.IS_NOT_NULL,
					FilterConditionType.EQUALS_FIELD, FilterConditionType.NOT_EQUALS_FIELD)));

	private static final Set<FilterConditionType> booleanConditionTypes_param = Collections
			.unmodifiableSet(new LinkedHashSet<FilterConditionType>(Arrays.asList(FilterConditionType.EQUALS,
					FilterConditionType.NOT_EQUALS, FilterConditionType.IS_NULL, FilterConditionType.IS_NOT_NULL,
					FilterConditionType.EQUALS_PARAM, FilterConditionType.NOT_EQUALS_PARAM)));

	private static final Set<FilterConditionType> numberConditionTypes_imm = Collections
			.unmodifiableSet(new LinkedHashSet<FilterConditionType>(Arrays.asList(FilterConditionType.EQUALS,
					FilterConditionType.GREATER_THAN, FilterConditionType.GREATER_OR_EQUAL,
					FilterConditionType.LESS_THAN, FilterConditionType.LESS_OR_EQUAL, FilterConditionType.BETWEEN,
					FilterConditionType.AMONGST, FilterConditionType.NOT_EQUALS, FilterConditionType.NOT_BETWEEN,
					FilterConditionType.NOT_AMONGST, FilterConditionType.IS_NULL, FilterConditionType.IS_NOT_NULL)));

	private static final Set<FilterConditionType> numberConditionTypes_field = Collections
			.unmodifiableSet(new LinkedHashSet<FilterConditionType>(Arrays.asList(FilterConditionType.EQUALS,
					FilterConditionType.GREATER_THAN, FilterConditionType.GREATER_OR_EQUAL,
					FilterConditionType.LESS_THAN, FilterConditionType.LESS_OR_EQUAL, FilterConditionType.BETWEEN,
					FilterConditionType.AMONGST, FilterConditionType.NOT_EQUALS, FilterConditionType.NOT_BETWEEN,
					FilterConditionType.NOT_AMONGST, FilterConditionType.IS_NULL, FilterConditionType.IS_NOT_NULL,
					FilterConditionType.EQUALS_FIELD, FilterConditionType.GREATER_THAN_FIELD,
					FilterConditionType.GREATER_OR_EQUAL_FIELD, FilterConditionType.LESS_THAN_FIELD,
					FilterConditionType.LESS_OR_EQUAL_FIELD, FilterConditionType.BETWEEN_FIELD,
					FilterConditionType.NOT_EQUALS_FIELD, FilterConditionType.NOT_BETWEEN_FIELD)));

	private static final Set<FilterConditionType> numberConditionTypes_param = Collections
			.unmodifiableSet(new LinkedHashSet<FilterConditionType>(Arrays.asList(FilterConditionType.EQUALS,
					FilterConditionType.GREATER_THAN, FilterConditionType.GREATER_OR_EQUAL,
					FilterConditionType.LESS_THAN, FilterConditionType.LESS_OR_EQUAL, FilterConditionType.BETWEEN,
					FilterConditionType.AMONGST, FilterConditionType.NOT_EQUALS, FilterConditionType.NOT_BETWEEN,
					FilterConditionType.NOT_AMONGST, FilterConditionType.IS_NULL, FilterConditionType.IS_NOT_NULL,
					FilterConditionType.EQUALS_PARAM, FilterConditionType.GREATER_THAN_PARAM,
					FilterConditionType.GREATER_OR_EQUAL_PARAM, FilterConditionType.LESS_THAN_PARAM,
					FilterConditionType.LESS_OR_EQUAL_PARAM, FilterConditionType.BETWEEN_PARAM,
					FilterConditionType.NOT_EQUALS_PARAM, FilterConditionType.NOT_BETWEEN_PARAM)));

	private static final Set<FilterConditionType> dateConditionTypes_imm = Collections
			.unmodifiableSet(new LinkedHashSet<FilterConditionType>(Arrays.asList(FilterConditionType.EQUALS_LINGUAL,
					FilterConditionType.GREATER_THAN_LINGUAL, FilterConditionType.GREATER_OR_EQUAL_LINGUAL,
					FilterConditionType.LESS_THAN_LINGUAL, FilterConditionType.LESS_OR_EQUAL_LINGUAL,
					FilterConditionType.EQUALS, FilterConditionType.GREATER_THAN, FilterConditionType.GREATER_OR_EQUAL,
					FilterConditionType.LESS_THAN, FilterConditionType.LESS_OR_EQUAL, FilterConditionType.BETWEEN,
					FilterConditionType.BETWEEN_LINGUAL, FilterConditionType.NOT_EQUALS,
					FilterConditionType.NOT_BETWEEN, FilterConditionType.NOT_BETWEEN_LINGUAL,
					FilterConditionType.IS_NULL, FilterConditionType.IS_NOT_NULL)));

	private static final Set<FilterConditionType> dateConditionTypes_field = Collections
			.unmodifiableSet(new LinkedHashSet<FilterConditionType>(Arrays.asList(FilterConditionType.EQUALS_LINGUAL,
					FilterConditionType.GREATER_THAN_LINGUAL, FilterConditionType.GREATER_OR_EQUAL_LINGUAL,
					FilterConditionType.LESS_THAN_LINGUAL, FilterConditionType.LESS_OR_EQUAL_LINGUAL,
					FilterConditionType.EQUALS, FilterConditionType.GREATER_THAN, FilterConditionType.GREATER_OR_EQUAL,
					FilterConditionType.LESS_THAN, FilterConditionType.LESS_OR_EQUAL, FilterConditionType.BETWEEN,
					FilterConditionType.BETWEEN_LINGUAL, FilterConditionType.NOT_EQUALS,
					FilterConditionType.NOT_BETWEEN, FilterConditionType.NOT_BETWEEN_LINGUAL,
					FilterConditionType.IS_NULL, FilterConditionType.IS_NOT_NULL, FilterConditionType.EQUALS_FIELD,
					FilterConditionType.GREATER_THAN_FIELD, FilterConditionType.GREATER_OR_EQUAL_FIELD,
					FilterConditionType.LESS_THAN_FIELD, FilterConditionType.LESS_OR_EQUAL_FIELD,
					FilterConditionType.BETWEEN_FIELD, FilterConditionType.NOT_EQUALS_FIELD,
					FilterConditionType.NOT_BETWEEN_FIELD)));

	private static final Set<FilterConditionType> dateConditionTypes_param = Collections
			.unmodifiableSet(new LinkedHashSet<FilterConditionType>(Arrays.asList(FilterConditionType.EQUALS_LINGUAL,
					FilterConditionType.GREATER_THAN_LINGUAL, FilterConditionType.GREATER_OR_EQUAL_LINGUAL,
					FilterConditionType.LESS_THAN_LINGUAL, FilterConditionType.LESS_OR_EQUAL_LINGUAL,
					FilterConditionType.EQUALS, FilterConditionType.GREATER_THAN, FilterConditionType.GREATER_OR_EQUAL,
					FilterConditionType.LESS_THAN, FilterConditionType.LESS_OR_EQUAL, FilterConditionType.BETWEEN,
					FilterConditionType.BETWEEN_LINGUAL, FilterConditionType.NOT_EQUALS,
					FilterConditionType.NOT_BETWEEN, FilterConditionType.NOT_BETWEEN_LINGUAL,
					FilterConditionType.IS_NULL, FilterConditionType.IS_NOT_NULL, FilterConditionType.EQUALS_PARAM,
					FilterConditionType.GREATER_THAN_PARAM, FilterConditionType.GREATER_OR_EQUAL_PARAM,
					FilterConditionType.LESS_THAN_PARAM, FilterConditionType.LESS_OR_EQUAL_PARAM,
					FilterConditionType.BETWEEN_PARAM, FilterConditionType.NOT_EQUALS_PARAM,
					FilterConditionType.NOT_BETWEEN_PARAM)));

	private static final Set<FilterConditionType> stringConditionTypes_imm = Collections
			.unmodifiableSet(new LinkedHashSet<FilterConditionType>(Arrays.asList(FilterConditionType.EQUALS_LINGUAL,
					FilterConditionType.NOT_EQUALS_LINGUAL, FilterConditionType.EQUALS, FilterConditionType.IEQUALS,
					FilterConditionType.BEGINS_WITH, FilterConditionType.IBEGINS_WITH, FilterConditionType.ENDS_WITH,
					FilterConditionType.IENDS_WITH, FilterConditionType.LIKE, FilterConditionType.ILIKE,
					FilterConditionType.BETWEEN, FilterConditionType.AMONGST, FilterConditionType.NOT_EQUALS,
					FilterConditionType.INOT_EQUALS, FilterConditionType.NOT_BEGIN_WITH,
					FilterConditionType.NOT_END_WITH, FilterConditionType.NOT_LIKE, FilterConditionType.NOT_BETWEEN,
					FilterConditionType.NOT_AMONGST, FilterConditionType.IS_NULL, FilterConditionType.IS_NOT_NULL)));

	private static final Set<FilterConditionType> enumConditionTypes_imm = Collections
			.unmodifiableSet(new LinkedHashSet<FilterConditionType>(Arrays.asList(FilterConditionType.EQUALS,
					FilterConditionType.AMONGST, FilterConditionType.NOT_EQUALS,
					FilterConditionType.NOT_AMONGST, FilterConditionType.IS_NULL, FilterConditionType.IS_NOT_NULL)));

	private static final Set<FilterConditionType> stringConditionTypes_field = Collections
			.unmodifiableSet(new LinkedHashSet<FilterConditionType>(Arrays.asList(FilterConditionType.EQUALS_LINGUAL,
					FilterConditionType.NOT_EQUALS_LINGUAL, FilterConditionType.EQUALS, FilterConditionType.IEQUALS,
					FilterConditionType.BEGINS_WITH, FilterConditionType.IBEGINS_WITH, FilterConditionType.ENDS_WITH,
					FilterConditionType.IENDS_WITH, FilterConditionType.LIKE, FilterConditionType.ILIKE,
					FilterConditionType.BETWEEN, FilterConditionType.AMONGST, FilterConditionType.NOT_EQUALS,
					FilterConditionType.INOT_EQUALS, FilterConditionType.NOT_BEGIN_WITH,
					FilterConditionType.NOT_END_WITH, FilterConditionType.NOT_LIKE, FilterConditionType.NOT_BETWEEN,
					FilterConditionType.NOT_AMONGST, FilterConditionType.IS_NULL, FilterConditionType.IS_NOT_NULL,
					FilterConditionType.EQUALS_FIELD, FilterConditionType.BEGINS_WITH_FIELD,
					FilterConditionType.ENDS_WITH_FIELD, FilterConditionType.LIKE_FIELD,
					FilterConditionType.BETWEEN_FIELD, FilterConditionType.NOT_EQUALS_FIELD,
					FilterConditionType.NOT_BEGIN_WITH_FIELD, FilterConditionType.NOT_END_WITH_FIELD,
					FilterConditionType.NOT_LIKE_FIELD, FilterConditionType.NOT_BETWEEN_FIELD,
					FilterConditionType.EQUALS_SESSIONPARAM, FilterConditionType.NOT_EQUALS_SESSIONPARAM)));

	private static final Set<FilterConditionType> enumConditionTypes_field = Collections
			.unmodifiableSet(new LinkedHashSet<FilterConditionType>(Arrays.asList(
					FilterConditionType.EQUALS, FilterConditionType.AMONGST, FilterConditionType.NOT_EQUALS,
					FilterConditionType.NOT_AMONGST, FilterConditionType.IS_NULL, FilterConditionType.IS_NOT_NULL,
					FilterConditionType.EQUALS_FIELD, FilterConditionType.NOT_EQUALS_FIELD,
					FilterConditionType.EQUALS_SESSIONPARAM, FilterConditionType.NOT_EQUALS_SESSIONPARAM)));

	private static final Set<FilterConditionType> stringConditionTypes_param = Collections
			.unmodifiableSet(new LinkedHashSet<FilterConditionType>(Arrays.asList(FilterConditionType.EQUALS,
					FilterConditionType.IEQUALS, FilterConditionType.BEGINS_WITH, FilterConditionType.IBEGINS_WITH,
					FilterConditionType.ENDS_WITH, FilterConditionType.IENDS_WITH, FilterConditionType.LIKE,
					FilterConditionType.ILIKE, FilterConditionType.BETWEEN, FilterConditionType.AMONGST,
					FilterConditionType.NOT_EQUALS, FilterConditionType.INOT_EQUALS, FilterConditionType.NOT_BEGIN_WITH,
					FilterConditionType.NOT_END_WITH, FilterConditionType.NOT_LIKE, FilterConditionType.NOT_BETWEEN,
					FilterConditionType.NOT_AMONGST, FilterConditionType.IS_NULL, FilterConditionType.IS_NOT_NULL,
					FilterConditionType.EQUALS_PARAM, FilterConditionType.BEGINS_WITH_PARAM,
					FilterConditionType.ENDS_WITH_PARAM, FilterConditionType.LIKE_PARAM,
					FilterConditionType.BETWEEN_PARAM, FilterConditionType.NOT_EQUALS_PARAM,
					FilterConditionType.NOT_BEGIN_WITH_PARAM, FilterConditionType.NOT_END_WITH_PARAM,
					FilterConditionType.NOT_LIKE_PARAM, FilterConditionType.NOT_BETWEEN_PARAM)));

	private static final Set<FilterConditionType> enumConditionTypes_param = Collections
			.unmodifiableSet(new LinkedHashSet<FilterConditionType>(Arrays.asList(FilterConditionType.EQUALS,
					FilterConditionType.AMONGST,
					FilterConditionType.NOT_EQUALS,
					FilterConditionType.NOT_AMONGST, FilterConditionType.IS_NULL, FilterConditionType.IS_NOT_NULL,
					FilterConditionType.EQUALS_PARAM, FilterConditionType.NOT_EQUALS_PARAM)));

	private static final Set<FilterConditionType> enumConstConditionTypes = Collections
			.unmodifiableSet(new LinkedHashSet<FilterConditionType>(Arrays.asList(FilterConditionType.EQUALS,
					FilterConditionType.NOT_EQUALS, FilterConditionType.IS_NULL, FilterConditionType.IS_NOT_NULL)));

	private static final Set<FilterConditionType> collectionConditionTypes = Collections
			.unmodifiableSet(new LinkedHashSet<FilterConditionType>(Arrays.asList(FilterConditionType.EQUALS_COLLECTION,
					FilterConditionType.NOT_EQUALS_COLLECTION, FilterConditionType.GREATER_THAN_COLLECTION,
					FilterConditionType.GREATER_OR_EQUAL_COLLECTION, FilterConditionType.LESS_THAN_COLLECTION,
					FilterConditionType.LESS_OR_EQUAL_COLLECTION, FilterConditionType.IS_NULL,
					FilterConditionType.IS_NOT_NULL)));

	private static final Set<FilterConditionType> objectConditionTypes = Collections
			.unmodifiableSet(new LinkedHashSet<FilterConditionType>(
					Arrays.asList(FilterConditionType.IS_NULL, FilterConditionType.IS_NOT_NULL)));

	private static final Map<FilterConditionListType, Map<Class<?>, Set<FilterConditionType>>> supportedConditionMap;

	static {
		Map<FilterConditionListType, Map<Class<?>, Set<FilterConditionType>>> mapAll = new EnumMap<FilterConditionListType, Map<Class<?>, Set<FilterConditionType>>>(
				FilterConditionListType.class);

		Map<Class<?>, Set<FilterConditionType>> map = new HashMap<Class<?>, Set<FilterConditionType>>();
		map.put(boolean.class, booleanConditionTypes_imm);
		map.put(Boolean.class, booleanConditionTypes_imm);
		map.put(char.class, stringConditionTypes_imm);
		map.put(Character.class, stringConditionTypes_imm);
		map.put(short.class, numberConditionTypes_imm);
		map.put(Short.class, numberConditionTypes_imm);
		map.put(int.class, numberConditionTypes_imm);
		map.put(Integer.class, numberConditionTypes_imm);
		map.put(long.class, numberConditionTypes_imm);
		map.put(Long.class, numberConditionTypes_imm);
		map.put(float.class, numberConditionTypes_imm);
		map.put(Float.class, numberConditionTypes_imm);
		map.put(double.class, numberConditionTypes_imm);
		map.put(Double.class, numberConditionTypes_imm);
		map.put(BigDecimal.class, numberConditionTypes_imm);
		map.put(Date.class, dateConditionTypes_imm);
		map.put(String.class, stringConditionTypes_imm);
		mapAll.put(FilterConditionListType.IMMEDIATE_ONLY, Collections.unmodifiableMap(map));

		map = new HashMap<Class<?>, Set<FilterConditionType>>();
		map.put(boolean.class, booleanConditionTypes_field);
		map.put(Boolean.class, booleanConditionTypes_field);
		map.put(char.class, stringConditionTypes_field);
		map.put(Character.class, stringConditionTypes_field);
		map.put(short.class, numberConditionTypes_field);
		map.put(Short.class, numberConditionTypes_field);
		map.put(int.class, numberConditionTypes_field);
		map.put(Integer.class, numberConditionTypes_field);
		map.put(long.class, numberConditionTypes_field);
		map.put(Long.class, numberConditionTypes_field);
		map.put(float.class, numberConditionTypes_field);
		map.put(Float.class, numberConditionTypes_field);
		map.put(double.class, numberConditionTypes_field);
		map.put(Double.class, numberConditionTypes_field);
		map.put(BigDecimal.class, numberConditionTypes_field);
		map.put(Date.class, dateConditionTypes_field);
		map.put(String.class, stringConditionTypes_field);
		mapAll.put(FilterConditionListType.IMMEDIATE_FIELD, Collections.unmodifiableMap(map));

		map = new HashMap<Class<?>, Set<FilterConditionType>>();
		map.put(boolean.class, booleanConditionTypes_param);
		map.put(Boolean.class, booleanConditionTypes_param);
		map.put(char.class, stringConditionTypes_param);
		map.put(Character.class, stringConditionTypes_param);
		map.put(short.class, numberConditionTypes_param);
		map.put(Short.class, numberConditionTypes_param);
		map.put(int.class, numberConditionTypes_param);
		map.put(Integer.class, numberConditionTypes_param);
		map.put(long.class, numberConditionTypes_param);
		map.put(Long.class, numberConditionTypes_param);
		map.put(float.class, numberConditionTypes_param);
		map.put(Float.class, numberConditionTypes_param);
		map.put(double.class, numberConditionTypes_param);
		map.put(Double.class, numberConditionTypes_param);
		map.put(BigDecimal.class, numberConditionTypes_param);
		map.put(Date.class, dateConditionTypes_param);
		map.put(String.class, stringConditionTypes_param);
		mapAll.put(FilterConditionListType.IMMEDIATE_PARAM, Collections.unmodifiableMap(map));

		map = new HashMap<Class<?>, Set<FilterConditionType>>();
		map.put(String.class, enumConditionTypes_imm);
		mapAll.put(FilterConditionListType.IMMEDIATE_ENUM_ONLY, Collections.unmodifiableMap(map));

		map = new HashMap<Class<?>, Set<FilterConditionType>>();
		map.put(String.class, enumConditionTypes_field);
		mapAll.put(FilterConditionListType.IMMEDIATE_ENUM_FIELD, Collections.unmodifiableMap(map));

		map = new HashMap<Class<?>, Set<FilterConditionType>>();
		map.put(String.class, enumConditionTypes_param);
		mapAll.put(FilterConditionListType.IMMEDIATE_ENUM_PARAM, Collections.unmodifiableMap(map));

		supportedConditionMap = Collections.unmodifiableMap(mapAll);
	}

	private static final Map<FilterConditionType, ObjectFilterPolicy> filterPolicies;

	static {
		Map<FilterConditionType, ObjectFilterPolicy> policies = new EnumMap<FilterConditionType, ObjectFilterPolicy>(
				FilterConditionType.class);
		policies.put(FilterConditionType.EQUALS, new EqualsPolicy());
		policies.put(FilterConditionType.IEQUALS, new IEqualsPolicy());
		policies.put(FilterConditionType.NOT_EQUALS, new NotEqualsPolicy());
		policies.put(FilterConditionType.INOT_EQUALS, new INotEqualsPolicy());
		policies.put(FilterConditionType.LESS_THAN, new LessPolicy());
		policies.put(FilterConditionType.LESS_OR_EQUAL, new LessOrEqualPolicy());
		policies.put(FilterConditionType.GREATER_THAN, new GreaterPolicy());
		policies.put(FilterConditionType.GREATER_OR_EQUAL, new GreaterOrEqualPolicy());
		policies.put(FilterConditionType.BETWEEN, new BetweenPolicy());
		policies.put(FilterConditionType.NOT_BETWEEN, new NotBetweenPolicy());
		policies.put(FilterConditionType.AMONGST, new AmongstPolicy());
		policies.put(FilterConditionType.NOT_AMONGST, new NotAmongstPolicy());
		policies.put(FilterConditionType.LIKE, new LikePolicy());
		policies.put(FilterConditionType.ILIKE, new ILikePolicy());
		policies.put(FilterConditionType.NOT_LIKE, new NotLikePolicy());
		policies.put(FilterConditionType.BEGINS_WITH, new BeginsWithPolicy());
		policies.put(FilterConditionType.IBEGINS_WITH, new IBeginsWithPolicy());
		policies.put(FilterConditionType.NOT_BEGIN_WITH, new NotBeginWithPolicy());
		policies.put(FilterConditionType.ENDS_WITH, new EndsWithPolicy());
		policies.put(FilterConditionType.IENDS_WITH, new IEndsWithPolicy());
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

		policies.put(FilterConditionType.EQUALS_PARAM, new EqualsPolicy());
		policies.put(FilterConditionType.NOT_EQUALS_PARAM, new NotEqualsPolicy());
		policies.put(FilterConditionType.LESS_THAN_PARAM, new LessPolicy());
		policies.put(FilterConditionType.LESS_OR_EQUAL_PARAM, new LessOrEqualPolicy());
		policies.put(FilterConditionType.GREATER_THAN_PARAM, new GreaterPolicy());
		policies.put(FilterConditionType.GREATER_OR_EQUAL_PARAM, new GreaterOrEqualPolicy());
		policies.put(FilterConditionType.BETWEEN_PARAM, new BetweenPolicy());
		policies.put(FilterConditionType.NOT_BETWEEN_PARAM, new NotBetweenPolicy());
		policies.put(FilterConditionType.LIKE_PARAM, new LikePolicy());
		policies.put(FilterConditionType.NOT_LIKE_PARAM, new NotLikePolicy());
		policies.put(FilterConditionType.BEGINS_WITH_PARAM, new BeginsWithPolicy());
		policies.put(FilterConditionType.NOT_BEGIN_WITH_PARAM, new NotBeginWithPolicy());
		policies.put(FilterConditionType.ENDS_WITH_PARAM, new EndsWithPolicy());
		policies.put(FilterConditionType.NOT_END_WITH_PARAM, new NotEndWithPolicy());

		policies.put(FilterConditionType.EQUALS_SESSIONPARAM, new EqualsPolicy());
		policies.put(FilterConditionType.NOT_EQUALS_SESSIONPARAM, new NotEqualsPolicy());

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

	public static FilterConditionListType getEnumFilterConditionListType(FilterConditionListType listType) {
		if (listType != null) {
			switch(listType) {
			case IMMEDIATE_ENUM_FIELD:
				break;
			case IMMEDIATE_ENUM_ONLY:
				break;
			case IMMEDIATE_ENUM_PARAM:
				break;
			case IMMEDIATE_FIELD:
				return FilterConditionListType.IMMEDIATE_ENUM_FIELD;
			case IMMEDIATE_ONLY:
				return FilterConditionListType.IMMEDIATE_ENUM_ONLY;
			case IMMEDIATE_PARAM:
				return FilterConditionListType.IMMEDIATE_ENUM_PARAM;
			default:
				break;		
			}
		}
		
		return listType;
	}
	
	public static String getFilterConditionTypeListCommand(Class<?> fieldType, FilterConditionListType listType) {
		String descriptor = classToFilterConditionSelectorMap.get(listType).get(fieldType);
		if (descriptor != null) {
			return descriptor;
		}

		if (EnumConst.class.isAssignableFrom(fieldType)) {
			return "enumconstconditionlist";
		}

		if (fieldType.isArray() || Collection.class.isAssignableFrom(fieldType)) {
			return "collectionconditionlist";
		}

		return "objectconditionlist";
	}

	public static Set<FilterConditionType> getSupportedFilterConditionTypes(Class<?> fieldType,
			FilterConditionListType listType) {
		Set<FilterConditionType> supported = supportedConditionMap.get(listType).get(fieldType);
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

	public static boolean isFilterConditionSupportedForType(Class<?> fieldType, FilterConditionType type,
			FilterConditionListType listType) {
		Set<FilterConditionType> supported = FilterUtils.getSupportedFilterConditionTypes(fieldType, listType);
		return supported.contains(type);
	}

	public static ObjectFilterPolicy getBeanFilterPolicy(FilterConditionType type) {
		return filterPolicies.get(type);
	}
}
