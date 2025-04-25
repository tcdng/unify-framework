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

/**
 * Provides utility methods for version operations.
 * 
 * @author The Code Department
 * @since 4.1
 */
public final class VersionUtils {

	private VersionUtils() {

	}

	public static boolean isNewerVersion(String newVersion, String oldVersion) {
		if (oldVersion != null && newVersion != null) {
			String[] oldVersionDigits = oldVersion.split("\\.");
			String[] newVersionDigits = newVersion.split("\\.");
			int len = oldVersionDigits.length;
			if (len > newVersionDigits.length) {
				len = newVersionDigits.length;
			}

			for (int i = 0; i < len; i++) {
				int comp = Integer.valueOf(newVersionDigits[i]).compareTo(Integer.valueOf(oldVersionDigits[i]));
				if (comp > 0) {
					return true;
				} else if (comp < 0) {
					return false;
				}
			}

			return newVersionDigits.length > len;
		}

		return false;
	}
}
