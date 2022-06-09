/*
 * Copyright 2018-2022 The Code Department.
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import com.tcdng.unify.core.util.StringUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

/**
 * HTTP response object implementation.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class HttpResponseImpl implements HttpResponse {

    private HttpServletResponse response;
    
    
    public HttpResponseImpl(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public void setHeader(String key, String val) {
        response.setHeader(key, val);
    }

    @Override
    public void setContentType(String contentType) {
        response.setContentType(contentType);
    }

    @Override
    public void setCharacterEncoding(String charset) {
        response.setCharacterEncoding(charset);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return response.getOutputStream();
    }

    @Override
    public Writer getWriter() throws IOException {
        return response.getWriter();
    }

    @Override
    public void setStatus(int status) {
        response.setStatus(status);
    }

    @Override
    public void setStatusOk() {
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    public void setStatusForbidden() {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Override
    public void setStatusNotFound() {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    public void setCookie(String name, String val) {
        Cookie cookie = new Cookie(name, val);
        response.addCookie(cookie);
    }

    @Override
    public void setCookie(String name, String val, int maxAge) {
        Cookie cookie = new Cookie(name, val);
        if (maxAge >= 0) {
            cookie.setMaxAge(maxAge);
        }
        
        response.addCookie(cookie);
    }

    @Override
    public void setCookie(String domain, String path, String name, String val, int maxAge) {
        Cookie cookie = new Cookie(name, val);       
        if (!StringUtils.isBlank(domain)) {
            cookie.setDomain(domain);
        }
        
        if (!StringUtils.isBlank(path)) {
            cookie.setPath(path);
        }
        
        if (maxAge >= 0) {
            cookie.setMaxAge(maxAge);
        }
        
        response.addCookie(cookie);
    }

}
