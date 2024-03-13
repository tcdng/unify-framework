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
package com.tcdng.unify.core.constant;

import com.tcdng.unify.common.annotation.StaticList;
import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Time series type constants.
 * 
 * @author The Code Department
 * @version 1.0
 */
@StaticList(name = "timeseriestypelist", description="$m{staticlist.timeseriestypelist}")
public enum TimeSeriesType implements EnumConst {

    HOUR("HR"),
    DAY("DY"),
    DAY_OF_WEEK("DW"),
    DAY_OF_MONTH("DM"),
    DAY_OF_YEAR("DR"),
    WEEK("WK"),
    MONTH("MN"),
    YEAR("YR");

    private final String code;

    private TimeSeriesType(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultCode() {
        return DAY.code;
    }

    public static TimeSeriesType fromCode(String code) {
        return EnumUtils.fromCode(TimeSeriesType.class, code);
    }

    public static TimeSeriesType fromName(String name) {
        return EnumUtils.fromName(TimeSeriesType.class, name);
    }
}
