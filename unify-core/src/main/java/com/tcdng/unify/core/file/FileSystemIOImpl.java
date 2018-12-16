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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Default implementation of a file system IO.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("filesystemio")
public class FileSystemIOImpl extends AbstractFileSystemIO {

    @Override
    public boolean isFile(String absoluteFilename) {
        return IOUtils.isFile(absoluteFilename);
    }

    @Override
    public InputStream openFileInputStream(String filename) throws UnifyException {
        return IOUtils.openFileInputStream(filename);
    }

    @Override
    public InputStream openFileInputStream(String filename, long skip) throws UnifyException {
        return IOUtils.openFileInputStream(filename, skip);
    }

    @Override
    public OutputStream openFileOutputStream(String filename) throws UnifyException {
        return IOUtils.openFileOutputStream(filename);
    }

    @Override
    public OutputStream openFileOutputStream(String filename, boolean append) throws UnifyException {
        return IOUtils.openFileOutputStream(filename, append);
    }
}
