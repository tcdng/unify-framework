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
package com.tcdng.unify.web.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyOperationException;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.web.ClientResponse;

/**
 * HTTP client response.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class HttpClientResponse implements ClientResponse {

    private HttpResponse response;

    private OutputStream outputStream;

    private PrintWriter writer;

    private boolean outUsed;

    public HttpClientResponse(HttpResponse response) {
        response.setStatusOk();
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
    public OutputStream getOutputStream() throws UnifyException {
        try {
            if (outputStream == null) {
                outputStream = response.getOutputStream();
                outUsed = true;
            }
            return outputStream;
        } catch (IOException e) {
            throw new UnifyOperationException(e, getClass().getSimpleName());
        }
    }

    @Override
    public PrintWriter getWriter() throws UnifyException {
        try {
            if (writer == null) {
                writer = response.getWriter();
                outUsed = true;
            }
            return writer;
        } catch (IOException e) {
            throw new UnifyOperationException(e, getClass().getSimpleName());
        }
    }

    @Override
    public void setStatus(int status) {
        response.setStatus(status);
    }

    @Override
    public void setStatusOk() {
        response.setStatusOk();
    }

    @Override
    public void setStatusForbidden() {
        response.setStatusForbidden();
    }

    @Override
    public void setStatusNotFound() {
        response.setStatusNotFound();
    }

    @Override
    public void setCookie(String name, String val) {
        response.setCookie(name, val);
    }

    @Override
    public void setCookie(String name, String val, int maxAge) {
        response.setCookie(name, val, maxAge);
    }

    @Override
    public void setCookie(String domain, String path, String name, String val, int maxAge) {
        response.setCookie(domain, path, name, val, maxAge);
    }

    @Override
    public void close() {
        IOUtils.close(outputStream);
        IOUtils.close(writer);
    }

    public boolean isOutUsed() {
        return outUsed;
    }

}
