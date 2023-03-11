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
package com.tcdng.unify.web.ui.util;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.constant.ShortcutFlagConstants;
import com.tcdng.unify.web.ui.UnifyWebUIErrorConstants;

/**
 * Web utilities.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class WebUtils {

    private WebUtils() {

    }

    /**
     * Encodes a shortcut string.
     * 
     * @param shortcut
     *            the shortcut string to encode
     * @return the encodede shortcut
     * @throws UnifyException
     *             if an error occurs
     */
    public static String encodeShortcut(String shortcut) throws UnifyException {
        String encodedShortcut = null;
        if (StringUtils.isNotBlank(shortcut)) {
            int encoded = 0;
            String[] elements = shortcut.toUpperCase().split("\\+");
            boolean validShortcut = false;
            for (int i = 0; i < elements.length; i++) {
                if ("SHIFT".equalsIgnoreCase(elements[i])) {
                    if (!(validShortcut = (encoded & ShortcutFlagConstants.SHIFT) == 0)) {
                        break;
                    }
                    encoded |= ShortcutFlagConstants.SHIFT;
                } else if ("CTRL".equalsIgnoreCase(elements[i])) {
                    if (!(validShortcut = (encoded & ShortcutFlagConstants.CTRL) == 0)) {
                        break;
                    }
                    encoded |= ShortcutFlagConstants.CTRL;
                } else if ("ALT".equalsIgnoreCase(elements[i])) {
                    if (!(validShortcut = (encoded & ShortcutFlagConstants.ALT) == 0)) {
                        break;
                    }
                    encoded |= ShortcutFlagConstants.ALT;
                } else {
                    if (!(validShortcut = ((encoded & 0x00FF) == 0) && (elements[i].length() == 1))) {
                        break;
                    }
                    encoded += elements[i].charAt(0);
                }
            }
            if (!validShortcut) {
                throw new UnifyException(UnifyWebUIErrorConstants.KEYCOMBO_IS_INVALID, shortcut);
            }
            encodedShortcut = String.valueOf(encoded);
        }
        return encodedShortcut;
    }
}
