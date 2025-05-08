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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import com.tcdng.unify.core.SessionAttributeProvider;
import com.tcdng.unify.web.ClientCookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

/**
 * HTTP request object implementation.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class HttpRequestImpl implements HttpRequest {

    private HttpServletRequest request;

	private List<ClientCookie> cookieList;

	private Map<String, ClientCookie> cookieMap;

    public HttpRequestImpl(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String getContentType() {
        return request.getContentType();
    }

    @Override
    public String getPathInfo() {
        return request.getPathInfo();
    }

    @Override
    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    @Override
    public String getHeader(String headerName) {
        return request.getHeader(headerName);
    }

    @Override
    public String getParameter(String paramName) {
        return request.getParameter(paramName);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return request.getReader();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return request.getInputStream();
    }

	@Override
    public Map<String, String[]> getParameterMap() {
        return request.getParameterMap();
    }

    @Override
    public Collection<HttpPart> getParts() throws Exception {
        List<HttpPart> partList = new ArrayList<HttpPart>();
        for (Part part : request.getParts()) {
            partList.add(new HttpPartImpl(part));
        }

        return partList;
    }

	@Override
	public List<ClientCookie> getCookies() {
		if (cookieList == null) {
			synchronized (this) {
				if (cookieList == null) {
					cookieList = Collections.emptyList();
					Cookie[] cookies = request.getCookies();
					if (cookies != null && cookies.length > 0) {
						cookieList = new ArrayList<ClientCookie>();
						for (Cookie cookie : cookies) {
							cookieList.add(new ClientCookie(cookie.getDomain(), cookie.getPath(), cookie.getName(),
									cookie.getValue(), cookie.getMaxAge()));
						}

						cookieList = Collections.unmodifiableList(cookieList);
					}

				}
			}
		}

		return cookieList;
	}

	@Override
	public Optional<ClientCookie> getCookie(String name) {
		if (cookieMap == null) {
			synchronized (this) {
				if (cookieMap == null) {
					cookieMap = Collections.emptyMap();
					Cookie[] cookies = request.getCookies();
					if (cookies != null && cookies.length > 0) {
						cookieMap = new HashMap<String, ClientCookie>();
						for (Cookie cookie : cookies) {
							cookieMap.put(cookie.getName(), new ClientCookie(cookie.getDomain(), cookie.getPath(),
									cookie.getName(), cookie.getValue(), cookie.getMaxAge()));
						}

						cookieMap = Collections.unmodifiableMap(cookieMap);
					}
				}
			}
		}

		return Optional.ofNullable(cookieMap.get(name));
	}

	@Override
	public boolean isWithCookie(String name) {
		return getCookie(name).isPresent();
	}

    @Override
    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
        return request.getRemoteHost();
    }

    @Override
    public String getRemoteUser() {
        return request.getRemoteUser();
    }

    @Override
    public String getServletPath() {
        return request.getServletPath();
    }

    @Override
    public String getScheme() {
        return request.getScheme();
    }

    @Override
    public String getServerName() {
        return request.getServerName();
    }

    @Override
    public int getServerPort() {
        return request.getServerPort();
    }

    @Override
	public HttpUserSession createHttpUserSession(SessionAttributeProvider attributeProvider, Locale locale,
			TimeZone timeZone, String sessionId, String uriBase, String contextPath, String tenantPath,
			String remoteIpAddress) {
		return new HttpUserSessionImpl(attributeProvider, locale, timeZone, sessionId, uriBase.toString(), contextPath,
				tenantPath, request.getRemoteHost(), remoteIpAddress, request.getRemoteUser());
	}

    @Override
    public void invalidateSession() {
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            httpSession.invalidate();
        }
    }

    @Override
    public void setSessionAttribute(String name, Object val) {
        request.getSession().setAttribute(name, val);
    }

    @Override
    public Object getSessionAttribute(String name) {
        return request.getSession().getAttribute(name);
    }

    @Override
    public void removeSessionAttribute(String name) {
        request.getSession().removeAttribute(name);
    }

    @Override
    public Object getSessionSychObject() {
        return request.getSession();
    }

}
