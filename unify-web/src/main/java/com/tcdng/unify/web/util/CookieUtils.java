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

package com.tcdng.unify.web.util;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Cookie utilities.
 * 
 * @author The Code Department
 * @since 4.1
 */
public final class CookieUtils {

    private CookieUtils() {
        
    }
    
	public static String getSessionCookieName(String infix, int appPort) throws UnifyException {
		final String _infix = StringUtils.flatten(infix).replaceAll("[^a-zA-Z0-9_]", "");
		return ("JS_" + _infix + "_" + appPort).toUpperCase();
	}
    
	public static String getLongSessionCookieName(String infix, int appPort) throws UnifyException {
		final String _infix = StringUtils.flatten(infix).replaceAll("[^a-zA-Z0-9_]", "");
		return ("NS_" + _infix + "_" + appPort).toUpperCase();
	}
}
