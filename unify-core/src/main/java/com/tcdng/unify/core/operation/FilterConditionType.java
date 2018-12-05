/*
 * Copyright 2014 The Code Department
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

	EQUALS("EQ", Operator.EQUAL), NOT_EQUAL("NEQ", Operator.NOT_EQUAL), GREATER("GT",
			Operator.GREATER), GREATER_OR_EQUAL("GTE", Operator.GREATER_OR_EQUAL), LESS("LT",
					Operator.LESS_THAN), LESS_OR_EQUAL("LTE", Operator.LESS_OR_EQUAL), BETWEEN("BT",
							Operator.BETWEEN), NOT_BETWEEN("NBT", Operator.NOT_BETWEEN), LIKE_BEGIN("BW",
									Operator.LIKE_BEGIN), LIKE_END("EW", Operator.LIKE_END), LIKE("LK",
											Operator.LIKE), NOT_LIKE_BEGIN("NBW",
													Operator.NOT_LIKE_BEGIN), NOT_LIKE_END("NEW",
															Operator.NOT_LIKE_END), NOT_LIKE("NLK",
																	Operator.NOT_LIKE), IS_NULL("IN",
																			Operator.IS_NULL), IS_NOT_NULL("NIN",
																					Operator.IS_NOT_NULL);

	private final String code;

	private final Operator operator;

	private FilterConditionType(String code, Operator operator) {
		this.code = code;
		this.operator = operator;
	}

	@Override
	public String code() {
		return code;
	}

	public Operator operator() {
		return operator;
	}

	public boolean isRange() {
		return BETWEEN.equals(this) || NOT_BETWEEN.equals(this);
	}

	public boolean isZeroParams() {
		return IS_NULL.equals(this) || IS_NOT_NULL.equals(this);
	}

	public static FilterConditionType fromCode(String code) {
		return EnumUtils.fromCode(FilterConditionType.class, code);
	}

	public static FilterConditionType fromName(String name) {
		return EnumUtils.fromName(FilterConditionType.class, name);
	}
}
