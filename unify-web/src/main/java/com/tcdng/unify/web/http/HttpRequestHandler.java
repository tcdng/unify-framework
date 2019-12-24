/*
 * Copyright 2018-2019 The Code Department.
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
import com.tcdng.unify.web.PathParts;

/**
 * HTTP request handler component.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface HttpRequestHandler extends UnifyComponent {

    /**
     * Resolve request path.
     * 
     * @param requestObject
     *            the request object
     * @return the path parts
     * @throws UnifyException
     *             if an error occurs
     */
    PathParts resolveRequestPath(Object requestObject) throws UnifyException;

    /**
     * Handles HTTP request.
     * 
     * @param methodType
     *            the request method type
     * @param pathParts
     *            the path parts
     * @param requestObject
     *            the request object
     * @param responseObject
     *            the response object
     * @throws UnifyException
     *             if an error occurs
     */
    void handleRequest(HttpRequestMethodType methodType, PathParts pathParts, Object requestObject,
            Object responseObject) throws UnifyException;
}
