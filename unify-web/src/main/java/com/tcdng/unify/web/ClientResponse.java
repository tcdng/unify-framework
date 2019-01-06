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
package com.tcdng.unify.web;

import java.io.OutputStream;
import java.io.Writer;

/**
 * A client response.
 * 
 * @author Lateef Ojulari
 * @since 1.0
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
    OutputStream getOutputStream() throws Exception;

    /**
     * Gets the response output stream
     */
    Writer getWriter() throws Exception;

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
     * Closes response.
     */
    void close();
}
