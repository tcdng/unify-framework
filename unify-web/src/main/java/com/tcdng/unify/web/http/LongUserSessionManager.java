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

package com.tcdng.unify.web.http;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Long user session manager.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface LongUserSessionManager extends UnifyComponent {

	/**
	 * Gets the long session cookie name.
	 * 
	 * @throws UnifyException if an error occurs
	 * @return the cookie name
	 */
	String getLongSessionCookieName() throws UnifyException;

	/**
	 * Auto-login of long user session.
	 * 
	 * @param httpRequest the HTTP request
	 * @param httpResponse the HTTP response
	 * @param userSession the user session
	 * @throws UnifyException if an error occurs
	 * @return true if auto-login was successful otherwise false
	 */
	boolean performAutoLogin(HttpRequest httpRequest,
			HttpResponse httpResponse, HttpUserSession userSession) throws UnifyException;

	/**
	 * Saves long session.
	 * 
	 * @param userLoginId   the user login ID
	 * @param cookieId      the cookie ID
	 * @param sessionInSecs session in seconds
	 * @return true if set otherwise false
	 * @throws UnifyException if an error occurs
	 */
	boolean saveLongSession(String userLoginId, String cookieId, int sessionInSecs) throws UnifyException;

	/**
	 * Deletes long session.
	 * 
	 * @param cookieId      the cookie ID
	 * @return true if set otherwise false
	 * @throws UnifyException if an error occurs
	 */
	boolean deleteLongSession(String cookieId) throws UnifyException;
}
