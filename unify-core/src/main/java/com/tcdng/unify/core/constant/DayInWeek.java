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
package com.tcdng.unify.core.constant;

import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.annotation.Tooling;
import com.tcdng.unify.core.util.EnumUtils;

@Tooling(description = "Day in Week")
@StaticList("staticdayinweeklist")
public enum DayInWeek implements EnumConst {

    SUNDAY("Sun", 1),
    MONDAY("Mon", 2),
    TUESDAY("Tue", 3),
    WEDNESDAY("Wed", 4),
    THURSDAY("Thu", 5),
    FRIDAY("Fri", 6),
    SATURDAY("Sat", 7);

    private static final DayInWeek[] BY_CALENDAR_INDEX =
            { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY };

    private final String code;

    private final int javaIndex;

    private DayInWeek(String code, int javaIndex) {
        this.code = code;
        this.javaIndex = javaIndex;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultCode() {
        return SUNDAY.code;
    }

    public int javaCalendarIndex() {
        return javaIndex;
    }

    public static int[] getJavaCalendarIndexes(String[] daysInWeek) {
        int[] indexes = null;
        if (daysInWeek != null) {
            indexes = new int[daysInWeek.length];
            for (int i = 0; i < daysInWeek.length; i++) {
                indexes[i] = DayInWeek.fromCode(daysInWeek[i]).javaIndex;
            }
        }

        return indexes;
    }

    public static DayInWeek byJavaCalendarIndex(int index) {
        return BY_CALENDAR_INDEX[index - 1];
    }

    public static DayInWeek fromCode(String code) {
        return EnumUtils.fromCode(DayInWeek.class, code);
    }

    public static DayInWeek fromName(String name) {
        return EnumUtils.fromName(DayInWeek.class, name);
    }
}
