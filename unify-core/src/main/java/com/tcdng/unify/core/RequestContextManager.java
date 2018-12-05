/*
 * Copyright 2014 The Code Department
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
package com.tcdng.unify.core;

/**
 * Manages life-cycle of request context objects.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface RequestContextManager extends UnifyComponent {

	/**
	 * Gets the current executing thread request context.
	 * 
	 * @return the request context
	 */
	RequestContext getRequestContext();

	/**
	 * Sets the request context for the current executing thread using supplied user
	 * session, context path and request path. Adds user session to application
	 * context if it's a new session.
	 * 
	 * @param userSession
	 *            the user session object
	 * @param requestPath
	 *            the request path.
	 * @throws UnifyException
	 *             if an error occurs
	 */
	void loadRequestContext(UserSession userSession, String requestPath) throws UnifyException;

	/**
	 * Sets the request context for the current executing thread with supplied
	 * context object.
	 * 
	 * @param requestContext
	 *            the request context object to use.
	 * @throws UnifyException
	 *             if an erro occurs
	 */
	void loadRequestContext(RequestContext requestContext) throws UnifyException;

	/**
	 * Unloads the current executing thread request context.
	 */
	void unloadRequestContext();

	/**
	 * Resets application context.
	 */
	void reset() throws UnifyException;
}
