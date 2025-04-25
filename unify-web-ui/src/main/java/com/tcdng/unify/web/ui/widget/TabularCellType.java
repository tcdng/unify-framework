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

package com.tcdng.unify.web.ui.widget;

import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Tabular cell type.
 * 
 * @author The Code Department
 * @since 4.1
 */
public enum TabularCellType implements EnumConst {

    TOP("TOP", "lcellt", "lcelltp"), MIDDLE("MID", "lcellm", "lcellmp"), BOTTOM("BTM", "lcellb", "lcellbp");

    private final String code;

    private final String styleClass;

    private final String padStyleClass;

    private TabularCellType(String code, String styleClass, String padStyleClass) {
        this.code = code;
        this.styleClass = styleClass;
        this.padStyleClass = padStyleClass;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return MIDDLE.code;
    }

    public String styleClass() {
        return styleClass;
    }

    public String padStyleClass() {
        return padStyleClass;
    }

    public static TabularCellType fromCode(String code) {
        return EnumUtils.fromCode(TabularCellType.class, code);
    }

    public static TabularCellType fromName(String name) {
        return EnumUtils.fromName(TabularCellType.class, name);
    }
}
