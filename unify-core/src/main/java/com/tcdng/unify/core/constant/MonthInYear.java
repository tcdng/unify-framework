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
package com.tcdng.unify.core.constant;

import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.annotation.Tooling;
import com.tcdng.unify.core.util.EnumUtils;

@Tooling("Month in Year List")
@StaticList("staticmonthinyearlist")
public enum MonthInYear implements EnumConst {

	JANUARY("Jan"), FEBRUARY("Feb"), MARCH("Mar"), APRIL("Apr"), MAY("May"), JUNE("Jun"), JULY("Jul"), AUGUST(
			"Aug"), SEPTEMBER("Sep"), OCTOBER("Oct"), NOVEMBER("Nov"), DECEMBER("Dec");

	private static final MonthInYear[] BY_CALENDAR_INDEX = { JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST,
			SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER };

	private final String code;

	private MonthInYear(String code) {
		this.code = code;
	}

	@Override
	public String code() {
		return this.code;
	}

	public static MonthInYear byJavaCalendarIndex(int index) {
		return BY_CALENDAR_INDEX[index - 1];
	}

	public static MonthInYear fromCode(String code) {
		return EnumUtils.fromCode(MonthInYear.class, code);
	}

	public static MonthInYear fromName(String name) {
		return EnumUtils.fromName(MonthInYear.class, name);
	}
}
