/*
 * Copyright 2018-2023 The Code Department.
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

package com.tcdng.unify.core.report;

import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Report layout types.
 * 
 * @author The Code Department
 * @since 1.0
 */
public enum ReportLayoutType implements EnumConst {
    TABULAR("TBL"),
    TABULAR_IMAGESONLY("TIO"),
    TABULAR_THUMBIMAGESONLY("TTO"),
    COLUMNAR("CMN"),
    SINGLECOLUMN_EMBEDDED_HTML("SCH"),
    MULTIDOCHTML_PDF("MDH"),
    PLACEMENT_PDF("PLC");

    private final String code;
    
    private ReportLayoutType(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return TABULAR.code;
    }

    public static ReportLayoutType fromCode(String code) {
        return EnumUtils.fromCode(ReportLayoutType.class, code);
    }

    public static ReportLayoutType fromName(String name) {
        return EnumUtils.fromName(ReportLayoutType.class, name);
    }
}
