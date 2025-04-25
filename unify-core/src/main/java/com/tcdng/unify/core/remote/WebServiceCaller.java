/*
 * Copyright 2018-2025 The Code Department.
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

package com.tcdng.unify.core.remote;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Web service caller component.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface WebServiceCaller extends UnifyComponent {

    /**
     * Performs a GET to remote web service;
     * 
     * @param resultType
     *            the result type
     * @param targetUrl
     *            the target URL
     * @return the result object
     * @throws UnifyException
     *             if an error occurs
     */
    <T> T getToRemote(Class<T> resultType, String targetUrl) throws UnifyException;

    /**
     * Performs a POST to remote web service;
     * 
     * @param resultType
     *            the result type
     * @param targetUrl
     *            the target URL
     * @return the result object
     * @throws UnifyException
     *             if an error occurs
     */
    <T> T postToRemote(Class<T> resultType, String targetUrl) throws UnifyException;

    /**
     * Performs a POST to remote web service;
     * 
     * @param resultType
     *            the result type
     * @param targetUrl
     *            the target URL
     * @param param
     *            the payload (optional)
     * @return the result object
     * @throws UnifyException
     *             if an error occurs
     */
    <T> T postToRemote(Class<T> resultType, String targetUrl, Object param) throws UnifyException;
}
