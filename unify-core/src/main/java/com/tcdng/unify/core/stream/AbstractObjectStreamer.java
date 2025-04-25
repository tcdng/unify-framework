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

package com.tcdng.unify.core.stream;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.PrintFormat;

/**
 * Convenient base class for object streamer.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractObjectStreamer extends AbstractUnifyComponent implements ObjectStreamer {

    @Override
    public <T> T unmarshal(Class<T> type, InputStream inputStream) throws UnifyException {
        return unmarshal(type, inputStream, null);
    }

    @Override
    public void marshal(Object object, OutputStream outputStream, Charset charset) throws UnifyException {
        marshal(object, outputStream, charset, PrintFormat.NONE);
    }

    @Override
    public void marshal(Object object, Writer writer) throws UnifyException {
        marshal(object, writer, PrintFormat.NONE);
    }

    @Override
    public Object marshal(Object object) throws UnifyException {
        return marshal(object, PrintFormat.NONE);
    }

    @Override
    public void marshal(Object object, OutputStream outputStream) throws UnifyException {
        marshal(object, outputStream, null, PrintFormat.NONE);
    }

    @Override
    public void marshal(Object object, OutputStream outputStream, PrintFormat printFormat) throws UnifyException {
        marshal(object, outputStream, null, printFormat);
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

}
