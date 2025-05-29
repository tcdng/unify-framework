/*
 * Copyright (c) 2018-2025 The Code Department.
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

import java.util.UUID;

import com.tcdng.unify.core.UserToken;

/**
 * Application utilities.
 * 
 * @author The Code Department
 * @since 4.1
 */
public final class ApplicationUtils {

    private ApplicationUtils() {

    }

    public static synchronized String generateSessionContextId() {
        return UUID.randomUUID().toString();
    }

	public static synchronized String generateLongSessionCookieId(UserToken userToken) {
		return StringUtils.concatenateUsingSeparatorFixed(':', userToken.getBranchCode(), userToken.getDepartmentCode(),
				userToken.getRoleCode(), userToken.getOrganizationCode());
	}

	public static synchronized void popuplateFromLongSessionCookieId(UserToken userToken, String cookieId) {
		final String[] parts = StringUtils.split(cookieId, "\\:");
		if (parts.length == 4) {
			if (!StringUtils.isBlank(parts[0])) {
				userToken.setBranchCode(parts[0]);
			}
			
			if (!StringUtils.isBlank(parts[1])) {
				userToken.setDepartmentCode(parts[1]);
			}
			
			if (!StringUtils.isBlank(parts[2])) {
				userToken.setRoleCode(parts[2]);
			}
			
			if (!StringUtils.isBlank(parts[3])) {
				userToken.setOrganizationCode(parts[3]);
			}			
		}
	}
}
