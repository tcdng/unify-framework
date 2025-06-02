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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import com.tcdng.unify.core.SessionAttributeProvider;
import com.tcdng.unify.web.ClientCookie;

/**
 * HTTP request.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface HttpRequest extends HttpRequestHeaders, HttpRequestParameters {

	String getContentType();

	String getPathInfo();

	String getCharacterEncoding();

	String getRemoteAddr();

	String getRemoteHost();

	String getRemoteUser();

	String getServletPath();

	String getScheme();

	String getServerName();

	String getQueryString();

	int getServerPort();

	HttpUserSession createHttpUserSession(SessionAttributeProvider attributeProvider, Locale locale, TimeZone timeZone,
			String sessionId, String uriBase, String contextPath, String tenantPath, String remoteIpAddress);

	BufferedReader getReader() throws IOException;

	InputStream getInputStream() throws IOException;

	Collection<HttpPart> getParts() throws Exception;

	List<ClientCookie> getCookies();
 
	Optional<ClientCookie> getCookie(String name);
	 
	boolean isWithCookie(String name);
	
	void invalidateSession();

	void setSessionAttribute(String name, Object val);

	Object getSessionAttribute(String name);

	void removeSessionAttribute(String name);

	Object getSessionSychObject();
}
