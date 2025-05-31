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

import java.util.Optional;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyCoreApplicationAttributeConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UserToken;
import com.tcdng.unify.web.ClientCookie;

/**
 * Convenient abstract base class for long user session manager.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractLongUserSessionManager extends AbstractUnifyComponent implements LongUserSessionManager {

	@Override
	public String getLongSessionCookieName() throws UnifyException {
		return getApplicationAttribute(String.class, UnifyCoreApplicationAttributeConstants.LONG_SESSION_COOKIE_NAME);
	}

	@Override
	public boolean performAutoLogin(HttpRequest httpRequest, HttpResponse httpResponse, HttpUserSession userSession)
			throws UnifyException {
		final String cookieName = getLongSessionCookieName();
		if (!userSession.isUserLoggedIn() && httpRequest.isWithCookie(cookieName)) {
			Optional<ClientCookie> cookie = httpRequest.getCookie(cookieName);
			if (cookie.isPresent()) {
				final String cookieId = cookie.get().getVal();
				Optional<UserToken> optional = getUserTokenByCookieId(cookieId);
				if (optional.isPresent()) {
					final UserToken userToken = optional.get();
					userSession.getSessionContext().setUserToken(userToken);
					System.out.println("@prime: XXXXXXXXXXXXXX");
					System.out.println("@prime: performAutoLogin()");
					System.out.println("@prime: sessionInSecs = " + userToken.getSessionInSecs());
					System.out.println("@prime: XXXXXXXXXXXXXX");
					httpResponse.setCookie(getLongSessionCookieName(), cookieId, userToken.getSessionInSecs());
					return true;
				}
			}
		}

		return false;
	}

	protected abstract Optional<UserToken> getUserTokenByCookieId(String cookieId) throws UnifyException;
	
	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

}
