/*
 * Copyright 2018-2020 The Code Department.
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

import jakarta.servlet.http.HttpServletResponse;

/**
 * HTTP response object implementation.
 * 
 * @author Lateef Ojulari
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
    public int getOk() {
        return HttpServletResponse.SC_OK;
    }

}
