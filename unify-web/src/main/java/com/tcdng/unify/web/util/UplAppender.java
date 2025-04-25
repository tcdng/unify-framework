/*
 * Copyright 2018-2025 The Code Department.
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
package com.tcdng.unify.web.util;

import com.tcdng.unify.core.util.StringUtils;

/**
 * UPL attribute appender.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class UplAppender {

    private UplAppender() {

    }

    public static void appendFormatterUplAttribute(StringBuilder sb, String formatter) {
        if (StringUtils.isNotBlank(formatter)) {
            sb.append(" formatter:$d{").append(formatter).append("}");
        }
    }

    public static void appendPrecisionUplAttribute(StringBuilder sb, Integer precision) {
        if (precision != null) {
            sb.append(" precision:").append(precision);
        }
    }

    public static void appendScaleUplAttribute(StringBuilder sb, Integer scale) {
        if (scale != null) {
            sb.append(" scale:").append(scale);
        }
    }

    public static void appendMaxLenUplAttribute(StringBuilder sb, Integer maxLen) {
        if (maxLen != null) {
            sb.append(" maxLen:").append(maxLen);
        }
    }

    public static void appendMinLenUplAttribute(StringBuilder sb, Integer minLen) {
        if (minLen != null) {
            sb.append(" minLen:").append(minLen);
        }
    }

    public static void appendSizeUplAttribute(StringBuilder sb, Integer size) {
        if (size != null) {
            sb.append(" size:").append(size);
        }
    }

    public static void appendColumnsUplAttribute(StringBuilder sb, Integer size) {
        if (size != null) {
            sb.append(" columns:").append(size);
        }
    }

    public static void appendRowsUplAttribute(StringBuilder sb, Integer size) {
        if (size != null) {
            sb.append(" rows:").append(size);
        }
    }

    public static void appendMultipleUplAttribute(StringBuilder sb, Boolean multiple) {
        if (Boolean.TRUE.equals(multiple)) {
            sb.append(" multiple:true");
        }
    }

    public static void appendSelectOnlyUplAttribute(StringBuilder sb, Boolean selectOnly) {
        if (Boolean.TRUE.equals(selectOnly)) {
            sb.append(" selectOnly:true");
        }
    }

    public static void appendUseGroupingUplAttribute(StringBuilder sb, Boolean useGrouping) {
        if (Boolean.TRUE.equals(useGrouping)) {
            sb.append(" useGrouping:true");
        }
    }

    public static void appendAcceptNegativeUplAttribute(StringBuilder sb, Boolean acceptNegative) {
        if (Boolean.TRUE.equals(acceptNegative)) {
            sb.append(" acceptNegative:true");
        }
    }

    public static void appendBlankOptionUplAttribute(StringBuilder sb, String blankOption) {
        if (blankOption != null) {
            sb.append(" blankOption:$s{").append(blankOption).append("}");
        }
    }

    public static void appendListUplAttribute(StringBuilder sb, String list) {
        if (StringUtils.isNotBlank(list)) {
            sb.append(" list:$s{").append(list).append("}");
        }
    }

    public static void appendListParamsUplAttribute(StringBuilder sb, String listParams) {
        if (StringUtils.isNotBlank(listParams)) {
            sb.append(" listParams:$l{").append(listParams).append("}");
        }
    }

    public static void appendListKeyUplAttribute(StringBuilder sb, String listKey) {
        if (StringUtils.isNotBlank(listKey)) {
            sb.append(" listKey:").append(listKey);
        }
    }

    public static void appendListDescriptionUplAttribute(StringBuilder sb, String listDesc) {
        if (StringUtils.isNotBlank(listDesc)) {
            sb.append(" listDescription:").append(listDesc);
        }
    }

    public static void appendCurrencyUplAttribute(StringBuilder sb, String currency) {
        if (StringUtils.isNotBlank(currency)) {
            sb.append(" currency:").append(currency);
        }
    }
}
