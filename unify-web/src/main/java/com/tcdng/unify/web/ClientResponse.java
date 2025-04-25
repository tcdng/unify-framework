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
package com.tcdng.unify.web;

import java.io.OutputStream;
import java.io.PrintWriter;

import com.tcdng.unify.core.UnifyException;

/**
 * A client response.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface ClientResponse {

    /**
     * Sets response meta data
     * 
     * @param key
     *            the meta data key
     * @param value
     *            the meta data value
     */
    void setMetaData(String key, String value);

    /**
     * Sets the response content type.
     * 
     * @param contentType
     *            the content type to set
     */
    void setContentType(String contentType);

    /**
     * Sets response character encoding.
     * 
     * @param charset
     *            the character set
     */
    void setCharacterEncoding(String charset);

    /**
     * Gets the response output stream
     */
    OutputStream getOutputStream() throws UnifyException;

    /**
     * Gets the response output stream
     */
    PrintWriter getWriter() throws UnifyException;

    /**
     * Returns true if response output is used.
     */
    boolean isOutUsed();

    /**
     * Sets response status.
     * 
     * @param status
     *            the status to set
     */
    void setStatus(int status);
    
    /**
     * Sets response status to OK
     */
    void setStatusOk();
    
    /**
     * Sets response status to forbidden
     */
    void setStatusForbidden();
    
    /**
     * Sets response status not found
     */
    void setStatusNotFound();

    /**
     * Sets a cookie in response.
     * 
     * @param name
     *             the cookie name
     * @param val
     *             the cookie value
     */
    void setCookie(String name, String val);

    /**
     * Sets a cookie in response.
     * 
     * @param name
     *               the cookie name
     * @param val
     *               the cookie value
     * @param maxAge
     *               maximum cookie age in seconds
     */
    void setCookie(String name, String val, int maxAge);

    /**
     * Sets a cookie in response.
     * 
     * @param domain
     *               the cookie domain
     * @param path
     *               the cookie path
     * @param name
     *               the cookie name
     * @param val
     *               the cookie value
     * @param maxAge
     *               maximum cookie age in seconds
     */
    void setCookie(String domain, String path, String name, String val, int maxAge);
    
    /**
     * Closes response.
     */
    void close();
}
