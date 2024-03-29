/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.web;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.ClientPlatform;
import com.tcdng.unify.web.http.HttpRequestHeaders;

/**
 * A client request.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface ClientRequest {

    /**
     * Gets the client platform.
     * 
     * @return the platform
     */
    ClientPlatform getClientPlatform();
    
    /**
     * Returns the client request type.
     */
    ClientRequestType getType();

    /**
     * Returns the request path parts
     */
    RequestPathParts getRequestPathParts();

    /**
     * Returns the request character set.
     */
    Charset getCharset();

    /**
     * Returns request parameter names.
     */
    Set<String> getParameterNames();

    /**
     * Gets a request parameter
     * 
     * @param name
     *            the parameter name
     * @return object if found otherwise false
     */
    Object getParameter(String name);

    /**
     * Gets a request parameter
     * 
     * @param clazz
     *            type
     * @param name
     *            the parameter name
     * @return object if found otherwise false
     */
    <T> T getParameter(Class<T> clazz, String name) throws UnifyException;

    /**
     * Gets input stream associated with this request.
     * 
     * @return the input stream
     * @throws UnifyException
     *             if not octet-stream request
     */
    InputStream getInputStream() throws UnifyException;

    /**
     * Returns request cookie names.
     */
    Set<String> getCookieNames();

    /**
     * Gets client cookie from request.
     * 
     * @param name
     *             the cookie name
     * @return the cookie if found otherwise null
     */
    ClientCookie getCookie(String name);
    
	/**
	 * Gets the request headers.
	 * 
	 * @return the request headers
	 */
	HttpRequestHeaders getRequestHeaders();
}
