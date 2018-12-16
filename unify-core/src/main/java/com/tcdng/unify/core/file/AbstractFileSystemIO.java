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
package com.tcdng.unify.core.file;

import java.io.InputStream;
import java.io.OutputStream;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Abstract file system IO.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractFileSystemIO extends AbstractUnifyComponent implements FileSystemIO {

    @Override
    public String buildFilename(String path, String filename) {
        return IOUtils.buildFilename(path, filename);
    }

    @Override
    public int read(byte[] buffer, InputStream inputStream) throws UnifyException {
        return IOUtils.read(buffer, inputStream);
    }

    @Override
    public byte[] readAll(InputStream inputStream) throws UnifyException {
        return IOUtils.readAll(inputStream);
    }

    @Override
    public void close(InputStream inputStream) {
        IOUtils.close(inputStream);
    }

    @Override
    public long writeAll(OutputStream outputStream, InputStream inputStream) throws UnifyException {
        return IOUtils.writeAll(outputStream, inputStream);
    }

    @Override
    public long writeAll(OutputStream outputStream, byte[] data) throws UnifyException {
        return IOUtils.writeAll(outputStream, data);
    }

    @Override
    public void close(OutputStream outputStream) {
        IOUtils.close(outputStream);
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }
}
