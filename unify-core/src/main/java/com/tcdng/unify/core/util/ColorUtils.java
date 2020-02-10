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

package com.tcdng.unify.core.util;

import com.tcdng.unify.core.constant.ColorScheme;

/**
 * Color utilities.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class ColorUtils {

    private ColorUtils() {

    }

    /**
     * Gets conforming color scheme code.
     * 
     * @param scheme
     *            the color scheme
     * @return the color scheme code otherwise null
     */
    public static String getConformingColorSchemeCode(String scheme) {
        if (!StringUtils.isBlank(scheme)) {
            ColorScheme colorScheme = ColorScheme.fromName(scheme);
            if (colorScheme != null) {
                return colorScheme.code();
            }
        }

        return null;
    }
}
