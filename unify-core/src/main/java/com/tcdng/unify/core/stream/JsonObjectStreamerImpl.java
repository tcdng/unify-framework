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
package com.tcdng.unify.core.stream;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Implementation of JSON object streamer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(name = ApplicationComponents.APPLICATION_JSONOBJECTSTREAMER, description = "JSON Object Streamer")
public class JsonObjectStreamerImpl extends AbstractObjectStreamer implements JsonObjectStreamer {

    @Override
    public <T> T unmarshal(Class<T> type, InputStream inputStream, Charset charset) throws UnifyException {
        return DataUtils.fromJsonInputStream(type, inputStream, charset);
    }

    @Override
    public <T> T unmarshal(Class<T> type, Reader reader) throws UnifyException {
        return DataUtils.fromJsonReader(type, reader);
    }

    @Override
    public <T> T unmarshal(Class<T> type, String string) throws UnifyException {
        return DataUtils.fromJsonString(type, string);
    }

    @Override
    public void marshal(Object object, OutputStream outputStream, Charset charset, PrintFormat printFormat)
            throws UnifyException {
        DataUtils.writeJsonObject(object, outputStream, charset, printFormat);
    }

    @Override
    public void marshal(Object object, Writer writer, PrintFormat printFormat) throws UnifyException {
        DataUtils.writeJsonObject(object, writer, printFormat);
    }

    @Override
    public String marshal(Object object, PrintFormat printFormat) throws UnifyException {
        return DataUtils.asJsonString(object, printFormat);
    }
}
