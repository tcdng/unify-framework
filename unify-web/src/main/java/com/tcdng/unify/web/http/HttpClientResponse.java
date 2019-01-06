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

import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.web.ClientResponse;

/**
 * HTTP client response.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class HttpClientResponse implements ClientResponse {

    private HttpServletResponse response;

    private OutputStream outputStream;

    private Writer writer;

    private boolean outUsed;

    public HttpClientResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public void setMetaData(String key, String value) {
        response.setHeader(key, value);
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
    public OutputStream getOutputStream() throws Exception {
        if (outputStream == null) {
            outputStream = response.getOutputStream();
            outUsed = true;
        }
        return outputStream;
    }

    @Override
    public Writer getWriter() throws Exception {
        if (writer == null) {
            writer = response.getWriter();
            outUsed = true;
        }
        return writer;
    }

    @Override
    public void setStatus(int status) {
        response.setStatus(status);
    }

    @Override
    public void close() {
        response.setStatus(HttpServletResponse.SC_OK);
        IOUtils.close(outputStream);
        IOUtils.close(writer);
    }

    public boolean isOutUsed() {
        return outUsed;
    }

}
