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

import java.nio.charset.Charset;
import java.util.Set;

/**
 * A client request.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface ClientRequest {

    /**
     * Returns the request path
     */
    String getPath();

    /**
     * Returns the request character set.
     */
    Charset getCharset();

    /**
     * Returns request parameter names.
     */
    Set<String> getParameterNames();

    /**
     * Returns a request parameter
     * 
     * @param name
     *            the parameter name
     * @return object if found otherwise false
     */
    Object getParameter(String name);
}
