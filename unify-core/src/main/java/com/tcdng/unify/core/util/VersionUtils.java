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
package com.tcdng.unify.core.util;

/**
 * Provides utility methods for version operations.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class VersionUtils {

    private VersionUtils() {

    }

    public static boolean isNewerVersion(String newVersion, String oldVersion) {
        if (oldVersion != null && newVersion != null) {
            String[] oldVersionDigits = oldVersion.split("\\.");
            String[] newVersionDigits = newVersion.split("\\.");
            int length = oldVersionDigits.length;
            if (length > newVersionDigits.length) {
                length = newVersionDigits.length;
            }

            for (int i = 0; i < length; i++) {
                int compareResult =
                        Integer.valueOf(oldVersionDigits[i]).compareTo(Integer.valueOf(newVersionDigits[i]));
                if (compareResult < 0) {
                    return true;
                } else if (compareResult > 0) {
                    return false;
                }
            }
        }
        return false;
    }
}
