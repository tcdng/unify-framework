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
package com.tcdng.unify.web.http;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UserSession;
import com.tcdng.unify.web.RequestPathParts;

/**
 * HTTP request handler component.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface HttpRequestHandler extends UnifyComponent {

    /**
     * Resolve request path.
     * 
     * @param httpRequest
     *                      the request object
     * @return the path parts
     * @throws UnifyException
     *                        if an error occurs
     */
    RequestPathParts resolveRequestPath(HttpRequest httpRequest) throws UnifyException;

    /**
     * Gets the parts of supplied request path.
     * 
     * @param requestPath
     *                    the request path
     * @return the request path parts
     * @throws UnifyException
     *                        if an error occurs
     */
    RequestPathParts getRequestPathParts(String requestPath) throws UnifyException;

    /**
     * Get user session.
     * 
     * @param httpModule
     *                      the HTTP module
     * @param httpRequest
     *                      the request object
     * @param reqPathParts
     *                      the request path parts
     * @return the user session
     * @throws UnifyException
     *                        if an error occurs
     */
    UserSession getUserSession(HttpServletModule httpModule, HttpRequest httpRequest,
            RequestPathParts reqPathParts) throws UnifyException;

    /**
     * Handles HTTP request.
     * 
     * @param methodType
     *                       the request method type
     * @param reqPathParts
     *                       the path parts
     * @param httpRequest
     *                       the request object
     * @param httpResponse
     *                       the response object
     * @throws UnifyException
     *                        if an error occurs
     */
    void handleRequest(HttpRequestMethodType methodType, RequestPathParts reqPathParts, HttpRequest httpRequest,
            HttpResponse httpResponse) throws UnifyException;
}
