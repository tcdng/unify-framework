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
package com.tcdng.unify.core.stream;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Implementation of JSON object streamer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(name = ApplicationComponents.APPLICATION_JSONOBJECTSTREAMER, description = "JSON Object Streamer")
public class JSONObjectStreamerImpl extends AbstractObjectStreamer implements JSONObjectStreamer {

    @Override
    public <T> T unmarshal(Class<T> type, InputStream inputStream, Charset charset) throws UnifyException {
        return DataUtils.readJsonObject(type, inputStream, charset);
    }

    @Override
    public <T> T unmarshal(Class<T> type, Reader reader) throws UnifyException {
        return DataUtils.readJsonObject(type, reader);
    }

    @Override
    public <T> T unmarshal(Class<T> type, String string) throws UnifyException {
        return DataUtils.readJsonObject(type, string);
    }

    @Override
    public void unmarshal(Object object, InputStream inputStream, Charset charset) throws UnifyException {
        DataUtils.readJsonObject(object, inputStream, charset);
    }

    @Override
    public void unmarshal(Object object, Reader reader) throws UnifyException {
        DataUtils.readJsonObject(object, reader);
    }

    @Override
    public void unmarshal(Object object, String string) throws UnifyException {
        DataUtils.readJsonObject(object, string);
    }

    @Override
    public void marshal(Object object, OutputStream outputStream, Charset charset) throws UnifyException {
        DataUtils.writeJsonObject(object, outputStream, charset);
    }

    @Override
    public void marshal(Object object, Writer writer) throws UnifyException {
        DataUtils.writeJsonObject(object, writer);
    }

    @Override
    public String marshal(Object object) throws UnifyException {
        return DataUtils.writeJsonObject(object);
    }
}
