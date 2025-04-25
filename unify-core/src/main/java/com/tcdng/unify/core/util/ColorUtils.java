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

package com.tcdng.unify.core.util;

import java.util.regex.Pattern;

import com.tcdng.unify.core.constant.ColorScheme;

/**
 * Color utilities.
 * 
 * @author The Code Department
 * @since 4.1
 */
public final class ColorUtils {
	
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");

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
    
    public static boolean isValidHexColor(String color) {
    	if (color != null) {
            return HEX_COLOR_PATTERN.matcher(color).matches();
    	}
    	
    	return false;
    }
}
