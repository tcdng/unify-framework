/*
 * Copyright 2018-2022 The Code Department.
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

import com.tcdng.unify.convert.annotation.StaticList;
import com.tcdng.unify.convert.constants.EnumConst;
import com.tcdng.unify.core.annotation.Tooling;
import com.tcdng.unify.core.util.EnumUtils;

@Tooling(description = "Color Scheme")
@StaticList(name = "colorschemelist", description="$m{staticlist.colorschemelist}")
public enum ColorScheme implements EnumConst {

    RED("red", ColorPalette.RED_SCALE),
    BLUE("blue", ColorPalette.BLUE_SCALE),
    GREEN("green", ColorPalette.GREEN_SCALE),
    YELLOW("yellow", ColorPalette.YELLOW_SCALE),
    ORANGE("orange", ColorPalette.ORANGE_SCALE),
    GRAY("gray", ColorPalette.GRAY_SCALE);

    private final String code;

    private final ColorPalette pallete;
    
    private ColorScheme(String code, ColorPalette pallete) {
        this.code = code;
        this.pallete = pallete;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String defaultCode() {
        return GRAY.code;
    }

    public ColorPalette pallete() {
        return pallete;
    }

    public static ColorScheme fromCode(String code) {
        return EnumUtils.fromCode(ColorScheme.class, code);
    }

    public static ColorScheme fromName(String name) {
        return EnumUtils.fromName(ColorScheme.class, name);
    }
}
