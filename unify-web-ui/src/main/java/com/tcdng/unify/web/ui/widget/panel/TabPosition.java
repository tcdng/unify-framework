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
package com.tcdng.unify.web.ui.widget.panel;

import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Tab position enumeration.
 * 
 * @author The Code Department
 * @since 1.0
 */
public enum TabPosition implements EnumConst {

    TOP("top"), BOTTOM("bottom"), LEFT("left"), RIGHT("right");

    private final String code;

    private TabPosition(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return TOP.code;
    }

    public static TabPosition fromCode(String code) {
        return EnumUtils.fromCode(TabPosition.class, code);
    }

    public static TabPosition fromName(String name) {
        return EnumUtils.fromName(TabPosition.class, name);
    }
}
