/*
 * Copyright 2018 The Code Department
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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.util.IOUtils;

/**
 * Test controller response.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TestClientResponse implements ClientResponse {

    private Map<String, String> metas;

    private String contentType;

    private ByteArrayOutputStream outputStream;

    private Writer writer;

    private boolean used;

    public TestClientResponse() {
        metas = new HashMap<String, String>();
        outputStream = new ByteArrayOutputStream();
        writer = new OutputStreamWriter(outputStream);
    }

    @Override
    public void setMetaData(String key, String value) {
        metas.put(key, value);
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public void setCharacterEncoding(String charset) {

    }

    @Override
    public OutputStream getOutputStream() throws Exception {
        used = true;
        return outputStream;
    }

    @Override
    public Writer getWriter() throws Exception {
        used = true;
        return writer;
    }

    @Override
    public boolean isOutUsed() {
        return used;
    }

    @Override
    public void setStatus(int status) {

    }

    @Override
    public void close() {
        IOUtils.close(writer);
        IOUtils.close(outputStream);
    }

    public Map<String, String> getMetas() {
        return metas;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getBytes() {
        return outputStream.toByteArray();
    }

    public String toString() {
        return new String(getBytes());
    }
}
