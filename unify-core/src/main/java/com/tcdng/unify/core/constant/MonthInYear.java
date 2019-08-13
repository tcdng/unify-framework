/*
 * Copyright 2018-2019 The Code Department.
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

@Tooling(description = "Month in Year")
@StaticList("staticmonthinyearlist")
public enum MonthInYear implements EnumConst {

    JANUARY("Jan", 0),
    FEBRUARY("Feb", 1),
    MARCH("Mar", 2),
    APRIL("Apr", 3),
    MAY("May", 4),
    JUNE("Jun", 5),
    JULY("Jul", 6),
    AUGUST("Aug", 7),
    SEPTEMBER("Sep", 8),
    OCTOBER("Oct", 9),
    NOVEMBER("Nov", 10),
    DECEMBER("Dec", 11);

    private static final MonthInYear[] BY_CALENDAR_INDEX =
            { JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER };

    private final String code;

    private final int javaIndex;

    private MonthInYear(String code, int javaIndex) {
        this.code = code;
        this.javaIndex = javaIndex;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultCode() {
        return JANUARY.code;
    }

    public int javaCalendarIndex() {
        return javaIndex;
    }

    public static int[] getJavaCalendarIndexes(String[] monthsInYear) {
        int[] indexes = null;
        if (monthsInYear != null) {
            indexes = new int[monthsInYear.length];
            for (int i = 0; i < monthsInYear.length; i++) {
                indexes[i] = MonthInYear.fromCode(monthsInYear[i]).javaIndex;
            }
        }

        return indexes;
    }

    public static MonthInYear byJavaCalendarIndex(int index) {
        return BY_CALENDAR_INDEX[index];
    }

    public static MonthInYear fromCode(String code) {
        return EnumUtils.fromCode(MonthInYear.class, code);
    }

    public static MonthInYear fromName(String name) {
        return EnumUtils.fromName(MonthInYear.class, name);
    }
}
